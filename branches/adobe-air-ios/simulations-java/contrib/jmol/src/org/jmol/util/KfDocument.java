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
/*
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream; //import java.io.RandomAccessFile;
import java.util.Hashtable;
*/

public class KfDocument {
/*
  private boolean debug;

  private DataInputStream stream;
  private long nBytes;
  
  private Hashtable htData = new Hashtable();

  public Hashtable getData() {
    return htData;
  }

  public KfDocument(BufferedInputStream bis) {
    stream = new DataInputStream(bis);
    stream.mark(Integer.MAX_VALUE);
    debug = Logger.debugging;
    readSuperBlock();
  }

  public static boolean isKfDocument(InputStream is) throws Exception {
    byte[] abMagic = new byte[8];
    is.mark(9);
    is.read(abMagic, 0, 8);
    is.reset();
    return isKfDocument(abMagic);
  }

  public static boolean isKfDocument(byte[] bytes) {
    return (bytes.length >= 8 && bytes[0] == (byte) 'S'
        && bytes[1] == (byte) 'U' && bytes[2] == (byte) 'P'
        && bytes[3] == (byte) 'E' && bytes[4] == (byte) 'R'
        && bytes[5] == (byte) 'I' && bytes[6] == (byte) 'N' && bytes[7] == (byte) 'D');
  }

  public int readByteArray(byte[] b) throws IOException {
    int n = stream.read(b);
    nBytes += n;
    return n;
  }

  public void seek(long offset) {
    // slower, but all that is available using the applet
    try {
      if (offset == nBytes)
        return;
      if (offset < nBytes) {
        stream.reset();
        nBytes = 0;
      } else {
        offset -= nBytes;
      }
      stream.skipBytes((int)offset);
      nBytes += offset;
    } catch (Exception e) {
      Logger.error(null, e);
    }
  }

  private Hashtable sections;

  private void readSuperBlock() {
  }


  private int readBlock(int recordNum, byte[] bytes) throws IOException {
    if (recordNum <= 0) {
      throw new IllegalArgumentException("Negative record number in readBlock");
    }
    if (bytes == null) {
      throw new IllegalArgumentException(
          "Null pointer for `bytes' in readBlock");
    }
    seek(recordOffset(recordNum));
    return readByteArray(bytes);
  }

  private long recordOffset(int record) {
    return ((long) record - 1) * BLOCKLENGTH;
  }

  public final static int BLOCKLENGTH = 4096;
*/
}
