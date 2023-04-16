package tororo1066.tororopluginapi.sCommand

import java.util.function.Function

class SCommandArg() {

    val alias = ArrayList<String>()
    val allowString = ArrayList<String>()

    val allowType = ArrayList<SCommandArgType>()

    val changeableAllowString = ArrayList<ChangeableAllowString>()
    val changeableAlias = ArrayList<ChangeableAlias>()

    constructor(allowString: String) : this() {
        addAllowString(allowString)
    }

    constructor(allowString: Collection<String>) : this() {
        addAllowString(allowString)
    }

    constructor(allowString: Array<out String>) : this() {
        addAllowString(allowString)
    }

    constructor(allowType: SCommandArgType) : this() {
        addAllowType(allowType)
    }

    fun addAllowString(string: String): SCommandArg {
        this.allowString.add(string)
        addAlias(string)
        return this
    }


    fun addAllowString(vararg string: String): SCommandArg {
        this.allowString.addAll(string)
        addAlias(string)
        return this
    }
    @JvmName("addAllowString1")
    fun addAllowString(string: Array<out String>): SCommandArg {
        this.allowString.addAll(string)
        addAlias(string)
        return this
    }

    fun addAllowString(string: Collection<String>): SCommandArg {
        this.allowString.addAll(string)
        addAlias(string)
        return this
    }

    fun addAlias(alias: String): SCommandArg {
        this.alias.add(alias)
        return this
    }

    fun addAlias(vararg alias: String): SCommandArg {
        this.alias.addAll(alias)
        return this
    }

    @JvmName("addAlias1")
    fun addAlias(alias: Array<out String>): SCommandArg {
        this.alias.addAll(alias)
        return this
    }

    fun addAlias(alias: Collection<String>): SCommandArg {
        this.alias.addAll(alias)
        return this
    }

    fun addAllowType(type: SCommandArgType): SCommandArg {
        this.allowType.add(type)
        return this
    }

    fun addChangeableAllowString(changeableAllowString: ChangeableAllowString): SCommandArg {
        this.changeableAllowString.add(changeableAllowString)
        return this
    }

    fun addChangeableAlias(changeableAlias: ChangeableAlias): SCommandArg {
        this.changeableAlias.add(changeableAlias)
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

    fun matches(arg : String, data: SCommandData): Boolean {
        if (!allMatch(arg))return false
        if (allowString.isEmpty() && changeableAllowString.isEmpty())return true
        for (string in allowString){
            if (string.equals(arg,true))return true
        }
        for (changeable in changeableAllowString){
            if (changeable.getAllowString(data).contains(arg))return true
        }
        return false
    }

}