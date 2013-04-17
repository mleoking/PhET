/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-08-17 20:56:43 -0700 (Tue, 17 Aug 2010) $
 * $Revision: 14027 $
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
package org.openscience.jmol.app.jmolpanel;

import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jmol.i18n.GT;
import org.openscience.jmol.app.jmolpanel.JmolPanel;

/**
 * Manages a list of recently opened files.
 *
 * @author Bradley A. Smith (bradley@baysmith.com)
 */
class RecentFilesDialog extends JDialog implements ActionListener,
    WindowListener {

  String selectedFileName = null;
  private static final int MAX_FILES = 10;
  private JButton okButton;
  private JButton cancelButton;
  String[] files = new String[MAX_FILES];
  JList fileList;
  java.util.Properties props;

  /** Creates a hidden recent files dialog
   * @param boss 
   */
  public RecentFilesDialog(java.awt.Frame boss) {

    super(boss, GT._("Recent Files"), true);
    props = new java.util.Properties();
    getFiles();
    getContentPane().setLayout(new java.awt.BorderLayout());
    JPanel buttonPanel = new JPanel();
    okButton = new JButton(GT._("Open"));
    okButton.addActionListener(this);
    buttonPanel.add(okButton);
    cancelButton =
        new JButton(GT._("Cancel"));
    cancelButton.addActionListener(this);
    buttonPanel.add(cancelButton);
    getContentPane().add("South", buttonPanel);

    fileList = new JList(files);
    fileList.setSelectedIndex(0);
    fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    MouseListener dblClickListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            int dblClickIndex = fileList.locationToIndex(e.getPoint());
            if (dblClickIndex >= 0 &&
                dblClickIndex < files.length &&
                files[dblClickIndex] != null) {
              selectedFileName = files[dblClickIndex];
              close();
            }
          }
        }
      };
    fileList.addMouseListener(dblClickListener);

    getContentPane().add("Center", fileList);
    //    System.out.println("I am setting my location relative to:" + boss);
    setLocation(100, 100);
    pack();
  }

  private void getFiles() {

    props = JmolPanel.historyFile.getProperties();
    for (int i = 0; i < MAX_FILES; i++) {
      files[i] = props.getProperty("recentFilesFile" + i);
    }
  }

  /**
   * Adds this file to the history. If already present,
   * this file is premoted to the top position.
   * @param name Name of the file
   */
  public void addFile(String name) {

    int currentPosition = -1;

    //Find if file is already present
    for (int i = 0; i < MAX_FILES; i++) {
      if ((files[i] != null) && files[i].equals(name)) {
        currentPosition = i;
      }
    }

    //No change so cope out
    if (currentPosition == 0) {
      return;
    }

    //present so shift files below current position up one,
    //removing current position
    if (currentPosition > 0) {
      for (int i = currentPosition; i < MAX_FILES - 1; i++) {
        files[i] = files[i + 1];
      }
    }

    // Shift everything down one
    for (int j = MAX_FILES - 2; j >= 0; j--) {
      files[j + 1] = files[j];
    }

    //Insert file at head of list
    files[0] = name;
    fileList.setListData(files);
    fileList.setSelectedIndex(0);
    pack();
    saveList();
  }

  /** Saves the list to the history file. Called automaticaly when files are added **/
  public void saveList() {

    for (int i = 0; i < 10; i++) {
      if (files[i] != null) {
        props.setProperty("recentFilesFile" + i, files[i]);
      }
    }

    JmolPanel.historyFile.addProperties(props);
  }

  /**
   *   @return String The name of the file picked or null if the action was aborted.
  **/
  public String getFile() {
    return selectedFileName;
  }

  public void windowClosing(java.awt.event.WindowEvent e) {
    cancel();
    close();
  }

  void cancel() {
    selectedFileName = null;
  }

  void close() {
    setVisible(false);
  }

  public void actionPerformed(java.awt.event.ActionEvent e) {

    if (e.getSource() == okButton) {
      int fileIndex = fileList.getSelectedIndex();
      if (fileIndex < files.length) {
        selectedFileName = files[fileIndex];
        close();
      }
    } else if (e.getSource() == cancelButton) {
      cancel();
      close();
    }
  }

  public void windowClosed(java.awt.event.WindowEvent e) {
  }

  public void windowOpened(java.awt.event.WindowEvent e) {
  }

  public void windowIconified(java.awt.event.WindowEvent e) {
  }

  public void windowDeiconified(java.awt.event.WindowEvent e) {
  }

  public void windowActivated(java.awt.event.WindowEvent e) {
  }

  public void windowDeactivated(java.awt.event.WindowEvent e) {
  }

  public void notifyFileOpen(String fullPathName) {
    if (fullPathName != null)
      addFile(fullPathName);
  }
}
