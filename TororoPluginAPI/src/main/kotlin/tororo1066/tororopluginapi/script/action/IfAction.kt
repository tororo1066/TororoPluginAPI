package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.ScriptFile.Companion.withVariables
import tororo1066.tororopluginapi.script.action.hidden.ElseAction

open class IfAction(name: String): AbstractAction(name) {

    constructor(): this("if")

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        val expression = Expression(line, scriptFile.configuration)
            .withVariables(function, scriptFile)
        if (expression.evaluate().booleanValue){
            val lines = loadNextLines(scriptFile, lineIndex, separator)
            //TODO: 成功したときにIf外のActionが実行されないから修正する
            lines.forEach {
                if (it.separator != separator+1){
                    return@forEach
                }
                it.invoke(function)
                if (scriptFile.returns.containsKey(function)){
                    return
                }
            }
        } else {
            val elseFind = scriptFile.lines.subList(lineIndex+1, scriptFile.lines.size).filter {
                it.separator == separator && (it.action is ElifAction || it.action is IfAction || it.action is ElseAction)
            }

            val first = elseFind.firstOrNull()?:return

            if (first.action is ElifAction) {
                first.invoke(function)
                return
            }

            if (first.action is IfAction)return

            val lines = loadNextLines(scriptFile, first.lineIndex, separator)

            lines.forEach {
                if (it.separator != separator+1){
                    return@forEach
                }
                it.invoke(function)
                if (scriptFile.returns.containsKey(function)){
                    return
                }
            }
        }
    }
}