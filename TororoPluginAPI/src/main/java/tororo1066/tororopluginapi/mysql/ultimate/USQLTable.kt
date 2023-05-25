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


    private fun selectQuery(condition: USQLCondition = USQLCondition.empty()): String {
        val query = "select * from $table ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }

        return query
    }

    fun select(condition: USQLCondition = USQLCondition.empty()): ArrayList<SMySQLResultSet> {
        return sMySQL.asyncQuery(selectQuery(condition))
    }

    fun callBackSelect(condition: USQLCondition = USQLCondition.empty(), callBack: (ArrayList<SMySQLResultSet>) -> Unit) {
        sMySQL.callbackQuery(selectQuery(condition), callBack)
    }

    private fun countQuery(condition: USQLCondition = USQLCondition.empty()): String {
        val query = "select count(*) from $table ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return query
    }

    fun count(condition: USQLCondition = USQLCondition.empty()): Int {
        return sMySQL.asyncCount(countQuery(condition))
    }

//    fun callBackCount(condition: USQLCondition = USQLCondition.empty(), callBack: (Int) -> Unit){
//
//    }

    private fun insertQuery(values: List<Any>): String {
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

        return query
    }

    private fun insertQuery1(values: HashMap<USQLVariable<*>,Any>): String {
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

        return query
    }

    private fun insertQuery2(values: HashMap<String, Any>): String {
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

        return query
    }

    fun insert(values: List<Any>): Boolean {
        return sMySQL.asyncExecute(insertQuery(values))
    }

    fun callBackInsert(values: List<Any>, callBack: (Boolean) -> Unit = {}) {
        sMySQL.callbackExecute(insertQuery(values),callBack)
    }

    fun insert(vararg values: Any): Boolean {
        return insert(arrayListOf(*values))
    }

    fun callBackInsert(vararg values: Any, callBack: (Boolean) -> Unit = {}) {
        callBackInsert(arrayListOf(*values), callBack)
    }

    fun insert(values: HashMap<USQLVariable<*>,Any>): Boolean {
        return sMySQL.asyncExecute(insertQuery1(values))
    }

    fun callBackInsert(values: HashMap<USQLVariable<*>, Any>, callBack: (Boolean) -> Unit = {}) {
        sMySQL.callbackExecute(insertQuery1(values), callBack)
    }

    fun insert(vararg values: Pair<USQLVariable<*>,Any>): Boolean {
        return insert(hashMapOf(*values))
    }

    fun callBackInsert(vararg values: Pair<USQLVariable<*>,Any>, callBack: (Boolean) -> Unit = {}) {
        callBackInsert(hashMapOf(*values), callBack)
    }

    @JvmName("insert1")
    fun insert(values: HashMap<String,Any>): Boolean {
        return sMySQL.asyncExecute(insertQuery2(values))
    }

    @JvmName("callBackInsert1")
    fun callBackInsert(values: HashMap<String, Any>, callBack: (Boolean) -> Unit = {}) {
        sMySQL.callbackExecute(insertQuery2(values), callBack)
    }

    private fun updateQuery(values: HashMap<USQLVariable<*>, Any>, condition: USQLCondition): String {
        val query = "update $table set ${values.entries.joinToString(",") { "${it.key.name} = ${USQLCondition.modifySQLString(variables[it.key.name]!!.type,it.value)}" }} ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return query
    }

    private fun updateQuery1(values: HashMap<String, Any>, condition: USQLCondition): String {
        val query = "update $table set ${values.entries.joinToString(",")
        { "${it.key} = ${USQLCondition.modifySQLString(variables[it.key]!!.type,it.value)}" }}" +
                " ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return query
    }

    fun update(values: HashMap<USQLVariable<*>,Any>, condition: USQLCondition): Boolean {
        return sMySQL.asyncExecute(updateQuery(values, condition))
    }

    fun update(vararg values: Pair<USQLVariable<*>,Any>, condition: USQLCondition = USQLCondition.empty()): Boolean {
        return update(hashMapOf(*values),condition)
    }

    fun update(condition: USQLCondition, vararg values: Pair<USQLVariable<*>,Any>): Boolean {
        return update(hashMapOf(*values),condition)
    }

    fun callBackUpdate(values: HashMap<USQLVariable<*>,Any>, condition: USQLCondition, callBack: (Boolean) -> Unit) {
        sMySQL.callbackExecute(updateQuery(values, condition), callBack)
    }

    fun callBackUpdate(vararg values: Pair<USQLVariable<*>,Any>, condition: USQLCondition = USQLCondition.empty(), callBack: (Boolean) -> Unit) {
        return callBackUpdate(hashMapOf(*values), condition, callBack)
    }

    fun callBackUpdate(condition: USQLCondition, vararg values: Pair<USQLVariable<*>,Any>, callBack: (Boolean) -> Unit) {
        return callBackUpdate(hashMapOf(*values), condition, callBack)
    }

    @JvmName("update1")
    fun update(values: HashMap<String,Any>, condition: USQLCondition): Boolean {
        return sMySQL.asyncExecute(updateQuery1(values, condition))
    }

    @JvmName("callBackUpdate1")
    fun callBackUpdate(values: HashMap<String,Any>, condition: USQLCondition, callBack: (Boolean) -> Unit) {
        sMySQL.callbackExecute(updateQuery1(values,condition), callBack)
    }

    private fun deleteQuery(condition: USQLCondition): String {
        val query = "delete from $table ${condition.build()}"
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return query
    }

    fun delete(condition: USQLCondition): Boolean {
        return sMySQL.asyncExecute(deleteQuery(condition))
    }

    fun callBackDelete(condition: USQLCondition, callBack: (Boolean) -> Unit) {
        sMySQL.callbackExecute(deleteQuery(condition), callBack)
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

    /**
     * 出来ないことがあったとき用
     */
    fun callBackExecute(query: String, callBack: (Boolean) -> Unit = {}) {
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        sMySQL.callbackExecute(query, callBack)
    }


    /**
     * 出来ないことがあったとき用
     */
    fun callBackQuery(query: String, callBack: (ArrayList<SMySQLResultSet>) -> Unit) {
        if (debug){
            sMySQL.plugin.logger.info(query)
        }
        return sMySQL.callbackQuery(query, callBack)
    }

}