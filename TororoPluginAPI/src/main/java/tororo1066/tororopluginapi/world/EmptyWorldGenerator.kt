package tororo1066.tororopluginapi.world

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import java.util.jar.JarFile

class EmptyWorldGenerator(val plugin: JavaPlugin) {

    val instanceId = AtomicInteger(0)

    constructor(): this(SJavaPlugin.plugin)

    fun createEmptyWorld(name: String): World {
        val worldName = "${plugin.name.lowercase()}_${name}_${instanceId.getAndIncrement()}"
        val world = plugin.server.getWorld(worldName)
        if (world != null) {
            throw IllegalArgumentException("World $worldName already exists")
        }

        val output = Bukkit.getWorldContainer().resolve(worldName)

        try {
            copyFolder(plugin, "empty_world", output)
        } catch (e: Exception) {
            throw RuntimeException("Failed to copy world files", e)
        }


        return Bukkit.createWorld(WorldCreator(worldName).generator(EmptyChunkGenerator))!!
    }

    private object EmptyChunkGenerator: ChunkGenerator()

    companion object {
        private fun copyFolder(plugin: JavaPlugin, dirName: String, outputFolder: File) {
            val pluginFile = if (plugin is SJavaPlugin) {
                plugin.file
            } else {
                val method = JavaPlugin::class.java.getDeclaredMethod("getFile")
                method.isAccessible = true
                method.invoke(plugin) as File
            }
            val jar = JarFile(pluginFile)

            jar.use {
                jar.entries().asSequence().forEach { entry ->
                    if (!entry.name.startsWith(dirName) || entry.isDirectory) {
                        return@forEach
                    }

                    val outputPath = outputFolder.resolve(entry.name.substring(dirName.length + 1))
                    outputPath.parentFile?.mkdirs()

                    jar.getInputStream(entry).use { input ->
                        outputPath.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
        }

        fun deleteWorld(world: World) {
            Bukkit.unloadWorld(world, false)
            val worldFolder = world.worldFolder
            worldFolder.deleteRecursively()
        }
    }
}