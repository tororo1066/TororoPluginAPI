package tororo1066.tororopluginapi.sCommand

import org.bukkit.Bukkit
import org.bukkit.command.CommandMap

class SCommandArg {

    val alias = ArrayList<String>()
    val allowString = ArrayList<String>()

    val allowType = ArrayList<SCommandArgType>()

    fun addAllowString(string: String): SCommandArg {
        this.allowString.add(string)
        addAlias(string)
        return this
    }

    fun addAlias(alias : String): SCommandArg {
        this.alias.add(alias)
        return this
    }

    fun addAllowType(type: SCommandArgType): SCommandArg {
        this.allowType.add(type)
        return this
    }

    fun hasType(type : SCommandArgType): Boolean {
        return this.allowType.contains(type)
    }

    fun allMatch(arg : String): Boolean {
        for (allow in allowType){
            if (!allow.match(arg))return false
        }
        return true
    }

    fun matches(arg : String): Boolean {
        if (!allMatch(arg))return false
        if (allowString.isEmpty())return true
        for (string in allowString){
            if (string.equals(arg,true))return true
        }
        return false
    }

}