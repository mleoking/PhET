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

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaFileView extends FileView
{
  public final static String HARDDRIVE_TYPE = "hd";
  public final static String FLOPPY_TYPE = "floppy";
  public final static String FOLDER_TYPE = "folder";
  public final static String FILE_TYPE = "file";

  public final static String MY_DOCUMENT_TYPE = "doc";
  public final static String MY_COMPUTER_TYPE = "computer";
  public final static String MY_HOMEPAGE_TYPE = "web";
  public final static String MY_APPLICATION_TYPE = "app";
  public final static String DESKTOP_TYPE = "desktop";

  public final static String CURRENT_TYPE = "current";
  public final static String HOME_TYPE = "home";

  protected Hashtable iconCache;
  protected Hashtable preference;
  protected boolean changed;

  protected JFileChooser filechooser;
  protected OyoahaFileSystemAdaptor adaptor;

  public OyoahaFileView(JFileChooser filechooser)
  {
    this.filechooser = filechooser;

    iconCache = new Hashtable();
    preference = new OyoahaProperties();

    String os = System.getProperty("os.name");
    os = os.toLowerCase();

    //load preference...
    File load = new File(System.getProperty("user.home"), ".filechooser" + File.separator + "OyoahaFileChooser.ini");

    if(load.exists())
    {
      try
      {
        ((OyoahaProperties)preference).load(new FileInputStream(load), false);
      }
      catch(Exception e)
      {

      }
    }
    else //try to whish...*/
    {
      /**
      * organize
      * sort
      */
      if(os.startsWith("windows"))
      {
        preference.put("organize", OyoahaDirectoryModel.getMode(OyoahaDirectoryModel.USE_EXTENTION)); //extention
        preference.put("sort", OyoahaDirectoryModel.getSort(OyoahaDirectoryModel.BY_NAME)); //name
      }
      else
      if(os.startsWith("macos"))
      {
        preference.put("organize", OyoahaDirectoryModel.getMode(OyoahaDirectoryModel.USE_TYPE)); //type
        preference.put("sort", OyoahaDirectoryModel.getSort(OyoahaDirectoryModel.BY_NAME)); //name
      }
      else
      if(os.equals("sunos") || os.equals("solaris"))
      {
        preference.put("organize", OyoahaDirectoryModel.getMode(OyoahaDirectoryModel.USE_CASESENSITIVE_EXTENTION)); //case sensitive extention
        preference.put("sort", OyoahaDirectoryModel.getSort(OyoahaDirectoryModel.BY_CASESENSITIVE_NAME)); //case sensitive name
      }
      else //linux? freebsd? beos? as400? aix?
      {
        preference.put("organize", OyoahaDirectoryModel.getMode(OyoahaDirectoryModel.USE_CASESENSITIVE_EXTENTION)); //case sensitive extention
        preference.put("sort", OyoahaDirectoryModel.getSort(OyoahaDirectoryModel.BY_CASESENSITIVE_NAME)); //case sensitive name
      }
    }

    //TODO load specific FileSystemAdaptor...
    //if version 1.4...
    
    if(OyoahaUtilities.isVersion("1.4"))
    adaptor = new OyoahaDefaultFileSystemAdaptor2();
    else
    adaptor = new OyoahaDefaultFileSystemAdaptor();
  }

  public JFileChooser getFileChooser()
  {
    return filechooser;
  }

  //----------------------------------------------------------------------------
  //DISPOSE FUNCTION
  //----------------------------------------------------------------------------

  public void dispose()
  {
    saveFileChooserPreference();
    clearIconCache();
    //TODO clear all other resource
  }

  public void clearIconCache()
  {
    iconCache.clear();
  }

  //----------------------------------------------------------------------------
  //MANAGE FILE CHOOSER PREFERENCE
  //----------------------------------------------------------------------------

  public Object getFileChooserPreference(Object key)
  {
    return preference.get(key);
  }

  public Object putFileChooserPreference(Object key, Object value)
  {
    changed = true;
    return preference.put(key, value);
  }

  private void saveFileChooserPreference()
  {
    if(changed)
    {
      File load = new File(System.getProperty("user.home"), ".filechooser" + File.separator + "OyoahaFileChooser.ini");

      String parent = load.getParent();

      if(parent!=null)
      {
        File tmp = new File(parent);

        if(!tmp.exists())
        {
          tmp.mkdirs();
        }
      }

      try
      {
        ((OyoahaProperties)preference).save(new FileOutputStream(load), "created by OYOAHALOOKANDFEEL", false);
      }
      catch(Exception ex)
      {

      }

      changed = false;
    }
  }

  //----------------------------------------------------------------------------
  //MANAGE VIRTUAL FOLDER - RECENT - FAVORITE
  //----------------------------------------------------------------------------

  public OyoahaVectorFolder getRecentFolder()
  {
    return OyoahaVectorFolder.getVirtualFolder("recent");
  }

  public void addRecentFile(File file)
  {
    OyoahaVectorFolder recent = OyoahaVectorFolder.getVirtualFolder("recent");
    recent.addFile(file, 30);
    recent.dispose();
  }

  public void removeRecentFile(File file)
  {
    OyoahaVectorFolder recent = OyoahaVectorFolder.getVirtualFolder("recent");
    recent.removeFile(file);
    recent.dispose();
  }

  public void clearRecent()
  {
    OyoahaVectorFolder.deleteVectorFolder("recent");
  }

  public OyoahaVectorFolder getFavoriteFolder()
  {
    return OyoahaVectorFolder.getVirtualFolder("favorite");
  }

  public void addFavoriteFile(File file)
  {
    OyoahaVectorFolder favorite = OyoahaVectorFolder.getVirtualFolder("favorite");
    favorite.addFile(file);
    favorite.dispose();
  }

  public void removeFavoriteFile(File file)
  {
    OyoahaVectorFolder favorite = OyoahaVectorFolder.getVirtualFolder("favorite");
    favorite.removeFile(file);
    favorite.dispose();
  }

  public void clearFavorite()
  {
    OyoahaVectorFolder.deleteVectorFolder("favorite");
  }

  //----------------------------------------------------------------------------
  //FILE SYSTEM - LINK - ROOTS
  //----------------------------------------------------------------------------

  public File resolveFile(File file)
  {
    return adaptor.resolve(file);
  }

  public File[] getChild(File file)
  {
    if(file instanceof OyoahaVectorFolder)
    {
      ((OyoahaVectorFolder)file).listFiles();
    }
    else
    if(file.isDirectory())
    {
      String[] s = file.list();
      File[] files = new File[s.length];

      for (int i=0;i<s.length;i++)
      {
        files[i] = resolveFile(new File(file, s[i]));
      }

      return files;
    }

    return null;
  }

  /**
   * return the system specifique file root system
   */
  public OyoahaDirectoryComboBoxModel getFileSystem()
  {
    // for the next version need more work :-(
    //model.add(new OyoahaDirectoryComboBoxNode(getRecentFolder(), 0));
    //model.add(new OyoahaDirectoryComboBoxNode(getFavoriteFolder(), 0));
//System.out.println("used????");
    return new OyoahaDirectoryComboBoxModel(getFileChooser());
  }
  
  public void getFileSystem(OyoahaDirectoryComboBoxModel model)
  {
    // for the next version need more work :-(
    //model.add(new OyoahaDirectoryComboBoxNode(getRecentFolder(), 0));
    //model.add(new OyoahaDirectoryComboBoxNode(getFavoriteFolder(), 0));
//System.out.println("getFileSystem -> " + model + " " + adaptor);
    adaptor.getFileSystem(model);
  }

  //----------------------------------------------------------------------------
  //ACTION ON FILE
  //----------------------------------------------------------------------------

  /**
   * platform specifique file operation
   */
  public boolean deleteFile(File file)
  {
    //TODO nextVersion ask to a OyoahaFileSystemAdaptor

    return file.delete();
  }

  public boolean renameFile(File file, String newname)
  {
    //TODO nextVersion ask to a OyoahaFileSystemAdaptor

    String parent = file.getParent();

    if(parent!=null)
    {
      File newFile = new File(parent, newname);
      return file.renameTo(newFile);
    }

    return false;
  }

  //TODO nextVersion add move copy file operation

  //----------------------------------------------------------------------------
  //VIEW METHOD
  //----------------------------------------------------------------------------

  /**
   * methods to get file information
   */
  public String getName(File file)
  {
    if(file instanceof OyoahaVectorFolder)
    {
      String name = file.getName();
      return name.substring(0,name.lastIndexOf("."));
    }

    return adaptor.getName(file);
  }

  public String getExtention(File file, boolean caseSensitive)
  {
    return adaptor.getExtention(file, caseSensitive);
  }

  public String getType(File file, boolean caseSensitive)
  {
    return adaptor.getType(file, caseSensitive);
  }

  public String getDescription(File file)
  {
    return adaptor.getDescription(file);
  }

  public String getTypeDescription(File file)
  {
    return adaptor.getTypeDescription(file);
  }

  public Icon getIcon(File file)
  {
    if(file instanceof OyoahaVectorFolder)
    {
      return adaptor.getIcon(file);
    }

    String key;

    if(adaptor.useGenericIcon(file))
    key = adaptor.getType(file, adaptor.isCaseSensitive()) + "-" + adaptor.getExtention(file, adaptor.isCaseSensitive());
    else
    key = file.getPath();

    if(iconCache.containsKey(key))
    {
      return (Icon)iconCache.get(key);
    }

    Icon icon = adaptor.getIcon(file);
    iconCache.put(key, icon);

    return icon;
  }

  public Color getColor(File file)
  {
    return adaptor.getColor(file);
  }

  public Boolean isTraversable(File file)
  {
    return adaptor.isTraversable(file);
  }

  public Boolean isHidden(File file)
  {
    return adaptor.isHidden(file);
  }
}