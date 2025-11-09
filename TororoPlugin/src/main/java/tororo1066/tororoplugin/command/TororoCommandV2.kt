package tororo1066.tororoplugin.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import tororo1066.commandapi.argumentType.EntityArg
import tororo1066.commandapi.argumentType.StringArg
import tororo1066.tororoplugin.TororoPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.annotation.SCommandV2Body
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.tororopluginapi.utils.sendMessage

class TororoCommandV2: SCommandV2("tororo") {

    init {
        root.setPermission("tororo.op")
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

    @SCommandV2Body
    val item = command {
        literal("item") {

            literal("fromBase64") {
                argument("base64", StringArg.greedyPhrase()) {
                    setPlayerFunctionExecutor { sender, _, args ->
                        val base64 = args.getArgument("base64", String::class.java)
                        val item = SItem.byBase64(base64)
                        if (item == null) {
                            sender.sendPrefixMsg(SStr("&c不正なBase64です"))
                            return@setPlayerFunctionExecutor
                        }
                        sender.inventory.addItem(item.build())
                        sender.sendPrefixMsg(SStr("&aアイテムを付与しました"))
                    }
                }
            }

            literal("toBase64") {
                setPlayerFunctionExecutor { sender, _, _ ->
                    val item = sender.itemInMainHand()?:return@setPlayerFunctionExecutor
                    val base64 = SItem(item).toByteArrayBase64()
                    sender.sendCopyableMsg(SStr("&a[ここをクリックでコピー]"), base64)
                }
            }
        }
    }

    @SCommandV2Body
    val sudo = command {
        literal("sudo") {
            argument("player", EntityArg(singleTarget = false, playersOnly = true)) {
                argument("command", StringArg.greedyPhrase()) {
                    setFunctionExecutor { sender, _, args ->
                        val players = args.getEntities("player").map { it as Player }
                        val command = args.getArgument("command", String::class.java)
                        players.forEach {
                            Bukkit.dispatchCommand(it, command)
                        }
                        sender.sendPrefixMsg(SStr("&aコマンドを実行しました"))
                    }
                }
            }
        }
    }
}