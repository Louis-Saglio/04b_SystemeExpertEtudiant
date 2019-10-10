package beans.launches

import beans.regles.Rule
import org.jdom2.Element
import java.io.BufferedWriter
import java.io.File

fun BufferedWriter.writeln(string: String) {
    write("$string\n")
}

fun String.quote() = "\"$this\""


class NodeAttrs(private val node: String = "") : HashMap<String, String>() {
    override fun toString(): String {
        var string = "$node ["
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
    writer.writeln("digraph \"graph1\" {")

    val verifiedRulesNames = verifiedRules.map { it.name }
    val trueFacts = mutableSetOf<String>()
    for (verifiedRule in verifiedRules) {
        trueFacts.add(verifiedRule.conclusion.name())
        trueFacts.addAll(verifiedRule.premisses.map { it.name() })
    }


    for (ruleElement in ruleElements.children) {
        val ruleName = ruleElement.getAttributeValue("nom")
        val premiseElements = ruleElement.getChild("premisses")
        val premiseNames = premiseElements.children.map { it.getAttributeValue("nom") }
        val conclusion = ruleElement.getChild("conclusion")
        val conclusionName = conclusion.getAttributeValue("nom")

        var ruleNode = ruleName.quote()
        var conclusionNode = conclusionName.quote()

        val premiseNodes = mutableListOf<String>()
        for (premiseElement in premiseElements.children) {
            val premiseAttr = NodeAttrs()
            premiseAttr["label"] = (premiseElement.getAttributeValue("valeur") ?: "").quote()
            premiseNodes.add("${premiseElement.getAttributeValue("nom").quote()} -> $ruleNode $premiseAttr")
        }

        val ruleAttrs = NodeAttrs(ruleNode)
        val premiseAttrs = mutableListOf<NodeAttrs>()
        val conclusionAttrs = mutableListOf<NodeAttrs>()
        ruleAttrs["style"] = "filled"
        if (ruleName in verifiedRulesNames) {
            ruleAttrs["color"] = "red"
            val linkColor = NodeAttrs()
            linkColor["color"] = "red"
            conclusionNode += linkColor
            for (i in premiseNodes.indices) {
                val premiseRuleLinkAttrs = NodeAttrs()
                premiseRuleLinkAttrs["color"] = "red"
                premiseNodes[i] = premiseNodes[i] + premiseRuleLinkAttrs
            }
            for (premiseName in premiseNames) {
                val attrs = NodeAttrs(premiseName.quote())
                attrs["color"] = "red"
                premiseAttrs.add(attrs)
            }
            val conclusionAttr = NodeAttrs(conclusionName.quote())
            conclusionAttr["color"] = "red"
            conclusionAttrs.add(conclusionAttr)
        } else {
            ruleAttrs["color"] = "lightblue"
        }

        ruleNode += " -> $conclusionNode"


        writer.writeln(ruleAttrs.toString())
        writer.writeln(premiseAttrs.joinToString("\n"))
        writer.writeln(conclusionAttrs.joinToString("\n"))
        writer.writeln(ruleNode)
        writer.writeln(premiseNodes.joinToString("\n"))
    }

    writer.write("}")
    writer.close()
}

fun generatePng(inputFile: File) {
    Runtime.getRuntime().exec("""dot -Tpng ${inputFile.absolutePath} -o outfile.png""").waitFor()
}
