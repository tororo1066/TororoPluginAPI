package tororo1066.tororopluginapi.sCommand.v2

import tororo1066.nmsutils.command.AbstractCommandElement
import tororo1066.nmsutils.command.LiteralCommandElement

class SCommandV2Literal(val literal: String): SCommandV2Arg() {

    override fun toElement(): AbstractCommandElement<*> {
        return LiteralCommandElement(literal).apply {
            this@SCommandV2Literal.children.forEach {
                addChild(it.toElement())
            }
            this@SCommandV2Literal.executors.forEach {  executor ->
                onExecute { sender, label, args ->
                    executor(SCommandV2Data(sender, label, args))
                }
            }



            this@SCommandV2Literal.requirements.forEach { requirement ->
                addRequirement { sender ->
                    requirement(sender)
                }
            }
        }
    }
}