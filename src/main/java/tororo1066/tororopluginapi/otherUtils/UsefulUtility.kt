package tororo1066.tororopluginapi.otherUtils

class UsefulUtility {

    companion object{
        fun doubleToFormatString(double: Double): String {
            return String.format("%,.0f",double)
        }
    }
}