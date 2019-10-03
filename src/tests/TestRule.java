package tests;

import beans.faits.BoolFact;
import beans.faits.FactsBase;
import beans.faits.IFact;
import beans.faits.IntFact;
import beans.regles.Rule;

public class TestRule {

  public static void main(String[] args) {
    FactsBase prem = new FactsBase();
    prem.add(new BoolFact("est rectangle", true, 0, "a-t-il un angle rectangle ?"));
    prem.add(new IntFact("nombre de côtés", 3, 0, "Combien a-t-il de côtés ?"));
    IFact conc = new BoolFact("est triangle rectangle", true, 0, "");
    Rule r = new Rule(prem, conc, "toto");
    System.out.println(r);
  }

}
