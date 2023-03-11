package tororo1066.tororopluginapi.mysql.ultimate

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
        fun modifySQLString(variable: USQLVariable.Type, value: Any): String{
            return when(variable){
                USQLVariable.Type.BOOLEAN -> if (value as Boolean) "1" else "0"
                USQLVariable.Type.DATE,USQLVariable.Type.DATETIME,USQLVariable.Type.TIME,USQLVariable.Type.YEAR -> dateModify(value,variable)
                USQLVariable.Type.CHAR,USQLVariable.Type.TINYTEXT,USQLVariable.Type.TEXT,USQLVariable.Type.MEDIUMTEXT,USQLVariable.Type.LONGTEXT,USQLVariable.Type.JSON,USQLVariable.Type.VARCHAR ->{
                    if (value.toString() == "now()") value.toString() else "'$value'"
                }
                else -> value.toString()
            }
        }

        private fun dateModify(value: Any, type: USQLVariable.Type): String{
            if (value is String){
                if (value == "now()") return value
            }
            if (value !is Date) return value.toString()
            return when(type){
                USQLVariable.Type.DATE -> SimpleDateFormat("yyyy-MM-dd").format(value)
                USQLVariable.Type.DATETIME -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value)
                USQLVariable.Type.TIME -> SimpleDateFormat("HH:mm:ss").format(value)
                USQLVariable.Type.YEAR -> SimpleDateFormat("yyyy").format(value)
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