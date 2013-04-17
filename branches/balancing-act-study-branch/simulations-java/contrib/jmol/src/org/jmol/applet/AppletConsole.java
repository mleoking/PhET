/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-09-18 04:32:23 -0700 (Sat, 18 Sep 2010) $
 * $Revision: 14331 $
 *
 * Copyright (C) 2004-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net, www.jmol.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.applet;

import org.jmol.api.JmolAppConsoleInterface;
import org.jmol.api.JmolViewer;
import org.jmol.console.JmolConsole;
import org.jmol.console.KeyJMenu;
import org.jmol.console.KeyJMenuItem;
import org.jmol.i18n.GT;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Keymap;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jmol.viewer.JmolConstants;

public class AppletConsole extends JmolConsole implements JmolAppConsoleInterface {

  /**
   * general entry point
   * 
   * @param viewer
   * @param externalContainer   a JFrame or JPanel or null
   * 
   */
  public AppletConsole(JmolViewer viewer, Container externalContainer) {
    this.viewer = viewer;
    Component display = viewer.getDisplay();
    this.viewerFrame = (display instanceof JFrame ? (JFrame) display : null);
    if (externalContainer == null) {
      JFrame jf = new JFrame();
      jf.setSize(600, 400);
      this.externalContainer = jf;
    } else {
      this.externalContainer = externalContainer;      
      viewer.setConsole(this);
    }
    addWindowListener();
    layoutWindow();
    output(defaultMessage);
  }

  public AppletConsole() {
    // required for Class.forName  
  }
  
  /**
   * don't delete! used by Viewer after it gets the class by name
   * 
   * @param viewer
   * @return          AppletConsole
   */
  public JmolAppConsoleInterface getAppConsole(JmolViewer viewer) {
    return new AppletConsole(viewer, null);
  }

  //public void finalize() {
  //  System.out.println("Console " + this + " finalize");
  //}

  protected final JTextArea input = new ControlEnterTextArea();
  private JButton clearOutButton, clearInButton, loadButton;
  private final JTextPane output = new JTextPane();
  private final Document outputDocument = output.getDocument();
  private final SimpleAttributeSet attributesCommand = new SimpleAttributeSet();

  
  @Override
  public void sendConsoleEcho(String strEcho) {
    if (strEcho == null) {
      // null here means new language
      updateLabels();
      output(null);
      strEcho = defaultMessage;
    }
    output(strEcho);
  }

  @Override
  public void sendConsoleMessage(String strInfo) {
    // null here indicates "clear console"
    if (strInfo != null && output.getText().startsWith(defaultMessage))
      output(null);
    output(strInfo);
  }

  public void zap() {
  }

