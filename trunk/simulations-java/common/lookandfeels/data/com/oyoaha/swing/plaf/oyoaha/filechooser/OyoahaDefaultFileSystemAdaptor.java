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
import java.awt.*;
import javax.swing.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaDefaultFileSystemAdaptor implements OyoahaFileSystemAdaptor
{
  /**
   * return the common root file system
   */
  public void getFileSystem(OyoahaDirectoryComboBoxModel modelToFill)
  {
    //-- no desktop found...
    //
    //FAVORITE
    //RECENT
    //mydocument
    //floppy <-sort by name
    //hardrive <-sort by name
    //extra folder <-sort by name

    //-- desktop without mycomputer
    //
    //FAVORITE
    //RECENT
    //desktop
    //  mydocument
    //  floppy <-sort by name
    //  hardrive <-sort by name
    //  desktop sub directory <-sort by name
    //extra folder <-sort by name

    //-- mycomputer without desktop
    //
    //FAVORITE
    //RECENT
    //mydocument
    //mycomputer
    //  floppy <-sort by name
    //  hardrive <-sort by name
    //extra folder <-sort by name

    //-- desktop and mycomputer
    //
    //FAVORITE
    //RECENT
    //desktop
    //  mydocument
    //  mycomputer
    //    floppy <-sort by name
    //    hardrive <-sort by name
    //  desktop sub directory <-sort by name
    //extra folder <-sort by name

    if (extraFile!=null)
    {
        extraFile.clear();
        extraFile = null;
    }
    
    extraFile = getExtraFile();

    //array to sort file...
    OyoahaSortFileArray floppyArray = new OyoahaSortFileArray();
    OyoahaSortFileArray hardArray = new OyoahaSortFileArray();
    OyoahaSortFileArray extraArray = new OyoahaSortFileArray();
    OyoahaSortFileArray desktopArray = null;

    //special file...
    File desktop = null;
    File document = null;
    File computer = null;

    Enumeration e = extraFile.keys();

    while(e.hasMoreElements())
    {
        File f = (File)e.nextElement();
        Object o = extraFile.get(f);

        if(o.toString().equals(OyoahaFileView.DESKTOP_TYPE) && desktop==null)
        {
            desktop = f;

            desktopArray = new OyoahaSortFileArray();

            if(f instanceof File)
            {
                String[] list = f.list();

                for(int i=0;i<list.length;i++)
                {
                    File ff = new File(((File)f), list[i]);

                    if(ff.isDirectory())
                    {
                        desktopArray.add(ff);
                    }
                }
            }
        }
        else
        if(o.toString().equals(OyoahaFileView.HARDDRIVE_TYPE))
        {
            if(!hardArray.contains(f))
            hardArray.add(f);
        }
        else
        if(o.toString().equals(OyoahaFileView.FLOPPY_TYPE))
        {
            if(!floppyArray.contains(f))
            floppyArray.add(f);
        }
        else
        if(o.toString().equals(OyoahaFileView.MY_DOCUMENT_TYPE) && document==null)
        {
            document = f;
        }
        else
        if(o.toString().equals(OyoahaFileView.MY_COMPUTER_TYPE) && computer==null)
        {
            computer = f;
        }
        else
        {
            extraArray.add(f);
        }
    }

    if(desktop!=null)
    {
        modelToFill.add(new OyoahaDirectoryComboBoxNode(desktop, 0));

        if(document!=null)
        {
            modelToFill.add(new OyoahaDirectoryComboBoxNode(document, 1));
        }

        if(computer!=null)
        {
            modelToFill.add(new OyoahaDirectoryComboBoxNode(computer, 1));
        }

        floppyArray.sort();

        int step = (computer==null)? 1 : 2;

        for(int i=0;i<floppyArray.getSize();i++)
        {
            modelToFill.add(new OyoahaDirectoryComboBoxNode(floppyArray.get(i), step));
        }

        hardArray.sort();

        for(int i=0;i<hardArray.getSize();i++)
        {
            modelToFill.add(new OyoahaDirectoryComboBoxNode(hardArray.get(i), step));
        }

        if(desktopArray!=null)
        {
            desktopArray.sort();

            for(int i=0;i<desktopArray.getSize();i++)
            {
                modelToFill.add(new OyoahaDirectoryComboBoxNode(desktopArray.get(i), 1));
            }
        }
    }
    else
    {
        if(document!=null)
        {
            modelToFill.add(new OyoahaDirectoryComboBoxNode(document, 0));
        }

        if(computer!=null)
        {
            modelToFill.add(new OyoahaDirectoryComboBoxNode(computer, 0));
        }

        floppyArray.sort();

        int step = (computer==null)? 0 : 1;

        for(int i=0;i<floppyArray.getSize();i++)
        {
            modelToFill.add(new OyoahaDirectoryComboBoxNode(floppyArray.get(i), step));
        }

        hardArray.sort();

        for(int i=0;i<hardArray.getSize();i++)
        {
            modelToFill.add(new OyoahaDirectoryComboBoxNode(hardArray.get(i), step));
        }
    }

    extraArray.sort();

    for(int i=0;i<extraArray.getSize();i++)
    {
        modelToFill.add(new OyoahaDirectoryComboBoxNode(extraArray.get(i), 0));
    }
  }

    /**
    * give a chance to resolve link/alias
    */
    public File resolve(File file)
    {
        return file;
    }

    /**
    * this file system is case sensitive for its type - extention
    */
    public boolean isCaseSensitive()
    {
        return false;
    }

    /**
    * this file can use a generic icon given by its (type + "-" + extention) string?
    */
    public boolean useGenericIcon(File file)
    {
        Hashtable extra = getExtraFile();
        return !extra.containsKey(file);
    }

    public String getName(File file)
    {
        String name = file.getName();

        if(name!=null && name.length()>0)
        return name;

        name = file.getPath();

        if(name!=null && name.length()>0)
        return name;

        return File.separator;    
    }

    public String getExtention(File file, boolean caseSensitive)
    {
        if(file==null)
        return null;

        String name = file.getName();
        int index = name.lastIndexOf('.');

        if(index>=0)
        {
            if(caseSensitive)
            return name.substring(index);

            return name.substring(index).toLowerCase();
        }

        return null;
    }

    public String getType(File file, boolean caseSensitive)
    {
        Hashtable extra = getExtraFile();

        if(extra.containsKey(file))
        {
            return (String)extra.get(file);
        }

        if(file.isDirectory())
        {
            if(file.getParent()==null)
            return OyoahaFileView.HARDDRIVE_TYPE;

            return OyoahaFileView.FOLDER_TYPE;
        }
        else
        {
            String ext = getExtention(file, false);

            if(ext!=null)
            {
                Hashtable table = getFileType();

                if(table.containsKey(ext))
                {
                    return (String)table.get(ext);
                }
            }
        }

        return OyoahaFileView.FILE_TYPE;
    }

    public String getDescription(File file)
    {
        return null;
    }

    public String getTypeDescription(File file)
    {
        Hashtable table = getFileInformation();

        String type = getType(file, false);

        if(type==null)
        {
            return null;
        }

        Object o = table.get(type+"_info");

        if(o!=null)
        {
            o.toString();
        }

        return null;
    }

  public Color getColor(File file)
  {
    Hashtable table = getFileInformation();

    String type = getType(file, false);

    if(type==null)
    {
      return null;
    }

    Object o = table.get(type+"_color");

    if(o==null)
    {
      return null;
    }

    if(o instanceof Color)
    {
      return (Color)o;
    }

    if(o instanceof String)
    {
      Color c = OyoahaThemeLoaderUtilities.readColor((String)o);
      table.put(type+"_color", c);
      return c;
    }

    return null;
  }

  public Boolean isTraversable(File file)
  {
    if (file.isDirectory() || file instanceof OyoahaVectorFolder)
    {
      return Boolean.TRUE;
    }
    else
    {
      return Boolean.FALSE;
    }
  }

  public Boolean isHidden(File file)
  {
    String name = file.getName();

    if(name != null && name.charAt(0) == '.')
    {
      return Boolean.TRUE;
    }
    else
    {
      return Boolean.FALSE;
    }
  }

  public Icon getIcon(File file)
  {
    Hashtable table = getFileInformation();

    if(file instanceof OyoahaVectorFolder)
    {
      return (Icon)table.get("folder_icon");
    }

    String type = getType(file, false);

    if(type==null) //normaly impossible
    {
      if(file.isDirectory())
      type = OyoahaFileView.FOLDER_TYPE;
      else
      type = OyoahaFileView.FILE_TYPE;
    }

    Object o = table.get(type+"_icon");

    if(o==null)
    {
      if(file.isDirectory())
      return (Icon)table.get("folder_icon");
      else
      return (Icon)table.get("file_icon");
    }

    if(o instanceof Icon)
    {
      return (Icon)o;
    }

    if(o instanceof String)
    {
      //TODO try to load from file?

      try
      {
        ImageIcon icon = new OyoahaImageIcon(Thread.currentThread().getContextClassLoader().getResource("/com/oyoaha/swing/plaf/oyoaha/rc/"+o), (String)o);
        table.put(type+"_icon", icon);
        return icon;
      }
      catch(Exception e)
      {
        //remove from table
        table.remove(type+"_icon");
      }
    }

    return (Icon)table.get("file_icon");
  }

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  // FILE ROUTINE
  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

  protected static Hashtable extraFile; //-> File : type (extraFile can be desktop or floppy drive)
  protected static Hashtable fileType; //-> extention/file/string : type
  protected static Hashtable fileInformation; //-> type + _foo : Object (String/Icon/Color)

  /**
   *
   * //->windows
   *
   * //->"desktop = File"
   * //->"doc = File"
   * //->"web = File"
   * //->"app = File"
   *
   */
  public static Hashtable getExtraFile()
  {
    //load only root by default
    if(extraFile==null)
    {
      extraFile = new Hashtable();

      File load = new File(System.getProperty("user.home"), ".filechooser" + File.separator + "extra.ini");

      if(load.exists()) //erase old version
      {
        load.delete();
      }

      //try to whish...
      String os = System.getProperty("os.name");
      os = os.toLowerCase();

      if(os.startsWith("windows"))
      {
        ResourceBundle bundle = ResourceBundle.getBundle("com.oyoaha.swing.plaf.oyoaha.filechooser.windowsFilename");

        File desktopRoot = new File(System.getProperty("user.home"));

        File desktop = new File(desktopRoot, bundle.getString(OyoahaFileView.DESKTOP_TYPE));

        if(desktop.exists())
        {
          extraFile.put(desktop, OyoahaFileView.DESKTOP_TYPE);
        }

        //doc...
        File doc = new File(desktopRoot, bundle.getString(OyoahaFileView.MY_DOCUMENT_TYPE));

        if(doc.exists())
        {
          extraFile.put(doc, OyoahaFileView.MY_DOCUMENT_TYPE);
        }

        //app..
        File app = new File("c:\\" + bundle.getString(OyoahaFileView.MY_APPLICATION_TYPE));

        if(app.exists())
        {
          extraFile.put(app, OyoahaFileView.MY_APPLICATION_TYPE);
        }

        //floppy A...
        extraFile.put(new File("a:\\"), OyoahaFileView.FLOPPY_TYPE);
      }
      /*else
      if(os.startsWith("macos"))
      {

      }
      else
      if(os.equals("sunos") || os.equals("solaris"))
      {

      }*/
      else //linux? freebsd? beos? as400? aix?
      {
        ResourceBundle bundle = ResourceBundle.getBundle("com.oyoaha.swing.plaf.oyoaha.filechooser.linuxFilename");

        //KDE DESKTOP
        File desktop = new File(System.getProperty("user.home"), bundle.getString(OyoahaFileView.DESKTOP_TYPE));

        if(desktop.exists())
        {
          extraFile.put(desktop, OyoahaFileView.DESKTOP_TYPE);
        }

        //WEB
        File web = new File(System.getProperty("user.home"), "httpd/html");

        if(web.exists())
        {
          extraFile.put(web, OyoahaFileView.MY_HOMEPAGE_TYPE);
        }

        //MNT hard drive and floppy (c d e cdrom floppy)
        File mnt = new File("/mnt");

        if(mnt.exists() && mnt.isDirectory())
        {
          String[] list = mnt.list();

          for(int i=0;i<list.length;i++)
          {
            File f = new File(mnt, list[i]);

            if(f.isDirectory())
            {
              if(list[i].equalsIgnoreCase(bundle.getString(OyoahaFileView.FLOPPY_TYPE)))
              extraFile.put(f, OyoahaFileView.FLOPPY_TYPE);
              else
              extraFile.put(f, OyoahaFileView.HARDDRIVE_TYPE);
            }
          }
        }
      }

      File[] roots = javax.swing.filechooser.FileSystemView.getFileSystemView().getRoots();

      for(int i=0;i<roots.length;i++)
      {
        extraFile.put(roots[i], OyoahaFileView.HARDDRIVE_TYPE);
      }

      //current
      File current = new File(System.getProperty("user.dir"));

      if(!extraFile.containsKey(current))
      extraFile.put(current, OyoahaFileView.CURRENT_TYPE);

      //home
      File home = new File(System.getProperty("user.home"));

      if(!extraFile.containsKey(home))
      extraFile.put(home, OyoahaFileView.HOME_TYPE);
    }

    return extraFile;
  }

  /**
  * //->".txt = name"
  */
  public static Hashtable getFileType()
  {
    if(fileType==null)
    {
      fileType = new Hashtable();

      //load the fileInformation from resource...
      //file information can be locale sensitive
      try
      {
        ResourceBundle bundle = ResourceBundle.getBundle("com.oyoaha.swing.plaf.oyoaha.filechooser.type");

        Enumeration e = bundle.getKeys();

        while(e.hasMoreElements())
        {
          String key = e.nextElement().toString();
          fileType.put(key, bundle.getString(key));
        }
      }
      catch(Exception e)
      {

      }

      File load = new File(System.getProperty("user.home"), ".filechooser" + File.separator + "type.ini");

      if(load.exists()) //erase old version
      {
        load.delete();
      }
    }

    return fileType;
  }

  /**
  * //->"name_icon = String/Icon"
  * //->"name_big_icon = String/Icon"
  * //->"name_info = String"
  */
  public static Hashtable getFileInformation()
  {
    if(fileInformation==null)
    {
      fileInformation = new Hashtable();

      //load the fileInformation from resource...
      //file information can be locale sensitive
      try
      {
        ResourceBundle bundle = ResourceBundle.getBundle("com.oyoaha.swing.plaf.oyoaha.filechooser.info");

        Enumeration e = bundle.getKeys();

        while(e.hasMoreElements())
        {
          String key = e.nextElement().toString();
          fileInformation.put(key, bundle.getString(key));
        }
      }
      catch(Exception e)
      {
      
      }

      File load = new File(System.getProperty("user.home"), ".filechooser" + File.separator + "info.ini");

      if(load.exists()) //erase old version
      {
        load.delete();
      }

      //mine info (special)
      fileInformation.put("file_info", UIManager.getString("FileChooser.fileDescriptionText"));
      fileInformation.put("folder_info", UIManager.getString("FileChooser.directoryDescriptionText"));

      //mine icon (special)
      fileInformation.put("folder_icon", UIManager.getIcon("FileView.directoryIcon"));
      fileInformation.put("file_icon", UIManager.getIcon("FileView.fileIcon"));
      fileInformation.put("hd_icon", UIManager.getIcon("FileView.hardDriveIcon"));
      fileInformation.put("floppy_icon", UIManager.getIcon("FileView.floppyDriveIcon"));
    }

    return fileInformation;
  }
}