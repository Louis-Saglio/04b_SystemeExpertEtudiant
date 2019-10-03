package beans.regles;

import beans.faits.FactsBase;
import beans.faits.IFact;

public class Rule {
  private FactsBase m_premisses;
  private IFact m_conclusion;
  private String m_name;

  public Rule(FactsBase m_premisses, IFact m_conclusion, String m_name) {
    this.m_premisses = m_premisses;
    this.m_conclusion = m_conclusion;
    this.m_name = m_name;
  }

  public String toString() {
    StringBuilder res = new StringBuilder(m_name + " : SI (");
    for (int i = 0; i < m_premisses.size(); i++)
      res.append(m_premisses.get(i)).append(i < m_premisses.size() - 1 ? " AND " : "");
    res.append(") ALORS ").append(m_conclusion);
    return res.toString();
  }

  public FactsBase getPremisses() {
    return m_premisses;
  }

  public void setPremisses(FactsBase premisses) {
    m_premisses = premisses;
  }

  public IFact getConclusion() {
    return m_conclusion;
  }

  public void setConclusion(IFact conclusion) {
    m_conclusion = conclusion;
  }

  public String getName() {
    return m_name;
  }

  public void setName(String name) {
    m_name = name;
  }

}
