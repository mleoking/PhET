/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-16 12:39:00 -0700 (Wed, 16 Mar 2011) $
 * $Revision: 15292 $
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import netscape.javascript.JSObject;

import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.TextFormat;
import org.jmol.util.ZipUtil;


public class Resolver {

  private final static String classBase = "org.jmol.adapter.readers.";
  private final static String[] readerSets = new String[] {
    "cifpdb.", ";Cif;Pdb;",
    "molxyz.", ";Mol;Xyz;",
    "more.", ";BinaryDcd;Gromacs;MdCrd;MdTop;Mol2;Pqr;P2n;",
    "quantum.", ";Adf;Csf;Dgrid;GamessUK;GamessUS;Gaussian;GausianWfn;Jaguar;" +
                 "Molden;MopacGraphf;GenNBO;NWChem;Odyssey;Psi;Qchem;Spartan;SpartanSmol;" +
                 "WebMO;",
    "simple.", ";Alchemy;Ampac;Cube;FoldingXyz;GhemicalMM;HyperChem;Jme;Mopac;", 
    "xtal.", ";Aims;Castep;Crystal;Espresso;Gulp;Shelx;VaspOutcar;Wien2k;"
  };
  
  public final static String getReaderClassBase(String type) {
    String name = type + "Reader";
    if (type.startsWith("Xml"))
      return classBase + "xml." + name;
    String key = ";" + type + ";";
    for (int i = 1; i < readerSets.length; i += 2)
      if (readerSets[i].indexOf(key) >= 0)
        return classBase + readerSets[i - 1] + name;
    return classBase + "???." + name;
  }
  
