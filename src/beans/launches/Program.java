package beans.launches;

import beans.faits.FactsBase;
import beans.faits.IFact;
import beans.interfaces.GraphBuilder;
import beans.interfaces.HumanInterface;
import beans.interfaces.Motor;
import beans.regles.Rule;
import beans.regles.RulesBase;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;


import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Program implements HumanInterface {
    private Scanner in = new Scanner(System.in);

    @Override
    public int askIntValue(String question) {
        System.out.print(question + " : ");
        int rep = 0;
        try {
            rep = in.nextInt();
        } catch (Exception exc) {
        }
        return rep;
    }

    @Override
    public boolean askBoolValue(String question) {
        System.out.print(question + " (yes, no) : ");
        String rep = in.next();
        return rep.equals("yes") ? true : false;
    }

    @Override
    public String printFacts(FactsBase facts) {
        //System.out.println("Solutions trouvées :");
        String res = "Base de faits (" + facts.size() + ") :";
        facts.tri();
        for (IFact fact : facts)
            res += "\n\t" + fact;
        return res;
    }

    @Override
    public void printRules(RulesBase rules) {
        for (Rule r : rules)
            System.out.println(r);
    }

    public static void main(String[] args) {
        Program p = new Program();
        p.run();

    }

    /**
     * Load the xml file into the rule base and the fact base
     * Then launch the inference engine
     */
    private void run() {
        System.out.println("*** Création du moteur ***");
        Motor m = new Motor(this);
        System.out.println("*** Ajout des règles ***");
        GraphBuilder graphBuilder = new GraphBuilder("graph1");

        try {
            SAXBuilder sxb = new SAXBuilder();
            Document doc = sxb.build(new File("geometrie.xml"));
            Element racine = doc.getRootElement();
            List<Element> lRegles = racine.getChild("baseDeRegles").getChildren();
            for (Element eRegle : lRegles) {
                m.addRule(eRegle);
            }

            printRules(m.getrDB());
            System.out.println("\n*** Résolution ***");

            m.solve();

            graphBuilder.generate(racine.getChild("baseDeRegles"), m.getfDB());
            graphBuilder.export();


        } catch (Exception exc) {
            System.err.println(exc.getMessage());
        }
    }

}
