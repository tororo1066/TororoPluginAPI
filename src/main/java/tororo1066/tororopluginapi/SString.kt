package tororo1066.tororopluginapi

import org.bukkit.ChatColor

class SString {
    private var text = ""
    private val disableOptions = ArrayList<DisableOption>()

    constructor(text: String){
        this.text = text
    }

    constructor(text: String, vararg disableOptions: DisableOption){
        this.text = text
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

    enum class DisableOption {
        COLOR_CODE,
    }
}

