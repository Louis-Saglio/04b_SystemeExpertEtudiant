package beans.interfaces;

import beans.faits.FactsBase;
import beans.regles.RulesBase;

public interface HumanInterface {
  int askIntValue(String question);

  boolean askBoolValue(String question);

  String printFacts(FactsBase facts);

  void printRules(RulesBase rules);
}
