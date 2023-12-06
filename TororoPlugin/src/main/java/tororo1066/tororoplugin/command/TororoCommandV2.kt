package tororo1066.tororoplugin.command

import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import tororo1066.nmsutils.command.ToolTip
import tororo1066.tororoplugin.TororoPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.frombukkit.SBukkit
import tororo1066.tororopluginapi.lang.SLang
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2
import tororo1066.tororopluginapi.sCommand.v2.argumentType.DoubleArg
import tororo1066.tororopluginapi.sCommand.v2.argumentType.EntityArg
import tororo1066.tororopluginapi.sCommand.v2.argumentType.IntArg
import tororo1066.tororopluginapi.sCommand.v2.argumentType.StringArg
import tororo1066.tororopluginapi.utils.sendMessage
import java.util.*
import kotlin.collections.HashMap

class TororoCommandV2: SCommandV2("tororo") {

    val japaneseEnchantMap = HashMap<String, Enchantment>()
    val japaneseAttributeMap = HashMap<String, Attribute>()

    init {
        root.setPermission("tororo")
        SBukkit.registerSEvent(this)


        SLang.downloadMcLangFile("ja_jp") { json ->
            Enchantment.values().forEach {
                japaneseEnchantMap[json[it.translationKey()].asString] = it
            }
            Attribute.values().forEach {
                japaneseAttributeMap[json[it.translationKey()].asString] = it
            }
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

            literal("attribute") {
                argument("attribute", StringArg.phrase()) {
                    suggest { _, _, _ ->
                        return@suggest Attribute.values().map { ToolTip(it.key.key, TororoPlugin.sNms.translate(it.translationKey())) }
                            .plus(japaneseAttributeMap.map { ToolTip("\"${it.key}\"") })

                    }

                    argument("slot", StringArg.word()) {
                        suggest(*EquipmentSlot.values().map { ToolTip(it.name.lowercase()) }.toTypedArray())

                        argument("amount", DoubleArg()) {
                            setPlayerFunctionExecutor { sender, _, args ->
                                val attribute = args.getArgument("attribute", String::class.java).let {
                                    if (japaneseAttributeMap.containsKey(it)) {
                                        japaneseAttributeMap[it]!!
                                    } else {
                                        UsefulUtility.sTry(
                                            { Attribute.valueOf(it.replace(".", "_").uppercase()) },
                                            { null }) ?: return@setPlayerFunctionExecutor
                                    }
                                }
                                val amount = args.getArgument("amount", Double::class.java)
                                val slot = args.getArgument("slot", String::class.java).let {
                                    UsefulUtility.sTry(
                                        { EquipmentSlot.valueOf(it.uppercase()) },
                                        { null }) ?: return@setPlayerFunctionExecutor
                                }
                                val item = sender.itemInMainHand() ?: return@setPlayerFunctionExecutor

                                val uuid = UUID.randomUUID()
                                item.itemMeta = item.itemMeta.apply {
                                    addAttributeModifier(attribute, AttributeModifier(uuid, uuid.toString(), amount, AttributeModifier.Operation.ADD_NUMBER, slot))
                                }

                                sender.sendPrefixMsg(SStr("&aAttributeを追加しました"))
                            }
                        }
                    }
                }
            }
        }
    }
}