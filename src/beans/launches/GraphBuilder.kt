package beans.launches

import beans.regles.Rule
import org.jdom2.Element
import java.io.BufferedWriter
import java.io.File

fun BufferedWriter.writeln(string: String) {
    write("$string\n")
}

fun String.quote() = "\"$this\""


abstract class AbstractDotNode {
    protected val attrs = NodeAttrs()

    abstract override fun toString(): String

    fun put(key: String, value: String) {
        attrs[key] = value
    }
}


class DotNode(val name: String) : AbstractDotNode() {
    override fun toString(): String {
        return "${name.quote()} $attrs"
    }
}


class NodeAttrs : HashMap<String, String>() {
    override fun toString(): String {
        var string = "["
        for (entry in entries) {
            string += "${entry.key.quote()}=${entry.value.quote()}, "
        }
        string += "]"
        return string
    }
}

class DotLink(val first: DotNode, val second: DotNode) : AbstractDotNode() {
    override fun toString(): String {
        return "${first.name.quote()} -> ${second.name.quote()} $attrs"
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
    val links = mutableSetOf<DotLink>()

    for (ruleElement in ruleElements.children) {
        val ruleName = ruleElement.getAttributeValue("nom")
        val premiseElements = ruleElement.getChild("premisses")
        val conclusion = ruleElement.getChild("conclusion")
        val conclusionName = conclusion.getAttributeValue("nom")

        val ruleNode = DotNode(ruleName)
        ruleNodes.add(ruleNode)
        ruleNode.put("style", "filled")

        val conclusionNode = DotNode(conclusionName)
        conclusionNodes.add(conclusionNode)

        val ruleToConclusionLink = DotLink(ruleNode, conclusionNode)
        links.add(ruleToConclusionLink)

        for (premiseElement in premiseElements.children) {
            val premiseNode = DotNode(premiseElement.getAttributeValue("nom"))
            premiseNodes.add(premiseNode)
            val premiseToConclusionLink = DotLink(premiseNode, ruleNode)
            links.add(premiseToConclusionLink)
            premiseToConclusionLink.put("label", premiseElement.getAttributeValue("valeur") ?: "")
        }
    }

    for (ruleNode in ruleNodes) {
        if (ruleNode.name in verifiedRulesNames) {
            ruleNode.put("color", "red")
        } else {
            ruleNode.put("color", "lightblue")
        }
    }

    for (premiseNode in premiseNodes) {
        if (premiseNode.name in trueFacts) {
            premiseNode.put("color", "red")
        } else {
            premiseNode.put("color", "lightblue")
        }
    }

    for (conclusionNode in conclusionNodes) {
        if (conclusionNode.name in trueFacts) {
            conclusionNode.put("color", "red")
        } else {
            conclusionNode.put("color", "lightblue")
        }
    }

    for (link in links) {
        if (link.first.name in verifiedRulesNames || link.first.name in trueFacts) {
            link.put("color", "red")
        }
    }

    writer.writeln(ruleNodes.joinToString("\n"))
    writer.writeln(conclusionNodes.joinToString("\n"))
    writer.writeln(premiseNodes.joinToString("\n"))
    writer.writeln(links.joinToString("\n"))
    writer.write("}")
    writer.close()
}

fun generatePng(inputFile: File) {
    Runtime.getRuntime().exec("""dot -Tpng ${inputFile.absolutePath} -o outfile.png""").waitFor()
}
