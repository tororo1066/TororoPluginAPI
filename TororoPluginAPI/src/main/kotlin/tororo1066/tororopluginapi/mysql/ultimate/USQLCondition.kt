package tororo1066.tororopluginapi.mysql.ultimate

import tororo1066.tororopluginapi.database.SDBVariable
import java.text.SimpleDateFormat
import java.util.*

class USQLCondition {

    private val builder = StringBuilder("where ")

    fun equal(variable: SDBVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.name} = ${modifySQLString(variable.type,value)}")
        return this
    }

    fun orHigher(variable: SDBVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.name} >= ${modifySQLString(variable.type,value)}")
        return this
    }

    fun orLower(variable: SDBVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.name} <= ${modifySQLString(variable.type,value)}")
        return this
    }

    fun moreThan(variable: SDBVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.name} > ${modifySQLString(variable.type,value)}")
        return this
    }

    fun lessThan(variable: SDBVariable<*>, value: Any): USQLCondition{
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
        fun modifySQLString(variable: SDBVariable.VariableType<*>, value: Any): String {
            return when(variable.javaClass){
                SDBVariable.DATE::class.java, SDBVariable.DATETIME::class.java,
                SDBVariable.TIME::class.java, SDBVariable.YEAR::class.java -> {
                    if (value.toString() == "now()") value.toString() else dateModify(value,variable)
                }
                else -> "'$value'"
            }
        }

        private fun dateModify(value: Any, type: SDBVariable.VariableType<*>): String {
            if (value is String){
                if (value == "now()") return value
            }
            if (value !is Date) return value.toString()
            return when(type.javaClass){
                SDBVariable.DATE::class.java -> SimpleDateFormat("yyyy-MM-dd").format(value)
                SDBVariable.DATETIME::class.java -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value)
                SDBVariable.TIME::class.java -> SimpleDateFormat("HH:mm:ss").format(value)
                SDBVariable.YEAR::class.java -> SimpleDateFormat("yyyy").format(value)
                else -> value.toString()
            }
        }

        fun empty(): USQLCondition {
            val uSqlCondition = USQLCondition()
            uSqlCondition.builder.clear()
            return uSqlCondition
        }
    }
}