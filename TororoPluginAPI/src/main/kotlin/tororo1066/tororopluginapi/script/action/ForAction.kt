package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import com.ezylang.evalex.data.EvaluationValue
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.ScriptFile.Companion.withVariables
import tororo1066.tororopluginapi.utils.toIntProgression
import tororo1066.tororopluginapi.utils.toIntProgressionOrNull
import java.util.UUID

class ForAction: AbstractAction("for") {

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        val loadLine = loadNextLines(scriptFile, lineIndex, separator)

        val split = line.split(" in ")
        val variable = split[0].replace(" ", "")
        val moreSplit = split[1].split("@")
        val ruleStr = moreSplit[0].replace(" ", "")
        val label = moreSplit.getOrNull(1)
        val uuid = UUID.randomUUID()
        val format = if (label != null) "$uuid $label" else uuid.toString()
        scriptFile.breakFunction[format] = false
        val expr = Expression(ruleStr, scriptFile.configuration)
            .withVariables(function, scriptFile)
            .evaluate()
        fun loop(variables: Iterable<Any>){
            for (i in variables) {
                scriptFile.publicVariables[variable] = (i as? EvaluationValue)?.value ?: i
                for (action in loadLine) {
                    if (scriptFile.returns.containsKey(function)) {
                        return
                    }
                    if (scriptFile.breakFunction[format] == true) {
                        break
                    }
                    if (action.separator != separator+1) {
                        continue
                    }
                    action.invoke(function)
                }
                if (scriptFile.breakFunction[format] == true) {
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



            else -> {
                throw NullPointerException("$ruleStr is not loopRule.")
            }
        }

    }
}