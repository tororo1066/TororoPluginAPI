package tororo1066.tororopluginapi.mysql.ultimate

import java.util.Date

class USQLVariable<V: USQLVariable.Type>(val type: V) {

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

    enum class Type(){
        BOOLEAN,
        TINYINT,
        SMALLINT,
        MEDIUMINT,
        INT,
        BIGINT,
        BIT,
        FLOAT,
        DOUBLE,
        DECIMAL,
        DATE,
        DATETIME,
        TIMESTAMP,
        TIME,
        YEAR,
        CHAR,
        VARCHAR,
        TINYTEXT,
        TEXT,
        MEDIUMTEXT,
        LONGTEXT,
        JSON,
        BINARY,
        VARBINARY,
        TINYBLOB,
        MEDIUMBLOB,
        BLOB,
        LONGBLOB;


    }
}