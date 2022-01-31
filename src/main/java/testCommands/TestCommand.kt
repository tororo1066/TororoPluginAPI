package testCommands

import org.bukkit.command.Command
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.entity.SPlayer
import tororo1066.tororopluginapi.sCommand.OnlyPlayerExecutor
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandObject

class TestCommand : SCommand("tororo") {

    init {
        setCommandNoFoundEvent {it.sender.sendMessage("そんなこまんどないよ")}
        addCommand(SCommandObject().addArg(SCommandArg().addAllowString("test")).addNeedPermission("test").setMode(SCommandObject.Mode.PLAYER).setExecutor(TestCommand2()))
    }
}

class TestCommand2 : OnlyPlayerExecutor {
    override fun onCommand(sender: SPlayer, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage("test!")
        return true
    }
}