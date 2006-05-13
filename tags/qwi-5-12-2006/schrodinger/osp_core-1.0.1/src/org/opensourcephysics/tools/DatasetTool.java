/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.beans.*;
import java.io.*;
import java.rmi.*;
import java.util.*;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;

/**
 * This provides a GUI for analyzing datasets.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class DatasetTool extends OSPFrame implements Tool {
  // static fields
  protected static JFileChooser chooser;
  protected static OSPLog log = OSPLog.getOSPLog();
  protected static Dimension dim = new Dimension(720, 500);

  // instance fields
  protected JTabbedPane tabbedPane;
  protected boolean useChooser = true;
  protected JPanel contentPane = new JPanel(new BorderLayout());
  protected PropertyChangeSupport support;
  protected XMLControl control = new XMLControlElement();
  protected JobManager jobManager = new JobManager(this);
  protected JMenu addMenu;
  protected JMenu subtractMenu;
  protected JMenu multiplyMenu;
  protected JMenu divideMenu;

  /**
   * A shared data tool.
   */
  final static DatasetTool DATASET_TOOL = new DatasetTool();

  /**
   * Gets the shared DatasetTool.
   *
   * @return the shared DatasetTool
   */
  public static DatasetTool getTool() {
    return DATASET_TOOL;
  }

  /**
   * Constructs a blank DatasetTool.
   */
  public DatasetTool() {
    super(DatasetRes.getString("Frame.Title"));
    String name = "DatasetTool"; // not displayed--no need to localize
    setName(name);
    createGUI();
    Toolbox.addTool(name, this);
    // Toolbox.addRMITool(name, this);
  }

  /**
   * Constructs a DatasetTool and opens the specified xml file.
   *
   * @param fileName the name of the xml file
   */
  public DatasetTool(String fileName) {
    this();
    open(fileName);
  }

  /**
   * Constructs a DatasetTool and opens the specified dataset.
   *
   * @param dataset the dataset
   */
  public DatasetTool(Dataset dataset) {
    this();
    addTab(dataset);
  }

  /**
   * Constructs a DatasetTool and opens datasets in the specified xml control.
   *
   * @param control the xml control
   */
  public DatasetTool(XMLControl control) {
    this();
    loadDatasets(control);
  }

  /**
   * Opens an xml file specified by name.
   *
   * @param fileName the file name
   * @return the file name, if successfully opened (datasets loaded)
   */
  public String open(String fileName) {
    OSPLog.fine("opening "+fileName);
    // read the file into an XML control and load datasets
    XMLControlElement control = new XMLControlElement(fileName);
    if(!loadDatasets(control).isEmpty()) {
      return fileName;
    }
    OSPLog.finest("no datasets found");
    return null;
  }

  /**
   * Sends a job to this tool and specifies a tool to reply to.
   *
   * @param job the Job
   * @param replyTo the tool to notify when the job is complete (may be null)
   * @throws RemoteException
   */
  public void send(Job job, Tool replyTo) throws RemoteException {
    XMLControlElement control = new XMLControlElement(job.getXML());
    if(control.failedToRead()||control.getObjectClass()==Object.class) {
      return;
    }
    // log the job in
    jobManager.log(job, replyTo);
    // load and get the list of loaded datasets from the control
    Collection datasetsLoaded = loadDatasets(control);
    // associate datasets with this job for easy replies
    Iterator it = datasetsLoaded.iterator();
    while(it.hasNext()) {
      jobManager.associate(job, it.next());
    }
  }

  /**
   * Sets the useChooser flag.
   *
   * @param useChooser true to load datasets with a chooser
   */
  public void setUseChooser(boolean useChooser) {
    this.useChooser = useChooser;
  }

  /**
   * Gets the useChooser flag.
   *
   * @return true if loading datasets with a chooser
   */
  public boolean isUseChooser() {
    return useChooser;
  }

  /**
   * Loads the datasets found in the specified xml control.
   *
   * @param control the xml control
   *
   * @return true if any datasets are loaded
   */
  public Collection loadDatasets(XMLControl control) {
    return loadDatasets(control, useChooser);
  }

  /**
   * Loads the datasets found in the specified xml control.
   *
   * @param control the xml control
   * @param useChooser true to present choices to user
   *
   * @return a collection of newly loaded datasets
   */
  public Collection loadDatasets(XMLControl control, boolean useChooser) {
    java.util.List props;
    Collection datasets = new HashSet();
    if(useChooser) {
      // open selected datasets using an xml tree chooser
      XMLTreeChooser chooser = new XMLTreeChooser(
          DatasetRes.getString("Chooser.Title"), DatasetRes.getString("Chooser.Label"), this);
      props = chooser.choose(control, Dataset.class);
    } else {
      // open all datasets in the control using an xml tree
      XMLTree tree = new XMLTree(control);
      tree.setHighlightedClass(Dataset.class);
      tree.selectHighlightedProperties();
      props = tree.getSelectedProperties();
      if(props.isEmpty()) {
        JOptionPane.showMessageDialog(null, DatasetRes.getString("Dialog.NoDatasets.Message"));
      }
    }
    if(!props.isEmpty()) {
      Iterator it = props.iterator();
      while(it.hasNext()) {
        XMLControl prop = (XMLControl) it.next();
        Dataset dataset = (Dataset) prop.loadObject(null);
        addTab(dataset);
        datasets.add(dataset);
      }
    }
    return datasets;
  }

  /**
   * Returns all open datasets.
   *
   * @return a collection of all open datasets
   */
  public Collection getDatasets() {
    Collection datasets = new HashSet();
    for(int i = 0;i<tabbedPane.getTabCount();i++) {
      DatasetTab tab = (DatasetTab) tabbedPane.getComponentAt(i);
      datasets.add(tab.getDataset());
    }
    return datasets;
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
   * Gets the dataset in the currently selected tab, if any.
   *
   * @return the selected dataset
   */
  public Dataset getSelectedDataset() {
    DatasetTab tab = getSelectedTab();
    if(tab!=null) {
      return tab.getDataset();
    }
    return null;
  }

  // ______________________________ protected methods _____________________________

  /**
   * Adds or selects a tab for the specified dataset.
   *
   * @param dataset the dataset
   * @return true if a new tab was added
   */
  protected boolean addTab(final Dataset dataset) {
    int i = getTabIndex(dataset); // by id number
    // if tab exists, update it's original with new data
    if(i>=0) { // tab exists
      tabbedPane.setSelectedIndex(i);
      double[] x = dataset.getXPoints();
      double[] y = dataset.getYPoints();
      DatasetTab tab = getSelectedTab();
      tab.original.setName(dataset.getName());
      tab.dataset.clear();
      tab.dataset.append(x, y);
      tab.dataset.setName(dataset.getName());
      tab.dataTable.tableChanged(null);
      tab.refresh();
      refreshTabTitles();
      return false;
    }
    final DatasetTab tab = new DatasetTab(dataset);
    tab.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        if (!tab.fitCheckBox.isSelected()) {
          tab.splitPanes[1].setDividerLocation(1.0);
        }
        if (!tab.statsCheckBox.isSelected()) {
          tab.splitPanes[2].setDividerLocation(0);
        }
      }
    });
    String title = dataset.getName() + " (" + dataset.getColumnName(0)+", "
                     + dataset.getColumnName(1) + ")";
    OSPLog.finer("adding tab "+title);
    tabbedPane.addTab(title, tab);
    tabbedPane.setSelectedComponent(tab);
    tab.dataTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
      public void columnAdded(TableColumnModelEvent e) {}
      public void columnRemoved(TableColumnModelEvent e) {}
      public void columnSelectionChanged(ListSelectionEvent e) {}
      public void columnMarginChanged(ChangeEvent e) {}
      public void columnMoved(TableColumnModelEvent e) {
        refreshTabTitles();
      }
    });
    validate();
    tab.init();
    tab.refresh();
    refreshTabTitles();
    return true;
  }

  /**
   * Gets the currently selected tab (DatasetTab), if any.
   *
   * @return the selected tab
   */
  protected DatasetTab getSelectedTab() {
    return(DatasetTab) tabbedPane.getSelectedComponent();
  }

  /**
   * Opens an xml file selected with a chooser.
   *
   * @return the name of the opened file
   */
  protected String open() {
    int result = getChooser().showOpenDialog(null);
    if(result==JFileChooser.APPROVE_OPTION) {
      OSPFrame.chooserDir = getChooser().getCurrentDirectory().toString();
      String fileName = getChooser().getSelectedFile().getAbsolutePath();
      fileName = XML.getRelativePath(fileName);
      return open(fileName);
    }
    return null;
  }

  /**
   * Returns the index of the tab containing the specified Dataset.
   *
   * @param data the Dataset
   * @return the name of the opened file
   */
  protected int getTabIndex(Dataset data) {
    for(int i = 0;i<tabbedPane.getTabCount();i++) {
      DatasetTab tab = (DatasetTab) tabbedPane.getComponentAt(i);
      Dataset dataset = tab.getDataset();
      if(dataset==data || dataset.getID() == data.getID()) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns the dataset at the specified index.
   *
   * @param i the tab index
   * @return the dataset, or null if none
   */
  protected Dataset getDataset(int i) {
    if(i<0||i>=tabbedPane.getTabCount()) {
      return null;
    }
    DatasetTab tab = (DatasetTab) tabbedPane.getComponentAt(i);
    return tab.getDataset();
  }

  /**
   * Removes the specified tab.
   *
   * @param index the tab number
   */
  protected void removeTab(int index) {
    if(index>=0 && index < tabbedPane.getTabCount()) {
      String title = tabbedPane.getTitleAt(index);
      OSPLog.finer("removing tab "+title);
      tabbedPane.removeTabAt(index);
      refreshTabTitles();
    }
  }

  /**
   * Removes all tabs except the specified index.
   *
   * @param index the tab number
   */
  protected void removeAllButTab(int index) {
    for (int i = tabbedPane.getTabCount()-1; i >= 0; i--) {
      if (i ==index) continue;
      String title = tabbedPane.getTitleAt(i);
      OSPLog.finer("removing tab "+title);
      tabbedPane.removeTabAt(i);
    }
    refreshTabTitles();
  }

  /**
   * Removes all tabs.
   */
  protected void removeAllTabs() {
    for (int i = tabbedPane.getTabCount()-1; i >= 0; i--) {
      String title = tabbedPane.getTitleAt(i);
      OSPLog.finer("removing tab "+title);
      tabbedPane.removeTabAt(i);
    }
  }

  protected void refreshTabTitles() {
    // show variables being plotted
    String[] tabTitles = new String[tabbedPane.getTabCount()];
    String[] datasetNames = new String[tabbedPane.getTabCount()];
    boolean singleName = true;
    for(int i = 0;i<tabTitles.length;i++) {
      DatasetTab tab = (DatasetTab)tabbedPane.getComponentAt(i);
      Dataset dataset = tab.displayData;
      tabTitles[i] = "(" + dataset.getColumnName(0)+", "
                     + dataset.getColumnName(1) + ")";
      datasetNames[i] = dataset.getName();
      if (datasetNames[i] == null || datasetNames[i].equals(""))
        datasetNames[i] = DatasetRes.getString("Dataset.Name.Default");
      singleName = singleName && datasetNames[i].equals(datasetNames[0]);
    }
    // show name of dataset
    for (int i = 0; i < tabTitles.length; i++) {
      if (!singleName ||
          datasetNames[i] != DatasetRes.getString("Dataset.Name.Default")) {
        tabTitles[i] = datasetNames[i] + " " + tabTitles[i];
      }
    }
    // set tab titles
    for(int i = 0;i<tabTitles.length;i++) {
      tabbedPane.setTitleAt(i, tabTitles[i]);
    }
  }

  /**
   * Creates the GUI.
   */
  protected void createGUI() {
    // configure the frame
    contentPane.setPreferredSize(dim);
    setContentPane(contentPane);
    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    this.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        DatasetTab tab = getSelectedTab();
        if (tab == null) return;
        if (!tab.fitCheckBox.isSelected()) {
          tab.splitPanes[1].setDividerLocation(1.0);
        }
        if (!tab.statsCheckBox.isSelected()) {
          tab.splitPanes[2].setDividerLocation(0);
        }
      }
    });
    // create tabbed pane
    tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    contentPane.add(tabbedPane, BorderLayout.CENTER);
    tabbedPane.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if(e.isPopupTrigger()||e.getButton()==MouseEvent.BUTTON3||(e.isControlDown()&&System.getProperty("os.name", "").indexOf("Mac")>-1)) {
          final int index = tabbedPane.getSelectedIndex();
          DatasetTab tab = (DatasetTab)tabbedPane.getComponentAt(index);
          final Dataset dataset1 = tab.displayData;
          final String plotVars = dataset1.getColumnName(0)+", "+dataset1.getColumnName(1);
          // make popup with close item
          JMenuItem item = new JMenuItem(DatasetRes.getString("MenuItem.Close"));
          JPopupMenu popup = new JPopupMenu();
          popup.add(item);
          item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              removeTab(index);
            }
          });
          item = new JMenuItem(DatasetRes.getString("MenuItem.CloseOthers"));
          popup.add(item);
          item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              removeAllButTab(index);
            }
          });
          item = new JMenuItem(DatasetRes.getString("MenuItem.CloseAll"));
          popup.add(item);
          item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              removeAllTabs();
            }
          });
          // create math operation items
          final ArrayList tabs = new ArrayList(); // operable tab numbers
          final String noName = DatasetRes.getString("Dataset.Name.Default");
          for(int i = 0;i<tabbedPane.getTabCount();i++) {
            if (i == index)
              continue;
            tab = (DatasetTab)tabbedPane.getComponentAt(i);
            Dataset target = tab.displayData;
            if (!target.getColumnName(0).equals(dataset1.getColumnName(0)))
              continue;
            tabs.add(String.valueOf(i));
          }
          if(!tabs.isEmpty()) {
            // create operate action to perform math operations
            Action operateAction = new AbstractAction() {
              public void actionPerformed(ActionEvent e) {
                // get the tabs and datasets to operate with
                int i = Integer.parseInt(e.getActionCommand()); // tab number
                DatasetTab tab = (DatasetTab)tabbedPane.getComponentAt(i);
                Dataset dataset2 = tab.displayData;
                // get names for menus and new variables
                String name1 = dataset1.getName();
                String name2 = dataset2.getName();
                String var1 = dataset1.getColumnName(1);
                String var2 = dataset2.getColumnName(1);
                // replace empty names with default
                if (name1 == null || name1.equals(""))
                  name1 = noName;
                if (name2 == null || name2.equals(""))
                  name2 = noName;
                // add parentheses if names or variables contain an operator
                if (name1.indexOf(" - ") > -1 ||
                    name1.indexOf(" + ") > -1 ||
                    name1.indexOf(" * ") > -1 ||
                    name1.indexOf(" / ") > -1) {
                  name1 = "(" + name1 + ")";
                }
                if (var1.indexOf("-") > -1 ||
                    var1.indexOf("+") > -1 ||
                    var1.indexOf("*") > -1 ||
                    var1.indexOf("/") > -1) {
                  var1 = "(" + var1 + ")";
                }
                if (name2.indexOf(" - ") > -1 ||
                    name2.indexOf(" + ") > -1 ||
                    name2.indexOf(" * ") > -1 ||
                    name2.indexOf(" / ") > -1) {
                  name2 = "(" + name2 + ")";
                }
                if (var2.indexOf("-") > -1 ||
                    var2.indexOf("+") > -1 ||
                    var2.indexOf("*") > -1 ||
                    var2.indexOf("/") > -1) {
                  var2 = "(" + var2 + ")";
                }
                // determine operation to perform
                String operation = "";
                JMenuItem selectedItem = (JMenuItem)e.getSource();
                if (selectedItem.getParent() == addMenu.getPopupMenu())
                  operation = "+";
                if (selectedItem.getParent() == subtractMenu.getPopupMenu())
                  operation = "-";
                else if (selectedItem.getParent() == multiplyMenu.getPopupMenu())
                  operation = "*";
                else if (selectedItem.getParent() == divideMenu.getPopupMenu())
                  operation = "/";
                // determine new name and variable
                String newName = name1;
                if (!name1.equals(name2))
                  newName += " " + operation + " " + name2;
                String newVar = var1 + operation + var2;
                // if requested tab exists, select it
                for (int j = 0; j < tabbedPane.getTabCount(); j++) {
                  tab = (DatasetTab)tabbedPane.getComponentAt(j);
                  if (newName.equals(tab.dataset.getName()) &&
                      plotVars.equals(tab.dataset.getColumnName(0)+", "+newVar)) {
                    tabbedPane.setSelectedIndex(j);
                    getSelectedTab().dataTable.tableChanged(null);
                    getSelectedTab().refresh();
                    refreshTabTitles();
                    return;
                  }
                }
                // operate on y-values only if x-values are the same
                double[] x1 = dataset1.getXPoints();
                double[] y1 = dataset1.getYPoints();
                double[] x2 = dataset2.getXPoints();
                double[] y2 = dataset2.getYPoints();
                int n = Math.min(y1.length, y2.length);
                Dataset newdata = new Dataset();
                for(int j = 0;j<n;j++) {
                  if (x1[j] != x2[j]) continue;
                  if (operation.equals("+")) {
                    newdata.append(x1[j], y1[j] + y2[j]);
                  }
                  else if (operation.equals("-")) {
                    newdata.append(x1[j], y1[j] - y2[j]);
                  }
                  else if (operation.equals("*")) {
                    newdata.append(x1[j], y1[j] * y2[j]);
                  }
                  else if (operation.equals("/")) {
                    newdata.append(x1[j], y1[j] / y2[j]);
                  }
                }
                if (newdata.getRowCount() > 0) {
                  if (!newName.equals(noName)) newdata.setName(newName);
                  newdata.setXYColumnNames(dataset1.getColumnName(0),
                                           var1 + operation + var2);
                  newdata.setMarkerShape(dataset1.getMarkerShape());
                  newdata.setMarkerSize(dataset1.getMarkerSize());
                  newdata.setConnected(dataset1.isConnected());
                  newdata.setLineColor(dataset1.getLineColor());
                  newdata.setMarkerColor(dataset1.getFillColor(),
                                         dataset1.getEdgeColor());
                  addTab(newdata);
                }
                else {
                  String title = DatasetRes.getString("Dialog.AddFailed.Title");
                  if (selectedItem.getParent() == subtractMenu.getPopupMenu())
                    title = DatasetRes.getString("Dialog.SubtractFailed.Title");
                  else if (selectedItem.getParent() == divideMenu.getPopupMenu())
                    title = DatasetRes.getString("Dialog.DivideFailed.Title");
                  JOptionPane.showMessageDialog(
                      tabbedPane,
                      DatasetRes.getString("Dialog.OperationFailed.Message"),
                      title,
                      JOptionPane.WARNING_MESSAGE);
                }
              }
            };
            addMenu = new JMenu(DatasetRes.getString("Menu.Add"));
            subtractMenu = new JMenu(DatasetRes.getString("Menu.Subtract"));
            multiplyMenu = new JMenu(DatasetRes.getString("Menu.MultiplyBy"));
            divideMenu = new JMenu(DatasetRes.getString("Menu.DivideBy"));
            popup.addSeparator();
            popup.add(addMenu);
            popup.add(subtractMenu);
            popup.add(multiplyMenu);
            popup.add(divideMenu);
            Iterator it = tabs.iterator();
            while(it.hasNext()) {
              String s = (String) it.next();
              int i = Integer.parseInt(s);
              item = new JMenuItem(tabbedPane.getTitleAt(i));
              item.setActionCommand(s);
              item.addActionListener(operateAction);
              addMenu.add(item);
              item = new JMenuItem(tabbedPane.getTitleAt(i));
              item.setActionCommand(s);
              item.addActionListener(operateAction);
              subtractMenu.add(item);
              item = new JMenuItem(tabbedPane.getTitleAt(i));
              item.setActionCommand(s);
              item.addActionListener(operateAction);
              multiplyMenu.add(item);
              item = new JMenuItem(tabbedPane.getTitleAt(i));
              item.setActionCommand(s);
              item.addActionListener(operateAction);
              divideMenu.add(item);
            }
          }
          popup.show(tabbedPane, e.getX(), e.getY()+8);
        }
      }
    });
    // create the menu bar
    int keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    JMenuBar menubar = new JMenuBar();
    JMenu fileMenu = new JMenu(DatasetRes.getString("Menu.File"));
    menubar.add(fileMenu);
    JMenuItem openItem = new JMenuItem(DatasetRes.getString("MenuItem.Open"));
    openItem.setAccelerator(KeyStroke.getKeyStroke('O', keyMask));
    openItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        open();
      }
    });
    fileMenu.add(openItem);
    JMenuItem closeItem = new JMenuItem(DatasetRes.getString("MenuItem.Close"));
    closeItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int index = tabbedPane.getSelectedIndex();
        removeTab(index);
      }
    });
    fileMenu.add(closeItem);
    JMenuItem closeAllItem = new JMenuItem(DatasetRes.getString("MenuItem.CloseAll"));
    closeAllItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeAllTabs();
      }
    });
    fileMenu.add(closeAllItem);
    fileMenu.addSeparator();
    JMenuItem exitItem = new JMenuItem(DatasetRes.getString("MenuItem.Exit"));
    exitItem.setAccelerator(KeyStroke.getKeyStroke('Q', keyMask));
    exitItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeAllTabs();
        System.exit(0);
      }
    });
    fileMenu.add(exitItem);
    JMenu editMenu = new JMenu(DatasetRes.getString("Menu.Edit"));
    menubar.add(editMenu);
    Action copyAction = new AbstractAction(DatasetRes.getString("MenuItem.Copy")) {
      public void actionPerformed(ActionEvent e) {
        Dataset dataset = getSelectedDataset();
        if(dataset==null) {
          return;
        }
        int i = tabbedPane.getSelectedIndex();
        String title = tabbedPane.getTitleAt(i);
        OSPLog.finest("copying "+title);
        XMLControl control = new XMLControlElement(dataset);
        StringSelection data = new StringSelection(control.toXML());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(data, data);
      }
    };
    JMenuItem copyItem = new JMenuItem(copyAction);
    copyItem.setAccelerator(KeyStroke.getKeyStroke('C', keyMask));
    editMenu.add(copyItem);
    Action pasteAction = new AbstractAction(DatasetRes.getString("MenuItem.Paste")) {
      public void actionPerformed(ActionEvent e) {
        try {
          Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          Transferable data = clipboard.getContents(null);
          String dataString = (String) data.getTransferData(DataFlavor.stringFlavor);
          if(dataString!=null) {
            XMLControl control = new XMLControlElement();
            control.readXML(dataString);
            if(!control.failedToRead()) {
              OSPLog.finest("pasting");
            }
            if(loadDatasets(control).isEmpty()) {
              OSPLog.finest("no datasets found");
            }
          }
        } catch(UnsupportedFlavorException ex) {}
        catch(IOException ex) {}
        catch(HeadlessException ex) {}
      }
    };
    JMenuItem pasteItem = new JMenuItem(pasteAction);
    pasteItem.setAccelerator(KeyStroke.getKeyStroke('V', keyMask));
    editMenu.add(pasteItem);
