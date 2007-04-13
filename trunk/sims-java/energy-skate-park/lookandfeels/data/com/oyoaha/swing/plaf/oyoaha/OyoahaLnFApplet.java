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

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

public class OyoahaLnFApplet extends JApplet implements ActionListener
{
  private final static Insets insets = new Insets(6,6,6,6);

  public void init()
  {
    try 
    {
      OyoahaLookAndFeel lnf = new OyoahaLookAndFeel();

      URL url = getClass().getResource("/gradient.otm");  
      lnf.setOyoahaTheme(url);

      UIManager.setLookAndFeel(lnf);
 
    }
    catch (Exception ex) 
    {
    
    }
    
    JMenuBar menuBar = new JMenuBar();
    
    JMenu menu = new JMenu("oyoaha themes");

    JMenuItem item = menu.add(new JMenuItem("gradient"));
    item.addActionListener(this);
    item = menu.add(new JMenuItem("slushy5"));
    item.addActionListener(this);
    item = menu.add(new JMenuItem("slushy3"));
    item.addActionListener(this);
    item = menu.add(new JMenuItem("slushy"));
    item.addActionListener(this);
    
    menuBar.add(menu);
    
    setJMenuBar(menuBar);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(createClassicDemoPanel());
  }
  
  public void actionPerformed(ActionEvent event)
  {
    String name = event.getActionCommand();
 
    try 
    {
      OyoahaLookAndFeel lnf = new OyoahaLookAndFeel();

      URL url = getClass().getResource("/" + name + ".otm");  
      lnf.setOyoahaTheme(url);

      UIManager.setLookAndFeel(lnf);
      SwingUtilities.updateComponentTreeUI(this);
 
    }
    catch (Exception ex) 
    {
      
    }
  }

  private JComponent createClassicDemoPanel()
  {
    JPanel p = new JPanel();
    p.setLayout(new GridBagLayout());

    JToolBar tools = new JToolBar();

    for(int i=0;i<5;i++)
    {
      JButton tb = new JButton("tool " + i);
      tb.setToolTipText("Toolbar button " + i);
      tools.add(tb);
    }

    tools.setFloatable(false);

    JRadioButton radioButton = new JRadioButton();
    radioButton.setText("JRadioButton");
    radioButton.setToolTipText("Radio button");

    JCheckBox checkBox = new JCheckBox();
    checkBox.setText("JCheckBox");
    checkBox.setToolTipText("Checkbox");

    JButton button = new JButton();
    button.setText("JButton");
    button.setToolTipText("Button");

    JComboBox comboBox = new JComboBox();
    comboBox.setToolTipText("Combobox");

    for(int i=0;i<20;i++)
    {
      comboBox.addItem("String " + i);
    }

    JComboBox comboBox2 = new JComboBox();
    comboBox2.setToolTipText("Editable combobox");

    for(int i=0;i<20;i++)
    {
      comboBox2.addItem("String " + i);
    }

    comboBox2.setEditable(true);

    JToggleButton toggleButton = new JToggleButton("JToggleButton");
    toggleButton.setToolTipText("Toggle button");


    JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
    slider.setLabelTable(slider.createStandardLabels(25));
    slider.setMinorTickSpacing(5);
    slider.setMajorTickSpacing(25);
    slider.setPaintTrack(true);
    slider.setPaintTicks(true);
    slider.setPaintLabels(true);
    slider.setToolTipText("Slider");

    JTextField textField = new JTextField("hello world");
    textField.setToolTipText("Textfield");

    JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
    scrollBar.setPreferredSize(new Dimension(200, 17));
    scrollBar.setToolTipText("Scrollbar");

    JLabel label = new JLabel();
    label.setText("Label");
    label.setToolTipText("Label");

    installComponent(p, tools,                  GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 0, 3, 1, 0.0D, 0.0D);
    installComponent(p, radioButton,            GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 1, 1, 1, 0.0D, 0.0D);
    installComponent(p, checkBox,               GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 1, 1, 1, 1, 0.0D, 0.0D);
    installComponent(p, toggleButton,           GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 2, 1, 1, 1, 0.0D, 0.0D);
    installComponent(p, slider,                 GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 2, 3, 1, 0.0D, 0.0D);
    installComponent(p, comboBox,               GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 3, 1, 1, 0.0D, 0.0D);
    installComponent(p, comboBox2,              GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 1, 3, 2, 1, 0.0D, 0.0D);
    installComponent(p, scrollBar,              GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 4, 3, 1, 0.0D, 0.0D);
    installComponent(p, textField,              GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 0, 5, 1, 1, 0.0D, 0.0D);
    installComponent(p, label,                  GridBagConstraints.CENTER, GridBagConstraints.NONE, insets, 2, 5, 1, 1, 0.0D, 0.0D);

    return p;
  }
  
  private void installComponent(Container container, Component obj, int anchor, int constraints, Insets insets, int i, int j, int k, int l, double d, double d1)
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
  
  public void destroy() 
  {
	getContentPane().removeAll();
    
  }

  public void stop() 
  {
    getContentPane().removeAll();
  }
  
  public String getAppletInfo() 
  {
    return "(c) copyright Oyoaha 2001-2002";
  }
}
