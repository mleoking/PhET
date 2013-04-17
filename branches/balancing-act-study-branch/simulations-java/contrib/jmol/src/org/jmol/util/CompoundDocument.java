/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-18 15:59:33 -0600 (Sat, 18 Mar 2006) $
 * $Revision: 4652 $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
 *
 * Contact: hansonr@stolaf.edu
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
package org.jmol.util;


import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/* a simple compound document reader. 
 * See http://sc.openoffice.org/compdocfileformat.pdf
 * 
 * random access file info: 
 * http://java.sun.com/docs/books/tutorial/essential/io/rafs.html
 * 
 * SHOOT! random access is only for applications, not applets!
 * 
 * With a bit more work, this could be set up to deliver binary files, but
 * right now I've only implemented it for string-based data. All Jmol is using
 * is getAllData().
 * 
 */

public class CompoundDocument extends BinaryDocument {

//  RandomAccessFile file;
  CmpDocHeader header = new CmpDocHeader();
  List<CmpDocDirectoryEntry> directory = new ArrayList<CmpDocDirectoryEntry>();
  CmpDocDirectoryEntry rootEntry;

  int[] SAT;
  int[] SSAT;
  int sectorSize;
  int shortSectorSize;
  int nShortSectorsPerStandardSector;
  int nIntPerSector;
  int nDirEntriesperSector;

  public CompoundDocument(BufferedInputStream bis) {

    /*    try {
     file = new RandomAccessFile(fileName, "r");
     isRandom = true;
     } catch (Exception e) {
     // probably an applet
     }
     */
    if (!isRandom) {
      stream = new DataInputStream(bis);
    }
    stream.mark(Integer.MAX_VALUE);
    if (!readHeader())
      return;
    getSectorAllocationTable();
    getShortSectorAllocationTable();
    getDirectoryTable();
  }

  public static boolean isCompoundDocument(InputStream is) throws Exception {
    byte[] abMagic = new byte[8];
    is.mark(9);
    int countRead = is.read(abMagic, 0, 8);
    is.reset();
    return (countRead == 8 && abMagic[0] == (byte) 0xD0
        && abMagic[1] == (byte) 0xCF && abMagic[2] == (byte) 0x11
        && abMagic[3] == (byte) 0xE0 && abMagic[4] == (byte) 0xA1
        && abMagic[5] == (byte) 0xB1 && abMagic[6] == (byte) 0x1A 
        && abMagic[7] == (byte) 0xE1);
  }
  
  public static boolean isCompoundDocument(byte[] bytes) {
    return (bytes.length >= 8 && bytes[0] == (byte) 0xD0
        && bytes[1] == (byte) 0xCF && bytes[2] == (byte) 0x11
        && bytes[3] == (byte) 0xE0 && bytes[4] == (byte) 0xA1
        && bytes[5] == (byte) 0xB1 && bytes[6] == (byte) 0x1A 
        && bytes[7] == (byte) 0xE1);
  }
  

  public List<CmpDocDirectoryEntry> getDirectory() {
    return directory;
  }

  public String getDirectoryListing(String separator) {
    String str = "";
    for (int i = 0; i < directory.size(); i++) {
      CmpDocDirectoryEntry thisEntry = directory.get(i);
      if (!thisEntry.isEmpty)
        str += separator
            + thisEntry.entryName
            + "\tlen="
            + thisEntry.lenStream
            + "\tSID="
            + thisEntry.SIDfirstSector
            + (thisEntry.isStandard ? "\tfileOffset="
                + getOffset(thisEntry.SIDfirstSector) : "");
    }
    return str;
  }

  StringBuffer data;
  
  public StringBuffer getAllData() {
    return getAllData(null, null);
  }

  /**
   * reads a compound document directory and saves all data in a Hashtable
   * so that the files may be organized later in a different order. Also adds
   * a #Directory_Listing entry.
   * 
   * Files are bracketed by BEGIN Directory Entry and END Directory Entry lines, 
   * similar to ZipUtil.getAllData.
   * 
   * @param prefix
   * @param binaryFileList   |-separated list of files that should be saved
   *                         as xx xx xx hex byte strings. The directory listing
   *                         is appended with ":asBinaryString"
   * @param fileData
   */
  public void getAllData(String prefix, 
                         String binaryFileList, Map<String, String> fileData) {
    fileData.put("#Directory_Listing", getDirectoryListing("|"));
    binaryFileList = "|" + binaryFileList + "|";
    for (int i = 0; i < directory.size(); i++) {
      CmpDocDirectoryEntry thisEntry = directory.get(i);
      String name = thisEntry.entryName;
      Logger.info("reading " + name);
      if (!thisEntry.isEmpty && thisEntry.entryType != 5) {
        boolean isBinary = (binaryFileList != null && binaryFileList.indexOf("|" + thisEntry.entryName + "|") >= 0);
        if (isBinary)
          name += ":asBinaryString";
        StringBuilder data = new StringBuilder();
        data.append("BEGIN Directory Entry ").append(name).append("\n"); 
        data.append(getFileAsString(thisEntry, isBinary));
        data.append("\nEND Directory Entry ").append(name).append("\n");
        fileData.put(prefix + "/" + name, data.toString());
      }
    }
    close();
  }

