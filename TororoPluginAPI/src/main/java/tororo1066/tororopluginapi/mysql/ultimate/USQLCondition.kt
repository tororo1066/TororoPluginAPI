package tororo1066.tororopluginapi.mysql.ultimate

import org.bukkit.Bukkit
import java.text.SimpleDateFormat
import java.util.*

class USQLCondition {

    private val builder = StringBuilder("where ")

    fun equal(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.name} = ${modifySQLString(variable.type,value)}")
        return this
    }

    fun orHigher(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.name} >= ${modifySQLString(variable.type,value)}")
        return this
    }

    fun orLower(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.name} <= ${modifySQLString(variable.type,value)}")
        return this
    }

    fun moreThan(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.name} > ${modifySQLString(variable.type,value)}")
        return this
    }

    fun lessThan(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.name} < ${modifySQLString(variable.type,value)}")
        return this
    }

    fun and(): USQLCondition{
        builder.append(" and ")
        return this
    }

    fun or(): USQLCondition{
        builder.append(" or ")
        return this
    }

    fun not(): USQLCondition{
        builder.append(" not ")
        return this
    }

    fun build(): String{
        return builder.toString().replace("  "," ")
    }

    override fun toString(): String {
        return build()
    }

    companion object{
        fun modifySQLString(variable: USQLVariable.VariableType<*>, value: Any): String{
            return when(variable.javaClass){
                USQLVariable.BOOLEAN::class.java -> if (value as Boolean) "1" else "0"
                USQLVariable.DATE::class.java,USQLVariable.DATETIME::class.java,
                USQLVariable.TIME::class.java,USQLVariable.YEAR::class.java -> {
                    if (value.toString() == "now()") value.toString() else dateModify(value,variable)
                }
                else -> "'$value'"
            }
        }

        private fun dateModify(value: Any, type: USQLVariable.VariableType<*>): String{
            if (value is String){
                if (value == "now()") return value
            }
            if (value !is Date) return value.toString()
            return when(type.javaClass){
                USQLVariable.DATE::class.java -> SimpleDateFormat("yyyy-MM-dd").format(value)
                USQLVariable.DATETIME::class.java -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value)
                USQLVariable.TIME::class.java -> SimpleDateFormat("HH:mm:ss").format(value)
                USQLVariable.YEAR::class.java -> SimpleDateFormat("yyyy").format(value)
                else -> value.toString()
            }
        }

        fun empty(): USQLCondition{
            val uSqlCondition = USQLCondition()
            uSqlCondition.builder.clear()
            return uSqlCondition
        }
    }
}