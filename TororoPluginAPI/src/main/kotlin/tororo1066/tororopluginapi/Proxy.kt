package tororo1066.tororopluginapi

import org.bukkit.plugin.java.JavaPlugin
import java.util.IdentityHashMap

class Proxy(val plugin: JavaPlugin, private val packageName: String) {
    private val version = plugin.server.bukkitVersion.split("-")[0].replace(".", "_")
    private val classCache = IdentityHashMap<String, Class<*>>()

//    fun <T: Any> getProxy(clazz: Class<T>): T {
//        if (cache.containsKey(clazz)) return clazz.cast(cache[clazz]!!)
//        val proxyClass = plugin::class.java.classLoader.loadClass("$packageName.v$version.${clazz.simpleName}Impl")
//        val instance = proxyClass.getDeclaredConstructor().newInstance()
//        if (clazz.isAssignableFrom(proxyClass) && clazz.isInstance(instance)) {
//            return clazz.cast(instance)
//        } else throw UnsupportedOperationException("${clazz.simpleName} is not supported in ${plugin.server.minecraftVersion}.")
//    }

    fun <T: Any> getProxy(clazz: Class<T>, vararg initArgs: Pair<Class<*>, Any>): T {
//        val proxyClass = plugin::class.java.classLoader.loadClass("$packageName.v$version.${clazz.simpleName}Impl")
        val proxyClass: Class<*> = if (classCache.containsKey(clazz.simpleName)) classCache[clazz.simpleName]!! else {
            val loadedClass = plugin::class.java.classLoader.loadClass("$packageName.v$version.${clazz.simpleName}Impl")
            classCache[clazz.simpleName] = loadedClass
            loadedClass
        }
        val instance = proxyClass.getDeclaredConstructor(*initArgs.map { it.first }.toTypedArray()).newInstance(*initArgs.map { it.second }.toTypedArray())
        if (clazz.isAssignableFrom(proxyClass) && clazz.isInstance(instance)) {
            return clazz.cast(instance)
        } else throw UnsupportedOperationException("${clazz.simpleName} is not supported in ${plugin.server.minecraftVersion}.")
    }
}