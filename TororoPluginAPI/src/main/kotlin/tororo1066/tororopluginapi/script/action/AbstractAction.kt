package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import tororo1066.tororopluginapi.script.ActionData
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.action.inline.EmptyAction
import tororo1066.tororopluginapi.utils.toPlayer
import java.util.UUID

abstract class AbstractAction(val internalName: String) {

    open fun init(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {}

    abstract fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int)

    protected fun loadNextLines(scriptFile: ScriptFile, lineIndex: Int, separator: Int): List<ActionData> {
        val lines = scriptFile.lines.subList(lineIndex+1, scriptFile.lines.size)
        val loadLine = ArrayList<ActionData>()
        run {
            lines.forEach {
                if (it.separator < separator+1 && it.action !is EmptyAction){
                    return@run
                }
                loadLine.add(it)
                scriptFile.debug("Load line: ${it.action.internalName}")
            }
        }

        return loadLine
    }

    protected fun getPlayer(string: String, scriptFile: ScriptFile): Player? {
        return Companion.getPlayer(string, scriptFile)
    }

    companion object {
        fun getPlayer(string: String, scriptFile: ScriptFile): Player? {
            scriptFile.debug("Get player from $string")
            val uuid = UsefulUtility.sTry({ UUID.fromString(string) }, { null })
            if (uuid != null){
                scriptFile.debug("Get player from $string as uuid")
                return uuid.toPlayer()
            }
            if (string.toPlayer() != null){
                scriptFile.debug("Get player from $string as name")
                return string.toPlayer()
            }
            val expr = Expression(string, scriptFile.configuration)
            expr.withValues(scriptFile.publicVariables)
            val format = expr.evaluate().stringValue
            val formatUUID = UsefulUtility.sTry({ UUID.fromString(format) }, { null })
            if (formatUUID != null){
                scriptFile.debug("Get player from $string as uuid")
                return formatUUID.toPlayer()
            }

            scriptFile.debug("Get player from $string as name")
            return format.toPlayer()
        }
    }
}