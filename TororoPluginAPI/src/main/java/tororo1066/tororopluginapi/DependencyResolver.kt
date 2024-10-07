package tororo1066.tororopluginapi

import org.bukkit.plugin.java.JavaPlugin
import java.net.URL
import java.net.URLClassLoader

class DependencyResolver(val plugin: JavaPlugin) {
    val urlClassLoader = plugin.javaClass.classLoader as URLClassLoader
    val libFolder = plugin.dataFolder.parentFile.parentFile.resolve("tororo-lib")
    val ADD_URL_METHOD = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java).apply { isAccessible = true }

    fun resolve(group: String, artifact: String, version: String, repository: String? = null) {
        val file = libFolder.resolve("$artifact-$version.jar")
        if (file.exists()) {
            addURL(file.toURI().toURL())
            return
        }
        val url = URL("https://$repository/$group/$artifact/$version/$artifact-$version.jar")

        url.openStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    fun addURL(url: URL) {
        ADD_URL_METHOD.invoke(urlClassLoader, url)
    }
}