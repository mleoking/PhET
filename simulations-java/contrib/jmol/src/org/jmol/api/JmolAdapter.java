/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-02-18 05:46:36 -0800 (Fri, 18 Feb 2011) $
 * $Revision: 15192 $
 *
 * Copyright (C) 2003-2005  The Jmol Development Team
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.BitSet;
import java.util.Map;

import org.jmol.modelset.Group;
import org.jmol.util.Elements;
import org.jmol.viewer.JmolConstants;

/****************************************************************
 * The JmolAdapter interface defines the API used by the JmolViewer to
 * read external files and fetch atom properties necessary for rendering.
 *
 * A client of the JmolViewer implements this interface on top of their
 * existing molecular model representation. The JmolViewer then requests
 * information from the implementation using this API. 
 *
 * Jmol will automatically calculate some atom properties if the client
 * is not capable or does not want to supply them.
 *
 * Note: If you are seeing pink atoms that have lots of bonds, then your
 * methods for getElementNumber(clientAtom) or getElementSymbol(clientAtom)
 * are probably returning stray values. Therefore, these atoms are getting
 * mapped to element 0 (Xx), which has color pink and a relatively large
 * covalent bonding radius. 
 * @see org.jmol.api.JmolViewer
 * @see org.jmol.adapter.smarter.SmarterJmolAdapter
 ****************************************************************/
public abstract class JmolAdapter {

  
  public final static short ORDER_COVALENT_SINGLE = JmolEdge.BOND_COVALENT_SINGLE;
  public final static short ORDER_COVALENT_DOUBLE = JmolEdge.BOND_COVALENT_DOUBLE;
  public final static short ORDER_COVALENT_TRIPLE = JmolEdge.BOND_COVALENT_TRIPLE;
  public final static short ORDER_AROMATIC        = JmolEdge.BOND_AROMATIC;
  public final static short ORDER_AROMATIC_SINGLE = JmolEdge.BOND_AROMATIC_SINGLE;
  public final static short ORDER_AROMATIC_DOUBLE = JmolEdge.BOND_AROMATIC_DOUBLE;
  public final static short ORDER_HBOND           = JmolEdge.BOND_H_REGULAR;
  public final static short ORDER_STEREO_NEAR     = JmolEdge.BOND_STEREO_NEAR; 
  public final static short ORDER_STEREO_FAR      = JmolEdge.BOND_STEREO_FAR; 
  public final static short ORDER_PARTIAL01       = JmolEdge.BOND_PARTIAL01;
  public final static short ORDER_PARTIAL12       = JmolEdge.BOND_PARTIAL12;
  public final static short ORDER_PARTIAL23       = JmolEdge.BOND_PARTIAL23;
  public final static short ORDER_PARTIAL32       = JmolEdge.BOND_PARTIAL32;
  public final static short ORDER_UNSPECIFIED     = JmolEdge.BOND_ORDER_UNSPECIFIED;
  
  public final static int        SHELL_S           = JmolConstants.SHELL_S;
  public final static int        SHELL_P           = JmolConstants.SHELL_P;
  public final static int        SHELL_SP          = JmolConstants.SHELL_SP;
  public final static int        SHELL_L           = JmolConstants.SHELL_L;
  public final static int        SHELL_D_SPHERICAL = JmolConstants.SHELL_D_SPHERICAL;
  public final static int        SHELL_D_CARTESIAN = JmolConstants.SHELL_D_CARTESIAN;
  public final static int        SHELL_F_SPHERICAL = JmolConstants.SHELL_F_SPHERICAL;
  public final static int        SHELL_F_CARTESIAN = JmolConstants.SHELL_F_CARTESIAN;
  public static final String SUPPORTED_BASIS_FUNCTIONS = JmolConstants.SUPPORTED_BASIS_FUNCTIONS;
  
  public static String getElementSymbol(int elementNumber) {
    return Elements.elementSymbolFromNumber(elementNumber);
  }
  
  public static short getElementNumber(String elementSymbol) {
    return Elements.elementNumberFromSymbol(elementSymbol, false);
  }
  
  public static int getNaturalIsotope(int elementNumber) {
    return Elements.getNaturalIsotope(elementNumber);
  }

  public static boolean isHetero(String group3) {
    return JmolConstants.isHetero(group3);
  }
  
  public static int getQuantumShellTagID(String tag) {
    return JmolConstants.getQuantumShellTagID(tag);
  }
                                           
  public static int getQuantumShellTagIDSpherical(String tag) {
    return JmolConstants.getQuantumShellTagIDSpherical(tag);
  }
  
