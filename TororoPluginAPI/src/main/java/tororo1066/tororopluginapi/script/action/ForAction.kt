package tororo1066.tororopluginapi.script.action

import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.utils.toIntRange
import tororo1066.tororopluginapi.utils.toIntRangeOrNull

class ForAction: AbstractAction("for") {

    override fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        val lines = scriptFile.lines.subList(lineIndex+1, scriptFile.lines.size).takeWhile {
            it.separator == separator+1
        }
        val split = line.split(" in ")
        val variable = split[0].replace(" ", "")
        val ruleStr = split[1]
        when {
            ruleStr.toIntRangeOrNull() != null -> {
                for (i in ruleStr.toIntRange()){
                    scriptFile.publicVariables[variable] = i
                    lines.forEach {
                        it.invoke()
                    }
                }
            }



            else-> {
                throw NullPointerException("$ruleStr is not loopRule.")
            }
        }

    }
}