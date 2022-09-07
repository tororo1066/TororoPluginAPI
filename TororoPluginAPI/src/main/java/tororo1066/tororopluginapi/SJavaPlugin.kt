package tororo1066.tororopluginapi

import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.annotation.SEventHandler
import tororo1066.tororopluginapi.otherPlugin.SVault
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandObject
import java.io.File
import java.net.JarURLConnection
import java.net.URISyntaxException
import java.net.URL
import java.util.jar.JarFile

abstract class SJavaPlugin() : JavaPlugin() {

    companion object {
        lateinit var sConfig: SConfig
        lateinit var mysql: SMySQL
        lateinit var vault: SVault
        lateinit var plugin: SJavaPlugin
    }

    private val useOptions = ArrayList<UseOption>()
    private var folder = ""

    constructor(vararg options: UseOption) : this(){
        this.useOptions.addAll(options)
    }

    constructor(pluginFolder: String, vararg options: UseOption): this(){
        this.useOptions.addAll(options)
        this.folder = pluginFolder
    }

    abstract fun onStart()

    @Suppress("UNCHECKED_CAST")
    override fun onEnable() {
        plugin = this
        if (useOptions.contains(UseOption.SConfig)){
            saveDefaultConfig()
            sConfig = SConfig(this)
        }
        if (useOptions.contains(UseOption.MySQL)){
            mysql = SMySQL(this)
        }
        if (useOptions.contains(UseOption.Vault)){
            vault = SVault()
        }

        if (folder.isBlank()){
            val split = description.main.split(".").dropLast(1).joinToString(".")
            folder = split
        }

        val instancedClasses = HashMap<Class<*>,Any>()
        javaClass.protectionDomain.codeSource.location.getClasses(folder).forEach { clazz ->
            if (UsefulUtility.sTry({clazz.getConstructor()},{null}) == null){
                return@forEach
            }
            if (clazz.superclass == SCommand::class.java){
                val instance = clazz.getConstructor().newInstance() as SCommand
                clazz.declaredFields.forEach second@ {
                    if (!it.isAnnotationPresent(SCommandBody::class.java))return@second
                    if (it.type != SCommandObject::class.java)return@second
                    it.isAccessible = true
                    val data = it.get(instance) as SCommandObject
                    val sCommand = it.getAnnotation(SCommandBody::class.java)
                    if (sCommand.permission.isNotBlank()){
                        data.addNeedPermission(sCommand.permission)
                    }
                    it.isAccessible = false
                    instance.addCommand(data)
                }
                instancedClasses[instance.javaClass] = instance
            }

            try {
                clazz.declaredMethods.forEach second@ { method ->
                    if (!method.isAnnotationPresent(SEventHandler::class.java))return@second
                    if (method.parameterTypes.size != 1)return@second
                    val sEvent = method.getAnnotation(SEventHandler::class.java)
                    val event = method.parameterTypes[0]
                    if (!sEvent.autoRegister)return@second
                    val instance =
                        if (instancedClasses.contains(clazz)) instancedClasses[clazz]!! else clazz.getConstructor().newInstance()
                    val listener = object : Listener, EventExecutor {
                        override fun execute(listener: Listener, e: Event) {
                            if (e.javaClass != event)return
                            method.invoke(instance,event.cast(e))
                        }
                    }
                    server.pluginManager.registerEvent(method.parameters[0].type as Class<out Event>,listener,sEvent.property,listener,this)
                }
            } catch (_: NoClassDefFoundError){

            }
        }

        onStart()
    }

    enum class UseOption {
        MySQL,
        Vault,
        SConfig
    }



}

private fun URL.getClasses(folder: String): List<Class<*>> {
    val classes = ArrayList<Class<*>>()
    val src = ArrayList<File>()
    val srcFile = try {
        File(toURI())
    } catch (ex: IllegalArgumentException) {
        File((openConnection() as JarURLConnection).jarFileURL.toURI())
    } catch (ex: URISyntaxException) {
        File(path)
    }

    src += srcFile

    src.forEach { s ->
        JarFile(s).stream().filter { it.name.endsWith(".class") }.forEach second@{
            val name = it.name.replace('/', '.').substring(0, it.name.length - 6)
            if (!name.contains(folder))return@second

            kotlin.runCatching {
                classes.add(Class.forName(name, false, SJavaPlugin::class.java.classLoader))
            }
        }
    }
    return classes
}