package beans.regles;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class RulesBase extends ArrayList<Rule> {
  public RulesBase() {

  }

  public RulesBase(RulesBase rbs) {
    this.addAll(rbs);
  }
}
