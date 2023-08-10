package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile

class ReturnAction: AbstractAction("return") {

    override fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        scriptFile.returnFlag = true
        if (line.isBlank()){
            scriptFile.returnValue = Unit
            return
        }
        val expression = Expression(line, ScriptFile.configuration)
            .withValues(scriptFile.publicVariables)
        val eval = expression.evaluate()
        if (eval.isBooleanValue){
            scriptFile.returnValue = eval.booleanValue
        } else {
            scriptFile.returnValue = eval.value
        }
    }
}