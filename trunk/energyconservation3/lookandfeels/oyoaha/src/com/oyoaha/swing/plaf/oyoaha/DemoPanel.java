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

package com.oyoaha.swing.plaf.oyoaha;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DemoPanel extends JPanel implements ActionListener
{
  private final static Insets insets = new Insets(4,4,4,4);
  
  private JTabbedPane tabbedPane;
  private JDesktopPane desktop;
  private JButton paletteButton;
  private JTextArea text;

  private JSplitPane jSplitPane;
  private JList list;
  private JTree tree;
  private JInternalFrame palette;
  private JTable table;

  private JToolBar tools;
  private JRadioButton radioButton;
  private JCheckBox checkBox;
  private JComboBox comboBox;
  private JComboBox comboBox2;
  private JToggleButton toggleButton;
  private JSlider slider;     
  private JTextField textField;
  private JScrollBar scrollBar;
  private JLabel label;
  
  private JToolBar jtools;
  private JRadioButton jradioButton;
  private JCheckBox jcheckBox;
  private JComboBox jcomboBox;
  private JToggleButton jtoggleButton;
  private JLabel jlabel;

  public DemoPanel()
  {
    tabbedPane = new JTabbedPane();
    tabbedPane.setVisible(false);
    setLayout(new BorderLayout());
    add(tabbedPane, BorderLayout.CENTER);

    Thread t = new Thread(new LoadDemoPanel());
    t.start();
  }

  public JTabbedPane getTabbedPane()
  {
    return tabbedPane;
  }
  
  public void setEnabled(boolean enabled)
  {
    if(tabbedPane!=null)
    tabbedPane.setEnabled(enabled);
    
    if (desktop!=null)
    {
        JInternalFrame[] f = desktop.getAllFrames();
        
        for(int i=0;i<f.length;i++)
        {
            if(f[i]!=null)
            f[i].setEnabled(enabled);
        }
    }
    
    if(text!=null)
    text.setEnabled(enabled);

    if(table!=null)
    table.setEnabled(enabled);

    if(paletteButton!=null)
    paletteButton.setEnabled(enabled);
    
    if(tree!=null)
    tree.setEnabled(enabled);

    if(list!=null)    
    list.setEnabled(enabled);

    if(jSplitPane!=null)    
    jSplitPane.setEnabled(enabled);
    
    if (tools!=null)
    {
        for(int i=0;;i++)
        {
            Component c = tools.getComponentAtIndex(i);
            
            if(c==null)
            break;
            
            c.setEnabled(enabled);
        }    
    }
    
    if(radioButton!=null)     
    radioButton.setEnabled(enabled);
    
    if(checkBox!=null) 
    checkBox.setEnabled(enabled);
    
    if(comboBox!=null) 
    comboBox.setEnabled(enabled);
    
    if(comboBox2!=null) 
    comboBox2.setEnabled(enabled);
    
    if(toggleButton!=null) 
    toggleButton.setEnabled(enabled);
    
    if(slider!=null) 
    slider.setEnabled(enabled);
    
    if(textField!=null) 
    textField.setEnabled(enabled);
    
    if(scrollBar!=null) 
    scrollBar.setEnabled(enabled);
    
    if(label!=null) 
    label.setEnabled(enabled);
    
    if (jtools!=null)
    {
        for(int i=0;;i++)
        {
            Component c = jtools.getComponentAtIndex(i);
            
            if(c==null)
            break;
            
            c.setEnabled(enabled);
        }    
    }
    
    if(jradioButton!=null)     
    jradioButton.setEnabled(enabled);
    
    if(jcheckBox!=null) 
    jcheckBox.setEnabled(enabled);
    
    if(jcomboBox!=null) 
    jcomboBox.setEnabled(enabled);
    
    if(jtoggleButton!=null) 
    jtoggleButton.setEnabled(enabled);
    
    if(jlabel!=null) 
    jlabel.setEnabled(enabled);  
  }

  public Component getRoot()
  {
    return SwingUtilities.getRoot(this);
  }

  public void actionPerformed(ActionEvent e)
  {
    openImage();
  }

  private JComponent createTableDemoPanel()
  {
    JPanel panel = new JPanel();

    DefaultTableModel model = new DefaultTableModel();
    table  = new JTable(model);

    for(int i=0;i<5;i++)
    {
      model.addColumn("Col " + i);
    }

    for(int i=0;i<10;i++)
    {
      Object[] o = new Object[5];

      for(int u=0;u<5;u++)
      {
        if(u<3)
        o[u] = new Integer(i);
        else
        if(u==3)
        o[u] = new Boolean(true);
        else
        o[u] = "cell " + i;
      }

      model.addRow(o);
    }

    table.setRowHeight(22);

    panel.setLayout(new BorderLayout());
    JScrollPane scroll = new JScrollPane(table);
    scroll.setPreferredSize(new Dimension(420,320));
    panel.add(scroll);

    return panel;
  }

  private JComponent createInfoDemoPanel()
  {
    JPanel panel = new JPanel();

    text = new JTextArea();
    text.setEditable(false);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);

    updateLnFInformation();

    panel.setLayout(new BorderLayout());
    JScrollPane scroll = new JScrollPane(text);
    scroll.setPreferredSize(new Dimension(420,320));
    panel.add(scroll);

    return panel;
  }

  private JComponent createWindowsDemoPanel()
  {
    desktop = new JDesktopPane();

    palette = new JInternalFrame("Palette");
    palette.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
    palette.setResizable(true);
    palette.setClosable(false);
    palette.setMaximizable(false);
    palette.setIconifiable(false);
    palette.getContentPane().setLayout(new BorderLayout());
    palette.setLocation(30,30);

    paletteButton = new JButton("Open a picture...");
    paletteButton.addActionListener(this);

    palette.getContentPane().add(paletteButton, BorderLayout.CENTER);
    palette.pack();

    desktop.add(palette, JDesktopPane.PALETTE_LAYER);
    palette.setVisible(true);

    desktop.setPreferredSize(new Dimension(420,320));

    return desktop;
  }

  private JComponent createSplitDemoPanel()
  {
    JPanel jPanel = new JPanel();

    String[] data = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
    list = new JList(data);
    tree = new JTree();
    tree.setEditable(true);

    JScrollPane scroll1 = new JScrollPane(tree);
    JScrollPane scroll2 = new JScrollPane(list);
    scroll1.setPreferredSize(new Dimension(210,320));
    scroll2.setPreferredSize(new Dimension(210,320));

    jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll1, scroll2);
    jSplitPane.setOneTouchExpandable(true);
    jSplitPane.setContinuousLayout(true);
    
    jPanel.setLayout(new BorderLayout());
    jPanel.add(jSplitPane, BorderLayout.CENTER);

    return jPanel;
  }

  private JComponent createClassicDemoPanel()
  {
    JPanel p = new JPanel();
    p.setLayout(new GridBagLayout());

    tools = new JToolBar();

    for(int i=0;i<5;i++)
    {
      JButton tb = new JButton("tool " + i);
      tb.setToolTipText("Toolbar button" + i);

      tools.add(tb);
    }

    tools.setFloatable(false);

    radioButton = new JRadioButton();
    radioButton.setText("JRadioButton");
    radioButton.setToolTipText("Radio button");

    checkBox = new JCheckBox();
    checkBox.setText("JCheckBox");
    checkBox.setToolTipText("Checkbox");

    comboBox = new JComboBox();
    comboBox.setToolTipText("Combobox");

    for(int i=0;i<20;i++)
    {
      comboBox.addItem("String " + i);
    }

    comboBox2 = new JComboBox();
    comboBox2.setToolTipText("Editable combobox");

    for(int i=0;i<20;i++)
    {
      comboBox2.addItem("String " + i);
    }

    comboBox2.setEditable(true);

    toggleButton = new JToggleButton("JToggleButton");
    toggleButton.setToolTipText("Toggle button");

    slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
    slider.setLabelTable(slider.createStandardLabels(25));
    slider.setMinorTickSpacing(5);
    slider.setMajorTickSpacing(25);
    slider.setPaintTrack(true);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.setToolTipText("Slider");
    Dimension d = slider.getPreferredSize();
    slider.setMinimumSize(d);

    textField = new JTextField("hello world");
    textField.setToolTipText("Textfield");
    d = textField.getPreferredSize();
    textField.setMinimumSize(d);

    scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
    scrollBar.setToolTipText("Scrollbar");
    d = scrollBar.getPreferredSize();
    d = new Dimension(200, d.height);
    scrollBar.setPreferredSize(d);
    scrollBar.setMinimumSize(d);
    JPanel pscrollBar = new JPanel();
    pscrollBar.setLayout(new BorderLayout());
    pscrollBar.add(scrollBar);
    pscrollBar.setPreferredSize(d);
    pscrollBar.setMinimumSize(d);

    label = new JLabel();
    label.setText("Label");
    label.setToolTipText("Label");

    installComponent(p, tools,                  GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 0, 3, 1, 0.0D, 0.0D);
    installComponent(p, radioButton,            GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 1, 1, 1, 0.0D, 0.0D);
    installComponent(p, checkBox,               GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 1, 1, 1, 1, 0.0D, 0.0D);
    installComponent(p, toggleButton,           GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 2, 1, 1, 1, 0.0D, 0.0D);
    installComponent(p, slider,                 GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 2, 3, 1, 0.0D, 0.0D);
    installComponent(p, comboBox,               GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 3, 1, 1, 0.0D, 0.0D);
    installComponent(p, comboBox2,              GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 1, 3, 2, 1, 0.0D, 0.0D);
    installComponent(p, pscrollBar,             GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 4, 3, 1, 0.0D, 0.0D);
    installComponent(p, textField,              GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 5, 1, 1, 0.0D, 0.0D);
    installComponent(p, label,                  GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 2, 5, 1, 1, 0.0D, 0.0D);

    p.setPreferredSize(new Dimension(420,320));
    return p;
  }

  private JComponent createClassicDemoPanel2()
  {
    JPanel p = new JPanel();
    p.setLayout(new GridBagLayout());

    jtools = new JToolBar();

    for(int i=0;i<5;i++)
    {
      JButton tb = new JButton("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font color=\"#FF00FF\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">Button " + i + "</font></html>");
      tb.setToolTipText("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font color=\"#FF00FF\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">Button " + i + "</font></html>");
      jtools.add(tb);
    }

    jtools.setFloatable(false);

    jradioButton = new JRadioButton();
    jradioButton.setText("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font color=\"#FF00FF\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">RadioButton</font></html>");
    jradioButton.setToolTipText("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font color=\"#FF00FF\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">RadioButton</font></html>");

    jcheckBox = new JCheckBox();
    jcheckBox.setText("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font size=\"2\" color=\"#FF00FF\" face=\"Arial, Helvetica, sans-serif\">CheckBox</font></html>");
    jcheckBox.setToolTipText("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font size=\"2\" color=\"#FF00FF\" face=\"Arial, Helvetica, sans-serif\">CheckBox</font></html>");

    jcomboBox = new JComboBox();
    jcomboBox.setToolTipText("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font color=\"#FF00FF\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">ComboBox</font></html>");

    for(int i=0;i<20;i++)
    {
      jcomboBox.addItem("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font color=\"#FF00FF\" size=\"2\" face=\"Arial, Helvetica, sans-serif\">ComboBox " + i + "</font></html>");
    }

    jtoggleButton = new JToggleButton("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font size=\"2\" color=\"#FF00FF\" face=\"Arial, Helvetica, sans-serif\">ToggleButton</font></html>");
    jtoggleButton.setToolTipText("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font size=\"2\" color=\"#FF00FF\" face=\"Arial, Helvetica, sans-serif\">ToggleButton</font></html>");

    jlabel = new JLabel();
    jlabel.setText("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font size=\"2\" color=\"#FF00FF\" face=\"Arial, Helvetica, sans-serif\">Label</font></html>");
    jlabel.setToolTipText("<html><b><font color=\"#0000FF\" face=\"Arial, Helvetica, sans-serif\">J</font></b><font size=\"2\" color=\"#FF00FF\" face=\"Arial, Helvetica, sans-serif\">Label</font></html>");

    installComponent(p, jtools,                  GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 0, 1, 1, 0.0D, 0.0D);
    installComponent(p, jradioButton,            GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 1, 1, 1, 0.0D, 0.0D);
    installComponent(p, jcheckBox,               GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 2, 1, 1, 0.0D, 0.0D);
    installComponent(p, jtoggleButton,           GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 3, 1, 1, 0.0D, 0.0D);
    installComponent(p, jcomboBox,               GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 4, 1, 1, 0.0D, 0.0D);
    installComponent(p, jlabel,                  GridBagConstraints.CENTER, GridBagConstraints.NONE,       insets, 0, 5, 1, 1, 0.0D, 0.0D);

    return p;
  }

  private JInternalFrame createInternalFrame(String _iconName)
  {
    JInternalFrame frame = new JInternalFrame("JInternalFrame");
    frame.getContentPane().setLayout(new BorderLayout());
    frame.setResizable(true);
    frame.setClosable(true);
    frame.setMaximizable(true);
    frame.setIconifiable(true);

    try
    {
      Icon icon = new ImageIcon(_iconName);

      if(icon!=null)
      frame.getContentPane().add(new JLabel(icon), BorderLayout.CENTER);
      else
      return null;
    }
    catch (Exception ex)
    {
      return null;
    }

    frame.pack();

    return frame;
  }

  protected class LoadDemoPanel implements Runnable
  {
    public void run()
    {
      JTabbedPane tabbedPane = getTabbedPane();
      tabbedPane.setVisible(true);
      
      tabbedPane.add(createClassicDemoPanel(), "Buttons");
      tabbedPane.add(createSplitDemoPanel(), "Tree&List"); 

      Component c = getRoot();
      int count = 0;

      while(c==null && count<10)
      {
        try
        {
          Thread.sleep(100);
        }
        catch (Exception ex)
        {

        }

        count++;
        c = getRoot();
      }

      if(c!=null && c instanceof Window)
      {
        ((Window)c).pack();
      }
      
      try
      {
        Thread.sleep(100);
      }
      catch (Exception ex)
      {

      }
         
      tabbedPane.add(createInfoDemoPanel(), "Info");
      tabbedPane.add(createTableDemoPanel(), "Table");
      tabbedPane.add(createWindowsDemoPanel(), "Internal Frame");
      
      tabbedPane.add(createClassicDemoPanel2(), "<html><center><font color=\"#0000FF\"><b><u><font face=\"Arial, Helvetica, sans-serif\">h</font></u></b></font><font face=\"Arial, Helvetica, sans-serif\"><u><b><font color=\"#FF00FF\">t</font><font color=\"#0000FF\">m</font><font color=\"#FF00FF\">l</font></b></u></font></center></html>");

      if(!System.getProperty("os.name").startsWith("window") && c!=null && c instanceof Window)
      {
        ((Window)c).pack();
      }
    }
  }

  private void openImage()
  {
    JFileChooser chooser = new JFileChooser();

    chooser.setFileFilter(new ImageFileFilter());
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    int result = chooser.showOpenDialog(null);

    if(result==JFileChooser.APPROVE_OPTION)
    {
      File tmp = chooser.getSelectedFile();

      if(tmp==null || !tmp.exists())
      {
        return;
      }

      openImage(tmp.getPath());
    }
  }

  public void setColor(Color color)
  {
    if(desktop==null)
    {
      return;
    }

    JTabbedPane p = getTabbedPane();
    p.setBackgroundAt(0, color);
  }

  public void openImage(String path)
  {
    if(desktop==null)
    {
      return;
    }

    JInternalFrame frame = createInternalFrame(path);

    if(frame==null)
    {
      return;
    }

    frame.pack();
    desktop.add(frame, JDesktopPane.PALETTE_LAYER);
    frame.show();

    JTabbedPane p = getTabbedPane();
    p.setSelectedIndex(4);
  }

  public void updateLnFInformation()
  {
    if(palette!=null)
    {
      palette.pack();  
    }

    if(text==null)
    return;

    LookAndFeel lnf = UIManager.getLookAndFeel();

    text.setText("Name:\n  ");
    text.append(lnf.getName());
    text.append("\n\nVersion:\n  ");
    text.append(lnf.getID());
    text.append("\n\nDescription:\n  ");
    text.append(lnf.getDescription());
  }

  protected class ImageFileFilter extends javax.swing.filechooser.FileFilter
  {
    public boolean accept(File _file)
    {
      if(_file.isDirectory())
      {
        return true;
      }

      String name = _file.getName().toLowerCase();

      if(name.endsWith(".gif") || name.endsWith(".jpg") || name.endsWith(".jpeg"))
      {
        return true;
      }

      return false;
    }

    public String getDescription()
    {
      return "gif and jpeg files";
    }
  }
  
  public final static void installComponent(Container container, Component obj, int anchor, int constraints, Insets insets, int i, int j, int k, int l, double d, double d1)
  {
    GridBagLayout gridbaglayout = (GridBagLayout)container.getLayout();
    GridBagConstraints gridbagconstraints = new GridBagConstraints();
    gridbagconstraints.gridx = i;
    gridbagconstraints.gridy = j;
    gridbagconstraints.gridwidth = k;
    gridbagconstraints.gridheight = l;
    gridbagconstraints.weightx = d;
    gridbagconstraints.weighty = d1;
    gridbagconstraints.fill = constraints;
    gridbagconstraints.insets = insets;
    gridbagconstraints.anchor = anchor;

    container.add(obj);
    gridbaglayout.setConstraints(obj, gridbagconstraints);
  }
}