/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-04-05 09:07:28 -0500 (Thu, 05 Apr 2007) $
 * $Revision: 7326 $
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
package org.jmol.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class SurfaceFileTyper {

  /* moved to util because drag-drop can then determine an isosurface file 
   * type and automatically load the file 
   */
  
  public final static String PMESH_BINARY_MAGIC_NUMBER = "PM" + '\1' + '\0';
  
  public static String determineSurfaceFileType(InputStream is) {
    BufferedReader br;
    try {
      br = new BufferedReader(new InputStreamReader(
          new BufferedInputStream(is, 8192), "ISO-8859-1"));
    } catch (UnsupportedEncodingException e) {
      return null;
    }
    return SurfaceFileTyper.determineSurfaceFileType(br);
  }
  
  public static String determineSurfaceFileType(BufferedReader bufferedReader) {
    // JVXL should be on the FIRST line of the file, but it may be 
    // after comments or missing.

    // Apbs, Jvxl, or Cube, also efvet

    String line;
    LimitedLineReader br = new LimitedLineReader(bufferedReader, 16000);
    //sure bets, but not REQUIRED:
    if ((line = br.info()).length() == 0)
      return null;

    //for (int i = 0; i < 220; i++)
    //  System.out.print(" " + i + ":" + (0 + line.charAt(i)));
    //System.out.println("");
    switch (line.charAt(0)) {
    case '#':
      if (line.indexOf(".obj") >= 0)
        return "Obj"; // #file: pymol.obj
      if (line.indexOf("MSMS") >= 0)
        return "Msms";
      break;
    case '&':
      if (line.indexOf("&plot") == 0)
        return "Jaguar";
      break;
    case '\r':
    case '\n':
      if (line.indexOf("ZYX") >= 0)
        return "Xplor";
      break;
    }
    if (line.indexOf("Here is your gzipped map") >= 0)
      return "UPPSALA" + line;
    if (line.indexOf("<jvxl") >= 0 && line.indexOf("<?xml") >= 0)
      return "JvxlXML";
    if (line.indexOf("#JVXL+") >= 0)
      return "Jvxl+";
    if (line.indexOf("#JVXL") >= 0)
      return "Jvxl";
    if (line.indexOf("<efvet ") >= 0)
      return "Efvet";
    if (line.indexOf("usemtl") >= 0)
      return "Obj";
    // binary formats: problem here is that the buffered reader
    // may be translating byte sequences into unicode
    // and thus shifting the offset
    int pt0 = line.indexOf('\0');
    if (pt0 >= 0) {
      if (line.indexOf(PMESH_BINARY_MAGIC_NUMBER) == 0)
        return "Pmesh";
      if (line.indexOf("MAP ") == 208)
        return "MRC";
      if (line.length() > 37 && (line.charAt(36) == 0 && line.charAt(37) == 100 
          || line.charAt(36) == 0 && line.charAt(37) == 100)) { 
           // header19 (short)100
          return "DSN6";
      }
    }
    // Apbs, Jvxl, Obj, or Cube, maybe formatted Plt

    line = br.readNonCommentLine();
    if (line.indexOf("object 1 class gridpositions counts") == 0)
      return "Apbs";

    String[] tokens = Parser.getTokens(line);
    String line2 = br.readNonCommentLine();// second line
    if (tokens.length == 2 && Parser.parseInt(tokens[0]) == 3
        && Parser.parseInt(tokens[1]) != Integer.MIN_VALUE) {
      tokens = Parser.getTokens(line2);
      if (tokens.length == 3 && Parser.parseInt(tokens[0]) != Integer.MIN_VALUE
          && Parser.parseInt(tokens[1]) != Integer.MIN_VALUE
          && Parser.parseInt(tokens[2]) != Integer.MIN_VALUE)
        return "PltFormatted";
    }
    String line3 = br.readNonCommentLine(); // third line
    if (line.startsWith("v ") && line2.startsWith("v ") && line3.startsWith("v "))
        return "Obj";
    //next line should be the atom line
    int nAtoms = Parser.parseInt(line3);
    if (nAtoms == Integer.MIN_VALUE)
      return (line3.indexOf("+") == 0 ? "Jvxl+" : null);
    if (nAtoms >= 0)
      return "Cube"; //Can't be a Jvxl file
    nAtoms = -nAtoms;
    for (int i = 4 + nAtoms; --i >= 0;)
      if ((line = br.readNonCommentLine()) == null)
        return null;
    int nSurfaces = Parser.parseInt(line);
    if (nSurfaces == Integer.MIN_VALUE)
      return null;
    return (nSurfaces < 0 ? "Jvxl" : "Cube"); //Final test looks at surface definition line
  }
  
}

class LimitedLineReader {
  //from Resolver
  private char[] buf;
  private int cchBuf;
  private int ichCurrent;

  LimitedLineReader(BufferedReader bufferedReader, int readLimit) {
    buf = new char[readLimit];
    try {
      bufferedReader.mark(readLimit);
      cchBuf = bufferedReader.read(buf);
      ichCurrent = 0;
      bufferedReader.reset();
    } catch (Exception e) {      
    }
  }

  protected String info() {
    return new String(buf);  
  }
  
  protected String readNonCommentLine() {
    while (ichCurrent < cchBuf) {
      int ichBeginningOfLine = ichCurrent;
      char ch = 0;
      while (ichCurrent < cchBuf &&
             (ch = buf[ichCurrent++]) != '\r' && ch != '\n') {
      }
      int cchLine = ichCurrent - ichBeginningOfLine;
      if (ch == '\r' && ichCurrent < cchBuf && buf[ichCurrent] == '\n')
        ++ichCurrent;
      if (buf[ichBeginningOfLine] == '#') // flush comment lines;
        continue;
      StringBuffer sb = new StringBuffer(cchLine);
      sb.append(buf, ichBeginningOfLine, cchLine);
      return sb.toString();
    }
    return "";
  }
}

