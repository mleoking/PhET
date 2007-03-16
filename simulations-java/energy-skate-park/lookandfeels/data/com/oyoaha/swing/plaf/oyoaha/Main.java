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

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.plaf.metal.*;

import com.oyoaha.swing.plaf.oyoaha.editor.*;

public class Main extends JFrame implements ActionListener
{
  protected File openDirectory = new File(System.getProperty("user.dir"));

  protected JFrame preview;
  protected JFrame help;
  protected JFrame compress;

  protected static File currentTheme;
  protected static MetalTheme currentMetalTheme;

  protected boolean showHelp;
  protected boolean showCompress;
  
  protected JTextArea text;
  protected JCheckBox brollover;

  public Main()
  {
    if(currentTheme==null)
    {
      File tmp = new File(System.getProperty("user.dir"), "gradient.otm");

      if(tmp.exists())
      {
        currentTheme = tmp;
      }
    }

//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    try
    {
      OyoahaLookAndFeel look = new OyoahaLookAndFeel();
      UIManager.setLookAndFeel(look);
    }
    catch(Exception e)
    {

    }

//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    //JToolBar bar = new JToolBar();
    //bar.setFloatable(false);

    JButton btest = new JButton("Test");
    JButton bopen = new JButton("Select a oyoaha theme");
    JButton bcompactor = new JButton("Compress");
    JButton bhelp = new JButton("Help");

    brollover = new JCheckBox("Enabled rollover effect");
    brollover.setToolTipText("Enabled rollover effect for button, checkbox, scrollbar...");
    brollover.setSelected(true);

    btest.setToolTipText("Test oyoaha lookandfeel");
    bopen.setToolTipText("Select a oyoaha theme");
    bcompactor.setToolTipText("Utility to remove unused features from the oyoaha lookandfeel jar file");
    bhelp.setToolTipText("Show help");

    btest.setName("test");
    bopen.setName("open");
    bcompactor.setName("compress");
    bhelp.setName("help");

    btest.addActionListener(this);
    bopen.addActionListener(this);
    bcompactor.addActionListener(this);
    bhelp.addActionListener(this);

    Icon icon = null;

    try
    {
      icon = new ImageIcon(getClass().getResource("rc/test.gif"));
      btest.setIcon(icon);
    }
    catch (Exception ex)
    {

    }

    try
    {
      icon = new ImageIcon(getClass().getResource("rc/open.gif"));
      bopen.setIcon(icon);
    }
    catch (Exception ex)
    {
                                  
    }
    
    try
    {
      icon = new ImageIcon(getClass().getResource("rc/compress.gif"));
      bcompactor.setIcon(icon);
    }
    catch (Exception ex)
    {

    }

    try
    {
      icon = new ImageIcon(getClass().getResource("rc/help.gif"));
      bhelp.setIcon(icon);
    }
    catch (Exception ex)
    {

    }

    JPanel bar = new JPanel();
    bar.setLayout(new GridLayout(4,1));

    btest.setHorizontalAlignment(SwingConstants.LEFT);
    bopen.setHorizontalAlignment(SwingConstants.LEFT);
    bcompactor.setHorizontalAlignment(SwingConstants.LEFT);
    bhelp.setHorizontalAlignment(SwingConstants.LEFT);

    bar.add(btest);
    bar.add(bopen);
    bar.add(bcompactor);
    bar.add(bhelp);

//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(bar, BorderLayout.CENTER);
    getContentPane().add(brollover, BorderLayout.SOUTH);

    addWindowListener (new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });

    pack();
    
    Dimension dim = getToolkit().getScreenSize();
    Dimension d = getSize();
    
    if (d.width>dim.width)
    {
    
        d.width = dim.width;
    }

    if (d.height>dim.height)
    {
        d.height = dim.height;
    }

    setBounds((dim.width-d.width)/2, (dim.height-d.height)/2, d.width, d.height);

