package tororo1066.tororoplugin.command

import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2
import tororo1066.tororopluginapi.sCommand.v2.argumentType.EntityArg
import tororo1066.tororopluginapi.sCommand.v2.argumentType.IntArg
import tororo1066.tororopluginapi.sCommand.v2.argumentType.StringArg

class TororoCommandV2: SCommandV2("tororo") {

    @SCommandBody
    val tororo = command {
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
}