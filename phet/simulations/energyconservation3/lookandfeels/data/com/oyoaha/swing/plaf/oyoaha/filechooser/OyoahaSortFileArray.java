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

public class OyoahaSortFileArray
{
  protected final static int DELTA = 10;

  protected int mode;
  protected boolean inverse;

  protected File[] file;
  protected int count;

  public OyoahaSortFileArray()
  {
    mode = OyoahaDirectoryModel.BY_NAME;
    inverse = false;
    file = new File[DELTA];
  }

  public OyoahaSortFileArray(int size)
  {
    mode = OyoahaDirectoryModel.BY_NAME;
    inverse = false;
    file = new File[size];
  }

  public OyoahaSortFileArray(int mode, boolean inverse)
  {
    this.mode = mode;
    this.inverse = inverse;
    file = new File[DELTA];
  }

  public OyoahaSortFileArray(int mode, boolean inverse, int size)
  {
    this.mode = mode;
    this.inverse = inverse;
    file = new File[size];
  }

  public void sort()
  {
    if(count>0)
    quickSort(file, 0, count-1);
  }

  public boolean contains(File f)
  {
    return indexOf(f)>=0;
  }

  public int indexOf(File f)
  {
    for(int i=0;i<count;i++)
    {
      if(f.equals(file[i]))
      {
        return i;
      }
    }

    return -1;
  }

  public void clear()
  {
    clear(DELTA);
  }

  public void clear(int delta)
  {
    file = new File[delta];
    count = 0;
  }

  public void add(File f)
  {
    if(f==null)
    {
      return;
    }

    if (count >= file.length)
    {
      File[] tmp = new File[file.length+DELTA];
      System.arraycopy(file, 0, tmp, 0, file.length);
      file = tmp;
    }

    file[count++] = f;
  }

  public File get(int index)
  {
    return file[index];
  }

  public int getSize()
  {
    return count;
  }

  protected final void quickSort(File[] objects, int lo0, int hi0)
  {
    int lo = lo0;
    int hi = hi0;
    File mid;

    if (hi0 > lo0)
    {
      mid = objects[(lo0 + hi0)/2];

      while(lo<=hi)
      {
        while((lo < hi0) && lt(objects[lo], mid))
        {
          ++lo;
        }

        while((hi > lo0) && lt(mid, objects[hi]))
        {
          --hi;
        }

        if(lo<=hi)
        {
          File o = objects[lo];
          objects[lo] = objects[hi];
          objects[hi] = o;

          ++lo;
          --hi;
        }
      }

      if(lo0<hi)
      {
        quickSort(objects, lo0, hi);
      }

      if(lo<hi0)
      {
        quickSort(objects, lo, hi0);
      }
    }
  }

  protected final String getName(File f)
  {
    if(f.getParent()!=null)
    {
      return f.getName();
    }

    return f.getPath();
  }

  protected final boolean lt(File a, File b)
  {
    switch(mode)
    {
      case OyoahaDirectoryModel.BY_CASESENSITIVE_NAME:
        if(inverse)
        return getName(a).compareTo(getName(b)) > 0;

        return getName(a).compareTo(getName(b)) < 0;
      case OyoahaDirectoryModel.BY_SIZE:
        if(a.isDirectory() && b.isDirectory())
        {
          if(inverse)
          return getName(a).toLowerCase().compareTo(getName(b).toLowerCase()) > 0;

          return getName(a).toLowerCase().compareTo(getName(b).toLowerCase()) < 0;
        }
        else
        {
          if(inverse)
          return a.length() > b.length();

          return a.length() < b.length();
        }
      case OyoahaDirectoryModel.BY_DATE:
        if(inverse)
        return a.lastModified() > b.lastModified();

        return a.lastModified() < b.lastModified();
    }

    if(inverse)
    return getName(a).toLowerCase().compareTo(getName(b).toLowerCase()) > 0;

    return getName(a).toLowerCase().compareTo(getName(b).toLowerCase()) < 0;
  }
}