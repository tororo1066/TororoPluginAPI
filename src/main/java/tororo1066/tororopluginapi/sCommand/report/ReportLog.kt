package tororo1066.tororopluginapi.sCommand.report

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ReportLog(val plugin: JavaPlugin, private val perm: String) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (!sender.hasPermission(perm)){
            if (plugin.description.authors.isEmpty()){
                sender.sendMessage("§4§l権限がありません")
                return true
            }
            if (!plugin.description.authors.contains(sender.name)){
                sender.sendMessage("§4§l権限がありません")
                return true
            }
        }

        val file = File(plugin.dataFolder.path + "/report/${args[1]}.yml")
        if (!file.exists()){
            sender.sendMessage("§4ファイルが存在しません")
            return true
        }

        val yaml = YamlConfiguration.loadConfiguration(file)
        sender.sendMessage("§d件名：${yaml.getString("Title")}")
        sender.sendMessage("§b送り主：${yaml.getString("Player")}")
        sender.sendMessage("§a日付：${yaml.getString("Date")}")
        sender.sendMessage(yaml.getString("Text"))
        yaml.set("Read",true)
        yaml.save(file)
        return true
    }
}