package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ActionData
import tororo1066.tororopluginapi.script.ScriptFile
import java.util.*
import kotlin.collections.ArrayList

class WhileAction: AbstractAction("while") {

    override fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        val loadLine = loadNextLines(scriptFile, lineIndex, separator)

        val split = line.split("@")
        val condition = Expression(split[0], ScriptFile.configuration)
        val label = split.getOrNull(1)
        val uuid = UUID.randomUUID()
        val format = if (label != null) "$uuid $label" else uuid.toString()
        scriptFile.breakFunction[format] = false
        while (condition.withValues(scriptFile.publicVariables).evaluate().booleanValue){
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
}