  public StringBuffer getAllData(String binaryFileList, String firstFile) {
    if (firstFile != null) {
      for (int i = 0; i < directory.size(); i++) {
        CmpDocDirectoryEntry thisEntry = directory.get(i);
        if (thisEntry.entryName.equals(firstFile)) {
          directory.remove(i);
          directory.add(1, thisEntry); // after ROOT_ENTRY
          break;
        }
      }
    }
    data = new StringBuffer();
    data.append("Compound Document File Directory: ");
    data.append(getDirectoryListing("|"));
    data.append("\n");
    binaryFileList = "|" + binaryFileList + "|";
    for (int i = 0; i < directory.size(); i++) {
      CmpDocDirectoryEntry thisEntry = directory.get(i);
      Logger.info("reading " + thisEntry.entryName);
      if (!thisEntry.isEmpty && thisEntry.entryType != 5) {
        data.append("BEGIN Directory Entry ").append(thisEntry.entryName).append("\n");            
        data.append(getFileAsString(thisEntry, binaryFileList != null && binaryFileList.indexOf("|" + thisEntry.entryName + "|") >= 0));
        data.append("\n");
        data.append("END Directory Entry ").append(thisEntry.entryName).append("\n");            
      }
    }
    close();
    return data;
  }

  public StringBuffer getFileAsString(String entryName) {
    for (int i = 0; i < directory.size(); i++) {
      CmpDocDirectoryEntry thisEntry = directory.get(i);
      if (thisEntry.entryName.equals(entryName))
        return getFileAsString(thisEntry, false);
    }
    return new StringBuffer();
  }

  class CmpDocHeader {

    //512 bytes
    //offset 0:
    byte[] magicNumbers = new byte[8]; // D0CF11E0A1B11AE1
    byte[] uniqueID = new byte[16];
    byte revNumber; // 3E = 62
    //byte unusedb1;
    byte verNumber; // 3
    //byte unusedb2;
    //short byteOrder; // -2 littleEndian
    short sectorPower; // 2^sectorPower = sector size; 512 = 2^9
    short shortSectorPower; // 2^shortSectorPower = short sector size; 64 = 2^6
    byte[] unused = new byte[10];
    int nSATsectors; // number of sectors for sector allocation table
    int SID_DIR_start; // sector identifier of start of directory sector
    byte[] unused2 = new byte[4];
    //offset 56:
    int minBytesStandardStream; // less than this (and not DIR) will be "short"
    int SID_SSAT_start; // start of short sector allocation table (SSAT)
    int nSSATsectors; // number of sectors allocated to SSAT
    int SID_MSAT_next; // pointer to next master sector allocation table sector
    int nAdditionalMATsectors; // number of sectors allocated to more MSAT sectors
    //offset 76; 436 bytes:      
    int[] MSAT0 = new int[109]; // beginning of master allocation table 

    /*
     *  Sector 0 is first sector AFTER this header
     *  
     *  If sectorPower = 9, then this allows for 109 PAGES
     *  of sector allocation tables, with 127 pointers per
     *  page (plus 1 pointer to the next SAT page), each 
     *  pointing to a sector of 512 bytes. Thus, with no additional
     *  MSAT pages, the header allows for 109*128*512 = 7.1 Mb file
     *  
     */

    final boolean readData() {
      try {
        readByteArray(magicNumbers, 0, 8);
        if (magicNumbers[0] != (byte) 0xD0 || magicNumbers[1] != (byte) 0xCF
            || magicNumbers[2] != (byte) 0x11 || magicNumbers[3] != (byte) 0xE0
            || magicNumbers[4] != (byte) 0xA1 || magicNumbers[5] != (byte) 0xB1
            || magicNumbers[6] != (byte) 0x1A || magicNumbers[7] != (byte) 0xE1)
          return false;
        readByteArray(uniqueID);
        revNumber = readByte();
        readByte();
        verNumber = readByte();
        readByte();
        byte b1 = readByte();
        byte b2 = readByte();
        isBigEndian = (b1 == -1 && b2 == -2);
        sectorPower = readShort();
        shortSectorPower = readShort();
        readByteArray(unused);
        nSATsectors = readInt();
        SID_DIR_start = readInt();
        readByteArray(unused2);
        minBytesStandardStream = readInt();
        SID_SSAT_start = readInt();
        nSSATsectors = readInt();
        SID_MSAT_next = readInt();
        nAdditionalMATsectors = readInt();
        for (int i = 0; i < 109; i++)
          MSAT0[i] = readInt();
      } catch (Exception e) {
        Logger.error(null, e);
        return false;
      }
      return true;
    }
  }

