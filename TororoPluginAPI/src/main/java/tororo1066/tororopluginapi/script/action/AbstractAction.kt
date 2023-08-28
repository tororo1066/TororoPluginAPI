package tororo1066.tororopluginapi.script.action

import org.bukkit.entity.Player
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

    protected fun getPlayer(string: String): Player? {
        val uuid = UUID.fromString(string)
        return if (uuid == null){
            string.toPlayer()
        } else {
            uuid.toPlayer()
        }
    }
}