    if(currentTheme==null)
    {
      super.setVisible(true);
    }
    else
    {
      help = null;
      compress = null;
      test();
    }
//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    compress = new OyoahaCompactorFrame(false);
    help = new HelpFrame();
  }

  public void actionPerformed(ActionEvent e)
  {
    String name = ((Component)e.getSource()).getName();

    if (name.equals("test"))
    {
        test();
    }
    else
    if (name.equals("open"))
    {
        open();
    }
    else
    if (name.equals("compress"))
    {
        if(compress!=null)
        compress.setVisible(true);
    }    
    else
    if (name.equals("help"))
    {
        if(help!=null)
        help.setVisible(true);
    }    
  }

  protected void open()
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileFilter(new ThemeFileFilter());
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    chooser.setCurrentDirectory(openDirectory);

    int result = chooser.showOpenDialog(null);

    if(result==JFileChooser.APPROVE_OPTION)
    {
      openDirectory = chooser.getCurrentDirectory();
      File f = chooser.getSelectedFile();

      if(f!=null && f.exists())
      {
        currentTheme = f;
        setTitle(f.getPath());
      }
    }
  }

  public void setVisible(boolean _visible)
  {
    if(_visible)
    {
      try
      {
        MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
        OyoahaLookAndFeel look = new OyoahaLookAndFeel();
        UIManager.setLookAndFeel(look);
        SwingUtilities.updateComponentTreeUI(this);
        
        if(help!=null)
        SwingUtilities.updateComponentTreeUI(help);
        
        if(compress!=null)
        SwingUtilities.updateComponentTreeUI(compress);
      }
      catch(Exception e)
      {

      }

      if(help!=null && showHelp)
      {
        help.setVisible(true);
      }
      
      if (compress!=null && showCompress)
      {
        compress.setVisible(true);
      }
    }
    else
    {
      if(help!=null)
      {
        help.setVisible(false);
      }
      
      if (compress!=null)
      {
        showCompress = compress.isVisible();
        compress.setVisible(false);
      }
    }

    super.setVisible(_visible);
    
    if (_visible)
    {
        toFront();
    }
  }

  private void test()
  {
    if(preview==null)
    {
      try
      {
        if(currentMetalTheme!=null)
        {
          MetalLookAndFeel.setCurrentTheme(currentMetalTheme);
        }

        OyoahaLookAndFeel look = new OyoahaLookAndFeel(brollover.isSelected());
        
        if (currentTheme!=null)
        {
            look.setOyoahaTheme(currentTheme);
        }
        
        UIManager.setLookAndFeel(look);
      }
      catch(Exception e)
      {

      }

      preview = new PreviewFrame(this);

      if(currentTheme!=null)
      preview.setTitle(currentTheme.getName());
      else
      preview.setTitle("Use default theme");

      preview.setVisible(true);
      super.setVisible(false);
      preview.toFront();
    }
    else
    {
      try
      {
        if (currentMetalTheme!=null)
        {
            MetalLookAndFeel.setCurrentTheme(currentMetalTheme);
        }
        
        OyoahaLookAndFeel look = new OyoahaLookAndFeel(brollover.isSelected());

        if (currentTheme!=null)
        {
            look.setOyoahaTheme(currentTheme);
        }
        
        UIManager.setLookAndFeel(look);
        SwingUtilities.updateComponentTreeUI(preview);

        if(currentTheme!=null)
        preview.setTitle(currentTheme.getName());
        else
        preview.setTitle("Use default theme");

        preview.setVisible(true);
        super.setVisible(false);
        preview.toFront();
      }
      catch(Exception e)
      {

      }
    }
  }

//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
// MAIN
//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

  public static void main(String[] arg)
  {
    if(arg.length>0)
    {
      File f = new File(arg[0]);

      if(f.exists() && f.isFile())
      {
        if(f.getName().endsWith(".theme"))
        currentMetalTheme = new OyoahaMetalTheme(f);
        else
        currentTheme = f;
      }
    }

    new Main();
  }

