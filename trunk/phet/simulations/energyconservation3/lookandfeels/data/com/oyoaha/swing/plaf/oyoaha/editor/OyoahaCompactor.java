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

package com.oyoaha.swing.plaf.oyoaha.editor;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class OyoahaCompactor
{
    protected File target;
    protected File source;
    
    protected boolean rManifest;
    
    protected boolean rTree;
    protected boolean rExamples;
    protected boolean rFilechooser;
    protected boolean rInternalFrame;
    protected boolean rMenu;
    protected boolean rOptionPane;
    protected boolean rSound;
    protected boolean rSplitPane;
    protected boolean rTabbedPane;
    protected boolean rTable;
    protected boolean rColorchooser;
    protected boolean rApplet;
    protected boolean rJavaFile;
    
    protected boolean rDebug = true;
    protected boolean rEditor = true;
    
    protected boolean rJava1_2;
    protected boolean rJava1_3;
    
    public void setSourceFile(File source)
    {
        this.source = source;
    }
    
    public File getSourceFile()
    {
        return source;
    }
    
    public void setTargetFile(File target)
    {
        this.target = target;
    }

    public File getTargetFile()
    {
        return target;
    }
    
    public void setrManifest(boolean bool)
    {
        rManifest=bool;
    }
    
    public void setrTree(boolean bool)
    {
        rTree=bool;
    }

    public void setrExamples(boolean bool)
    {
        rExamples=bool;
    }

    public void setrFilechooser(boolean bool)
    {
        rFilechooser=bool;
    }

    public void setrInternalFrame(boolean bool)
    {
        rInternalFrame=bool;
    }

    public void setrJava1_2(boolean bool)
    {
        rJava1_2=bool;
    }

    public void setrJava1_3(boolean bool)
    {
        rJava1_3=bool;
    }

    public void setrMenu(boolean bool)
    {
        rMenu=bool;
    }

    public void setrOptionPane(boolean bool)
    {
        rOptionPane=bool;
    }

    public void setrSound(boolean bool)
    {
        rSound=bool;
    }

    public void setrSplitPane(boolean bool)
    {
        rSplitPane=bool;
    }

    public void setrTabbedPane(boolean bool)
    {
        rTabbedPane=bool;
    }

    public void setrTable(boolean bool)
    {
        rTable=bool;
    }

    public void setrColorchooser(boolean bool)
    {
        rColorchooser=bool;
    }
    
    public void setrJavaFile(boolean bool)
    {
        rJavaFile=bool;
    }
    
    public void setrApplet(boolean bool)
    {
        rApplet=bool;
    }
    
    public void setrDebug(boolean bool)
    {
        rDebug=bool;
    }
    
    public void setrEditor(boolean bool)
    {
        rEditor=bool;
    }

    public boolean getrManifest()
    {
        return rManifest;
    }
    
    public boolean getrTree()
    {
        return rTree;
    }

    public boolean getrExamples()
    {
        return rExamples;
    }

    public boolean getrFilechooser()
    {
        return rFilechooser;
    }

    public boolean getrInternalFrame()
    {
        return rInternalFrame;
    }

    public boolean getrJava1_2()
    {
        return rJava1_2;
    }

    public boolean getrJava1_3()
    {
        return rJava1_3;
    }

    public boolean getrMenu()
    {
        return rMenu;
    }

    public boolean getrOptionPane()
    {
        return rOptionPane;
    }

    public boolean getrSound()
    {
        return rSound;
    }

    public boolean getrSplitPane()
    {
        return rSplitPane;
    }

    public boolean getrTabbedPane()
    {
        return rTabbedPane;
    }

    public boolean getrTable()
    {
        return rTable;
    }

    public boolean getrColorchooser()
    {
        return rColorchooser;
    }
    
    public boolean getrJavaFile()
    {
        return rJavaFile;
    }
    
    public boolean getrApplet()
    {
        return rApplet;
    }
    
    public void loadConfiguration(File f)
    {
        Properties conf = new Properties();
        
        try
        {
            conf.load(new FileInputStream(f));
        }
        catch(Exception e)
        {
        
        }
        
        if (source==null)
        {
            String p = conf.getProperty("source");
            
            if(p!=null)
            source = new File(p);
        }
        
        if (target==null)
        {
            String p = conf.getProperty("target");
            
            if(p!=null)
            target = new File(p);
        }
        
        rManifest = getBoolean(conf.getProperty("rManifest", "false"));
        
        rTree = getBoolean(conf.getProperty("rTree", "false"));
        rExamples = getBoolean(conf.getProperty("rExamples", "false"));
        rFilechooser = getBoolean(conf.getProperty("rFilechooser", "false"));
        rInternalFrame = getBoolean(conf.getProperty("rInternalFrame", "false"));
        rJava1_2 = getBoolean(conf.getProperty("rJava1_2", "false"));
        rJava1_3 = getBoolean(conf.getProperty("rJava1_3", "false"));
        rMenu = getBoolean(conf.getProperty("rMenu", "false"));
        rOptionPane = getBoolean(conf.getProperty("rOptionPane", "false"));
        rSound = getBoolean(conf.getProperty("rSound", "false"));
        rSplitPane = getBoolean(conf.getProperty("rSplitPane", "false"));
        rTabbedPane = getBoolean(conf.getProperty("rTabbedPane", "false"));
        rTable = getBoolean(conf.getProperty("rTable", "false"));
        rColorchooser = getBoolean(conf.getProperty("rColorchooser", "false"));
        rJavaFile = getBoolean(conf.getProperty("rJavaFile", "false"));
        rApplet = getBoolean(conf.getProperty("rApplet", "false"));
    }
    
    protected boolean getBoolean(String string)
    {
        try
        {
            return Boolean.valueOf(string).booleanValue();
        }
        catch(Exception e)
        {
        
        }
        
        return false;
    }
    
    public void saveConfiguration(File f, boolean saveSource, boolean saveTarget)
    {
        Properties conf = new Properties();
        
        if (source!=null && saveSource)
        {
            conf.put("source", source.getPath());
        }
        
        if (target!=null && saveTarget)
        {
            conf.put("target", target.getPath());
        }
        
        conf.put("rManifest", String.valueOf(rManifest));
        conf.put("rTree", String.valueOf(rTree));
        conf.put("rExamples", String.valueOf(rExamples));
        conf.put("rFilechooser", String.valueOf(rFilechooser));
        conf.put("rInternalFrame", String.valueOf(rInternalFrame));
        conf.put("rJava1_2", String.valueOf(rJava1_2));
        conf.put("rJava1_3", String.valueOf(rJava1_3));
        conf.put("rMenu", String.valueOf(rMenu));
        conf.put("rOptionPane", String.valueOf(rOptionPane));
        conf.put("rSound", String.valueOf(rSound));
        conf.put("rSplitPane", String.valueOf(rSplitPane));
        conf.put("rTabbedPane", String.valueOf(rTabbedPane));
        conf.put("rTable", String.valueOf(rTable));
        conf.put("rColorchooser", String.valueOf(rColorchooser));
        conf.put("rJavaFile", String.valueOf(rJavaFile));
        conf.put("rApplet", String.valueOf(rApplet));
        
        try
        {
            conf.save(new FileOutputStream(f), "OyoahaLnFCompactor");
        }
        catch(Exception e)
        {
        	
        }
    }

    public void compact()
    {
        Vector remove = new Vector();
        
        if (rManifest)
        {
            remove.addElement("meta-inf/manifest.mf");
        }
        
        if (rTree)
        {
            fill(remove, "/rules/rTree.list");
        }

        if (rExamples)
        {
            fill(remove, "/rules/rExamples.list");
        }

        if (rFilechooser)
        {
            fill(remove, "/rules/rFilechooser.list");
        }

        if (rInternalFrame)
        {
            fill(remove, "/rules/rInternalFrame.list");
        }

        if (rApplet)
        {
            fill(remove, "/rules/rApplet.list");
        }

        if (rMenu)
        {
            fill(remove, "/rules/rMenu.list");
        }

        if (rOptionPane)
        {
            fill(remove, "/rules/rOptionPane.list");
        }

        if (rSound)
        {
            fill(remove, "/rules/rSound.list");
        }

        if (rSplitPane)
        {
            fill(remove, "/rules/rSplitPane.list");
        }

        if (rTabbedPane)
        {
            fill(remove, "/rules/rTabbedPane.list");
        }

        if (rTable)
        {
            fill(remove, "/rules/rTable.list");
        }

        if (rColorchooser)
        {
            fill(remove, "/rules/rColorchooser.list");
        }

        if (rDebug)
        {
            fill(remove, "/rules/rDebug.list");
        }
        
        if (rEditor)
        {
            fill(remove, "/rules/rEditor.list");
        }
        
        try
        {
            ZipFile sourceFile = new ZipFile(source);
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target));
            //out.setLevel(9);
            
            Enumeration enumeration = sourceFile.entries();

            while (enumeration.hasMoreElements())
            {
                ZipEntry entry = (ZipEntry)enumeration.nextElement();
                
                if (entry.getName().toLowerCase().equals("com/oyoaha/swing/plaf/oyoaha/uidefaults2.properties"))
                {
                    if (!rJava1_3)
                    {
                        writeUIDefault(sourceFile, out, entry);
                    }
                }
                else
                if (entry.getName().toLowerCase().equals("com/oyoaha/swing/plaf/oyoaha/uidefaults.properties"))
                {
                    if (!rJava1_2)
                    {
                        writeUIDefault(sourceFile, out, entry);
                    }
                }
                else
                if (rJavaFile && entry.getName().toLowerCase().endsWith(".java"))
                {
                    //do nothing
                }
                else
                if (!remove.contains(entry.getName().toLowerCase()))
                {
                    //save this entry...
                    add(sourceFile, out, entry);
                }
            }
            
            out.finish();
            out.close();
            sourceFile.close();
        }
        catch(Exception e)
        {
           
        }
    }
    
    protected void writeUIDefault(ZipFile sourceFile, ZipOutputStream out, ZipEntry esource) throws IOException
    {
        Properties properties = new Properties();
        properties.load(sourceFile.getInputStream(esource));

        if (rTree)
        {
            properties.remove("TreeUI");
        }

        if (rFilechooser)
        {
            properties.remove("FileChooserUI");
        }

        if (rInternalFrame)
        {
            properties.remove("InternalFrameUI");
            properties.remove("DesktopIconUI");
            properties.remove("DesktopPaneUI");
        }

        if (rMenu)
        {
            properties.remove("MenuBarUI");
            properties.remove("MenuItemUI");
            properties.remove("MenuUI");
            properties.remove("CheckBoxMenuItemUI");
            properties.remove("RadioButtonMenuItemUI");
        }

        if (rOptionPane)
        {
            properties.remove("OptionPaneUI");
        }

        if (rSplitPane)
        {
            properties.remove("SplitPaneUI");
        }

        if (rTabbedPane)
        {
            properties.remove("TabbedPaneUI");
        }

        if (rTable)
        {
            properties.remove("TableUI");
            properties.remove("TableHeaderUI");
        }

        if (rColorchooser)
        {
            properties.remove("ColorChooserUI");
        }
        
        ZipEntry tmp = new ZipEntry(esource.getName());
        out.putNextEntry(tmp);
        properties.save(out, "");
        out.closeEntry();
    }
    
    protected void add(ZipFile sourceFile, ZipOutputStream out, ZipEntry esource) throws IOException
    {
        ZipEntry entry = new ZipEntry(esource.getName());
        InputStream in = sourceFile.getInputStream(esource);
        out.putNextEntry(entry);
        
        byte[] buf = new byte[10240];
        
        for ( int i = 0 ; ; i++ ) 
        {
            int len = in.read(buf);
            
            if (len<0)
            {
                break;
            }
            
            out.write(buf, 0, len);
        }
        
        in.close();
        out.closeEntry();
    }
    
    protected void fill(Vector vector, String rc)
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(rc)));
        
            String line = reader.readLine();
            
            while (line!=null)
            {
                vector.addElement(line.toLowerCase());
                line = reader.readLine();
            }
        
            reader.close();
        }
        catch(Exception e)
        {
       
        }
    }
    
    
    public final static void main(String[] arg)
    {
        File f;
        
        if(arg.length>0)
        {
            f = new File(arg[0]);
        }
        else
        {
            f = new File(System.getProperty("user.dir"), "rawlnf.zip");    
        }
        
        String parent = f.getParent();
        
        //used for oyoaha lookanfeel package...
        OyoahaCompactor compactor = new OyoahaCompactor();
        compactor.setSourceFile(f);
        compactor.setTargetFile(new File(parent, "oalnf.jar"));
        
        compactor.setrJavaFile(true);
        compactor.setrApplet(true);
        
        compactor.setrExamples(false);
        
        compactor.setrColorchooser(false);
        compactor.setrFilechooser(false);
        compactor.setrInternalFrame(false);
        compactor.setrManifest(false);
        compactor.setrMenu(false);
        compactor.setrOptionPane(false);
        compactor.setrSound(false);
        compactor.setrSplitPane(false);
        compactor.setrTabbedPane(false);
        compactor.setrTable(false);
        compactor.setrTree(false);
        compactor.setrJava1_2(false);
        
        compactor.compact();
        
        //used for oyoaha lookanfeel applet...
        compactor = new OyoahaCompactor();
        compactor.setSourceFile(f);
        compactor.setTargetFile(new File(parent, "appletlnf.jar"));
        
        compactor.setrJavaFile(true);
        compactor.setrExamples(true);
        
        compactor.setrApplet(false);

        compactor.setrColorchooser(true);
        compactor.setrFilechooser(true);
        compactor.setrInternalFrame(true);
        compactor.setrManifest(true);
        compactor.setrMenu(false);
        compactor.setrOptionPane(true);
        compactor.setrSound(true);
        compactor.setrSplitPane(true);
        compactor.setrTabbedPane(true);
        compactor.setrTable(true);
        compactor.setrTree(true);
        compactor.setrJava1_2(false);
        
        compactor.compact();
        
        //used for debug oyoaha lookanfeel package...
        compactor = new OyoahaCompactor();
        compactor.setSourceFile(f);
        compactor.setTargetFile(new File(parent, "oalnf_d.jar"));
        
        compactor.setrJavaFile(true);
        compactor.setrApplet(true);
        
        compactor.setrExamples(false);
        
        compactor.setrDebug(false);
        
        compactor.setrColorchooser(false);
        compactor.setrFilechooser(false);
        compactor.setrInternalFrame(false);
        compactor.setrManifest(false);
        compactor.setrMenu(false);
        compactor.setrOptionPane(false);
        compactor.setrSound(false);
        compactor.setrSplitPane(false);
        compactor.setrTabbedPane(false);
        compactor.setrTable(false);
        compactor.setrTree(false);
        compactor.setrJava1_2(false);
        
        compactor.compact();
    }
}