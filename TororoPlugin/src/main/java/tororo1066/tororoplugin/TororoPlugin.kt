package tororo1066.tororoplugin

import net.ess3.api.IEssentials
import org.bukkit.Bukkit
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import java.util.UUID

class TororoPlugin: SJavaPlugin() {

    companion object{
        val commandLogPlayers = ArrayList<UUID>()
        val prefix = SStr("&6[&aTororo&5Plugin&cAPI&6]").toString()
        var essentials: IEssentials? = null
    }

    override fun onStart() {
        val essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials")
        if (essentialsPlugin != null){
            essentials = essentialsPlugin as IEssentials
        }
    }

}