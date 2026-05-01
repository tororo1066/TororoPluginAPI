package tororo1066.tororopluginapiextended.sInventoryV2.itemStack

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import tororo1066.tororopluginapi.SInput
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapiextended.sInventoryV2.SInventoryV2
import tororo1066.tororopluginapiextended.sInventoryV2.utils.UnaryPlusBuilder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

sealed class InputItemStack {
    companion object {
        val sEvent = SEvent()
        private val inputSessions = ConcurrentHashMap<UUID, InputSession>()

        init {
            sEvent.register<PlayerCommandPreprocessEvent> { e ->
                val session = inputSessions[e.player.uniqueId] ?: return@register
                if (e.message.isBlank()) return@register

                e.isCancelled = true

                if (e.message == "/cancel") {
                    e.player.sendMessage("§a入力をキャンセルしました")
                    endInput(e.player, session)
                    return@register
                }

                if (!session.action.invoke(e.message.replaceFirst("/", ""), e.player)) {
                    return@register
                }

                endInput(e.player, session)
            }
        }

        private fun endInput(player: Player, session: InputSession) {
            inputSessions.remove(player.uniqueId)
            session.onEnd(player)
        }

        class InputSession(
            val action: (String, Player) -> Boolean,
            val onEnd: (Player) -> Unit
        )
    }

    data class EnteredContext<T: Any>(val value: T, val player: Player)
    data class NullableEnteredContext<T: Any>(val value: T?, val player: Player)

    open class NullableInputItemStack<T: Any>(
        val inventory: SInventoryV2,
        modernItemStack: ModernItemStack,
        val type: Class<T>
    ): ModernItemStack(modernItemStack.build()) {

        var message: String = "§a/<入れるデータ(§d${type.simpleName}§a)>"
        var errorMsg: (value: String) -> String = { "§d${it}§4は§d${type.simpleName}§4ではありません" }
        var allowedClickTypes: List<ClickType> = listOf()
        var openInventoryOnEnter: Boolean = true
        var isEmptyAllowed: Boolean = false
        private var onNullableEnter: (NullableEnteredContext<T>.() -> Unit)? = null

        fun allowClickTypes(builder: UnaryPlusBuilder<ClickType>.() -> Unit) {
            allowedClickTypes = UnaryPlusBuilder<ClickType>().apply(builder).build()
        }

        open fun onNullableEnter(builder: NullableEnteredContext<T>.() -> Unit) {
            onNullableEnter = builder
        }

        open fun createSession(msg: String, player: Player): InputSession {
            return InputSession({ msg, player ->
                val (blank, value) = SInput.modifyClassValue(type, msg, isEmptyAllowed)
                if (!blank && value == null) {
                    player.sendMessage(errorMsg.invoke(msg))
                    return@InputSession false
                }

                onNullableEnter?.invoke(NullableEnteredContext(value, player))
                true
            }, {
                if (openInventoryOnEnter) {
                    inventory.open(player)
                }
            })
        }

        fun applyToModernItemStack(): NullableInputItemStack<T> {
            onClick {
                if (allowedClickTypes.isNotEmpty() && !allowedClickTypes.contains(inventoryClickEvent.click)) return@onClick

                player.sendMessage(message)

                inputSessions[player.uniqueId] = createSession(message, player)
                player.closeInventory()
            }
            return this
        }
    }

    class InputItemStack<T: Any>(
        inventory: SInventoryV2,
        modernItemStack: ModernItemStack,
        type: Class<T>
    ): NullableInputItemStack<T>(inventory, modernItemStack, type) {

        private var onEnter: (EnteredContext<T>.() -> Unit)? = null

        fun onEnter(builder: EnteredContext<T>.() -> Unit) {
            onEnter = builder
        }

        @Deprecated("", level = DeprecationLevel.HIDDEN)
        override fun onNullableEnter(builder: NullableEnteredContext<T>.() -> Unit) {}

        override fun createSession(msg: String, player: Player): InputSession {
            return InputSession({ msg, player ->
                val (_, value) = SInput.modifyClassValue(type, msg)
                if (value == null) {
                    player.sendMessage(errorMsg.invoke(msg))
                    return@InputSession false
                }

                onEnter?.invoke(EnteredContext(value, player))
                true
            }, {
                if (openInventoryOnEnter) {
                    inventory.open(player)
                }
            })
        }
    }
}