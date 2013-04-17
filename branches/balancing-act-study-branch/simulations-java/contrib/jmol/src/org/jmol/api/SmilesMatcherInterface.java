package org.jmol.api;

import java.util.BitSet;

public interface SmilesMatcherInterface {

  public abstract String getLastException();

  public int areEqual(String smiles1, String smiles2);

  public abstract BitSet[] find(String pattern,/* ...in... */String smiles,
                                boolean isSmarts, boolean firstMatchOnly);

  public abstract BitSet getSubstructureSet(String pattern, JmolNode[] atoms,
                                            int atomCount, BitSet bsSelected,
                                            boolean isSmarts,
                                            boolean firstMatchOnly);

  public abstract BitSet[] getSubstructureSetArray(String pattern,
                                                   JmolNode[] atoms,
                                                   int atomCount,
                                                   BitSet bsSelected,
                                                   BitSet bsAromatic,
                                                   boolean isSmarts,
                                                   boolean firstMatchOnly);

  public abstract int[][] getCorrelationMaps(String pattern, JmolNode[] atoms,
                                             int atomCount, BitSet bsSelected,
                                             boolean isSmarts,
                                             boolean firstMatchOnly);

  public abstract String getMolecularFormula(String pattern, boolean isSearch);

  public abstract String getSmiles(JmolNode[] atoms, int atomCount,
                                   BitSet bsSelected, boolean asBioSmiles,
                                   boolean allowUnmatchedRings, boolean addCrossLinks, String comment);
}
