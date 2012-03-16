/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-04-26 16:57:51 -0500 (Thu, 26 Apr 2007) $
 * $Revision: 7502 $
 *
 * Copyright (C) 2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.api;


import java.util.BitSet;
import java.util.List;

public interface JmolNode {
  
  // abstracts out the essential pieces for SMILES processing
  
  public void set(float x, float y, float z);
  public int getAtomSite();
  public int getBondedAtomIndex(int j);
  public int getCovalentHydrogenCount();
  public JmolEdge[] getEdges();
  public short getElementNumber();
  public int getFormalCharge();
  public int getIndex();
  public short getIsotopeNumber();
  public int getModelIndex();
  public int getValence();
  public int getCovalentBondCount();
  public int getImplicitHydrogenCount();
  public short getAtomicAndIsotopeNumber();
  
  // BIOSMILES/BIOSMARTS
  
  public String getAtomName();
  public String getGroupType();
  public String getGroup1(char c0);
  public String getGroup3(boolean allowNull);
  public int getResno();
  public char getChainID();
  public int getOffsetResidueAtom(String name, int offset);
  public boolean getCrossLinkLeadAtomIndexes(List<Integer> vReturn);
  public void setGroupBits(BitSet bs);
  public boolean isLeadAtom();
  public boolean isCrossLinked(JmolNode node);
  public boolean isProtein();
  public boolean isNucleic();
  public boolean isDna();
  public boolean isRna();
  public boolean isPurine();
  public boolean isPyrimidine();
  public boolean isDeleted();
}
