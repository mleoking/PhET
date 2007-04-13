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
import javax.swing.*;
import javax.swing.filechooser.*;
import java.beans.*;

public class OyoahaDirectoryModel extends AbstractListModel implements PropertyChangeListener
{
  public final static int USE_EXTENTION = 0;
  public final static int USE_TYPE = 1;
  public final static int USE_CASESENSITIVE_EXTENTION = 3;
  public final static int USE_NONE = 4;

  public final static int BY_NAME = 0;
  public final static int BY_CASESENSITIVE_NAME = 1;
  public final static int BY_SIZE = 2;
  public final static int BY_DATE = 3;

  protected boolean inverse;
  protected boolean onlyType;

  protected JFileChooser filechooser = null;
  protected OyoahaFileView view = null;

  protected Thread loadThread = null;
  protected int use = USE_EXTENTION;
  protected int mode = BY_NAME;

  protected int fetchID;
  protected int length;
  protected Object[] cached;

  public OyoahaDirectoryModel(JFileChooser filechooser, OyoahaFileView view)
  {
    this.filechooser = filechooser;
    this.view = view;
    validateFileCache();
  }

  public static String getMode(int mode)
  {
    switch(mode)
    {
      case USE_TYPE:
      return "use_type";
      case USE_CASESENSITIVE_EXTENTION:
      return "use_casesensitive_extention";
      case USE_NONE:
      return "use_none";
      default:
      return "use_extention";
    }
  }

  public static String getSort(int mode)
  {
    switch(mode)
    {
      case BY_CASESENSITIVE_NAME:
      return "by_casesensitive_name";
      case BY_SIZE:
      return "by_size";
      case BY_DATE:
      return "by_date";
      default:
      return "by_name";
    }
  }

  public static int getModeFromString(String string)
  {
    string = string.toLowerCase();

    if(string.equals("use_type"))
    {
      return USE_TYPE;
    }
    else
    if(string.equalsIgnoreCase("use_casesensitive_extention"))
    {
      return USE_CASESENSITIVE_EXTENTION;
    }
    else
    if(string.equalsIgnoreCase("use_none"))
    {
      return USE_NONE;
    }

    return USE_EXTENTION;
  }

  public static int geSortFromString(String string)
  {
    string = string.toLowerCase();

    if(string.equals("by_casesensitive_name"))
    {
      return BY_CASESENSITIVE_NAME;
    }
    else
    if(string.equalsIgnoreCase("by_size"))
    {
      return BY_SIZE;
    }
    else
    if(string.equalsIgnoreCase("by_date"))
    {
      return BY_DATE;
    }

    return BY_NAME;
  }

  public void inverse()
  {
    inverse = !inverse;
    validateFileCache();
  }

  public void setInverse(boolean inverse)
  {
    this.inverse = inverse;
    validateFileCache();
  }

  public void setMode(int mode)
  {
    this.mode = mode;
    validateFileCache();
  }

  public void setUse(int use)
  {
    this.use = use;
    validateFileCache();
  }

  public boolean isInverse()
  {
    return inverse;
  }

  public int getMode()
  {
    return mode;
  }

  public int getUse()
  {
    return use;
  }

  public void propertyChange(PropertyChangeEvent e)
  {
    String prop = e.getPropertyName();

    if(prop == JFileChooser.DIRECTORY_CHANGED_PROPERTY || prop == JFileChooser.FILE_VIEW_CHANGED_PROPERTY || prop == JFileChooser.FILE_FILTER_CHANGED_PROPERTY || prop == JFileChooser.FILE_HIDING_CHANGED_PROPERTY || prop == JFileChooser.FILE_SELECTION_MODE_CHANGED_PROPERTY)
    {
      invalidateFileCache();
      validateFileCache();
    }
  }

  public void invalidateFileCache()
  {
    length = 0;
    cached = null;
  }

  public void validateFileCache()
  {
    File currentDirectory = filechooser.getCurrentDirectory();

    if(currentDirectory == null)
    {
      invalidateFileCache();
      return;
    }

    if(loadThread != null)
    {
      loadThread.interrupt();
    }

    fetchID++;
    fireContentsChanged();
    invalidateFileCache();

    switch(use)
    {
      case USE_NONE:
        loadThread = new OyoahaLoadFilesThread(fetchID, currentDirectory);
      break;
      case USE_TYPE:
        loadThread = new OyoahaLoadFilesByTypeThread(fetchID, currentDirectory);
      break;
      default: //USE_EXTENTION
        loadThread = new OyoahaLoadFilesByExtentionThread(fetchID, currentDirectory);
      break;
    }

    loadThread.start();
  }

