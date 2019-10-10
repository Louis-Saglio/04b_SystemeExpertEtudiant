package beans.launches

import beans.regles.Rule
import org.jdom2.Element
import java.io.BufferedWriter
import java.io.File

fun BufferedWriter.writeln(string: String) {
    write("$string\n")
}

fun String.quote() = "\"$this\""


class DotNode(private val name: String) {

    private val attrs = NodeAttrs()

    override fun toString(): String {
        return "${name.quote()} $attrs"
    }

    fun put(key: String, value: String) {
        attrs[key] = value
    }
}


class NodeAttrs : HashMap<String, String>() {
    override fun toString(): String {
        var string = "["
        for (entry in entries) {
            string += "${entry.key}=${entry.value}, "
        }
        string += "]"
        return string
    }
}


fun generateGraphAsDotCode(ruleElements: Element, verifiedRules: List<Rule>) {
    println("Build graph")
    val writer = File("graph.txt").bufferedWriter()
    writer.writeln("digraph {")

    val verifiedRulesNames = verifiedRules.map { it.name }
    val trueFacts = mutableSetOf<String>()
    for (verifiedRule in verifiedRules) {
        trueFacts.add(verifiedRule.conclusion.name())
        trueFacts.addAll(verifiedRule.premisses.map { it.name() })
    }

    val ruleNodes = mutableSetOf<DotNode>()
    val conclusionNodes = mutableSetOf<DotNode>()
    val premiseNodes = mutableSetOf<DotNode>()


    for (ruleElement in ruleElements.children) {
        val ruleName = ruleElement.getAttributeValue("nom")
        val premiseElements = ruleElement.getChild("premisses")
        val premiseNames = premiseElements.children.map { it.getAttributeValue("nom") }
        val conclusion = ruleElement.getChild("conclusion")
        val conclusionName = conclusion.getAttributeValue("nom")

        val ruleNode = DotNode(ruleName)
        ruleNodes.add(ruleNode)
        ruleNode.put("style", "filled")

        val conclusionNode = DotNode(conclusionName)
        conclusionNodes.add(conclusionNode)

        for (premiseName in premiseNames) {
            val premiseNode = DotNode(premiseName)
            premiseNodes.add(premiseNode)
        }


        if (ruleName in verifiedRulesNames) {
            ruleNode.put("color", "red")
            conclusionNode.put("color", "red")
            for (premiseNode in premiseNodes) {
                premiseNode.put("color", "red")
            }
        } else {
            ruleNode.put("color", "lightblue")
            conclusionNode.put("color", "lightblue")
            for (premiseNode in premiseNodes) {
                premiseNode.put("color", "lightblue")
            }
        }

        if (conclusionName in trueFacts) {

        } else {

        }

        for (premiseName in premiseNames) {
            if (premiseName in trueFacts) {

            } else {

            }
        }
    }

    writer.writeln(ruleNodes.joinToString("\n"))
    writer.writeln(conclusionNodes.joinToString("\n"))
    writer.writeln(premiseNodes.joinToString("\n"))
    writer.write("}")
    writer.close()
}

fun generatePng(inputFile: File) {
    Runtime.getRuntime().exec("""dot -Tpng ${inputFile.absolutePath} -o outfile.png""").waitFor()
}
