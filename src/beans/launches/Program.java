package beans.launches;

import beans.faits.FactsBase;
import beans.faits.IFact;
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
        } catch (Exception ignored) {
        }
        return rep;
    }

    @Override
    public boolean askBoolValue(String question) {
        System.out.print(question + " (yes, no) : ");
        String rep = in.next();
        return rep.equals("yes");
    }

    @Override
    public String printFacts(FactsBase facts) {
        //System.out.println("Solutions trouvées :");
        StringBuilder res = new StringBuilder("Base de faits (" + facts.size() + ") :");
        facts.tri();
        for (IFact fact : facts)
            res.append("\n\t").append(fact);
        return res.toString();
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

            System.out.println(m.getM_usedRules());

            GraphBuilderKt.generateGraphAsDotCode(racine.getChild("baseDeRegles"), m.getM_usedRules());
            GraphBuilderKt.generatePng(new File("graph.txt"));


        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
