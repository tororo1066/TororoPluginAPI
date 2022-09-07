package tororo1066.tororopluginapi

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor

class SStr {
    private val componentBuilder = Component.text("").toBuilder()
    private val disableOptions = ArrayList<DisableOption>()

    constructor(text: String){
        this.componentBuilder.append(Component.text(text.replace("&","§")))
    }

    constructor(text: String, vararg disableOptions: DisableOption){
        this.disableOptions.addAll(disableOptions)
        if (disableOptions.contains(DisableOption.COLOR_CODE)){
            this.componentBuilder.append(Component.text(text))
            return
        }
        this.componentBuilder.append(Component.text(text.replace("&","§")))
    }

    constructor(vararg disableOptions: DisableOption){
        this.disableOptions.addAll(disableOptions)
    }

    private constructor(component: Component){
        componentBuilder.append(component)
    }

    fun append(any: Any): SStr {
        if (disableOptions.contains(DisableOption.COLOR_CODE)) {
            this.componentBuilder.append(Component.text(any.toString()))
            return this
        }
        this.componentBuilder.append(Component.text(any.toString().replace("&", "§")))
        return this
    }

    fun appendTrans(key: String): SStr {
        this.componentBuilder.append(Component.translatable(key))
        return this
    }


    operator fun plus(any: Any): SStr {
        return append(any)
    }

    operator fun plusAssign(any: Any) {
        append(any)
    }

    fun toInt(): Int?{
        return ChatColor.stripColor(PlainTextComponentSerializer.plainText().serialize(componentBuilder.build()))!!.replace(",","").toIntOrNull()
    }

    override fun toString(): String {
        return PlainTextComponentSerializer.plainText().serialize(componentBuilder.build())
    }

    fun toTextComponent(): TextComponent {
        return componentBuilder.build()
    }

    fun toBaseComponent(): BaseComponent {
        return net.md_5.bungee.api.chat.TextComponent(PlainTextComponentSerializer.plainText().serialize(componentBuilder.build()))
    }

    fun hoverText(text: String): SStr {
        componentBuilder.hoverEvent(HoverEvent.showText(Component.text(text)))
        return this
    }

    fun commandText(command: String): SStr {
        return clickText(ClickEvent.Action.RUN_COMMAND, command)
    }

    fun suggestText(command: String): SStr {
        return clickText(ClickEvent.Action.SUGGEST_COMMAND,command)
    }

    fun clickText(action: ClickEvent.Action, actionString: String): SStr {
        componentBuilder.clickEvent(ClickEvent.clickEvent(action,actionString))
        return this
    }

    enum class DisableOption {
        COLOR_CODE,
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

