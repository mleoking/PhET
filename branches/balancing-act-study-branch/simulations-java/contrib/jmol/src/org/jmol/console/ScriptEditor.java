/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2009-06-24 10:35:00 -0500 (Wed, 24 Jun 2009) $
 * $Revision: 11106 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
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
package org.jmol.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTextPane; //import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.undo.UndoManager;
import javax.swing.JScrollPane;

import org.jmol.api.JmolScriptEditorInterface;
import org.jmol.api.JmolViewer;
import org.jmol.i18n.GT;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;
import org.jmol.script.ScriptContext;

public final class ScriptEditor extends JDialog implements JmolScriptEditorInterface, ActionListener {

  protected EditorTextPane editor;
  private JButton openButton;
  private JButton closeButton;
  private JButton loadButton;
  private JButton topButton;
  private JButton checkButton;
  private JButton runButton;
  private JButton pauseButton;
  private JButton haltButton;
  private JButton clearButton;
  private JButton stateButton;
  private JButton consoleButton;
  
  protected JButton stepButton;
  protected JButton resumeButton;

  private JmolViewer viewer;

  /*
   * methods sendeditorEcho, sendeditorMessage(strStatus), notifyScriptStart(),
   * notifyScriptTermination() are public in case developers want to use
   * ScriptWindow separate from the Jmol application.
   */

  public ScriptEditor() { 
  }

  private JmolConsole jmolConsole;

  protected String title;
  protected String parsedData = "";
  protected ScriptContext parsedContext;
  
  protected SimpleAttributeSet attHighlight;
  protected SimpleAttributeSet attEcho;
  protected SimpleAttributeSet attError;

  ScriptEditor(JmolViewer viewer, JFrame frame, JmolConsole jmolConsole) {
    super(frame, null, false);
    // from appConsole only;
    setAttributes();
    setTitle(title = GT._("Jmol Script Editor"));
    this.viewer = viewer;
    this.jmolConsole = jmolConsole;
    layoutWindow(getContentPane());
    setSize(745, 400);
    if (frame != null)
      setLocationRelativeTo(frame);
  }

  private void setAttributes() {
    attHighlight = new SimpleAttributeSet();
    StyleConstants.setBackground(attHighlight, Color.LIGHT_GRAY);
    StyleConstants.setForeground(attHighlight, Color.blue);
    StyleConstants.setBold(attHighlight, true);

    attEcho = new SimpleAttributeSet();
    StyleConstants.setForeground(attEcho, Color.blue);
    StyleConstants.setBold(attEcho, true);

    attError = new SimpleAttributeSet();
    StyleConstants.setForeground(attError, Color.red);
    StyleConstants.setBold(attError, true);

  }
  
  private JPanel buttonPanel = new JPanel();