//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
// Inner Class...
//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

  class HelpFrame extends JFrame
  {
    HelpFrame()
    {
      super("help");

      getContentPane().setLayout(new BorderLayout());

      try
      {
        URL url = null;
        String path = null;

        try
        {
          path = "rc/index.html";
          url = getClass().getResource(path);
        }
        catch (Exception ex1)
        {
          url = null;
        }

        if(url != null)
        {
          JEditorPane html = new JEditorPane(url);
          html.setEditable(false);
          //html.addHyperlinkListener(createHyperLinkListener());

          JScrollPane scroller = new JScrollPane();
          JViewport vp = scroller.getViewport();
          vp.add(html);

          getContentPane().add(scroller, BorderLayout.CENTER);
        }
      }
      catch (Exception ex2)
      {

      }

      addWindowListener (new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          setVisible(false);
          showHelp = false;
        }
      });

      JButton close = new JButton("close");

      close.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          setVisible(false);
          showHelp = false;
        }
      });

      JPanel p = new JPanel();
      p.setLayout(new FlowLayout(FlowLayout.RIGHT));
      p.add(close);
      getContentPane().add(p, BorderLayout.SOUTH);

      pack();

      Dimension dim = getToolkit().getScreenSize();
      Dimension d = new Dimension(440, 380);
      
      if (d.width>dim.width)
      {
        d.width = dim.width;
      }

      if (d.height>dim.height)
      {
        d.height = dim.height;
      }

      setBounds((dim.width-d.width)/2, (dim.height-d.height)/2, d.width, d.height);
    }

    public void setVisible(boolean _visible)
    {
      if(!showHelp && _visible)
      {
        showHelp = true;
      }

      super.setVisible(_visible);
    }
  }

  protected void forceRepaintAfterDialog()
  {
    repaint();
  }

  class PreviewFrame extends JFrame implements ActionListener
  {
    private JFrame parent;
    private JTabbedPane tabbedPane;
    private DemoPanel demo;
    private JColorChooser colorChooser;

    PreviewFrame(JFrame _parent)
    {
      parent = _parent;

      demo = new DemoPanel();
      tabbedPane = demo.getTabbedPane();

      //--------

      JMenuBar menuBar = new JMenuBar();
      menuBar.setOpaque(true);

      try
      {
        File file = new File(System.getProperty("user.dir"), "metal");

        if(file.exists() && file.isDirectory())
        {
          String list[] = file.list();

          Vector themes = new Vector();

          themes.addElement(new DefaultMetalTheme());

          for(int i=0;i<list.length;i++)
          {
            if(list[i].endsWith(".theme"))
            themes.addElement(new OyoahaMetalTheme(new File(file, list[i])));
          }

          JMenu themeMenu = new MetalThemeMenu("MetalTheme", themes);
          menuBar.add(themeMenu);
        }
      }
      catch (Exception e)
      {

      }

      JMenu tabbedPane = new JMenu("TabbedPane");

      JMenuItem top = new JCheckBoxMenuItem("TabbedPane.top");
      JMenuItem left = new JCheckBoxMenuItem("TabbedPane.left");
      JMenuItem bottom = new JCheckBoxMenuItem("TabbedPane.bottom");
      JMenuItem right = new JCheckBoxMenuItem("TabbedPane.right");

      top.setName("top");
      left.setName("left");
      bottom.setName("bottom");
      right.setName("right");

      ButtonGroup group = new ButtonGroup();
      group.add(top);
      group.add(bottom);
      group.add(left);
      group.add(right);

      top.setSelected(true);

      top.addActionListener(this);
      bottom.addActionListener(this);
      left.addActionListener(this);
      right.addActionListener(this);

      tabbedPane.add(top);
      tabbedPane.add(left);
      tabbedPane.add(bottom);
      tabbedPane.add(right);

      colorChooser = new JColorChooser();

      menuBar.add(tabbedPane);

      JMenu dialog = new JMenu("OptionPane");

      JMenuItem color = new JMenuItem("ColorChooser...");
      JMenuItem file = new JMenuItem("FileChooser...");

      JMenuItem info = new JMenuItem("Information Dialog...");
      JMenuItem question = new JMenuItem("Question Dialog...");
      JMenuItem warning = new JMenuItem("Warning Dialog...");
      JMenuItem error = new JMenuItem("Error Dialog...");

      color.setName("color");
      file.setName("file");

      info.setName("info");
      question.setName("question");
      warning.setName("warning");
      error.setName("error");

      color.addActionListener(this);
      file.addActionListener(this);

      info.addActionListener(this);
      question.addActionListener(this);
      warning.addActionListener(this);
      error.addActionListener(this);

      dialog.add(color);
      dialog.add(file);
      dialog.addSeparator();
      dialog.add(info);
      dialog.add(question);
      dialog.add(warning);
      dialog.add(error);

      menuBar.add(dialog);

      setJMenuBar(menuBar);

      //--------

      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(demo, BorderLayout.CENTER);

      addWindowListener (new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          setVisible(false);
        }
      });

      JCheckBox enabledComponent = new JCheckBox("Disable components");
      enabledComponent.addActionListener(this);
      
      JButton close = new JButton("close");
      
      close.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          setVisible(false);
        }
      });

      JPanel p = new JPanel();
      p.setLayout(new FlowLayout(FlowLayout.RIGHT));
      p.add(enabledComponent);
      p.add(close);
      getContentPane().add(p, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e)
    {
      Object o = e.getSource();
      String name = ((Component)o).getName();

      if (o instanceof JCheckBox)
      {
        demo.setEnabled(!((JCheckBox)o).isSelected());
      }
      else
      if(o instanceof JCheckBoxMenuItem)
      {
        if(name.equalsIgnoreCase("top"))
        {
          tabbedPane.setTabPlacement(JTabbedPane.TOP);
        }
        else
        if(name.equalsIgnoreCase("left"))
        {
          tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        }
        else
        if(name.equalsIgnoreCase("bottom"))
        {
          tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        }
        else
        if(name.equalsIgnoreCase("right"))
        {
          tabbedPane.setTabPlacement(JTabbedPane.RIGHT);
        }

        pack();
      }
      else
      {
        if(name.equalsIgnoreCase("color"))
        {
          Color tmp = JColorChooser.showDialog(this, "Color chooser", Color.pink);

          if(tmp!=null)
          {
            demo.setColor(tmp);
          }

          forceRepaintAfterDialog();
        }
        else
        if(name.equalsIgnoreCase("file"))
        {
          JFileChooser fileChooser = new JFileChooser();
          int result = fileChooser.showOpenDialog(this);

          if(result==JFileChooser.APPROVE_OPTION)
          {
            File tmp = fileChooser.getSelectedFile();

            if(tmp==null || !tmp.exists() || tmp.isDirectory())
            {
              return;
            }

            String tmpname = tmp.getName().toLowerCase();

            if(tmpname.endsWith(".gif") || tmpname.endsWith(".jpeg") || tmpname.endsWith(".jpg"))
            {
              demo.openImage(tmp.getPath());
            }
          }

          forceRepaintAfterDialog();
        }
        else
        if(name.equalsIgnoreCase("question"))
        {
          JOptionPane.showMessageDialog(this, "This a question message", "question", JOptionPane.QUESTION_MESSAGE);
        }
        else
        if(name.equalsIgnoreCase("warning"))
        {
          JOptionPane.showMessageDialog(this, "This a warning message", "warning", JOptionPane.WARNING_MESSAGE);
        }
        else
        if(name.equalsIgnoreCase("error"))
        {
          JOptionPane.showMessageDialog(this, "This a error message", "error", JOptionPane.ERROR_MESSAGE);
        }
        else
        if(name.equalsIgnoreCase("info"))
        {
          JOptionPane.showMessageDialog(this, "This a information message", "information", JOptionPane.INFORMATION_MESSAGE);
        }
      }
    }

    public void setVisible(boolean _visible)
    {
      parent.setVisible(!_visible);
      
      if(_visible)
      {
        pack();
      }

      if (_visible)
      {
        toFront();
        demo.updateLnFInformation();
      }
      
      super.setVisible(_visible);
    }

    public void pack()
    {
      super.pack();

      Dimension dim = getToolkit().getScreenSize();
      Dimension d = getSize(); 
     
      if (d.width>dim.width)
      {
        d.width = dim.width;
      }
      
      if (d.height>dim.height)
      {
        d.height = dim.height;
      }
      
      setBounds((dim.width-d.width)/2, (dim.height-d.height)/2, d.width, d.height);
    }
  }

  class MetalThemeMenu extends JMenu implements ActionListener
  {
    protected Object[] themes;

    public MetalThemeMenu(String name, Vector themeArray)
    {
      super(name);

      themes = new Object[themeArray.size()];
      themeArray.copyInto(themes);
      ButtonGroup group = new ButtonGroup();

      for(int i = 0; i < themes.length; i++)
      {
          JRadioButtonMenuItem item = new JRadioButtonMenuItem(((MetalTheme)themes[i]).getName() );
          group.add(item);
          add( item );
          item.setActionCommand(i+"");
          item.addActionListener(this);

          if(i==0)
          {
            item.setSelected(true);
          }
      }
    }

    public void actionPerformed(ActionEvent e)
    {
      String numStr = e.getActionCommand();
      currentMetalTheme = (MetalTheme)themes[Integer.parseInt(numStr)];

      try
      {
        MetalLookAndFeel.setCurrentTheme(currentMetalTheme);
        OyoahaLookAndFeel look = new OyoahaLookAndFeel();

        if(currentTheme!=null)
        look.setOyoahaTheme(currentTheme);

        UIManager.setLookAndFeel(look);
        SwingUtilities.updateComponentTreeUI(preview);
        preview.pack();
        preview.setVisible(true);
      }
      catch(Exception ex)
      {

      }
    }
  }

  protected class ThemeFileFilter extends javax.swing.filechooser.FileFilter
  {
    public boolean accept(File _file)
    {
      if(_file.isDirectory())
      {
      	return true;
      }

      String name = _file.getName();

      if(name.endsWith(".zotm"))
      {
        return true;
      }

      return false;
    }

    public String getDescription()
    {
      return "OyoahaTheme";
    }
  }
}
