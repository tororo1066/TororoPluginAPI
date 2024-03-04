package tororo1066.tororopluginapi.script.action.entity.player

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.ScriptFile.Companion.withVariables
import tororo1066.tororopluginapi.script.action.AbstractAction

class SendMessageAction: AbstractAction("sendMessage") {

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        val split = line.split(" ")
        val player = getPlayer(split[0], scriptFile)?:return
        val message = Expression(split.drop(1).joinToString(" "))
            .withVariables(function, scriptFile)
            .evaluate().stringValue.replace("&","§")
        player.sendMessage(message)
    }
}