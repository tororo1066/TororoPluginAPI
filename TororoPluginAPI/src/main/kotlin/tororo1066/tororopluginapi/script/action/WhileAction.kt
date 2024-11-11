package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import tororo1066.tororopluginapi.script.ScriptFile
import java.util.*

class WhileAction: AbstractAction("while") {

    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        val loadLine = loadNextLines(scriptFile, lineIndex, separator)

        val split = line.split("@")
        val condition = Expression(split[0], scriptFile.configuration)
        val label = split.getOrNull(1)
        val uuid = UUID.randomUUID()
        val format = if (label != null) "$uuid $label" else uuid.toString()
        scriptFile.breakFunction[format] = false
        while (condition.withValues(scriptFile.publicVariables).evaluate().booleanValue){
            for (action in loadLine) {
                if (scriptFile.returns.containsKey(function)){
                    return
                }
                if (scriptFile.breakFunction[format] == true){
                    break
                }
                if (action.separator != separator+1){
                    continue
                }
                action.invoke(function)
            }
            if (scriptFile.breakFunction[format] == true){
                break
            }
        }
        scriptFile.breakFunction.remove(format)
    }
}