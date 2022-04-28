package tororo1066.tororopluginapi

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor

class SString {
    private var text = ""
    private val disableOptions = ArrayList<DisableOption>()

    constructor(text: String){
        this.text = text.replace("&","§")
    }

    constructor(text: String, vararg disableOptions: DisableOption){
        this.disableOptions.addAll(disableOptions)
        if (disableOptions.contains(DisableOption.COLOR_CODE)){
            this.text = text
            return
        }
        this.text = text.replace("&","§")
    }

    constructor(vararg disableOptions: DisableOption){
        this.disableOptions.addAll(disableOptions)
    }

    fun append(any: Any): SString {
        if (disableOptions.contains(DisableOption.COLOR_CODE)){
            text += any.toString()
            return this
        }
        text += any.toString().replace("&","§")
        return this
    }

    fun toInt(): Int?{
        return ChatColor.stripColor(text)!!.replace(",","").toIntOrNull()
    }

    override fun toString(): String {
        return text
    }

    fun toBaseComponent(): BaseComponent {
        return TextComponent(text)
    }

    enum class DisableOption {
        COLOR_CODE,
    }
}

