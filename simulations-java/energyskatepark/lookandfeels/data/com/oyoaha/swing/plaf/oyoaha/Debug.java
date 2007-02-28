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
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

public class Debug extends JFrame implements ActionListener
{
  private File openDirectory = new File(System.getProperty("user.dir"));

  private JFrame preview;
  private JFrame help;

  private File currentTheme;
  private MetalTheme currentMetalTheme;

  private boolean showHelp;
  private DebugOyoahaConsole debug;
  private JEditorPane text;

  public Debug()
  {
    debug = new DebugOyoahaConsole();

//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    try
    {
      OyoahaLookAndFeel look = new DebugOyoahaLookAndFeel(debug);
      UIManager.setLookAndFeel(look);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    JToolBar bar = new JToolBar();
    bar.setFloatable(false);

    JButton btest = new JButton("Test");
    JButton bopen = new JButton("Open a oyoaha theme");
    JButton bhelp = new JButton("Help");

    btest.setToolTipText("Test oyoaha lookandfeel");
    bopen.setToolTipText("Open a oyoaha theme");
    bhelp.setToolTipText("Show help");

    btest.setName("test");
    bopen.setName("open");
    bhelp.setName("help");

    btest.addActionListener(this);
    bopen.addActionListener(this);
    bhelp.addActionListener(this);

    btest.setVisible(false);
    bopen.setVisible(false);
    bhelp.setVisible(false);

    bar.add(btest);
    bar.add(bopen);
    bar.add(bhelp);

//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    getContentPane().setLayout(new BorderLayout());

    getContentPane().add(bar, BorderLayout.NORTH);
    getContentPane().add(new JLabel("(c) copyright 2000 oyoaha - http://www.oyoaha.com", SwingConstants.LEFT), BorderLayout.SOUTH);

    text = new JEditorPane("text/html", "<HTML><HEAD></HEAD><BODY></BODY></HTML>");
    text.setEditable(false);

    JScrollPane scroll = new JScrollPane(text);
    scroll.setPreferredSize(new Dimension(630,400));

    getContentPane().add(scroll, BorderLayout.CENTER);

    addWindowListener (new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });

    pack();
    Dimension d = getSize();
    Dimension dim = getToolkit().getScreenSize();
    setLocation((dim.width-d.width)/2, (dim.height-d.height)/2);

    super.setVisible(true);

//-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

    help = new HelpFrame();

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
      icon = new ImageIcon(getClass().getResource("rc/help.gif"));
      bhelp.setIcon(icon);
    }
    catch (Exception ex)
    {

    }

    btest.setVisible(true);
    bopen.setVisible(true);
    bhelp.setVisible(true);

