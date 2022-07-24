package tororo1066.tororopluginapi.sCommand

class SCommandArg {

    val alias = ArrayList<String>()
    val allowString = ArrayList<String>()
    val argsAllowString = ArrayList<java.util.function.Function<List<String>,List<String>>>()

    val allowType = ArrayList<SCommandArgType>()

    fun addAllowString(string: String): SCommandArg {
        this.allowString.add(string)
        addAlias(string)
        return this
    }

    fun addArgsAllowString(func: (List<String>) -> List<String>): SCommandArg {
        this.argsAllowString.add(func)
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

    fun addAlias(alias : String): SCommandArg {
        this.alias.add(alias)
        return this
    }

    fun addAlias(vararg alias : String): SCommandArg {
        this.alias.addAll(alias)
        return this
    }

    @JvmName("addAlias1")
    fun addAlias(alias : Array<out String>): SCommandArg {
        this.alias.addAll(alias)
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

    fun matches(list: List<String>, arg : String): Boolean {
        if (!allMatch(arg))return false
        if (allowString.isEmpty())return true
        for (string in arg){
            argsAllowString.forEach {
                if (it.apply(list).contains(arg))return true
            }
        }
        for (string in allowString){
            if (string.equals(arg,true))return true
        }
        return false
    }

}