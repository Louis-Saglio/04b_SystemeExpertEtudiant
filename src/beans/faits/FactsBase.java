package beans.faits;

import java.util.ArrayList;
import java.util.Comparator;

@SuppressWarnings("serial")
public class FactsBase extends ArrayList<IFact> {
  public IFact search(String name) {
    for (IFact fact : this) {
      if (fact.name().equals(name)) {
        return fact;
      }
    }
    return null;
  }

  public Object value(String name) {
    IFact fact = search(name);
    if (fact != null)
      return fact.value();
    else
      return null;
  }

  public boolean contient(IFact f) {
    IFact trouve = search(f.name());
    if (trouve != null) {
      return f.value().equals(trouve.value());
    } else {
      return false;
    }
  }

  public void tri() {
    this.sort(Comparator.comparing(IFact::level));
  }
}
