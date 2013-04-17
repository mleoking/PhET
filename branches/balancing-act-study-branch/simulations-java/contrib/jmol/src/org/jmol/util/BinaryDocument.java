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


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jmol.viewer.Viewer;


//import java.io.RandomAccessFile;

/* a basic binary file reader (extended by CompoundDocument). 
 * 
 * random access file info: 
 * http://java.sun.com/docs/books/tutorial/essential/io/rafs.html
 * 
 * SHOOT! random access is only for applications, not applets!
 * 
 * Note that YOU are responsible for determining whether a file
 * is bigEndian or littleEndian; the default is bigEndian.
 * 
 */

public class BinaryDocument {

  public BinaryDocument() {  
  }

  public BinaryDocument(BufferedInputStream bis) {
    setStream(bis, false);   
  }
//  RandomAccessFile file;
  
  protected DataInputStream stream;
  protected boolean isRandom = false;
  protected boolean isBigEndian = true;

  public void close() {
    if (stream != null)
      try {
        stream.close();
      } catch (Exception e) {
        // ignore
      }
    if (os != null) {
      try {
        os.flush();
        os.close();
      } catch (IOException e) {
        // ignore
      }
    }
  }
  
  public void setStream(BufferedInputStream bis, boolean isBigEndian) {
    if (bis == null)
      return;
    stream = new DataInputStream(bis);
    this.isBigEndian = isBigEndian;
  }
  
  public void setIsBigEndian(boolean TF) {
    this.isBigEndian = TF;
  }
  
  public void setStream(DataInputStream stream) {
    this.stream = stream;
  }
  
  public void setRandom(boolean TF) {
    isRandom = TF;
    //CANNOT be random for web 
  }
  
  public byte readByte() throws Exception {
    nBytes++;
    return ioReadByte();
  }

  private byte ioReadByte() throws Exception {
    byte b = stream.readByte();
    if (os != null)
      os.write(b);
    return b;
  }

  public void readByteArray(byte[] b) throws Exception {
    readByteArray(b, 0, b.length);
  }

  public int readByteArray(byte[] b, int off, int len) throws Exception {
    int n = ioRead(b, off, len);
    if (n > 0)
      nBytes += n;
    int nBytesRead = n;
    if (n > 0 && n < len) {
      // apparently this is possible over the web
      // it occurred in getting a DNS6B format file from Uppsala
      while (nBytesRead < len && n > 0) {
        n = ioRead(b, nBytesRead, len - nBytesRead);
        if (n > 0) {
          nBytes += n;
          nBytesRead += n;
        }
      }
    }
    return nBytesRead;
  }

  private int ioRead(byte[] b, int off, int len) throws Exception {
    int n = stream.read(b, off, len);
    if (n > 0 && os != null)
      writeBytes(b, off, n);
    return n;
  }

  public void writeBytes(byte[] b) throws Exception {
    os.write(b, 0, b.length);
  }

  public void writeBytes(byte[] b, int off, int n) throws Exception {
    os.write(b, off, n);
  }

  public String readString(int nChar) throws Exception {
    byte[] temp = new byte[nChar];
    readByteArray(temp);
    StringBuffer s = new StringBuffer();
    for (int j = 0; j < nChar; j++)
      s.append((char) temp[j]);
    return s.toString();
  }
  
  public short readShort() throws Exception {
    nBytes += 2;
    return (isBigEndian ? ioReadShort()
        : (short) ((ioReadByte() & 0xff) 
                 | (ioReadByte() & 0xff) << 8));
  }

  private short ioReadShort() throws Exception {
    short b = stream.readShort();
    if (os != null)
      writeShort(b);
    return b;
  }


  public void writeShort(short i) throws Exception {
    os.write((byte) ((i >> 8) & 0xFF));
    os.write((byte) (i & 0xFF));
  }

  public int readInt() throws Exception {
    nBytes += 4;
    return (isBigEndian ? ioReadInt() : readLEInt());
  }
  
