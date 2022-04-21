package tororo1066.tororopluginapi

import org.bukkit.plugin.java.JavaPlugin

abstract class SJavaPlugin() : JavaPlugin() {

    lateinit var sConfig: SConfig
    lateinit var mysql: SMySQL

    private var useMySQL = false

    constructor(useMySQL: Boolean): this(){
        this.useMySQL = useMySQL
    }


    override fun onEnable() {
        saveDefaultConfig()
        sConfig = SConfig(this)
        if (useMySQL){
            mysql = SMySQL(this)
        }
    }

}