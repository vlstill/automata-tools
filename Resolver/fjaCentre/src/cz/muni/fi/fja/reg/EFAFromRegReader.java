package cz.muni.fi.fja.reg;

import cz.muni.fi.fja.common.Alphabet;
import cz.muni.fi.fja.common.Control;
import cz.muni.fi.fja.common.ModelError;
import cz.muni.fi.fja.common.ModelReader;
import cz.muni.fi.fja.common.Rule;

public class EFAFromRegReader implements ModelReader {
  Rule[] rules;
  Alphabet[] alphabets;
  Control[] finals;
  ModelError error;

  public EFAFromRegReader(Rule[] r, Alphabet[] a, Rule f) {
    this(r, a, f.getControl(), null);
  }

  public EFAFromRegReader(ModelError e) {
    this(new Rule[0], new Alphabet[0], null, e);
  }

  private EFAFromRegReader(Rule[] r, Alphabet[] a, Control f, ModelError e) {
    rules = r;
    alphabets = a;
    finals = new Control[] { f };
    error = e;
  }

  public Rule[] getAllControl() {
    return rules;
  }

  public Alphabet[] getAllAlphabet() {
    return alphabets;
  }

  public Control[] getAllFinal() {
    return finals;
  }

  public ModelError getError() {
    return error;
  }

}
