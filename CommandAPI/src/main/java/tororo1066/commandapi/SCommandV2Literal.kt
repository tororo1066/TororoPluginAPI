package tororo1066.commandapi

class SCommandV2Literal(vararg val literal: String): SCommandV2Arg() {

    constructor(literal: String): this(*arrayOf(literal))



    override fun copy(): SCommandV2Literal {
        val copy = SCommandV2Literal(*literal)
        super.copyTo(copy)
        return copy
    }
}