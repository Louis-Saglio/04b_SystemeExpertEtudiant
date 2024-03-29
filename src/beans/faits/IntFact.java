package beans.faits;

public class IntFact implements IFact {
  private String m_name;

  @Override
  public String name() {
    return m_name;
  }

  private int m_value;

  @Override
  public Object value() {
    return m_value;
  }

  private int m_level;

  @Override
  public int level() {
    return m_level;
  }

  private String m_question = null;

  @Override
  public String question() {
    return m_question;
  }

  @Override
  public void setLevel(int l) {
    m_level = l;
  }

  public IntFact(String name, int value, int level, String question) {
    m_name = name;
    m_value = value;
    m_level = level;
    m_question = question;
  }

  public String toString() {
    return m_name + "=" + m_value + " (" + m_level + ")";
  }
}
