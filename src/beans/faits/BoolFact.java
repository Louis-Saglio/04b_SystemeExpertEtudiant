package beans.faits;

public class BoolFact implements IFact {
  private String m_name;

  @Override
  public String name() {
    return m_name;
  }

  private boolean m_value;

  @Override
  public Object value() {
    return m_value;
  }

  private int m_level;

  @Override
  public int level() {
    return m_level;
  }

  private String m_question;

  @Override
  public String question() {
    // TODO Auto-generated method stub
    return m_question;
  }

  @Override
  public void setLevel(int l) {
    m_level = l;
  }

  public BoolFact(String name, boolean value, int level, String question) {
    m_name = name;
    m_value = value;
    m_level = level;
    m_question = question;
  }

  public String toString() {
    String res = !m_value ? "!" : "";
    return res + m_name + " (" + m_level + ")";
  }
}
