package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.ScriptFile.Companion.withVariables
import tororo1066.tororopluginapi.script.action.hidden.ElseAction

class IfAction: AbstractAction("if") {

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        val expression = Expression(line, scriptFile.configuration)
            .withVariables(function, scriptFile)
        if (expression.evaluate().booleanValue){
            val lines = loadNextLines(scriptFile, lineIndex, separator)

            lines.forEach {
                if (scriptFile.returns.containsKey(function)){
                    return
                }
                if (it.separator != separator+1){
                    return@forEach
                }
                it.invoke(function)
            }
        } else {
            val elseFind = scriptFile.lines.subList(lineIndex+1, scriptFile.lines.size).filter {
                it.separator == separator && (it.action is IfAction || it.action is ElseAction)
            }

            if ((elseFind.firstOrNull()?:return).action is IfAction)return

            val lines = loadNextLines(scriptFile, elseFind.first().lineIndex, separator)

            lines.forEach {
                if (scriptFile.returns.containsKey(function)){
                    return
                }
                if (it.separator != separator+1){
                    return@forEach
                }
                it.invoke(function)
            }
        }
    }
}