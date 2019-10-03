package beans.faits;

public interface IFact {
  String name();

  Object value();

  int level();

  String question();

  void setLevel(int l);
}
