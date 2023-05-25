package tororo1066.tororopluginapi.mysql.ultimate

class USQLOption {

    enum class Order{
        DESC,
        ASC
    }

    private var orderBy = ""
    private var take = ""

    fun orderBy(variable: USQLVariable<*>, order: Order): USQLOption {
        orderBy = "order by ${variable.name} ${order.name.lowercase()}"
        return this
    }

    fun take(takeAmount: Int): USQLOption {
        take = "limit $takeAmount"
        return this
    }
}