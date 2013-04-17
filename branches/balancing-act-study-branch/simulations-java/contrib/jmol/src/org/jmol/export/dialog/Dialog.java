/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2008-03-05 17:47:26 -0600 (Wed, 05 Mar 2008) $
 * $Revision: 9055 $
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
package org.jmol.export.dialog;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;

import org.jmol.api.JmolDialogInterface;
import org.jmol.api.JmolViewer;
import org.jmol.export.history.HistoryFile;
import org.jmol.i18n.GT;
import org.jmol.util.Escape;
import org.jmol.viewer.FileManager;

public class Dialog extends JPanel implements JmolDialogInterface {

  String[] extensions = new String[10];
  String choice;
  String extension;
  
  // static so that they can be shared among instances
  
  static private int defaultChoice;
  static int qualityJPG = 75;
  static int qualityPNG = 2;
  
  private JSlider qSliderJPEG, qSliderPNG;
  private JComboBox cb;

  JPanel qPanelJPEG, qPanelPNG;
  static JFileChooser imageChooser;
  static JFileChooser saveChooser;

  public Dialog() {
  }

  private static FileChooser openChooser;
  private FilePreview openPreview;

  public String getOpenFileNameFromDialog(String appletContext,
                                          JmolViewer viewer,
                                          String fileName, Object historyFileObject,
                                          String windowName,
                                          boolean allowAppend) {

    HistoryFile historyFile = (HistoryFile) historyFileObject;

    if (openChooser == null) {
      openChooser = new FileChooser();
      Object temp = UIManager.get("FileChooser.fileNameLabelText");
      UIManager.put("FileChooser.fileNameLabelText", GT._("File or URL:"));
      getXPlatformLook(openChooser);
      UIManager.put("FileChooser.fileNameLabelText", temp);
    }
    if (openPreview == null
        && (viewer.isApplet() || Boolean.valueOf(
            System.getProperty("openFilePreview", "true")).booleanValue())) {
      openPreview = new FilePreview(openChooser, viewer.getModelAdapter(), allowAppend,
          appletContext);
    }

    if (historyFile != null) {
      Dimension dim = historyFile.getWindowSize(windowName);
      if (dim != null)
        openChooser.setDialogSize(dim);
      Point loc = historyFile.getWindowPosition(windowName);
      if (loc != null)
        openChooser.setDialogLocation(loc);
    }

    openChooser.resetChoosableFileFilters();

    if (fileName != null) {
      int pt = fileName.lastIndexOf(".");
      String sType = fileName.substring(pt + 1);
      if (pt >= 0 && sType.length() > 0)
        openChooser.addChoosableFileFilter(new TypeFilter(sType));
      if (fileName.indexOf(".") == 0)
        fileName = "Jmol" + fileName;
      if (fileName.length() > 0)
        openChooser.setSelectedFile(new File(fileName));
    }
    //System.out.println("fileName for dialog: " + fileName);
    if (fileName == null || fileName.indexOf(":") < 0 && fileName.indexOf("/") != 0) {
      File dir = FileManager.getLocalDirectory(viewer, true);
      //System.out.println("directory for dialog: " + dir.getAbsolutePath());
      openChooser.setCurrentDirectory(dir);
    }
    File file = null;
    if (openChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
      file = openChooser.getSelectedFile();


    if (file == null)
      return closePreview();
    
    if (historyFile != null)
      historyFile.addWindowInfo(windowName, openChooser.getDialog(), null);

    String url = FileManager.getLocalUrl(file);
    if (url != null) {
      fileName = url;
    } else {
      FileManager.setLocalPath(viewer, file.getParent(), true);
      fileName = file.getAbsolutePath();
    }
    boolean doAppend = (allowAppend && openPreview != null && openPreview.isAppendSelected());
    closePreview();
    if (fileName.startsWith("/"))
      fileName = "file://" + fileName; // for Macs
    return (doAppend ? "load append " + Escape.escape(fileName) : fileName);
  }

  String closePreview() {
    if (openPreview != null)
      openPreview.updatePreview(null);
    return null;
  }
  
  public String getSaveFileNameFromDialog(JmolViewer viewer, String fileName,
                                          String type) {
    if (saveChooser == null) {
      saveChooser = new JFileChooser();
      getXPlatformLook(saveChooser);
    }
    saveChooser.setCurrentDirectory(FileManager.getLocalDirectory(viewer, true));
    File file = null;
    saveChooser.resetChoosableFileFilters();
    if (fileName != null) {
      int pt = fileName.lastIndexOf(".");
      String sType = fileName.substring(pt + 1);
      if (pt >= 0 && sType.length() > 0)
        saveChooser.addChoosableFileFilter(new TypeFilter(sType));
      if (fileName.equals("*"))
        fileName = viewer.getModelSetFileName();
      if (fileName.indexOf(".") == 0)
        fileName = "Jmol" + fileName;
      file = new File(fileName);
    }
    if (type != null)
      saveChooser.addChoosableFileFilter(new TypeFilter(type));
    saveChooser.setSelectedFile(file);
    if ((file = showSaveDialog(this, saveChooser, file)) == null)
      return null;
    FileManager.setLocalPath(viewer, file.getParent(), true);
    return file.getAbsolutePath();
  }

  
  public String getImageFileNameFromDialog(JmolViewer viewer, String fileName,
                                           String type, String[] imageChoices,
                                           String[] imageExtensions,
                                           int qualityJPG0, int qualityPNG0) {
    if (qualityJPG0 < 0 || qualityJPG0 > 100)
      qualityJPG0 = qualityJPG;
    if (qualityPNG0 < 0)
      qualityPNG0 = qualityPNG;
    if (qualityPNG0 > 9)
      qualityPNG0 = 2;
    qualityJPG = qualityJPG0;
    qualityPNG = qualityPNG0;
    if (extension == null)
      extension = "jpg";
    
    if (imageChooser == null) {
      imageChooser = new JFileChooser();
      getXPlatformLook(imageChooser);
    }
    imageChooser.setCurrentDirectory(FileManager.getLocalDirectory(viewer, true));
    imageChooser.resetChoosableFileFilters();
    File file = null;
    if (fileName == null) {
      fileName = viewer.getModelSetFileName();
      String pathName = imageChooser.getCurrentDirectory().getPath();
      if (fileName != null && pathName != null) {
        int extensionStart = fileName.lastIndexOf('.');
        if (extensionStart != -1) {
          fileName = fileName.substring(0, extensionStart) + "."
              + extension;
        }
        file = new File(pathName, fileName);
      }
    } else {
      if (fileName.indexOf(".") == 0)
        fileName = "Jmol" + fileName;
      file = new File(fileName);
      type = fileName.substring(fileName.lastIndexOf(".") + 1);
      for (int i = 0; i < imageExtensions.length; i++)
        if (type.equals(imageChoices[i])
            || type.toLowerCase().equals(imageExtensions[i])) {
          type = imageChoices[i];
          break;
        }
    }
    createExportPanel(imageChoices, imageExtensions, type);
    imageChooser.setSelectedFile(initialFile = file);
    if ((file = showSaveDialog(this, imageChooser, file)) == null)
      return null;
    qualityJPG = qSliderJPEG.getValue();
    qualityPNG = qSliderPNG.getValue();
    if (cb.getSelectedIndex() >= 0)
      defaultChoice = cb.getSelectedIndex();
    FileManager.setLocalPath(viewer, file.getParent(), true);
    return file.getAbsolutePath();
  }

  File initialFile;

  private void createExportPanel(String[] choices,
                          String[] extensions, String type) {
    imageChooser.setAccessory(this);
    setLayout(new BorderLayout());
    if (type == null || type.equals("JPG"))
      type = "JPEG";
    for (defaultChoice = choices.length; --defaultChoice >= 1;)
      if (choices[defaultChoice].equals(type))
        break;
    extension = extensions[defaultChoice];
    choice = choices[defaultChoice];
    this.extensions = extensions;
    imageChooser.resetChoosableFileFilters();
    imageChooser.addChoosableFileFilter(new TypeFilter(extension));
    JPanel cbPanel = new JPanel();
    cbPanel.setLayout(new FlowLayout());
    cbPanel.setBorder(new TitledBorder(GT._("Image Type")));
    cb = new JComboBox();
    for (int i = 0; i < choices.length; i++) {
      cb.addItem(choices[i]);
    }
    cbPanel.add(cb);
    cb.setSelectedIndex(defaultChoice);
    cb.addItemListener(new ExportChoiceListener());
    add(cbPanel, BorderLayout.NORTH);

    JPanel qPanel2 = new JPanel();
    qPanel2.setLayout(new BorderLayout());

    qPanelJPEG = new JPanel();
    qPanelJPEG.setLayout(new BorderLayout());
    qPanelJPEG.setBorder(new TitledBorder(GT._("JPEG Quality ({0})",
        qualityJPG)));
    qSliderJPEG = new JSlider(SwingConstants.HORIZONTAL, 50, 100, qualityJPG);
    qSliderJPEG.putClientProperty("JSlider.isFilled", Boolean.TRUE);
    qSliderJPEG.setPaintTicks(true);
    qSliderJPEG.setMajorTickSpacing(10);
    qSliderJPEG.setPaintLabels(true);
    qSliderJPEG.addChangeListener(new QualityListener(true, qSliderJPEG));
    qPanelJPEG.add(qSliderJPEG, BorderLayout.SOUTH);
    qPanel2.add(qPanelJPEG, BorderLayout.NORTH);

    qPanelPNG = new JPanel();
    qPanelPNG.setLayout(new BorderLayout());
    qPanelPNG
        .setBorder(new TitledBorder(GT._("PNG Compression  ({0})", qualityPNG)));
    qSliderPNG = new JSlider(SwingConstants.HORIZONTAL, 0, 9, qualityPNG);
    qSliderPNG.putClientProperty("JSlider.isFilled", Boolean.TRUE);
    qSliderPNG.setPaintTicks(true);
    qSliderPNG.setMajorTickSpacing(2);
    qSliderPNG.setPaintLabels(true);
    qSliderPNG.addChangeListener(new QualityListener(false, qSliderPNG));
    qPanelPNG.add(qSliderPNG, BorderLayout.SOUTH);
    qPanel2.add(qPanelPNG, BorderLayout.SOUTH);
    add(qPanel2, BorderLayout.SOUTH);
  }

  public class QualityListener implements ChangeListener {

    private boolean isJPEG;
    private JSlider slider;

    public QualityListener(boolean isJPEG, JSlider slider) {
      this.isJPEG = isJPEG;
      this.slider = slider;
    }

    public void stateChanged(ChangeEvent arg0) {
      int value = slider.getValue();
      if (isJPEG) {
        qualityJPG = value;
        qPanelJPEG
            .setBorder(new TitledBorder(GT._("JPEG Quality ({0})", value)));
      } else {
        qualityPNG = value;
        qPanelPNG.setBorder(new TitledBorder(GT._("PNG Quality ({0})", value)));
      }
    }
  }

  public class ExportChoiceListener implements ItemListener {
    public void itemStateChanged(ItemEvent e) {

      JComboBox source = (JComboBox) e.getSource();
      File selectedFile = imageChooser.getSelectedFile();
      if (selectedFile == null)
        selectedFile = initialFile;
      File newFile = null;
      String name;
      String newExt = extensions[source.getSelectedIndex()];
      if ((name = selectedFile.getName()) != null
          && name.endsWith("." + extension)) {
        name = name.substring(0, name.length() - extension.length());
        name += newExt;
        initialFile = newFile = new File(selectedFile.getParent(), name);
      }
      extension = newExt;
      imageChooser.resetChoosableFileFilters();
      imageChooser.addChoosableFileFilter(new TypeFilter(extension));
      if (newFile != null)
        imageChooser.setSelectedFile(newFile);
      choice = (String) source.getSelectedItem();
    }
  }

  /* (non-Javadoc)
   * @see org.jmol.export.JmolImageTyperInterface#getType()
   */
  public String getType() {
    return choice;
  }

  /* (non-Javadoc)
   * @see org.jmol.export.JmolImageTyperInterface#getQuality(java.lang.String)
   */
  public int getQuality(String sType) {
    return (sType.equals("JPEG") || sType.equals("JPG") ? qualityJPG : sType
        .equals("PNG") ? qualityPNG : -1);
  }

  private static boolean doOverWrite(JFileChooser chooser, File file) {
    Object[] options = { GT._("Yes"), GT._("No") };
    int opt = JOptionPane.showOptionDialog(chooser, GT._(
        "Do you want to overwrite file {0}?", file.getAbsolutePath()), GT
        ._("Warning"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
        null, options, options[0]);
    return (opt == 0);
  }

  private File showSaveDialog(Component c,
                                     JFileChooser chooser, File file) {
    while (true) {
      if (chooser.showSaveDialog(c) != JFileChooser.APPROVE_OPTION)
        return null;
      if (cb != null && cb.getSelectedIndex() >= 0)
        defaultChoice = cb.getSelectedIndex();
      if ((file = chooser.getSelectedFile()) == null || !file.exists()
          || doOverWrite(chooser, file))
        return file;
    }
  }

  public static class TypeFilter extends FileFilter {

    String thisType;

    TypeFilter(String type) {
      thisType = type.toLowerCase();
    }

    @Override
    public boolean accept(File f) {
      if (f.isDirectory() || thisType == null) {
        return true;
      }
      String ext = f.getName();
      int pt = ext.lastIndexOf(".");
      return (pt >= 0 && ext.substring(pt + 1).toLowerCase().equals(thisType));
    }

    @Override
    public String getDescription() {
      return thisType.toUpperCase() + " (*." + thisType + ")";
    }

  }

  static boolean haveTranslations = false;

  public void setupUI(boolean forceNewTranslation) {
    if (forceNewTranslation || !haveTranslations)
      setupUIManager();
    haveTranslations = true;
  }

  /**
   * Setup the UIManager (for i18n) 
   */

  public static void setupUIManager() {

    // FileChooser strings
    UIManager.put("FileChooser.acceptAllFileFilterText", GT._("All Files"));
    UIManager.put("FileChooser.cancelButtonText", GT._("Cancel"));
    UIManager.put("FileChooser.cancelButtonToolTipText", GT
        ._("Abort file chooser dialog"));
    UIManager.put("FileChooser.detailsViewButtonAccessibleName", GT
        ._("Details"));
    UIManager.put("FileChooser.detailsViewButtonToolTipText", GT._("Details"));
    UIManager.put("FileChooser.directoryDescriptionText", GT._("Directory"));
    UIManager.put("FileChooser.directoryOpenButtonText", GT._("Open"));
    UIManager.put("FileChooser.directoryOpenButtonToolTipText", GT
        ._("Open selected directory"));
    UIManager.put("FileChooser.fileAttrHeaderText", GT._("Attributes"));
    UIManager.put("FileChooser.fileDateHeaderText", GT._("Modified"));
    UIManager.put("FileChooser.fileDescriptionText", GT._("Generic File"));
    UIManager.put("FileChooser.fileNameHeaderText", GT._("Name"));
    UIManager.put("FileChooser.fileNameLabelText", GT._("File Name:"));
    UIManager.put("FileChooser.fileSizeHeaderText", GT._("Size"));
    UIManager.put("FileChooser.filesOfTypeLabelText", GT._("Files of Type:"));
    UIManager.put("FileChooser.fileTypeHeaderText", GT._("Type"));
    UIManager.put("FileChooser.helpButtonText", GT._("Help"));
    UIManager
        .put("FileChooser.helpButtonToolTipText", GT._("FileChooser help"));
    UIManager.put("FileChooser.homeFolderAccessibleName", GT._("Home"));
    UIManager.put("FileChooser.homeFolderToolTipText", GT._("Home"));
    UIManager.put("FileChooser.listViewButtonAccessibleName", GT._("List"));
    UIManager.put("FileChooser.listViewButtonToolTipText", GT._("List"));
    UIManager.put("FileChooser.lookInLabelText", GT._("Look In:"));
    UIManager.put("FileChooser.newFolderErrorText", GT
        ._("Error creating new folder"));
    UIManager.put("FileChooser.newFolderAccessibleName", GT._("New Folder"));
    UIManager
        .put("FileChooser.newFolderToolTipText", GT._("Create New Folder"));
    UIManager.put("FileChooser.openButtonText", GT._("Open"));
    UIManager.put("FileChooser.openButtonToolTipText", GT
        ._("Open selected file"));
    UIManager.put("FileChooser.openDialogTitleText", GT._("Open"));
    UIManager.put("FileChooser.saveButtonText", GT._("Save"));
    UIManager.put("FileChooser.saveButtonToolTipText", GT
        ._("Save selected file"));
    UIManager.put("FileChooser.saveDialogTitleText", GT._("Save"));
    UIManager.put("FileChooser.saveInLabelText", GT._("Save In:"));
    UIManager.put("FileChooser.updateButtonText", GT._("Update"));
    UIManager.put("FileChooser.updateButtonToolTipText", GT
        ._("Update directory listing"));
    UIManager.put("FileChooser.upFolderAccessibleName", GT._("Up"));
    UIManager.put("FileChooser.upFolderToolTipText", GT._("Up One Level"));

    // OptionPane strings
    UIManager.put("OptionPane.cancelButtonText", GT._("Cancel"));
    UIManager.put("OptionPane.noButtonText", GT._("No"));
    UIManager.put("OptionPane.okButtonText", GT._("OK"));
    UIManager.put("OptionPane.yesButtonText", GT._("Yes"));
  }
  
  private static boolean isMac = System.getProperty("os.name").startsWith("Mac");
  
  private static void getXPlatformLook(JFileChooser fc) {
    if (isMac) {
      LookAndFeel lnf = UIManager.getLookAndFeel();
      // JFileChooser on Mac OS X with the native L&F doesn't work well.
      // If the native L&F of Mac is selected, disable it for the file chooser
      if (lnf.isNativeLookAndFeel()) {
        try {
          UIManager.setLookAndFeel(UIManager
              .getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
          e.printStackTrace();
        }
        fc.updateUI();
        try {
          UIManager.setLookAndFeel(lnf);
        } catch (UnsupportedLookAndFeelException e) {
          e.printStackTrace();
        }
      }
    } else {
      fc.updateUI();
    }
  }
}
