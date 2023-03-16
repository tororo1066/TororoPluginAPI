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

        abstract val name: String
        abstract fun getNullableVal(sqlResultSet: SMySQLResultSet): V?

        fun getVal(sqlResultSet: SMySQLResultSet): V {
            return getNullableVal(sqlResultSet)!!
        }

    }

    object BOOLEAN: VariableType<Boolean>(){

        override val name = "boolean"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Boolean? {
            return sqlResultSet.getNullableBoolean(name)
        }
    }

    object TINYINT: VariableType<Int>(){

        override val name = "tinyint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    object SMALLINT: VariableType<Int>(){

        override val name = "smallint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    object MEDIUMINT: VariableType<Int>(){

        override val name = "mediumint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    object INT: VariableType<Int>(){

        override val name = "int"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    object BIGINT: VariableType<Long>(){

        override val name = "bigint"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Long? {
            return sqlResultSet.getNullableLong(name)
        }
    }

    object BIT: VariableType<Int>(){

        override val name = "bit"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Int? {
            return sqlResultSet.getNullableInt(name)
        }
    }

    object FLOAT: VariableType<Float>(){

        override val name = "float"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Float? {
            return sqlResultSet.getNullableFloat(name)
        }
    }

    object DOUBLE: VariableType<Double>(){

        override val name = "double"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Double? {
            return sqlResultSet.getNullableDouble(name)
        }
    }

    object DECIMAL: VariableType<Double>(){

        override val name = "decimal"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): Double? {
            return sqlResultSet.getNullableDouble(name)
        }
    }

    object DATE: VariableType<LocalDateTime>(){

        override val name = "date"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    object DATETIME: VariableType<LocalDateTime>(){

        override val name = "datetime"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    object TIMESTAMP: VariableType<LocalDateTime>(){

        override val name = "timestamp"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    object TIME: VariableType<LocalDateTime>(){

        override val name = "time"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    object YEAR: VariableType<LocalDateTime>(){

        override val name = "year"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): LocalDateTime? {
            return sqlResultSet.getNullableDate(name)
        }
    }

    object CHAR: VariableType<String>(){

        override val name = "char"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    object VARCHAR: VariableType<String>(){

        override val name = "varchar"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    object TINYTEXT: VariableType<String>(){

        override val name = "tinytext"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    object TEXT: VariableType<String>(){

        override val name = "text"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    object MEDIUMTEXT: VariableType<String>(){

        override val name = "mediumtext"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    object LONGTEXT: VariableType<String>(){

        override val name = "longtext"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    object JSON: VariableType<String>(){

        override val name = "json"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): String? {
            return sqlResultSet.getNullableString(name)
        }
    }

    object BINARY: VariableType<ByteArray>(){

        override val name = "binary"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    object VARBINARY: VariableType<ByteArray>(){

        override val name = "varbinary"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    object TINYBLOB: VariableType<ByteArray>(){

        override val name = "tinyblob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    object MEDIUMBLOB: VariableType<ByteArray>(){

        override val name = "mediumblob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    object BLOB: VariableType<ByteArray>(){

        override val name = "blob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }

    object LONGBLOB: VariableType<ByteArray>(){

        override val name = "longblob"
        override fun getNullableVal(sqlResultSet: SMySQLResultSet): ByteArray? {
            return sqlResultSet.getNullableBytes(name)
        }
    }
}