  final public static short lookupGroupID(String group3) {
    return Group.lookupGroupID(group3);
  }

  public static float getBondingRadiusFloat(int atomicNumber, int charge) {
    return JmolConstants.getBondingRadiusFloat(atomicNumber, charge);
  }

  //////////////////////////////////////////////////////////////////
  // file related
  //////////////////////////////////////////////////////////////////


  String adapterName;

  public JmolAdapter(String adapterName) {
    this.adapterName = adapterName;
  }

  public String getAdapterName() {
    return adapterName;
  }
  
/**
 * Read an atomSetCollection object from a bufferedReader and close the reader.
 * 
 * <p>Given the BufferedReader, return an object which represents the file
 * contents. The parameter <code>name</code> is assumed to be the
 * file name or URL which is the source of reader. Note that this 'file'
 * may have been automatically decompressed. Also note that the name
 * may be 'String', representing a string constant. Therefore, few
 * assumptions should be made about the <code>name</code> parameter.
 *
 * The return value is an object which represents a <code>atomSetCollection</code>.
 * This <code>atomSetCollection</code> will be passed back in to other methods.
 * If the return value is <code>instanceof String</code> then it is
 * considered an error condition and the returned String is the error
 * message. 
 *
 * @param name File name, String or URL acting as the source of the reader
 * @param type File type, if known, or null
 * @param bufferedReader The BufferedReader
 * @param htParams a hash table containing parameter information
 * @return The atomSetCollection or String with an error message
 */
abstract public Object getAtomSetCollectionReader(String name, String type,
                                 BufferedReader bufferedReader, Map<String, Object> htParams);

abstract public Object getAtomSetCollection(Object atomSetCollectionReader);
  /**
   * Associate a atomSetCollection object with an array of BufferedReader.
   * 
   * <p>Given the array of BufferedReader, return an object which represents
   * the concatenation of every file contents. The parameter <code>name</code>
   * is assumed to be the  file names or URL which are the source of each
   * reader. Note that each of this 'file' may have been automatically
   * decompressed. Also note that the name may be 'String',
   * representing a string constant. Therefore, few
   * assumptions should be made about the <code>name</code> parameter.
   *
   * The return value is an object which represents a <code>atomSetCollection</code>.
   * This <code>atomSetCollection</code> will be passed back in to other methods.
   * If the return value is <code>instanceof String</code> then it is
   * considered an error condition and the returned String is the error
   * message. 
   *
   * @param fileReader  the thread requesting a set of files if bufferedReaders is null
   * @param names File names, String or URL acting as the source of each reader
   * @param types File types, if known, or null
   * @param htParams  The input parameters for each file to load
   * @param getReadersOnly 
   * @return The atomSetCollection or String with an error message
   */

  abstract public Object getAtomSetCollectionReaders(JmolFilesReaderInterface fileReader, String[] names, String[] types,
                                    Map<String, Object> htParams, boolean getReadersOnly);

  abstract public Object getAtomSetCollectionFromSet(Object readers, Object atomSets, Map<String, Object> htParams);

  abstract public Object getAtomSetCollectionOrBufferedReaderFromZip(InputStream is, String fileName, String[] zipDirectory,
                             Map<String, Object> htParams, boolean asBufferedReader, boolean asBufferendInputStream);

  /**
   * all in one -- for TestSmarterJmolAdapter
   * 
   * @param name
   * @param type
   * @param bufferedReader
   * @param htParams
   * @return           AtomSetCollection or error string
   */
  public Object getAtomSetCollectionFromReader(String name, String type,
                                               BufferedReader bufferedReader,
                                               Map<String, Object> htParams) {
    if (htParams == null)
      htParams = new Hashtable<String, Object>();
    // viewer is now needed for CIF and GenNBO file reading
    if (!htParams.containsKey("viewer"))
      htParams.put("viewer", JmolViewer.allocateViewer(null, this));
    Object a = getAtomSetCollectionReader(name, type, bufferedReader, htParams);
    if (a instanceof String)
      return a;
    return getAtomSetCollection(a);
  }


  // alternative settings, for posterity:

  public Object openBufferedReader(String name, BufferedReader bufferedReader) {
    return getAtomSetCollectionFromReader(name, null, bufferedReader, null);
  }

  public Object openBufferedReader(String name, BufferedReader bufferedReader,
                                   Map<String, Object> htParams) {
    return getAtomSetCollectionFromReader(name, null, bufferedReader, htParams);
  }

