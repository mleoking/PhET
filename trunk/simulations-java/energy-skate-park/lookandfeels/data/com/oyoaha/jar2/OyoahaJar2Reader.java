/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.jar2;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class OyoahaJar2Reader
{
  protected String header;
  protected boolean compress;
  protected Hashtable entries;

  protected boolean cache;

  protected OyoahaJar2StreamWarper warper;

  public OyoahaJar2Reader(OyoahaJar2StreamWarper warper, String header)
  {
    this.warper = warper;
    this.header = header;

    initializeEntries();
  }

  public void enabledCache(boolean cache)
  {
    this.cache = cache;
  }

  private void initializeEntries()
  {
    entries = new Hashtable();

    try
    {
      InputStream in = new BufferedInputStream(getInputStream());

      byte[] hh = new byte[2];
      byte[] h = new byte[2];

      readFully(in, hh, 0, hh.length);
      System.arraycopy(hh, 0, h, 0, hh.length);

      DataInputStream in2 = new DataInputStream(new ByteArrayInputStream(h));
      int tmps = in2.readUnsignedShort();
      in2.close();

      h = new byte[hh.length+tmps+12];
      System.arraycopy(hh, 0, h, 0, hh.length);
      readFully(in, h, hh.length, h.length-hh.length);

      in2 = new DataInputStream(new ByteArrayInputStream(h));

      String eheader = in2.readUTF(); //the header

      if(!header.equals(eheader))
      {
        in2.close();
        in.close();
        return;
      }

      int ecount = in2.readInt(); //number of entry
      int elength = in2.readInt(); //total length of the entry block
      int ecompress = in2.readInt(); //compressed flag

      if(ecompress<1)
      compress = false;
      else
      compress = true;

      in2.close();

      byte[] e = new byte[elength];
      readFully(in, e, 0, e.length);

      //compute length before first data block
      long length = h.length + e.length;

      Vector v = new Vector(ecount);
      in2 = new DataInputStream(new ByteArrayInputStream(e));

      for(int i=0;i<ecount;i++)
      {
        String nn = in2.readUTF();
        long ll = in2.readLong();
        OyoahaJar2Entry entry = new OyoahaJar2Entry(nn, length, ll);

        if(compress)
        {
          entry.uncompressed = in2.readLong();
        }

        length += entry.length;

        v.addElement(entry);
        entries.put(entry.name, entry);
      }

      in2.close();

      length = h.length + e.length;

      for(int i=0;i<v.size();i++)
      {
        OyoahaJar2Entry entry = (OyoahaJar2Entry)v.elementAt(i);

        if(warper.forceLoading())
        {
          byte[] bytes = new byte[(int)entry.length];

          //first read this in a byte array
          readFully(in, bytes, 0, bytes.length);

          if(compress)
          {
            byte[] b = new byte[(int)entry.uncompressed];
            b = readCompressed(b, bytes, 0, b.length);
            entry.data = b;
          }
          else
          {
            entry.data = bytes;
          }
        }
        else
        {
          entry.start = length;
          length += entry.length;
        }
      }

      in.close();
    }
    catch (Exception ex)
    {
        
    }
  }

  private InputStream getInputStream()
  {
    if(warper!=null)
    {
      return warper.getInputStream();
    }

    return null;
  }

 private byte[] readFully(InputStream in, byte[] b, int off, int len) throws IOException
  {
    if (len < 0)
    throw new IndexOutOfBoundsException();

    int n = 0;

    while(n < len)
    {
      int count = in.read(b, off + n, len - n);

      if (count < 0)
      throw new EOFException();

      n += count;
    }

    return b;
  }

  private byte[] readCompressed(byte[] bytes, byte[] b, int off, int len) throws IOException
  {
    if (len < 0)
    throw new IndexOutOfBoundsException();

    GZIPInputStream in2 = new GZIPInputStream(new ByteArrayInputStream(b));

    int n = 0;

    while(n < len)
    {
      int count = in2.read(bytes, off + n, len - n);

      if (count < 0)
      throw new EOFException();

      n += count;
    }

    return bytes;
  }

  public String[] getOyoahaJarEntryName()
  {
    String[] s = new String[entries.size()];

    Enumeration e = entries.keys();

    int i=0;
    while(e.hasMoreElements())
    {
      s[i++] = (String)e.nextElement();
    }

    return s;
  }

  public OyoahaJar2Entry getOyoahaJar2Entry(String name)
  {
    return (OyoahaJar2Entry)entries.get(name);
  }

  public InputStream getInputStream(String name)
  {
    byte[] bytes = getBytes(name);

    if(bytes!=null)
    return new ByteArrayInputStream(bytes);

    return null;
  }

  public byte[] getBytes(String name)
  {
    try
    {
      if(!entries.containsKey(name))
      return null;

      OyoahaJar2Entry entry = (OyoahaJar2Entry)entries.get(name);

      if(entry.data!=null)
      return entry.data;

      byte[] bytes = new byte[(int)entry.length];

      InputStream in = new BufferedInputStream(getInputStream());
      in.skip(entry.start);

      readFully(in, bytes, 0, bytes.length);
      in.close();

      if(compress)
      {
        byte[] b = new byte[(int)entry.uncompressed];
        b = readCompressed(b, bytes, 0, b.length);

        if(!cache)
        entry.data = b;

        return b;
      }

      if(!cache)
      entry.data = bytes;

      return bytes;
    }
    catch(Exception e)
    {
        
    }

    return null;
  }
}