  protected void fireIntervalAdded(int start, int end)
  {
    fireIntervalAdded(this, start, end);
  }

  protected void fireContentsChanged()
  {
    fireContentsChanged(this, -1, length);
  }

  public int getSize()
  {
    return length;
  }

  public void replace(Object source, Object target)
  {
    int index = indexOf(source);

    if(index>-1)
    {
      cached[index] = target;
    }
  }

  public boolean contains(Object o)
  {
    return indexOf(o)>=0;
  }

  public int indexOf(Object o)
  {
    for(int i=0;i<length;i++)
    {
      if(o.equals(cached[i]))
      return i;
    }

    return -1;
  }

  public Object getElementAt(int index)
  {
    if(cached!=null)
    {
      return cached[index];
    }

    return null;
  }

  protected final void quickSortListFiles(ListFiles[] objects, int lo0, int hi0)
  {
    int lo = lo0;
    int hi = hi0;
    Object mid;

    if (hi0 > lo0)
    {
      mid = objects[(lo0 + hi0)/2];

      while(lo<=hi)
      {
        while((lo < hi0) && ltb(objects[lo], mid))
        {
          ++lo;
        }

        while((hi > lo0) && ltb(mid, objects[hi]))
        {
          --hi;
        }

        if(lo<=hi)
        {
          ListFiles o = objects[lo];
          objects[lo] = objects[hi];
          objects[hi] = o;

          ++lo;
          --hi;
        }
      }

      if(lo0<hi)
      {
        quickSortListFiles(objects, lo0, hi);
      }

      if(lo<hi0)
      {
        quickSortListFiles(objects, lo, hi0);
      }
    }
  }

  protected final boolean ltb(Object a, Object b)
  {
    return a.toString().toLowerCase().compareTo(b.toString().toLowerCase()) < 0;
  }

  protected File[] getFilesFromFileSystemView(File currentDirectory)
  {
    if(currentDirectory instanceof OyoahaVectorFolder)
    {
      return view.getChild(currentDirectory);
    }
    else
    {
      FileSystemView fileSystem = filechooser.getFileSystemView();

      File[] list = fileSystem.getFiles(currentDirectory, filechooser.isFileHidingEnabled());
      File[] files = new File[list.length];

      int index = 0;

      for (int i=0;i<list.length;i++)
      {
        if(filechooser.accept(list[i]))
        {
          files[index++] = list[i];
        }
      }

      File[] tmp = new File[index];

      System.arraycopy(files, 0, tmp, 0, index);
      return tmp;
    }
  }

  public class OyoahaLoadFilesByExtentionThread extends Thread
  {
    protected File currentDirectory = null;
    protected int fid;

    protected ListFiles arrayDirectories;
    protected ListFiles arrayFiles;
    protected int sizeArraySortFiles;
    protected ListFiles[] arraySortFiles;

    protected Hashtable table;

    public OyoahaLoadFilesByExtentionThread(int fid, File currentDirectory)
    {
      super("Oyoaha L&F File Loading Thread");
      this.fid = fid;
      this.currentDirectory = currentDirectory;
    }

    protected ListFiles getListFiles(String key, int size)
    {
      if(table.containsKey(key))
      return (ListFiles)table.get(key);

      ListFiles list = new ListFiles(key, size);
      table.put(key, list);

      return list;
    }

    protected void addToArraySortFiles(ListFiles array)
    {
      if(sizeArraySortFiles==arraySortFiles.length)
      {
        ListFiles[] tmp = new ListFiles[sizeArraySortFiles+10];
        System.arraycopy(arraySortFiles, 0, tmp, 0, sizeArraySortFiles);
        arraySortFiles = tmp;
      }

      arraySortFiles[sizeArraySortFiles++] = array;
    }

    protected String getExtention(File file)
    {
      return view.getExtention(file, (use==USE_CASESENSITIVE_EXTENTION)? true : false);
    }

