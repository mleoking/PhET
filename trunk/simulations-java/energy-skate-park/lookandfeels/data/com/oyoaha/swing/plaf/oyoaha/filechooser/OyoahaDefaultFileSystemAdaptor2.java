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
import java.lang.reflect.*;
import javax.swing.*;

import sun.awt.shell.*;

public class OyoahaDefaultFileSystemAdaptor2 extends OyoahaDefaultFileSystemAdaptor
{
    protected static boolean initialized;
    
    protected static boolean useShellFolder;
    protected static boolean windowPlatform;
    
    public OyoahaDefaultFileSystemAdaptor2()
    {
        if (!initialized)
        {
            useShellFolder = false;
        
            try
            {
                Class classShellFolder = Class.forName("sun.awt.shell.ShellFolderManager");
        
                if (classShellFolder!=null)
                {
                    Method[] methods = classShellFolder.getMethods();
                
                    for (int i=0;i<methods.length;i++)
                    {
                        if (methods[i].getName()=="get")
                        {
                            useShellFolder = true;
                            break;
                        }
                    }
                }
                
                windowPlatform = System.getProperty("os.name").startsWith("windows");
            }
            catch(Exception e)
            {
        
            }
            
            initialized = true;
        }
    }

    /**
    * return the common root file system
    */
    public void getFileSystem(OyoahaDirectoryComboBoxModel modelToFill)
    {
       if (!useShellFolder)
       {
        super.getFileSystem(modelToFill);
       }
       else
       {
            try
            {
               File[] files = (File[])ShellFolder.get("fileChooserComboBoxFolders");
               int[] depths = calculateDepths(files);
               
               if (extraFile!=null)
               {
                    extraFile.clear();
                    extraFile = null;
               }
               
               extraFile = new Hashtable();

               for(int i=0;i<files.length;i++)
               {
                    extraFile.put(files[i], OyoahaFileView.FOLDER_TYPE);
               }
               
               for(int i=0;i<files.length;i++)
               {
                    modelToFill.add(new OyoahaDirectoryComboBoxNode(files[i], depths[i]));
               }
            }
            catch(Exception e)
            {
            
            }
       }
    }
  
    protected int[] calculateDepths(File[] files)
    {
        int[] depths = new int[files.length];
    
        for (int i = 0; i < depths.length; i++)
        {
            File dir = files[i];
            File parent = dir.getParentFile();
            depths[i] = 0;
        
            if (parent != null) 
            {
                for (int j = i-1; j >= 0; j--)
                {
                    if (parent.equals(files[j]))
                    {
                        depths[i] = depths[j] + 1;
                        break;
                    }
                }
            }
        }
        
        return depths;
    }
    
    public boolean useGenericIcon(File file)
    {
        if (windowPlatform && !file.isDirectory())
        {
            if (file.getName()!=null && file.getName().endsWith(".exe"))
            {
                return false;
            }
        }
        
        return super.useGenericIcon(file);
    }
    
    public String getName(File file)
    {
        if (useShellFolder && file instanceof ShellFolder)
        {
            return ((ShellFolder)file).getDisplayName();
        }

        return super.getName(file);
    }
    
    public Icon getIcon(File file)
    {
        if (!useShellFolder)
        {
            return super.getIcon(file);
        }
        
        if (file instanceof ShellFolder)
        {
            Image image = ((ShellFolder)file).getIcon(false);
            
            if(image!=null)
            return new ImageIcon(image);
        }
        else
        {
            try
            {
                ShellFolder sfolder = ShellFolder.getShellFolder(file);
                Image image = sfolder.getIcon(false);
            
                if(image!=null)
                return new ImageIcon(image);
            }
            catch(Exception e)
            {
            
            }
        }
        
        return super.getIcon(file);
    }
    
    public File resolve(File file)
    {    
        if (!useShellFolder)
        {
            return file;
        }
        
        if (file instanceof ShellFolder)
        {
            try
            {
                if (((ShellFolder)file).isLink())
                {
                    return ((ShellFolder)file).getLinkLocation();
                }
            }
            catch(Exception e)
            {
            
            }
        }
        
        return super.resolve(file);
    }
    
    public static Hashtable getExtraFile()
    {
        if(extraFile==null)
        {
          extraFile = new Hashtable();
        }
        
        return extraFile;
    }
}