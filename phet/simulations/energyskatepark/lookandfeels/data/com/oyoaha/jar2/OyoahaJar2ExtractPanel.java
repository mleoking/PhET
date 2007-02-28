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

package com.oyoaha.jar2;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class OyoahaJar2ExtractPanel implements ActionListener
{
  protected JList list;
  protected JButton decode;

  protected File openDirectory = new File(System.getProperty("user.dir"));
  protected File directory;

  protected String header;
  protected String rootKey;

  protected boolean headerOption;

  protected boolean hideContentKey;
  protected URL help;
  protected Dimension dimension;
  protected boolean resizable;

  public JPanel getOyoahaJar2ExtractPanel()
  {
    JPanel panel = new JPanel();

    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(this);
    cancel.setName("cancel");

    decode = new JButton("Decode");
    decode.addActionListener(this);
    decode.setName("decode");

    JButton add = new JButton("Select a Encoded File");
    add.addActionListener(this);
    add.setName("add");

    JButton bhelp = null;

    if(help!=null)
    {
      bhelp = new JButton("Help");
      bhelp.addActionListener(this);
      bhelp.setName("help");
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    Icon icon = null;

    try
    {
      icon = new ImageIcon(getClass().getResource("rc/test.gif"));
      decode.setIcon(icon);
    }
    catch (Exception ex)
    {

    }

    try
    {
      icon = new ImageIcon(getClass().getResource("rc/cancel.gif"));
      cancel.setIcon(icon);
    }
    catch (Exception ex)
    {

    }

    try
    {
      icon = new ImageIcon(getClass().getResource("rc/open.gif"));
      add.setIcon(icon);
    }
    catch (Exception ex)
    {

    }

    if(bhelp!=null)
    {
      try
      {
        icon = new ImageIcon(getClass().getResource("rc/help.gif"));
        bhelp.setIcon(icon);
      }
      catch (Exception ex)
      {

      }
    }

    //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    JTextField header = null;

    if(hideContentKey)
    header = new JPasswordField();
    else
    header = new JTextField();

    header.setText(this.header);
    header.getDocument().addDocumentListener(new CheckStringValue(header));
    header.setName("header");

    JScrollPane jScrollPane1 = new JScrollPane();
    list = new JList(new DefaultListModel());

    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));

    if(bhelp!=null)
    {
      jPanel1.add(bhelp, null);
    }

    jPanel1.add(cancel, null);
    jPanel1.add(decode, null);

    JPanel jPanel5 = new JPanel();
    jPanel5.setLayout(new FlowLayout(FlowLayout.LEFT));
    jPanel5.add(add, null);

    JPanel jPanel3 = new JPanel();
    jPanel3.setLayout(new BorderLayout());
    jScrollPane1.getViewport().add(list, null);
    jPanel3.add(jScrollPane1, BorderLayout.CENTER);
    jPanel3.add(jPanel5, BorderLayout.SOUTH);

    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new GridBagLayout());
    jPanel2.add(new JLabel("Header"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel2.add(header, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
    //jPanel2.add(new JLabel(""), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    
    panel.setLayout(new BorderLayout());
    panel.add(jPanel1, BorderLayout.SOUTH);
    panel.add(jPanel2, BorderLayout.NORTH);
    panel.add(jPanel3, BorderLayout.CENTER);

    header.setEnabled(!headerOption);

    canEncode();

    return panel;
  }

  // - - - - - - - - - - SET DEFAULT VALUE - - -

  public void setDirectory(File directory)
  {
    this.directory = directory;
    canEncode();
  }

  public void setHeader(String header)
  {
    this.header = header;
    canEncode();
  }

  public void setRootKey(String rootKey)
  {
    this.rootKey = rootKey;
    canEncode();
  }

  // - - - - - - - - - - ENABLE DISABLE OPTION - - -


  public void disabledHeaderOption(String header)
  {
    this.header = header;
    this.headerOption = true;
  }

  public void hideContentKey()
  {
    this.hideContentKey = true;
  }

  public void setHelpButton(URL help)
  {
    this.help = help;
  }

  public void setPreferredSize(Dimension dimension)
  {
    this.dimension = dimension;
  }

  public Dimension getPreferredSize()
  {
    return dimension;
  }

  public void setResizable(boolean resizable)
  {
    this.resizable = resizable;
  }

  public boolean getResizable()
  {
    return resizable;
  }

  // - - - - - - - - - - ACTION - - -

  protected JFrame helpFrame;

  public void actionPerformed(ActionEvent e)
  {
    JComponent c = (JComponent)e.getSource();
    String name = c.getName();

    if(name==null)
    return;

    if(name.equalsIgnoreCase("cancel"))
    {
      System.exit(0);
    }
    else
    if(name.equalsIgnoreCase("help"))
    {
      if(helpFrame==null)
      {
        helpFrame = new HelpFrame(help);
      }

      helpFrame.setVisible(true);
    }
    else
    if(name.equalsIgnoreCase("add"))
    {
      //open
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      chooser.setCurrentDirectory(openDirectory);
      chooser.setApproveButtonText("Select");

      int result = chooser.showOpenDialog(null);

      if(result==JFileChooser.APPROVE_OPTION)
      {
        openDirectory = chooser.getCurrentDirectory();
        File f = chooser.getSelectedFile();

        if(f!=null && f.exists())
        {
          directory = f;
          canEncode();
        }
      }
    }
    else
    if(name.equalsIgnoreCase("decode"))
    {
      //encode
      JFileChooser chooser = new JFileChooser();
      chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      chooser.setCurrentDirectory(openDirectory);

      int result = chooser.showSaveDialog(null);

      if(result==JFileChooser.APPROVE_OPTION)
      {
        openDirectory = chooser.getCurrentDirectory();
        File f = chooser.getSelectedFile();

        if(f!=null)
        {
          try
          {
            if(f.exists() && !f.isDirectory())
            {
              f = new File(f.getParent());
            }

            byte[] rootKeyByte = rootKey.getBytes("UTF-8");
            OyoahaJar2Reader oyoahaJarReader = new OyoahaJar2Reader(new OyoahaJar2FileStreamWarper(directory, false), header);
            oyoahaJarReader.enabledCache(false);

            if(!f.exists())
            {
              f.mkdirs();
            }

            String[] s = oyoahaJarReader.getOyoahaJarEntryName();

            for(int i=0;i<s.length;i++)
            {
              InputStream in = oyoahaJarReader.getInputStream(s[i]);
              s[i].replace('/', File.separatorChar);

              File outFile = new File(f, s[i]);
              mkdirs(new File(outFile.getParent()));

              OutputStream out = new FileOutputStream(outFile);

              byte[] bytes = new byte[1024];

              while(true)
              {
                int uu = in.read(bytes, 0, bytes.length);

                if (uu>0)
                {
                  out.write(bytes, 0, uu);
                  out.flush();
                }
                else
                {
                  break;
                }
              }

              out.flush();
              out.close();
              in.close();
            }
          }
          catch(Exception ex)
          {

          }

          System.exit(0);
        }
      }
    }
  }

  // - - - - - - - - - - PROTECTED - - -

  private void mkdirs(File file)
  {
    String p = file.getParent();

    if(p!=null)
    {
      File parent = new File(p);

      if(parent.exists())
      {
        file.mkdirs();
        return;
      }
      else
      {
        mkdirs(parent);
      }
    }

    file.mkdirs();
  }

  protected void canEncode()
  {
    if(decode==null)
    return;

    decode.setEnabled(header!=null & directory!=null);

    DefaultListModel model = (DefaultListModel)list.getModel();
    model.clear();

    if(decode.isEnabled())
    {
      try
      {
        byte[] rootKeyByte = rootKey.getBytes("UTF-8");
        OyoahaJar2Reader oyoahaJarReader = new OyoahaJar2Reader(new OyoahaJar2FileStreamWarper(directory, false), header);

        String[] s = oyoahaJarReader.getOyoahaJarEntryName();

        for(int i=0;i<s.length;i++)
        {
          model.addElement(s[i]);
        }
      }
      catch(Exception ex)
      {

      }
    }
  }

  protected class CheckStringValue implements DocumentListener
  {
    protected JTextField field;

    public CheckStringValue(JTextField field)
    {
      this.field = field;
    }

    public void insertUpdate(DocumentEvent e)
    {
      update();
    }

    public void removeUpdate(DocumentEvent e)
    {
      update();
    }

    public void changedUpdate(DocumentEvent e)
    {
      update();
    }

    protected void update()
    {
      String name = field.getName();

      if(name==null)
      return;

      String t = field.getText();

      if(t.equals(""))
      t = null;

      if(name.equalsIgnoreCase("key"))
      {
        setRootKey(t);
      }
      else
      if(name.equalsIgnoreCase("header"))
      {
        setHeader(t);
      }
    }
  }

  protected class HelpFrame extends JFrame
  {
    public HelpFrame(URL url)
    {
      super("help");
      getContentPane().setLayout(new BorderLayout());

      try
      {

        if(url != null)
        {
          JEditorPane html = new JEditorPane(url);
          html.setEditable(false);

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
        }
      });

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
      p.add(close);
      getContentPane().add(p, BorderLayout.SOUTH);

      pack();

      Dimension dim = getToolkit().getScreenSize();
      setBounds((dim.width-440)/2, (dim.height-380)/2, 440, 380);
    }
  }

  // - - - - - - - - - - MAIN - - -

  public final static void showOyoahaJar2Panel(OyoahaJar2ExtractPanel main)
  {
    showOyoahaJar2Panel(main, null, null);
  }

  public final static void showOyoahaJar2Panel(OyoahaJar2ExtractPanel main, URL screen, URL help)
  {
    JWindow window = null;

    if(screen!=null)
    {
      window = new JWindow();
      window.getContentPane().setLayout(new BorderLayout());
      window.getContentPane().add(new JLabel(new ImageIcon(screen)), BorderLayout.CENTER);

      window.pack();
      Dimension d = window.getSize();
      Dimension dim = window.getToolkit().getScreenSize();
      window.setLocation((dim.width-d.width)/2, (dim.height-d.height)/2);
      window.setVisible(true);
    }

    if(help!=null)
    {
      main.setHelpButton(help);
    }

    JFrame frame = new JFrame();

    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(main.getOyoahaJar2ExtractPanel(), BorderLayout.CENTER);

    frame.addWindowListener (new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        System.exit(0);
      }
    });

    frame.pack();
    Dimension dim = frame.getToolkit().getScreenSize();
    Dimension d = main.getPreferredSize();

    if(d==null)
    {
      d = frame.getSize();
      frame.setLocation((dim.width-d.width)/2, (dim.height-d.height)/2);
    }
    else
    {
      frame.setBounds((dim.width-d.width)/2, (dim.height-d.height)/2, d.width, d.height);
    }

    frame.setResizable(main.getResizable());

    if(window!=null)
    {
      window.dispose();
    }

    frame.setVisible(true);
  }

  public final static void main(String[] arg)
  {
    OyoahaJar2ExtractPanel main = new OyoahaJar2ExtractPanel();
    showOyoahaJar2Panel(main);
  }
}