  private int ioReadInt() throws Exception {
    int i = stream.readInt();
    if (os != null)
      writeInt(i);
    return i;
  }

  public void writeInt(int i) throws Exception {
    os.write((byte) ((i >> 24) & 0xFF));
    os.write((byte) ((i >> 16) & 0xFF));
    os.write((byte) ((i >> 8) & 0xFF));
    os.write((byte) (i & 0xFF));
  }

  public static int swapBytes(int n) {
    return (((n >> 24) & 0xff)
        | ((n >> 16) & 0xff) << 8
        | ((n >> 8) & 0xff) << 16 
        | (n & 0xff) << 24);
  }

  public static short swapBytes(short n) {
    return (short) ((((n >> 8) & 0xff)
        | (n & 0xff) << 8));
  }

  
  public int readUnsignedShort() throws Exception {
    nBytes += 2;
    int a = (ioReadByte() & 0xff);
    int b = (ioReadByte() & 0xff);
    return (isBigEndian ? (a << 8) + b : (b << 8) + a);
  }
  
  public long readLong() throws Exception {
    nBytes += 8;
    return (isBigEndian ? ioReadLong()
       : ((((long) ioReadByte()) & 0xff)
        | (((long) ioReadByte()) & 0xff) << 8
        | (((long) ioReadByte()) & 0xff) << 16
        | (((long) ioReadByte()) & 0xff) << 24
        | (((long) ioReadByte()) & 0xff) << 32
        | (((long) ioReadByte()) & 0xff) << 40
        | (((long) ioReadByte()) & 0xff) << 48 
        | (((long) ioReadByte()) & 0xff) << 54));
  }

  private long ioReadLong() throws Exception {
    long b = stream.readLong();
    if (os != null)
      writeLong(b);
    return b;
  }

  public void writeLong(long b) throws Exception {
    writeInt((int)((b >> 32) & 0xFFFFFFFFl));
    writeInt((int)(b & 0xFFFFFFFFl));
  }

  public float readFloat() throws Exception {
    nBytes += 4;
    return (isBigEndian ? ioReadFloat() 
        : Float.intBitsToFloat(readLEInt()));
  }
  
  private float ioReadFloat() throws Exception {
    float f = stream.readFloat();
    if (os != null)
      os.write(Float.floatToIntBits(f));
    return f;
  }

  public double readDouble() throws Exception {
    nBytes += 8;
    return (isBigEndian ? ioReadDouble() : Double.longBitsToDouble(readLELong()));  
  }
    
  private double ioReadDouble() throws Exception {
    double d = stream.readDouble();
    if (os != null)
      writeLong(Double.doubleToRawLongBits(d));
    return d;
  }

  private int readLEInt() throws Exception {
    return ((ioReadByte() & 0xff)
          | (ioReadByte() & 0xff) << 8
          | (ioReadByte() & 0xff) << 16 
          | (ioReadByte() & 0xff) << 24);
  }

  private long readLELong() throws Exception {
    return ((((long) ioReadByte()) & 0xff)
          | (((long) ioReadByte()) & 0xff) << 8
          | (((long) ioReadByte()) & 0xff) << 16 
          | (((long) ioReadByte()) & 0xff) << 24
          | (((long) ioReadByte()) & 0xff) << 32
          | (((long) ioReadByte()) & 0xff) << 40
          | (((long) ioReadByte()) & 0xff) << 48
          | (((long) ioReadByte()) & 0xff) << 56);
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

  long nBytes;
  
  public long getPosition() {
    return nBytes;
  }

  OutputStream os;
  public void setOutputStream(OutputStream os, Viewer viewer, double privateKey) {
    if (viewer.checkPrivateKey(privateKey))
      this.os = os;
  }


/*  random access -- application only:
 * 
    void seekFile(long offset) {
    try {
      file.seek(offset);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
*/
}
