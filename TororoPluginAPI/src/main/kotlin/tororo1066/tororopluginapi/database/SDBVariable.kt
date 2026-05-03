package tororo1066.tororopluginapi.database

import java.time.LocalDateTime

@Suppress("UNUSED")
class SDBVariable<V: SDBVariable.VariableType<*>>(val type: V) {

    companion object{
        val Boolean get() = BOOLEAN()
        val TinyInt get() = TINYINT()
        val SmallInt get() = SMALLINT()
        val MediumInt get() = MEDIUMINT()
        val Int get() = INT()
        val BigInt get() = BIGINT()
        val Float get() = FLOAT()
        val Double get() = DOUBLE()
        val Decimal get() = DECIMAL()
        val Date get() = DATE()
        val DateTime get() = DATETIME()
        val TimeStamp get() = TIMESTAMP()
        val Time get() = TIME()
        val Year get() = YEAR()
        val Char get() = CHAR()
        val VarChar get() = VARCHAR()
        val TinyText get() = TINYTEXT()
        val Text get() = TEXT()
        val MediumText get() = MEDIUMTEXT()
        val LongText get() = LONGTEXT()
        val Binary get() = BINARY()
        val VarBinary get() = VARBINARY()
        val TinyBlob get() = TINYBLOB()
        val MediumBlob get() = MEDIUMBLOB()
        val Blob get() = BLOB()
        val LongBlob get() = LONGBLOB()
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

    enum class Index(val tableString: String, val usingBTREE: Boolean){
        PRIMARY("primary key",true),
        KEY("index",true),
        UNIQUE("unique index",false),
        FULLTEXT("fulltext index",false),
        SPATIAL("spatial index",false)
    }

    fun equal(value: Any): SDBCondition {
        return SDBCondition().equal(this.name, value)
    }

    fun orHigher(value: Any): SDBCondition {
        return SDBCondition().orHigher(this.name, value)
    }

    fun orLower(value: Any): SDBCondition {
        return SDBCondition().orLower(this.name, value)
    }

    fun moreThan(value: Any): SDBCondition {
        return SDBCondition().moreThan(this.name, value)
    }

    fun lessThan(value: Any): SDBCondition {
        return SDBCondition().lessThan(this.name, value)
    }

    abstract class VariableType<V>{

        abstract val variableName: String
        var name: String = ""
    }

    class BOOLEAN: VariableType<Boolean>(){

        override val variableName = "tinytext"
    }

    class TINYINT: VariableType<Int>(){

        override val variableName = "tinyint"
    }

    class SMALLINT: VariableType<Int>(){

        override val variableName = "smallint"
    }

    class MEDIUMINT: VariableType<Int>(){

        override val variableName = "mediumint"
    }

    class INT: VariableType<Int>(){

        override val variableName = "int"
    }

    class BIGINT: VariableType<Long>(){

        override val variableName = "bigint"
    }

    class BIT: VariableType<Int>(){

        override val variableName = "bit"
    }

    class FLOAT: VariableType<Float>(){

        override val variableName = "float"
    }

    class DOUBLE: VariableType<Double>(){

        override val variableName = "double"
    }

    class DECIMAL: VariableType<Double>(){

        override val variableName = "decimal"
    }

    class DATE: VariableType<LocalDateTime>(){

        override val variableName = "date"
    }

    class DATETIME: VariableType<LocalDateTime>(){

        override val variableName = "datetime"
    }

    class TIMESTAMP: VariableType<LocalDateTime>(){

        override val variableName = "timestamp"
    }

    class TIME: VariableType<LocalDateTime>(){

        override val variableName = "time"
    }

    class YEAR: VariableType<LocalDateTime>(){

        override val variableName = "year"
    }

    class CHAR: VariableType<String>(){

        override val variableName = "char"
    }

    class VARCHAR: VariableType<String>(){

        override val variableName = "varchar"
    }

    class TINYTEXT: VariableType<String>(){

        override val variableName = "tinytext"
    }

    class TEXT: VariableType<String>(){

        override val variableName = "text"
    }

    class MEDIUMTEXT: VariableType<String>(){

        override val variableName = "mediumtext"
    }

    class LONGTEXT: VariableType<String>(){

        override val variableName = "longtext"
    }

    class JSON: VariableType<String>(){

        override val variableName = "json"
    }

    class BINARY: VariableType<ByteArray>(){

        override val variableName = "binary"
    }

    class VARBINARY: VariableType<ByteArray>(){

        override val variableName = "varbinary"
    }

    class TINYBLOB: VariableType<ByteArray>(){

        override val variableName = "tinyblob"
    }

    class MEDIUMBLOB: VariableType<ByteArray>(){

        override val variableName = "mediumblob"
    }

    class BLOB: VariableType<ByteArray>(){

        override val variableName = "blob"
    }

    class LONGBLOB: VariableType<ByteArray>(){

        override val variableName = "longblob"
    }
}