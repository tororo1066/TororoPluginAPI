package tororo1066.tororopluginapi.script.action

import tororo1066.tororopluginapi.script.ScriptFile
import tororo1066.tororopluginapi.script.action.hidden.OrChoiceAction

class RandomChoiceAction: AbstractAction("randomChoice") {
    override fun invoke(scriptFile: ScriptFile, function: String, line: String, lineIndex: Int, separator: Int) {
        val lines = loadNextLines(scriptFile, lineIndex, separator)
            .filter { it.separator == separator + 1 && it.action is OrChoiceAction }

        if (lines.isEmpty()) {
            return
        }

        val random = lines.random()

        val nextLines = loadNextLines(scriptFile, random.lineIndex, random.separator)

        for (action in nextLines) {
            if (scriptFile.returns.containsKey(function)) {
                return
            }
            if (action.separator != separator+1) {
                continue
            }
            action.invoke(function)
        }
    }
}