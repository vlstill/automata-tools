package cz.muni.fi.fja.fa;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cz.muni.fi.fja.RegularDevice;
import cz.muni.fi.fja.common.Alphabet;

public class QuickEqual {
  private int alphabetCount;
  private int[] convertAlphabets1;
  private int[] convertAlphabets2;
  private boolean[] final1;
  private boolean[] final2;
  private int[][] tableRule1;
  private int[][] tableRule2;
  private static final int blackHole = -1;
  private boolean error;

  public QuickEqual(RegularDevice dev1, RegularDevice dev2) {
    if (dev1.isError() || dev2.isError()) {
      error = true;
    } else {
      error = false;
      DFA d1 = (DFA) dev1.makeDFA();
      DFA d2 = (DFA) dev2.makeDFA();
      tableRule1 = d1.getTableRule();
      tableRule2 = d2.getTableRule();
      unionAlphabets(d1.getAlphabets(), d2.getAlphabets());
      final1 = getFinals(d1.getFinals(), tableRule1.length);
      final2 = getFinals(d2.getFinals(), tableRule2.length);
    }
  }

  // nastavi convertAlphabets1 a convertAlphabets2
  private void unionAlphabets(Alphabet[] alphabets1, Alphabet[] alphabets2) {
    int length1 = alphabets1.length;
    int length2 = alphabets2.length;
    convertAlphabets1 = new int[length1 + length2];
    convertAlphabets2 = new int[length1 + length2];
    Arrays.fill(convertAlphabets1, blackHole);
    Arrays.fill(convertAlphabets2, blackHole);
    int i = 0;
    for (int i1 = 0; i1 < length1; i1++) {
      Alphabet alpha1 = alphabets1[i1];
      convertAlphabets1[i] = i1;
      for (int i2 = 0; i2 < length2; i2++) {
        if (alpha1.equalString(alphabets2[i2])) {
          convertAlphabets2[i] = i2;
        }
      }
      i++;
    }
    System.out.println();
    for (int i2 = 0; i2 < length2; i2++) {
      Alphabet alpha2 = alphabets2[i2];
      boolean foundAlphabet = false;
      for (int i1 = 0; i1 < length1; i1++) {
        if (alpha2.equalString(alphabets1[i1])) {
          foundAlphabet = true;
        }
      }
      if (!foundAlphabet) {
        convertAlphabets2[i] = i2;
        i++;
      }
    }
    alphabetCount = i;
  }

  private boolean[] getFinals(int[] f, int c) {
    boolean[] newf = new boolean[c];
    for (int i = 0; i < f.length; i++) {
      newf[f[i]] = true;
    }
    return newf;
  }

  public boolean equalAlphabets() {
    if (error) {
      return false;
    }
    for (int i = 0; i < alphabetCount; i++) {
      if (convertAlphabets1[i] < 0 || convertAlphabets2[i] < 0) {
        return false;
      }
    }
    return true;
  }

  public boolean result() {
    if (error) {
      return false;
    }
    // System.out.println(Arrays.toString(final1));
    // System.out.println(Arrays.toString(final2));
    // System.out.println(Arrays.toString(convertAlphabets1));
    // System.out.println(Arrays.toString(convertAlphabets2));
    Set<Twins> allTwins = new HashSet<Twins>();
    Set<Twins> addedTwins = new HashSet<Twins>();
    {
      Twins t = new Twins(0, 0);
      if (!t.checkTwin()) {
        return false;
      }
      allTwins.add(t);
      addedTwins.add(t);
    }
    while (!addedTwins.isEmpty()) {
      Set<Twins> newTwins = new HashSet<Twins>();
      for (Twins t : addedTwins) {
        for (int i = 0; i < alphabetCount; i++) {
          Twins newT = t.createNewTwin(i);
          if (!newT.checkTwin()) {
            return false;
          }
          if (allTwins.add(newT)) {
            newTwins.add(newT);
          }
        }
      }
      addedTwins = newTwins;
    }
    return true;
  }

  class Twins {
    private int q1;
    private int q2;

    Twins(int a, int b) {
      q1 = a;
      q2 = b;
    }

    Twins createNewTwin(int alphabet) {
      int x, y;
      if (q1 < 0) {
        x = blackHole;
      } else {
        int a = convertAlphabets1[alphabet];
        if (a < 0) {
          x = blackHole;
        } else {
          x = tableRule1[q1][a];
        }
      }
      if (q2 < 0) {
        y = blackHole;
      } else {
        int a = convertAlphabets2[alphabet];
        if (a < 0) {
          y = blackHole;
        } else {
          y = tableRule2[q2][a];
        }
      }
      // System.out.println("createTwin:" + q1 + "-" + q2 + " : -> :" + x + "-"
      // + y);
      return new Twins(x, y);
    }

    boolean checkTwin() {
      // System.out.println("checkTwin:" + q1 + "-" + q2);
      if (q1 < 0) {
        if (q2 < 0) {
          return true;
        } else {
          return !final2[q2];
        }
      } else {
        if (q2 < 0) {
          return !final1[q1];
        } else {
          return final1[q1] == final2[q2];
        }
      }
    }

    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (!(o instanceof Twins))
        return false;

      Twins t = (Twins) o;
      return q1 == t.q1 && q2 == t.q2;
    }

    public int hashCode() {
      return 37 * q1 + q2;
    }

  }

}
