package tororo1066.tororopluginapi.integer

import tororo1066.tororopluginapi.exception.PlusIntException
import tororo1066.tororopluginapi.otherUtils.UsefulUtility

class PlusInt() {

    private var int: Int = 0

    constructor(int: Int): this(){
        if (int < 0) throw PlusIntException("failed cast $int to PlusInt")
        this.int = int
    }

    fun get(): Int {
        return int
    }

    fun set(int: Int): Boolean {
        if (int < 0)return false
        this.int = int
        return true
    }

    override fun toString(): String {
        return this.int.toString()
    }

    companion object{
        fun Int.toPlusInt(): PlusInt?{
            return UsefulUtility.sTry({PlusInt(this)},{null})
        }

        fun String.toPlusInt(): PlusInt?{
            return UsefulUtility.sTry({PlusInt(this.toIntOrNull()?:return@sTry null)},{null})
        }
    }
}