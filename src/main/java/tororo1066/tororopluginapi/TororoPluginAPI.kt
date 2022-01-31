package tororo1066.tororopluginapi

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import testCommands.TestCommand
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TororoPluginAPI : JavaPlugin() , Listener{

    companion object{
        lateinit var plugin : TororoPluginAPI
        lateinit var es : ExecutorService
    }

    override fun onEnable() {
        plugin = this
        es = Executors.newCachedThreadPool()
        TestCommand()
    }
    init {

    }





}