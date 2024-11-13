package tororo1066.tororopluginapi

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.kyori.adventure.title.Title
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.ItemTag
import net.md_5.bungee.api.chat.TranslatableComponent
import net.md_5.bungee.api.chat.hover.content.Item
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Duration

@Suppress("UNUSED", "DEPRECATION")
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
        md5ComponentBuilder.append(BungeeComponentSerializer.get().serialize(component))
    }

    private constructor(component: Array<out BaseComponent>){
        componentBuilder.append(BungeeComponentSerializer.get().deserialize(component))
        md5ComponentBuilder.append(component)
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

    fun appendTrans(key: String, vararg variable: Any): SStr {
        val toComponent = variable.map { Component.text(it.toString()) }
        this.componentBuilder.append(Component.translatable(key, toComponent))
        this.md5ComponentBuilder.append(TranslatableComponent(key, *variable))
        return this
    }

    fun font(font: String): SStr {
        this.componentBuilder.font(Key.key(font))
        this.md5ComponentBuilder.font(font)
        return this
    }


    operator fun plus(any: Any): SStr {
        return SStr(this).append(any)
    }

    operator fun plusAssign(any: Any) {
        append(any)
    }

    fun toInt(): Int? {
        return toString().replace(",","").toIntOrNull()
    }

    override fun toString(): String {
        return if (isPaper()){
            PlainTextComponentSerializer.plainText().serialize(componentBuilder.build())
        } else {
            md5ComponentBuilder.create().first().toPlainText()
        }
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
        return clickText(ClickAction.RUN_COMMAND, command)
    }

    fun suggestText(command: String): SStr {
        return clickText(ClickAction.SUGGEST_COMMAND,command)
    }

    fun clickText(action: ClickAction, actionString: String): SStr {
        componentBuilder.clickEvent(ClickEvent.clickEvent(action.getPaperAction(),actionString))
        md5ComponentBuilder.event(net.md_5.bungee.api.chat.ClickEvent(action.getBukkitAction(),actionString))
        return this
    }

    /**
     * Only Supported Paper
     */
    fun showItem(item: ItemStack){
        checkPaper()
        componentBuilder.hoverEvent(item)
        md5ComponentBuilder.event(net.md_5.bungee.api.chat.HoverEvent(
                net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_ITEM, Bukkit.getItemFactory().hoverContentOf(item))
        ) // Only supported Paper because Bukkit.getItemFactory().hoverContentOf(item) is Paper API function.
    }
    fun sendMessage(commandSender: CommandSender){
        if (isPaper()){
            commandSender.sendMessage(toPaperComponent())
        } else {
            commandSender.sendMessage(*toBukkitComponent())
        }
    }

    fun actionBar(player: Player){
        if (isPaper()){
            player.sendActionBar(toPaperComponent())
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, *toBukkitComponent())
        }
    }

    fun broadcast(){
        if (isPaper()){
            Bukkit.broadcast(toPaperComponent(), Server.BROADCAST_CHANNEL_USERS)
        } else {
            Bukkit.broadcast(*toBukkitComponent())
        }
    }

    fun showTitle(player: Player, subtitle: SStr? = null, fadeIn: Int = 10, stay: Int = 70, fadeOut: Int = 20){
        if (isPaper()){
            player.showTitle(
                Title.title(
                    toPaperComponent(),
                    subtitle?.toPaperComponent()?:Component.empty(),
                    Title.Times.times(Duration.ofMillis(fadeIn * 50L), Duration.ofMillis(stay * 50L), Duration.ofMillis(fadeOut * 50L))
                )
            )
        } else {
            player.showTitle(
                toBukkitComponent(),
                subtitle?.toBukkitComponent()?:ComponentBuilder().create(),
                fadeIn * 20,
                stay * 20,
                fadeOut * 20
            )
        }
    }

    enum class DisableOption {
        COLOR_CODE,
    }

    enum class ClickAction {

        RUN_COMMAND,
        SUGGEST_COMMAND,
        OPEN_URL,
        COPY_TO_CLIPBOARD,
        CHANGE_PAGE;

        fun getBukkitAction(): net.md_5.bungee.api.chat.ClickEvent.Action {
            return net.md_5.bungee.api.chat.ClickEvent.Action.valueOf(this.name)
        }

        fun getPaperAction(): ClickEvent.Action {
            return ClickEvent.Action.valueOf(this.name)
        }
    }

    public override fun clone(): SStr {
        return super.clone() as SStr
    }

    companion object {

        fun Component.toSStr(): SStr {
            return SStr(this)
        }
        fun Any.toSStr(): SStr{
            return SStr(this.toString())
        }

        fun isPaper(): Boolean {
            var isPaper = false
            try {
                Class.forName("io.papermc.paper.text.PaperComponents")
                isPaper = true
            } catch (_: ClassNotFoundException) {
            }

            return isPaper
        }

        fun checkPaper() {
            if (!isPaper()) throw UnsupportedOperationException("This function is available only Paper.")
        }
    }
}

