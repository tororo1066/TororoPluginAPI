package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import org.bukkit.Bukkit
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.action.hidden.ElseAction

class IfAction: AbstractAction("if") {

    override fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        val expression = Expression(line, ScriptFile.configuration)
            .withValues(scriptFile.publicVariables)
        if (expression.evaluate().booleanValue){
            val lines = scriptFile.lines.subList(lineIndex+1, scriptFile.lines.size).takeWhile {
                it.separator == separator+1
            }

            lines.forEach {
                it.invoke()
            }
        } else {
            val elseFind = scriptFile.lines.subList(lineIndex+1, scriptFile.lines.size).filter {
                it.separator == separator && (it.action is IfAction || it.action is ElseAction)
            }

            if ((elseFind.firstOrNull()?:return).action is IfAction)return

            val lines = scriptFile.lines.subList(elseFind.first().lineIndex+1, scriptFile.lines.size).takeWhile {
                it.separator == separator+1
            }

            lines.forEach {
                it.invoke()
            }
        }
    }
}