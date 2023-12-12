package tororo1066.tororopluginapi

import org.bukkit.plugin.java.JavaPlugin
import java.util.IdentityHashMap

class Proxy(val plugin: JavaPlugin, val packageName: String) {
    private val version = plugin.server.bukkitVersion.split("-")[0].replace(".", "_")
    private val cache = IdentityHashMap<Class<out Any>, Any>()

    fun <T: Any> getProxy(clazz: Class<T>): T {
        if (cache.containsKey(clazz)) return clazz.cast(cache[clazz]!!)
        val proxyClass = plugin::class.java.classLoader.loadClass("$packageName.v$version.${clazz.simpleName}Impl")
        val instance = proxyClass.getDeclaredConstructor().newInstance()
        if (clazz.isAssignableFrom(proxyClass) && clazz.isInstance(instance)) {
            return clazz.cast(instance)
        } else throw UnsupportedOperationException("${clazz.simpleName} is not supported in ${plugin.server.bukkitVersion}.")
    }
}