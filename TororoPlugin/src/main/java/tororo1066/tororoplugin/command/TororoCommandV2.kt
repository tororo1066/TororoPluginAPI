package tororo1066.tororoplugin.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.translation.GlobalTranslator
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import tororo1066.nmsutils.SNms
import tororo1066.nmsutils.command.ToolTip
import tororo1066.tororoplugin.TororoPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.SStr.Companion.toSStr
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.annotation.SEventHandler
import tororo1066.tororopluginapi.frombukkit.SBukkit
import tororo1066.tororopluginapi.lang.SLang
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2
import tororo1066.tororopluginapi.sCommand.v2.argumentType.EntityArg
import tororo1066.tororopluginapi.sCommand.v2.argumentType.IntArg
import tororo1066.tororopluginapi.sCommand.v2.argumentType.StringArg
import tororo1066.tororopluginapi.utils.sendMessage
import java.util.*
import kotlin.collections.HashMap

class TororoCommandV2: SCommandV2("tororo") {

    val japaneseEnchantMap = HashMap<String, Enchantment>(mapOf("test" to Enchantment.DAMAGE_ALL))

    init {
        root.setPermission("tororo")
        SBukkit.registerSEvent(this)


    }

    private fun Player.noMessageItemInMainHand() = if (inventory.itemInMainHand.type.isAir) {
        null
    } else inventory.itemInMainHand

    private fun Player.itemInMainHand(): ItemStack? {
        val item = noMessageItemInMainHand()
        if (item == null) {
            sendPrefixMsg(SStr("&cアイテムを持っていません"))
            return null
        }
        return item
    }

    private fun CommandSender.sendPrefixMsg(msg: SStr) {
        sendMessage(TororoPlugin.prefix + msg)
    }


    @SCommandBody
    val tororo = command {
        literal("test") {
            setPlayerFunctionExecutor { sender, _, _ ->
                val lang = SLang.getMcLangFile("ja_jp", sender)?:return@setPlayerFunctionExecutor
                Enchantment.values().forEach {
                    japaneseEnchantMap[lang[it.translationKey()].asString] = it
                }
                sender.updateCommands()
            }
        }

        literal("send") {
            setPermission("tororo.send")
            argument("player", EntityArg.players()) {
                argument("repeat", IntArg(1,10)) {

                    suggest("3" toolTip "3回", "5" toolTip "5回", "10" toolTip "10回")

                    argument("text", StringArg.greedyPhrase()) {

                        suggest("Hello" toolTip "こんにちは", "Bye" toolTip "さようなら")

                        setFunctionExecutor { sender, label, args ->
                            val players = args.getEntities("player")
                            val text = args.getArgument("text", String::class.java)
                            val repeat = args.getArgument("repeat", Int::class.java)
                            players.forEach { player ->
                                repeat(repeat) {
                                    player.sendMessage(text)
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    @SCommandBody
    val item = command {
        literal("item") {
            setPermission("tororo.item")

            literal("info") {

            }

            literal("displayName") {
                argument("name", StringArg.greedyPhrase()) {
                    playerSuggest { sender, _, _ ->
                        val item = sender.noMessageItemInMainHand()?:return@playerSuggest emptyList()
                        listOf(item.itemMeta.displayName.replace("§","&") toolTip "現在の名前")
                    }
                    setPlayerFunctionExecutor { sender, _, args ->
                        val name = args.getArgument("name", String::class.java)
                        val item = sender.itemInMainHand()?:return@setPlayerFunctionExecutor
                        item.itemMeta = item.itemMeta.apply {
                            setDisplayName(name.replace("&", "§"))
                        }

                        sender.sendPrefixMsg(SStr("&a名前を変更しました"))
                    }
                }
            }

            literal("lore") {
                argument("lore", StringArg.greedyPhrase()) {
                    playerSuggest { sender, _, _ ->
                        val item = sender.noMessageItemInMainHand()?:return@playerSuggest emptyList()
                        listOf((item.itemMeta.lore?.joinToString("\\n") { it.replace("§", "&") } ?: "") toolTip "現在のlore")
                    }
                    setPlayerFunctionExecutor { sender, _, args ->
                        val lore = args.getArgument("lore", String::class.java)
                        val item = sender.itemInMainHand()?:return@setPlayerFunctionExecutor
                        item.itemMeta = item.itemMeta.apply {
                            this.lore = lore.split("\\n").map { it.replace("&", "§") }
                        }

                        sender.sendPrefixMsg(SStr("&aloreを変更しました"))
                    }
                }
            }

            literal("customModelData") {
                argument("data", IntArg(min = 0)) {
                    playerSuggest { sender, _, _ ->
                        val item = sender.noMessageItemInMainHand()?:return@playerSuggest emptyList()
                        val cmd = if (item.itemMeta.hasCustomModelData()) item.itemMeta.customModelData else 0
                        listOf(cmd.toString() toolTip "現在のCustomModelData")
                    }
                    setPlayerFunctionExecutor { sender, _, args ->
                        val data = args.getArgument("data", Int::class.java)
                        val item = sender.itemInMainHand()?:return@setPlayerFunctionExecutor
                        item.itemMeta = item.itemMeta?.apply {
                            setCustomModelData(data)
                        }

                        sender.sendPrefixMsg(SStr("&aCustomModelDataを変更しました"))
                    }
                }
            }



            literal("enchant") {
                argument("enchant", StringArg.phrase()) {
                    suggest { _, _, _ ->
                        return@suggest Enchantment.values().map { ToolTip(it.key.key, TororoPlugin.sNms.translate(it.translationKey())) }
                            .plus(japaneseEnchantMap.map { ToolTip("\"${it.key}\"") })
                    }
                    argument("level", IntArg(min = 1, max = 255)) {
                        setPlayerFunctionExecutor { sender, _, args ->
                            val enchant = args.getArgument("enchant", String::class.java).let {
                                if (japaneseEnchantMap.containsKey(it)) {
                                    japaneseEnchantMap[it]!!
                                } else {
                                    Enchantment.getByKey(NamespacedKey.minecraft(it))?:return@setPlayerFunctionExecutor
                                }
                            }
                            val level = args.getArgument("level", Int::class.java)
                            val item = sender.itemInMainHand()?:return@setPlayerFunctionExecutor

                            item.addUnsafeEnchantment(enchant, level)

                            sender.sendPrefixMsg(SStr("&aエンチャントを追加しました"))
                        }
                    }
                }
            }
        }
    }
}