  class CmpDocDirectoryEntry {
    // 128 bytes
    //offset 0:
    byte[] unicodeName = new byte[64];
    short nBytesUnicodeName; // twice the ascii length, including terminating 0
    byte entryType; // 0 empty; 1 storage; 2 stream; 5 root storage
    //byte entryColor; // 0 red or 1 black
    //int DIDchildLeft;
    //int DIDchildRight;
    //int DIDstorageRoot;
    byte[] uniqueID = new byte[16];
    byte[] userflags = new byte[4];
    //long timeStamp1;
    //long timeStamp2;
    //offset 116:
    int SIDfirstSector; // either SAT or SSAT
    int lenStream;
    byte[] unused = new byte[4]; // or maybe this is really a long...

    // derived:

    String entryName;
    boolean isStandard;
    boolean isEmpty;

    final boolean readData() {
      try {
        readByteArray(unicodeName);
        nBytesUnicodeName = readShort();
        entryType = readByte();
        /*entryColor = */readByte();
        /*DIDchildLeft = */readInt();
        /*DIDchildRight = */readInt();
        /*DIDstorageRoot = */readInt();
        readByteArray(uniqueID);
        readByteArray(userflags);
        /*timeStamp1 = */readLong();
        /*timeStamp2 = */readLong();
        //offset 116:
        SIDfirstSector = readInt();
        lenStream = readInt();
        readByteArray(unused);
      } catch (Exception e) {
        Logger.error(null, e);
        return false;
      }
      entryName = "";
      for (int i = 0; i < nBytesUnicodeName - 2; i += 2)
        entryName += (char) unicodeName[i];
      isStandard = (entryType == 5 || lenStream >= header.minBytesStandardStream);
      isEmpty = (entryType == 0 || lenStream <= 0);
      //System.out.println(entryName + " type " + entryType);
      return true;
    }
  }

  private long getOffset(int SID) {
    return (SID + 1) * sectorSize;
  }

  private void gotoSector(int SID) {
    seek(getOffset(SID));
  }

  private boolean readHeader() {
    if (!header.readData())
      return false;
    sectorSize = 1 << header.sectorPower;
    shortSectorSize = 1 << header.shortSectorPower;
    nShortSectorsPerStandardSector = sectorSize / shortSectorSize; // e.g. 512 / 64 = 8
    nIntPerSector = sectorSize / 4; // e.g. 512 / 4 = 128
    nDirEntriesperSector = sectorSize / 128; // e.g. 512 / 128 = 4
    if (Logger.debugging) {
      Logger.debug(
          "compound document: revNum=" + header.revNumber +
          " verNum=" + header.verNumber + " isBigEndian=" + isBigEndian +
          " bytes per standard/short sector=" + sectorSize + "/" + shortSectorSize);
    }
    return true;
  }

  private void getSectorAllocationTable() {
    int nSID = 0;
    int thisSID;
    SAT = new int[header.nSATsectors * nIntPerSector + 109];

    try {
      for (int i = 0; i < 109; i++) {
        thisSID = header.MSAT0[i];
        if (thisSID < 0)
          break;
        gotoSector(thisSID);
        for (int j = 0; j < nIntPerSector; j++) {
          SAT[nSID++] = readInt();
          //Logger.debug(thisSID+"."+j + "/" + (nSID - 1) + " : " + SAT[nSID - 1]);
        }
      }
      int nMaster = header.nAdditionalMATsectors;
      thisSID = header.SID_MSAT_next;
      int[] MSAT = new int[nIntPerSector];
      out: while (nMaster-- > 0 && thisSID >= 0) {
        // read a page of sector identifiers pointing to SAT sectors
        gotoSector(thisSID);
        for (int i = 0; i < nIntPerSector; i++)
          MSAT[i] = readInt();
        // read each page of SAT sector identifiers 
        // last entry is pointer to next master sector allocation table page
        for (int i = 0; i < nIntPerSector - 1; i++) {
          thisSID = MSAT[i];
          if (thisSID < 0)
            break out;
          gotoSector(thisSID);
          for (int j = nIntPerSector; --j >= 0;)
            SAT[nSID++] = readInt();
        }
        thisSID = MSAT[nIntPerSector - 1];
      }
    } catch (Exception e) {
      Logger.error(null, e);
    }
  }