  /**
   * From SmarterJmolAdapter.getFileTypeName(Object atomSetCollectionOrReader)
   * just return the file type with no exception issues
   * 
   * @param br
   * @return String file type
   */
  static String getFileType(BufferedReader br) {
    try {
      return determineAtomSetCollectionReader(br, false);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 
   * Special loading for file directories. This method is called from
   * the FileManager via SmarterJmolAdapter. It's here because Resolver 
   * is the place where all distinctions are made.
   * 
   * In the case of spt files, no need to load them; here we are just checking
   * for type.
   * 
   * In the case of .spardir directories, we need to provide a list of
   * the critical files that need loading and concatenation for the
   * SpartanSmolReader. 
   * 
   * we return an array for which:
   * 
   * [0] file type (class prefix) or null for SPT file 
   * [1] header to add for each BEGIN/END block (ignored)
   * [2...] files to load and concatenate
   * 
   * @param name
   * @param type
   * @return array detailing action for this set of files
   */
  static String[] specialLoad(String name, String type) {
    int pt = name.lastIndexOf(".spardir");
    boolean isPreliminary = (type.equals("filesNeeded?"));
    if (isPreliminary) {
      // check for .spt file type -- Jmol script
      if (name.endsWith(".spt"))
        return new String[] { null, null, null }; // DO NOT actually load any file
      // check for zipped up spardir -- we'll automatically take first file there
      if (name.endsWith(".spardir.zip"))
        return new String[] { "SpartanSmol", "Directory Entry ", "?|output"};
      name = name.replace('\\', '/');
      if (!name.endsWith(".spardir") && name.indexOf(".spardir/") < 0)
        return null; 
      // look for .spardir or .spardir/...
      if (pt < 0)
        return null;
      if (name.lastIndexOf("/") > pt) {
        // a single file in the spardir directory is requested
        return new String[] { "SpartanSmol", "Directory Entry ",
            name + "/input", name + "/archive",
            name + "/Molecule:asBinaryString", name + "/proparc" };      
      }
      return new String[] { "SpartanSmol", "Directory Entry ", name + "/output" };
    }
    // make list of required files
    String[] dirNums = getSpartanDirs(type);
    if (dirNums.length == 0 && name.endsWith(".spardir.zip") 
        && type.indexOf(".zip|output") >= 0) {
      // try again, with the idea that 
      String sname = name.replace('\\','/');
      pt = sname.lastIndexOf("/");
      // mac directory zipped up?
      sname = name + "|" + name.substring(pt + 1, name.length() - 4);
      return new String[] { "SpartanSmol", sname, sname + "/output" };
    }    
    return getSpartanFileList(name, dirNums);
  }

  /**
   * called by SmarterJmolAdapter to see if we can automatically assign a file
   * from the zip file. If so, return a subfile list for this file. The first
   * element of the list is left empty -- it would be the zipfile name. 
   * 
   * Assignment can be made if (1) there is only one file in the collection or
   * (2) if the first file is xxxx.spardir/
   * 
   * Note that __MACOS? files are ignored by the ZIP file reader.
   * 
   * @param zipDirectory
   * @return subFileList
   */
  static String[] checkSpecialInZip(String[] zipDirectory) {
    String name;
    return (zipDirectory.length < 2 ? null 
        : (name = zipDirectory[1]).endsWith(".spardir/") || zipDirectory.length == 2 ?
        new String[] { "",
          (name.endsWith("/") ? name.substring(0, name.length() - 1) : name) } 
        : null);
  }

  /**
   * called by SmarterJmolAdapter to see if we have a Spartan directory and, if so,
   * open it and get all the data into the correct order.
   * 
   * @param is
   * @param zipDirectory
   * @return String data for processing
   */
  static StringBuffer checkSpecialData(InputStream is, String[] zipDirectory) {
    boolean isSpartan = false;
    // 0 entry is not used here
    for (int i = 1; i < zipDirectory.length; i++) {
      if (zipDirectory[i].endsWith(".spardir/")
          || zipDirectory[i].indexOf("_spartandir") >= 0) {
        isSpartan = true;
        break;
      }
    }
    if (!isSpartan)
      return null;
    StringBuffer data = new StringBuffer();
    data.append("Zip File Directory: ").append("\n").append(
        Escape.escape(zipDirectory, true)).append("\n");
    Map<String, String> fileData = new Hashtable<String, String>();
    ZipUtil.getAllData(is, new String[] {}, "",
        "Molecule", fileData);
    String prefix = "|";
    String outputData = fileData.get(prefix + "output");
    if (outputData == null)
      outputData = fileData.get((prefix = "|" + zipDirectory[1]) + "output");
    data.append(outputData);
    String[] files = getSpartanFileList(prefix, getSpartanDirs(outputData));
    for (int i = 2; i < files.length; i++) {
      String name = files[i];
      if (fileData.containsKey(name))
        data.append(fileData.get(name));
      else
        data.append(name + "\n");
    }
    return data;
  }

  /**
   * the main method for reading files. Called from SmarterJmolAdapter when
   * reading a file, reading a set of files, or reading a ZIP file
   * 
   * @param fullName
   * @param type
   * @param bufferedReader
   * @param htParams
   * @param ptFile
   * @return an AtomSetCollection or a String error
   * @throws Exception
   */
  static Object getAtomCollectionReader(String fullName, String type,
                                        BufferedReader bufferedReader,
                                        Map<String, Object> htParams, int ptFile)
      throws Exception {
    AtomSetCollectionReader atomSetCollectionReader = null;
    String readerName;
    fullName = fullName.replace('\\', '/');
    String errMsg = null;
    if (type != null) {
      readerName = getReaderFromType(type);
      if (readerName == null)
        errMsg = "unrecognized file format type " + type;
      else
        Logger.info("The Resolver assumes " + readerName);
    } else {
      readerName = determineAtomSetCollectionReader(bufferedReader, true);
      if (readerName.charAt(0) == '\n') {
        type = (String) htParams.get("defaultType");
        if (type != null) {
          // allow for MDTOP to specify default MDCRD
          type = getReaderFromType(type);
          if (type != null)
            readerName = type;
        }
      }
      if (readerName.charAt(0) == '\n')
        errMsg = "unrecognized file format for file " + fullName + "\n"
            + readerName;
      else if (readerName.equals("spt"))
        errMsg = "NOTE: file recognized as a script file: " + fullName + "\n";
      else
        Logger.info("The Resolver thinks " + readerName);
    }
    if (errMsg != null) {
      bufferedReader.close();
      return errMsg;
    }
    if (htParams == null)
      htParams = new Hashtable<String, Object>();
    htParams.put("ptFile", Integer.valueOf(ptFile));
    if (ptFile <= 0)
      htParams.put("readerName", readerName);
    if (readerName.indexOf("Xml") == 0)
      readerName = "Xml";
    String className = null;
    Class<?> atomSetCollectionReaderClass;
    String err = null;
    try {
      try {
        className = getReaderClassBase(readerName);
        atomSetCollectionReaderClass = Class.forName(className);
        atomSetCollectionReader = (AtomSetCollectionReader) atomSetCollectionReaderClass
            .newInstance();
      } catch (Exception e) {
        err = "File reader was not found:" + readerName;
        Logger.error(err);
        return err;
      }
      return atomSetCollectionReader;
    } catch (Exception e) {
      err = "uncaught error in file loading for " + className;
      Logger.error(err);
      e.printStackTrace();
      return err;
    }
  }

  /**
   * a largely untested reader of the DOM - where in a browser there
   * is model actually in XML format already present on the page.
   * -- Egon Willighagen
   * 
   * @param DOMNode
   * @param htParams 
   * @return an AtomSetCollection or a String error
   * @throws Exception
   */
  static Object DOMResolve(Object DOMNode, Map<String, Object> htParams) throws Exception {
    String className = null;
    Class<?> atomSetCollectionReaderClass;
    AtomSetCollectionReader atomSetCollectionReader; 
    String atomSetCollectionReaderName = getXmlType((JSObject) DOMNode);
    if (Logger.debugging) {
      Logger.debug("The Resolver thinks " + atomSetCollectionReaderName);
    }
    htParams.put("readerName", atomSetCollectionReaderName);
    try {
      className = classBase + "xml.XmlReader";
      atomSetCollectionReaderClass = Class.forName(className);
      atomSetCollectionReader = (AtomSetCollectionReader) atomSetCollectionReaderClass.newInstance();
      return atomSetCollectionReader;
    } catch (Exception e) {
      String err = "File reader was not found:" + className;
      Logger.error(err, e);
      return err;
    }
  }

  /**
   * returns the list of files to read for every Spartan spardir. Simple numbers
   * are assumed to be Profiles; others are models.
   * 
   * @param name
   * @param dirNums
   * @return String[] list of files to read given a list of directory names
   * 
   */
  private static String[] getSpartanFileList(String name, String[] dirNums) {    
    String[] files = new String[2 + dirNums.length*5];
    files[0] = "SpartanSmol";
    files[1] = "Directory Entry ";
    int pt = 2;
    name = name.replace('\\', '/');
    if (name.endsWith("/"))
      name = name.substring(0, name.length() - 1);
    for (int i = 0; i < dirNums.length; i++) {
      String path = name + (Character.isDigit(dirNums[i].charAt(0)) ? 
          "/Profile." + dirNums[i] : "/" + dirNums[i]);
      files[pt++] = path + "/#JMOL_MODEL " + dirNums[i];
      files[pt++] = path + "/input";
      files[pt++] = path + "/archive";
      files[pt++] = path + "/Molecule:asBinaryString";
      files[pt++] = path + "/proparc";
    }
    return files;
  }

  /**
   * read the output file from the Spartan directory and decide from that what
   * files need to be read and in what order - usually M0001 or a set of Profiles.
   * But Spartan saves the Profiles in alphabetical order, not numerical. So we
   * fix that here.
   * 
   * @param outputFileData
   * @return String[] list of files to read
   */
  private static String[] getSpartanDirs(String outputFileData) {
    if (outputFileData == null)
      return new String[]{};
    if (outputFileData.startsWith("java.io.FileNotFoundException")
        || outputFileData.startsWith("FILE NOT FOUND")
        || outputFileData.indexOf("<html") >= 0)
      return new String[] { "M0001" };
    List<String> v = new ArrayList<String>();
    String token;
    String lasttoken = "";
    try {
      StringTokenizer tokens = new StringTokenizer(outputFileData, " \t\r\n");
      while (tokens.hasMoreTokens()) {
        // profile file name is just before each right-paren:
        /*
         * MacSPARTAN '08 ENERGY PROFILE: x86/Darwin 130
         * 
         * Dihedral Move : C3 - C2 - C1 - O1 [ 4] -180.000000 .. 180.000000
         * Dihedral Move : C2 - C1 - O1 - H3 [ 4] -180.000000 .. 180.000000
         * 
         * 1 ) -180.00 -180.00 -504208.11982719 2 ) -90.00 -180.00
         * -504200.18593376
         * 
         * ...
         * 
         * 24 ) 90.00 180.00 -504200.18564495 25 ) 180.00 180.00
         * -504208.12129747
         * 
         * Found a local maxima E = -504178.25455465 [ 3 3 ]
         * 
         * 
         * Reason for exit: Successful completion Mechanics CPU Time : 1:51.42
         * Mechanics Wall Time: 12:31.54
         */
        if ((token = tokens.nextToken()).equals(")"))
          v.add(lasttoken);
        else if (token.equals("Start-") && tokens.nextToken().equals("Molecule"))
          v.add(TextFormat.split(tokens.nextToken(), '"')[1]);
        lasttoken = token;
      }
    } catch (Exception e) {
      //
    }
    String[] dirs = new String[v.size()];
    for (int i = 0; i < v.size(); i++)
      dirs[i] = v.get(i);
    return dirs;
  }
  
  private static final String CML_NAMESPACE_URI = "http://www.xml-cml.org/schema";

  private static String getXmlType(JSObject DOMNode) {
    String namespaceURI = (String) DOMNode.getMember("namespaceURI");
    String localName = (String) DOMNode.getMember("localName");
    if (namespaceURI.startsWith("http://www.molpro.net/"))
      return specialTags[SPECIAL_MOLPRO_DOM][0];
    if ("odyssey_simulation".equals(localName))
      return specialTags[SPECIAL_ODYSSEY_DOM][0];
    if ("arguslab".equals(localName))
      return specialTags[SPECIAL_ARGUS_DOM][0];
    if (namespaceURI.startsWith(CML_NAMESPACE_URI) || "cml".equals(localName))
      return specialTags[SPECIAL_CML_DOM][0];
    return specialTags[SPECIAL_CML_DOM][0] + "(unidentified)";
  }


  /**
   * the main resolver method. One of the great advantages of Jmol is that it can
   * smartly determine a file type from its contents. In cases where this is not possible,
   * one can force a file type using a prefix to a filename. For example:
   * 
   * load mol2::xxxx.whatever
   * 
   * This is only necessary for a few file types, where only numbers are involved --
   * molecular dynamics coordinate files, for instance (mdcrd).
   * 
   * We must do this in a very specific order. DON'T MESS WITH THIS!
   * 
   * @param bufferedReader
   * @param returnLines
   * @return readerName or a few lines, if requested, or null
   * @throws Exception
   */
  private static String determineAtomSetCollectionReader(BufferedReader bufferedReader, boolean returnLines)
      throws Exception {
    LimitedLineReader llr = new LimitedLineReader(bufferedReader, 16384);
    
    // first check just the first 10 bytes
    
    String leader = llr.getHeader(LEADER_CHAR_MAX);

    for (int i = 0; i < fileStartsWithRecords.length; ++i) {
      String[] recordTags = fileStartsWithRecords[i];
      for (int j = 1; j < recordTags.length; ++j) {
        String recordTag = recordTags[j];
        if (leader.startsWith(recordTag))
          return recordTags[0];
      }
    }

    // PNG or BCD-encoded JPG or JPEG
    if (leader.indexOf("PNG") == 1 || leader.indexOf("JPG") == 1
        || leader.indexOf("JFIF") == 6)
      return "spt"; // presume embedded script --- allows dragging into Jmol

    // now allow identification in first 16 lines
    // excluding those starting with "#"
    
    String[] lines = new String[16];
    int nLines = 0;
    for (int i = 0; i < lines.length; ++i) {
      lines[i] = llr.readLineWithNewline();
      if (lines[i].length() > 0)
        nLines++;
    }

    String readerName = checkSpecial(nLines, lines, false);
    
    if (readerName != null)
      return readerName;

    for (int i = 0; i < lineStartsWithRecords.length; ++i) {
      String[] recordTags = lineStartsWithRecords[i];
      for (int j = 1; j < recordTags.length; ++j) {
        String recordTag = recordTags[j];
        for (int k = 0; k < lines.length; ++k) {
          if (lines[k].startsWith(recordTag))
            return recordTags[0];
        }
      }
    }

    String header = llr.getHeader(0);
    String type = null;
    for (int i = 0; i < containsRecords.length; ++i) {
      String[] recordTags = containsRecords[i];
      for (int j = 1; j < recordTags.length; ++j) {
        String recordTag = recordTags[j];
        if (header.indexOf(recordTag) != -1) {
          type = recordTags[0];
          if (type.equals("Xml")) {
            // check for an error message from a server -- certainly not XML
            // but new CML format includes xmlns:xhtml="http://www.w3.org/1999/xhtml" in <cml> tag.
            if (header.indexOf("XHTML") >= 0 || header.indexOf("xhtml") >= 0 && header.indexOf("<cml") < 0)
              break; 
            type = getXmlType(header);
          }
          return type;
        }
      }
    }
    
    readerName = checkSpecial(nLines, lines, true);
    
    if (readerName != null)
      return readerName;
    
    return (returnLines ? "\n" + lines[0] + "\n" + lines[1] + "\n" + lines[2] + "\n" : null);
  }

  private static String getXmlType(String header) throws Exception  {
    if (header.indexOf("http://www.molpro.net/") >= 0) {
      return specialTags[SPECIAL_MOLPRO_XML][0];
    }
    if (header.indexOf("odyssey") >= 0) {
      return specialTags[SPECIAL_ODYSSEY_XML][0];
    }
    if (header.indexOf("C3XML") >= 0) {
      return specialTags[SPECIAL_CHEM3D_XML][0];
    }
    if (header.indexOf("arguslab") >= 0) {
      return specialTags[SPECIAL_ARGUS_XML][0];
    }
    if (header.indexOf("jvxl") >= 0) {
      return specialTags[SPECIAL_CML_XML][0];
    }
    if (header.indexOf(CML_NAMESPACE_URI) >= 0
        || header.indexOf("cml:") >= 0) {
      return specialTags[SPECIAL_CML_XML][0];
    }
    if (header.indexOf("XSD") >= 0) {
      return specialTags[SPECIAL_XSD_XML][0];
    }
    if (header.indexOf(">vasp") >= 0) {
      return specialTags[SPECIAL_VASP_XML][0];
    }
    
    return specialTags[SPECIAL_CML_XML][0] + "(unidentified)";
  }

  private final static int SPECIAL_JME                = 0;
  private final static int SPECIAL_MOPACGRAPHF        = 1;
//  private final static int SPECIAL_V3000              = 2; // implemented in MOL
  private final static int SPECIAL_ODYSSEY            = 3;
  private final static int SPECIAL_MOL                = 4;
  private final static int SPECIAL_XYZ                = 5;
  private final static int SPECIAL_FOLDINGXYZ         = 6;
  private final static int SPECIAL_CUBE               = 7;
  private final static int SPECIAL_ALCHEMY            = 8;
  private final static int SPECIAL_WIEN               = 9;
  private final static int SPECIAL_CASTEP             = 10;
  private final static int SPECIAL_AIMS               = 11;
  private final static int SPECIAL_CRYSTAL            = 12;
  private final static int SPECIAL_VASP               = 13;
  private final static int SPECIAL_GULP               = 14;
  private final static int SPECIAL_ESPRESSO           = 15;
  private final static int SPECIAL_GROMACS            = 16;
  private final static int SPECIAL_GENNBO             = 17;
  
  // these next are needed by the XML reader
  
  public final static int SPECIAL_ARGUS_XML   = 18;
  public final static int SPECIAL_CML_XML     = 19;
  public final static int SPECIAL_CHEM3D_XML  = 20;
  public final static int SPECIAL_MOLPRO_XML  = 21;
  public final static int SPECIAL_ODYSSEY_XML = 22;
  public final static int SPECIAL_XSD_XML     = 23;
  public final static int SPECIAL_VASP_XML    = 24; 
  
  public final static int SPECIAL_ARGUS_DOM   = 25;
  public final static int SPECIAL_CML_DOM     = 26;
  public final static int SPECIAL_CHEM3D_DOM  = 27;
  public final static int SPECIAL_MOLPRO_DOM  = 28;
  public final static int SPECIAL_ODYSSEY_DOM = 29;
  public final static int SPECIAL_XSD_DOM     = 30; // not implemented
  public final static int SPECIAL_VASP_DOM    = 31; 
  
  public final static String[][] specialTags = {
    { "Jme" },
    { "MopacGraphf" },
    { "V3000" },
    { "Odyssey" },
    { "Mol" },
    
    { "Xyz" },
    { "FoldingXyz" },
    { "Cube" },
    { "Alchemy" },
    { "Wien2k" },
    
    { "Castep" },
    { "Aims" },  
    { "Crystal" },  
    { "VaspOutcar" },
    { "Gulp"  },
    { "Espresso" },
    { "Gromacs" },
    { "GenNBO" },
    
    { "XmlArgus" }, 
    { "XmlCml" },
    { "XmlChem3d" },
    { "XmlMolpro" },
    { "XmlOdyssey" },
    { "XmlXsd" },
    { "XmlVasp" },

    { "XmlArgus(DOM)" }, //19
    { "XmlCml(DOM)" },
    { "XmlChem3d(DOM)" },
    { "XmlMolpro(DOM)" },
    { "XmlOdyssey(DOM)" },
    { "XmlXsd(DOM)" },
    { "XmlVasp(DOM)" },

    { "MdCrd" }

  };

  private final static String getReaderFromType(String type) {
    type = type.toLowerCase();
    String base = null;
    if ((base = checkType(specialTags, type)) != null)
      return base;
    if ((base = checkType(fileStartsWithRecords, type)) != null)
      return base;
    if ((base = checkType(lineStartsWithRecords, type)) != null)
      return base;
    return checkType(containsRecords, type);
  }
  
  private final static String checkType(String[][] typeTags, String type) {
    for (int i = 0; i < typeTags.length; ++i)
      if (typeTags[i][0].toLowerCase().equals(type))
        return typeTags[i][0];
    return null;
  }
  
  ////////////////////////////////////////////////////////////////
  // file types that need special treatment
  ////////////////////////////////////////////////////////////////

  private final static String checkSpecial(int nLines, String[] lines,
                                           boolean isEnd) {
    // the order here is CRITICAL
    
    if (isEnd) {
      if (checkGromacs(lines))
        return specialTags[SPECIAL_GROMACS][0];
      if (checkCrystal(lines))
        return specialTags[SPECIAL_CRYSTAL][0];
      if (checkVasp(lines))
        return specialTags[SPECIAL_VASP][0];
      if (checkGulp(lines))
        return specialTags[SPECIAL_GULP][0];
      if (checkQuantumEspresso(lines))
        return specialTags[SPECIAL_ESPRESSO][0];
    } else {
      if (nLines == 1 && lines[0].length() > 0
          && Character.isDigit(lines[0].charAt(0)))
        return specialTags[SPECIAL_JME][0]; //only one line, and that line starts with a number 
      if (checkMopacGraphf(lines))
        return specialTags[SPECIAL_MOPACGRAPHF][0]; //must be prior to checkFoldingXyz and checkMol
      if (checkOdyssey(lines))
        return specialTags[SPECIAL_ODYSSEY][0];
      if (checkMol(lines))
        return specialTags[SPECIAL_MOL][0];
      if (checkXyz(lines))
        return specialTags[SPECIAL_XYZ][0];
      if (checkAlchemy(lines[0]))
        return specialTags[SPECIAL_ALCHEMY][0];
      if (checkFoldingXyz(lines))
        return specialTags[SPECIAL_FOLDINGXYZ][0];
      if (checkCube(lines))
        return specialTags[SPECIAL_CUBE][0];
      if (checkWien2k(lines))
        return specialTags[SPECIAL_WIEN][0];
      if (checkCastep(lines))
        return specialTags[SPECIAL_CASTEP][0];
      if (checkAims(lines))
        return specialTags[SPECIAL_AIMS][0];
      if (checkGenNBO(lines))
        return specialTags[SPECIAL_GENNBO][0];
    }
    return null;
  }
  
  private static boolean checkAims(String[] lines) {

    // use same tokenizing mechanism as in AimsReader.java to also recognize
    // AIMS geometry files with indented keywords
    // use same tokenizing mechanism as in AimsReader.java 
    //  to reliably recognize FHI-aims files
    // "atom" is a VERY generic term; just "atom" breaks HIN reader. 
    // >= token.length are necessary to allow for comments at the end of valid lines
    //  (as perfectly legal in simple Fortran list based IO) 
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].startsWith("mol 1"))
        return false;  /* hin format also uses "atom " */
      String[] tokens = Parser.getTokens(lines[i]);
      if (tokens.length == 0)
        continue;
      if (tokens[0].startsWith("atom") && tokens.length >= 5
          || tokens[0].startsWith("multipole") && tokens.length >= 6
          || tokens[0].startsWith("lattice_vector") && tokens.length >= 4)
        return true;
    }
    return false;
  }

  private static boolean checkAlchemy(String line) {
    /*
    11 ATOMS,    12 BONDS,     0 CHARGES
    */
    int pt;
    if ((pt = line.indexOf("ATOMS")) >= 0 && line.indexOf("BONDS") > pt)
      try {
        int n = Integer.parseInt(line.substring(0, pt).trim());
        return (n > 0);
      } catch (NumberFormatException nfe) {
        // ignore
      }
    return false;
  }

  private static boolean checkCastep(String[] lines) {
    for ( int i = 0; i<lines.length; i++ ) {
      if ( lines[i].toUpperCase().startsWith("%BLOCK LATTICE_ABC") ) return true;
      if ( lines[i].toUpperCase().startsWith("%BLOCK LATTICE_CART") ) return true;
      if ( lines[i].toUpperCase().startsWith("%BLOCK POSITIONS_FRAC") ) return true;
      if ( lines[i].toUpperCase().startsWith("%BLOCK POSITIONS_ABS") ) return true;
    }
    return false;
  }

  private static boolean checkCrystal(String[] lines) {
    String s = lines[1].trim();
    if (s.equals("SLAB") ||s.equals("MOLECULE")
        || s.equals("CRYSTAL")
        || s.equals("POLYMER") || (s = lines[3]).equals("SLAB")
        || s.equals("MOLECULE") || s.equals("POLYMER"))
      return true;
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].trim().equals("OPTGEOM"))
        return true;
    }
    return false;
  }
  
  private static boolean checkCube(String[] lines) {
    try {
      StringTokenizer tokens2 = new StringTokenizer(lines[2]);
      if (tokens2 == null || tokens2.countTokens() != 4)
        return false;
      Integer.parseInt(tokens2.nextToken());
      for (int i = 3; --i >= 0; )
        new Float(tokens2.nextToken());
      StringTokenizer tokens3 = new StringTokenizer(lines[3]);
      if (tokens3 == null || tokens3.countTokens() != 4)
        return false;
      Integer.parseInt(tokens3.nextToken());
      for (int i = 3; --i >= 0; )
        if ((new Float(tokens3.nextToken())).floatValue() < 0)
          return false;
      return true;
    } catch (NumberFormatException nfe) {
    }
    return false;
  }

  /**
   * @param lines First lines of the files.
   * @return Indicates if the file may be a Folding@Home file.
   */
  private static boolean checkFoldingXyz(String[] lines) {
    // Checking first line: <number of atoms> <protein name>
    StringTokenizer tokens = new StringTokenizer(lines[0].trim(), " \t");
    if (tokens.countTokens() < 2)
      return false;
    try {
      Integer.parseInt(tokens.nextToken().trim());
    } catch (NumberFormatException nfe) {
      return false;
    }
    
    // Checking second line: <atom number> ...
    String secondLine = lines[1].trim();
    if (secondLine.length() == 0)
        secondLine = lines[2].trim();
    tokens = new StringTokenizer(secondLine, " \t");
    if (tokens.countTokens() == 0)
      return false;
    try {
      Integer.parseInt(tokens.nextToken().trim());
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }
  
  private static boolean checkGenNBO(String[] lines) {
    // .31-.41 file or .nbo file
    return (lines[1].startsWith(" Basis set information needed for plotting orbitals")
      || lines[1].indexOf("s in the AO basis:") >= 0
      || lines[2].indexOf(" N A T U R A L   A T O M I C   O R B I T A L") >= 0);
  }
  
  private static boolean checkGromacs(String[] lines) {
    if (Parser.parseInt(lines[1]) == Integer.MIN_VALUE)
      return false;
    int len = -1;
    for (int i = 2; i < 16 && len != 0; i++)
      if ((len = lines[i].length()) != 69 && len != 45 && len != 0)
        return false;
    return true;
  }
  
  private static boolean checkGulp(String[] lines) {

    if (lines[2]
              .startsWith("*                       GENERAL UTILITY LATTICE PROGRAM      "))
      return true;
    for (int i = 0; i < lines.length; i++) {
      if (lines[i]
                .startsWith("*                       GENERAL UTILITY LATTICE PROGRAM      ")) {
        return true;
      } else if (lines[i].contains("Julian Gale")) {
        return true;
      }
    }
    return false;
  }

  private static boolean checkMol(String[] lines) {
    String line4trimmed = ("X" + lines[3]).trim().toUpperCase();
    if (line4trimmed.length() < 7 || line4trimmed.indexOf(".") >= 0)
      return false;
    if (line4trimmed.endsWith("V2000") || line4trimmed.endsWith("V3000"))
      return true;
    try {
      int n1 = Integer.parseInt(lines[3].substring(0, 3).trim());
      int n2 = Integer.parseInt(lines[3].substring(3, 6).trim());
      return (n1 > 0 && n2 >= 0 && lines[0].indexOf("@<TRIPOS>") != 0
          && lines[1].indexOf("@<TRIPOS>") != 0 
          && lines[2].indexOf("@<TRIPOS>") != 0);
    } catch (NumberFormatException nfe) {
    }
    return false;
  }

  /**
   * @param lines First lines of the files.
   * @return Indicates if the file is a Mopac GRAPHF output file.
   */
  
  private static boolean checkMopacGraphf(String[] lines) {
    return (lines[0].indexOf("MOPAC-Graphical data") > 2); //nAtoms MOPAC-Graphical data
  }

  private static boolean checkOdyssey(String[] lines) {
    int i;
    for (i = 0; i < lines.length; i++)
      if (!lines[i].startsWith("C ") && lines[i].length() != 0)
        break;
    if (i >= lines.length 
        || lines[i].charAt(0) != ' ' 
        || (i = i + 2) + 1 >= lines.length)
      return false;
    try {
      // distinguishing between Spartan input and MOL file
      // MOL files have aaabbb.... on the data line
      // SPIN files have cc s on that line (c = charge; s = spin)
      // so the typical MOL file, with more parameters, will fail getting the spin
      int spin = Integer.parseInt(lines[i].substring(2).trim());
      int charge = Integer.parseInt(lines[i].substring(0, 2).trim());
      // and if it does not, then we get the next lines of info
      int atom1 = Integer.parseInt(lines[++i].substring(0, 2).trim());
      if (spin < 0 || spin > 5 || atom1 <= 0 || charge > 5)
        return false;
      // hard to believe we would get here for a MOL file
      float[] atomline = new float[5];
      AtomSetCollectionReader.getTokensFloat(lines[i], atomline, 5);
      return !Float.isNaN(atomline[1]) && !Float.isNaN(atomline[2]) && !Float.isNaN(atomline[3]) && Float.isNaN(atomline[4]);
    } catch (Exception e) {
    }
    return false;
  }
  
  private static boolean checkQuantumEspresso(String[] lines) {
    for (int i = 0; i < lines.length; i++) {
      if (lines[i].contains("Program PWSCF"))
        return true;
      if (lines[i].contains("Program PHONON")) 
        return true;
    }
    return false;
  }
  
  private static boolean checkVasp(String[] lines){
    if(lines[1].startsWith(" vasp."))
      return true;
    for(int i =0 ; i < lines.length; i++){
      if(lines[i].startsWith(" INCAR:"))
          return true;
    }
    return false;
  }

 private static boolean checkWien2k(String[] lines) {
   return (lines[2].startsWith("MODE OF CALC=") 
       || lines[2].startsWith("             RELA")
       || lines[2].startsWith("             NREL"));
 }
 
  private static boolean checkXyz(String[] lines) {
    try {
      Integer.parseInt(lines[0].trim());
      return true;
    } catch (NumberFormatException nfe) {
    }
    return false;
  }


/*
  private void dumpLines(String[] lines) {
      for (int i = 0; i < lines.length; i++) {
        Logger.info("\nLine "+i + " len " + lines[i].length());
        for (int j = 0; j < lines[i].length(); j++)
          Logger.info("\t"+(int)lines[i].charAt(j));
      }
      Logger.info("");
  }

*/
  
  ////////////////////////////////////////////////////////////////
  // these test files that startWith one of these strings
  ////////////////////////////////////////////////////////////////

  private final static int LEADER_CHAR_MAX = 10;
  
  private final static String[] cubeFileStartRecords =
  {"Cube", "JVXL", "#JVXL"};

  private final static String[] mol2Records =
  {"Mol2", "mol2", "@<TRIPOS>"};

  private final static String[] webmoFileStartRecords =
  {"WebMO", "[HEADER]"};
  
  private final static String[] moldenFileStartRecords =
  {"Molden", "[Molden"};

  private final static String[] dcdFileStartRecords =
  {"BinaryDcd", "T\0\0\0CORD", "\0\0\0TCORD"};

  private final static String[][] fileStartsWithRecords =
  { cubeFileStartRecords, mol2Records, webmoFileStartRecords, moldenFileStartRecords, dcdFileStartRecords };

  ////////////////////////////////////////////////////////////////
  // these test lines that startWith one of these strings
  ////////////////////////////////////////////////////////////////

  private final static String[] pqrLineStartRecords = 
  { "Pqr", "REMARK   1 PQR" };

  private final static String[] p2nLineStartRecords = 
  { "P2n", "REMARK   1 P2N" };

  private final static String[] pdbLineStartRecords = {
    "Pdb", "HEADER", "OBSLTE", "TITLE ", "CAVEAT", "COMPND", "SOURCE", "KEYWDS",
    "EXPDTA", "AUTHOR", "REVDAT", "SPRSDE", "JRNL  ", "REMARK ",
    "DBREF ", "SEQADV", "SEQRES", "MODRES", 
    "HELIX ", "SHEET ", "TURN  ",
    "CRYST1", "ORIGX1", "ORIGX2", "ORIGX3", "SCALE1", "SCALE2", "SCALE3",
    "ATOM  ", "HETATM", "MODEL ",
  };

  private final static String[] shelxLineStartRecords =
  { "Shelx", "TITL ", "ZERR ", "LATT ", "SYMM ", "CELL " };

  private final static String[] cifLineStartRecords =
  { "Cif", "data_", "_publ" };

  private final static String[] ghemicalMMLineStartRecords =
  { "GhemicalMM", "!Header mm1gp", "!Header gpr" };

  private final static String[] jaguarLineStartRecords =
  { "Jaguar", "  |  Jaguar version", };

  private final static String[] mdlLineStartRecords = 
  { "Mol", "$MDL " };

  private final static String[] spartanSmolLineStartRecords =
  { "SpartanSmol", "INPUT=" };

  private final static String[] csfLineStartRecords =
  { "Csf", "local_transform" };
  
  private final static String[] mdTopLineStartRecords =
  { "MdTop", "%FLAG TITLE" };
  
  private final static String[] hyperChemLineStartRecords = 
  { "HyperChem", "mol 1" };
  
  private final static String[][] lineStartsWithRecords =
  { cifLineStartRecords, pqrLineStartRecords, p2nLineStartRecords,
    pdbLineStartRecords, shelxLineStartRecords, 
    ghemicalMMLineStartRecords, jaguarLineStartRecords, 
    mdlLineStartRecords, spartanSmolLineStartRecords, csfLineStartRecords, 
    mol2Records, mdTopLineStartRecords, hyperChemLineStartRecords };

  ////////////////////////////////////////////////////////////////
  // contains formats
  ////////////////////////////////////////////////////////////////

  private final static String[] sptContainsRecords = 
  { "spt", "# Jmol state" };
  
  private final static String[] xmlContainsRecords = 
  { "Xml", "<?xml", "<atom", "<molecule", "<reaction", "<cml", "<bond", ".dtd\"",
    "<list>", "<entry", "<identifier", "http://www.xml-cml.org/schema/cml2/core" };

  private final static String[] gaussianContainsRecords =
  { "Gaussian", "Entering Gaussian System", "Entering Link 1", "1998 Gaussian, Inc." };

  /*
  private final static String[] gaussianWfnRecords =
  { "GaussianWfn", "MO ORBITALS" };
  */

  private final static String[] ampacContainsRecords =
  { "Ampac", "AMPAC Version" };
  
  private final static String[] mopacContainsRecords =
  { "Mopac", "MOPAC 93 (c) Fujitsu", "MOPAC2002 (c) Fujitsu",
    "MOPAC FOR LINUX (PUBLIC DOMAIN VERSION), ",
    "MOPAC:  VERSION  6", "MOPAC   7", "MOPAC2009" };

  private final static String[] qchemContainsRecords = 
  { "Qchem", "Welcome to Q-Chem", "A Quantum Leap Into The Future Of Chemistry" };

  private final static String[] gamessUKContainsRecords =
  { "GamessUK", "GAMESS-UK", "G A M E S S - U K" };

  private final static String[] gamessUSContainsRecords =
  { "GamessUS", "GAMESS" };

  private final static String[] spartanBinaryContainsRecords =
  { "SpartanSmol" , "|PropertyArchive", "_spartan", "spardir", "BEGIN Directory Entry Molecule" };

  private final static String[] spartanContainsRecords =
  { "Spartan", "Spartan" };  // very old Spartan files?

  private final static String[] adfContainsRecords =
  { "Adf", "Amsterdam Density Functional" };
  
  private final static String[] dgridContainsRecords =
  { "Dgrid", "BASISFILE   created by DGrid" };
  
  private final static String[] psiContainsRecords =
  { "Psi", "    PSI  3"};
 
  private final static String[] nwchemContainsRecords =
  { "NWChem", " argument  1 = "};

  private final static String[] uicrcifContainsRecords =
  { "Cif", "Crystallographic Information File"};
  
  private final static String[] crystalContainsRecords =
  { "Crystal", "*                                CRYSTAL"};

  private final static String[][] containsRecords =
  { sptContainsRecords, xmlContainsRecords, gaussianContainsRecords, 
    ampacContainsRecords, mopacContainsRecords, qchemContainsRecords, 
    gamessUKContainsRecords, gamessUSContainsRecords,
    spartanBinaryContainsRecords, spartanContainsRecords, mol2Records, adfContainsRecords, psiContainsRecords,
    nwchemContainsRecords, uicrcifContainsRecords, dgridContainsRecords, crystalContainsRecords
  };
}

