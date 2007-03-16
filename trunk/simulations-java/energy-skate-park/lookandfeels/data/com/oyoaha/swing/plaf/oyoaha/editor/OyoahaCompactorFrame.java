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
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaCompactorFrame extends JFrame implements ActionListener
{
    protected File openDirectory = new File(System.getProperty("user.dir"));
    
    protected JCheckBox rManifest;
    
    protected JCheckBox rTree;
    protected JCheckBox rExamples;
    protected JCheckBox rFilechooser;
    protected JCheckBox rInternalFrame;
    protected JCheckBox rMenu;
    protected JCheckBox rOptionPane;
    protected JCheckBox rSplitPane;
    protected JCheckBox rTabbedPane;
    protected JCheckBox rTable;
    protected JCheckBox rColorchooser;
    protected JCheckBox rSound;
    protected JCheckBox rJavaFile;
    
    protected JRadioButton rJavaKeep;
    protected JRadioButton rJava1_2;
    protected JRadioButton rJava1_3;
    
    protected OyoahaCompactor compactor;

    protected JButton cancel;
    protected JButton ok;
    protected JButton chooseTargetFile;
    protected JButton chooseSourceFile;

    protected boolean exitOnClose;

    public OyoahaCompactorFrame(boolean exitOnClose)
    {
        this.setTitle("compress...");
        
        this.exitOnClose = exitOnClose;
        
        if (exitOnClose)
        {
            addWindowListener (new WindowAdapter()
            {
              public void windowClosing(WindowEvent e)
              {
                System.exit(0);
              }
            });
        }
    
        compactor = new OyoahaCompactor();
        
        getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,1));
        
        chooseSourceFile = new JButton("choose the source jar...");
        chooseTargetFile = new JButton("choose the target jar...");
        
        chooseSourceFile.setHorizontalAlignment(SwingConstants.LEFT);
        chooseTargetFile.setHorizontalAlignment(SwingConstants.LEFT);
        
        chooseSourceFile.addActionListener(this);
        chooseTargetFile.addActionListener(this);
        
        panel.add(chooseSourceFile);
        panel.add(chooseTargetFile);
        
        getContentPane().add(panel, BorderLayout.NORTH);
        
        rExamples = new JCheckBox("remove Examples");
        
        rTree = new JCheckBox("remove JTree support");
        rFilechooser = new JCheckBox("remove JFilechooser support");
        rInternalFrame = new JCheckBox("remove JInternalFrame support");
        rMenu = new JCheckBox("remove JMenu support");
        rOptionPane = new JCheckBox("remove JOptionPane support");
        rSplitPane = new JCheckBox("remove JSplitPane support");
        rTabbedPane = new JCheckBox("remove JTabbedPane support");
        rTable = new JCheckBox("remove JTable support");
        rColorchooser = new JCheckBox("remove JColorChooser support");
        
        rSound = new JCheckBox("remove Sound support");
        rJavaFile = new JCheckBox("remove all java source file");

        rExamples.addActionListener(this);
        rTree.addActionListener(this);
        rFilechooser.addActionListener(this);
        rInternalFrame.addActionListener(this);
        rMenu.addActionListener(this);
        rOptionPane.addActionListener(this);
        rSound.addActionListener(this);
        rSplitPane.addActionListener(this);
        rTabbedPane.addActionListener(this);
        rTable.addActionListener(this);
        rColorchooser.addActionListener(this);
        rJavaFile.addActionListener(this);

        rJavaKeep = new JRadioButton("leave java1 & java2 support");
        rJava1_2 = new JRadioButton("remove java1 support");
        rJava1_3 = new JRadioButton("remove java2 support");

        rJavaKeep.addActionListener(this);
        rJava1_2.addActionListener(this);
        rJava1_3.addActionListener(this);
        
        ButtonGroup group = new ButtonGroup();
        group.add(rJavaKeep);
        group.add(rJava1_2);
        group.add(rJava1_3);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(rExamples);
        panel.add(rTree);
        panel.add(rFilechooser);
        panel.add(rInternalFrame);
        panel.add(rMenu);
        panel.add(rOptionPane);
        panel.add(rSplitPane);
        panel.add(rTabbedPane);
        panel.add(rTable);
        panel.add(rColorchooser);
        
        panel.add(rSound);
        panel.add(rJavaFile);
        
        panel.add(rJavaKeep);
        panel.add(rJava1_2);
        panel.add(rJava1_3);

        useDefaultConfiguration();
        
        JScrollPane s = new JScrollPane(panel);
        Dimension d = s.getPreferredSize();
        d.height = d.height/2;
        d.width += 40;
        s.setPreferredSize(d);
        getContentPane().add(s);
        
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        cancel = new JButton("cancel");
        ok = new JButton("ok");
        ok.setEnabled(false);
        
        cancel.addActionListener(this);
        ok.addActionListener(this);
        
        panel.add(cancel);
        panel.add(ok);
        
        getContentPane().add(panel, BorderLayout.SOUTH);
        
        pack();
        
        Dimension dim = getToolkit().getScreenSize();
        d = getPreferredSize();

        if (d.width>dim.width)
        {
            d.width = dim.width;
        }

        if (d.height>dim.height)
        {
            d.height = dim.height;
        }

        setBounds((dim.width-d.width)/2, (dim.height-d.height)/2, d.width, d.height);
        
        if (exitOnClose)
        {
            setVisible(true);
        }
    }
    
    public void actionPerformed(ActionEvent event)
    {
        AbstractButton button = (AbstractButton)event.getSource();
        
        if (button==rManifest)
        {
            compactor.setrManifest(button.isSelected());
        }
        else
        if (button==rTree)
        {
            compactor.setrTree(button.isSelected());
        }
        else
        if (button==rExamples)
        {
            compactor.setrExamples(button.isSelected());
            compactor.setrApplet(button.isSelected());
        }
        else
        if (button==rFilechooser)
        {
            compactor.setrFilechooser(button.isSelected());
        }
        else
        if (button==rInternalFrame)
        {
            compactor.setrInternalFrame(button.isSelected());
        }
        else
        if (button==rMenu)
        {
            compactor.setrMenu(button.isSelected());
        }
        else
        if (button==rOptionPane)
        {
            compactor.setrOptionPane(button.isSelected());
        }
        else
        if (button==rSound)
        {
            compactor.setrSound(button.isSelected());
        }
        else
        if (button==rSplitPane)
        {
            compactor.setrSplitPane(button.isSelected());
        }
        else
        if (button==rTabbedPane)
        {
            compactor.setrTabbedPane(button.isSelected());
        }
        else
        if (button==rTable)
        {
            compactor.setrTable(button.isSelected());
        }
        else
        if (button==rColorchooser)
        {
            compactor.setrColorchooser(button.isSelected());
        }
        else
        if (button==rJavaFile)
        {
            compactor.setrJavaFile(button.isSelected());
        }
        else
        if (button==rJavaKeep)
        {
            compactor.setrJava1_2(!button.isSelected());
            compactor.setrJava1_3(!button.isSelected());
        }
        else
        if (button==rJava1_2)
        {
            compactor.setrJava1_2(button.isSelected());
        }
        else
        if (button==rJava1_3)
        {
            compactor.setrJava1_3(button.isSelected());
        }
        else
        if (button==cancel)
        {
            //exit - ask confirmation?
            if(exitOnClose)
            System.exit(0);
            else
            dispose();  
        }
        else
        if (button==ok)
        {
            //compact
            compactor.compact();
            
            if(exitOnClose)
            System.exit(0);
        }
        else
        if (button==chooseSourceFile)
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new JarFileFilter());
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            chooser.setCurrentDirectory(openDirectory);

            int result = chooser.showOpenDialog(null);

            if(result==JFileChooser.APPROVE_OPTION)
            {
              openDirectory = chooser.getCurrentDirectory();
              File f = chooser.getSelectedFile();

              if(f!=null && f.exists())
              {
                File file = compactor.getTargetFile();
                
                if (file!=null)
                {
                    if (f.equals(file))
                    {
                        JOptionPane.showMessageDialog(this, "Target and Source can't be the same file", "wrong source file", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        compactor.setSourceFile(f);
                        button.setText("source: " + f.getName());
                        ok.setEnabled(true);
                    }
                }
                else
                {
                    compactor.setSourceFile(f);
                    button.setText("source: " + f.getName());
                }
              }
            } 
        }
        else
        if (button==chooseTargetFile)
        {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new JarFileFilter());
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            chooser.setCurrentDirectory(openDirectory);

            int result = chooser.showSaveDialog(null);

            if(result==JFileChooser.APPROVE_OPTION)
            {
              openDirectory = chooser.getCurrentDirectory();
              File f = chooser.getSelectedFile();

              if(f!=null)
              {
                File file = compactor.getSourceFile();
                
                if (file!=null)
                {
                    if (f.equals(file))
                    {
                        JOptionPane.showMessageDialog(this, "Target and Source can't be the same file", "wrong target file", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    {
                        compactor.setTargetFile(f);
                        button.setText("target: " + f.getName());
                        ok.setEnabled(true);
                    }
                }
                else
                {
                    compactor.setTargetFile(f);
                    button.setText("target: " + f.getName());
                }
              }
            } 
        }
    }
    
    protected void openConfigurationFile()
    {
        //update all ui component...
    }
    
    protected void saveConfigurationFile()
    {
        //save current configuration on the disk
    }
    
    protected void useDefaultConfiguration()
    {
        rExamples.setSelected(true);

        rTree.setSelected(false);
        rFilechooser.setSelected(false);
        rInternalFrame.setSelected(false);
        rMenu.setSelected(false);
        rOptionPane.setSelected(false);
        rSplitPane.setSelected(false);
        rTabbedPane.setSelected(false);
        rTable.setSelected(false);
        rColorchooser.setSelected(false);

        rSound.setSelected(false);
        rJavaFile.setSelected(true);

        rJavaKeep.setSelected(true);
        rJava1_2.setSelected(false);
        rJava1_3.setSelected(false);

        compactor.setrExamples(true);
        compactor.setrApplet(true);

        compactor.setrTree(false);
        compactor.setrFilechooser(false);
        compactor.setrInternalFrame(false);
        compactor.setrMenu(false);
        compactor.setrOptionPane(false);
        compactor.setrSplitPane(false);
        compactor.setrTabbedPane(false);
        compactor.setrTable(false);
        compactor.setrColorchooser(false);

        compactor.setrSound(false);
        compactor.setrJavaFile(true);

        compactor.setrJava1_2(false);
        compactor.setrJava1_3(false);
    }
    
    public final static void main(String[] arg)
    {
        try
        {
          OyoahaLookAndFeel look = new OyoahaLookAndFeel();
          UIManager.setLookAndFeel(look);
        }
        catch(Exception e)
        {

        }
        
        //start a new OyoahaCompactorFrame...
        new OyoahaCompactorFrame(true);
    }
    
    protected class JarFileFilter extends javax.swing.filechooser.FileFilter
    {
        public boolean accept(File _file)
        {
          if(_file.isDirectory())
          {
            return true;
          }

          String name = _file.getName();

          if(name.endsWith(".jar") || name.endsWith(".zip"))
          {
            return true;
          }

          return false;
        }

        public String getDescription()
        {
          return "jar/zip file";
        }
    }
}