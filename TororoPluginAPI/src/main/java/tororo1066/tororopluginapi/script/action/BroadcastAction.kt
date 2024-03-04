package tororo1066.tororopluginapi.script.action

import com.ezylang.evalex.Expression
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server
import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.ScriptFile.Companion.withVariables

class BroadcastAction: AbstractAction("broadcast") {
    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        Bukkit.broadcast(Component.text(
            Expression(line, scriptFile.configuration)
                .withVariables(function, scriptFile)
                .evaluate().stringValue
        ), Server.BROADCAST_CHANNEL_USERS)
    }


}