package beans.interfaces;

import beans.faits.BoolFact;
import beans.faits.IFact;
import beans.faits.IntFact;
import org.jdom2.Element;

class FactFactory {
  static IFact fact(IFact f, Motor m) {
    IFact newFact = null;
    if (f.getClass().getSimpleName().equals("IntFact")) {
      int value = m.getIhm().askIntValue(f.question());
      newFact = new IntFact(f.name(), value, 0, "");
    } else if (f.getClass().getSimpleName().equals("BoolFact")) {
      boolean value = m.getIhm().askBoolValue(f.question());
      newFact = new BoolFact(f.name(), value, 0, "");
    }
    return newFact;
  }

  static IFact fact(Element eFact) {
    String nom = eFact.getAttributeValue("nom");
    String type = eFact.getAttributeValue("type");
    String question = eFact.getAttributeValue("question");
    if (type.equals("int")) {
      int valeur = Integer.parseInt(eFact.getAttributeValue("valeur"));
      return new IntFact(nom, valeur, 0, question);
    } else
      return new BoolFact(nom, true, 0, question);
  }
}
