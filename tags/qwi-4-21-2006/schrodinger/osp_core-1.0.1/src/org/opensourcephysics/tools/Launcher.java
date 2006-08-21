/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.*;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;
import javax.swing.border.Border;

/**
 * This provides a GUI for launching osp applications and xml files.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class Launcher {
  // static fields
  protected static String defaultFileName = "launcher_default";
  protected static String resourcesPath = "/org/opensourcephysics/resources/tools/";
  protected static String classPath; // list of jar names for classes, resources
  protected static String tabSetBasePath = ""; // absolute base path of tabset
  protected static JFileChooser chooser;
  protected static FileFilter xmlFileFilter;
  protected static FileFilter xsetFileFilter;
  protected static FileFilter launcherFileFilter;
  protected static int wInit = 480, hInit = 400;
  protected static JDialog splashDialog;
  protected static JLabel splashTitleLabel;
  protected static JLabel splashPathLabel;
  protected static javax.swing.Timer splashTimer;
  // some icons are created in LaunchBuilder

  protected static Icon launchIcon, launchedIcon, singletonIcon, whiteFolderIcon;
  protected static Icon redFileIcon, greenFileIcon, magentaFileIcon, yellowFileIcon;
  protected static Icon whiteFileIcon, noFileIcon, ghostFileIcon;
  protected static Icon redFolderIcon, greenFolderIcon, yellowFolderIcon;
  public static boolean singleAppMode = false;
  public static boolean singletonMode = false;
  private static boolean newVMAllowed = false;
  protected static javax.swing.Timer frameFinder;
  protected static ArrayList existingFrames = new ArrayList();
  public static LaunchNode activeNode;

  // instance fields
  protected JDialog inspector;
  protected int divider = 160;
  public JFrame frame;
  protected JPanel contentPane;
  protected JTabbedPane tabbedPane;
  protected JMenuItem singleAppItem;
  protected LaunchNode selectedNode;
  protected LaunchNode previousNode;
  protected String tabSetName;                // name relative to tabSetBasePath
  protected JMenu fileMenu;
  protected JMenuItem openItem;
  protected JMenuItem closeTabItem;
  protected JMenuItem closeAllItem;
  protected JMenuItem editItem;
  protected JMenuItem exitItem;
  protected JMenuItem inspectItem;
  protected JMenuItem hideItem;
  protected LaunchClassChooser classChooser;
  protected JPopupMenu popup = new JPopupMenu();
  protected Set openPaths = new HashSet();    // relative paths
  protected Launcher spawner;
  protected boolean previewing = false;
  protected boolean editorEnabled = false;    // enables editing current set
  protected Set changedFiles = new HashSet(); // relative paths
  protected MouseListener tabListener;
  protected boolean newNodeSelected = false;
  protected boolean selfContained = false;
  protected String jarBasePath = null;
  protected String title;
  protected ArrayList tabs = new ArrayList();

  /**
   * Constructs a bare Launcher with a splash screen.
   */
  public Launcher() {
    this(true);
  }

  /**
   * Constructs a bare Launcher with or without a splash screen.
   *
   * @param splash true to show the splash screen
   */
  public Launcher(boolean splash) {
    createGUI(splash);
    XML.setLoader(LaunchSet.class, new LaunchSet());
    // determine whether launching in new vm is possible
    // create a test launch thread
    Runnable launchRunner = new Runnable() {
      public void run() {
        try {
          Process proc = Runtime.getRuntime().exec("java");
          // if it made it this far, new VM is allowed (?)
          newVMAllowed = true;
          proc.destroy();
        } catch(Exception ex) {}
      }
    };
    try {
      Thread thread = new Thread(launchRunner);
      thread.start();
      thread.join(5000); // wait for join but don't wait over 5 seconds
    } catch(InterruptedException ex) {}
    // center frame on the screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (dim.width-frame.getBounds().width)/2;
    int y = (dim.height-frame.getBounds().height)/2;
    frame.setLocation(x, y);
  }

  /**
   * Constructs a Launcher and opens the specified xml file.
   *
   * @param fileName the name of the xml file
   */
  public Launcher(String fileName) {
    this(fileName, fileName==null||!fileName.startsWith("<?xml"));
  }

  /**
   * Constructs a Launcher and opens the specified xml file.
   *
   * @param fileName the name of the xml file
   * @param splash true to show the splash screen
   */
  public Launcher(String fileName, boolean splash) {
    createGUI(splash);
    XML.setLoader(LaunchSet.class, new LaunchSet());
    String path = null;
    if(fileName==null) {
      // look for default file with launchJarName or defaultFileName
      if (ResourceLoader.launchJarName != null) {
        fileName = XML.stripExtension(ResourceLoader.launchJarName)+".xset";
        path = open(fileName);
      }
      if(path==null) {
        fileName = defaultFileName+".xset";
        path = open(fileName);
      }
      if(path==null) {
        fileName = defaultFileName+".xml";
        path = open(fileName);
      }
    }
    else path = open(fileName);
    refreshGUI();
    // center frame on the screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (dim.width-frame.getBounds().width)/2;
    int y = (dim.height-frame.getBounds().height)/2;
    frame.setLocation(x, y);
  }

  /**
   * Gets the launch tree in the selected tab. May return null.
   *
   * @return the launch tree
   */
  public LaunchPanel getSelectedTab() {
    return(LaunchPanel) tabbedPane.getSelectedComponent();
  }

  /**
   * Gets the selected launch node. May return null.
   *
   * @return the selected launch node
   */
  public LaunchNode getSelectedNode() {
    if(getSelectedTab()==null) {
      selectedNode = null;
    } else {
      selectedNode = getSelectedTab().getSelectedNode();
    }
    return selectedNode;
  }

  /**
   * Gets the root node of the selected launch tree. May return null.
   *
   * @return the root node
   */
  public LaunchNode getRootNode() {
    if(getSelectedTab()==null) {
      return null;
    }
    return getSelectedTab().getRootNode();
  }

  /**
   * Gets the launch tree at the specified tab index. May return null.
   *
   * @param i the tab index
   * @return the launch tree
   */
  public LaunchPanel getTab(int i) {
    if(i>=tabbedPane.getTabCount()) {
      return null;
    }
    return(LaunchPanel) tabbedPane.getComponentAt(i);
  }

  /**
   * Gets the content pane.
   *
   * @return the content pane
   */
  public Container getContentPane() {
    return contentPane;
  }

  /**
   * Opens an xml document specified by name and displays it in a new tab.
   * Name may be a relative path, absolute path, or self-contained xml string.
   *
   * @param name the name
   * @return the absolute path, or null if failed
   */
  public String open(String name) {
    if(name==null) {
      return null;
    }
    // add search paths to resource loader in last-to-first order
    ResourceLoader.addSearchPath(resourcesPath);
    ResourceLoader.addSearchPath(tabSetBasePath);
    String path = name;
    String absolutePath = "";
    XMLControlElement control = new XMLControlElement();
    // look for self-contained xml string (commonly used for previews)
    if(name.startsWith("<?xml")) {
      control.readXML(name);
      if(control.failedToRead()) {
        return null;
      }
    }
    if(control.getObjectClass()==Object.class) { // did not read xml string
      // read the named file
      absolutePath = control.read(path); // gets absolute path
    }
    if(control.failedToRead()) {
      String jar = XML.stripExtension(ResourceLoader.launchJarName);
      if(!name.startsWith(defaultFileName) &&
         jar != null &&
         !name.startsWith(jar)) {
        OSPLog.info(LaunchRes.getString("Log.Message.InvalidXML")+" "+name);
        JOptionPane.showMessageDialog(null, LaunchRes.getString("Dialog.InvalidXML.Message")+" \""+name+"\"", LaunchRes.getString("Dialog.InvalidXML.Title"), JOptionPane.WARNING_MESSAGE);
      }
      return null;
    }
    OSPLog.fine(name);
    // absolute and return paths are empty if name is xml string
    String returnPath = XML.forwardSlash(absolutePath);
    // set non-null jar base path if opening from a jar or zip file
    String jarName = LaunchRes.getString("Splash.Label.Internal");
    jarBasePath = null;
    int zip = Math.max(returnPath.indexOf("jar!"), returnPath.indexOf("zip!"));
    if(zip>-1) {
      jarName = XML.getName(returnPath.substring(0, zip+3));
      String s = XML.getDirectoryPath(returnPath.substring(0, zip+3));
      jarBasePath = s.equals("") ? XML.forwardSlash(System.getProperty("user.dir", "")) : s;
      returnPath = returnPath.substring(zip+5);
    }
    Class type = control.getObjectClass();
    if(type!=null&&LaunchSet.class.isAssignableFrom(type)) {
      if(!returnPath.equals("")&&splashDialog!=null&&splashDialog.isVisible()) {
        // show name and icon in splash label
        Resource res = ResourceLoader.getResource(path);
        String loading = LaunchRes.getString("Log.Message.Loading");
        if(res.getFile()!=null) { // external file
          loading += ": "+name;
          // pale blue background and magenta icon since external file
          splashDialog.getContentPane().setBackground(new Color(228, 228, 255));
          splashPathLabel.setIcon(magentaFileIcon);
          splashPathLabel.setText(loading);
          OSPLog.info(loading+" ("+res.getAbsolutePath()+")");
        } else {
          loading += " from "+jarName+": "+name;
          boolean internal = jarName.equals(LaunchRes.getString("Splash.Label.Internal"))||jarName.equals(ResourceLoader.launchJarName);
          if(internal) {
            // pale yellow background and green icon if internal resource
            splashDialog.getContentPane().setBackground(new Color(255, 255, 204));
            splashPathLabel.setIcon(greenFileIcon);
          } else {
            // pale blue background and magenta icon if external resource
            splashDialog.getContentPane().setBackground(new Color(228, 228, 255));
            splashPathLabel.setIcon(magentaFileIcon);
          }
          splashPathLabel.setText(loading);
          OSPLog.info(loading+" ("+res.getAbsolutePath()+")");
        }
      }
      // close all open tabs
      removeAllTabs();
      // set paths
      tabSetName = XML.getName(returnPath);
      if(!returnPath.equals("")) { // don't change static base path when reading xml string
        if(jarBasePath!=null) {
          tabSetBasePath = "";
        } else {
          tabSetBasePath = XML.getDirectoryPath(returnPath);
        }
      }
      // load the xml file data
      OSPLog.finest(LaunchRes.getString("Log.Message.Loading")+": "+returnPath);
      control.loadObject(new LaunchSet(this, tabSetName));
      changedFiles.clear();
      OSPLog.fine("returning "+returnPath);
      return returnPath;
    } else if(type!=null&&LaunchNode.class.isAssignableFrom(type)) {
      // load the xml file data
      OSPLog.finest(LaunchRes.getString("Log.Message.Loading")+": "+path);
      LaunchNode node = new LaunchNode(LaunchRes.getString("NewNode.Name"));
      // assign file name BEFORE loading node
      node.setFileName(XML.getPathRelativeTo(returnPath, tabSetBasePath));
      control.loadObject(node);
      String tabName = getDisplayName(returnPath);
      // if node is already open, select the tab
      for(int i = 0;i<tabbedPane.getComponentCount();i++) {
        if(tabbedPane.getTitleAt(i).equals(tabName)) {
          LaunchNode root = ((LaunchPanel) tabbedPane.getComponent(i)).getRootNode();
          if(root.matches(node)) {
            tabbedPane.setSelectedIndex(i);
            return null;
          }
        }
      }
      // if no tabset is open, then create new one
      if(tabSetName==null) {
        tabSetName = LaunchRes.getString("Tabset.Name.New");
        title = null;
        tabSetBasePath = XML.getDirectoryPath(returnPath);
        editorEnabled = false;
      }
      addTab(node);
      Enumeration e = node.breadthFirstEnumeration();
      while(e.hasMoreElements()) {
        LaunchNode next = (LaunchNode) e.nextElement();
        next.setLaunchClass(next.launchClassName);
      }
      return returnPath; // successfully opened and added to tab
    } else {
      OSPLog.info(LaunchRes.getString("Log.Message.NotLauncherFile"));
      JOptionPane.showMessageDialog(null, LaunchRes.getString("Dialog.NotLauncherFile.Message")+" \""+name+"\"", LaunchRes.getString("Dialog.NotLauncherFile.Title"), JOptionPane.WARNING_MESSAGE);
    }
    return null;
  }

  // ______________________________ protected methods _____________________________

  /**
   * Creates a LaunchPanel with the specified root and adds it to a new tab.
   *
   * @param root the root node
   * @return true if tab was added
   */
  protected boolean addTab(LaunchNode root) {
    final LaunchPanel tab = new LaunchPanel(root, this);
    tabs.add(tab);
    if(root.isHiddenInLauncher()&&!(this instanceof LaunchBuilder)) {
      return false;
    }
    tab.tree.setCellRenderer(new LaunchRenderer());
    tab.tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        newNodeSelected = true;
        refreshGUI();
        newNodeSelected = false;
      }
    });
    tab.tree.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        popup.removeAll();
        handleMousePressed(e, tab);
      }
    });
    tabbedPane.addTab(root.toString(), tab);
    // tabbedPane.addTab(getDisplayName(root.getFileName()), tab);
    tabbedPane.setSelectedComponent(tab);
    tab.setSelectedNode(root);
    tab.dataPanel.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        divider = tab.splitPane.getDividerLocation();
      }
    });
    return true;
  }

  /**
   * Opens an xml file selected with a chooser.
   *
   * @return the name of the opened file
   */
  protected String open() {
    getXMLChooser().setFileFilter(launcherFileFilter);
    int result = getXMLChooser().showOpenDialog(null);
    if(result==JFileChooser.APPROVE_OPTION) {
      File file = getXMLChooser().getSelectedFile();
      String fileName = XML.forwardSlash(file.getAbsolutePath());
      OSPFrame.chooserDir = XML.getDirectoryPath(fileName);
      return open(fileName);
    }
    return null;
  }

  /**
   * Removes the selected tab.
   *
   * @return true if the tab was removed
   */
  protected boolean removeSelectedTab() {
    int i = tabbedPane.getSelectedIndex();
    if(i<0) {
      return false;
    }
    tabs.remove(getTab(i));
    tabbedPane.removeTabAt(i);
    previousNode = selectedNode;
    newNodeSelected = true;
    if(tabbedPane.getTabCount()==0) {
      tabSetName = null;
      title = null;
      editorEnabled = false;
    }
    refreshGUI();
    return true;
  }

  /**
   * Removes all tabs.
   *
   * @return true if all tabs were removed
   */
  protected boolean removeAllTabs() {
    int n = tabbedPane.getTabCount();
    for(int i = n-1;i>=0;i--) {
      tabbedPane.removeTabAt(i);
    }
    if(tabbedPane.getTabCount()==0) {
      tabSetName = null;
      title = null;
      editorEnabled = false;
    } else {
      previousNode = selectedNode;
      newNodeSelected = true;
    }
    refreshGUI();
    tabs.clear();
    for(int i = 0;i<tabbedPane.getTabCount();i++) {
      tabs.add(tabbedPane.getComponentAt(i));
    }
    return tabbedPane.getTabCount()==0;
  }

  /**
   * Refreshes the GUI.
   */
  protected void refreshGUI() {
    // set frame title
    String name = title==null ? tabSetName : title;
    if(name==null) {
      frame.setTitle(LaunchRes.getString("Frame.Title"));
    } else {
      frame.setTitle(LaunchRes.getString("Frame.Title")+": "+name);
    }
    // set tab properties
    LaunchPanel tab = getSelectedTab();
    if(tab!=null) {
      getSelectedTab().splitPane.setDividerLocation(divider);
    }
    // rebuild file menu
    fileMenu.removeAll();
    if(OSPFrame.applet!=null) { // added by W. Christian
      fileMenu.add(hideItem);
      return;
    }
    fileMenu.add(openItem);
    if(tab!=null) {
      fileMenu.addSeparator();
      fileMenu.add(closeTabItem);
      fileMenu.add(closeAllItem);
      if(editorEnabled) {
        fileMenu.addSeparator();
        fileMenu.add(editItem);
      }
    }
    fileMenu.addSeparator();
    if(exitItem!=null) {
      fileMenu.add(exitItem);
    }
  }

  /**
   * Creates the GUI.
   */
  private void appletGUI() { // added by W. Christian
    hideItem = new JMenuItem(LaunchRes.getString("Menu.File.Hide"));
    hideItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        exit();
      }
    });
  }

  /**
   * Creates the GUI.
   *
   * @param splash true to show the splash screen
   */
  protected void createGUI(boolean splash) {
    appletGUI(); // added by W. Christian
    // get existing frames
    Frame[] frames = Frame.getFrames();
    for(int i = 0, n = frames.length;i<n;i++) {
      existingFrames.add(frames[i]);
    }
    // instantiate the OSPLog
    OSPLog.getOSPLog();
    // create the frame
    frame = new LauncherFrame();
    existingFrames.add(frame);
    if(splash&&OSPFrame.applet==null) {
      splash();
    }
    inspector = new JDialog(frame, false);
    inspector.setSize(new java.awt.Dimension(600, 300));
    // center inspector on screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (dim.width-inspector.getBounds().width)/2;
    int y = (dim.height-inspector.getBounds().height)/2;
    inspector.setLocation(x, y);
    contentPane = new JPanel(new BorderLayout());
    contentPane.setPreferredSize(new Dimension(wInit, hInit));
    frame.setContentPane(contentPane);
    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    String imageFile = "/org/opensourcephysics/resources/tools/images/launch.gif";
    launchIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/launched.gif";
    launchedIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/singleton.gif";
    singletonIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/nofile.gif";
    noFileIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/greenfile.gif";
    greenFileIcon = ResourceLoader.getIcon(imageFile);
    imageFile = "/org/opensourcephysics/resources/tools/images/magentafile.gif";
    magentaFileIcon = ResourceLoader.getIcon(imageFile);
    // create tabbed pane
    tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
    contentPane.add(tabbedPane, BorderLayout.CENTER);
    tabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        previousNode = selectedNode;
        newNodeSelected = true;
        refreshGUI();
      }
    });
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
          popup.show(tabbedPane, e.getX(), e.getY()+8);
        }
      }
    };
    tabbedPane.addMouseListener(tabListener);
    // create the menu bar
    JMenuBar menubar = new JMenuBar();
    fileMenu = new JMenu(LaunchRes.getString("Menu.File"));
    menubar.add(fileMenu);
    openItem = new JMenuItem(LaunchRes.getString("Menu.File.Open"));
    int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    openItem.setAccelerator(KeyStroke.getKeyStroke('O', mask));
    openItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        open();
        refreshGUI();
      }
    });
    closeTabItem = new JMenuItem(LaunchRes.getString("Menu.File.CloseTab"));
    closeTabItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeSelectedTab();
      }
    });
    closeAllItem = new JMenuItem(LaunchRes.getString("Menu.File.CloseAll"));
    closeAllItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeAllTabs();
      }
    });
    editItem = new JMenuItem(LaunchRes.getString("Menu.File.Edit"));
    editItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(previewing) {
          previewing = false;
          exit(); // edit previews by exiting to builder
        } else {
          LaunchSet tabset = new LaunchSet(Launcher.this, tabSetName);
          XMLControl control = new XMLControlElement(tabset);
          LaunchBuilder builder = new LaunchBuilder(control.toXML());
          builder.spawner = Launcher.this;
          builder.tabSetName = tabSetName;
          builder.jarBasePath = jarBasePath;
          builder.frame.setVisible(true);
          builder.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
      }
    });
    JMenu helpMenu = new JMenu(LaunchRes.getString("Menu.Help"));
    menubar.add(helpMenu);
    JMenuItem logItem = new JMenuItem(LaunchRes.getString("Menu.Help.MessageLog"));
    logItem.setAccelerator(KeyStroke.getKeyStroke('L', mask));
    logItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(OSPFrame.applet==null) { // not running as applet so create and postion the log.
          OSPLog log = OSPLog.getOSPLog();
          if(log.getLocation().x==0&&log.getLocation().y==0) {
            Point p = frame.getLocation();
            log.setLocation(p.x+28, p.y+28);
          }
        }
        OSPLog.showLog();
      }
    });
    helpMenu.add(logItem);
    inspectItem = new JMenuItem(LaunchRes.getString("Menu.Help.Inspect"));
    helpMenu.add(inspectItem);
    inspectItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        LaunchSet launchSet = new LaunchSet(Launcher.this, tabSetName);
        launchSet.showHiddenNodes = Launcher.this instanceof LaunchBuilder;
        XMLControl xml = new XMLControlElement(launchSet);
        XMLTreePanel treePanel = new XMLTreePanel(xml, false);
        inspector.setContentPane(treePanel);
        inspector.setTitle(LaunchRes.getString("Inspector.Title.TabSet")+" \""+getDisplayName(tabSetName)+"\"");
        inspector.setVisible(true);
      }
    });
    helpMenu.addSeparator();
    JMenuItem aboutItem = new JMenuItem(LaunchRes.getString("Menu.Help.About"));
    aboutItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showAboutDialog();
      }
    });
    helpMenu.add(aboutItem);
    if(OSPFrame.applet==null) {
      // add window listener to exit
      frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          exit();
        }
      });
      // add exit menu item
      fileMenu.addSeparator();
      exitItem = new JMenuItem(LaunchRes.getString("Menu.File.Exit"));
      exitItem.setAccelerator(KeyStroke.getKeyStroke('Q', mask));
      exitItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          exit();
        }
      });
    }
    frame.setJMenuBar(menubar);
    frame.pack();
  }

  /**
   * Gets the paths of currently open set and tabs.
   *
   * @return the open paths
   */
  protected Set getOpenPaths() {
    openPaths.clear();
    openPaths.add(tabSetName);
    for(int i = 0;i<tabbedPane.getTabCount();i++) {
      LaunchPanel panel = (LaunchPanel) tabbedPane.getComponentAt(i);
      LaunchNode[] nodes = panel.getRootNode().getAllOwnedNodes();
      for(int j = 0;j<nodes.length;j++) {
        openPaths.add(nodes[j].getFileName());
      }
      openPaths.add(panel.getRootNode().getFileName());
    }
    return openPaths;
  }

  /**
   * Shows the about dialog.
   */
  protected void showAboutDialog() {
    String aboutString = "Launcher 1.0  Nov 2005\n"+"Open Source Physics Project\n"+"www.opensourcephysics.org";
    JOptionPane.showMessageDialog(frame, aboutString, LaunchRes.getString("Help.About.Title")+" Launcher", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Determines whether the specified node is launchable.
   *
   * @param node the launch node to verify
   * @return <code>true</code> if the node is launchable
   */
  protected boolean isLaunchable(LaunchNode node) {
    if(node==null||!node.isLeaf()) {
      return false;
    }
    return isLaunchable(node.getLaunchClass());
  }

  /**
   * Determines whether the specified class is launchable.
   *
   * @param type the launch class to verify
   * @return <code>true</code> if the class is launchable
   */
  protected static boolean isLaunchable(Class type) {
    if(type==null) {
      return false;
    }
    try {
      //trow exception if main method does not exist; return value not used
      type.getMethod("main", new Class[] {String[].class});
      return true;
    } catch(NoSuchMethodException ex) {
      return false;
    }
  }

  /**
   * Handles a mouse pressed event.
   *
   * @param e the mouse event
   * @param tab the launch panel receiving the event
   */
  protected void handleMousePressed(MouseEvent e, final LaunchPanel tab) {
    if(e.isPopupTrigger()||e.getButton()==MouseEvent.BUTTON3||(e.isControlDown()&&System.getProperty("os.name", "").indexOf("Mac")>-1)) {
      // make sure node is selected for right-clicks
      TreePath path = tab.tree.getPathForLocation(e.getX(), e.getY());
      if(path==null) {
        return;
      }
      tab.tree.setSelectionPath(path);
      final LaunchNode node = getSelectedNode();
      if(node==null) {
        return;
      }
      // add items to popup menu
      JMenuItem inspectItem = new JMenuItem(LaunchRes.getString("MenuItem.Inspect"));
      inspectItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          inspectXML(node);
        }
      });
      popup.add(inspectItem);
      if(node.getLaunchClass()!=null) {
        if(node.launchCount==0) {
          popup.addSeparator();
          JMenuItem launchItem = new JMenuItem(LaunchRes.getString("MenuItem.Launch"));
          popup.add(launchItem);
          launchItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              node.launch(tab);
            }
          });
        } else {
          popup.addSeparator();
          JMenuItem terminateItem = new JMenuItem(LaunchRes.getString("MenuItem.Terminate"));
          popup.add(terminateItem);
          terminateItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              node.terminateAll();
            }
          });
          if(node.launchCount>1) {
            terminateItem.setText(LaunchRes.getString("MenuItem.TerminateAll"));
          }
          if(!node.isSingleton()) {
            JMenuItem launchItem = new JMenuItem(LaunchRes.getString("MenuItem.Relaunch"));
            popup.add(launchItem);
            launchItem.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                node.launch(tab);
              }
            });
          }
        }
      }
      // show popup when running Launcher (LaunchBuilder)
      if(getClass().equals(Launcher.class)) {
        popup.show(tab, e.getX()+4, e.getY()+12);
      }
    } else if(e.getClickCount()==2&&isLaunchable(getSelectedNode())) {
      LaunchNode node = getSelectedNode();
      if(node.launchCount==0) {
        node.launch(tab);
      } else if(node.isSingleton()||(node.isSingleVM()&&node.isSingleApp())) {
        JOptionPane.showMessageDialog(frame, LaunchRes.getString("Dialog.Singleton.Message")+" \""+node.toString()+"\"", LaunchRes.getString("Dialog.Singleton.Title"), JOptionPane.INFORMATION_MESSAGE);
      } else {
        int selected = JOptionPane.showConfirmDialog(frame, LaunchRes.getString("Dialog.Relaunch.Message"), LaunchRes.getString("Dialog.Relaunch.Title"), JOptionPane.YES_NO_OPTION);
        if(selected==JOptionPane.YES_OPTION) {
          node.launch(tab);
        }
      }
      if(node.launchPanel!=null) {
        node.launchPanel.repaint();
      }
    }
  }

  private void exitCurrentApps() {
    final Frame[] prevFrames = JFrame.getFrames();
    for(int i = 0, n = prevFrames.length;i<n;i++) {
      if(existingFrames.contains(prevFrames[i])) {
        continue; // don't mess with pre-existing frames such as the applet plugin
      }
      if(!(prevFrames[i] instanceof LauncherFrame)) {
        WindowListener[] listeners = prevFrames[i].getWindowListeners();
        for(int j = 0;j<listeners.length;j++) {
          listeners[j].windowClosing(null);
        }
        prevFrames[i].dispose();
      }
    }
  }

  /**
   * Displays a splash screen while loading.
   */
  private void splash() {
    int w = 360;
    int h = 120;
    if(splashDialog==null) {
      Color darkred = new Color(128, 0, 0);
      JPanel splash = new JPanel(new BorderLayout());
      // white background to start
      splash.setBackground(new Color(255, 255, 255));
      splash.setPreferredSize(new Dimension(w, h));
      Border etch = BorderFactory.createEtchedBorder();
      splash.setBorder(etch);
      splashDialog = new JDialog(frame, false);
      splashDialog.setUndecorated(true);
      splashDialog.setContentPane(splash);
      Box labels = Box.createVerticalBox();
      JLabel titleLabel = new JLabel("OSP Launcher");
      if(this instanceof LaunchBuilder) {
        titleLabel.setText("OSP Launch Builder");
      }
      titleLabel.setFont(Font.decode("Arial-BOLD-20"));
      titleLabel.setForeground(darkred);
      titleLabel.setAlignmentX(0.5f);
      titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
      JLabel creditsLabel = new JLabel("By Douglas Brown");
      creditsLabel.setFont(Font.decode("Arial-PLAIN-10"));
      creditsLabel.setHorizontalAlignment(SwingConstants.CENTER);
      creditsLabel.setAlignmentX(0.5f);
      splashPathLabel = new JLabel(" ") {
        public void setText(String s) {
          int max = 80;
          if(s!=null&&s.length()>max) {
            s = s.substring(0, max-4)+"...";
          }
          super.setText(s);
        }
      };
      splashPathLabel.setFont(Font.decode("Arial-PLAIN-12"));
      splashPathLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 10, 2));
      splashPathLabel.setHorizontalAlignment(SwingConstants.CENTER);
      labels.add(Box.createGlue());
      labels.add(titleLabel);
      labels.add(creditsLabel);
      labels.add(Box.createGlue());
      splash.add(labels, BorderLayout.CENTER);
      splash.add(splashPathLabel, BorderLayout.SOUTH);
      splashDialog.pack();
      splashTimer = new javax.swing.Timer(4000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          splashDialog.dispose();
        }
      });
      splashTimer.setRepeats(false);
    }
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = dim.width/2;
    int y = dim.height/2;
    splashDialog.setLocation(x-w/2, y-h/2);
    splashDialog.setVisible(true);
    splashTimer.start();
  }

  /**
   * Exits this application.
   */
  protected void exit() {
    if(!terminateApps()) {
      // change default close operation to prevent window closing
      final int op = frame.getDefaultCloseOperation();
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      // restore default close operation later
      Runnable runner = new Runnable() {
        public void run() {
          frame.setDefaultCloseOperation(op);
        }
      };
      SwingUtilities.invokeLater(runner);
      return;
    }
    // HIDE_ON_CLOSE apps should exit
    if(OSPFrame.applet==null&&frame.getDefaultCloseOperation()==JFrame.HIDE_ON_CLOSE) {
      System.exit(0);
    }
    // all applets and non-HIDE_ON_CLOSE apps should just hide themselves
    else {
      exitCurrentApps(); // close existing programs
      frame.setVisible(false);
    }
  }

  /**
   * Terminates running apps.
   *
   * @return false if process is cancelled by the user
   */
  protected boolean terminateApps() {
    if(frame.getDefaultCloseOperation()==JFrame.HIDE_ON_CLOSE) {
      // ask to terminate apps running in this process
      boolean approved = false;
      Frame[] frames = JFrame.getFrames();
      for(int i = 0, n = frames.length;i<n;i++) {
        if(!approved&&frames[i].isVisible()&&!(frames[i] instanceof LauncherFrame)&&!(frames[i] instanceof OSPLog)) {
          if(existingFrames.contains(frames[i])) {
            continue; // don't mess with pre-exisitng frames such as the applet plugin
          }
          int selected = JOptionPane.showConfirmDialog(frame, LaunchRes.getString("Dialog.Terminate.Message")+XML.NEW_LINE+LaunchRes.getString("Dialog.Terminate.Question"), LaunchRes.getString("Dialog.Terminate.Title"), JOptionPane.YES_NO_OPTION);
          if(selected==JOptionPane.YES_OPTION) {
            approved = true;
          } else {
            return false;
          }
        }
      }
      // ask to terminate apps running in separate processes
      approved = false;
      boolean declined = false;
      // look for nodes with running processes
      Component[] comps = tabbedPane.getComponents();
      for(int i = 0;i<comps.length;i++) {
        LaunchPanel tab = (LaunchPanel) comps[i];
        Enumeration e = tab.getRootNode().breadthFirstEnumeration();
        while(e.hasMoreElements()) {
          LaunchNode node = (LaunchNode) e.nextElement();
          if(!node.processes.isEmpty()) {
            if(!approved&&!declined) { // ask for approval
              int selected = JOptionPane.showConfirmDialog(frame, LaunchRes.getString("Dialog.TerminateSeparateVM.Message")+XML.NEW_LINE+LaunchRes.getString("Dialog.TerminateSeparateVM.Question"), LaunchRes.getString("Dialog.TerminateSeparateVM.Title"), JOptionPane.YES_NO_OPTION);
              approved = (selected==JOptionPane.YES_OPTION);
              declined = !approved;
            }
            if(approved) { // terminate processes
              for(Iterator it = node.processes.iterator();it.hasNext();) {
                Process proc = (Process) it.next();
                it.remove();
                proc.destroy();
              }
            } else {
              return false;
            }
          }
        }
      }
    }
    return true;
  }

  /**
   * A unique frame class for Launcher. This is used to differentiate Launcher
   * from other apps when disposing of frames in singleApp mode.
   */
  private class LauncherFrame extends OSPFrame {
    LaunchNode node;

    public LauncherFrame() {
      setName("LauncherTool");
    }

    public LaunchNode getLaunchNode() {
      return node;
    }
  }

  /**
   * A cell renderer class to show launchable nodes.
   */
  private class LaunchRenderer extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      setToolTipText(null);
      LaunchNode node = (LaunchNode) value;
      if(node.getFileName()!=null&&Launcher.this instanceof LaunchBuilder) {
        setToolTipText(LaunchRes.getString("ToolTip.FileName")+" \""+node.getFileName()+"\"");
        setIcon(getFileIcon(node));
      } else if(node.launchCount>0) {
        if(node.isSingleton()) {
          setIcon(singletonIcon);
        } else if(node.isSingleVM()&&node.isSingleApp()) {
          setIcon(singletonIcon);
        } else {
          setIcon(launchedIcon);
        }
      } else if(isLaunchable(node)) {
        setIcon(launchIcon);
      } else if(node.isLeaf()) {
        setIcon(noFileIcon);
      }
      return this;
    }
  }

  /**
   * Gets an appropriate File icon for a node (must have non-null filename)
   *
   * @param node the launch node
   * @return the icon
   */
  protected Icon getFileIcon(LaunchNode node) {
    if(node.getFileName().length()==0) {
      return null;
    }
    File file = node.getFile();
    Resource res = node.getResource();
    boolean changed = changedFiles.contains(node.getFileName());
    // yellow file icon if changed
    if(changed) {
      return yellowFileIcon;
      // ghost file icon if resource is null or parent self contained
    } else if(res==null||node.isParentSelfContained()) {
      return ghostFileIcon;
      // white file icon if has writable file
    } else if(file!=null&&file.canWrite()) {
      return whiteFileIcon;
      // magenta icon if opened from jar or zip file
    } else if(file==null) {
      return magentaFileIcon;
    }
    // red icon if opened from read-only file
    return redFileIcon;
  }

  /**
   * A class to save and load a set of launch tabs and Launcher static fields.
   */
  protected class LaunchSet implements XML.ObjectLoader {
    Launcher launcher;
    String name;
    boolean showHiddenNodes = true;

    private LaunchSet() {}

    protected LaunchSet(Launcher launcher, String path) {
      this.launcher = launcher;
      name = XML.getName(XML.forwardSlash(path));
    }

    public void saveObject(XMLControl control, Object obj) {
      LaunchSet tabset = (LaunchSet) obj;
      Launcher launcher = tabset.launcher;
      control.setValue("classpath", classPath);
      control.setValue("title", launcher.title);
      if(launcher.editorEnabled) {
        control.setValue("editor_enabled", true);
      }
      // save dimensions and split pane divider location
      Dimension dim = launcher.contentPane.getSize();
      control.setValue("width", dim.width);
      control.setValue("height", dim.height);
      control.setValue("divider", launcher.divider);
      // save collection of tab root nodes
      Collection nodes = new ArrayList();
      for(int i = 0;i<launcher.tabs.size();i++) {
        LaunchNode root = ((LaunchPanel) launcher.tabs.get(i)).getRootNode();
        if(root.isHiddenInLauncher()&&!tabset.showHiddenNodes) {
          continue;
        }
        // initialize selfContained, previewing and saveHiddenNodes properties
        root.parentSelfContained = false;
        root.previewing = false;
        root.saveHiddenNodes = tabset.showHiddenNodes;
        if(launcher.selfContained) {
          // save tab root
          root.setSelfContained(false);
          root.parentSelfContained = true;
          nodes.add(root);
        } else if(launcher.previewing) {
          // preview tab root
          root.previewing = true;
          nodes.add(root);
        } else if(root.getFileName()==null||root.getFileName().equals("")) {
          // save tab root
          nodes.add(root);
        } else {
          // save tab root filename
          nodes.add(root.getFileName());
        }
      }
      control.setValue("launch_nodes", nodes);
    }

    public Object createObject(XMLControl control) {
      return launcher;
    }

    public Object loadObject(XMLControl control, Object obj) {
      LaunchSet tabset = (LaunchSet) obj;
      Launcher launcher = tabset.launcher;
      // load a different launch set
      if(control.getPropertyNames().contains("launchset")) {
        launcher.open(control.getString("launchset"));
        return obj;
      }
      // load static properties
      if(control.getPropertyNames().contains("classpath")) {
        classPath = control.getString("classpath");
      }
      // load launch nodes
      Collection nodes = (Collection) control.getObject("launch_nodes");
      if(nodes!=null&&!nodes.isEmpty()) {
        int i = launcher.tabbedPane.getSelectedIndex();
        Iterator it = nodes.iterator();
        boolean tabAdded = false;
        while(it.hasNext()) {
          Object next = it.next();
          // prevent circular references
          if(tabset.name!=null&&tabset.name.equals(next)) {
            continue;
          }
          if(next instanceof String) {
            String path = XML.getResolvedPath((String) next, tabSetBasePath);
            tabAdded = (launcher.open(path)!=null)||tabAdded;
          } else if(next instanceof LaunchNode) {
            // add the child node
            LaunchNode node = (LaunchNode) next;
            tabAdded = launcher.addTab(node);
          }
        }
        // select the first added tab
        if(tabAdded) {
          launcher.tabbedPane.setSelectedIndex(i+1);
        }
      }
      launcher.editorEnabled = control.getBoolean("editor_enabled");
      launcher.title = control.getString("title");
      // load dimensions
      if(control.getPropertyNames().contains("width")&&control.getPropertyNames().contains("height")) {
        int w = control.getInt("width");
        int h = control.getInt("height");
        launcher.contentPane.setPreferredSize(new Dimension(w, h));
        launcher.frame.pack();
      }
      // load divider position
      if(control.getPropertyNames().contains("divider")) {
        launcher.divider = control.getInt("divider");
        launcher.refreshGUI();
      }
      return obj;
    }
  }

  // ________________________________ static methods _____________________________

  /**
   * Launches an application with no arguments.
   *
   * @param type the class to be launched
   */
  public static void launch(Class type) {
    launch(type, null, null);
  }

  /**
   * Launches an application with an array of string arguments.
   *
   * @param type the class to be launched
   * @param args the String array of arguments
   */
  public static void launch(Class type, String[] args) {
    launch(type, args, null);
  }

  /**
   * Launches an application asociated with a launch node.
   *
   * @param type the class to be launched
   * @param args the argument array (may be null)
   * @param node the launch node (may be null)
   */
  public static void launch(final Class type, String[] args, final LaunchNode node) {
    if(type==null) {
      OSPLog.info(LaunchRes.getString("Log.Message.NoClass"));
      JOptionPane.showMessageDialog(null, LaunchRes.getString("Dialog.NoLaunchClass.Message"), LaunchRes.getString("Dialog.NoLaunchClass.Title"), JOptionPane.WARNING_MESSAGE);
      return;
    }
    String desc = LaunchRes.getString("Log.Message.Launching")+" "+type+", args ";
    if(args==null) {
      desc += args;
    } else {
      desc += "{";
      for(int i = 0;i<args.length;i++) {
        desc += args[i];
        if(i<args.length-1) {
          desc += ", ";
        }
      }
      desc += "}";
    }
    OSPLog.fine(desc);
    // launch the app in single vm mode
    if(OSPParameters.launchingInSingleVM||!newVMAllowed) {
      OSPParameters.launchingInSingleVM = true;
      OSPLog.finer(LaunchRes.getString("Log.Message.LaunchCurrentVM"));
      // get array of frames before launching
      final Frame[] prevFrames = JFrame.getFrames();
      // dispose of previous frames if single app mode
      if(singleAppMode) {
        OSPLog.finer(LaunchRes.getString("Log.Message.LaunchSingleApp"));
        boolean vis = OSPLog.isLogVisible();
        for(int i = 0, n = prevFrames.length;i<n;i++) {
          if(existingFrames.contains(prevFrames[i])) {
            continue; // don't mess with pre-exisitng frames such as the applet plugin
          }
          if(!(prevFrames[i] instanceof LauncherFrame)) {
            WindowListener[] listeners = prevFrames[i].getWindowListeners();
            for(int j = 0;j<listeners.length;j++) {
              listeners[j].windowClosing(null);
            }
            prevFrames[i].dispose();
          }
        }
        if(vis) {
          OSPLog.showLog();
        }
      }
      // set xml class loader
      if(node!=null) {
        String classPath = node.getClassPath();
        XML.setClassLoader(LaunchClassChooser.getClassLoader(classPath));
      }
      // launch in singleVM by invoking main method from separate daemon thread
      final String[] arg = args;
      final Runnable launchRunner = new Runnable() {
        public void run() {
          activeNode = node;
          try {
            Method m = type.getMethod("main", new Class[] {String[].class});
            m.invoke(type, new Object[] {arg}); // may not return!
          } catch(NoSuchMethodException ex) {}
          catch(InvocationTargetException ex) {}
          catch(IllegalAccessException ex) {}
          // main method returned, so remove launchThread from node map
          node.threads.remove(this);
          activeNode = null;
          // find frames associated with launched app and store in node
          if(frameFinder!=null) {
            findFramesFor(node, prevFrames, this);
          }
        }
      };
      // create timer to look for new frames in case main method doesn't return
      if(frameFinder!=null) {
        frameFinder.stop();
      }
      frameFinder = new javax.swing.Timer(1000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          findFramesFor(node, prevFrames, launchRunner); // stops timer if finds frames
        }
      });
      frameFinder.start();
      Thread launchThread = new Thread(launchRunner);
      launchThread.setDaemon(true);
      node.threads.put(launchRunner, launchThread);
      launchThread.start();
      return;
    }
    // launch the app in a separate java process
    OSPLog.finer(LaunchRes.getString("Log.Message.LaunchSeparateVM"));
    // construct the command to execute
    final Vector cmd = new Vector();
    cmd.add("java");
    if(classPath!=null&&!classPath.equals("")) {
      String jar = getDefaultJar();
      if(jar!=null&&classPath.indexOf(jar)==-1) {
        classPath += ";"+jar;
      }
      // convert any colons to semicolons
      int i = classPath.indexOf(":");
      while(i!=-1) {
        classPath = classPath.substring(0, i)+";"+classPath.substring(i+1);
        i = classPath.indexOf(":");
      }
      // replace semicolons with platform-dependent path separator
      char pathSeparator = System.getProperty("path.separator").charAt(0);
      classPath = classPath.replace(';', pathSeparator);
      cmd.add("-classpath");
      cmd.add(classPath);
    }
    cmd.add(type.getName());
    if(args!=null) {
      for(int i = 0;i<args.length&&args[i]!=null;i++) {
        cmd.add(args[i]);
      }
    }
    // create a launch thread for separate VM
    Runnable launchRunner = new Runnable() {
      public void run() {
        OSPLog.finer(LaunchRes.getString("Log.Message.Command")+" "+cmd.toString());
        String[] cmdarray = (String[]) cmd.toArray(new String[0]);
        try {
          Process proc = Runtime.getRuntime().exec(cmdarray);
          if(node!=null) {
            node.processes.add(proc);
          }
          proc.waitFor();
          if(node!=null) {
            node.threadRunning(false);
            node.processes.remove(proc);
          }
        } catch(Exception ex) {
          OSPLog.info(ex.toString());
          if(node!=null) {
            node.threadRunning(false);
          }
        }
      }
    };
    if(node!=null) {
      node.threadRunning(true);
    }
    new Thread(launchRunner).start();
  }

  /**
   * Main entry point when used as application.
   *
   * @param args args[0] may be an xml file name
   */
  public static void main(String[] args) {
    OSPLog.setLevel(ConsoleLevel.OUT_CONSOLE);
    Launcher launcher = new Launcher();
    if(args!=null&&args.length>0) {
      for(int i = 0;i<args.length;i++) {
        launcher.open(args[i]);
      }
    }
    else {
      String path = null;
      // look for default file with launchJarName or defaultFileName
      if (ResourceLoader.launchJarName != null) {
        path = launcher.open(
            XML.stripExtension(ResourceLoader.launchJarName)+".xset");
      }
      if(path==null) {
        path = launcher.open(defaultFileName+".xset");
      }
      if(path==null) {
        path = launcher.open(defaultFileName+".xml");
      }
    }
    launcher.refreshGUI();
    // center frame on the screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (dim.width-launcher.frame.getBounds().width)/2;
    int y = (dim.height-launcher.frame.getBounds().height)/2;
    launcher.frame.setLocation(x, y);
    ((LauncherFrame) launcher.frame).setKeepHidden(false);
    launcher.frame.setVisible(true);
  }

  /**
   * Gets a class chooser for selecting launchable classes from jar files.
   *
   * @return the jar class chooser
   */
  protected LaunchClassChooser getClassChooser() {
    if(classChooser==null) {
      classChooser = new LaunchClassChooser(contentPane);
    }
    return classChooser;
  }

  /**
   * Gets a file chooser for selecting xml files.
   *
   * @return the xml chooser
   */
  protected static JFileChooser getXMLChooser() {
    if(chooser!=null) {
      return chooser;
    }
    chooser = new JFileChooser(new File(OSPFrame.chooserDir));
    // add xml file filters
    launcherFileFilter = new FileFilter() {
      // accept all directories, *.xml, *.xset and zip files.
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
        if(extension!=null&&(extension.equals("xset")||extension.equals("xml")||extension.equals("zip"))) {
          return true;
        }
        return false;
      }
      // the description of this filter
      public String getDescription() {
        return LaunchRes.getString("FileChooser.LauncherFilter.Description");
      }
    };
    xmlFileFilter = new FileFilter() {
      // accept all directories and *.xml files.
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
        if(extension!=null&&extension.equals("xml")) {
          return true;
        }
        return false;
      }
      // the description of this filter
      public String getDescription() {
        return LaunchRes.getString("FileChooser.XMLFilter.Description");
      }
    };
    xsetFileFilter = new FileFilter() {
      // accept all directories and *.xset files.
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
        if(extension!=null&&extension.equals("xset")) {
          return true;
        }
        return false;
      }
      // the description of this filter
      public String getDescription() {
        return LaunchRes.getString("FileChooser.XSETFilter.Description");
      }
    };
    chooser.addChoosableFileFilter(xmlFileFilter);
    chooser.addChoosableFileFilter(xsetFileFilter);
    chooser.addChoosableFileFilter(launcherFileFilter);
    return chooser;
  }

  /**
   * Gets the display name of the specified file name.
   *
   * @param fileName the file name
   * @return the bare name without path or extension
   */
  protected static String getDisplayName(String fileName) {
    fileName = XML.getName(fileName);
    int i = fileName.lastIndexOf(".");
    if(i!=-1) {
      return fileName.substring(0, i);
    }
    return fileName;
  }

  /**
   * Gets the name of the jar containing the default launcher xml file, if any.
   *
   * @return the jar name
   */
  protected static String getDefaultJar() {
    URL url = ClassLoader.getSystemResource(defaultFileName+".xset");
    if(url==null) {
      url = ClassLoader.getSystemResource(defaultFileName+".xml");
    }
    if(url==null) {
      return null;
    }
    String path = url.getPath();
    // trim trailing slash and file name
    int i = path.indexOf("/"+defaultFileName);
    if(i==-1) {
      return null;
    }
    path = path.substring(0, i);
    // jar name is followed by "!"
    i = path.lastIndexOf("!");
    if(i==-1) {
      return null;
    }
    // trim and return jar name
    return path.substring(path.lastIndexOf("/")+1, i);
  }

  /**
   * Displays the properties of the specified node in an xml inspector.
   *
   * @param node the launch node
   */
  private void inspectXML(LaunchNode node) {
    XMLControl xml = new XMLControlElement(node);
    XMLTreePanel treePanel = new XMLTreePanel(xml, false);
    inspector.setContentPane(treePanel);
    inspector.setTitle(LaunchRes.getString("Inspector.Title.Node")+" \""+node.name+"\"");
    inspector.setVisible(true);
  }

  private static void findFramesFor(final LaunchNode node, Frame[] prevFrames, final Runnable runner) {
    // get current frames
    Frame[] frames = JFrame.getFrames();
    // make newFrames collection and throw out previous frames
    final Collection newFrames = new ArrayList();
    for(int i = 0;i<frames.length;i++) {
      newFrames.add(frames[i]);
    }
    for(int i = 0;i<prevFrames.length;i++) {
      newFrames.remove(prevFrames[i]);
    }
    // return if no new frames found
    if(newFrames.isEmpty()) {
      return;
    }
    // look thru new frames for "control" frame
    frames = (Frame[]) newFrames.toArray(new Frame[0]);
    newFrames.clear();
    for(int i = 0, n = frames.length;i<n;i++) {
      if(frames[i] instanceof JFrame&&!(frames[i] instanceof LauncherFrame)) { // found new frame
        JFrame frame = (JFrame) frames[i];
        if((frame instanceof OSPFrame&&((OSPFrame) frame).wishesToExit())||frame.getDefaultCloseOperation()==JFrame.EXIT_ON_CLOSE) { // found control frame
          if(frame.getDefaultCloseOperation()==JFrame.EXIT_ON_CLOSE) {
            // change default close operation from EXIT to DISPOSE
            // (otherwise exiting a launched app also exits Launcher)
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
          }
          // add window listener so control frames can close associated windows
          frame.addWindowListener(new FrameCloser(node, newFrames, runner));
        }
        newFrames.add(frame);
      }
    }
    // add frames to node
    if(node!=null) {
      node.frames.addAll(newFrames);
      node.launchCount++;
      // turn off and release timer
      if(frameFinder!=null) {
        frameFinder.stop();
        frameFinder = null;
      }
    }
    // repaint
    if(node.launchPanel!=null) {
      node.launchPanel.repaint();
    }
  }

  // a class to refresh a node when a launched frame closes
  static class FrameCloser extends WindowAdapter {
    LaunchNode node;
    Collection frames;
    Runnable runner;

    FrameCloser(LaunchNode node, Collection newFrames, Runnable runner) {
      frames = newFrames;
      this.node = node;
      this.runner = runner;
    }

    public void windowClosing(WindowEvent e) {
      Iterator it = frames.iterator();
      while(it.hasNext()) {
        Frame frame = (Frame) it.next();
        frame.removeWindowListener(this);
        frame.dispose();
      }
      if(node!=null) {
        Thread thread = (Thread) node.threads.get(runner);
        if(thread!=null) {
          thread.interrupt();
          node.threads.put(runner, null);
        }
        node.frames.removeAll(frames);
        node.launchCount = Math.max(0, --node.launchCount);
        if(node.launchPanel!=null) {
          node.launchPanel.repaint();
        }
      }
    }
  }
}

/**
 * String resources for launcher classes.
 */
class LaunchRes {
  // static fields
  static ResourceBundle res = ResourceBundle.getBundle("org.opensourcephysics.resources.tools.launcher");

  /**
   * Private constructor to prevent instantiation.
   */
  private LaunchRes() {}

  /**
   * Gets the localized value of a string. If no localized value is found, the
   * key is returned surrounded by exclamation points.
   *
   * @param key the string to localize
   * @return the localized string
   */
  static String getString(String key) {
    try {
      return res.getString(key);
    } catch(MissingResourceException ex) {
      return "!"+key+"!";
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
