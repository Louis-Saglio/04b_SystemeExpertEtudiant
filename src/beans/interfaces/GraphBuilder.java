package beans.interfaces;

import beans.faits.FactsBase;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import org.jdom2.Element;

import static guru.nidi.graphviz.model.Factory.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public void generate(Element rules, FactsBase factDB) {
        Node order = node("Ordre").with(Color.RED);

        for (Element rule : rules.getChildren()) {
            String ruleName = rule.getAttributeValue("nom");
            Element premises = rule.getChild("premisses");
            List<Element> facts = premises.getChildren();
            Element conclusion = rule.getChild("conclusion");

            this.addNodes(node(ruleName).with(Color.BLUE).link(node(conclusion.getAttributeValue("nom"))));

            for (Element fact : facts) {
                Node factNode = node(fact.getAttributeValue("nom")).link(to(node(ruleName)));
                int index = this.getIndexOfNodes(factNode);

                if (index == -1)
                    this.addNodes(factNode);
                else {
                    Node node = this.nodes.get(index).link(node(ruleName));
                    this.nodes.set(index, node);
                }
            }
        }

        this.addNodes(order);
    }

    public void export() throws IOException {
        Graph g = graph(this.name).directed()
                .graphAttr().with(Rank.dir(RankDir.LEFT_TO_RIGHT))
                .with(this.nodes);

        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(this.name + ".png"));
    }

}
