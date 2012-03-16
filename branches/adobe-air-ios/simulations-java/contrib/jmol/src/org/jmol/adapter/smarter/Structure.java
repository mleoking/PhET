/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-10-12 05:49:48 -0700 (Tue, 12 Oct 2010) $
 * $Revision: 14469 $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
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

package org.jmol.adapter.smarter;

import org.jmol.viewer.JmolConstants;

public class Structure {
  public int structureType;
  public int substructureType;
  public String structureID;
  public int serialID;
  public int strandCount;
  public char startChainID = ' ';
  public int startSequenceNumber;
  public char startInsertionCode = ' ';
  public char endChainID = ' ';
  public int endSequenceNumber;
  public char endInsertionCode = ' ';
  public int modelIndex;

  public final static byte PROTEIN_STRUCTURE_NONE = JmolConstants.PROTEIN_STRUCTURE_NONE;
  public final static byte PROTEIN_STRUCTURE_TURN = JmolConstants.PROTEIN_STRUCTURE_TURN;
  public final static byte PROTEIN_STRUCTURE_SHEET = JmolConstants.PROTEIN_STRUCTURE_SHEET;
  public final static byte PROTEIN_STRUCTURE_HELIX = JmolConstants.PROTEIN_STRUCTURE_HELIX;
  public final static byte PROTEIN_STRUCTURE_HELIX_310 = JmolConstants.PROTEIN_STRUCTURE_HELIX_310;
  public final static byte PROTEIN_STRUCTURE_HELIX_ALPHA = JmolConstants.PROTEIN_STRUCTURE_HELIX_ALPHA;
  public final static byte PROTEIN_STRUCTURE_HELIX_PI = JmolConstants.PROTEIN_STRUCTURE_HELIX_PI;

  public static int getHelixType(int type) {
    switch (type) {
    case 1:
      return PROTEIN_STRUCTURE_HELIX_ALPHA;
    case 3:
      return PROTEIN_STRUCTURE_HELIX_PI;
    case 5:
      return PROTEIN_STRUCTURE_HELIX_310;
    }
    return PROTEIN_STRUCTURE_HELIX;
  }
  

  public Structure(int type) {
    structureType = substructureType = type;
  }

  public Structure(int modelIndex, int structureType, int substructureType,
            String structureID, int serialID, int strandCount,
            char startChainID, int startSequenceNumber, char startInsertionCode,
            char endChainID, int endSequenceNumber, char endInsertionCode) {
    this.modelIndex = modelIndex;
    this.structureType = structureType;
    this.substructureType = substructureType;
    this.structureID = structureID;
    this.strandCount = strandCount; // 1 for sheet initially; 0 for helix or turn
    this.serialID = serialID;
    this.startChainID = startChainID;
    this.startSequenceNumber = startSequenceNumber;
    this.startInsertionCode = startInsertionCode;
    this.endChainID = endChainID;
    this.endSequenceNumber = endSequenceNumber;
    this.endInsertionCode = endInsertionCode;
  }

}
