/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.beans.*;
import org.opensourcephysics.controls.XMLControl;
import org.opensourcephysics.controls.XMLProperty;

/**
 * A dialog that displays an array table.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class ArrayInspector extends JDialog implements PropertyChangeListener {
  // instance fields
  JTabbedPane tabbedPane = new JTabbedPane();
  ArrayTable[] tables;
  JSpinner spinner;
  JScrollPane scrollpane;
  Object array;
  boolean changed;

  public static ArrayInspector getInspector(XMLProperty arrayProp) {
    if(!arrayProp.getPropertyType().equals("array")) {
      return null;
    }
    // get base component type and depth
    Class type = arrayProp.getPropertyClass();
    while(type.getComponentType()!=null) {
      type = type.getComponentType();
    }
    if(type.getName().equals("double")||type.getName().equals("int")) { // node is double or int array
      String name = arrayProp.getPropertyName();
      XMLProperty parent = arrayProp.getParentProperty();
      while(!(parent instanceof XMLControl)) {
        name = parent.getPropertyName();
        arrayProp = parent;
        parent = parent.getParentProperty();
      }
      // get array depth
      type = arrayProp.getPropertyClass();
      int i = 0;
      while(type.getComponentType()!=null) {
        type = type.getComponentType();
        i++;
      }
      XMLControl arrayControl = (XMLControl) parent;
      Object arrayObj = arrayControl.getObject(name);
      if(arrayObj==null) {
        return null;
      }
      return getInspector(arrayObj, name);
    }
    return null;
  }

  public static boolean canInspect(XMLProperty arrayProp) {
    if(!arrayProp.getPropertyType().equals("array")) {
      return false;
    }
    String name = arrayProp.getPropertyName();
    XMLProperty parent = arrayProp.getParentProperty();
    while(!(parent instanceof XMLControl)) {
      name = parent.getPropertyName();
      arrayProp = parent;
      parent = parent.getParentProperty();
    }
    XMLControl arrayControl = (XMLControl) parent;
    Object arrayObj = arrayControl.getObject(name);
    return canInspect(arrayObj);
  }

  public static boolean canInspect(Object obj) {
    if(obj==null) {
      return false;
    }
    if(obj instanceof double[]||obj instanceof double[][]||obj instanceof double[][][]||obj instanceof int[]||obj instanceof int[][]||obj instanceof int[][][]) {
      return true;
    }
    return false;
  }

  public static ArrayInspector getInspector(Object arrayObj, String name) {
    ArrayInspector inspector = null;
    if(arrayObj instanceof double[]) {
      double[] array = (double[]) arrayObj;
      inspector = new org.opensourcephysics.tools.ArrayInspector(array, name);
    } else if(arrayObj instanceof double[][]) {
      double[][] array = (double[][]) arrayObj;
      inspector = new org.opensourcephysics.tools.ArrayInspector(array, name);
    } else if(arrayObj instanceof double[][][]) {
      double[][][] array = (double[][][]) arrayObj;
      inspector = new org.opensourcephysics.tools.ArrayInspector(array, name);
    } else if(arrayObj instanceof int[]) {
      int[] array = (int[]) arrayObj;
      inspector = new org.opensourcephysics.tools.ArrayInspector(array, name);
    } else if(arrayObj instanceof int[][]) {
      int[][] array = (int[][]) arrayObj;
      inspector = new org.opensourcephysics.tools.ArrayInspector(array, name);
    } else if(arrayObj instanceof int[][][]) {
      int[][][] array = (int[][][]) arrayObj;
      inspector = new org.opensourcephysics.tools.ArrayInspector(array, name);
    }
    if(inspector!=null) {
      inspector.array = arrayObj;
    }
    return inspector;
  }

  private ArrayInspector() {
    super((Frame) null, true); // modal dialog
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent e) {
        if(changed) {
          firePropertyChange("arrayData", null, null);
        }
      }
    });
  }

  private ArrayInspector(int[] array) {
    this();
    tables = new ArrayTable[1];
    tables[0] = new ArrayTable(array);
    tables[0].addPropertyChangeListener("cell", this);
    createGUI();
    setTitle("Array: int[row]");
  }

  private ArrayInspector(int[] array, String arrayName) {
    this(array);
    setTitle("Array \""+arrayName+"\": int[row]");
  }

  private ArrayInspector(int[][] array) {
    this();
    tables = new ArrayTable[1];
    tables[0] = new ArrayTable(array);
    tables[0].addPropertyChangeListener("cell", this);
    createGUI();
    setTitle("Array: int[row][column]");
  }

  private ArrayInspector(int[][] array, String arrayName) {
    this(array);
    setTitle("Array \""+arrayName+"\": int[row][column]");
  }

  private ArrayInspector(int[][][] array) {
    this();
    tables = new ArrayTable[array.length];
    for(int i = 0;i<tables.length;i++) {
      tables[i] = new ArrayTable(array[i]);
      tables[i].addPropertyChangeListener("cell", this);
    }
    createGUI();
    setTitle("Array: int[index][row][column]");
  }

  private ArrayInspector(int[][][] array, String arrayName) {
    this(array);
    setTitle("Array \""+arrayName+"\": int[index][row][column]");
  }

  private ArrayInspector(double[] array) {
    this();
    tables = new ArrayTable[1];
    tables[0] = new ArrayTable(array);
    tables[0].addPropertyChangeListener("cell", this);
    createGUI();
    setTitle("Array: double[row]");
  }

  private ArrayInspector(double[] array, String arrayName) {
    this(array);
    setTitle("Array \""+arrayName+"\": double[row]");
  }

  private ArrayInspector(double[][] array) {
    this();
    tables = new ArrayTable[1];
    tables[0] = new ArrayTable(array);
    tables[0].addPropertyChangeListener("cell", this);
    createGUI();
    setTitle("Array: double[row][column]");
  }

  private ArrayInspector(double[][] array, String arrayName) {
    this(array);
    setTitle("Array \""+arrayName+"\": double[row][column]");
  }

  private ArrayInspector(double[][][] array) {
    this();
    tables = new ArrayTable[array.length];
    for(int i = 0;i<tables.length;i++) {
      tables[i] = new ArrayTable(array[i]);
      tables[i].addPropertyChangeListener("cell", this);
    }
    createGUI();
    setTitle("Array: double[index][row][column]");
  }

  private ArrayInspector(double[][][] array, String arrayName) {
    this(array);
    setTitle("Array \""+arrayName+"\": double[index][row][column]");
  }

  public Object getArray() {
    return array;
  }

  /**
   * Listens for cell events (data changes) from ArrayTable.
   *
   * @param e the property change event
   */
  public void propertyChange(PropertyChangeEvent e) {
    // forward event to listeners
    changed = true;
    firePropertyChange(e.getPropertyName(), e.getOldValue(), e.getNewValue());
  }

  public void setEditable(boolean editable) {
    for(int i = 0;i<tables.length;i++) {
      tables[i].setEditable(editable);
    }
  }

  protected void createGUI() {
    setSize(400, 300);
    setContentPane(new JPanel(new BorderLayout()));
    scrollpane = new JScrollPane(tables[0]);
    if(tables.length>1) {
      // create spinner
      SpinnerModel model = new SpinnerNumberModel(0, 0, tables.length-1, 1);
      spinner = new JSpinner(model);
      JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner);
      editor.getTextField().setFont(tables[0].indexRenderer.getFont());
      spinner.setEditor(editor);
      spinner.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          int i = ((Integer) spinner.getValue()).intValue();
          scrollpane.setViewportView(tables[i]);
        }
      });
      Dimension dim = spinner.getMinimumSize();
      spinner.setMaximumSize(dim);
      getContentPane().add(scrollpane, BorderLayout.CENTER);
      JToolBar toolbar = new JToolBar();
      toolbar.setFloatable(false);
      toolbar.add(new JLabel(" index "));
      toolbar.add(spinner);
      toolbar.add(Box.createHorizontalGlue());
      getContentPane().add(toolbar, BorderLayout.NORTH);
    } else {
      scrollpane.createHorizontalScrollBar();
      getContentPane().add(scrollpane, BorderLayout.CENTER);
    }
  }

  public void refreshTable() {
    for(int i = 0;i<tables.length;i++) {
      tables[i].refreshTable();
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