class LimitedLineReader {
  private char[] buf;
  private int cchBuf;
  private int ichCurrent;

  LimitedLineReader(BufferedReader bufferedReader, int readLimit)
    throws Exception {
    // It appears that some web servers cannot handle this. 
    // All I know is that the Indiana University smi23d server
    // returns only one char the SECOND time this is run. 
    // for some reason the URL connection is not closing?
    // but the problem only occurs when this limited buffer is used;
    // when you use MOL:: in front of the filename, then it is fine.
    // So we do that...
    bufferedReader.mark(readLimit);
    buf = new char[readLimit];
    cchBuf = Math.max(bufferedReader.read(buf), 0);
    ichCurrent = 0;
    bufferedReader.reset();
  }

  String getHeader(int n) {
    return (n == 0 ? new String(buf) : new String(buf, 0, Math.min(cchBuf, n)));
  }
  
  String readLineWithNewline() {
    // mth 2004 10 17
    // for now, I am going to put in a hack here
    // we have some CIF files with many lines of '#' comments
    // I believe that for all formats we can flush if the first
    // char of the line is a #
    // if this becomes a problem then we will need to adjust
    while (ichCurrent < cchBuf) {
      int ichBeginningOfLine = ichCurrent;
      char ch = 0;
      while (ichCurrent < cchBuf &&
             (ch = buf[ichCurrent++]) != '\r' && ch != '\n') {
      }
      if (ch == '\r' && ichCurrent < cchBuf && buf[ichCurrent] == '\n')
        ++ichCurrent;
      int cchLine = ichCurrent - ichBeginningOfLine;
      if (buf[ichBeginningOfLine] == '#')
        continue; // flush comment lines;
      StringBuffer sb = new StringBuffer(cchLine);
      sb.append(buf, ichBeginningOfLine, cchLine);
      return sb.toString();
    }
    //Logger.debug("org.jmol.adapter.smarter.Resolver short input buffer");
    // miguel 2005 01 26
    // for now, just return the empty string.
    // it will only affect the Resolver code
    // it will be easier to handle because then everyone does not
    // need to check for the null pointer
    //
    // If it becomes a problem, then change this to null and modify
    // all the code above to make sure that it tests for null before
    // attempting to invoke methods on the strings. 
    return "";
  }
}
