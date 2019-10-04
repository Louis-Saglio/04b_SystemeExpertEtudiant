package beans.interfaces;

import beans.regles.Rule;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.Node;
import org.jdom2.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.model.Factory.*;

public class GraphBuilder {
    private String name;
    private List<Node> nodes = new ArrayList<>();

    public GraphBuilder(String name) {
        this.name = name;
    }

    private void addNodes(Node node) {
        this.nodes.add(node);
    }

    private Integer getIndexOfNodes(Node node) {
        return this.nodes.indexOf(node);
    }

    public void generate(Element rules, List<Rule> usedRules) {
        HashSet<String> ruleNames = usedRules.stream()
            .map(Rule::getName)
            .collect(Collectors.toCollection(HashSet::new));
        HashSet<String> trueFactNames = usedRules.stream()
            .map(rule1 -> rule1.getConclusion().name())
            .collect(Collectors.toCollection(HashSet::new));

//        Node firstNode = node("Ordre").with(Color.RED);
        List<Element> children = rules.getChildren();
        Node firstNode = node(
            children.get(0)
                .getChild("premisses")
                .getChild("fait")
                .getAttributeValue("nom")
        ).with(Color.RED);
        System.out.println(firstNode);
        addNodes(firstNode);

        for (Element rule : rules.getChildren()) {
            String ruleName = rule.getAttributeValue("nom");
            Element premiseElements = rule.getChild("premisses");
            Element conclusion = rule.getChild("conclusion");
            String conclusionName = conclusion.getAttributeValue("nom");

            Node ruleNode = node(ruleName).with(Color.LIGHTBLUE);

            for (Element premiseElement : premiseElements.getChildren()) {
                String premiseName = premiseElement.getAttributeValue("nom");
                Node premiseNode = node(premiseName);
                Link linkToRule = to(ruleNode);
                premiseNode = premiseNode.link(linkToRule);
                addNodes(premiseNode);
            }

            Node conclusionNode = node(conclusionName);
            Link linkToConclusion = to(conclusionNode);

            ruleNode = ruleNode.link(linkToConclusion);

            // Add colors
            if (ruleNames.contains(ruleName)) {
                // If the rule name is in the list of used rules
                ruleNode = ruleNode.with(Style.FILLED); // Fill used rules
                // Since the rule is used, its conclusion is true : color it in green
                conclusionNode = conclusionNode.with(Style.FILLED).with(Color.rgb(0, 255, 0));
            }


            addNodes(ruleNode);
        }

    }

    public void export() throws IOException {
        Graph g = graph(this.name).directed()
                .graphAttr().with(Rank.dir(RankDir.TOP_TO_BOTTOM))
                .with(this.nodes);

        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(this.name + ".png"));
    }

}
