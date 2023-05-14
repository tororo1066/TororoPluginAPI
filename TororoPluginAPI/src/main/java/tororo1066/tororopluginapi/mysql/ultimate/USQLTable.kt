package tororo1066.tororopluginapi.mysql.ultimate

import org.bukkit.Bukkit
import tororo1066.tororopluginapi.mysql.SMySQL
import tororo1066.tororopluginapi.mysql.SMySQLResultSet

abstract class USQLTable(private val table: String, private val sMySQL: SMySQL) {

    private var variables = LinkedHashMap<String,USQLVariable<*>>()

    var debug = false
    var disableAutoCreateTable = false

    constructor(table: String, sMySQL: SMySQL, disableAutoCreateTable: Boolean): this(table,sMySQL){
        this.disableAutoCreateTable = disableAutoCreateTable
    }

    init {
        javaClass.declaredFields.forEach { field ->
            if (field.type == USQLVariable::class.java){
                field.isAccessible = true
                val variable = field.get(null) as? USQLVariable<*>?:return@forEach
                variable.name = field.name
                variable.type.name = field.name
                variables[field.name] = variable
            }
        }
        if (!disableAutoCreateTable){
            createTable()
        }
    }

    fun createTable(): Boolean{
        val queryBuilder = StringBuilder()
        if (sMySQL.useSQLite){
            queryBuilder.append("create table if not exists $table (")
            queryBuilder.append(variables.values.joinToString(",") { it.name + " " + (if (it.type is USQLVariable.INT) "integer" else it.type.variableName.lowercase()) +
                    (if (it.index != null) " ${it.index!!.tableString}" else "") +
                    (if (!it.nullable && !it.autoIncrement) " not null" else "") +
                    (if (it.autoIncrement || !it.nullable) "" else if (it.default == null) " default null" else " default " + USQLCondition.modifySQLString(it.type,it.default!!)) +
                    if (it.autoIncrement) " autoincrement" else "" })
            queryBuilder.append(")")
        } else {
            queryBuilder.append("create table if not exists $table (")
            queryBuilder.append(variables.values.joinToString(",") { it.name + " " + it.type.variableName.lowercase() + (if (it.length != -1) "(${it.length})" else "") +
                    (if (!it.nullable) " not null" else " null") +
                    (if (it.autoIncrement || !it.nullable) "" else if (it.default == null) " default null" else " default " + USQLCondition.modifySQLString(it.type,it.default!!)) +
                    if (it.autoIncrement) " auto_increment" else "" })
            queryBuilder.append(if (variables.values.find { it.index != null } != null) ", " + variables.values.filter { it.index != null }.joinToString(",")
            { (if (it.index == USQLVariable.Index.PRIMARY) "${it.index!!.tableString} (${it.name})" else "${it.index!!.tableString} ${it.name} (${it.name})") + if (it.index!!.usingBTREE) " using btree" else "" } else "")
            queryBuilder.append(")")
        }

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

    fun insert(values: List<Any>): Boolean {
        var query = "insert into $table (${variables.values.filterNot { it.autoIncrement }.joinToString(",") { it.name }})" +
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

    fun insert(vararg values: Any): Boolean {
        return insert(arrayListOf(*values))
    }

    fun insert(values: HashMap<USQLVariable<*>,Any>): Boolean {
        var query = "insert into $table (${values.keys.joinToString(",") { it.name }})" +
                " values("
        for (variable in values){
            query += "${USQLCondition.modifySQLString(variable.key.type,variable.value)},"
        }
        query = query.dropLast(1)
        query += ")"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncExecute(query)
    }

    fun insert(vararg values: Pair<USQLVariable<*>,Any>): Boolean {
        return insert(hashMapOf(*values))
    }

    @JvmName("insert1")
    fun insert(values: HashMap<String,Any>): Boolean {
        var query = "insert into $table (${values.keys.joinToString(",") { it }})" +
                " values("
        for (variable in values){
            query += "${USQLCondition.modifySQLString(variables[variable.key]!!.type,variable.value)},"
        }
        query = query.dropLast(1)
        query += ")"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncExecute(query)
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

    @JvmName("update1")
    fun update(values: HashMap<String,Any>, condition: USQLCondition): Boolean {
        val query = "update $table set ${values.entries.joinToString(",") 
        { "${it.key} = ${USQLCondition.modifySQLString(variables[it.key]!!.type,it.value)}" }}" +
                " ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.asyncExecute(query)
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