package tororo1066.tororopluginapi.script.action.entity.player

import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.action.AbstractAction

class SendMessageAction: AbstractAction("sendMessage") {

    override fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        val split = line.split(" ")
        val player = getPlayer(scriptFile, split[0])?:return
        val message = split.drop(1).joinToString(" ").replace("&","§")
        player.sendMessage(message)
    }
}