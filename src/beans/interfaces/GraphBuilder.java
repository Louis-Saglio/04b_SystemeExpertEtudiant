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
import java.util.*;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.model.Factory.*;

public class GraphBuilder {
  private String name;
  private List<Node> nodes = new ArrayList<>();

  public GraphBuilder(String name) {
    this.name = name;
  }

  private void addNode(Node node) {
    this.nodes.add(node);
  }

  public void generate(Element rules, List<Rule> usedRules) {
    HashSet<String> ruleNames = usedRules.stream()
        .map(Rule::getName)
        .collect(Collectors.toCollection(HashSet::new));
    HashSet<String> trueFactNames = usedRules.stream()
        .map(rule1 -> rule1.getConclusion().name())
        .collect(Collectors.toCollection(HashSet::new));

    List<Element> children = rules.getChildren();
    Node firstNode = node(
        children.get(0)
            .getChild("premisses")
            .getChild("fait")
            .getAttributeValue("nom")
    ).with(Color.RED);
    addNode(firstNode);

    for (Element rule : rules.getChildren()) {
      String ruleName = rule.getAttributeValue("nom");
      Element premiseElements = rule.getChild("premisses");
      Element conclusion = rule.getChild("conclusion");
      String conclusionName = conclusion.getAttributeValue("nom");

      Node ruleNode = node(ruleName).with(Color.LIGHTBLUE);

      HashMap<Node, Link> linkToRuleByPremiseNode = new HashMap<>();
      for (Element premiseElement : premiseElements.getChildren()) {
        String premiseName = premiseElement.getAttributeValue("nom");
        Node premiseNode = node(premiseName);
        Link linkToRule = to(ruleNode);
        // Store ruleNode and a link toward it in a map in order to link them later
        // We cannot link them here because if the rule is verified the link must be in green
        // So, we modify the link in the part of the code which manage the case where the rule is verified
        // If we link here, the change on the link made here will not be taken in account (once the link is linked, it cannot be changed)
        linkToRuleByPremiseNode.put(premiseNode, linkToRule);
      }

      Node conclusionNode = node(conclusionName);
      Link linkToConclusion = to(conclusionNode);

      // Add colors
      if (ruleNames.contains(ruleName)) {
        // If the rule name is in the list of used rules
        ruleNode = ruleNode.with(Style.FILLED); // Fill used rules
        // Since the rule is used, its conclusion is true : color it in green
        conclusionNode = conclusionNode.with(Style.FILLED).with(Color.rgb(0, 255, 0));
        // Also color the link between the rule and its conclusion in green
        linkToConclusion = linkToConclusion.with(Color.rgb(0, 255, 0));
        // Also color links between premises and validated rule
        linkToRuleByPremiseNode.replaceAll((k, v) -> v.with(Color.rgb(0, 255, 0)));
      }

      for (Map.Entry<Node, Link> nodeLinkEntry : linkToRuleByPremiseNode.entrySet()) {
        Node premiseNode = nodeLinkEntry.getKey().link(nodeLinkEntry.getValue());
        addNode(premiseNode);
      }

      ruleNode = ruleNode.link(linkToConclusion);

      addNode(conclusionNode);
      addNode(ruleNode);
    }

  }

  public void export() throws IOException {
    Graph g = graph(name).directed()
        .graphAttr().with(Rank.dir(RankDir.TOP_TO_BOTTOM))
        .with(nodes);

    Graphviz.fromGraph(g).render(Format.DOT).toFile(new File(name + ".txt"));
    Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(name + ".png"));
  }

}
