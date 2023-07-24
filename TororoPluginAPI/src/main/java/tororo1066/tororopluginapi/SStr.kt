package tororo1066.tororopluginapi

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

class SStr: Cloneable {
    private val componentBuilder = Component.text()
    private val md5ComponentBuilder = ComponentBuilder()
    private val disableOptions = ArrayList<DisableOption>()

    constructor(text: String){
        this.componentBuilder.append(Component.text(text.replace("&","§")))
        this.md5ComponentBuilder.append(text.replace("&","§"))
    }

    constructor(text: String, vararg disableOptions: DisableOption){
        this.disableOptions.addAll(disableOptions)
        if (disableOptions.contains(DisableOption.COLOR_CODE)){
            this.componentBuilder.append(Component.text(text))
            this.md5ComponentBuilder.append(text)
            return
        }
        this.componentBuilder.append(Component.text(text.replace("&","§")))
        this.md5ComponentBuilder.append(text.replace("&","§"))
    }

    constructor(vararg disableOptions: DisableOption){
        this.disableOptions.addAll(disableOptions)
    }

    private constructor(component: Component){
        componentBuilder.append(component)
        md5ComponentBuilder.append(PlainTextComponentSerializer.plainText().serialize(component))
    }

    private constructor(sStr: SStr){
        componentBuilder.append(sStr.componentBuilder)
        md5ComponentBuilder.append(sStr.md5ComponentBuilder.create())
        disableOptions.addAll(sStr.disableOptions)
    }

    fun append(any: Any): SStr {
        if (any is SStr){
            this.componentBuilder.append(any.componentBuilder)
            this.md5ComponentBuilder.append(any.md5ComponentBuilder.create())
            return this
        }
        if (disableOptions.contains(DisableOption.COLOR_CODE)) {
            this.componentBuilder.append(Component.text(any.toString()))
            this.md5ComponentBuilder.append(any.toString())
            return this
        }
        this.componentBuilder.append(Component.text(any.toString().replace("&", "§")))
        this.md5ComponentBuilder.append(any.toString().replace("&", "§"))
        return this
    }

    /**
     * Only Supported Paper
     */
    fun appendTrans(key: String): SStr {
        this.componentBuilder.append(Component.translatable(key))
        return this
    }


    operator fun plus(any: Any): SStr {
        return SStr(this).append(any)
    }

    operator fun plusAssign(any: Any) {
        append(any)
    }

    fun toInt(): Int?{
        return toString().replace(",","").toIntOrNull()
    }

    override fun toString(): String {
        return PlainTextComponentSerializer.plainText().serialize(componentBuilder.build())
    }

    fun toPaperComponent(): TextComponent {
        checkPaper()
        return componentBuilder.build()
    }

    fun toBukkitComponent(): Array<out BaseComponent> {
        return md5ComponentBuilder.create()
    }

    fun hoverText(text: String): SStr {
        componentBuilder.hoverEvent(HoverEvent.showText(Component.text(text)))
        md5ComponentBuilder.event(net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,Text(text)))
        return this
    }

    fun commandText(command: String): SStr {
        return clickText(ClickEvent.Action.RUN_COMMAND, command)
    }

    fun suggestText(command: String): SStr {
        return clickText(ClickEvent.Action.SUGGEST_COMMAND,command)
    }

    /**
     * Only Supported Paper
     * @since 1.17.1
     */
    fun clickText(action: ClickEvent.Action, actionString: String): SStr {
        componentBuilder.clickEvent(ClickEvent.clickEvent(action,actionString))
        md5ComponentBuilder.event(net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(action.name),actionString))
        return this
    }

    fun clickText(action: net.md_5.bungee.api.chat.ClickEvent.Action, actionString: String): SStr {
        md5ComponentBuilder.event(net.md_5.bungee.api.chat.ClickEvent(action,actionString))
        return this
    }

    /**
     * Only Supported Paper
     * @since 1.17.1
     */
    fun showItem(item: ItemStack){
        checkPaper()
        componentBuilder.hoverEvent(item)
    }
    fun sendMessage(commandSender: CommandSender){
        if (isPaper()){
            commandSender.sendMessage(toPaperComponent())
        } else {
            commandSender.sendMessage(*toBukkitComponent())
        }
    }

    fun broadcast(){
        if (isPaper()){
            Bukkit.broadcast(toPaperComponent(), Server.BROADCAST_CHANNEL_USERS)
        } else {
            Bukkit.broadcast(*toBukkitComponent())
        }
    }



    enum class DisableOption {
        COLOR_CODE,
    }

    public override fun clone(): SStr {
        return super.clone() as SStr
    }

    private fun isPaper(): Boolean {
        var isPaper = false
        try {
            Class.forName("io.papermc.paper.text.PaperComponents")
            isPaper = true
        } catch (_: ClassNotFoundException) {
        }

        return isPaper
    }

    private fun checkPaper() {
        if (!isPaper()) throw UnsupportedOperationException("This function is available only Paper.")
    }

    companion object{

        fun Component.toSStr(): SStr {
            return SStr(this)
        }
        fun Any.toSStr(): SStr{
            return SStr(this.toString())
        }
    }
}

