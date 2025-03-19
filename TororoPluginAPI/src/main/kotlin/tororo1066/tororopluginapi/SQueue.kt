package tororo1066.tororopluginapi

import org.bukkit.Bukkit
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

class SQueue(val prefix: String = "Undefined", val sync: Boolean = false) {

    companion object {
        val queues = arrayListOf<SQueue>()

        fun shutdownAll() {
            queues.forEach {
                it.shutdown()
            }
        }
    }

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val queue = LinkedBlockingQueue<() -> Unit>()

    init {
        queues.add(this)
        executor.execute {
            while (true) {
                try {
                    val task = queue.take()
                    if (sync) {
                        Bukkit.getScheduler().runTask(SJavaPlugin.plugin, task)
                    } else {
                        task.invoke()
                    }
                } catch (_: InterruptedException) {
                    break
                } catch (e: Exception) {
                    SJavaPlugin.plugin.logger.warning("$prefix: Error in queue")
                    e.printStackTrace()
                }
            }
        }
    }

    fun addTask(task: () -> Unit) {
        queue.add(task)
    }

    fun shutdown() {
        executor.shutdown()
    }
}