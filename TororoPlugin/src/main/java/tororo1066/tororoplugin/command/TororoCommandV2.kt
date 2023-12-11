package tororo1066.tororoplugin.command

import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import tororo1066.tororoplugin.TororoPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.annotation.SCommandV2Body
import tororo1066.tororopluginapi.lang.SLang
import tororo1066.tororopluginapi.nms.SNms
import tororo1066.tororopluginapi.sCommand.v2.CommandArguments
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2Arg
import tororo1066.tororopluginapi.sCommand.v2.ToolTip
import tororo1066.tororopluginapi.sCommand.v2.argumentType.DoubleArg
import tororo1066.tororopluginapi.sCommand.v2.argumentType.IntArg
import tororo1066.tororopluginapi.sCommand.v2.argumentType.StringArg
import tororo1066.tororopluginapi.utils.sendMessage
import java.util.*

class TororoCommandV2: SCommandV2("tororo") {

    val japaneseEnchantMap = HashMap<String, Enchantment>()
    val japaneseAttributeMap = HashMap<String, Attribute>()

    var japanese: JsonObject? = null

    init {
        root.setPermission("tororo")
        root.setFunctionExecutor { sender, _, _ ->
            sender.sendMessage("TororoPlugin v${TororoPlugin.plugin.description.version}")
        }


        SLang.downloadMcLangFile("ja_jp") { json ->
            Enchantment.values().forEach {
                japaneseEnchantMap[json[it.translationKey()].asString] = it
            }
            Attribute.values().forEach {
                japaneseAttributeMap[json[it.translationKey()].asString] = it
            }
            japanese = json
        }
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

    private fun CommandSender.sendCopyableMsg(msg: SStr, copyText: String) {
        sendMessage(msg.hoverText("§6クリックでコピー").clickText(SStr.ClickAction.COPY_TO_CLIPBOARD, copyText))
    }

    private inline fun <reified T : Enum<T>> getValue(name: String): T? {
        return enumValues<T>().find { it.name.equals(name, true) }
    }




    lateinit var test: SCommandV2Arg

    @SCommandV2Body
    val test_ = command {
        literal("test") {
            setPlayerFunctionExecutor { sender, _, _ ->
                try {
                    val testA = Class.forName("tororo1066.nmsutils.v${Bukkit.getServer().bukkitVersion.split("-")[0].replace(".","_")}.SNmsImpl")
                    val testB = testA.getConstructor().newInstance() as SNms
                    sender.sendMessage("ok")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SCommandV2Body
    val item = command {
        literal("item") {
            setPermission("tororo.item")

            literal("info") {

                setPlayerFunctionExecutor { sender, _, _ ->
                    val item = sender.itemInMainHand()?:return@setPlayerFunctionExecutor
                    val meta = item.itemMeta?:return@setPlayerFunctionExecutor
                    sender.sendCopyableMsg(SStr("&7Type: ${item.type.name}"), item.type.name)
                    sender.sendCopyableMsg(SStr("&7DisplayName: ${meta.displayName}"), meta.displayName)
                    sender.sendCopyableMsg(SStr("&7Lore: ${if (meta.lore.isNullOrEmpty()) "なし" else ""}"),"")
                    meta.lore?.forEach {
                        sender.sendCopyableMsg(SStr("  $it"), it)
                    }
                    sender.sendCopyableMsg(SStr("&7CustomModelData: ${if (meta.hasCustomModelData()) meta.customModelData else "なし"}"), "")
                    sender.sendCopyableMsg(SStr("&7Enchantments: ${if (meta.enchants.isEmpty()) "なし" else ""}"), "")
                    meta.enchants.forEach {
                        sender.sendCopyableMsg(SStr(
                            "  &7${japanese?.get(it.key.translationKey())?.asString?:it.key.key.key}: ${it.value}"
                        ), it.key.key.key)
                    }
                    sender.sendCopyableMsg(SStr("&7Unbreakable: ${meta.isUnbreakable}"), meta.isUnbreakable.toString())
                    if (meta is Damageable) {
                        sender.sendCopyableMsg(SStr("&7Durability: ${item.type.maxDurability - meta.damage}"), "")
                    }
                    sender.sendCopyableMsg(SStr("&7AttributeModifiers: ${
                        if (meta.attributeModifiers == null || meta.attributeModifiers!!.isEmpty) "なし" else ""
                    }"), "")
                    meta.attributeModifiers?.forEach { attribute, modifier ->
                        sender.sendCopyableMsg(SStr(
                            "  &7${japanese?.get(attribute.translationKey())?.asString?:attribute.key.key}: ${modifier.amount}"
                        ), attribute.key.key)
                    }
                    sender.sendCopyableMsg(SStr("&7ItemFlags: ${if (meta.itemFlags.isEmpty()) "なし" else ""}"), "")
                    meta.itemFlags.forEach {
                        sender.sendCopyableMsg(SStr("  &7${it.name}"), it.name)
                    }
                }
            }

            literal("displayName") {
                test = argument("name", StringArg.greedyPhrase()) {
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

            literal("unbreakable") {
                setPlayerFunctionExecutor { sender, _, _ ->
                    val item = sender.itemInMainHand()?:return@setPlayerFunctionExecutor
                    if (item.itemMeta.isUnbreakable) {
                        item.itemMeta = item.itemMeta.apply {
                            isUnbreakable = false
                        }
                        sender.sendPrefixMsg(SStr("&a不可解を解除しました"))
                    } else {
                        item.itemMeta = item.itemMeta.apply {
                            isUnbreakable = true
                        }
                        sender.sendPrefixMsg(SStr("&a不可解にしました"))
                    }
                }
            }

            fun addAttribute(sender: Player, args: CommandArguments) {
                val item = sender.itemInMainHand()?:return
                val attribute = args.getArgument("attribute", String::class.java).let {
                    if (japaneseAttributeMap.containsKey(it)) {
                        japaneseAttributeMap[it]!!
                    } else {
                        getValue(it.replace(".", "_"))?:return
                    }
                }
                val amount = args.getArgument("amount", Double::class.java)
                val operation = args.getNullableArgument("operation", String::class.java)?.let {
                    getValue<AttributeModifier.Operation>(it)?:return
                }?:AttributeModifier.Operation.ADD_NUMBER
                val slot = args.getNullableArgument("slot", String::class.java)?.let {
                    getValue<EquipmentSlot>(it)?:return
                }
                val uuid = UUID.randomUUID()
                item.itemMeta = item.itemMeta.apply {
                    addAttributeModifier(attribute, AttributeModifier(uuid, uuid.toString(), amount, operation, slot))
                }

                sender.sendPrefixMsg(SStr("&aAttributeを追加しました"))
            }

            literal("attribute") {
                argument("attribute", StringArg.phrase()) {
                    suggest { _, _, _ ->
                        return@suggest Attribute.values().map { ToolTip(it.key.key, TororoPlugin.sNms.translate(it.translationKey())) }
                            .plus(japaneseAttributeMap.map { ToolTip("\"${it.key}\"") })
                    }

                    argument("amount", DoubleArg()) {
                        setPlayerFunctionExecutor { sender, _, args ->
                            addAttribute(sender, args)
                        }

                        argument("operation", StringArg.word()) {
                            suggest(*AttributeModifier.Operation.values().map { ToolTip(it.name.lowercase()) }.toTypedArray())

                            setPlayerFunctionExecutor { sender, _, args ->
                                addAttribute(sender, args)
                            }

                            argument("slot", StringArg.word()) {
                                suggest(*EquipmentSlot.values().map { ToolTip(it.name.lowercase()) }.toTypedArray())

                                setPlayerFunctionExecutor { sender, _, args ->
                                    addAttribute(sender, args)
                                }
                            }
                        }
                    }
                }
            }

            literal("flags") {
                argument("flag", StringArg.word()) {
                    suggest { _, _, _ ->
                        return@suggest ItemFlag.values().map { ToolTip(it.name.lowercase()) }
                    }

                    setPlayerFunctionExecutor { sender, _, args ->
                        val flag = args.getArgument("flag", String::class.java).let {
                            getValue<ItemFlag>(it)?:return@setPlayerFunctionExecutor
                        }
                        val item = sender.itemInMainHand()?:return@setPlayerFunctionExecutor
                        val meta = item.itemMeta
                        if (meta.hasItemFlag(flag)) {
                            meta.removeItemFlags(flag)
                            sender.sendPrefixMsg(SStr("&aフラグを削除しました"))
                        } else {
                            meta.addItemFlags(flag)
                            sender.sendPrefixMsg(SStr("&aフラグを追加しました"))
                        }
                        item.itemMeta = meta
                    }
                }
            }

            literal("durability") {
                argument("durability", IntArg(min = 0, max = 32767)) {
                    setPlayerFunctionExecutor { sender, _, args ->
                        val durability = args.getArgument("durability", Int::class.java)
                        val item = sender.itemInMainHand()?:return@setPlayerFunctionExecutor
                        val meta = item.itemMeta
                        if (meta !is Damageable) {
                            sender.sendPrefixMsg(SStr("&c耐久値を持たないアイテムです"))
                            return@setPlayerFunctionExecutor
                        }
                        meta.damage = item.type.maxDurability - durability
                        item.itemMeta = meta
                        sender.sendPrefixMsg(SStr("&a耐久値を変更しました"))
                    }
                }
            }
        }
    }

    @SCommandV2Body(asRoot = true)
    val tororo = command {
        literal("rename") {
            arg(test)
        }
    }
}