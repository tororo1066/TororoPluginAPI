package tororo1066.tororopluginapi

import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.otherPlugin.SVault

abstract class SJavaPlugin() : JavaPlugin() {

    companion object {
        lateinit var sConfig: SConfig
        lateinit var mysql: SMySQL
        lateinit var vault: SVault
    }


    private val useOptions = ArrayList<UseOption>()


    constructor(vararg options: UseOption) : this(){
        this.useOptions.addAll(options)
    }


    override fun onEnable() {
        saveDefaultConfig()
        sConfig = SConfig(this)
        if (useOptions.contains(UseOption.MySQL)){
            mysql = SMySQL(this)
        }
        if (useOptions.contains(UseOption.Vault)){
            vault = SVault()
        }
    }

    enum class UseOption {
        MySQL,
        Vault
    }

}