    public void run()
    {
      try
      {
        File[] files = getFilesFromFileSystemView(currentDirectory);

        //initialise...
        arrayDirectories = new ListFiles(40);
        arrayFiles = new ListFiles(files.length);
        table = new Hashtable();

        for(int i=0;i<files.length;i++)
        {
          boolean isTraversable = filechooser.isTraversable(files[i]);

          if(isTraversable)
          {
            arrayDirectories.add(files[i]);
          }
          else
          if(!isTraversable && filechooser.isFileSelectionEnabled())
          {
            String ext = getExtention(files[i]);

            if(ext!=null && !ext.equals("file"))
            getListFiles(ext, 20).add(files[i]);
            else
            arrayFiles.add(files[i]);
          }
        }

        arraySortFiles = new ListFiles[table.size()];
        Enumeration e = table.keys();

        while(e.hasMoreElements())
        {
          ListFiles tmp = (ListFiles)table.get(e.nextElement());
          addToArraySortFiles(tmp);
        }

        if(arraySortFiles!=null && sizeArraySortFiles>1)
        {
          quickSortListFiles(arraySortFiles, 0, sizeArraySortFiles-1);
        }

        if(isInterrupted())
        {
          cancelRunnables();
          return;
        }

        //now set the right size of cached...
        //WARNING THIS IS REALLY BAD!
        if(cached==null)
        {
          cached = new Object[files.length];
          length = 0;
        }
        else
        {
          if(isInterrupted())
          {
            cancelRunnables();
            return;
          }
          else
          {
            cached = new Object[files.length];
            length = 0;
          }
        }

        //start thread...
        if(arrayDirectories.getSize()>0)
        {
          arrayDirectories.setFid(fid);
          SwingUtilities.invokeLater(arrayDirectories);
        }

        if(arraySortFiles!=null)
        {
          for(int i=0;i<sizeArraySortFiles;i++)
          {
            arraySortFiles[i].setFid(fid);
            SwingUtilities.invokeLater(arraySortFiles[i]);
          }
        }

        if(arrayFiles.getSize()>0)
        {
          arrayFiles.setFid(fid);
          SwingUtilities.invokeLater(arrayFiles);
        }

        if(isInterrupted())
        {
          cancelRunnables();
        }
      }
      catch(Exception e)
      {
        cancelRunnables();
      }
    }

    public void cancelRunnables()
    {
      if(arrayDirectories!=null)
      {
        arrayDirectories.cancel();
        arrayDirectories = null;
      }

      if(arraySortFiles!=null)
      {
        for(int i=0;i<sizeArraySortFiles;i++)
        {
          arraySortFiles[i].cancel();
          arraySortFiles[i] = null;
        }

        arraySortFiles = null;
      }

      if(arrayFiles!=null)
      {
        arrayFiles.cancel();
        arrayFiles = null;
      }
    }
  }

  public class OyoahaLoadFilesByTypeThread extends OyoahaLoadFilesByExtentionThread
  {
    public OyoahaLoadFilesByTypeThread(int fid, File currentDirectory)
    {
      super(fid, currentDirectory);
    }

    protected String getExtention(File file)
    {
      return view.getType(file, false);
    }
  }

  public class OyoahaLoadFilesThread extends OyoahaLoadFilesByExtentionThread
  {
    public OyoahaLoadFilesThread(int fid, File currentDirectory)
    {
       super(fid, currentDirectory);
    }

    protected String getExtention(File file)
    {
      return null;
    }
  }

  public class DoChangeContents extends OyoahaSortFileArray implements Runnable
  {
    protected Object lock = new Object();
    protected int fid;

    public DoChangeContents()
    {
      this.mode = getMode();
      this.inverse = isInverse();
      file = new File[DELTA];
    }

    public DoChangeContents(int size)
    {
      this.mode = getMode();
      this.inverse = isInverse();
      file = new File[size];
    }

    protected void setFid(int fid)
    {
      this.fid = fid;
    }

    protected synchronized void cancel()
    {
      synchronized(lock)
      {
        file = null;
        count = 0;
      }
    }

    public void run()
    {
      if(fetchID == fid)
      {
        synchronized(lock)
        {
          if(count>0)
          {
            sort();

            //just in case...
            if(cached==null)
            {
              cached = new File[count];
              length = 0;
            }
            else
            if(length+count>=cached.length)
            {
              File[] tmp = new File[length+count];
              System.arraycopy(cached, 0, tmp, 0, length);
              cached = tmp;
            }

            System.arraycopy(file, 0, cached, length, count);
            int old = length;
            length += count;
            fireIntervalAdded(old, old+count-1);
          }
        }
      }
    }
  }

  public class ListFiles extends DoChangeContents
  {
    protected String suffix;

    public ListFiles(int size)
    {
      super(size);
      this.suffix = "";
    }

    public ListFiles(String suffix)
    {
      super(DELTA);
      this.suffix = suffix;
    }

    public ListFiles(String suffix, int size)
    {
      super(size);
      this.suffix = suffix;
    }

    public String toString()
    {
      return suffix;
    }
  }
}