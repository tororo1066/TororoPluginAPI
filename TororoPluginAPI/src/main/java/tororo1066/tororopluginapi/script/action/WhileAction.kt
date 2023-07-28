package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ActionData
import tororo1066.tororopluginapi.script.ScriptFile
import java.util.*
import kotlin.collections.ArrayList

class WhileAction: AbstractAction("while") {

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

        val split = line.split("@")
        val condition = Expression(split[0])
        val label = split.getOrNull(1)
        val uuid = UUID.randomUUID()
        val format = if (label != null) "$uuid $label" else uuid.toString()
        scriptFile.breakFunction[format] = false
        while (condition.evaluate().booleanValue){
            for (actionData in loadLine) {
                if (scriptFile.breakFunction[format] == true){
                    break
                }
                actionData.invoke()
            }
            if (scriptFile.breakFunction[format] == true){
                break
            }
        }
        scriptFile.breakFunction.remove(format)
    }
}