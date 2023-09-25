package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import tororo1066.tororopluginapi.script.ActionData
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.utils.toPlayer
import java.util.UUID

abstract class AbstractAction(val internalName: String) {

    abstract fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int)

    protected fun loadNextLines(scriptFile: ScriptFile, lineIndex: Int, separator: Int): List<ActionData> {
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

        return loadLine
    }

    protected fun getPlayer(scriptFile: ScriptFile, string: String): Player? {
        val format = Expression(string, ScriptFile.configuration)
            .withValues(scriptFile.publicVariables)
            .evaluate().stringValue
        val uuid = UsefulUtility.sTry({ UUID.fromString(format) }, { null })
        return if (uuid == null){
            format.toPlayer()
        } else {
            uuid.toPlayer()
        }
    }
}