    pack();
    d = getSize();
    setBounds((dim.width-d.width)/2, (dim.height-d.height)/2, d.width, d.height);
  }

  public void actionPerformed(ActionEvent e)
  {
    String name = ((Component)e.getSource()).getName();

    if(name.equals("test"))
    test();
    else
    if(name.equals("open"))
    open();
    else
    if(name.equals("help"))
    help.setVisible(true);
  }

  protected void open()
  {
    JFileChooser chooser = new JFileChooser();

    chooser.setFileFilter(new ThemeFileFilter());
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    chooser.setCurrentDirectory(openDirectory);

    int result = chooser.showOpenDialog(null);

    if(result==JFileChooser.APPROVE_OPTION)
    {
      openDirectory = chooser.getCurrentDirectory();
      File f = chooser.getSelectedFile();

      if(f!=null && f.exists())
      {
        currentTheme = f;

        if(f==null || !f.exists())
        return;

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
        OyoahaLookAndFeel look = new DebugOyoahaLookAndFeel(debug);
        UIManager.setLookAndFeel(look);
        SwingUtilities.updateComponentTreeUI(this);
        SwingUtilities.updateComponentTreeUI(help);
      }
      catch(Exception e)
      {

      }

      if(showHelp)
      {
        help.setVisible(true);
      }

      text.setText(debug.getText());
    }
    else
    {
      help.setVisible(false);
    }

    super.setVisible(_visible);
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

        OyoahaLookAndFeel look = new DebugOyoahaLookAndFeel(debug);
        debug.setText("");

        if(currentTheme!=null)
        look.setOyoahaTheme(currentTheme);
        
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
    }
    else
    {
      try
      {
        if(currentMetalTheme!=null)
        MetalLookAndFeel.setCurrentTheme(currentMetalTheme);

        OyoahaLookAndFeel look = new DebugOyoahaLookAndFeel(debug);
        debug.setText("");

        if(currentTheme!=null)
        look.setOyoahaTheme(currentTheme);

        UIManager.setLookAndFeel(look);
        SwingUtilities.updateComponentTreeUI(preview);

        if(currentTheme!=null)
        preview.setTitle(currentTheme.getName());
        else
        preview.setTitle("Use default theme");

        preview.setVisible(true);
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
    new Debug();
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
      setBounds((dim.width-440)/2, (dim.height-380)/2, 440, 380);
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

  class PropertiesMetalTheme extends DefaultMetalTheme
  {
    private String name = "Custom Theme";

    private ColorUIResource primary1;
    private ColorUIResource primary2;
    private ColorUIResource primary3;

    private ColorUIResource secondary1;
    private ColorUIResource secondary2;
    private ColorUIResource secondary3;

    private ColorUIResource black;
    private ColorUIResource white;

    protected FontUIResource controlFont;
    protected FontUIResource systemFont;
    protected FontUIResource userFont;
    protected FontUIResource smallFont;

    private File file;

    public PropertiesMetalTheme(File _file)
    {
      file = _file;

      initColors();

      try
      {
        loadProperties(new FileInputStream(file));
      }
      catch(Exception e)
      {

      }
    }

    public File getFile()
    {
      return file;
    }

    private void initColors()
    {
      primary1 = super.getPrimary1();
      primary2 = super.getPrimary2();
      primary3 = super.getPrimary3();

      secondary1 = super.getSecondary1();
      secondary2 = super.getSecondary2();
      secondary3 = super.getSecondary3();

      black = super.getBlack();
      white = super.getWhite();
    }

    private void loadProperties(InputStream stream)
    {
      Properties prop = new Properties();

      try
      {
        prop.load(stream);
      }
      catch(IOException e)
      {

      }

      Object tempName = prop.get("name");

      if (tempName != null)
      {
        name = tempName.toString();
      }

      Object colorString = null;

      colorString = prop.get("primary1");
      if (colorString != null)
      {
        primary1 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
      }

      colorString = prop.get("primary2");
      if (colorString != null)
      {
          primary2 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
      }

      colorString = prop.get("primary3");
      if (colorString != null)
      {
          primary3 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
      }

      colorString = prop.get("secondary1");
      if (colorString != null)
      {
          secondary1 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
      }

      colorString = prop.get("secondary2");
      if (colorString != null)
      {
          secondary2 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
      }

      colorString = prop.get("secondary3");
      if (colorString != null)
      {
          secondary3 = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
      }

      colorString = prop.get("black");
      if (colorString != null)
      {
          black = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
      }

      colorString = prop.get("white");
      if (colorString != null)
      {
        white = OyoahaThemeLoaderUtilities.readColor(colorString.toString());
      }

      colorString = prop.get("controlFont");
      if (colorString!=null)
      {
        controlFont = OyoahaThemeLoaderUtilities.readFont(colorString.toString());
      }

      colorString = prop.get("systemFont");
      if (colorString!=null)
      {
        systemFont = OyoahaThemeLoaderUtilities.readFont(colorString.toString());
      }

      colorString = prop.get("userFont");
      if (colorString!=null)
      {
        userFont = OyoahaThemeLoaderUtilities.readFont(colorString.toString());
      }

      colorString = prop.get("smallFont");
      if (colorString!=null)
      {
        smallFont = OyoahaThemeLoaderUtilities.readFont(colorString.toString());
      }
    }

    public String getName()
    {
      return name;
    }

    public FontUIResource getControlTextFont()
    {
      return (controlFont!=null) ? controlFont : super.getControlTextFont();
    }

    public FontUIResource getSystemTextFont()
    {
      return (systemFont!=null) ? systemFont : super.getSystemTextFont();
    }

    public FontUIResource getUserTextFont()
    {
      return (userFont!=null) ? userFont : super.getUserTextFont();
    }

    public FontUIResource getMenuTextFont()
    {
      return (controlFont!=null) ? controlFont : super.getControlTextFont();
    }

    public FontUIResource getWindowTitleFont()
    {
      return (controlFont!=null) ? controlFont : super.getControlTextFont();
    }

    public FontUIResource getSubTextFont()
    {
      return (smallFont!=null) ? smallFont : super.getSubTextFont();
    }

    protected ColorUIResource getPrimary1() { return primary1; }
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }

    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }

    protected ColorUIResource getBlack() { return black; }
    protected ColorUIResource getWhite() { return white; }
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

    PreviewFrame(JFrame _parent)
    {
      parent = _parent;

      /*JButton button = new JButton("GO!");
      Dimension d = new Dimension(2,2);
      JPanel north = new JPanel();
      JPanel south = new JPanel();
      JPanel west = new JPanel();
      JPanel east = new JPanel();
      north.setPreferredSize(d);
      south.setPreferredSize(d);
      west.setPreferredSize(d);
      east.setPreferredSize(d);
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(button, BorderLayout.CENTER);
      getContentPane().add(north, BorderLayout.NORTH);
      getContentPane().add(south, BorderLayout.SOUTH);
      getContentPane().add(east, BorderLayout.EAST);
      getContentPane().add(west, BorderLayout.WEST);

      Color pink = new Color(255, 204, 204);

      setBackground(pink);
      north.setBackground(pink);
      south.setBackground(pink);
      west.setBackground(pink);
      east.setBackground(pink);*/

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
            themes.addElement(new PropertiesMetalTheme(new File(file, list[i])));
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
      if(_visible)
      {
        pack();
        demo.updateLnFInformation();
      }

      parent.setVisible(!_visible);
      super.setVisible(_visible);
    }

    public void pack()
    {
      super.pack();

      Dimension dim = getToolkit().getScreenSize();
      /*Dimension d = new Dimension(160,80);
      setBounds((dim.width-d.width)/2, (dim.height-d.height)/2, d.width, d.height);*/
      Dimension d = getSize();
      setLocation((dim.width-d.width)/2, (dim.height-d.height)/2);
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
        OyoahaLookAndFeel look = new DebugOyoahaLookAndFeel(debug);
        debug.setText("");

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
      return "OyoahaTheme, OyoahaResource";
    }
  }
}
