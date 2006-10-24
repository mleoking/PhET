/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display;
import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *  TableFrame displays a DataTable with a scroll pane in a frame.
 *
 * @author     Joshua Gould
 * @author     Wolfgang Christian
 * @created    August 16, 2002
 * @version    1.0
 */
public class DataTableFrame extends OSPFrame {
  protected JMenuBar menuBar;
  protected JMenu fileMenu;
  protected JMenu editMenu;
  protected JMenuItem saveAsItem;
  protected DataTable table;

  /**
   *  TableFrame Constructor
   *
   * @param  _table  Description of the Parameter
   */
  public DataTableFrame(DataTable _table) {
    this("Data Table", _table);
  }

  /**
   *  TableFrame Constructor
   *
   * @param  title
   * @param  _table  Description of the Parameter
   */
  public DataTableFrame(String title, DataTable _table) {
    super(title);
    table = _table;
    JScrollPane scrollPane = new JScrollPane(table);
    Container c = getContentPane();
    c.add(scrollPane, BorderLayout.CENTER);
    // table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    pack();
    // setVisible(true);
    if(!OSPFrame.appletMode) {
      createMenuBar();
    }
  }

  private void createMenuBar() {
    menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    fileMenu = new JMenu("File");
    editMenu = new JMenu("Edit");
    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    JMenuItem saveAsItem = new JMenuItem("Save As...");
    JMenuItem copyItem = new JMenuItem("Copy");
    JMenuItem selectAlItem = new JMenuItem("Select All");
    fileMenu.add(saveAsItem);
    editMenu.add(copyItem);
    editMenu.add(selectAlItem);
    copyItem.setAccelerator(KeyStroke.getKeyStroke('C', DrawingFrame.MENU_SHORTCUT_KEY_MASK));
    copyItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        copy();
      }
    });
    selectAlItem.setAccelerator(KeyStroke.getKeyStroke('A', DrawingFrame.MENU_SHORTCUT_KEY_MASK));
    selectAlItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        table.selectAll();
      }
    });
    saveAsItem.setAccelerator(KeyStroke.getKeyStroke('S', DrawingFrame.MENU_SHORTCUT_KEY_MASK));
    saveAsItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveAs();
      }
    });
    validate();
  }

  /** Copies the data in the table to the system clipboard */
  public void copy() {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    int[] selectedRows = table.getSelectedRows();
    int[] selectedColumns = table.getSelectedColumns();
    StringBuffer buf = getSelectedData(selectedRows, selectedColumns);
    StringSelection stringSelection = new StringSelection(buf.toString());
    clipboard.setContents(stringSelection, stringSelection);
  }

  /**
   *  Refresh the data in the DataTable, as well as other changes to the table,
   *  such as row number visibility. Changes to the TableModels displayed in the
   *  table will not be visible until this method is called.
   */
  public void refreshTable() {
    table.refreshTable();
  }

  /**
   *  Gets the data selected by the user in the table.
   *
   * @param  selectedRows     Description of the Parameter
   * @param  selectedColumns  Description of the Parameter
   * @return                  the selected data.
   */
  public StringBuffer getSelectedData(int[] selectedRows, int[] selectedColumns) {
    StringBuffer buf = new StringBuffer();
    for(int i = 0;i<selectedRows.length;i++) {
      for(int j = 0;j<selectedColumns.length;j++) {
        int row = i;
        int temp = table.convertColumnIndexToModel(selectedColumns[j]);
        if(table.isRowNumberVisible()) {
          if(temp==0) {
            continue;
          }
        }
        Object value = table.getValueAt(row, selectedColumns[j]); // column converted to model
        if(value!=null) {
          buf.append(value);
        }
        buf.append("\t");
      }
      buf.append("\n");
    }
    return buf;
  }

  /**
 * Sorts  the table using the given column.
 * @param col int
 */
  public void sort(int col) {
    table.sort(col);
  }

  /**
   *  Pops open a save file dialog to save the data in this table to a file.
   */
  public void saveAs() {
    File file = GUIUtils.showSaveDialog(this);
    if(file==null) {
      return;
    }
    int firstRow = 0;
    int lastRow = table.getRowCount()-1;
    int lastColumn = table.getColumnCount()-1;
    int firstColumn = 0;
    if(table.isRowNumberVisible()) {
      firstColumn++;
    }
    int[] selectedRows = new int[lastRow+1];
    int[] selectedColumns = new int[lastColumn+1];
    for(int i = firstRow;i<=lastRow;i++) {
      selectedRows[i] = i;
    }
    for(int i = firstColumn;i<=lastColumn;i++) {
      selectedColumns[i] = i;
    }
    try {
      FileWriter fw = new FileWriter(file);
      PrintWriter pw = pw = new PrintWriter(fw);
      pw.print(getSelectedData(selectedRows, selectedColumns));
      pw.close();
    } catch(IOException e) {
      JOptionPane.showMessageDialog(this, "An error occurred while saving your file. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
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
