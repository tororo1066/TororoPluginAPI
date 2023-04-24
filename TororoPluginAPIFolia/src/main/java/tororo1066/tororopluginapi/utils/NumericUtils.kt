package tororo1066.tororopluginapi.utils

import tororo1066.tororopluginapi.otherUtils.UsefulUtility

fun String.isInt(): Boolean {
    return this.toIntOrNull() != null
}

fun String.isDouble(): Boolean {
    return this.toDoubleOrNull() != null
}

fun String.isLong(): Boolean {
    return this.toLongOrNull() != null
}

fun String.toIntRange(): IntRange {
    if (!this.contains(".."))throw NumberFormatException("$this is not IntRange.")
    val split = this.split("..")
    if (!split[0].isInt() || !split[1].isInt())throw NumberFormatException("$this is not IntRange.")
    return split[0].toInt()..split[1].toInt()
}

fun String.toIntRangeOrNull(): IntRange? {
    return UsefulUtility.sTry({ this.toIntRange() }) { null }
}