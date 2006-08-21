/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;

/**
 * This provides a GUI for building LaunchNode xml files.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class LaunchBuilder extends Launcher {
  // static constants
  static final Color RED = new Color(255, 102, 102);

  // static fields
  static JFileChooser fileChooser;
  static javax.swing.filechooser.FileFilter jarFileFilter;
  static javax.swing.filechooser.FileFilter htmlFileFilter;
  static javax.swing.filechooser.FileFilter allFileFilter;
  static int maxArgs = 4; // max number of main method args

  // instance fields
  Action changeAction;    // loads input data into selected node
  Action newTabAction;    // creates a new root node and tab
  Action addAction;       // adds new node to selected node
  Action cutAction;       // cuts selected node to clipboard
  Action copyAction;      // copies selected node to clipboard
  Action pasteAction;     // adds clipboard node to selected node
  Action importAction;    // adds xml file node to selected node
  Action saveAsAction;    // saves selected node to a new file
  Action saveAction;      // saves selected node
  Action saveAllAction;   // saves tabset and all nodes
  Action saveSetAsAction; // saves tabset and all nodes to new files
  Action moveUpAction;    // moves selected node up
  Action moveDownAction;  // moves selected node down
  Action openJarAction;   // opens a jar file and sets node path
  Action searchJarAction; // searches the current jars for a launchable class
  Action openArgAction;   // opens an xml file and sets the current argument
  Action openURLAction;   // opens an html file and sets url
  Action openTabAction;   // opens a node in a new tab
  FocusListener focusListener;
  KeyListener keyListener;
  JTabbedPane editorTabs;
  JTextField titleField;
  JTextField nameField;
  JTextField classField;
  JTextField argField;
  JSpinner argSpinner;
  JTextField jarField;
  JTextField urlField;
  JPanel urlPanel;
  JTextPane descriptionPane;
  JScrollPane descriptionScroller;
  JSplitPane displaySplitPane;
  JTextField authorField;
  JTextField codeAuthorField;
  JTextPane commentPane;
  JScrollPane commentScroller;
  JCheckBox editorEnabledCheckBox;
  JCheckBox hideRootCheckBox;
  JCheckBox hiddenCheckBox;
  JCheckBox singleVMCheckBox;
  JCheckBox showLogCheckBox;
  JCheckBox clearLogCheckBox;
  JCheckBox singletonCheckBox;
  JCheckBox singleAppCheckBox;
  JComboBox levelDropDown;
  // Level[] allLevels;

  String previousClassPath;
  JMenuItem newItem;
  JMenuItem previewItem;
  JMenuItem saveNodeItem;
  JMenuItem saveNodeAsItem;
  JMenuItem saveSetAsItem;
  JMenuItem saveAllItem;
  JMenuItem importItem;
  JToolBar toolbar;
  LaunchSaver saver = new LaunchSaver(this);

  /**
   * No-arg constructor.
   */
  public LaunchBuilder() {}

  /**
   * Constructs a builder and loads the specified file.
   *
   * @param fileName the file name
   */
  public LaunchBuilder(String fileName) {
    super(fileName);
  }

  /**
   * Main entry point when used as application.
   *
   * @param args args[0] may be an xml file name
   */
  public static void main(String[] args) {
    // java.util.Locale.setDefault(new java.util.Locale("es"));
    // OSPLog.setLevel(ConsoleLevel.ALL);
    String fileName = null;
    if(args!=null&&args.length!=0) {
      fileName = args[0];
    }
    LaunchBuilder builder = new LaunchBuilder(fileName);
    builder.frame.setVisible(true);
  }

  /**
   * Saves a node to the specified file.
   *
   * @param node the node
   * @param fileName the desired name of the file
   * @return the name of the saved file, or null if not saved
   */
  public String save(LaunchNode node, String fileName) {
    if(node==null) {
      return null;
    }
    if(fileName==null||fileName.trim().equals("")) {
      return saveAs(node);
    }
    // add .xml extension if none but don't require it
    if(XML.getExtension(fileName)==null) {
      while(fileName.endsWith(".")) {
        fileName = fileName.substring(0, fileName.length()-1);
      }
      fileName += ".xml";
    }
    if(!saveOwnedNodes(node)) {
      return null;
    }
    OSPLog.fine(fileName);
    File file = new File(XML.getResolvedPath(fileName, tabSetBasePath));
    String fullName = XML.forwardSlash(file.getAbsolutePath());
    String path = XML.getDirectoryPath(fullName);
    XML.createFolders(path);
    XMLControlElement control = new XMLControlElement(node);
    control.write(fullName);
    if(!control.canWrite) {
      OSPLog.info(LaunchRes.getString("Dialog.SaveFailed.Message")+" "+fullName);
      JOptionPane.showMessageDialog(null, LaunchRes.getString("Dialog.SaveFailed.Message")+" "+fileName, LaunchRes.getString("Dialog.SaveFailed.Title"), JOptionPane.WARNING_MESSAGE);
      return null;
    }
    node.setFileName(fileName);
    changedFiles.remove(node.getFileName());
    return fileName;
  }

  /**
   * Saves a node to an xml file selected with a chooser.
   *
   * @param node the node
   * @return the name of the file
   */
  public String saveAs(LaunchNode node) {
    getXMLChooser().setFileFilter(xmlFileFilter);
    if(node.getFileName()!=null) {
      String name = XML.getResolvedPath(node.getFileName(), tabSetBasePath);
      getXMLChooser().setSelectedFile(new File(name));
    } else {
      String name = node.name;
      if(name.equals(LaunchRes.getString("NewNode.Name"))||name.equals(LaunchRes.getString("NewTab.Name"))) {
        name = LaunchRes.getString("NewFile.Name");
      }
      String path = XML.getResolvedPath(name+".xml", tabSetBasePath);
      getXMLChooser().setSelectedFile(new File(path));
    }
    int result = getXMLChooser().showDialog(null, LaunchRes.getString("FileChooser.SaveAs.Title"));
    if(result==JFileChooser.APPROVE_OPTION) {
      File file = getXMLChooser().getSelectedFile();
      // create folder structure
      String path = XML.forwardSlash(file.getParent());
      XML.createFolders(path);
      // check to see if file already exists
      if(file.exists()) {
        // prevent duplicate file names
        String name = XML.forwardSlash(file.getAbsolutePath());
        name = XML.getPathRelativeTo(name, tabSetBasePath);
        if(getOpenPaths().contains(name)) {
          JOptionPane.showMessageDialog(frame, LaunchRes.getString("Dialog.DuplicateFileName.Message")+" \""+name+"\"", LaunchRes.getString("Dialog.DuplicateFileName.Title"), JOptionPane.WARNING_MESSAGE);
          return null;
        }
        int selected = JOptionPane.showConfirmDialog(frame, LaunchRes.getString("Dialog.ReplaceFile.Message")+" "+file.getName()+XML.NEW_LINE+LaunchRes.getString("Dialog.ReplaceFile.Question"), LaunchRes.getString("Dialog.ReplaceFile.Title"), JOptionPane.YES_NO_OPTION);
        if(selected!=JOptionPane.YES_OPTION) {
          return null;
        }
      }
      path = XML.forwardSlash(file.getAbsolutePath());
      String fileName = XML.getPathRelativeTo(path, tabSetBasePath);
      OSPFrame.chooserDir = XML.getDirectoryPath(path);
      // get clones before saving
      Map clones = getClones(node);
      path = save(node, fileName);
      if(path!=null) {
        if(node.isRoot()) {
          // refresh title of root tab
          for(int i = 0;i<tabbedPane.getTabCount();i++) {
            LaunchPanel tab = (LaunchPanel) tabbedPane.getComponentAt(i);
            if(tab.getRootNode()==node) {
              tabbedPane.setTitleAt(i, node.toString());
              break;
            }
          }
        }
        // refresh clones
        for(Iterator it = clones.keySet().iterator();it.hasNext();) {
          LaunchPanel cloneTab = (LaunchPanel) it.next();
          LaunchNode clone = (LaunchNode) clones.get(cloneTab);
          clone.setFileName(node.getFileName());
          // refresh title of clone tab
          if(clone==cloneTab.getRootNode()) {
            int n = tabbedPane.indexOfComponent(cloneTab);
            tabbedPane.setTitleAt(n, node.toString());
          }
        }
        if(tabSetName!=null) {
          changedFiles.add(tabSetName);
        }
      }
      return path;
    }
    return null;
  }

  // ______________________________ protected methods _____________________________

  /**
   * Saves the owned nodes of the specified node.
   *
   * @param node the node
   * @return true unless cancelled by user
   */
  public boolean saveOwnedNodes(LaunchNode node) {
    if(node==null) {
      return false;
    }
    if(node.isSelfContained()) {
      return true; // owned nodes saved within node
    }
    LaunchNode[] nodes = node.getChildOwnedNodes();
    for(int i = 0;i<nodes.length;i++) {
      // save owned nodes of owned node, if any
      if(nodes[i].getChildOwnedNodes().length>1) {
        if(!saveOwnedNodes(nodes[i])) {
          return false;
        }
      }
      if(save(nodes[i], nodes[i].getFileName())==null) {
        return false;
      }
    }
    return true;
  }

  /**
   * Saves a tabset to a file selected with a chooser.
   *
   * @return the absolute path of the tabset file
   */
  protected String saveTabSetAs() {
    saver.setBuilder(this);
    saver.setVisible(true);
    if(!saver.isApproved()) {
      return null;
    }
    return saveTabSet();
  }

  /**
   * Saves the current tabset.
   *
   * @return the full path to the saved file
   */
  protected String saveTabSet() {
    if(tabSetName.trim().equals("")) {
      return saveTabSetAs();
    }
    // check for read-only files
    if(!isTabSetWritable()) {
      return saveTabSetAs();
    }
    if(!selfContained&&!saveTabs()) {
      return null;
    }
    // save tab set
    String fileName = XML.getResolvedPath(tabSetName, tabSetBasePath);
    OSPLog.fine(fileName);
    File file = new File(fileName);
    fileName = XML.forwardSlash(file.getAbsolutePath());
    String path = XML.getDirectoryPath(fileName);
    XML.createFolders(path);
    LaunchSet tabset = new LaunchSet(this, tabSetName);
    XMLControlElement control = new XMLControlElement(tabset);
    control.write(fileName);
    changedFiles.clear();
    jarBasePath = null; // signals tabset is now a file
    if(spawner!=null) {
      spawner.open(fileName);
      spawner.refreshGUI();
    }
    return fileName;
  }

  /**
   * Saves tabs.
   * @return true unless cancelled by user
   */
  protected boolean saveTabs() {
    Component[] tabs = tabbedPane.getComponents();
    for(int i = 0;i<tabs.length;i++) {
      LaunchPanel tab = (LaunchPanel) tabs[i];
      LaunchNode root = tab.getRootNode();
      if(root.getFileName()==null||root.getFileName().equals("")) {
        continue;
      }
      save(root, XML.getResolvedPath(root.getFileName(), tabSetBasePath));
    }
    return true;
  }

  /**
   * Refreshes the selected node.
   */
  protected void refreshSelectedNode() {
    refreshNode(getSelectedNode());
  }

  /**
   * Refreshes the specified node with data from the input fields.
   *
   * @param node the node to refresh
   */
  protected void refreshNode(LaunchNode node) {
    boolean changed = false;
    if(node!=null) {
      if(node.isSingleVM()!=singleVMCheckBox.isSelected()) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeVM"));
        node.singleVM = singleVMCheckBox.isSelected();
        if(node.isSingleVM()) {
          showLogCheckBox.setSelected(node.showLog);
          clearLogCheckBox.setSelected(node.clearLog);
          singleAppCheckBox.setSelected(node.singleApp);
        } else {
          singletonCheckBox.setSelected(node.singleton);
        }
        changed = true;
      }
      if(node.isSingleVM()&&node.showLog!=showLogCheckBox.isSelected()) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeShowLog"));
        node.showLog = showLogCheckBox.isSelected();
        changed = true;
      }
      if(node.isSingleVM()&&node.isShowLog()&&node.clearLog!=clearLogCheckBox.isSelected()) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeClearLog"));
        node.clearLog = clearLogCheckBox.isSelected();
        changed = true;
      }
      if(node.isSingleVM()&&node.singleApp!=singleAppCheckBox.isSelected()) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeSingleApp"));
        node.singleApp = singleAppCheckBox.isSelected();
        changed = true;
      }
      if(node.singleton!=singletonCheckBox.isSelected()) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeSingleton"));
        node.singleton = singletonCheckBox.isSelected();
        changed = true;
      }
      if(node.hiddenInLauncher!=hiddenCheckBox.isSelected()) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeHidden"));
        node.hiddenInLauncher = hiddenCheckBox.isSelected();
        changed = true;
      }
      if(!node.name.equals(nameField.getText())) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeName"));
        node.name = nameField.getText();
        changed = true;
      }
      if(!node.description.equals(descriptionPane.getText())) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeDesc"));
        node.description = descriptionPane.getText();
        changed = true;
      }
      int n = ((Integer) argSpinner.getValue()).intValue();
      String arg = argField.getText();
      if(!arg.equals("")) {
        node.setMinimumArgLength(n+1);
      }
      if(node.args.length>n&&!arg.equals(node.args[n])) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeArgs")+" "+n);
        node.args[n] = arg;
        if(arg.equals("")) {
          node.setMinimumArgLength(1);
        }
        changed = true;
      }
      String jarPath = jarField.getText();
      if((jarPath.equals("")&&node.classPath!=null)||(!jarPath.equals("")&&!jarPath.equals(node.classPath))) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodePath"));
        node.setClassPath(jarPath.equals("") ? null : jarPath);
        changed = true;
      }
      String input = urlField.getText();
      if(input.equals("")) {
        input = null;
      }
      if((node.urlName!=null&&!node.urlName.equals(input))||(input!=null&&!input.equals(node.urlName))) {
        node.setURL(input);
        changed = true;
      }
      String className = classField.getText();
      if(className.equals("")) {
        if(node.launchClassName!=null) {
          node.launchClassName = null;
          node.launchClass = null;
          OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeLaunchClass"));
          changed = true;
        }
      } else if(!className.equals(node.launchClassName)||!className.equals("")&&node.getLaunchClass()==null) {
        boolean change = node.setLaunchClass(className);
        if(change) {
          OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeLaunchClass"));
          changed = true;
        }
      }
      if(!node.author.equals(authorField.getText())) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeAuthor"));
        node.author = authorField.getText();
        changed = true;
      }
      if(!node.codeAuthor.equals(codeAuthorField.getText())) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeCodeAuthor"));
        node.codeAuthor = codeAuthorField.getText();
        changed = true;
      }
      if(!node.comment.equals(commentPane.getText())) {
        OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeComment"));
        node.comment = commentPane.getText();
        changed = true;
      }
      LaunchNode root = (LaunchNode) node.getRoot();
      if(root!=null) {
        boolean hide = hideRootCheckBox.isSelected();
        if(hide!=root.hiddenWhenRoot) {
          root.hiddenWhenRoot = hide;
          OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeRootHidden"));
          changed = true;
        }
        boolean edit = editorEnabledCheckBox.isSelected();
        if(edit!=editorEnabled) {
          editorEnabled = edit;
          OSPLog.finest(LaunchRes.getString("Log.Message.ChangeNodeEditorEnabled"));
          if(tabSetName!=null) {
            changedFiles.add(tabSetName);
          }
          refreshGUI();
        }
      }
      if(changed) {
        OSPLog.fine(LaunchRes.getString("Log.Message.ChangeNode")+" \""+node.toString()+"\"");
        LaunchPanel tab = getSelectedTab();
        if(tab!=null) {
          tab.treeModel.nodeChanged(node);
        }
        if(node.getOwner()!=null) {
          changedFiles.add(node.getOwner().getFileName());
        } else {
          changedFiles.add(tabSetName);
        }
        refreshClones(node);
        refreshGUI();
      }
    }
  }

  /**
   * Creates a LaunchPanel with the specified root and adds it to a new tab.
   *
   * @param root the root node
   * @return true if tab successfully added
   */
  protected boolean addTab(LaunchNode root) {
    OSPLog.finest(root.toString());
    boolean added = super.addTab(root);
    if(added) {
      final LaunchPanel tab = getSelectedTab();
      tab.showText = false;
      tab.textPane.setText(null);
      if(tabSetName==null) {
        tabSetName = LaunchRes.getString("Tabset.Name.New");
      }
      changedFiles.add(tabSetName);
      refreshGUI();
    }
    return added;
  }

  /**
   * Removes the selected tab. Overrides Launcher method.
   *
   * @return true if the tab was removed
   */
  protected boolean removeSelectedTab() {
    // close the tabset if only one tab is open
    if(tabbedPane.getTabCount()==1) {
      return removeAllTabs();
    }
    // check for unsaved changes in the selected tab
    LaunchPanel tab = (LaunchPanel) tabbedPane.getSelectedComponent();
    if(tab!=null) {
      if(!saveChanges(tab)) {
        return false;
      }
    }
    boolean removed = super.removeSelectedTab();
    if(tabSetName!=null) {
      changedFiles.add(tabSetName);
      refreshGUI();
    }
    return removed;
  }

  /**
   * Offers to save changes, if any, to the specified tab.
   *
   * @param tab the tab
   * @return true unless cancelled by user
   */
  protected boolean saveChanges(LaunchPanel tab) {
    LaunchNode root = tab.getRootNode();
    int n = tabbedPane.indexOfComponent(tab);
    String name = n>-1 ? tabbedPane.getTitleAt(n) : getDisplayName(root.getFileName());
    boolean changed = changedFiles.contains(root.getFileName());
    LaunchNode[] nodes = root.getAllOwnedNodes();
    for(int i = 0;i<nodes.length;i++) {
      changed = changed||changedFiles.contains(nodes[i].getFileName());
    }
    if(changed) {
      int selected = JOptionPane.showConfirmDialog(frame, LaunchRes.getString("Dialog.SaveChanges.Tab.Message")+" \""+name+"\""+XML.NEW_LINE+LaunchRes.getString("Dialog.SaveChanges.Question"), LaunchRes.getString("Dialog.SaveChanges.Title"), JOptionPane.YES_NO_CANCEL_OPTION);
      if(selected==JOptionPane.CANCEL_OPTION) {
        return false;
      }
      if(selected==JOptionPane.YES_OPTION) {
        // save root and all owned nodes
        save(root, root.getFileName());
      }
    }
    return true;
  }

  /**
   * Removes all tabs and closes the tabset.
   *
   * @return true if all tabs were removed
   */
  protected boolean removeAllTabs() {
    if(!saveAllChanges()) {
      return false;
    }
    return super.removeAllTabs();
  }

  /**
   * Offers to save all changes, if any.
   *
   * @return true unless cancelled by user
   */
  protected boolean saveAllChanges() {
    // save changes to tab set
    if(!changedFiles.isEmpty()&&tabbedPane.getTabCount()>0) {
      String message = LaunchRes.getString("Dialog.SaveChanges.Tabset.Message");
      int selected = JOptionPane.showConfirmDialog(frame, message+XML.NEW_LINE+LaunchRes.getString("Dialog.SaveChanges.Question"), LaunchRes.getString("Dialog.SaveChanges.Title"), JOptionPane.YES_NO_CANCEL_OPTION);
      if(selected==JOptionPane.CANCEL_OPTION) {
        return false;
      }
      if(selected==JOptionPane.YES_OPTION) {
        if(tabSetName.equals(LaunchRes.getString("Tabset.Name.New"))||!saveAllItem.isEnabled()) {
          saveTabSetAs();
        } else {
          saveTabSet();
        }
      }
    }
    return true;
  }

  /**
   * Refreshes the GUI.
   */
  protected void refreshGUI() {
    if(previousNode!=null) { // new tab has been selected
      LaunchNode prev = previousNode;
      previousNode = null;
      refreshNode(prev);
    }
    if(newNodeSelected) {
      argSpinner.setValue(new Integer(0));
    }
    titleField.setText(title);
    titleField.setBackground(Color.white);
    super.refreshGUI();
    // refresh frame title
    String theTitle = frame.getTitle();
    if(this.title!=null) {
      if(!changedFiles.isEmpty()) {
        theTitle += " ["+tabSetName+"*]";
      } else {
        theTitle += " ["+tabSetName+"]";
      }
    } else if(!changedFiles.isEmpty()) {
      theTitle += "*";
    }
    frame.setTitle(theTitle);
    final LaunchNode node = getSelectedNode();
    if(node!=null) {
      // refresh tab titles
      for(int i = 0;i<tabbedPane.getTabCount();i++) {
        LaunchNode root = getTab(i).getRootNode();
        tabbedPane.setTitleAt(i, root.toString());
      }
      // refresh display tab
      hiddenCheckBox.setSelected(node.isHiddenInLauncher());
      boolean parentHidden = node.getParent()!=null&&((LaunchNode) node.getParent()).isHiddenInLauncher();
      hiddenCheckBox.setEnabled(!parentHidden);
      nameField.setText(node.toString());
      nameField.setBackground(Color.white);
      descriptionPane.setText(node.description);
      descriptionPane.setBackground(Color.white);
      urlField.setText(node.urlName);
      urlField.setBackground(node.url==null&&node.urlName!=null ? RED : Color.white);
      JEditorPane html = getSelectedTab().textPane;
      if(node.url!=null) {
        // set page
        try {
          if(node.url.getContent()!=null) {
            html.setPage(node.url);
          }
        } catch(Exception ex) {
          html.setText(null);
        }
      } else {
        getSelectedTab().textPane.setContentType(LaunchPanel.defaultType);
        getSelectedTab().textPane.setText(null);
      }
      // refresh launch tab
      // check the path and update the class chooser
      String path = node.getClassPath(); // in node-to-root order
      if(!path.equals(previousClassPath)) {
        boolean success = getClassChooser().setPath(path);
        searchJarAction.setEnabled(success);
      }
      // store path for later comparison
      previousClassPath = node.getClassPath();
      jarField.setText(node.classPath);
      jarField.setBackground(node.classPath!=null&&!getClassChooser().isLoaded(node.classPath) ? RED : Color.white);
      classField.setText(node.launchClassName);
      classField.setBackground(node.getLaunchClass()==null&&node.launchClassName!=null ? RED : Color.white);
      int n = ((Integer) argSpinner.getValue()).intValue();
      if(node.args.length>n) {
        argField.setText(node.args[n]);
      } else {
        argField.setText("");
      }
      argField.setBackground(Color.white);
      // set enabled and selected states of checkboxes
      LaunchNode parent = (LaunchNode) node.getParent(); // may be null
      singletonCheckBox.setEnabled(parent==null||!parent.isSingleton());
      singletonCheckBox.setSelected(node.isSingleton());
      singleVMCheckBox.setEnabled(parent==null||!parent.isSingleVM());
      singleVMCheckBox.setSelected(node.isSingleVM());
      if(node.isSingleVM()) {
        showLogCheckBox.setEnabled(parent==null||!parent.isShowLog());
        showLogCheckBox.setSelected(node.isShowLog());
        clearLogCheckBox.setEnabled(parent==null||!parent.isClearLog());
        clearLogCheckBox.setSelected(node.isClearLog());
        singleAppCheckBox.setEnabled(parent==null||!parent.isSingleApp());
        singleAppCheckBox.setSelected(node.isSingleApp());
      } else {
        showLogCheckBox.setEnabled(false);
        showLogCheckBox.setSelected(false);
        clearLogCheckBox.setEnabled(false);
        clearLogCheckBox.setSelected(false);
        singleAppCheckBox.setEnabled(false);
        singleAppCheckBox.setSelected(false);
      }
      levelDropDown.setVisible(node.isShowLog());
      clearLogCheckBox.setVisible(node.isShowLog());
      // refresh the level dropdown if visible
      if(levelDropDown.isVisible()) {
        boolean useAll = (parent==null||!parent.isShowLog());
        // disable during refresh to prevent triggering events
        levelDropDown.setEnabled(false);
        levelDropDown.removeAllItems();
        for(int i = 0;i<OSPLog.levels.length;i++) {
          if(useAll||OSPLog.levels[i].intValue()<=parent.getLogLevel().intValue()) {
            levelDropDown.addItem(OSPLog.levels[i]);
          }
        }
        levelDropDown.setSelectedItem(node.getLogLevel());
        levelDropDown.setEnabled(true);
      }
      // refresh metadata tab
      authorField.setText(node.author);
      authorField.setBackground(Color.white);
      codeAuthorField.setText(node.codeAuthor);
      codeAuthorField.setBackground(Color.white);
      commentPane.setText(node.comment);
      commentPane.setBackground(Color.white);
    }
    // rebuild file menu
    fileMenu.removeAll();
    fileMenu.add(newItem);
    fileMenu.addSeparator();
    fileMenu.add(openItem);
    LaunchPanel tab = getSelectedTab();
    boolean isZipped = jarBasePath!=null&&!jarBasePath.equals("");
    saveAllItem.setEnabled(!isZipped&&isTabSetWritable());
    if(tab!=null) {
      fileMenu.add(importItem);
      fileMenu.addSeparator();
      fileMenu.add(closeTabItem);
      fileMenu.add(closeAllItem);
      fileMenu.addSeparator();
      fileMenu.add(previewItem);
      fileMenu.addSeparator();
      fileMenu.add(saveAllItem);
      fileMenu.add(saveSetAsItem);
      // set tab properties
      frame.getContentPane().add(toolbar, BorderLayout.NORTH);
      tab.dataPanel.add(editorTabs, BorderLayout.CENTER);
      tab.textScroller.setBorder(BorderFactory.createTitledBorder(LaunchRes.getString("Label.HTML")));
      displaySplitPane.setTopComponent(tab.textScroller);
      displaySplitPane.setDividerLocation(0.7);
      // hidden root
      if(getRootNode().getChildCount()==0) {
        getRootNode().hiddenWhenRoot = false;
        hideRootCheckBox.setEnabled(false);
      } else {
        hideRootCheckBox.setEnabled(true);
      }
      boolean rootVisible = !getRootNode().hiddenWhenRoot;
      hideRootCheckBox.setSelected(!rootVisible);
      tab.tree.setRootVisible(rootVisible);
      if(getSelectedNode()==null&&!rootVisible) {
        tab.setSelectedNode((LaunchNode) getRootNode().getChildAt(0));
      }
      // editor enabled
      editorEnabledCheckBox.setSelected(editorEnabled);
    } else { // no tab
      frame.getContentPane().remove(toolbar);
    }
    if(exitItem!=null){
       fileMenu.add(exitItem);
       fileMenu.addSeparator();
    }
    // update tabbed pane tool tips and icons
    for(int k = 0;k<tabbedPane.getTabCount();k++) {
      LaunchNode root = getTab(k).getRootNode();
      if(root.getFileName()!=null) {
        tabbedPane.setIconAt(k, getFileIcon(root));
        tabbedPane.setToolTipTextAt(k, LaunchRes.getString("ToolTip.FileName")+" \""+root.getFileName()+"\"");
      } else {
        tabbedPane.setIconAt(k, null);
        tabbedPane.setToolTipTextAt(k, null);
      }
    }
  }

  /**
   * Creates the GUI.
   *
   * @param splash true to show the splash screen
   */
  protected void createGUI(boolean splash) {
    wInit = 600;
    hInit = 540;
    ArrayList labels = new ArrayList();
    super.createGUI(splash);
    // add window listener to refresh GUI
    tabbedPane.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        refreshGUI();
      }
    });
    // create additional file and folder icons
    String imageFile = "/org/opensourcephysics/resources/tools/images/whitefile.gif";
    whiteFileIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/ghostfile.gif";
    ghostFileIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/redfile.gif";
    redFileIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/yellowfile.gif";
    yellowFileIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/whitefolder.gif";
    whiteFolderIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/redfolder.gif";
    redFolderIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/greenfolder.gif";
    greenFolderIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/yellowfolder.gif";
    yellowFolderIcon = ResourceLoader.getIcon(imageFile);
    // create actions
    createActions();
    // create fields
    titleField = new JTextField();
    titleField.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ENTER) {
          String text = titleField.getText();
          if(text.equals("")) {
            text = null;
          }
          if(text!=title) {
            changedFiles.add(tabSetName);
          }
          title = text;
          refreshGUI();
        } else {
          titleField.setBackground(Color.yellow);
        }
      }
    });
    titleField.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        String text = titleField.getText();
        if(text.equals("")) {
          text = null;
        }
        if(text!=title) {
          changedFiles.add(tabSetName);
        }
        title = text;
        refreshGUI();
      }
    });
    nameField = new JTextField();
    nameField.addKeyListener(keyListener);
    nameField.addFocusListener(focusListener);
    classField = new JTextField();
    classField.addKeyListener(keyListener);
    classField.addFocusListener(focusListener);
    argField = new JTextField();
    argField.addKeyListener(keyListener);
    argField.addFocusListener(focusListener);
    jarField = new JTextField();
    jarField.addKeyListener(keyListener);
    jarField.addFocusListener(focusListener);
    urlField = new JTextField();
    urlField.addKeyListener(keyListener);
    urlField.addFocusListener(focusListener);
    codeAuthorField = new JTextField();
    codeAuthorField.addKeyListener(keyListener);
    codeAuthorField.addFocusListener(focusListener);
    authorField = new JTextField();
    authorField.addKeyListener(keyListener);
    authorField.addFocusListener(focusListener);
    commentPane = new JTextPane();
    commentPane.addKeyListener(keyListener);
    commentPane.addFocusListener(focusListener);
    commentScroller = new JScrollPane(commentPane);
    commentScroller.setBorder(BorderFactory.createTitledBorder(LaunchRes.getString("Label.Comments")));
    descriptionPane = new JTextPane();
    descriptionPane.addKeyListener(keyListener);
    descriptionPane.addFocusListener(focusListener);
    descriptionScroller = new JScrollPane(descriptionPane);
    descriptionScroller.setBorder(BorderFactory.createTitledBorder(LaunchRes.getString("Label.Description")));
    displaySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    displaySplitPane.setEnabled(false);
    displaySplitPane.setBottomComponent(descriptionScroller);
    hiddenCheckBox = new JCheckBox(LaunchRes.getString("Checkbox.Hidden"));
    hiddenCheckBox.addActionListener(changeAction);
    hiddenCheckBox.setContentAreaFilled(false);
    hiddenCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    // assemble display panel
    JPanel displayPanel = new JPanel(new BorderLayout());
    JToolBar nameBar = new JToolBar();
    nameBar.setFloatable(false);
    JLabel label = new JLabel(LaunchRes.getString("Label.Name"));
    labels.add(label);
    nameBar.add(label);
    nameBar.add(nameField);
    nameBar.add(hiddenCheckBox);
    displayPanel.add(nameBar, BorderLayout.NORTH);
    urlPanel = new JPanel(new BorderLayout());
    displayPanel.add(urlPanel, BorderLayout.CENTER);
    JToolBar urlBar = new JToolBar();
    urlBar.setFloatable(false);
    label = new JLabel(LaunchRes.getString("Label.URL"));
    labels.add(label);
    urlBar.add(label);
    urlBar.add(urlField);
    urlBar.add(openURLAction);
    urlPanel.add(urlBar, BorderLayout.NORTH);
    urlPanel.add(displaySplitPane, BorderLayout.CENTER);
    // determine label size
    FontRenderContext frc = new FontRenderContext(null, // no AffineTransform
      false,                                            // no antialiasing
      false);                                           // no fractional metrics
    Font font = label.getFont();
    // determine display panel label size
    int w = 0;
    for(Iterator it = labels.iterator();it.hasNext();) {
      JLabel next = (JLabel) it.next();
      Rectangle2D rect = font.getStringBounds(next.getText()+" ", frc);
      w = Math.max(w, (int) rect.getWidth()+1);
    }
    Dimension labelSize = new Dimension(w, 20);
    // set label properties
    for(Iterator it = labels.iterator();it.hasNext();) {
      JLabel next = (JLabel) it.next();
      next.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
      next.setPreferredSize(labelSize);
      next.setHorizontalAlignment(SwingConstants.TRAILING);
    }
    labels.clear();
    // assemble launch panel
    JPanel launchPanel = new JPanel(new BorderLayout());
    JToolBar jarBar = new JToolBar();
    jarBar.setFloatable(false);
    label = new JLabel(LaunchRes.getString("Label.Jar"));
    labels.add(label);
    jarBar.add(label);
    jarBar.add(jarField);
    jarBar.add(openJarAction);
    launchPanel.add(jarBar, BorderLayout.NORTH);
    JPanel classPanel = new JPanel(new BorderLayout());
    launchPanel.add(classPanel, BorderLayout.CENTER);
    JToolBar classBar = new JToolBar();
    classBar.setFloatable(false);
    label = new JLabel(LaunchRes.getString("Label.Class"));
    labels.add(label);
    classBar.add(label);
    classBar.add(classField);
    classBar.add(searchJarAction);
    classPanel.add(classBar, BorderLayout.NORTH);
    JPanel argPanel = new JPanel(new BorderLayout());
    classPanel.add(argPanel, BorderLayout.CENTER);
    JToolBar argBar = new JToolBar();
    argBar.setFloatable(false);
    label = new JLabel(LaunchRes.getString("Label.Args"));
    labels.add(label);
    argBar.add(label);
    SpinnerModel model = new SpinnerNumberModel(0, 0, maxArgs-1, 1);
    argSpinner = new JSpinner(model);
    JSpinner.NumberEditor editor = new JSpinner.NumberEditor(argSpinner);
    argSpinner.setEditor(editor);
    argSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        if(newNodeSelected) {
          return;
        }
        if(argField.getBackground()==Color.yellow) {
          refreshSelectedNode();
        } else {
          refreshGUI();
        }
      }
    });
    argBar.add(argSpinner);
    argBar.add(argField);
    argBar.add(openArgAction);
    argPanel.add(argBar, BorderLayout.NORTH);
    JPanel optionsPanel = new JPanel(new BorderLayout());
    argPanel.add(optionsPanel, BorderLayout.CENTER);
    JToolBar optionsBar = new JToolBar();
    optionsBar.setFloatable(false);
    // determine launch panel label size
    w = 0;
    for(Iterator it = labels.iterator();it.hasNext();) {
      JLabel next = (JLabel) it.next();
      Rectangle2D rect = font.getStringBounds(next.getText()+" ", frc);
      w = Math.max(w, (int) rect.getWidth()+1);
    }
    labelSize = new Dimension(w, 20);
    // set label properties
    for(Iterator it = labels.iterator();it.hasNext();) {
      JLabel next = (JLabel) it.next();
      next.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
      next.setPreferredSize(labelSize);
      next.setHorizontalAlignment(SwingConstants.TRAILING);
    }
    labels.clear();
    singleVMCheckBox = new JCheckBox(LaunchRes.getString("Checkbox.SingleVM"));
    singleVMCheckBox.addActionListener(changeAction);
    singleVMCheckBox.setContentAreaFilled(false);
    singleVMCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    showLogCheckBox = new JCheckBox(LaunchRes.getString("Checkbox.ShowLog"));
    showLogCheckBox.addActionListener(changeAction);
    showLogCheckBox.setContentAreaFilled(false);
    showLogCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    clearLogCheckBox = new JCheckBox(LaunchRes.getString("Checkbox.ClearLog"));
    clearLogCheckBox.addActionListener(changeAction);
    clearLogCheckBox.setContentAreaFilled(false);
    clearLogCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    singletonCheckBox = new JCheckBox(LaunchRes.getString("Checkbox.Singleton"));
    singletonCheckBox.addActionListener(changeAction);
    singletonCheckBox.setContentAreaFilled(false);
    singletonCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    singleAppCheckBox = new JCheckBox(LaunchRes.getString("Checkbox.SingleApp"));
    singleAppCheckBox.addActionListener(changeAction);
    singleAppCheckBox.setContentAreaFilled(false);
    singleAppCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    levelDropDown = new JComboBox(OSPLog.levels);
    levelDropDown.setMaximumSize(levelDropDown.getMinimumSize());
    levelDropDown.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(levelDropDown.isEnabled()) {
          LaunchNode node = getSelectedNode();
          if(node!=null) {
            node.setLogLevel((Level) levelDropDown.getSelectedItem());
          }
        }
      }
    });
    Box checkBoxPanel = Box.createVerticalBox();
    JToolBar bar = new JToolBar();
    bar.setFloatable(false);
    bar.setAlignmentX(Component.LEFT_ALIGNMENT);
    bar.add(singletonCheckBox);
    bar.add(Box.createHorizontalGlue());
    checkBoxPanel.add(bar);
    bar = new JToolBar();
    bar.setFloatable(false);
    bar.setAlignmentX(Component.LEFT_ALIGNMENT);
    bar.add(singleVMCheckBox);
    bar.add(Box.createHorizontalGlue());
    checkBoxPanel.add(bar);
    bar = new JToolBar();
    bar.setFloatable(false);
    bar.setAlignmentX(Component.LEFT_ALIGNMENT);
    bar.add(singleAppCheckBox);
    bar.add(Box.createHorizontalGlue());
    checkBoxPanel.add(bar);
    bar = new JToolBar();
    bar.setFloatable(false);
    bar.add(showLogCheckBox);
    bar.add(Box.createHorizontalStrut(4));
    bar.add(levelDropDown);
    bar.add(Box.createHorizontalStrut(4));
    bar.add(clearLogCheckBox);
    bar.add(Box.createHorizontalGlue());
    bar.setAlignmentX(Component.LEFT_ALIGNMENT);
    checkBoxPanel.add(bar);
    Border titled = BorderFactory.createTitledBorder(LaunchRes.getString("Label.Options"));
    Border recess = BorderFactory.createLoweredBevelBorder();
    optionsBar.setBorder(BorderFactory.createCompoundBorder(recess, titled));
    optionsBar.add(checkBoxPanel);
    optionsPanel.add(optionsBar, BorderLayout.NORTH);
    // create metadata panel
    JPanel metadataPanel = new JPanel(new BorderLayout());
    JToolBar authorBar = new JToolBar();
    authorBar.setFloatable(false);
    label = new JLabel(LaunchRes.getString("Label.Author"));
    labels.add(label);
    authorBar.add(label);
    authorBar.add(authorField);
    metadataPanel.add(authorBar, BorderLayout.NORTH);
    JPanel codePanel = new JPanel(new BorderLayout());
    metadataPanel.add(codePanel, BorderLayout.CENTER);
    JToolBar codeBar = new JToolBar();
    codeBar.setFloatable(false);
    label = new JLabel(LaunchRes.getString("Label.CodeAuthor"));
    labels.add(label);
    codeBar.add(label);
    codeBar.add(codeAuthorField);
    codePanel.add(codeBar, BorderLayout.NORTH);
    codePanel.add(commentScroller, BorderLayout.CENTER);
    // determine metadata panel label size
    w = 0;
    for(Iterator it = labels.iterator();it.hasNext();) {
      JLabel next = (JLabel) it.next();
      Rectangle2D rect = font.getStringBounds(next.getText()+" ", frc);
      w = Math.max(w, (int) rect.getWidth()+1);
    }
    labelSize = new Dimension(w, 20);
    // set label properties
    for(Iterator it = labels.iterator();it.hasNext();) {
      JLabel next = (JLabel) it.next();
      next.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
      next.setPreferredSize(labelSize);
      next.setHorizontalAlignment(SwingConstants.TRAILING);
    }
    // create server panel
    //JPanel serverPanel = new JPanel(new BorderLayout());
    // create editor tabbed pane
    editorTabs = new JTabbedPane(JTabbedPane.TOP);
    editorTabs.addTab(LaunchRes.getString("Tab.Display"), displayPanel);
    editorTabs.addTab(LaunchRes.getString("Tab.Launch"), launchPanel);
    editorTabs.addTab(LaunchRes.getString("Tab.Metadata"), metadataPanel);
    // editorTabs.addTab(LaunchRes.getString("Tab.Server"), serverPanel);
    // create toolbar
    toolbar = new JToolBar();
    toolbar.setFloatable(false);
    toolbar.setRollover(true);
    toolbar.setBorder(BorderFactory.createLineBorder(Color.gray));
    frame.getContentPane().add(toolbar, BorderLayout.NORTH);
    JButton button = new JButton(newTabAction);
    toolbar.add(button);
    button = new JButton(addAction);
    toolbar.add(button);
    button = new JButton(cutAction);
    toolbar.add(button);
    button = new JButton(copyAction);
    toolbar.add(button);
    button = new JButton(pasteAction);
    toolbar.add(button);
    button = new JButton(moveUpAction);
    toolbar.add(button);
    button = new JButton(moveDownAction);
    toolbar.add(button);
    hideRootCheckBox = new JCheckBox(LaunchRes.getString("Checkbox.HideRoot"));
    hideRootCheckBox.addActionListener(changeAction);
    hideRootCheckBox.setContentAreaFilled(false);
    toolbar.add(hideRootCheckBox);
    editorEnabledCheckBox = new JCheckBox(LaunchRes.getString("Checkbox.EditorEnabled"));
    editorEnabledCheckBox.addActionListener(changeAction);
    editorEnabledCheckBox.setContentAreaFilled(false);
    toolbar.add(editorEnabledCheckBox);
    label = new JLabel(LaunchRes.getString("Label.Title"));
    label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 2));
    toolbar.add(label);
    toolbar.add(titleField);
    // create menu items
    int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    // create new item
    newItem = new JMenuItem(newTabAction);
    newItem.setAccelerator(KeyStroke.getKeyStroke('N', mask));
    // create preview item
    previewItem = new JMenuItem(LaunchRes.getString("Menu.File.Preview"));
    previewItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String base = tabSetBasePath;
        previewing = true;
        LaunchSet set = new LaunchSet(LaunchBuilder.this, tabSetName);
        XMLControl control = new XMLControlElement(set);
        control.setValue("filename", tabSetName);
        Launcher launcher = new Launcher(control.toXML());
        launcher.frame.setVisible(true);
        // set close operation to dispose to distinguish from default of hide
        launcher.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tabSetBasePath = base;
        previewing = false;
        launcher.previewing = true;
      }
    });
    // create other menu items
    importItem = new JMenuItem(importAction);
    saveNodeItem = new JMenuItem(saveAction);
    saveNodeAsItem = new JMenuItem(saveAsAction);
    saveAllItem = new JMenuItem(saveAllAction);
    saveAllItem.setAccelerator(KeyStroke.getKeyStroke('S', mask));
    saveSetAsItem = new JMenuItem(saveSetAsAction);
    // replace tab listener
    tabbedPane.removeMouseListener(tabListener);
    tabListener = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if(contentPane.getTopLevelAncestor()!=frame) {
          return;
        }
        if(e.isPopupTrigger()||e.getButton()==MouseEvent.BUTTON3||(e.isControlDown()&&System.getProperty("os.name", "").indexOf("Mac")>-1)) {
          // make popup and add items
          JPopupMenu popup = new JPopupMenu();
          JMenuItem item = new JMenuItem(LaunchRes.getString("MenuItem.Close"));
          item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              removeSelectedTab();
            }
          });
          popup.add(item);
          popup.addSeparator();
          item = new JMenuItem(LaunchRes.getString("Menu.File.SaveAs"));
          item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              LaunchNode node = getSelectedTab().getRootNode();
              if(saveAs(node)!=null) {
                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), node.toString());
              }
              refreshGUI();
            }
          });
          popup.add(item);
          final int i = tabbedPane.getSelectedIndex();
          if(i>0||i<tabbedPane.getTabCount()-1) {
            popup.addSeparator();
          }
          if(i<tabbedPane.getTabCount()-1) {
            item = new JMenuItem(LaunchRes.getString("Popup.MenuItem.MoveUp"));
            item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                LaunchPanel tab = getSelectedTab();
                LaunchNode root = tab.getRootNode();
                LaunchBuilder.super.removeSelectedTab();
                tabbedPane.insertTab(getDisplayName(root.getFileName()), null, tab, null, i+1);
                tabbedPane.setSelectedComponent(tab);
              }
            });
            popup.add(item);
          }
          if(i>0) {
            item = new JMenuItem(LaunchRes.getString("Popup.MenuItem.MoveDown"));
            item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                LaunchPanel tab = getSelectedTab();
                LaunchNode root = tab.getRootNode();
                LaunchBuilder.super.removeSelectedTab();
                tabbedPane.insertTab(getDisplayName(root.getFileName()), null, tab, null, i-1);
                tabbedPane.setSelectedComponent(tab);
              }
            });
            popup.add(item);
          }
          popup.show(tabbedPane, e.getX(), e.getY()+8);
        }
      }
    };
    tabbedPane.addMouseListener(tabListener);
    frame.pack();
  }

  /**
   * Creates the actions.
   */
  protected void createActions() {
    String imageFile = "/org/opensourcephysics/resources/tools/images/open.gif";
    Icon openIcon = new ImageIcon(Launcher.class.getResource(imageFile));
    openJarAction = new AbstractAction(null, openIcon) {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = getJARChooser();
        int result = chooser.showOpenDialog(null);
        if(result==JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          jarField.setText(XML.getRelativePath(file.getPath()));
          OSPFrame.chooserDir = XML.getDirectoryPath(file.getPath());
          searchJarAction.setEnabled(true);
          refreshSelectedNode();
        }
      }
    };
    searchJarAction = new AbstractAction(null, openIcon) {
      public void actionPerformed(ActionEvent e) {
        LaunchNode node = getSelectedNode();
        if(node!=null&&getClassChooser().chooseClassFor(node)) {
          if(node.getOwner()!=null) {
            changedFiles.add(node.getOwner().getFileName());
          } else {
            changedFiles.add(tabSetName);
          }
          refreshClones(node);
          refreshGUI();
        }
      }
    };
    searchJarAction.setEnabled(false);
    openArgAction = new AbstractAction(null, openIcon) {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = getFileChooser();
        int result = chooser.showOpenDialog(null);
        if(result==JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          argField.setText(XML.getRelativePath(file.getPath()));
          OSPFrame.chooserDir = XML.getDirectoryPath(file.getPath());
          refreshSelectedNode();
        }
      }
    };
    openURLAction = new AbstractAction(null, openIcon) {
      public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = getHTMLChooser();
        int result = chooser.showOpenDialog(null);
        if(result==JFileChooser.APPROVE_OPTION) {
          File file = chooser.getSelectedFile();
          urlField.setText(XML.getRelativePath(file.getPath()));
          OSPFrame.chooserDir = XML.getDirectoryPath(file.getPath());
          refreshSelectedNode();
        }
      }
    };
    openTabAction = new AbstractAction(LaunchRes.getString("Action.OpenTab")) {
      public void actionPerformed(ActionEvent e) {
        LaunchNode node = getSelectedNode();
        String tabName = node.toString();
        for(int i = 0;i<tabbedPane.getComponentCount();i++) {
          if(tabbedPane.getTitleAt(i).equals(tabName)) {
            tabbedPane.setSelectedIndex(i);
            return;
          }
        }
        XMLControl control = new XMLControlElement(node);
        XMLControl cloneControl = new XMLControlElement(control);
        LaunchNode clone = (LaunchNode) cloneControl.loadObject(null);
        clone.setFileName(node.getFileName());
        addTab(clone);
        refreshGUI();
      }
    };
    changeAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        refreshSelectedNode();
      }
    };
    addAction = new AbstractAction(LaunchRes.getString("Action.Add")) {
      public void actionPerformed(ActionEvent e) {
        LaunchNode newNode = new LaunchNode(LaunchRes.getString("NewNode.Name"));
        addChildToSelectedNode(newNode);
      }
    };
    newTabAction = new AbstractAction(LaunchRes.getString("Action.New")) {
      public void actionPerformed(ActionEvent e) {
        LaunchNode root = new LaunchNode(LaunchRes.getString("NewTab.Name"));
        addTab(root);
      }
    };
    cutAction = new AbstractAction(LaunchRes.getString("Action.Cut")) {
      public void actionPerformed(ActionEvent e) {
        copyAction.actionPerformed(null);
        removeSelectedNode();
      }
    };
    copyAction = new AbstractAction(LaunchRes.getString("Action.Copy")) {
      public void actionPerformed(ActionEvent e) {
        LaunchNode node = getSelectedNode();
        if(node!=null) {
          XMLControl control = new XMLControlElement(node);
          control.setValue("filename", node.getFileName());
          StringSelection data = new StringSelection(control.toXML());
          Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          clipboard.setContents(data, data);
        }
      }
    };
    pasteAction = new AbstractAction(LaunchRes.getString("Action.Paste")) {
      public void actionPerformed(ActionEvent e) {
        try {
          Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          Transferable data = clipboard.getContents(null);
          String dataString = (String) data.getTransferData(DataFlavor.stringFlavor);
          if(dataString!=null) {
            XMLControlElement control = new XMLControlElement();
            control.readXML(dataString);
            if(control.getObjectClass()==LaunchNode.class) {
              String fileName = control.getString("filename");
              LaunchNode newNode = (LaunchNode) control.loadObject(null);
              newNode.setFileName(fileName);
              addChildToSelectedNode(newNode);
            }
          }
        } catch(UnsupportedFlavorException ex) {}
        catch(IOException ex) {}
        catch(HeadlessException ex) {}
      }
    };
    importAction = new AbstractAction(LaunchRes.getString("Action.Import")) {
      public void actionPerformed(ActionEvent e) {
        getXMLChooser().setFileFilter(xmlFileFilter);
        int result = getXMLChooser().showOpenDialog(null);
        if(result==JFileChooser.APPROVE_OPTION) {
          // open the file in an xml control
          File file = getXMLChooser().getSelectedFile();
          String fileName = file.getAbsolutePath();
          OSPFrame.chooserDir = XML.getDirectoryPath(file.getPath());
          XMLControlElement control = new XMLControlElement(fileName);
          if(control.failedToRead()) {
            OSPLog.info(LaunchRes.getString("Log.Message.InvalidXML")+" "+fileName);
            JOptionPane.showMessageDialog(null, LaunchRes.getString("Dialog.InvalidXML.Message")+" \""+XML.getName(fileName)+"\"", LaunchRes.getString("Dialog.InvalidXML.Title"), JOptionPane.WARNING_MESSAGE);
            return;
          }
          if(control.getObjectClass()==LaunchNode.class) {
            // add the child node
            LaunchNode child = (LaunchNode) control.loadObject(null);
            child.setFileName(fileName);
            addChildToSelectedNode(child);
          } else {
            OSPLog.info(LaunchRes.getString("Log.Message.NotLauncherFile")+" "+fileName);
            JOptionPane.showMessageDialog(null, LaunchRes.getString("Dialog.NotLauncherFile.Message")+" \""+XML.getName(fileName)+"\"", LaunchRes.getString("Dialog.NotLauncherFile.Title"), JOptionPane.WARNING_MESSAGE);
          }
        }
      }
    };
    saveAction = new AbstractAction(LaunchRes.getString("Action.SaveNode")) {
      public void actionPerformed(ActionEvent e) {
        LaunchNode node = getSelectedNode();
        if(node.getFileName()!=null) {
          save(node, node.getFileName());
          refreshGUI();
        }
      }
    };
    saveAsAction = new AbstractAction(LaunchRes.getString("Action.SaveNodeAs")) {
      public void actionPerformed(ActionEvent e) {
        LaunchNode node = getSelectedNode();
        LaunchNode parent = (LaunchNode) node.getParent();
        String fileName = saveAs(node);
        if(fileName!=null) {
          // set ancestors to self-contained=false so this node will be saved correctly
          selfContained = false;
          Enumeration en = node.pathFromAncestorEnumeration(node.getRoot());
          while(en.hasMoreElements()) {
            LaunchNode next = (LaunchNode) en.nextElement();
            next.setSelfContained(false);
            next.parentSelfContained = false;
          }
          if(parent!=null) {
            if(parent.getOwner()!=null) {
              changedFiles.add(parent.getOwner().getFileName());
            }
            refreshClones(parent);
          }
        }
        refreshGUI();
      }
    };
    saveAllAction = new AbstractAction(LaunchRes.getString("Action.SaveAll")) {
      public void actionPerformed(ActionEvent e) {
        if(tabSetName.equals(LaunchRes.getString("Tabset.Name.New"))) {
          saveTabSetAs();
        } else {
          saveTabSet();
        }
        refreshGUI();
      }
    };
    saveSetAsAction = new AbstractAction(LaunchRes.getString("Action.SaveSetAs")) {
      public void actionPerformed(ActionEvent e) {
        saveTabSetAs();
        refreshGUI();
      }
    };
    moveUpAction = new AbstractAction(LaunchRes.getString("Action.Up")) {
      public void actionPerformed(ActionEvent e) {
        LaunchNode node = getSelectedNode();
        if(node==null) {
          return;
        }
        LaunchNode parent = (LaunchNode) node.getParent();
        if(parent==null) {
          return;
        }
        int i = parent.getIndex(node);
        if(i>0) {
          getSelectedTab().treeModel.removeNodeFromParent(node);
          getSelectedTab().treeModel.insertNodeInto(node, parent, i-1);
          getSelectedTab().setSelectedNode(node);
          if(parent.getOwner()!=null) {
            changedFiles.add(parent.getOwner().getFileName());
          } else {
            changedFiles.add(tabSetName);
          }
          refreshGUI();
        }
      }
    };
    moveDownAction = new AbstractAction(LaunchRes.getString("Action.Down")) {
      public void actionPerformed(ActionEvent e) {
        LaunchNode node = getSelectedNode();
        if(node==null) {
          return;
        }
        LaunchNode parent = (LaunchNode) node.getParent();
        if(parent==null) {
          return;
        }
        int i = parent.getIndex(node);
        int end = parent.getChildCount();
        if(i<end-1) {
          getSelectedTab().treeModel.removeNodeFromParent(node);
          getSelectedTab().treeModel.insertNodeInto(node, parent, i+1);
          getSelectedTab().setSelectedNode(node);
          if(parent.getOwner()!=null) {
            changedFiles.add(parent.getOwner().getFileName());
          } else {
            changedFiles.add(tabSetName);
          }
          refreshGUI();
        }
      }
    };
    // create focus listener
    focusListener = new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        refreshSelectedNode();
        refreshGUI();
      }
    };
    // create key listener
    keyListener = new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        JComponent comp = (JComponent) e.getSource();
        if(e.getKeyCode()==KeyEvent.VK_ENTER&&((comp!=descriptionPane&&comp!=commentPane)||e.isControlDown()||e.isShiftDown())) {
          refreshSelectedNode();
          refreshGUI();
        } else {
          comp.setBackground(Color.yellow);
        }
      }
    };
  }

  /**
   * Removes the selected node.
   */
  protected void removeSelectedNode() {
    LaunchNode node = getSelectedNode();
    if(node==null||node.getParent()==null) {
      Toolkit.getDefaultToolkit().beep();
      return;
    }
    LaunchNode parent = (LaunchNode) node.getParent();
    getSelectedTab().treeModel.removeNodeFromParent(node);
    getSelectedTab().setSelectedNode(parent);
    if(parent.getOwner()!=null) {
      changedFiles.add(parent.getOwner().getFileName());
    } else {
      changedFiles.add(tabSetName);
    }
    refreshClones(parent);
    refreshGUI();
  }

  /**
   * Adds a child node to the selected node.
   *
   * @param child the child node to add
   */
  protected void addChildToSelectedNode(LaunchNode child) {
    LaunchNode parent = getSelectedNode();
    if(parent!=null&&child!=null) {
      LaunchNode[] nodes = child.getAllOwnedNodes();
      for(int i = 0;i<nodes.length;i++) {
        LaunchNode node = getSelectedTab().getClone(nodes[i]);
        if(node!=null) {
          getSelectedTab().setSelectedNode(node);
          JOptionPane.showMessageDialog(frame, LaunchRes.getString("Dialog.DuplicateNode.Message")+" \""+node+"\"", LaunchRes.getString("Dialog.DuplicateNode.Title"), JOptionPane.WARNING_MESSAGE);
          return;
        }
      }
      getSelectedTab().treeModel.insertNodeInto(child, parent, parent.getChildCount());
      getSelectedTab().tree.scrollPathToVisible(new TreePath(child.getPath()));
      child.setLaunchClass(child.launchClassName);
      if(parent.getOwner()!=null) {
        changedFiles.add(parent.getOwner().getFileName());
      } else {
        changedFiles.add(tabSetName);
      }
      refreshClones(parent);
      refreshGUI();
    }
  }

  /**
   * Replaces clones of a specified node with new clones.
   *
   * @param node the current version of the node to clone
   */
  protected void refreshClones(LaunchNode node) {
    Map clones = getClones(node);
    replaceClones(node, clones);
  }

  /**
   * Replaces nodes with clones of the specified node.
   *
   * @param node the current version of the node to clone
   * @param clones the current clones to replace
   */
  protected void replaceClones(LaunchNode node, Map clones) {
    // find clones
    if(clones.isEmpty()) {
      return;
    }
    // replace clones
    XMLControl control = new XMLControlElement(node.getOwner());
    Iterator it = clones.keySet().iterator();
    while(it.hasNext()) {
      LaunchPanel tab = (LaunchPanel) it.next();
      LaunchNode clone = (LaunchNode) clones.get(tab);
      LaunchNode parent = (LaunchNode) clone.getParent();
      boolean expanded = tab.tree.isExpanded(new TreePath(clone.getPath()));
      if(parent!=null) {
        int index = parent.getIndex(clone);
        tab.treeModel.removeNodeFromParent(clone);
        clone = (LaunchNode) new XMLControlElement(control).loadObject(null);
        clone.setFileName(node.getFileName());
        tab.treeModel.insertNodeInto(clone, parent, index);
      } else {
        clone = (LaunchNode) new XMLControlElement(control).loadObject(null);
        clone.setFileName(node.getFileName());
        tab.treeModel.setRoot(clone);
      }
      if(expanded) {
        tab.tree.expandPath(new TreePath(clone.getPath()));
      }
    }
  }

  /**
   * Returns clones containing a specified node in a tab-to-node map.
   *
   * @param node the node
   * @return the tab-to-node map
   */
  protected Map getClones(LaunchNode node) {
    Map clones = new HashMap();
    // find clones
    node = node.getOwner();
    if(node==null) {
      return clones;
    }
    Component[] tabs = tabbedPane.getComponents();
    for(int i = 0;i<tabs.length;i++) {
      LaunchPanel tab = (LaunchPanel) tabs[i];
      LaunchNode clone = tab.getClone(node);
      if(clone!=null&&clone!=node) {
        clones.put(tab, clone);
      }
    }
    return clones;
  }

  /**
   * Shows the about dialog.
   */
  protected void showAboutDialog() {
    String aboutString = "Launch Builder 1.0  Aug 2005\n"+"Open Source Physics Project\n"+"www.opensourcephysics.org";
    JOptionPane.showMessageDialog(frame, aboutString, LaunchRes.getString("Help.About.Title")+" LaunchBuilder", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Gets a file chooser for selecting jar files.
   *
   * @return the jar chooser
   */
  protected static JFileChooser getJARChooser() {
    getFileChooser().setFileFilter(jarFileFilter);
    return fileChooser;
  }

  /**
   * Gets a file chooser for selecting html files.
   *
   * @return the html chooser
   */
  protected static JFileChooser getHTMLChooser() {
    getFileChooser().setFileFilter(htmlFileFilter);
    return fileChooser;
  }

  /**
   * Gets a file chooser.
   *
   * @return the file chooser
   */
  protected static JFileChooser getFileChooser() {
    if(fileChooser==null) {
      fileChooser = new JFileChooser(new File(OSPFrame.chooserDir));
      allFileFilter = fileChooser.getFileFilter();
      // create jar file filter
      jarFileFilter = new javax.swing.filechooser.FileFilter() {
        // accept all directories and *.jar files.
        public boolean accept(File f) {
          if(f==null) {
            return false;
          }
          if(f.isDirectory()) {
            return true;
          }
          String extension = null;
          String name = f.getName();
          int i = name.lastIndexOf('.');
          if(i>0&&i<name.length()-1) {
            extension = name.substring(i+1).toLowerCase();
          }
          if(extension!=null&&extension.equals("jar")) {
            return true;
          }
          return false;
        }
        // the description of this filter
        public String getDescription() {
          return LaunchRes.getString("FileChooser.JarFilter.Description");
        }
      };
      // create html file filter
      htmlFileFilter = new javax.swing.filechooser.FileFilter() {
        // accept all directories, *.htm and *.html files.
        public boolean accept(File f) {
          if(f==null) {
            return false;
          }
          if(f.isDirectory()) {
            return true;
          }
          String extension = null;
          String name = f.getName();
          int i = name.lastIndexOf('.');
          if(i>0&&i<name.length()-1) {
            extension = name.substring(i+1).toLowerCase();
          }
          if(extension!=null&&(extension.equals("htm")||extension.equals("html"))) {
            return true;
          }
          return false;
        }
        // the description of this filter
        public String getDescription() {
          return LaunchRes.getString("FileChooser.HTMLFilter.Description");
        }
      };
    }
    fileChooser.removeChoosableFileFilter(jarFileFilter);
    fileChooser.removeChoosableFileFilter(htmlFileFilter);
    fileChooser.setFileFilter(allFileFilter);
    return fileChooser;
  }

  /**
   * Handles a mouse pressed event.
   *
   * @param e the mouse event
   * @param tab the launch panel triggering the event
   */
  protected void handleMousePressed(MouseEvent e, final LaunchPanel tab) {
    super.handleMousePressed(e, tab);
    if(e.isPopupTrigger()||e.getButton()==MouseEvent.BUTTON3||(e.isControlDown()&&System.getProperty("os.name", "").indexOf("Mac")>-1)) {
      TreePath path = tab.tree.getPathForLocation(e.getX(), e.getY());
      if(path==null) {
        return;
      }
      final LaunchNode node = getSelectedNode();
      if(node==null) {
        return;
      }
      // add items to popup
      String fileName = node.getFileName();
      if(fileName!=null&&changedFiles.contains(fileName)) {
        // add saveNode item
        if(popup.getComponentCount()!=0) {
          popup.addSeparator();
        }
        popup.add(saveNodeItem);
      }
      // add saveNodeAs item
      if(popup.getComponentCount()!=0) {
        popup.addSeparator();
      }
      popup.add(saveNodeAsItem);
      // add openTab item
      if(!node.isRoot()) {
        popup.addSeparator();
        JMenuItem item = new JMenuItem(openTabAction);
        popup.add(item);
      }
      popup.show(tab, e.getX()+4, e.getY()+12);
    }
  }

  /**
   * Overrides Launcher exit method.
   */
  protected void exit() {
    if(!saveAllChanges()) {
      // change default close operation to prevent window closing
      final int op = frame.getDefaultCloseOperation();
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      // restore default close operation
      Runnable runner = new Runnable() {
        public void run() {
          frame.setDefaultCloseOperation(op);
        }
      };
      SwingUtilities.invokeLater(runner);
      return;
    }
    super.exit();
  }

  /**
   * Returns true if tabset is writable.
   *
   * @return true if writable
   */
  protected boolean isTabSetWritable() {
    String path = XML.getResolvedPath(tabSetName, tabSetBasePath);
    Resource res = ResourceLoader.getResource(path);
    File file = res==null ? null : res.getFile();
    boolean writable = file==null ? true : file.canWrite();
    if(!selfContained) {
      for(int i = 0;i<tabbedPane.getTabCount();i++) {
        LaunchNode root = getTab(i).getRootNode();
        writable = writable&&isNodeWritable(root);
      }
    }
    return writable;
  }

  /**
   * Returns true if node is writable.
   *
   * @param node the node
   * @return true if writable
   */
  protected boolean isNodeWritable(LaunchNode node) {
    File file = node.getFile();
    boolean writable = file==null ? true : file.canWrite();
    // check node's owned child nodes if not self contained
    if(!node.isSelfContained()) {
      LaunchNode[] nodes = node.getChildOwnedNodes();
      for(int i = 0;i<nodes.length;i++) {
        writable = writable&&isNodeWritable(nodes[i]);
      }
    }
    return writable;
  }
}

/*
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
