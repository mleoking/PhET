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

public class OyoahaDirectoryComboBoxNode
{
  protected String name;
  protected int depth;

  protected File file;

  public OyoahaDirectoryComboBoxNode(File file, int depth)
  {
    this.file = file;
    this.depth = depth;
  }

  public int getDepth()
  {
    return depth;
  }

  public String getName()
  {
    if(name!=null)
    {
     return name;
    }

    if(file!=null)
    {
      String n = file.getName();

      if(n!=null && n.length()>0)
      return n;

      return file.getPath();
    }

    return "???";
  }

  public File getFile()
  {
    return file;
  }

  public boolean equals(Object o)
  {
    if(file==null || o instanceof OyoahaVectorFolder)
    {
      return false;
    }

    if(o instanceof File)
    {
      return file.equals(o);
    }
    else
    if(o instanceof OyoahaDirectoryComboBoxNode)
    {
      if(name!=null)
      return name.equalsIgnoreCase(o.toString());

      File f = ((OyoahaDirectoryComboBoxNode)o).getFile();

      if(f!=null)
      return file.equals(f);
      else
      return false;
    }
    else
    if(o instanceof String)
    {
      if(name!=null)
      return name.equalsIgnoreCase((String)o);

      return file.getPath().equals(o);
    }

    return false;
  }

  public String toString()
  {
    if(name!=null)
    return name;

    if(file!=null)
    return file.toString();

    return "???";
  }
}