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

public class OyoahaJar2Writer
{
  protected String header;
  protected boolean compress;
  protected File directoryToCompress;
  protected File[] files;
  protected OyoahaJar2Entry[] entries;

  /**
   * setDirectoyToCompress create a new set of entries
   * and set oyoahaJarWriter ready to write
   */

  public void setDirectoryToCompress(String header, File directoryToCompress, boolean compress)
  {
    this.header = header;
    this.directoryToCompress = (directoryToCompress.isDirectory())? directoryToCompress : new File(directoryToCompress.getParent());
    this.compress = compress;

    files = getFiles(this.directoryToCompress);
    entries = new OyoahaJar2Entry[files.length];

    String rootPath = this.directoryToCompress.getPath();

    for(int i=0;i<entries.length;i++)
    {
      entries[i] = new OyoahaJar2Entry(getPath(files[i], rootPath));
    }
  }

  public void setDirectoryToCompress(File directoryToCompress)
  {
    this.directoryToCompress = (directoryToCompress.isDirectory())? directoryToCompress : new File(directoryToCompress.getParent());

    files = getFiles(this.directoryToCompress);
    entries = new OyoahaJar2Entry[files.length];

    String rootPath = this.directoryToCompress.getPath();

    for(int i=0;i<entries.length;i++)
    {
      entries[i] = new OyoahaJar2Entry(getPath(files[i], rootPath));
    }
  }

  public String[] getOyoahaJarEntryName()
  {
    if(entries==null)
    return null;

    String[] s = new String[entries.length];

    for(int i=0;i<entries.length;i++)
    {
      s[i] = entries[i].name;
    }

    return s;
  }

  public OyoahaJar2Entry getOyoahaJarEntry(String name)
  {
    if(entries==null)
    return null;

    for(int i=0;i<entries.length;i++)
    {
      if(name.equals(entries[i].name))
      return entries[i];
    }

    return null;
  }

  public void write(File file)
  {
    write(header, file, compress);
  }

  public void write(String header, File file, boolean compress)
  {
    try
    {
      File[] tmps = new File[files.length];

      if(compress)
      {
        byte[] bytes = new byte[1024];

        for(int i=0;i<files.length;i++)
        {
          tmps[i] = File.createTempFile("_ojar" + i, ".tmp");
          GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(tmps[i]));
          InputStream in = new BufferedInputStream(new FileInputStream(files[i]));

          //write data
          while(true)
          {
            int z = in.read(bytes);

            if (z>0)
            {
              out.write(bytes, 0, z);
            }
            else
            {
              break;
            }
          }

          out.flush();
          out.close();
          in.close();

          entries[i].length = tmps[i].length();
        }
      }

      OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

      //write each name / length
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream out2 = new DataOutputStream(byteArrayOutputStream);

      for(int i=0;i<files.length;i++)
      {
        out2.writeUTF(entries[i].name);

        if(compress)
        {
          out2.writeLong(entries[i].length);
          out2.writeLong(files[i].length());
        }
        else
        {
          out2.writeLong(files[i].length());
        }
      }

      out2.flush();

      byte[] e = byteArrayOutputStream.toByteArray();
      out2.close();
      byteArrayOutputStream.close();

      //write header
      byteArrayOutputStream = new ByteArrayOutputStream();
      out2 = new DataOutputStream(byteArrayOutputStream);
      out2.writeUTF(header);
      out2.writeInt(files.length);
      out2.writeInt(e.length);

      if(compress)
      out2.writeInt(1);
      else
      out2.writeInt(0);

      out2.flush();

      byte[] h = byteArrayOutputStream.toByteArray();
      out2.close();
      byteArrayOutputStream.close();
      byteArrayOutputStream = null;
      out2 = null;

      out.write(h);
      out.flush();
      out.write(e);
      out.flush();

      byte[] bytes = new byte[1024];

      for(int i=0;i<files.length;i++)
      {
        int rk = 0;
        int lk = 0;

        InputStream in = new BufferedInputStream(new FileInputStream((compress)? tmps[i] : files[i]));

        byte[] lkey = Long.toHexString((compress)? entries[i].length : files[i].length()).getBytes();

        while(true)
        {
          int z = in.read(bytes);

          if (z>0)
          {
            out.write(bytes, 0, z);
          }
          else
          {
            break;
          }
        }

        out.flush();
        in.close();
      }

        out.close(); 

        if(compress)
        {
            for(int i=0;i<tmps.length;i++)
            {
                tmps[i].delete();
            }
        }
    }
    catch(Exception ex)
    {

    }
  }

  private String getPath(File file, String rootPath)
  {
    String f = file.getPath();

    f = f.substring(rootPath.length());
    f = f.replace(File.separatorChar, '/');

    if(f.startsWith("/"))
    {
      f = f.substring(1);
    }

    return f;
  }

  private final static File[] getFiles(File directory)
  {
    Vector v = new Vector();
    getFiles(v, directory);
    File[] files = new File[v.size()];

    for(int i=0;i<files.length;i++)
    {
      files[i] = (File)v.elementAt(i);
    }

    return files;
  }

  private final static void getFiles(Vector v, File directory)
  {
    String[] list = directory.list();

    for(int i=0;i<list.length;i++)
    {
      File f = new File(directory, list[i]);

      if(!f.isDirectory())
      {
        v.addElement(f);
      }
      else
      {
        getFiles(v, f);
      }
    }
  }
}