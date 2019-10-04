package misc;

import beans.regles.Rule;

public class Tuple {
  public final Rule r;
  public final int l;

  public Tuple(Rule r, int l) {
    this.r = r;
    this.l = l;
  }

  @Override
  public String toString() {
    return "Tuple(" + r + ", " + l + ")";
  }
}
