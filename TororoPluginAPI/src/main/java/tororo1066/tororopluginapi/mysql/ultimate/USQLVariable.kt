package tororo1066.tororopluginapi.mysql.ultimate

import tororo1066.tororopluginapi.mysql.SMySQLResultSet
import java.time.LocalDateTime

@Suppress("UNUSED")
class USQLVariable<V: USQLVariable.VariableType<*>>(val type: V) {

    companion object{
        val BOOLEAN get() = BOOLEAN()
        val TINYINT get() = TINYINT()
        val SMALLINT get() = SMALLINT()
        val MEDIUMINT get() = MEDIUMINT()
        val INT get() = INT()
        val INTEGER get() = INTEGER()
        val BIGINT get() = BIGINT()
        val FLOAT get() = FLOAT()
        val DOUBLE get() = DOUBLE()
        val DECIMAL get() = DECIMAL()
        val DATE get() = DATE()
        val DATETIME get() = DATETIME()
        val TIMESTAMP get() = TIMESTAMP()
        val TIME get() = TIME()
        val YEAR get() = YEAR()
        val CHAR get() = CHAR()
        val VARCHAR get() = VARCHAR()
        val TINYTEXT get() = TINYTEXT()
        val TEXT get() = TEXT()
        val MEDIUMTEXT get() = MEDIUMTEXT()
        val LONGTEXT get() = LONGTEXT()
        val BINARY get() = BINARY()
        val VARBINARY get() = VARBINARY()
        val TINYBLOB get() = TINYBLOB()
        val MEDIUMBLOB get() = MEDIUMBLOB()
        val BLOB get() = BLOB()
        val LONGBLOB get() = LONGBLOB()
    }

    constructor(type: V,length: Int): this(type){
        this.length = length
    }

    constructor(type: V,nullable: Boolean,index: Index? = null): this(type){
        this.nullable = nullable
        this.index = index
    }

    constructor(type: V,default: Any?,index: Index? = null): this(type){
        this.default = default
        this.index = index
    }

    constructor(type: V,length: Int,default: Any,index: Index? = null): this(type){
        this.length = length
        this.default = default
        this.index = index
    }

    constructor(type: V,length: Int,nullable: Boolean,index: Index? = null): this(type){
        this.length = length
        this.nullable = nullable
        this.index = index
    }

    constructor(type: V,length: Int,autoIncrement: Boolean): this(type){
        this.length = length
        this.autoIncrement = autoIncrement
        this.index = Index.PRIMARY
    }

    constructor(type: V,autoIncrement: Boolean): this(type){
        this.autoIncrement = autoIncrement
        this.index = Index.PRIMARY
    }

    constructor(type: V,index: Index,autoIncrement: Boolean): this(type){
        this.autoIncrement = autoIncrement
        this.index = index
    }

    constructor(type: V,length: Int,index: Index,autoIncrement: Boolean): this(type){
        this.length = length
        this.autoIncrement = autoIncrement
        this.index = index
    }

    var name = ""
    var nullable = false
    var default: Any? = null
    var index: Index? = null

    var autoIncrement = false

    var length = -1

    enum class Index(val tableString: String,val usingBTREE: Boolean){
        PRIMARY("primary key",true),
        KEY("index",true),
        UNIQUE("unique index",false),
        FULLTEXT("fulltext index",false),
        SPATIAL("spatial index",false)
    }

    fun equal(value: Any): USQLCondition {
        return USQLCondition().equal(this,value)
    }

    fun orHigher(value: Any): USQLCondition {
        return USQLCondition().orHigher(this,value)
    }

    fun orLower(value: Any): USQLCondition {
        return USQLCondition().orLower(this,value)
    }

    fun moreThan(value: Any): USQLCondition {
        return USQLCondition().moreThan(this,value)
    }

    fun lessThan(value: Any): USQLCondition {
        return USQLCondition().lessThan(this,value)
    }

    abstract class VariableType<V>{

        abstract val variableName: String
        var name: String = ""
        abstract fun getNullableVal(sqlResultSet: SMySQLResultSet): V?

        fun getVal(sqlResultSet: SMySQLResultSet): V {
            return getNullableVal(sqlResultSet)!!
        }

    }

    class BOOLEAN: VariableType<Boolean>(){

        override val variableName = "boolean"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Boolean? {
            return sqlResultSet.getNullableBoolean(name)
        }
    }

    class TINYINT: VariableType<Int>(){

        override val variableName = "tinyint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    class SMALLINT: VariableType<Int>(){

        override val variableName = "smallint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    class MEDIUMINT: VariableType<Int>(){

        override val variableName = "mediumint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    class INT: VariableType<Int>(){

        override val variableName = "int"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    class INTEGER: VariableType<Int>(){

        override val variableName = "integer"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    class BIGINT: VariableType<Long>(){

        override val variableName = "bigint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Long? {
            return sqlResultSet.getNullableLong(name)
        }
    }

    class BIT: VariableType<Int>(){

        override val variableName = "bit"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    class FLOAT: VariableType<Float>(){

        override val variableName = "float"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Float? {
            return sqlResultSet.getNullableFloat(name)
        }
    }

    class DOUBLE: VariableType<Double>(){

        override val variableName = "double"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Double? {
            return sqlResultSet.getNullableDouble(name)
        }
    }

    class DECIMAL: VariableType<Double>(){

        override val variableName = "decimal"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Double? {
            return sqlResultSet.getNullableDouble(name)
        }
    }

    class DATE: VariableType<LocalDateTime>(){

        override val variableName = "date"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    class DATETIME: VariableType<LocalDateTime>(){

        override val variableName = "datetime"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    class TIMESTAMP: VariableType<LocalDateTime>(){

        override val variableName = "timestamp"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    class TIME: VariableType<LocalDateTime>(){

        override val variableName = "time"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    class YEAR: VariableType<LocalDateTime>(){

        override val variableName = "year"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    class CHAR: VariableType<String>(){

        override val variableName = "char"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    class VARCHAR: VariableType<String>(){

        override val variableName = "varchar"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    class TINYTEXT: VariableType<String>(){

        override val variableName = "tinytext"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    class TEXT: VariableType<String>(){

        override val variableName = "text"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    class MEDIUMTEXT: VariableType<String>(){

        override val variableName = "mediumtext"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    class LONGTEXT: VariableType<String>(){

        override val variableName = "longtext"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    class JSON: VariableType<String>(){

        override val variableName = "json"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    class BINARY: VariableType<ByteArray>(){

        override val variableName = "binary"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    class VARBINARY: VariableType<ByteArray>(){

        override val variableName = "varbinary"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    class TINYBLOB: VariableType<ByteArray>(){

        override val variableName = "tinyblob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    class MEDIUMBLOB: VariableType<ByteArray>(){

        override val variableName = "mediumblob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    class BLOB: VariableType<ByteArray>(){

        override val variableName = "blob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    class LONGBLOB: VariableType<ByteArray>(){

        override val variableName = "longblob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }
}