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

package com.oyoaha.swing.plaf.oyoaha.filechooser;

import java.io.*;
import java.util.*;

public class OyoahaVectorFolder extends File
{
  protected final static String TEMP_FILENAME = "temp.ovf";

  protected Vector files;
  protected String name;
  protected boolean changed;

  protected OyoahaVectorFolder(String parent, String child)
  {
    super(parent, child);
    load();
  }

  public static void deleteVectorFolder(String name)
  {
    File f = getVirtualFolder(name);

    if(f.exists())
    {
      f.delete();
    }
  }

  public static OyoahaVectorFolder getVirtualFolder(String name)
  {
    if(!name.endsWith(".ovf"))
    name = name + ".ovf";

    return new OyoahaVectorFolder(System.getProperty("user.home"), ".filechooser" + File.separatorChar + name);
  }

  public static OyoahaVectorFolder getTempVirtualFolder()
  {
    return getVirtualFolder(TEMP_FILENAME);
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  public String getUserName()
  {
    return getName().substring(0, getName().length()-4);
  }

  public File[] listFiles()
  {
    File[] f = new File[files.size()];

    for(int i=0;i<f.length;i++)
    {
      f[i] = (File)files.elementAt(i);
    }

    return f;
  }

  public void addOrMoveFile(File file, int max)
  {
    int index = files.indexOf(file);

    if(index<0)
    {
      if(files.size()<=max)
      {
        files.removeElementAt(0);
      }

      files.addElement(file);
    }
    else
    {
      files.removeElementAt(index);
      files.addElement(file);
    }

    changed = true;
  }

  public void addOrMoveFile(File file)
  {
    int index = files.indexOf(file);

    if(index<0)
    {
      files.addElement(file);
    }
    else
    {
      files.removeElementAt(index);
      files.addElement(file);
    }
    changed = true;
  }

  public void addFile(File file, int max)
  {
    if(!files.contains(file))
    {
      if(files.size()<=max)
      {
        files.removeElementAt(0);
      }

      files.addElement(file);
      changed = true;
    }
  }

  public void addFile(File file)
  {
    if(!files.contains(file))
    {
      files.addElement(file);
      changed = true;
    }
  }

  public void removeFile(File file)
  {
    int index = files.indexOf(file);

    if(index>-1)
    {
      files.removeElementAt(index);
      changed = true;
    }
  }

  public void dispose()
  {
    if(getName().equals(TEMP_FILENAME))
    {
      if(exists())
      {
        delete();
      }
    }
    else
    if(changed)
    {
      save();
    }
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  protected void save()
  {
    try
    {
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this)));

      for(int i=0;i<files.size();i++)
      {
        out.println(((File)files.elementAt(i)).getPath());
      }

      out.flush();
      out.close();
    }
    catch(Exception e)
    {

    }

    changed = false;
  }

  protected void load()
  {
    try
    {
      BufferedReader in = new BufferedReader(new FileReader(this));

      String line = in.readLine();

      while(line!=null && line.length()>0)
      {
        File f = new File(line);

        if(f.exists())
        {
          files.addElement(f);
        }

        line = in.readLine();
      }

      in.close();
    }
    catch(Exception e)
    {

    }

    changed = false;
  }
}