  void layoutWindow(Container container) {
    editor = new EditorTextPane();
    editor.setDragEnabled(true);
    JScrollPane editorPane = new JScrollPane(editor);

    consoleButton = setButton(GT._("Console"));
    if (!viewer.isApplet() || viewer.getBooleanProperty("_signedApplet"))
      openButton = setButton(GT._("Open"));
    loadButton = setButton(GT._("Script"));
    checkButton = setButton(GT._("Check"));
    topButton = setButton(TextFormat.split(GT._("Top[as in \"go to the top\" - (translators: remove this bracketed part]"), '[')[0]);
    stepButton = setButton(GT._("Step"));
    runButton = setButton(GT._("Run"));
    pauseButton = setButton(GT._("Pause"));
    pauseButton.setEnabled(true);
    resumeButton = setButton(GT._("Resume"));
    resumeButton.setEnabled(false);
    haltButton = setButton(GT._("Halt"));
    haltButton.setEnabled(false);
    clearButton = setButton(GT._("Clear"));
    closeButton = setButton(GT._("Close"));

    // container.setLayout(new BorderLayout());
    // container.add(editorPane, BorderLayout.CENTER);
    JPanel buttonPanelWrapper = new JPanel();
    buttonPanelWrapper.setLayout(new BorderLayout());
    buttonPanelWrapper.add(buttonPanel, BorderLayout.CENTER);

    JSplitPane spane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorPane,
        buttonPanelWrapper);
    editorPane.setMinimumSize(new Dimension(300, 300));
    editorPane.setPreferredSize(new Dimension(5000, 5000));
    buttonPanelWrapper.setMinimumSize(new Dimension(60, 60));
    buttonPanelWrapper.setMaximumSize(new Dimension(1000, 60));
    buttonPanelWrapper.setPreferredSize(new Dimension(60, 60));
    spane.setDividerSize(0);
    spane.setResizeWeight(0.95);
    container.add(spane);
    // container.setLayout(new BorderLayout());
    // container.add(editorPane,BorderLayout.CENTER);
    // container.add(buttonPanelWrapper,BorderLayout.SOUTH);

  }

  private JButton setButton(String s) {
    JButton b = new JButton(s);
    b.addActionListener(this);
    buttonPanel.add(b);
    return b;
  }
  
  public void notifyScriptStart() {
    runButton.setEnabled(false);
    resumeButton.setEnabled(false);
    haltButton.setEnabled(true);
    pauseButton.setEnabled(true);
  }

  public void notifyScriptTermination() {
    runButton.setEnabled(true);
    pauseButton.setEnabled(false);
    resumeButton.setEnabled(false);
    haltButton.setEnabled(false);
    editor.editorDoc.clearHighlight();
    editor.setCaretPosition(editor.editorDoc.getLength());
  }

  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);
    viewer.getProperty("DATA_API", "scriptEditorState", b ? Boolean.TRUE : Boolean.FALSE);
    if (b)
      editor.grabFocus();
  }
  
  public String getText() {
    return editor.getText();
  }

  
  public void output(String message) {
    editor.clearContent(message);
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  /*
   * (non-Javadoc)
   * @see org.jmol.api.JmolScriptEditorInterface#notifyContext(org.jmol.script.ScriptContext, java.lang.Object[])
   * 
   * from org.jmol.viewer.StatusManager:
   * 
      if (isScriptCompletion && viewer.getMessageStyleChime()
          && viewer.getDebugScript()) {
        jmolCallbackListener.notifyCallback(JmolConstants.CALLBACK_SCRIPT,
            new Object[] { null, "script <exiting>", statusMessage,
                new Integer(-1), strErrorMessageUntranslated });
        strStatus = "Jmol script completed.";
      }
      jmolCallbackListener.notifyCallback(JmolConstants.CALLBACK_SCRIPT,
          new Object[] { sJmol, strStatus, statusMessage,
              new Integer(isScriptCompletion ? -1 : msWalltime),
              strErrorMessageUntranslated });
   */
  
  public void notifyContext(ScriptContext context, Object[] data) {
    haltButton.setEnabled(context.errorMessage == null);
    pauseButton.setEnabled(context.errorMessage == null);
    resumeButton.setEnabled(false);
    if (context.errorMessage == null)
      setContext(context); 
  }

  protected String filename;
  
  private synchronized void setContext(ScriptContext context) {
    pauseButton.setEnabled(viewer.isScriptExecuting());
    if (context.script.indexOf(JmolConstants.SCRIPT_EDITOR_IGNORE) >= 0)
      return; 
    parsedContext = context;
    filename = context.filename;
    setTitle(title + parsedContext.contextPath);
    if (filename == null && context.functionName != null)
      filename = "function " + context.functionName; 
    //pcLast = context.pc;
    parsedData = editor.editorDoc.outputEcho(context.script);
    boolean isPaused = context.executionPaused || context.executionStepping;
    pauseButton.setEnabled(!isPaused && viewer.isScriptExecuting());
    resumeButton.setEnabled(isPaused);
    gotoCommand(context.pc, isPaused, attHighlight);
  }
  
  private void gotoCommand(int pt, boolean isPaused, SimpleAttributeSet attr) {    
    ScriptContext context = parsedContext;
    try {
      try {
        setVisible(true);
        int pt2;
        int pt1;
        if (pt < 0) {
          pt1 = 0;
          pt2 = editor.getDocument().getLength();
        } else if (context == null || context.aatoken == null) {
          pt1 = pt2 = 0;
        } else if (pt < context.aatoken.length) {
          pt1 = context.lineIndices[pt][0];
          pt2 = context.lineIndices[pt][1];
          //System.out.println ("cursor set to " + pt + " ispaused " + isPaused + " " + context.pc + " " + context.lineIndices.length);
        } else {
          pt1 = pt2 = editor.getDocument().getLength();
        }
        if (isPaused) {
          editor.setCaretPosition(pt1);
          editor.editorDoc.doHighlight(pt1, pt2, attr);
          //System.out.println("highlighting /" + editor.editorDoc.getText(pt1,pt2 - pt1) + "/");
        }
        //editor.grabFocus();
      } catch (Exception e) {
        editor.setCaretPosition(0);
        // do we care?
      }
    } catch (Error er) {
      // no. We don't.
    }    
  }

  public void actionPerformed(ActionEvent e) {
    checkAction(e);
  }
  
  private synchronized void checkAction(ActionEvent e) {
    Object source = e.getSource();
    if (source == consoleButton) {
      jmolConsole.setVisible(true);
      return;
    }
    if (source == openButton) {
      doOpen();
      return;
    }
    if (source == closeButton) {
      setVisible(false);
      return;
    }
    if (source == loadButton) {
      setContext((ScriptContext) viewer.getProperty("DATA_API",
          "scriptContext", null));
      return;
    }
    if (source == topButton) {
      gotoTop();
      return;
    }
    if (source == checkButton) {
      checkScript();
      return;
    }
    if (source == runButton) {
      notifyScriptStart();
      String s = editor.getText();
      jmolConsole.execute(s + "\1##");
      return;
    }
    if (source == pauseButton) {
      jmolConsole.execute("!pause\1##");
      return;
    }
    if (source == resumeButton) {
      doResume();
      return;
    }
    if (source == stepButton) {
      doStep();
      return;
    }
    if (source == clearButton) {
      editor.clearContent();
      return;
    }
    if (source == stateButton) {
      editor.clearContent(viewer.getStateInfo());
      return;
    }
    if (source == haltButton) {
      viewer.haltScriptExecution();
      return;
    }

  }
 
  private static String[] lastOpened = {"?.spt", null} ;
  private void doOpen() {
    viewer.getFileAsString(lastOpened, Integer.MAX_VALUE, false);
    editor.clearContent(lastOpened[1]);
    lastOpened[1] = null;
  }

  public void gotoTop() {
    editor.setCaretPosition(0);
    editor.grabFocus();
    gotoPosition(0, 0);
  }

  public void checkScript() {
    parsedContext = null;
    parseScript(editor.getText());
  }
  
  protected void parseScript(String text) {
    if (text == null || text.length() == 0) {
      parsedContext = null;
      parsedData = "";
      setTitle(title);
      return;
    }
    if (parsedContext == null || !text.equals(parsedData)) {
      parsedData = text;
      parsedContext = (ScriptContext) viewer.getProperty("DATA_API","scriptCheck", text);
    }
    gotoParsedLine();
  }

  private void gotoParsedLine() {
    setTitle(title + " " + parsedContext.contextPath 
        + " -- " + (parsedContext.aatoken == null ? "" : parsedContext.aatoken.length + " commands ") 
        + (parsedContext.iCommandError < 0 ? "" : " ERROR: " + parsedContext.errorType));
    boolean isError = (parsedContext.iCommandError >= 0);
    gotoCommand(isError ? parsedContext.iCommandError : 0, true, isError ? attError : attHighlight);
  }

  public void doStep() {
    boolean isPaused = viewer.getBooleanProperty("executionPaused");
    jmolConsole.execute(isPaused ? "!step\1##" 
        : editor.getText() + "\1##SCRIPT_STEP\n##SCRIPT_START=" +  editor.getCaretPosition());
  }

  protected void doResume() {
    editor.clearContent();
    jmolConsole.execute("!resume\1##");
  }
  private void gotoPosition(int i, int j) {
    editor.scrollRectToVisible(new Rectangle(i, j));
  }

  class EditorTextPane extends JTextPane {

    EditorDocument editorDoc;
    //JmolViewer viewer;

    boolean checking = false;

    EditorTextPane() {
      super(new EditorDocument());
      editorDoc = (EditorDocument) getDocument();
      editorDoc.setEditorTextPane(this);
      //this.viewer = scriptEditor.viewer;
    }

    public void clearContent() {
      filename = null;
      clearContent(null);
    }

    public synchronized void clearContent(String text) {
      editorDoc.outputEcho(text);
      parseScript(text);
    }
    
    @Override
    protected void processKeyEvent(KeyEvent ke) {
      int kcode = ke.getKeyCode();
      int kid = ke.getID();
      if (kid == KeyEvent.KEY_PRESSED) {
        switch (kcode) {
        case KeyEvent.VK_Z:
          if (ke.isControlDown()) {
            if (ke.isShiftDown())
              editor.editorDoc.redo();
            else
              editor.editorDoc.undo();
            return;
          }
          break;
        case KeyEvent.VK_Y:
          if (ke.isControlDown()) {
            editor.editorDoc.redo();
            return;
          }
          break;
        case KeyEvent.VK_F5:
          if (stepButton.isEnabled())
            doStep();
          return;
        case KeyEvent.VK_F8:
          if (resumeButton.isEnabled())
            doResume();
          return;
        }
      }
      super.processKeyEvent(ke);
    }
  }

  class EditorDocument extends DefaultStyledDocument {

    EditorTextPane EditorTextPane;

    EditorDocument() {
      super();
      putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
      addUndoableEditListener(new MyUndoableEditListener());
    }

    void setEditorTextPane(EditorTextPane EditorTextPane) {
      this.EditorTextPane = EditorTextPane;
    }

    
    
    void doHighlight(int from, int to, SimpleAttributeSet attr) {
      clearHighlight();
      if (from >= to)
        return;
      setCharacterAttributes(from, to - from, attr, true);
      editor.select(from, to);
      editor.setSelectedTextColor(attr == attError ? Color.RED : Color.black);

    }

    void clearHighlight() {
      setCharacterAttributes(0, editor.editorDoc.getLength(), attEcho, true);
    }

    protected UndoManager undo = new UndoManager();

    protected class MyUndoableEditListener implements UndoableEditListener {
      public void undoableEditHappened(UndoableEditEvent e) {
        // Remember the edit and update the menus
        undo.addEdit(e.getEdit());
        // undoAction.updateUndoState();
        // redoAction.updateRedoState();
      }
    }  

    protected void undo() {
      try {
        undo.undo();
      } catch (Exception e) {
        //
      }
    }
    
    protected void redo() {
      try {
        undo.redo();
      } catch (Exception e) {
        //
      }
    }
    
    /**
     * Removes all content of the script window, and add a new prompt.
     */
    void clearContent() {
      try {
        super.remove(0, getLength());
      } catch (Exception exception) {
        //Logger.error("Could not clear script window content", exception);
      }
    }

    String outputEcho(String text) {
      clearContent();
      if (text == null)
        return "";
      if (!text.endsWith("\n"))
        text += "\n";
      try {
        super.insertString(0, text, attEcho);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return text;
    }
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

}