//    editMenu.addSeparator();
//    JMenuItem applyItem = new JMenuItem("Apply Changes");
//    applyItem.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        Dataset dataset = getSelectedDataset();
//        jobManager.sendReplies(dataset);
//      }
//    });
//    editMenu.add(applyItem);
    JMenu helpMenu = new JMenu(DatasetRes.getString("Menu.Help"));
    menubar.add(helpMenu);
    JMenuItem logItem = new JMenuItem(DatasetRes.getString("MenuItem.Log"));
    logItem.setAccelerator(KeyStroke.getKeyStroke('L', keyMask));
    logItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if(log.getLocation().x==0&&log.getLocation().y==0) {
          Point p = getLocation();
          log.setLocation(p.x+28, p.y+28);
        }
        log.setVisible(true);
      }
    });
    helpMenu.add(logItem);
    helpMenu.addSeparator();
    JMenuItem aboutItem = new JMenuItem(DatasetRes.getString("MenuItem.About"));
    aboutItem.setAccelerator(KeyStroke.getKeyStroke('A', keyMask));
    aboutItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showAboutDialog();
      }
    });
    helpMenu.add(aboutItem);
    setJMenuBar(menubar);
    pack();
    // center this on the screen
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (dim.width-getBounds().width)/2;
    int y = (dim.height-getBounds().height)/2;
    setLocation(x, y);
  }

  /**
   * Shows the about dialog.
   */
  protected void showAboutDialog() {
    String aboutString = getName()+" 1.0  Jan 2006\n"+"Open Source Physics Project\n"+"www.opensourcephysics.org";
    JOptionPane.showMessageDialog(this, aboutString,
                                  DatasetRes.getString("Dialog.About.Title")+" "+getName(),
                                  JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Main entry point when used as application.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    DatasetTool tool = getTool();
    tool.setVisible(true);
  }
}

/**
 * String resources for datasetTool classes.
 */
class DatasetRes {
  // static fields
  static ResourceBundle res = ResourceBundle.getBundle("org.opensourcephysics.resources.tools.dataset");

  /**
   * Private constructor to prevent instantiation.
   */
  private DatasetRes() {}

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
