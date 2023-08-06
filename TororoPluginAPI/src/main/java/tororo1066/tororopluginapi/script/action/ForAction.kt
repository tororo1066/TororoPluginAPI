package tororo1066.tororopluginapi.script.action

import tororo1066.tororopluginapi.script.ActionData
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.utils.toIntRange
import tororo1066.tororopluginapi.utils.toIntRangeOrNull
import java.util.UUID

class ForAction: AbstractAction("for") {

    override fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        val lines = scriptFile.lines.subList(lineIndex+1, scriptFile.lines.size)
        val loadLine = ArrayList<ActionData>()
        run {
            lines.forEach {
                if (it.separator < separator+1){
                    return@run
                }
                if (it.separator > separator+1){
                    return@forEach
                }
                loadLine.add(it)
            }
        }

        val split = line.split(" in ")
        val variable = split[0].replace(" ", "")
        val moreSplit = split[1].split("@")
        val ruleStr = moreSplit[0].replace(" ", "")
        val label = moreSplit.getOrNull(1)
        val uuid = UUID.randomUUID()
        val format = if (label != null) "$uuid $label" else uuid.toString()
        scriptFile.breakFunction[format] = false
        when {
            ruleStr.toIntRangeOrNull() != null -> {
                for (i in ruleStr.toIntRange()) {
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



            else-> {
                throw NullPointerException("$ruleStr is not loopRule.")
            }
        }

    }
}