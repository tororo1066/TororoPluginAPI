package tororo1066.tororopluginapi

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

class SQueue(val prefix: String = "Undefined") {

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
                    queue.take().invoke()
                } catch (e: InterruptedException) {
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