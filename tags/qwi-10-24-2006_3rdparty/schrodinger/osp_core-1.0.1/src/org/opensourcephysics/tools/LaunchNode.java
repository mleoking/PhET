/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import org.opensourcephysics.controls.*;
import java.awt.Frame;

/**
 * This is a tree node that can describe and launch an application.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class LaunchNode extends DefaultMutableTreeNode {
  // static fields
  static final Level DEFAULT_LOG_LEVEL = ConsoleLevel.OUT_CONSOLE;

  // instance fields
  Object launchObj;                  // object providing xml data when arg 0 is "this"
  String classPath;                  // relative paths to jar files with launch/support classes
  String launchClassName;            // name of class to be launched
  Class launchClass;                 // class to be launched
  String[] args = new String[] {""}; // args passed to main method when launching
  boolean showLog = false;            // shows OSPLog when in single vm
  boolean clearLog = false;           // clears OSPLog if shown
  Level logLevel = DEFAULT_LOG_LEVEL; // OSPLog set to this level before log is shown
  boolean singleVM = false;         // launches in current vm
  boolean hiddenWhenRoot = false;   // hides node when at root position
  boolean singleton = false;        // allows single instance when in separate vm
  boolean singleApp = false;        // allows single app when in single vm
  boolean webStart = false;         // true when under web start control
  boolean hiddenInLauncher = false; // hides node in Launcher (not LaunchBuilder)
  String name = "";        // display name of this node
  String description = ""; // description displayed if no url specified or found
  String author = "";                   // author of the curricular activity defined by this node
  String codeAuthor = "";               // author of the program used by this node
  String comment = "";                  // comments about this node
  String urlName;                       // relative path of url specifying html page to be displayed
  URL url;                              // url specifying html page to be displayed
  private String fileName;              // xml file name relative to tabset base
  ClassLoader classLoader;              // loads classes from jar files
  Collection processes = new HashSet(); // processes launched by this node
  Collection frames = new HashSet();    // frames launched by this node
  Collection actions = new HashSet();   // terminateActions to be performed by this node
  Map threads = new HashMap();
  int launchCount = 0;
  LaunchPanel launchPanel;
  boolean selfContained;
  boolean parentSelfContained;
  boolean previewing;
  boolean saveHiddenNodes;
  List jars = new ArrayList();

  /**
   * Contructs a node with the specified name.
   *
   * @param name the name
   */
  public LaunchNode(String name) {
    setUserObject(this);
    if(name!=null) {
      this.name = name;
    }
    // try {
    // BasicService bs = (BasicService) ServiceManager.lookup(
    // "javax.jnlp.BasicService");
    // webStart = true;
    // }
    // catch (UnavailableServiceException ex) {}
  }

  /**
   * Signals that a launch thread for this node is about to start or end.
   *
   * @param starting true if the thread is starting
   */
  public void threadRunning(boolean starting) {
    launchCount += starting ? +1 : -1;
    launchCount = Math.max(0, launchCount);
    if(launchPanel!=null) {
      launchPanel.repaint();
    }
  }

  /**
   * Launches this node.
   */
  public void launch() {
    launch(null);
  }

  /**
   * Launches this node from the specified launch panel.
   *
   * @param tab the launch panel
   */
  public void launch(LaunchPanel tab) {
    if(!isLeaf()) {
      return;
    }
    launchPanel = tab;
    OSPParameters.launchingInSingleVM = this.isSingleVM();
    Launcher.singleAppMode = this.isSingleApp();
    Launcher.classPath = getClassPath(); // in node-to-root order
    if(isShowLog()&&isSingleVM()) {
      OSPLog.setLevel(getLogLevel());
      OSPLog log = OSPLog.getOSPLog();
      if(isClearLog()) {
        log.clear();
      }
      log.setVisible(true);
    }
    setMinimumArgLength(1); // trim args if nec
    String arg0 = args[0];
    if(getLaunchClass()!=null) {
      if(arg0.equals("this")) {
        Object launchObj = getLaunchObject();
        if(launchObj!=null) {
          // replace with xml from launch object, if any
          XMLControl control = new XMLControlElement(launchObj);
          args[0] = control.toXML();
        } else {
          args[0] = "";
        }
      }
      if(args[0].equals("")&&args.length==1) {
        Launcher.launch(getLaunchClass(), null, this);
      } else {
        Launcher.launch(getLaunchClass(), args, this);
      }
    }
    args[0] = arg0;
  }

  /**
   * Returns the nearest ancestor with a non-null file name.
   * This node is returned if it has a non-null file name.
   *
   * @return the file node
   */
  public LaunchNode getOwner() {
    if(fileName!=null) {
      return this;
    }
    if(getParent()!=null) {
      return((LaunchNode) getParent()).getOwner();
    }
    return null;
  }

  /**
   * Returns all descendents of this node with non-null file names.
   * This node is not included.
   *
   * @return an array of launch nodes
   */
  public LaunchNode[] getAllOwnedNodes() {
    Collection nodes = new ArrayList();
    Enumeration e = breadthFirstEnumeration();
    while(e.hasMoreElements()) {
      LaunchNode next = (LaunchNode) e.nextElement();
      if(next.fileName!=null&&next!=this) {
        nodes.add(next);
      }
    }
    return(LaunchNode[]) nodes.toArray(new LaunchNode[0]);
  }

  /**
   * Returns the nearest descendents of this node with non-null file names.
   * This node is not included.
   *
   * @return an array of launch nodes
   */
  public LaunchNode[] getChildOwnedNodes() {
    Collection nodes = new ArrayList();
    LaunchNode[] owned = getAllOwnedNodes();
    LaunchNode owner = getOwner();
    for(int i = 0;i<owned.length;i++) {
      LaunchNode next = ((LaunchNode) owned[i].getParent()).getOwner();
      if(next==owner) {
        nodes.add(owned[i]);
      }
    }
    return(LaunchNode[]) nodes.toArray(new LaunchNode[0]);
  }

  /**
   * Returns a string used as a display name for this node.
   *
   * @return the string name of this node
   */
  public String toString() {
    // return name, if any
    if(name!=null&&!name.equals("")) {
      return name;
    }
    // return launch class name, if any
    if(launchClassName!=null) {
      return XML.getExtension(launchClassName);
    }
    // return args[0] name, if any
    if(!args[0].equals("")) {
      String name = args[0];
      name = XML.getName(name);
      return XML.stripExtension(name);
    }
    return "";
  }

  /**
   * Gets the unique ID string for this node.
   *
   * @return the ID string
   */
  public String getID() {
    return String.valueOf(this.hashCode());
  }

  /**
   * Sets the name of this node.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the description of this node.
   *
   * @param desc the description
   */
  public void setDescription(String desc) {
    this.description = desc;
  }

  /**
   * Sets the launch arguments of this node.
   *
   * @param args the arguments
   */
  public void setArgs(String[] args) {
    if(args!=null&&args.length>0&&args[0]!=null) {
      this.args = args;
    }
  }

  /**
   * Gets the complete class path in node-to-root order.
   * If Launcher is running from a jar, that jar is in every classpath.
   *
   * @return the class path
   */
  public String getClassPath() {
    String path = "";
    if(classPath!=null) {
      path += classPath;
    }
    LaunchNode node = this;
    while(!node.isRoot()) {
      node = (LaunchNode) node.getParent();
      if(node.classPath!=null) {
        if(!path.equals("")) {
          path += ";";
        }
        path += node.classPath;
      }
    }
    if(!path.equals("")) {
      // eliminate duplicate jars
      jars.clear();
      String next = path;
      int i = path.indexOf(";");
      if(i==-1) {
        i = path.indexOf(":");
      }
      if(i!=-1) {
        next = path.substring(0, i);
        path = path.substring(i+1);
      } else {
        path = "";
      }
      // iterate thru path and add each unique jar to jars list
      while(!next.equals("")) {
        next = next.trim();
        if(!jars.contains(next)) {
          jars.add(next);
        }
        i = path.indexOf(";");
        if(i==-1) {
          i = path.indexOf(":");
        }
        if(i==-1) {
          next = path;
          path = "";
        } else {
          next = path.substring(0, i);
          path = path.substring(i+1);
        }
      }
      // reconstruct clean path using semicolon delimiters
      Iterator it = jars.iterator();
      while(it.hasNext()) {
        if(!path.equals("")) {
          path += ";";
        }
        path += it.next();
      }
    }
    // add ResourceLoader.launchJarName, if any
    if(ResourceLoader.launchJarName!=null&&path.indexOf(ResourceLoader.launchJarName)==-1) {
      if(!path.equals("")) {
        path += ";";
      }
      path += ResourceLoader.launchJarName;
    }
    return path;
  }

  /**
   * Sets the class path (jar file names separated by colons or semicolons).
   *
   * @param jarNames the class path
   */
  public void setClassPath(String jarNames) {
    if(jarNames==null||jarNames.equals("")) {
      classPath = null;
      return;
    }
    // eliminate extra colons and semicolons
    while(jarNames.startsWith(":")||jarNames.startsWith(";")) {
      jarNames = jarNames.substring(1);
    }
    while(jarNames.endsWith(":")||jarNames.endsWith(";")) {
      jarNames = jarNames.substring(0, jarNames.length()-1);
    }
    String s = jarNames;
    int i = jarNames.indexOf(";;");
    if(i==-1) {
      i = jarNames.indexOf("::");
    }
    if(i==-1) {
      i = jarNames.indexOf(":;");
    }
    if(i==-1) {
      i = jarNames.indexOf(";:");
    }
    while(i>-1) {
      jarNames = jarNames.substring(0, i+1)+s.substring(i+2);
      s = jarNames;
      i = jarNames.indexOf(";;");
      if(i==-1) {
        i = jarNames.indexOf("::");
      }
      if(i==-1) {
        i = jarNames.indexOf(":;");
      }
      if(i==-1) {
        i = jarNames.indexOf(";:");
      }
    }
    classPath = jarNames;
  }

  /**
   * Sets the launch class for this node.
   *
   * @param className the name of the class
   * @return true if the class was successfully loaded for the first time
   */
  public boolean setLaunchClass(String className) {
    if(className==null) {
      return false;
    }
    if(launchClassName==className&&launchClass!=null) {
      return false;
    }
    OSPLog.finest(LaunchRes.getString("Log.Message.SetLaunchClass")+" "+className);
    launchClassName = className;
    launchClass = LaunchClassChooser.getClass(getClassPath(), className);
    return launchClass!=null;
  }

  /**
   * Gets the launch class for this node.
   *
   * @return the launch class
   */
  public Class getLaunchClass() {
    if(launchClass==null&&launchClassName!=null&&!launchClassName.equals("")) {
      setLaunchClass(launchClassName);
    }
    return launchClass;
  }

  /**
   * Gets the launch object. May be null.
   *
   * @return the launch object
   */
  public Object getLaunchObject() {
    if(launchObj!=null) {
      return launchObj;
    } else if(isRoot()) {
      return null;
    }
    LaunchNode node = (LaunchNode) getParent();
    return node.getLaunchObject();
  }

  /**
   * Sets the launch object.
   *
   * @param obj the launch object
   */
  public void setLaunchObject(Object obj) {
    launchObj = obj;
  }

  /**
   * Sets the url.
   *
   * @param path the path of the url
   * @return the url
   */
  public URL setURL(String path) {
    if(path==null||path.equals("")) {
      urlName = null;
      url = null;
      return null;
    }
    urlName = path;
    OSPLog.finest(LaunchRes.getString("Log.Message.SetURLPath")+" "+path);
    Resource res = ResourceLoader.getResource(path);
    if(res!=null&&res.getURL()!=null) {
      try {
        return setURL(res.getURL());
      } catch(Exception ex) {
        OSPLog.info(LaunchRes.getString("Log.Message.NotFound")+" "+path);
      }
    }
    url = null;
    return null;
  }

  /**
   * Gets the fileName.
   *
   * @return the fileName, assumed to be a relative path
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Sets the fileName. Accepts relative paths or
   * will convert absolute paths to relative.
   *
   * @param path the path to the file
   * @return the file name
   */
  public String setFileName(String path) {
    if(path==null) {
      fileName = null;
    } else {
      fileName = XML.getPathRelativeTo(path, Launcher.tabSetBasePath);
    }
    return fileName;
  }

  /**
   * Gets the parentSelfContained flag.
   *
   * @return true if parentSelfContained is true for this or an ancestor
   */
  public boolean isParentSelfContained() {
    if(parentSelfContained) {
      return true;
    } else if(isRoot()) {
      return false;
    }
    LaunchNode parent = (LaunchNode) getParent();
    return parent.isSelfContained();
  }

  /**
   * Gets the selfContained flag.
   *
   * @return true if selfContained is true for this or an ancestor
   */
  public boolean isSelfContained() {
    if(selfContained||isParentSelfContained()) {
      return true;
    }
    return false;
  }

  /**
   * Gets the previewing flag.
   *
   * @return true if previewing is true for this or an ancestor
   */
  public boolean isPreviewing() {
    if(previewing) {
      return true;
    } else if(isRoot()) {
      return false;
    }
    LaunchNode node = (LaunchNode) getParent();
    return node.isPreviewing();
  }

  /**
   * Gets the saveHiddenNodes flag.
   *
   * @return true if saveHiddenNodes is true for this or an ancestor
   */
  public boolean isSavingHiddenNodes() {
    if(saveHiddenNodes) {
      return true;
    } else if(isRoot()) {
      return false;
    }
    LaunchNode node = (LaunchNode) getParent();
    return node.isSavingHiddenNodes();
  }

  /**
   * Sets the selfContained flag.
   *
   * @param selfContained true if self contained
   */
  public void setSelfContained(boolean selfContained) {
    this.selfContained = selfContained;
  }

  /**
   * Gets the singleVM flag.
   *
   * @return true if singleVM is true for this or an ancestor
   */
  public boolean isSingleVM() {
    if(singleVM||webStart) {
      return true;
    } else if(isRoot()) {
      return false;
    }
    LaunchNode node = (LaunchNode) getParent();
    return node.isSingleVM();
  }

  /**
   * Sets the single VM flag.
   *
   * @param singleVM true if single vm
   */
  public void setSingleVM(boolean singleVM) {
    this.singleVM = singleVM;
  }

  /**
   * Gets the showLog value.
   *
   * @return true if showLog is true for this or an ancestor
   */
  public boolean isShowLog() {
    if(showLog) {
      return true;
    } else if(isRoot()) {
      return false;
    }
    LaunchNode parent = (LaunchNode) getParent();
    return parent.isShowLog();
  }

  /**
   * Sets the showLog flag.
   *
   * @param show true to show the OSPLog (single vm only)
   */
  public void setShowLog(boolean show) {
    showLog = show;
  }

  /**
   * Gets the clearLog value.
   *
   * @return true if clearLog is true for this or an ancestor
   */
  public boolean isClearLog() {
    if(clearLog) {
      return true;
    } else if(isRoot()) {
      return false;
    }
    LaunchNode parent = (LaunchNode) getParent();
    return parent.isClearLog();
  }

  /**
   * Sets the showLog flag.
   *
   * @param clear true to clear the OSPLog (single vm only)
   */
  public void setClearLog(boolean clear) {
    clearLog = clear;
  }

  /**
   * Gets the log level.
   *
   * @return the level
   */
  public Level getLogLevel() {
    if(isRoot()) {
      return logLevel;
    }
    LaunchNode parent = (LaunchNode) getParent();
    if(parent.isShowLog()) {
      Level parentLevel = parent.getLogLevel();
      if(parentLevel.intValue()<logLevel.intValue()) {
        return parentLevel;
      }
    }
    return logLevel;
  }

  /**
   * Sets the log level.
   *
   * @param level the level
   */
  public void setLogLevel(Level level) {
    if(level!=null) {
      logLevel = level;
    }
  }

  /**
   * Gets the singleApp value.
   *
   * @return true if singleApp is true for this or an ancestor
   */
  public boolean isSingleApp() {
    if(singleApp) {
      return true;
    } else if(isRoot()) {
      return false;
    }
    LaunchNode parent = (LaunchNode) getParent();
    return parent.isSingleApp();
  }

  /**
   * Sets the singleApp flag.
   *
   * @param singleApp true to close other apps when launching new app (single vm only)
   */
  public void setSingleApp(boolean singleApp) {
    this.singleApp = singleApp;
  }

  /**
   * Sets the hiddenWhenRoot flag.
   *
   * @param hide true to hide node when at root
   */
  public void setHiddenWhenRoot(boolean hide) {
    hiddenWhenRoot = hide;
  }

  /**
   * Gets the singleton value.
   *
   * @return true if singleApp is true for this or an ancestor
   */
  public boolean isSingleton() {
    if(singleton) {
      return true;
    } else if(isRoot()) {
      return false;
    }
    LaunchNode parent = (LaunchNode) getParent();
    return parent.isSingleton();
  }

  /**
   * Sets the singleton flag.
   *
   * @param singleton true to allow single instance when in separate vm
   */
  public void setSingleton(boolean singleton) {
    this.singleton = singleton;
  }

  /**
   * Gets the hiddenInLauncher value.
   *
   * @return true if hiddenInLauncher is true for this or an ancestor
   */
  public boolean isHiddenInLauncher() {
    if(hiddenInLauncher) {
      return true;
    } else if(isRoot()) {
      return false;
    }
    LaunchNode parent = (LaunchNode) getParent();
    return parent.isHiddenInLauncher();
  }

  /**
   * Sets the hiddenInLauncher flag.
   *
   * @param hide true to hide this node in Launcher
   */
  public void setHiddenInLauncher(boolean hide) {
    hiddenInLauncher = hide;
  }

  /**
   * Gets the resource, if any, for this node
   *
   * @return the resource
   */
  public Resource getResource() {
    if(fileName==null) {
      return null;
    }
    String path = XML.getResolvedPath(fileName, Launcher.tabSetBasePath);
    return ResourceLoader.getResource(path);
  }

  /**
   * Determines whether a resource exists for this node.
   *
   * @return true if a resource exists
   */
  public boolean exists() {
    return(getResource()!=null);
  }

  /**
   * Return an existing file with current file name and specified base.
   * May return null.
   *
   * @return existing file, or null
   */
  public File getFile() {
    if(exists()) {
      return getResource().getFile();
    }
    return null;
  }

  /**
   * Determines if this node matches another node.
   *
   * @param node the node to match
   * @return true if the nodes match
   */
  public boolean matches(LaunchNode node) {
    if(node==null) {
      return false;
    }
    boolean match = showLog==node.showLog&&clearLog==node.clearLog&&singleton==node.singleton&&singleVM==node.singleVM&&hiddenWhenRoot==node.hiddenWhenRoot&&name.equals(node.name)&&description.equals(node.description)&&args[0].equals(node.args[0])&&((fileName==null&&node.fileName==null)||(fileName!=null&&fileName.equals(node.fileName)))&&((getLaunchClass()==null&&node.getLaunchClass()==null)||(getLaunchClass()!=null&&getLaunchClass().equals(node.getLaunchClass())))&&((classPath==null&&node.classPath==null)||(classPath!=null&&classPath.equals(node.classPath)));
    return match;
  }

  /**
   * Gets a child node specified by fileName.
   *
   * @param childFileName the file name of the child
   * @return the first child found, or null
   */
  public LaunchNode getChildNode(String childFileName) {
    Enumeration e = breadthFirstEnumeration();
    while(e.hasMoreElements()) {
      LaunchNode next = (LaunchNode) e.nextElement();
      if(childFileName.equals(next.fileName)) {
        return next;
      }
    }
    return null;
  }

  /**
   * Adds menu item to a JPopupMenu or JMenu.
   *
   * @param menu the menu
   */
  public void addMenuItemsTo(final JComponent menu) {
    Enumeration e = children();
    while(e.hasMoreElements()) {
      LaunchNode child = (LaunchNode) e.nextElement();
      if(child.isLeaf()) {
        JMenuItem item = new JMenuItem(child.toString());
        menu.add(item);
        item.setToolTipText(child.description);
        item.setActionCommand(child.getID());
        item.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String id = e.getActionCommand();
            LaunchNode root = (LaunchNode) LaunchNode.this.getRoot();
            Enumeration e2 = root.postorderEnumeration();
            while(e2.hasMoreElements()) {
              LaunchNode node = (LaunchNode) e2.nextElement();
              if(node.getID().equals(id)) {
                node.launch();
                break;
              }
            }
          }
        });
      } else {
        JMenu item = new JMenu(child.toString());
        menu.add(item);
        child.addMenuItemsTo(item);
      }
    }
  }

  /**
   * Adds a terminate frame to this node's frames collection.
   *
   * @param frame the frame to add
   */
  public void addTerminateFrame(JFrame frame) {
    frames.add(frame);
  }

  /**
   * Removes a terminate frame from this node's frames collection.
   *
   * @param frame the frame to remove
   */
  public void removeTerminateFrame(JFrame frame) {
    WindowListener[] listeners = frame.getWindowListeners();
    for(int j = 0;j<listeners.length;j++) {
      if(listeners[j] instanceof Launcher.FrameCloser) {
        frame.removeWindowListener(listeners[j]);
      }
    }
    frames.remove(frame);
  }

  /**
   * Adds an action to this node's actions collection.
   *
   * @param action the action to add
   */
  public void addTerminateAction(Action action) {
    actions.add(action);
  }

  /**
   * Removes an action from this node's actions collection.
   *
   * @param action the action to remove
   */
  public void removeTerminateAction(Action action) {
    actions.remove(action);
  }

  /**
   * Removes an action from this node's actions collection.
   *
   * @param action the action to remove
   */
  public void terminate(Action action) {
    if(actions.contains(action)) {
      removeTerminateAction(action);
      launchCount = Math.max(0, --launchCount);
      if(launchPanel!=null) {
        launchPanel.repaint();
      }
    }
  }

  /**
   * Terminates all apps launched by this node.
   */
  public void terminateAll() {
    for(Iterator it = processes.iterator();it.hasNext();) {
      Process proc = (Process) it.next();
      proc.destroy();
    }
    for(Iterator it = frames.iterator();it.hasNext();) {
      Frame frame = (Frame) it.next();
      WindowListener[] listeners = frame.getWindowListeners();
      for(int j = 0;j<listeners.length;j++) {
        if(listeners[j] instanceof Launcher.FrameCloser) {
          frame.removeWindowListener(listeners[j]);
        }
      }
      frame.dispose();
    }
    for(Iterator it = threads.values().iterator();it.hasNext();) {
      Thread thread = (Thread) it.next();
      if(thread!=null) {
        thread.interrupt();
      }
    }
    Collection allActions = new HashSet(actions);
    for(Iterator it = allActions.iterator();it.hasNext();) {
      Action action = (Action) it.next();
      if(action!=null) {
        action.actionPerformed(null);
      }
    }
    processes.clear();
    frames.clear();
    threads.clear();
    actions.clear();
    launchCount = 0;
  }

  /**
   * Returns the XML.ObjectLoader for this class.
   *
   * @return the object loader
   */
  public static XML.ObjectLoader getLoader() {
    return new Loader();
  }

  /**
   * A class to save and load LaunchNode data in an XMLControl.
   */
  private static class Loader extends XMLLoader {
    public void saveObject(XMLControl control, Object obj) {
      LaunchNode node = (LaunchNode) obj;
      node.setMinimumArgLength(1); // trim args if nec
      if(!node.name.equals("")) {
        control.setValue("name", node.name);
      }
      if(!node.description.equals("")) {
        control.setValue("description", node.description);
      }
      if(node.urlName!=null) {
        control.setValue("url", node.urlName);
      }
      if(node.getLaunchClass()!=null) {
        control.setValue("launch_class", node.getLaunchClass().getName());
      } else if(node.launchClassName!=null) {
        control.setValue("launch_class", node.launchClassName);
      }
      if(!node.args[0].equals("")||node.args.length>1) {
        control.setValue("launch_args", node.args);
      }
      if(node.classPath!=null&&!node.classPath.equals("")) {
        control.setValue("classpath", node.classPath);
      }
      if(node.hiddenWhenRoot) {
        control.setValue("root_hidden", true);
      }
      if(node.singleton) {
        control.setValue("singleton", true);
      }
      if(node.singleVM) {
        control.setValue("single_vm", true);
      }
      if(node.showLog) {
        control.setValue("show_log", true);
      }
      if(node.logLevel!=DEFAULT_LOG_LEVEL) {
        control.setValue("log_level", node.logLevel.getName());
      }
      if(node.clearLog) {
        control.setValue("clear_log", true);
      }
      if(node.singleApp) {
        control.setValue("single_app", true);
      }
      if(node.hiddenInLauncher) {
        control.setValue("hidden_in_launcher", true);
      }
      if(!node.author.equals("")) {
        control.setValue("author", node.author);
      }
      if(!node.codeAuthor.equals("")) {
        control.setValue("code_author", node.codeAuthor);
      }
      if(!node.comment.equals("")) {
        control.setValue("comment", node.comment);
      }
      if(node.children!=null) {
        // list the children and save the list
        ArrayList children = new ArrayList();
        Enumeration e = node.children();
        boolean saveAll = node.isSavingHiddenNodes();
        while(e.hasMoreElements()) {
          LaunchNode child = (LaunchNode) e.nextElement();
          if(!saveAll&&child.isHiddenInLauncher()) {
            continue;
          }
          if(node.isPreviewing()) {
            children.add(child);
          } else if(child.fileName!=null&&!node.isSelfContained()) {
            children.add(child.fileName);
          } else {
            child.fileName = null;
            child.setSelfContained(false);
            children.add(child);
          }
        }
        if(children.size()>0) {
          control.setValue("child_nodes", children);
        }
      }
    }

    public Object createObject(XMLControl control) {
      String name = control.getString("name");
      return new LaunchNode(name);
    }

    public Object loadObject(XMLControl control, Object obj) {
      LaunchNode node = (LaunchNode) obj;
      String name = control.getString("name");
      if(name!=null) {
        node.name = name;
      }
      String description = control.getString("description");
      if(description!=null) {
        node.description = description;
      }
      node.setURL(control.getString("url"));
      node.setClassPath(control.getString("classpath"));
      String className = control.getString("launch_class");
      if(className!=null) {
        node.launchClassName = className;
      }
      String[] args = (String[]) control.getObject("launch_args");
      if(args!=null) {
        node.setArgs(args);
      }
      node.hiddenWhenRoot = control.getBoolean("root_hidden");
      node.singleton = control.getBoolean("singleton");
      node.singleVM = control.getBoolean("single_vm");
      node.showLog = control.getBoolean("show_log");
      node.clearLog = control.getBoolean("clear_log");
      node.singleApp = control.getBoolean("single_app");
      node.hiddenInLauncher = control.getBoolean("hidden_in_launcher");
      Level level = OSPLog.parseLevel(control.getString("log_level"));
      if(level!=null) {
        node.logLevel = level;
      }
      // meta items should not be set to null
      String author = control.getString("author");
      if(author!=null) {
        node.author = author;
      }
      author = control.getString("code_author");
      if(author!=null) {
        node.codeAuthor = author;
      }
      String comment = control.getString("comment");
      if(comment!=null) {
        node.comment = comment;
      }
      name = control.getString("filename");
      if(name!=null) {
        node.setFileName(name);
      }
      Collection children = (ArrayList) control.getObject("child_nodes");
      if(children!=null) {
        node.removeAllChildren();
        Iterator it = children.iterator();
        while(it.hasNext()) {
          Object next = it.next();
          if(next instanceof LaunchNode) { // child node
            // add the child node
            LaunchNode child = (LaunchNode) next;
            node.add(child);
            child.setLaunchClass(child.launchClassName);
          } else if(next instanceof String) { // file name
            String fileName = (String) next;
            String path = XML.getResolvedPath(fileName, Launcher.tabSetBasePath);
            // last path added is first path searched
            ResourceLoader.addSearchPath(Launcher.resourcesPath);
            ResourceLoader.addSearchPath(Launcher.tabSetBasePath);
            // open the file in an xml control
            XMLControlElement childControl = new XMLControlElement();
            String absolutePath = childControl.read(path);
            if(childControl.failedToRead()) {
              JOptionPane.showMessageDialog(null, LaunchRes.getString("Dialog.InvalidXML.Message")+" \""+fileName+"\"", LaunchRes.getString("Dialog.InvalidXML.Title"), JOptionPane.WARNING_MESSAGE);
            }
            // check root to prevent circular references
            LaunchNode root = (LaunchNode) node.getRoot();
            if(root.getChildNode(fileName)!=null||fileName.equals(root.fileName)) {
              continue;
            }
            Class type = childControl.getObjectClass();
            if(LaunchNode.class.isAssignableFrom(type)) {
              // first add the child with just the fileName
              // this allows descendents to get the root
              LaunchNode child = new LaunchNode(LaunchRes.getString("NewNode.Name"));
              child.setFileName(fileName);
              OSPLog.finest(LaunchRes.getString("Log.Message.Loading")+": "+absolutePath);
              node.add(child);
              // then load the child with data
              childControl.loadObject(child);
            }
          }
        }
      }
      return node;
    }
  }

  private URL setURL(URL url) throws Exception {
    InputStream in = url.openStream();
    in.close();
    OSPLog.finer(LaunchRes.getString("Log.Message.URL")+" "+url);
    this.url = url;
    return url;
  }

  protected void setMinimumArgLength(int n) {
    n = Math.max(n, 1); // never shorter than 1 element
    if(n==args.length) {
      return;
    }
    if(n>args.length) {
      // increase the size of the args array
      String[] newArgs = new String[n];
      for(int i = 0;i<n;i++) {
        if(i<args.length) {
          newArgs[i] = args[i];
        } else {
          newArgs[i] = "";
        }
      }
      setArgs(newArgs);
    } else {
      while(args.length>n&&args[args.length-1].equals("")) {
        String[] newArgs = new String[args.length-1];
        for(int i = 0;i<newArgs.length;i++) {
          newArgs[i] = args[i];
        }
        setArgs(newArgs);
      }
    }
  }

  protected void removeThread(Runnable runner) {
    threads.remove(runner);
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
