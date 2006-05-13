/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.opensourcephysics.controls.*;

/**
 * This is a dialog that displays a tree showing tabs to be saved.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class LaunchSaver extends JDialog {
  // instance fields
  private String prevTabSetName;
  private String prevTabSetBasePath;
  private boolean prevTabSetSelfContained;
  private Map prevNodeNames = new HashMap();
  private Map prevNodeSelfContains = new HashMap();
  private LaunchBuilder builder;
  private DefaultTreeModel treeModel;
  private JTree tree;
  private ArrayList treePaths = new ArrayList();
  private JTextField pathField;
  private JButton chooseButton;
  private JButton inspectButton;
  private Node root;
  private JScrollPane treeScroller;
  private boolean approved = false;
  private Editor editor = new Editor();
  private JDialog inspector;
  private boolean active;

  /**
   * Constructor.
   *
   * @param builder the LaunchBuilder with tabs to save
   */
  public LaunchSaver(LaunchBuilder builder) {
    super(builder.frame, true);
    createGUI();
    setBuilder(builder); // creates tree for launch builder
    pack();
    // center on screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (dim.width-getBounds().width)/2;
    int y = (dim.height-getBounds().height)/2;
    setLocation(x, y);
  }

  /**
   * Sets the LaunchBuilder and creates a new tree.
   *
   * @param builder the LaunchBuilder
   */
  public void setBuilder(LaunchBuilder builder) {
    this.builder = builder;
    createTree();
    refresh();
    approved = false;
  }

  /**
   * Sets the selected node.
   *
   * @param node the node to select
   */
  public void setSelectedNode(Node node) {
    if(node==null) {
      return;
    }
    tree.setSelectionPath(new TreePath(node.getPath()));
  }

  /**
   * Gets the selected node.
   *
   * @return the selected node
   */
  public Node getSelectedNode() {
    TreePath path = tree.getSelectionPath();
    if(path==null) {
      return null;
    } else {
      return(Node) path.getLastPathComponent();
    }
  }

  /**
   * Returns true if user has clicked the save button, otherwise false.
   *
   * @return true to save
   */
  public boolean isApproved() {
    return approved;
  }

  // ______________________________ private methods _____________________________

  /**
   * Refreshes the GUI.
   */
  private void refresh() {
    inspectButton.setEnabled(getSelectedNode()!=null);
    String path = null;
    if(!Launcher.tabSetBasePath.equals("")) {
      path = Launcher.tabSetBasePath;
    } else if(builder.jarBasePath!=null) {
      path = builder.jarBasePath;
    } else {
      path = XML.forwardSlash(XML.getUserDirectory());
    }
    pathField.setText(path+"/");
    pathField.setBackground(Color.white);
    treeModel.nodeChanged(root);
    tree.repaint();
  }

  /**
   * Displays the properties of the selected node in an xml inspector.
   */
  private void inspectSelectedNode() {
    Node node = getSelectedNode();
    if(node==null) {
      node = root;
    }
    Object obj = node.node;
    if(obj==null) {
      obj = builder.new LaunchSet(builder, builder.tabSetName);
    }
    XMLControl xml = new XMLControlElement(obj);
    XMLTreePanel treePanel = new XMLTreePanel(xml, false);
    inspector.setContentPane(treePanel);
    String name = node!=root ? node.node.getFileName() : builder.tabSetName;
    name = XML.getResolvedPath(name, Launcher.tabSetBasePath);
    inspector.setTitle(LaunchRes.getString("Inspector.Title.File")+" \""+name+"\"");
    inspector.setVisible(true);
  }

  /**
   * Reverts to initial state.
   */
  private void revert() {
    Launcher.tabSetBasePath = prevTabSetBasePath;
    builder.tabSetName = prevTabSetName;
    builder.selfContained = prevTabSetSelfContained;
    // set parentSelfContained property of tabs
    for(int i = 0;i<builder.tabbedPane.getTabCount();i++) {
      LaunchNode root = builder.getTab(i).getRootNode();
      root.parentSelfContained = prevTabSetSelfContained;
    }
    Iterator it = prevNodeNames.keySet().iterator();
    while(it.hasNext()) {
      Node node = (Node) it.next();
      String path = (String) prevNodeNames.get(node);
      node.node.setFileName(path);
      Boolean bool = (Boolean) prevNodeSelfContains.get(node);
      node.node.setSelfContained(bool.booleanValue());
    }
  }

  /**
   * Sets the base path.
   *
   * @param path the path
   */
  private void setBasePath(String path) {
    path = XML.forwardSlash(path);
    // strip leading and trailing slashes
    boolean leadingSlash = false;
    while(path.startsWith("/")) {
      leadingSlash = true;
      path = path.substring(1);
    }
    // replace single leading slash if any found
    if(leadingSlash) {
      path = "/"+path;
    }
    while(path.endsWith("/")) {
      path = path.substring(0, path.length()-1);
    }
    if(!path.startsWith("/")&&path.indexOf(":")==-1) { // not an absolute path
      String base = XML.forwardSlash(XML.getUserDirectory());
      if(builder.jarBasePath!=null) {
        base = builder.jarBasePath;
      }
      if(!path.equals("")) {
        base += "/";
      }
      path = base+path;
    }
    Launcher.tabSetBasePath = path;
    refresh();
  }

  /**
   * Sets the tabset name.
   *
   * @param proposed the proposed name
   * @return the tabset name
   */
  private String setTabSetName(String proposed) {
    String name = XML.getName(proposed);
    if(!"xset".equals(XML.getExtension(name))) {
      while(name.indexOf(".")>-1) {
        name = name.substring(0, name.length()-1);
      }
      name += ".xset";
    }
    builder.tabSetName = name;
    refresh();
    return name;
  }

  /**
   * Creates the GUI.
   */
  private void createGUI() {
    // create inspector
    inspector = new JDialog(this, false);
    inspector.setSize(new java.awt.Dimension(600, 300));
    // center inspector on screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (dim.width-inspector.getBounds().width)/2;
    int y = (dim.height-inspector.getBounds().height)/2;
    inspector.setLocation(x, y);
    // create panel components
    setTitle(LaunchRes.getString("Saver.Title"));
    JPanel panel = new JPanel(new BorderLayout());
    panel.setPreferredSize(new Dimension(600, 300));
    setContentPane(panel);
    JPanel legend = new JPanel(new GridLayout(1, 4));
    panel.add(legend, BorderLayout.NORTH);
    JLabel label = new JLabel(LaunchRes.getString("Saver.Legend.New"), Launcher.greenFileIcon, JLabel.HORIZONTAL);
    label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    legend.add(label);
    label = new JLabel(LaunchRes.getString("Saver.Legend.Replace"), Launcher.yellowFileIcon, JLabel.HORIZONTAL);
    label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    legend.add(label);
    label = new JLabel(LaunchRes.getString("Saver.Legend.ReadOnly"), Launcher.redFileIcon, JLabel.HORIZONTAL);
    label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    legend.add(label);
    label = new JLabel(LaunchRes.getString("Saver.Legend.SelfContained"), Launcher.whiteFolderIcon, JLabel.HORIZONTAL);
    label.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    legend.add(label);
    legend.setBorder(BorderFactory.createLoweredBevelBorder());
    JPanel lower = new JPanel(new BorderLayout());
    panel.add(lower, BorderLayout.SOUTH);
    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);
    lower.add(toolbar, BorderLayout.NORTH);
    label = new JLabel(LaunchRes.getString("Saver.Label.Base"));
    label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 4));
    toolbar.add(label);
    pathField = new JTextField();
    pathField.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_ENTER) {
          setBasePath(pathField.getText());
        } else {
          pathField.setBackground(Color.yellow);
        }
      }
    });
    pathField.addFocusListener(new FocusAdapter() {
      public void focusLost(FocusEvent e) {
        setBasePath(pathField.getText());
      }
    });
    toolbar.add(pathField);
    // choose button
    chooseButton = new JButton(LaunchRes.getString("Saver.Button.Choose"));
    chooseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        chooseTabSet();
      }
    });
    // inspect button
    inspectButton = new JButton(LaunchRes.getString("MenuItem.Inspect"));
    inspectButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        inspectSelectedNode();
      }
    });
    JToolBar buttonbar = new JToolBar();
    buttonbar.setFloatable(false);
    buttonbar.add(Box.createHorizontalGlue());
    buttonbar.add(Box.createHorizontalGlue());
    buttonbar.add(Box.createHorizontalGlue());
    JPanel buttons = new JPanel(new GridLayout(1, 4));
    buttonbar.add(buttons);
    buttons.add(inspectButton);
    buttons.add(chooseButton);
    lower.add(buttonbar, BorderLayout.SOUTH);
    // save button
    JButton saveButton = new JButton(LaunchRes.getString("Saver.Button.Save"));
    buttons.add(saveButton);
    saveButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        approved = true;
        setVisible(false);
      }
    });
    // cancel button
    JButton cancelButton = new JButton(LaunchRes.getString("Saver.Button.Cancel"));
    buttons.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        revert();
        setVisible(false);
      }
    });
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        if(!isApproved()) {
          revert();
        }
      }
    });
  }

  /**
   * Creates a new tree.
   */
  private void createTree() {
    active = false;
    treePaths.clear();
    // save initial values for revert method
    prevNodeNames.clear();
    prevNodeSelfContains.clear();
    prevTabSetName = builder.tabSetName;
    prevTabSetBasePath = Launcher.tabSetBasePath;
    prevTabSetSelfContained = builder.selfContained;
    // create root node
    root = new Node();
    treePaths.add(new TreePath(root.getPath()));
    // add child nodes to root
    int n = builder.tabbedPane.getTabCount();
    for(int i = 0;i<n;i++) {
      Node tab = new Node(builder.getTab(i).getRootNode());
      if(tab.node.getFileName()!=null) {
        root.add(tab);
        addChildren(tab); // also saves tab file name and selfContained
      } else {
        LaunchNode[] nodes = tab.node.getChildOwnedNodes();
        for(int k = 0;k<nodes.length;k++) {
          Node node = new Node(nodes[k]);
          root.add(node);
          addChildren(node);
        }
      }
    }
    // create tree model and tree
    treeModel = new DefaultTreeModel(root);
    tree = new JTree(treeModel);
    tree.setCellRenderer(new Renderer());
    tree.setCellEditor(editor);
    tree.setEditable(true);
    tree.setShowsRootHandles(true);
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if(path==null) {
          editor.stopCellEditing();
        }
      }
    });
    tree.addTreeWillExpandListener(new ExpansionListener());
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        if(inspector.isVisible()) {
          inspectSelectedNode();
        }
      }
    });
    // set tree row height
    FontRenderContext frc = new FontRenderContext(null, // no AffineTransform
      false,                                            // no antialiasing
      false);                                           // no fractional metrics
    Font font = editor.field.getFont();
    Rectangle2D rect = font.getStringBounds("A", frc);
    int h = (int) rect.getHeight();
    tree.setRowHeight(h+3);
    tree.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
    // put tree in a scroller and add to dialog
    if(treeScroller!=null) {
      getContentPane().remove(treeScroller);
    }
    treeScroller = new JScrollPane(tree);
    getContentPane().add(treeScroller, BorderLayout.CENTER);
    setSelectedNode(root);
    // collapse tree to show self-containment structure
    ListIterator it = treePaths.listIterator();
    while(it.hasNext()) {
      TreePath path = (TreePath) it.next();
      tree.expandPath(path);
    }
    while(it.hasPrevious()) {
      TreePath path = (TreePath) it.previous();
      Node node = (Node) path.getLastPathComponent();
      if(isFolder(node)) {
        tree.collapsePath(path);
      }
    }
    active = true;
  }

  private void addChildren(Node node) {
    // save tab file name and selfContained property
    prevNodeNames.put(node, node.node.getFileName());
    prevNodeSelfContains.put(node, new Boolean(node.node.selfContained));
    treePaths.add(new TreePath(node.getPath()));
    // add node's owned child nodes
    LaunchNode[] nodes = node.node.getChildOwnedNodes();
    for(int k = 0;k<nodes.length;k++) {
      Node child = new Node(nodes[k]);
      node.add(child);
      addChildren(child);
    }
  }

  private boolean isFolder(Node node) {
    if(node==root) {
      return builder.selfContained;
    } else {
      return node.node.selfContained&&!node.isLeaf();
    }
  }

  /**
   * Selects the tabset with a chooser.
   */
  private void chooseTabSet() {
    String fileName = XML.getResolvedPath(builder.tabSetName, Launcher.tabSetBasePath);
    JFileChooser chooser = Launcher.getXMLChooser();
    chooser.setSelectedFile(new File(fileName));
    chooser.setFileFilter(Launcher.xsetFileFilter);
    int result = chooser.showDialog(this, LaunchRes.getString("Saver.FileChooser.Title"));
    if(result==JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      fileName = file.getAbsolutePath();
      setTabSetName(fileName);
      setBasePath(XML.getDirectoryPath(fileName));
    }
    refresh();
  }

  private Icon getIcon(Node node) {
    File file;
    if(node==root) {
      String path = XML.getResolvedPath(builder.tabSetName, Launcher.tabSetBasePath);
      Resource res = ResourceLoader.getResource(path);
      file = (res==null ? null : res.getFile());
    } else {
      file = node.node.getFile();
    }
    // green icon if creating
    if(file==null) {
      if(isFolder(node)) {
        return Launcher.greenFolderIcon;
      }
      return Launcher.greenFileIcon;
    }
    // yellow icon if replacing
    else if(file.canWrite()) {
      if(isFolder(node)) {
        return Launcher.yellowFolderIcon;
      }
      return Launcher.yellowFileIcon;
    }
    // red icon if read-only
    else {
      if(isFolder(node)) {
        return Launcher.redFolderIcon;
      }
      return Launcher.redFileIcon;
    }
  }

  /**
   * A tree node.
   */
  private class Node extends DefaultMutableTreeNode {
    private LaunchNode node;
    // Constructor.

    Node() {}

    // Constructor for launch node.
    Node(LaunchNode node) {
      this.node = node;
    }

    // returns the text displayed.
    public String toString() {
      if(node==null) {
        return LaunchRes.getString("Saver.Tree.TabSet")+":";
      } else if(node.isRoot()) {
        return LaunchRes.getString("Saver.Tree.Tab")+" \""+node.name+"\":";
      } else {
        return LaunchRes.getString("Saver.Tree.Node")+" \""+node.name+"\":";
      }
    }
  }

  /**
   * A class to render tree cells.
   */
  class Renderer implements TreeCellRenderer {
    JPanel panel = new JPanel(new BorderLayout());
    JLabel label = new JLabel();
    JTextField field = new JTextField();

    // Constructor.
    public Renderer() {
      panel.add(label, BorderLayout.WEST);
      panel.add(field, BorderLayout.CENTER);
      panel.setOpaque(false);
      label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
      field.setBorder(null);
      field.setBackground(field.getSelectionColor());
    }

    // Gets the component to be displayed.
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      Node node = (Node) value;
      String name = node!=root ? node.node.getFileName() : builder.tabSetName;
      field.setText(name);
      label.setIcon(getIcon(node));
      label.setText(node.toString());
      field.setOpaque(selected ? true : false);
      return panel;
    }
  }

  /**
   * A class to edit tree node names.
   */
  private class Editor extends AbstractCellEditor implements TreeCellEditor {
    JPanel panel = new JPanel(new BorderLayout());
    JTextField field = new JTextField();
    JLabel label = new JLabel();

    // Constructor.
    Editor() {
      panel.add(label, BorderLayout.WEST);
      panel.add(field, BorderLayout.CENTER);
      panel.setOpaque(false);
      label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
      field.setBorder(BorderFactory.createLineBorder(Color.black));
      field.setEditable(true);
      field.setColumns(30);
      field.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          stopCellEditing();
        }
      });
      field.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          if(e.getKeyCode()==KeyEvent.VK_ENTER) {
            stopCellEditing();
          } else {
            field.setBackground(Color.yellow);
          }
        }
      });
    }

    // Gets the component to be displayed while editing.
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
      Node node = (Node) value;
      String name = node!=root ? node.node.getFileName() : builder.tabSetName;
      field.setText(name);
      label.setIcon(getIcon(node));
      label.setText(node.toString());
      return panel;
    }

    // Determines when editing starts.
    public boolean isCellEditable(EventObject e) {
      if(e instanceof MouseEvent) {
        return true;
      }
      return false;
    }

    // Called when editing is completed.
    public Object getCellEditorValue() {
      field.setBackground(Color.white);
      Node node = getSelectedNode();
      String name = field.getText();
      // prevent duplicate file names
      String prev;
      if(node.node!=null) {
        prev = node.node.getFileName();
      } else {
        prev = builder.tabSetName;
      }
      if(builder.getOpenPaths().contains(name)&&!prev.equals(name)) {
        JOptionPane.showMessageDialog(LaunchSaver.this, LaunchRes.getString("Dialog.DuplicateFileName.Message")+" \""+name+"\"", LaunchRes.getString("Dialog.DuplicateFileName.Title"), JOptionPane.WARNING_MESSAGE);
        refresh();
        return null;
      }
      if(node.node!=null) {
        node.node.setFileName(name);
      } else {
        name = setTabSetName(name);
      }
      return name;
    }
  }

  private class ExpansionListener implements TreeWillExpandListener {
    public void treeWillExpand(TreeExpansionEvent e) {
      TreePath path = e.getPath();
      set(path, false);
    }

    public void treeWillCollapse(TreeExpansionEvent e) {
      TreePath path = e.getPath();
      set(path, true);
    }

    private void set(TreePath path, boolean selfContained) {
      if(active) {
        Node node = (Node) path.getLastPathComponent();
        if(node==root) {
          builder.selfContained = selfContained;
          // set parentSelfContained property of tabs
          for(int i = 0;i<builder.tabbedPane.getTabCount();i++) {
            LaunchNode root = builder.getTab(i).getRootNode();
            root.parentSelfContained = selfContained;
          }
        } else {
          node.node.setSelfContained(selfContained);
        }
        if(inspector.isVisible()) {
          inspectSelectedNode();
        }
      }
    }
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
