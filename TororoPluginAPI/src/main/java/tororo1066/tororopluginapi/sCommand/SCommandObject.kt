package tororo1066.tororopluginapi.sCommand

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.function.Consumer

class SCommandObject {

    enum class Mode{
        ALL,
        PLAYER
    }

    val args = ArrayList<SCommandArg>()
    private var mode = Mode.ALL
    private val executors = ArrayList<CommandExecutor>()
    private val consumerExecutors = ArrayList<Consumer<SCommandData>>()
    private val onlyPlayerExecutors = ArrayList<OnlyPlayerExecutor>()
    private val onlyPlayerConsumerExecutors = ArrayList<Consumer<SCommandOnlyPlayerData>>()
    private var noLimit = false

    private val permission = ArrayList<String>()


    fun setMode(mode : Mode): SCommandObject {
        this.mode = mode
        return this
    }

    fun noLimit(boolean: Boolean): SCommandObject {
        this.noLimit = boolean
        return this
    }

    fun addArg(arg : SCommandArg): SCommandObject {
        this.args.add(arg)
        return this
    }

    fun setExecutor(executor: CommandExecutor): SCommandObject {
        if (executor is OnlyPlayerExecutor){
            setMode(Mode.PLAYER)
            this.onlyPlayerExecutors.add(executor)
            return this
        }
        this.executors.add(executor)
        return this
    }
    @Deprecated("Use setNormalExecutor",ReplaceWith("setNormalExecutor(executor)"))
    fun setExecutor(executor: Consumer<SCommandData>): SCommandObject {
        this.consumerExecutors.add(executor)
        return this
    }

    @JvmName("setExecutor1")
    @Deprecated("Use setPlayerExecutor",ReplaceWith("setPlayerExecutor(executor)"))
    fun setExecutor(executor: Consumer<SCommandOnlyPlayerData>): SCommandObject {
        setMode(Mode.PLAYER)
        this.onlyPlayerConsumerExecutors.add(executor)
        return this
    }

    fun setNormalExecutor(executor: Consumer<SCommandData>): SCommandObject {
        this.consumerExecutors.add(executor)
        return this
    }

    fun setNormalFunction(executor: (sender: CommandSender, command: Command, label: String, args: Array<out String>)->Unit): SCommandObject {
        this.consumerExecutors.add(Consumer { executor.invoke(it.sender, it.command, it.label, it.args) })
        return this
    }

    fun setPlayerExecutor(executor: Consumer<SCommandOnlyPlayerData>): SCommandObject {
        setMode(Mode.PLAYER)
        this.onlyPlayerConsumerExecutors.add(executor)
        return this
    }

    fun setPlayerFunction(executor: (sender: Player, command: Command, label: String, args: Array<out String>)->Unit): SCommandObject {
        setMode(Mode.PLAYER)
        this.onlyPlayerConsumerExecutors.add(Consumer { executor.invoke(it.sender, it.command, it.label, it.args) })
        return this
    }

    fun addNeedPermission(perm : String): SCommandObject {
        this.permission.add(perm)
        return this
    }

    fun hasPermission(p : CommandSender): Boolean {
        for (perm in permission){
            if (!p.hasPermission(perm))return false
        }
        return true
    }

    fun matches(data: SCommandData): Boolean {
        val args = data.args
        if (args.size < this.args.size) return false
        if (args.size > this.args.size && !noLimit) return false
        for (i in args.indices){
            if (this.args.size-1 <= i && noLimit)continue

            if (!this.args[i].matches(args[i],data))return false
        }
        return true
    }

    fun validOption(data: SCommandData): Boolean {
        val args = data.args
        if (args.size > this.args.size) return false
        for (i in 0 until args.size - 1) {
            if (!this.args[i].matches(args[i],data)) {
                return false
            }
        }
        return true
    }

    fun execute(data : SCommandData) : Boolean {
        if (this.mode == Mode.PLAYER){
            if (data.sender !is Player)return false

            for (executor in this.onlyPlayerExecutors){
                executor.onCommand(data.sender,data.command,data.label,data.args)
            }

            for (executor in this.onlyPlayerConsumerExecutors){
                executor.accept(SCommandOnlyPlayerData(data.sender as Player,data.command,data.label,data.args))
            }
        }
        for (executor in this.executors){
            executor.onCommand(data.sender,data.command,data.label,data.args)
        }
        for (executor in this.consumerExecutors){
            executor.accept(data)
        }
        return true
    }




}