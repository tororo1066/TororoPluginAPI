package tororo1066.tororopluginapi.sCommand.report

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class ReportList(val plugin: JavaPlugin, private val perm: String) : CommandExecutor {
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

        val file = File(plugin.dataFolder.path + "/report")
        if (!file.exists()){
            sender.sendMessage("§4レポートが存在しません")
            return true
        }
        sender.sendMessage("§b§lプラグイン名 §a§l送り主 §d§lタイトル")
        for ((index, oneFile) in (file.listFiles()?:return true).withIndex()){
            if (oneFile.extension != "yml")continue
            val yaml = YamlConfiguration.loadConfiguration(oneFile)
            val read = yaml.getBoolean("Read",true)
            if (read)continue
            if (index >= 9)break
            sender.spigot().sendMessage(*ComponentBuilder("§b§l${plugin.name}§f§l：§a§l${yaml.getString("Player","null")}§f§l：§d§l${yaml.getString("Title")}").event(
                ClickEvent(ClickEvent.Action.RUN_COMMAND,"/${label} reportlog ${oneFile.nameWithoutExtension}")
            ).event(HoverEvent(HoverEvent.Action.SHOW_TEXT,Text("§6ここをクリックでログを見る"))).create())
        }
        return true
    }
}