  private void getShortSectorAllocationTable() {
    int nSSID = 0;
    int thisSID = header.SID_SSAT_start;
    int nMax = header.nSSATsectors * nIntPerSector;
    SSAT = new int[nMax];
    try {
      while (thisSID > 0 && nSSID < nMax) {
        gotoSector(thisSID);
        for (int j = 0; j < nIntPerSector; j++) {
          SSAT[nSSID++] = readInt();
          //System.out.println("short: " + thisSID+"."+j+" SSID=" +(nSSID-1)+" "+SSAT[nSSID-1]);
        }
        thisSID = SAT[thisSID];
      }
    } catch (Exception e) {
      Logger.error(null, e);
    }
  }

  private void getDirectoryTable() {
    int thisSID = header.SID_DIR_start;
    CmpDocDirectoryEntry thisEntry;
    rootEntry = null;
    try {
      while (thisSID > 0) {
        gotoSector(thisSID);
        for (int j = nDirEntriesperSector; --j >= 0;) {
          thisEntry = new CmpDocDirectoryEntry();
          thisEntry.readData();
          if (thisEntry.lenStream > 0) {
            directory.add(thisEntry);
            //System.out.println(thisEntry.entryName);
          }
          if (thisEntry.entryType == 5)
            rootEntry = thisEntry;
        }
        thisSID = SAT[thisSID];
      }
    } catch (Exception e) {
      Logger.error(null, e);
    }
    if (Logger.debugging)
      Logger.debug("CompoundDocument directory entry: \n"
        + getDirectoryListing("\n"));
  }

  private StringBuffer getFileAsString(CmpDocDirectoryEntry thisEntry, boolean asBinaryString) {
    if(thisEntry.isEmpty)
      return new StringBuffer();
    //System.out.println(thisEntry.entryName + " " + thisEntry.entryType + " " + thisEntry.lenStream + " " + thisEntry.isStandard + " " + thisEntry.SIDfirstSector);
    return (thisEntry.isStandard ? getStandardStringData(
            thisEntry.SIDfirstSector, thisEntry.lenStream, asBinaryString)
            : getShortStringData(thisEntry.SIDfirstSector, thisEntry.lenStream, asBinaryString));
  }

  private StringBuffer getStandardStringData(int thisSID, int nBytes,
                                             boolean asBinaryString) {
    StringBuffer data = new StringBuffer();
    byte[] byteBuf = new byte[sectorSize];
    try {
      while (thisSID > 0 && nBytes > 0) {
        gotoSector(thisSID);
        nBytes = getSectorData(data, byteBuf, sectorSize, nBytes, asBinaryString);
        thisSID = SAT[thisSID];
      }
      if (nBytes == -9999)
        return new StringBuffer();
    } catch (Exception e) {
      Logger.error(null, e);
    }
    return data;
  }

  private int getSectorData(StringBuffer data, byte[] byteBuf,
                            int nSectorBytes, int nBytes, boolean asBinaryString)
      throws Exception {
    readByteArray(byteBuf);
    if (asBinaryString) {
      for (int i = 0; i < nSectorBytes; i++) {
        data.append(Integer.toHexString(byteBuf[i] & 0xFF)).append(' ');
        if (--nBytes < 1)
          break;
      }
    } else {
      for (int i = 0; i < nSectorBytes; i++) {
        if (byteBuf[i] == 0)
          return -9999; // don't allow binary data
        data.append((char) byteBuf[i]);
        if (--nBytes < 1)
          break;
      }
    }
    return nBytes;
  }
  private StringBuffer getShortStringData(int shortSID, int nBytes, boolean asBinaryString) {
    StringBuffer data = new StringBuffer();
    if (rootEntry == null)
      return data;
    int thisSID = rootEntry.SIDfirstSector;
    int ptShort = 0;
    byte[] byteBuf = new byte[shortSectorSize];
    try {
      //System.out.println("CD shortSID=" + shortSID);
      // point to correct short data sector, 512/64 = 4 per page
      while (thisSID >= 0 && shortSID >= 0 && nBytes > 0) {
        while (shortSID - ptShort >= nShortSectorsPerStandardSector) {
          ptShort += nShortSectorsPerStandardSector;
          thisSID = SAT[thisSID];
        }
        seek(getOffset(thisSID) + (shortSID - ptShort) * shortSectorSize);
        nBytes = getSectorData(data, byteBuf, shortSectorSize, nBytes, asBinaryString);
        shortSID = SSAT[shortSID];
        //System.out.println("CD shortSID=" + shortSID);
      }
    } catch (Exception e) {
      Logger.error(data.toString());
      Logger.error(null, e);
    }
    //System.out.println(data);
    return data;
  }
  
  
}
