package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.utils.toIntProgression
import tororo1066.tororopluginapi.utils.toIntProgressionOrNull
import java.util.UUID

class ForAction: AbstractAction("for") {

    override fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        val loadLine = loadNextLines(scriptFile, lineIndex, separator)

        val split = line.split(" in ")
        val variable = split[0].replace(" ", "")
        val moreSplit = split[1].split("@")
        val ruleStr = moreSplit[0].replace(" ", "")
        val label = moreSplit.getOrNull(1)
        val uuid = UUID.randomUUID()
        val format = if (label != null) "$uuid $label" else uuid.toString()
        scriptFile.breakFunction[format] = false
        val expr = Expression(ruleStr, ScriptFile.configuration)
            .withValues(scriptFile.publicVariables).evaluate()
        fun loop(variables: Iterable<Any>){
            for (i in variables) {
                scriptFile.publicVariables[variable] = i
                for (action in loadLine) {
                    if (scriptFile.returnFlag){
                        return
                    }
                    if (scriptFile.breakFunction[format] == true){
                        break
                    }
                    action.invoke()
                }
                if (scriptFile.breakFunction[format] == true){
                    break
                }
            }
            scriptFile.breakFunction.remove(format)
        }
        when {
            expr != null && expr.stringValue?.toIntProgressionOrNull() != null -> {
                loop(expr.stringValue!!.toIntProgression())
            }

            expr != null && expr.isArrayValue -> {
                loop(expr.arrayValue)
            }



            else-> {
                throw NullPointerException("$ruleStr is not loopRule.")
            }
        }

    }
}