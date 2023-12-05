package tororo1066.nmsutils.command

import com.mojang.brigadier.Message

class ToolTip(val text: String, val toolTip: Message? = null) {

    constructor(text: String, toolTip: String): this(text, Message { toolTip })
}