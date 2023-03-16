package tororo1066.tororopluginapi.mysql.ultimate

import tororo1066.tororopluginapi.mysql.SMySQLResultSet
import java.time.LocalDateTime

@Suppress("UNUSED")
class USQLVariable<V: USQLVariable.VariableType<*>>(val type: V) {

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

    object BOOLEAN: VariableType<Boolean>(){

        override val variableName = "boolean"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Boolean? {
            return sqlResultSet.getNullableBoolean(variableName)
        }
    }

    object TINYINT: VariableType<Int>(){

        override val variableName = "tinyint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(variableName)
        }
    }

    object SMALLINT: VariableType<Int>(){

        override val variableName = "smallint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(variableName)
        }
    }

    object MEDIUMINT: VariableType<Int>(){

        override val variableName = "mediumint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(variableName)
        }
    }

    object INT: VariableType<Int>(){

        override val variableName = "int"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(variableName)
        }
    }

    object BIGINT: VariableType<Long>(){

        override val variableName = "bigint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Long? {
            return sqlResultSet.getNullableLong(variableName)
        }
    }

    object BIT: VariableType<Int>(){

        override val variableName = "bit"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(variableName)
        }
    }

    object FLOAT: VariableType<Float>(){

        override val variableName = "float"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Float? {
            return sqlResultSet.getNullableFloat(variableName)
        }
    }

    object DOUBLE: VariableType<Double>(){

        override val variableName = "double"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Double? {
            return sqlResultSet.getNullableDouble(variableName)
        }
    }

    object DECIMAL: VariableType<Double>(){

        override val variableName = "decimal"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Double? {
            return sqlResultSet.getNullableDouble(variableName)
        }
    }

    object DATE: VariableType<LocalDateTime>(){

        override val variableName = "date"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(variableName)
        }
    }

    object DATETIME: VariableType<LocalDateTime>(){

        override val variableName = "datetime"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(variableName)
        }
    }

    object TIMESTAMP: VariableType<LocalDateTime>(){

        override val variableName = "timestamp"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(variableName)
        }
    }

    object TIME: VariableType<LocalDateTime>(){

        override val variableName = "time"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(variableName)
        }
    }

    object YEAR: VariableType<LocalDateTime>(){

        override val variableName = "year"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(variableName)
        }
    }

    object CHAR: VariableType<String>(){

        override val variableName = "char"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(variableName)
        }
    }

    object VARCHAR: VariableType<String>(){

        override val variableName = "varchar"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(variableName)
        }
    }

    object TINYTEXT: VariableType<String>(){

        override val variableName = "tinytext"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(variableName)
        }
    }

    object TEXT: VariableType<String>(){

        override val variableName = "text"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(variableName)
        }
    }

    object MEDIUMTEXT: VariableType<String>(){

        override val variableName = "mediumtext"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(variableName)
        }
    }

    object LONGTEXT: VariableType<String>(){

        override val variableName = "longtext"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(variableName)
        }
    }

    object JSON: VariableType<String>(){

        override val variableName = "json"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(variableName)
        }
    }

    object BINARY: VariableType<ByteArray>(){

        override val variableName = "binary"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(variableName)
        }
    }

    object VARBINARY: VariableType<ByteArray>(){

        override val variableName = "varbinary"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(variableName)
        }
    }

    object TINYBLOB: VariableType<ByteArray>(){

        override val variableName = "tinyblob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(variableName)
        }
    }

    object MEDIUMBLOB: VariableType<ByteArray>(){

        override val variableName = "mediumblob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(variableName)
        }
    }

    object BLOB: VariableType<ByteArray>(){

        override val variableName = "blob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(variableName)
        }
    }

    object LONGBLOB: VariableType<ByteArray>(){

        override val variableName = "longblob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(variableName)
        }
    }
}