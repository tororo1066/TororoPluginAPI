package tororo1066.tororopluginapi.otherClass

@Suppress("UNUSED")
class StrExcludeFileIllegalCharacter(private val str: String){
    val string: String get() {
        if (str.matches(Regex("[(<|>:?\"/\\\\)*]")))throw NullPointerException("$str is use file illegal character.")
        return str
    }
    val nullableString: String? get() {
        if (str.matches(Regex("[(<|>:?\"/\\\\)*]")))return null
        return str
    }
}