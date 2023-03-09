package tororo1066.tororopluginapi.mysql.ultimate

import java.text.SimpleDateFormat
import java.util.*

class USQLCondition {

    private val builder = StringBuilder("where ")

    fun equal(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.type.columnName} = ${modifySQLString(variable.type,value)}")
        return this
    }

    fun orHigher(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.type.columnName} >= ${modifySQLString(variable.type,value)}")
        return this
    }

    fun orLower(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.type.columnName} <= ${modifySQLString(variable.type,value)}")
        return this
    }

    fun moreThan(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.type.columnName} > ${modifySQLString(variable.type,value)}")
        return this
    }

    fun lessThan(variable: USQLVariable<*>, value: Any): USQLCondition{
        builder.append("${variable.type.columnName} < ${modifySQLString(variable.type,value)}")
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
        return builder.toString()
    }

    override fun toString(): String {
        return builder.toString()
    }

    companion object{
        fun modifySQLString(variable: USQLVariable.Type, value: Any): String{
            return when(variable){
                USQLVariable.Type.BOOLEAN -> if (value as Boolean) "1" else "0"
                USQLVariable.Type.DATE -> SimpleDateFormat("yyyy-MM-dd").format(value as Date)
                USQLVariable.Type.DATETIME -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value as Date)
                USQLVariable.Type.TIME -> SimpleDateFormat("HH:mm:ss").format(value as Date)
                USQLVariable.Type.YEAR -> SimpleDateFormat("yyyy").format(value as Date)
                USQLVariable.Type.CHAR,USQLVariable.Type.TINYTEXT,USQLVariable.Type.TEXT,USQLVariable.Type.MEDIUMTEXT,USQLVariable.Type.LONGTEXT,USQLVariable.Type.JSON,USQLVariable.Type.VARCHAR ->{
                    if (value.toString() == "now()") value.toString() else "'$value'"
                }
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