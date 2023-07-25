package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server
import tororo1066.tororopluginapi.script.ScriptFile

class PrintAction: AbstractAction("print") {
    override fun invoke(scriptFile: ScriptFile, line: String, lineIndex: Int, separator: Int) {
        Bukkit.broadcast(Component.text(
            Expression(line)
                .withValues(scriptFile.publicVariables)
                .evaluate().stringValue
        ), Server.BROADCAST_CHANNEL_USERS)
    }


}