  public Object openBufferedReader(String name, String type,
                                   BufferedReader bufferedReader) {
    return getAtomSetCollectionFromReader(name, type, bufferedReader, null);
  }

  abstract public Object getAtomSetCollectionFromDOM(Object DOMNode, Map<String, Object> htParams);

  /**
   * @param atomSetCollection  
   */
  public void finish(Object atomSetCollection) {}

  /**
   * Get the type of this file or molecular model, if known.
   * @param atomSetCollection  The client file
   * @return The type of this file or molecular model, default
   *         <code>"unknown"</code>
   */
  abstract public String getFileTypeName(Object atomSetCollection);

  /**
   * Get the name of the atom set collection, if known.
   * 
   * <p>Some file formats contain a formal name of the molecule in the file.
   * If this method returns <code>null</code> then the JmolViewer will
   * automatically supply the file/URL name as a default.
   * @param atomSetCollection
   * @return The atom set collection name or <code>null</code>
   */
  abstract public String getAtomSetCollectionName(Object atomSetCollection);

  /**
   * Get the auxiliary information for this atomSetCollection.
   *
   * <p>Via the smarterJmolAdapter
   * @param atomSetCollection The client file
   * @return The auxiliaryInfo Hashtable that may be available for particular
   * filetypes for this atomSetCollection or <code>null</code>
   */
  abstract public Map<String, Object> getAtomSetCollectionAuxiliaryInfo(Object atomSetCollection);
  
  /**
   * Get number of atomSets in the file.
   *
   * <p>NOTE WARNING:
   * <br>Not yet implemented everywhere, it is in the smarterJmolAdapter
   * @param atomSetCollection The client file
   * @return The number of atomSets in the file
   */
  abstract public int getAtomSetCount(Object atomSetCollection);

  /**
   * Get the number identifying each atomSet.
   *
   * <p>For a PDB file, this is is the model number. For others it is
   * a 1-based atomSet number.
   * <p>
   * <i>Note that this is not currently implemented in PdbReader</i>
   * @param atomSetCollection The client file
   * @param atomSetIndex The atom set's index for which to get
   *                     the atom set number
   * @return The number identifying each atom set.
   */
  abstract public int getAtomSetNumber(Object atomSetCollection, int atomSetIndex);
 
  /**
   * Get the name of an atomSet.
   * 
   * @param atomSetCollection The client file
   * @param atomSetIndex The atom set index
   * @return The name of the atom set, default the string representation
   *         of atomSetIndex
   */
  abstract public String getAtomSetName(Object atomSetCollection, int atomSetIndex);

  /**
   * Get the auxiliary information for a particular atomSet.
   *
   * <p>Via the smarterJmolAdapter
   * @param atomSetCollection The client file
   * @param atomSetIndex The atom set index
   * @return The auxiliaryInfo Hashtable that may be available for particular
   * filetypes for this atomSet or <code>null</code>
   */
  abstract public Map<String, Object>  getAtomSetAuxiliaryInfo(Object atomSetCollection, int atomSetIndex);

  /**
   * Get the estimated number of atoms contained in the file.
   *
   * <p>Just return -1 if you don't know (or don't want to figure it out)
   * @param atomSetCollection The client file
   * @return The estimated number of atoms in the file
   */
  abstract public int getAtomCount(Object atomSetCollection);

  
  /**
   * Get the boolean whether coordinates are fractional.
   * @param atomSetCollection The client file
   * @return true if the coordinates are fractional, default <code>false</code>
   */
  abstract public boolean coordinatesAreFractional(Object atomSetCollection);

  /**
   * Get the notional unit cell.
   * 
   * <p>This method returns the parameters that define a crystal unitcell
   * the parameters are returned in a float[] in the following order
   * <code>a, b, c, alpha, beta, gamma</code>
   * <br><code>a, b, c</code> : angstroms
   * <br><code>alpha, beta, gamma</code> : degrees
   * <br>if there is no unit cell data then return null
   * @param atomSetCollection The client file
   * @return The array of the values or <code>null</code>
   */
  abstract public float[] getNotionalUnitcell(Object atomSetCollection);
  
  /**
   * Get the PDB scale matrix.
   * 
   * <p>Does not seem to be overriden by any descendent
   * @param atomSetCollection The client file
   * @return The array of 9 floats for the matrix or <code>null</code>
   */
  abstract public float[] getPdbScaleMatrix(Object atomSetCollection);
  
  /**
   * Get the PDB scale translation vector.
   * <p>Does not seem to be overriden by any descendent
   * @param atomSetCollection The client file
   * @return The x, y and z translation values or <code>null</code>
   */
  abstract public float[] getPdbScaleTranslate(Object atomSetCollection);

