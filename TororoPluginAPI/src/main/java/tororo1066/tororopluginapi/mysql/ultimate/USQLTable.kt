package tororo1066.tororopluginapi.mysql.ultimate

import tororo1066.tororopluginapi.mysql.SMySQL
import tororo1066.tororopluginapi.mysql.SMySQLResultSet

abstract class USQLTable(clazz: Class<out USQLTable>, private val table: String, private val sMySQL: SMySQL) {

    private var variables = LinkedHashMap<String,USQLVariable<*>>()

    var debug = false
    var disableAutoCreateTable = false

    constructor(clazz: Class<out USQLTable>, table: String, sMySQL: SMySQL, disableAutoCreateTable: Boolean): this(clazz,table,sMySQL){
        this.disableAutoCreateTable = disableAutoCreateTable
    }

    init {
        clazz.declaredFields.forEach { field ->
            if (field.type == USQLVariable::class.java){
                field.isAccessible = true
                val variable = field.get(null) as USQLVariable<*>
                variable.name = field.name
                variable.type.columnName = field.name
                variables[field.name] = variable
            }
        }
        if (!disableAutoCreateTable){
            createTable()
        }
    }

    fun createTable(): Boolean{
        val queryBuilder = StringBuilder("create table if not exists $table (")
        queryBuilder.append(variables.values.joinToString(",") { it.type.columnName + " " + it.type.name.lowercase() + (if (it.type.length != -1) "(${it.type.length})" else "") +
                (if (!it.nullable) " not null " else " null ") +
                (if (it.autoIncrement || !it.nullable) "" else if (it.default == null) "default null" else "default " + USQLCondition.modifySQLString(it.type,it.default!!)) +
                if (it.autoIncrement) "auto_increment" else "" })
        queryBuilder.append(if (variables.values.find { it.index != null } != null) ", " + variables.values.filter { it.index != null }.joinToString(",")
        { (if (it.index == USQLVariable.Index.PRIMARY) "${it.index!!.tableString} (${it.name})" else "${it.index!!.tableString} ${it.name} (${it.name})") + if (it.index!!.usingBTREE) " using btree" else "" } else "")
        queryBuilder.append(")")
//        val query = ("create table if not exists $table" +
//                " (${variables.values.joinToString(",") { it.type.columnName + " " + it.type.name.lowercase() + (if (it.type.length != -1) "(${it.type.length})" else "") + "${if (!it.nullable) " not null " else " null "}${if (it.autoIncrement) "" else "default ${if (it.default == null) "null" else USQLCondition.modifySQLString(it.type,it.default!!)}"}" +
//                        if (it.autoIncrement) "auto_increment" else "" }}" +
//                if (variables.values.find { it.index != null } != null) ", " + variables.values.filter { it.index != null }.joinToString(",")
//                { (if (it.index == USQLVariable.Index.PRIMARY) "${it.index!!.tableString} (${it.name})" else "${it.index!!.tableString} ${it.name} (${it.name})") + if (it.index!!.usingBTREE) " using btree" else "" } else "") + ")"
        if (debug){
            sMySQL.plugin.logger.info(queryBuilder.toString())
        }
        return sMySQL.asyncExecute(queryBuilder.toString())
    }


    fun select(condition: USQLCondition = USQLCondition.empty()): ArrayList<SMySQLResultSet> {
        val query = "select * from $table ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncQuery(query)
    }

    fun count(condition: USQLCondition = USQLCondition.empty()): Int {
        val query = "select count(*) from $table ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncCount(query)
    }

    fun insert(values: ArrayList<Any>): Boolean {
        var query = "insert into $table (${variables.values.filterNot { it.autoIncrement }.joinToString(",") { it.type.columnName }})" +
                " values("
        var count = 0
        for (variable in variables.values){
            if (variable.autoIncrement)continue
            if (count != 0) query += ","
            query += USQLCondition.modifySQLString(variable.type,values[count])
            count++
        }
        query += ")"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncExecute(query)
    }

    fun insert(values: HashMap<USQLVariable<*>,Any>): Boolean {
        val query = "insert into $table (${values.keys.joinToString(",") { it.name }})" +
                " values(${values.values.joinToString(",") { USQLCondition.modifySQLString(variables[it]!!.type,it) }})"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncExecute(query)
    }

    fun insert(vararg values: Pair<USQLVariable<*>,Any>): Boolean {
        return insert(hashMapOf(*values))
    }

    fun update(values: HashMap<USQLVariable<*>,Any>, condition: USQLCondition): Boolean {
        val query = "update $table set ${values.entries.joinToString(",") { "${it.key.name} = ${USQLCondition.modifySQLString(variables[it.key.name]!!.type,it.value)}" }} ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncExecute(query)
    }

    fun update(vararg values: Pair<USQLVariable<*>,Any>, condition: USQLCondition = USQLCondition.empty()): Boolean {
        return update(hashMapOf(*values),condition)
    }

    fun update(condition: USQLCondition, vararg values: Pair<USQLVariable<*>,Any>): Boolean {
        return update(hashMapOf(*values),condition)
    }

    fun delete(condition: USQLCondition): Boolean {
        val query = "delete from $table ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncExecute(query)
    }

    /**
     * 出来ないことがあったとき用
     */
    fun execute(query: String): Boolean {
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncExecute(query)
    }


    /**
     * 出来ないことがあったとき用
     */
    fun query(query: String): ArrayList<SMySQLResultSet> {
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncQuery(query)
    }



}