  private void layoutWindow() {
    //Logger.debug("Console constructor");
    setLabels();
    setupInput();
    setupOutput();

    JScrollPane jscrollInput = new JScrollPane(input);
    jscrollInput.setMinimumSize(new Dimension(2, 100));

    JScrollPane jscrollOutput = new JScrollPane(output);
    jscrollOutput.setMinimumSize(new Dimension(2, 100));
    Container c = getPane();
    c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));

    //System.out.println("Console " + this + " set(2)");

    JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jscrollOutput,
        jscrollInput);
    jsp.setResizeWeight(.9);
    jsp.setDividerLocation(200);

    jsp.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.add(jsp);

    Container c2 = new Container();
    c2.setLayout(new BoxLayout(c2, BoxLayout.X_AXIS));
    c2.add(Box.createGlue());
    c2.add(editButton);
    c2.add(runButton);
    c2.add(loadButton);
    c2.add(clearInButton);
    c2.add(clearOutButton);
    c2.add(historyButton);
    c2.add(stateButton);
    c2.add(Box.createGlue());
    c.add(c2);
    label1.setAlignmentX(Component.CENTER_ALIGNMENT);
    c.add(label1);
    if (externalContainer instanceof JFrame)
      ((JFrame) externalContainer).setJMenuBar(createMenubar());

    //System.out.println("Console " + this + " set(3)");

  }

  private void setLabels() {
    boolean doTranslate = GT.getDoTranslate();
    GT.setDoTranslate(true);
    editButton = setButton("Editor");
    stateButton = setButton("State");
    runButton = setButton("Run");
    clearOutButton = setButton("Clear Output");
    clearInButton = setButton("Clear Input");
    historyButton = setButton("History");
    loadButton = setButton("Load");
    label1 = new JLabel(getLabel("label1"), SwingConstants.CENTER);
    defaultMessage = getLabel("default");
    setTitle();
    GT.setDoTranslate(doTranslate);
  }

  @Override
  protected void setupLabels() {
    labels.put("help", GT._("&Help"));
    labels.put("search", GT._("&Search..."));
    labels.put("commands", GT._("&Commands"));
    labels.put("functions", GT._("Math &Functions"));
    labels.put("parameters", GT._("Set &Parameters"));
    labels.put("more", GT._("&More"));
    labels.put("Editor", GT._("Editor"));
    labels.put("State", GT._("State"));
    labels.put("Run", GT._("Run"));
    labels.put("Clear Output", GT._("Clear Output"));
    labels.put("Clear Input", GT._("Clear Input"));
    labels.put("History", GT._("History"));
    labels.put("Load", GT._("Load"));
    labels.put("label1", GT
        ._("press CTRL-ENTER for new line or paste model data and press Load"));
    labels.put("default",
        GT._("Messages will appear here. Enter commands in the box below. Click the console Help menu item for on-line help, which will appear in a new browser window."));
  }

  protected JMenuBar createMenubar() {
    JMenuBar mb = new JMenuBar();
    //addNormalMenuBar(mb);
    mb.add(Box.createHorizontalGlue());
    addHelpMenuBar(mb);
    return mb;
  }
  
  protected void addHelpMenuBar(JMenuBar menuBar) {
    JMenu m0 = new KeyJMenu("help", getLabel("help"), menuMap);
    JMenuItem item = createMenuItem("search");
    item.addActionListener(this);
    item.setName("help ?search=?");
    m0.add(item);
    addHelpItems(m0, "commands", "command");
    addHelpItems(m0, "functions", "mathfunc");
    addHelpItems(m0, "parameters", "setparam");
    addHelpItems(m0, "more", "misc");
    menuBar.add(m0);
  }

  private void addHelpItems(JMenu m0, String key, String attr) {
    JMenu m = new KeyJMenu(key, getLabel(key), menuMap);
    String[] commands = (String[]) viewer.getProperty(null, "tokenList", attr);
    m0.add(m);
    JMenu m2 = null;
    String firstCommand = null;
    int n = 20;
    for (int i = 0; i < commands.length; i++) {
      String cmd = commands[i];
      if (!Character.isLetter(cmd.charAt(0)))
        continue;
      JMenuItem item = new JMenuItem(cmd);
      item.addActionListener(this);
      item.setName("help " + cmd);
      if (m2 == null) {
        m2 = new JMenu();
        firstCommand = cmd;
        m2.add(item);
        m2.setText(firstCommand);
        continue;
      }
      if ((i % n) + 1 == n) {
        m2.add(item);
        m2.setText(firstCommand + " - " + cmd);
        m.add(m2);
        m2 = null;
        continue;
      }
      m2.add(item);
      if (i + 1 == commands.length && m2 != null) {
        m2.setText(firstCommand + " - " + cmd);
        m.add(m2);
      }
    }
  }

  protected JMenuItem createMenuItem(String cmd) {
    return new KeyJMenuItem(cmd, getLabel(cmd), menuMap);
  }

  private void setupInput() {
    //System.out.println("AppletConsole.setupOutput " + input);
    input.setLineWrap(true);
    input.setWrapStyleWord(true);
    input.setDragEnabled(true);
    //input.setText("Input a command in the box below or select a menu item from above.");

    Keymap map = input.getKeymap();
    //    KeyStroke shiftCR = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
    //                                               InputEvent.SHIFT_MASK);
    KeyStroke shiftA = KeyStroke.getKeyStroke(KeyEvent.VK_A,
        InputEvent.SHIFT_MASK);
    map.removeKeyStrokeBinding(shiftA);
  }

  private void setupOutput() {
    //System.out.println("AppletConsole.setupOutput " + output);
    output.setEditable(false);
    output.setDragEnabled(true);
    //    output.setLineWrap(true);
    //    output.setWrapStyleWord(true);
    StyleConstants.setBold(attributesCommand, true);
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    input.requestFocus();
  }

  private void output(String message) {
    output(message, null);
  }

  private void output(String message, AttributeSet att) {
    //System.out.println("AppletConsole.output " + message + " " + att);
    if (message == null || message.length() == 0) {
      output.setText("");
      return;
    }
    if (message.charAt(message.length() - 1) != '\n')
      message += "\n";
    try {
      outputDocument.insertString(outputDocument.getLength(), message, att);
    } catch (BadLocationException ble) {
    }
    output.setCaretPosition(outputDocument.getLength());
  }

  @Override
  protected void clearContent(String text) {
    //System.out.println("AppletConsole.clearContent()");
    output.setText(text);
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    //System.out.println("AppletConsole.actionPerformed" +  source);
    if (source == clearInButton) {
      input.setText("");
      return;
    }
    if (source == clearOutButton) {
      output.setText("");
      return;
    }
    if (source == loadButton) {
      viewer.loadInline(input.getText(), false);
      return;
    }
    if (source instanceof JMenuItem) {
      execute(((JMenuItem) source).getName());
      return;
    }
    
    super.actionPerformed(e);
  }

  @Override
  protected void execute(String strCommand) {
    String cmd = (strCommand == null ? input.getText() : strCommand);
    if (strCommand == null)
      input.setText(null);
    String strErrorMessage = viewer.script(cmd + JmolConstants.SCRIPT_EDITOR_IGNORE);
    if (strErrorMessage != null && !strErrorMessage.equals("pending"))
      output(strErrorMessage);
    if (strCommand == null)
      input.requestFocus();
  }

  @Override
  protected String completeCommand(String thisCmd) {
    return super.completeCommand(thisCmd);
  }
  
  class ControlEnterTextArea extends JTextArea {
    @SuppressWarnings("deprecation")
    @Override
    public void processComponentKeyEvent(KeyEvent ke) {
      int kcode = ke.getKeyCode();
      switch (ke.getID()) {
      case KeyEvent.KEY_PRESSED:
        switch (kcode) {
        case KeyEvent.VK_TAB:
          ke.consume();
          if (input.getCaretPosition() == input.getText().length()) {
            String cmd = completeCommand(getText());
            if (cmd != null)
              setText(cmd.replace('\t',' '));
            nTab++;
            return;
          }
          break;
        case KeyEvent.VK_ESCAPE:
          ke.consume();
          setText("");
          break;
        }
        nTab = 0;
        if (kcode == KeyEvent.VK_ENTER && !ke.isControlDown()) {
          execute(null);
          return;
        }
        if (kcode == KeyEvent.VK_UP || kcode == KeyEvent.VK_DOWN) {
          recallCommand(kcode == KeyEvent.VK_UP);
          return;
        }
        break;
      case KeyEvent.KEY_RELEASED:
        if (kcode == KeyEvent.VK_ENTER && !ke.isControlDown())
          return;
        break;
      }
      if (kcode == KeyEvent.VK_ENTER)
        ke.setModifiers(0);
      super.processComponentKeyEvent(ke);
    }

    private void recallCommand(boolean up) {
      String cmd = viewer.getSetHistory(up ? -1 : 1);
      if (cmd == null)
        return;
      setText(cmd);
    }
  }
}