  /**
   * Get an AtomIterator for retrieval of all atoms in the file.
   * 
   * <p>This method may not return <code>null</code>.
   * @param atomSetCollection The client file
   * @return An AtomIterator
   * @see AtomIterator
   */
  abstract public AtomIterator getAtomIterator(Object atomSetCollection);
  /**
   * Get a BondIterator for retrieval of all bonds in the file.
   * 
   * <p>If this method returns <code>null</code> and no
   * bonds are defined then the JmolViewer will automatically apply its
   * rebonding code to build bonds between atoms.
   * @param atomSetCollection The client file
   * @return A BondIterator or <code>null</code>
   * @see BondIterator
   */
  abstract public BondIterator getBondIterator(Object atomSetCollection);

  /**
   * Get a StructureIterator.
   * @param atomSetCollection The client file
   * @return A StructureIterator or <code>null</code>
   */

  abstract public StructureIterator getStructureIterator(Object atomSetCollection);

  /****************************************************************
   * AtomIterator is used to enumerate all the <code>clientAtom</code>
   * objects in a specified frame. 
   * Note that Java 1.1 does not have java.util.Iterator
   * so we will define our own AtomIterator
   ****************************************************************/
  public abstract class AtomIterator {
    public abstract boolean hasNext();
    public int getAtomSetIndex() { return 0; }
    public BitSet getAtomSymmetry() { return null; }
    public int getAtomSite() { return Integer.MIN_VALUE; }
    abstract public Object getUniqueID();
    public short getElementNumber() { return -1; } // may be atomicNumber + isotopeNumber*128
    public String getAtomName() { return null; }
    public int getFormalCharge() { return 0; }
    public float getPartialCharge() { return Float.NaN; }
    public Object[] getEllipsoid() { return null; }
    public float getRadius() { return Float.NaN; }
    abstract public float getX();
    abstract public float getY();
    abstract public float getZ();
    public float getVectorX() { return Float.NaN; }
    public float getVectorY() { return Float.NaN; }
    public float getVectorZ() { return Float.NaN; }
    public float getBfactor() { return Float.NaN; }
    public int getOccupancy() { return 100; }
    public boolean getIsHetero() { return false; }
    public int getAtomSerial() { return Integer.MIN_VALUE; }
    public char getChainID() { return (char)0; }
    public char getAlternateLocationID() { return (char)0; }
    public String getGroup3() { return null; }
    public int getSequenceNumber() { return Integer.MIN_VALUE; }
    public char getInsertionCode() { return (char)0; }
  }

  /****************************************************************
   * BondIterator is used to enumerate all the bonds
   ****************************************************************/

  public abstract class BondIterator {
    public abstract boolean hasNext();
    public abstract Object getAtomUniqueID1();
    public abstract Object getAtomUniqueID2();
    public abstract int getEncodedOrder();
  }

  /****************************************************************
   * StructureIterator is used to enumerate Structures
   * Helix, Sheet, Turn
   ****************************************************************/

  public abstract class StructureIterator {
    public abstract boolean hasNext();
    public abstract int getModelIndex();
    public abstract int getStructureType();
    public abstract int getSubstructureType();
    public abstract String getStructureID();
    public abstract int getSerialID();
    public abstract int getStrandCount();
    public abstract char getStartChainID();
    public abstract int getStartSequenceNumber();
    public abstract char getStartInsertionCode();
    public abstract char getEndChainID();
    public abstract int getEndSequenceNumber();
    public abstract char getEndInsertionCode();
  }
  
  //////////////////////////////////////////////////////////////////
  // range-checking routines
  /////////////////////////////////////////////////////////////////

  public final static char canonizeAlphaDigit(char ch) {
    if ((ch >= 'A' && ch <= 'Z') ||
        (ch >= 'a' && ch <= 'z') ||
        (ch >= '0' && ch <= '9'))
      return ch;
    return '\0';
  }

  public final static char canonizeChainID(char chainID) {
    return canonizeAlphaDigit(chainID);
  }

  public final static char canonizeInsertionCode(char insertionCode) {
    return canonizeAlphaDigit(insertionCode);
  }

  public final static char canonizeAlternateLocationID(char altLoc) {
    // pdb altLoc
    return canonizeAlphaDigit(altLoc);
  }

  /**
   * @param name 
   * @param type  
   * @return Special load array
   */
  public String[] specialLoad(String name, String type) {
    return null;
  }

}
