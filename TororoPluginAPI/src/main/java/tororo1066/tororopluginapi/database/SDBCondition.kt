package tororo1066.tororopluginapi.database

import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import org.bukkit.Bukkit
import java.text.SimpleDateFormat
import java.util.*

class SDBCondition {

    private val builder = StringBuilder("where ")
    private var filter = Filters.empty()
    private var orMode = false
    private var andMode = false
    private var notMode = false

    fun equal(variable: String, value: Any, type: SDBVariable.VariableType<*>? = null): SDBCondition {
        append(Filters.eq(variable, value))
        builder.append("$variable = ${modifySQLString(type?: SDBVariable.Text,value)}")
        return this
    }

    fun orHigher(variable: String, value: Any, type: SDBVariable.VariableType<*>? = null): SDBCondition {
        append(Filters.gte(variable, value))
        builder.append("$variable >= ${modifySQLString(type?: SDBVariable.Text,value)}")
        return this
    }

    fun orLower(variable: String, value: Any, type: SDBVariable.VariableType<*>? = null): SDBCondition {
        append(Filters.lte(variable, value))
        builder.append("$variable <= ${modifySQLString(type?: SDBVariable.Text,value)}")
        return this
    }

    fun moreThan(variable: String, value: Any, type: SDBVariable.VariableType<*>? = null): SDBCondition {
        append(Filters.gt(variable, value))
        builder.append("$variable > ${modifySQLString(type?: SDBVariable.Text,value)}")
        return this
    }

    fun lessThan(variable: String, value: Any, type: SDBVariable.VariableType<*>? = null): SDBCondition {
        append(Filters.lt(variable, value))
        builder.append("$variable < ${modifySQLString(type?: SDBVariable.Text,value)}")
        return this
    }

    fun like(variable: String, value: Any, type: SDBVariable.VariableType<*>? = null): SDBCondition {
        append(Filters.regex(variable, value.toString()))
        builder.append("$variable like ${modifySQLString(type?: SDBVariable.Text,value)}")
        return this
    }

    private fun append(filter: Bson): SDBCondition {

        val modifiedFilter = if (notMode) Filters.not(filter) else filter
        when {
            andMode -> {
                this.filter = Filters.and(this.filter, modifiedFilter)
            }
            orMode -> {
                this.filter = Filters.or(this.filter, modifiedFilter)
            }

            else -> {
                this.filter = modifiedFilter
            }
        }
        notMode = false
        andMode = false
        orMode = false

        return this
    }

    fun and(): SDBCondition {
        andMode = true
        orMode = false
        builder.append(" and ")
        return this
    }

    fun or(): SDBCondition {
        orMode = true
        andMode = false
        builder.append(" or ")
        return this
    }

    fun not(): SDBCondition {
        notMode = true
        builder.append(" not ")
        return this
    }

    fun build(): String {
        return builder.toString().replace("  "," ")
    }

    fun buildAsMongo(): Bson {
        return filter
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

        fun empty(): SDBCondition {
            val sDBCondition = SDBCondition()
            sDBCondition.builder.clear()
            return sDBCondition
        }
    }
}