/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;
import java.awt.*;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.opensourcephysics.numerics.DoubleArray;
import org.opensourcephysics.numerics.IntegerArray;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

/**
 *  A Control that shows its parameters in a JTable.
 *  Custom bottons can be added.
 *
 * @author       Wolfgang Christian
 * @version 1.0
 */
public class OSPControl extends ControlFrame implements PropertyChangeListener {
  XMLControl control = new XMLControlElement();
  XMLTable table = new XMLTable(control);
  JScrollPane controlScrollPane = new JScrollPane(table);
  JTextArea messageTextArea;
  JLabel clearLabel;
  JSplitPane splitPane;
  private boolean lockValues = false;
  HashMap valueCache = new HashMap();
  static final Color PANEL_BACKGROUND = javax.swing.UIManager.getColor("Panel.background");

  /**
   *  Constructs an OSPControl.
   *
   * @param  _model
   */
  public OSPControl(Object _model) {
    super("OSP Control");
    model = _model;
    if(model!=null) {
      String name = model.getClass().getName();
      setTitle(name.substring(1+name.lastIndexOf("."))+" Control");
    }
    Font labelFont = new Font("Dialog", Font.BOLD, 12);
    JLabel inputLabel = new JLabel("Input Parameters", SwingConstants.CENTER);
    inputLabel.setFont(labelFont);
    messageTextArea = new JTextArea(5, 5);
    JScrollPane messageScrollPane = new JScrollPane(messageTextArea);
    // contains a view of the control
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(inputLabel, BorderLayout.NORTH);
    topPanel.add(controlScrollPane, BorderLayout.CENTER);
    buttonPanel.setVisible(true);
    topPanel.add(buttonPanel, BorderLayout.SOUTH); // buttons are added using addButton method.
    // clear panel acts like a button to clear the message area
    JPanel clearPanel = new JPanel(new BorderLayout());
    clearPanel.addMouseListener(new ClearMouseAdapter());
    clearLabel = new JLabel(" clear");
    clearLabel.setFont(new Font(clearLabel.getFont().getFamily(), Font.PLAIN, 9));
    clearLabel.setForeground(Color.black);
    clearPanel.add(clearLabel, BorderLayout.WEST);
    // contains the messages
    JPanel bottomPanel = new JPanel(new BorderLayout());
    JLabel messageLabel = new JLabel("Messages", SwingConstants.CENTER);
    messageLabel.setFont(labelFont);
    bottomPanel.add(messageLabel, BorderLayout.NORTH);
    bottomPanel.add(messageScrollPane, BorderLayout.CENTER);
    bottomPanel.add(clearPanel, BorderLayout.SOUTH);
    Container cp = getContentPane();
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
    splitPane.setOneTouchExpandable(true);
    cp.add(splitPane, BorderLayout.CENTER);
    messageTextArea.setEditable(false);
    controlScrollPane.setPreferredSize(new Dimension(350, 200));
    controlScrollPane.setMinimumSize(new Dimension(0, 50));
    messageScrollPane.setPreferredSize(new Dimension(350, 75));
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    setLocation((d.width-getSize().width)/2, (d.height-getSize().height)/2); // center the frame
    init();
    invalidate();
    pack();
  }

  /**
   * Listens for property change events from XMLTable.
   *
   * @param e the property change event
   */
  public void propertyChange(PropertyChangeEvent e) {
    // forward event to listeners
    firePropertyChange(e.getPropertyName(), e.getOldValue(), e.getNewValue());
  }

  /**
   * Initializes this control after all objects have been created.
   *
   * Override this method and change the default close operation if this control is used with an applet.
   */
  protected void init() {
    validate();
    pack();
    setVisible(true);
    splitPane.setDividerLocation(-1);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public Object getModel() {
    return model;
  }

  /**
   * Sets the location of the divider between the control table and the message panel.
   * @param loc int
   */
  public void setDividerLocation(int loc) {
    splitPane.setDividerLocation(loc);
  }

  /**
   * Sets the editable property of the given parameter so that it can not be changed from within the GUI.
   *
   * @param parameter String
   * @param editable boolean
   */
  public void setEditable(String parameter, boolean editable) {
    table.setEditable(parameter, editable);
  }

  /**
   * Locks the control's interface. Values sent to the control will not
   * update the display until the control is unlocked.
   *
   * @param lock boolean
   */
  public void setLockValues(boolean lock) {
    control.setLockValues(lock);
    lockValues = lock;
    if(!lockValues) {
      table.refresh();
    }
  }

  /**
   *  Creates a string representation of the control parameters.
   *
   * @return    the control parameters
   */
  public String toString() {
    return table.toString();
  }

  /**
   *  Adds a parameter to the input display.
   *
   * @param  par  the parameter name
   * @param  val  the initial parameter value
   */
  public void setValue(String par, Object val) {
    control.setValue(par, val);
    if(!lockValues) {
      table.refresh();
    }
  }

  /**
   *  Adds an initial boolean value of a parameter to the input display.
   *
   * @param  par  the parameter name
   * @param  val  the initial parameter value
   */
  public void setValue(String par, boolean val) {
    control.setValue(par, val);
  }

  /**
   *  Adds an initial value of a parameter to the input display.
   *
   * @param  par  the parameter name
   * @param  val  the initial parameter value
   */
  public void setValue(String par, double val) {
    setValue(par, Double.toString(val));
    if(!Double.isNaN(val)) {
      valueCache.put(par, new Double(val)); // store last good value
    }
  }

  /**
   *  Adds an initial value of a parameter to the input display.
   *
   * @param  par  the parameter name
   * @param  val  the initial parameter value
   */
  public void setValue(String par, int val) {
    setValue(par, Integer.toString(val));
    valueCache.put(par, new Double(val)); // store last good value
  }

  /**
   *  Removes a parameter from the table.
   *
   * @param  par  the parameter name
   */
  public void removeParameter(String par) {
    control.setValue(par, null);
  }

  /**
   *  Reads a parameter value from the input display.
   *
   * @param  par
   * @return      double the value of of the parameter
   */
  public double getDouble(String par) {
    String str = control.getString(par);
    Color color = (Color) table.cellColors.get(par);
    boolean editable = table.isEditable(par);
    try {
      double val = Double.parseDouble(str);
      if(editable&&color!=Color.WHITE) { // background is not correct so change it
        table.setBackgroundColor(par, Color.WHITE);
        table.refresh();
      } else if(!editable&&color!=PANEL_BACKGROUND) { // background is not correct so change it
        table.setBackgroundColor(par, PANEL_BACKGROUND);
        table.refresh();
      }
      valueCache.put(par, new Double(val));
      return val;
    } catch(NumberFormatException ex) {}
    double val = org.opensourcephysics.numerics.Util.evalMath(str);
    if(Double.isNaN(val)&&color!=Color.PINK) { // string is not a valid number
      table.setBackgroundColor(par, Color.PINK);
      table.refresh();
    } else if(editable&&color!=Color.WHITE) {  // background is not correct so change it
      table.setBackgroundColor(par, Color.WHITE);
      table.refresh();
    } else if(!editable&&color!=PANEL_BACKGROUND) { // background is not correct so change it
      table.setBackgroundColor(par, PANEL_BACKGROUND);
      table.refresh();
    }
    if(Double.isNaN(val)&&valueCache.containsKey(par)) {
      val = ((Double) valueCache.get(par)).doubleValue();
    } else {
      valueCache.put(par, new Double(val));
    }
    return val;
  }

  /**
   *  Reads a parameter value from the input display.
   *
   * @param  par
   * @return      int the value of of the parameter
   */
  public int getInt(String par) {
    String str = control.getString(par);
    Color color = (Color) table.cellColors.get(par);
    boolean editable = table.isEditable(par);
    try {
      int val = Integer.parseInt(par);
      if(editable&&color!=Color.WHITE) { // background is not correct so change it
        table.setBackgroundColor(par, Color.WHITE);
        table.refresh();
      } else if(!editable&&color!=PANEL_BACKGROUND) { // background is not correct so change it
        table.setBackgroundColor(par, PANEL_BACKGROUND);
        table.refresh();
      }
      valueCache.put(par, new Double(val));
      return val;
    } catch(NumberFormatException ex) {}
    try {
      int val = (int) Double.parseDouble(par);
      if(editable&&color!=Color.WHITE) { // background is not correct so change it
        table.setBackgroundColor(par, Color.WHITE);
        table.refresh();
      } else if(!editable&&color!=PANEL_BACKGROUND) { // background is not correct so change it
        table.setBackgroundColor(par, PANEL_BACKGROUND);
        table.refresh();
      }
      valueCache.put(par, new Double(val));
      return val;
    } catch(NumberFormatException ex) {}
    double dval = org.opensourcephysics.numerics.Util.evalMath(str);
    if(Double.isNaN(dval)&&color!=Color.PINK) {
      table.setBackgroundColor(par, Color.PINK);
      table.refresh();
      if(valueCache.containsKey(par)) {
        return(int) ((Double) valueCache.get(par)).doubleValue();
      } else {
        return 0;
      }
    }
    if(editable&&color!=Color.WHITE) { // background is not correct so change it
      table.setBackgroundColor(par, Color.WHITE);
      table.refresh();
    } else if(!editable&&color!=PANEL_BACKGROUND) { // background is not correct so change it
      table.setBackgroundColor(par, PANEL_BACKGROUND);
      table.refresh();
    }
    valueCache.put(par, new Double(dval));
    return(int) dval;
  }

  /**
   * Gets the object with the specified property name.
   * Throws an UnsupportedOperationException if the named object has not been stored.
   *
   * @param  par
   * @return the object
   */
  public Object getObject(String par) throws UnsupportedOperationException {
    return control.getObject(par);
  }

  /**
   *  Reads a parameter value from the input display.
   *
   * @param  par  the parameter name
   * @return      String the value of of the parameter
   */
  public String getString(String par) {
    return control.getString(par);
  }

  /**
   *  Reads a parameter value from the input display.
   *
   * @param  par  the parameter name
   * @return      the value of of the parameter
   */
  public boolean getBoolean(String par) {
    return control.getBoolean(par);
  }

  /**
   *  Reads the current property names.
   *
   * @return      the property names
   */
  public Collection getPropertyNames() {
    return control.getPropertyNames();
  }

  /**
   *  Adds a custom button to the control's frame.
   *
   * @param  methodName  the name of the method; the method has no parameters
   * @param  text        the button's text label
   * @return             the custom button
   */
  public JButton addButton(String methodName, String text) {
    return addButton(methodName, text, null, model);
  }

  /**
   *  Adds a custom button to the control's frame.
   *
   * @param  methodName   the name of the method; the method has no parameters
   * @param  text         the button's text label
   * @param  toolTipText  the button's tool tip text
   * @return              the custom button
   */
  public JButton addButton(String methodName, String text, String toolTipText) {
    return addButton(methodName, text, toolTipText, model);
  }

  /**
   *  Adds a ControlTableListener that invokes method in the control's model.
   *  The method in the model is invoked with the table's variable name passed as a
   *  parameter.
   *
   * @param  methodName   the name of the method; the method has no parameters
   */
  public void addControlTableListener(String methodName) {
    addControlTableListener(methodName, model);
  }

  /**
   *  Adds a ControlTableListener that invokes method in the given object.
   *  The method in the target is invoked with the table's variable name passed as a
   *  parameter.
   *
   * @param  methodName   the name of the method; the method has no parameters
   * @param  target       the target for the method
   */
  public void addControlTableListener(String methodName, final Object target) {
    Class[] parameters = new Class[] {String.class};
    try {
      final java.lang.reflect.Method m = target.getClass().getMethod(methodName, parameters);
      table.tableModel.addTableModelListener(new TableModelListener() {
        public void tableChanged(TableModelEvent e) {
          if(e.getType()!=TableModelEvent.UPDATE||e.getColumn()!=1||e.getFirstRow()<0) {
            return;
          }
          String name = table.getValueAt(e.getFirstRow(), 0).toString();
          Object[] args = {name};
          try {
            m.invoke(target, args);
          } catch(IllegalAccessException iae) {}
          catch(java.lang.reflect.InvocationTargetException ite) {}
        }
      });
    } catch(NoSuchMethodException nsme) {
      System.err.println("The method "+methodName+"() does not exist.");
    }
  }

  /**
   *  Prints a line of text in the message area.
   *
   * @param  s
   */
  public void println(String s) {
    print(s+"\n");
  }

  /**
   *  Prints a blank line in the message area.
   */
  public void println() {
    print("\n");
  }

  /**
   *  Prints text in the message area.
   *
   * @param  s
   */
  public void print(final String s) {
    if(SwingUtilities.isEventDispatchThread()||Thread.currentThread().getName().equals("main")) {
      messageTextArea.append(s);
      return;
    }
    Runnable doLater = new Runnable() {
      public void run() {
        messageTextArea.append(s);
      }
    };
    // Update from the event queue.
    java.awt.EventQueue.invokeLater(doLater);
  }

  /**
   *  Remove all text from the message area.
   */
  public void clearMessages() {
    if(SwingUtilities.isEventDispatchThread()||Thread.currentThread().getName().equals("main")) {
      messageTextArea.setText("");
      return;
    }
    Runnable doLater = new Runnable() {
      public void run() {
        messageTextArea.setText("");
      }
    };
    // Update from the event queue.
    java.awt.EventQueue.invokeLater(doLater);
  }

  /**
   *  Remove all text from the data input area.
   */
  public void clearValues() {
    control.clearValues();
  }

  /**
   *  A signal that a method has completed. A message is printed in the message area.
   *
   * @param  message
   */
  public void calculationDone(String message) {
    // not implemented
    println(message);
  }

  class ClearMouseAdapter extends java.awt.event.MouseAdapter {

    /**
     * Method mousePressed
     *
     * @param evt
     */
    public void mousePressed(java.awt.event.MouseEvent evt) {
      clearMessages();
    }

    /**
     * Method mouseEntered
     *
     * @param evt
     */
    public void mouseEntered(java.awt.event.MouseEvent evt) {
      clearLabel.setFont(new Font(clearLabel.getFont().getFamily(), Font.BOLD, 10));
      clearLabel.setText(" click here to clear messages");
    }

    /**
     * Method mouseExited
     *
     * @param evt
     */
    public void mouseExited(java.awt.event.MouseEvent evt) {
      clearLabel.setFont(new Font(clearLabel.getFont().getFamily(), Font.PLAIN, 9));
      clearLabel.setText(" clear");
    }
  }

  /**
   * Returns an XML.ObjectLoader to save and load data for this object.
   *
   * @return the object loader
   */
  public static XML.ObjectLoader getLoader() {
    return new OSPControlLoader();
  }

  /**
 * A class to save and load data for OSPControls.
 */
  static class OSPControlLoader implements XML.ObjectLoader {

    /**
     * Saves object data to an XMLControl.
     *
     * @param control the control to save to
     * @param obj the object to save
     */
    public void saveObject(XMLControl xmlControl, Object obj) {
      OSPControl ospControl = (OSPControl) obj;
      saveControlProperites(xmlControl, ospControl);
      // save the model if the control is the top level
      if(xmlControl.getLevel()==0) {
        xmlControl.setValue("model", ospControl.model);
      }
    }

    protected void saveControlProperites(XMLControl xmlControl, OSPControl ospControl) {
      // save the parameters
      Iterator it = ospControl.getPropertyNames().iterator();
      while(it.hasNext()) {
        String name = (String) it.next();
        Object val = ospControl.getObject(name);
        if(val.getClass()==DoubleArray.class) {
          xmlControl.setValue(name, ((DoubleArray) val).getArray());
        } else if(val.getClass()==IntegerArray.class) {
          xmlControl.setValue(name, ((IntegerArray) val).getArray());
        } else if(val.getClass()==Boolean.class) {
          xmlControl.setValue(name, ((Boolean) val).booleanValue());
        } else if(val.getClass()==Double.class) {
          xmlControl.setValue(name, ((Double) val).doubleValue());
        } else if(val.getClass()==Integer.class) {
          xmlControl.setValue(name, ((Integer) val).intValue());
        } else if(val.getClass().isArray()) {
          xmlControl.setValue(name, val);
        } else {
          xmlControl.setValue(name, val);
        }
        // xmlControl.setValue(name, val.toString());
      }
    }

    /**
     * Creates an object using data from an XMLControl.
     *
     * @param control the control
     * @return the newly created object
     */
    public Object createObject(XMLControl control) {
      return new OSPControl(null);
    }

    /**
     * Loads an object with data from an XMLControl.
     *
     * @param control the control
     * @param obj the object
     * @return the loaded object
     */
    public Object loadObject(XMLControl control, Object obj) {
      OSPControl cf = (OSPControl) obj;
      // iterate over properties and add them to table model
      Iterator it = control.getPropertyNames().iterator();
      cf.control.setLockValues(true);
      while(it.hasNext()) {
        String name = (String) it.next();
        if(control.getPropertyType(name).equals("string")) {
          cf.setValue(name, control.getString(name));
        } else if(control.getPropertyType(name).equals("int")) {
          cf.setValue(name, control.getInt(name));
        } else if(control.getPropertyType(name).equals("double")) {
          cf.setValue(name, control.getDouble(name));
        } else if(control.getPropertyType(name).equals("boolean")) {
          cf.setValue(name, control.getBoolean(name));
        } else {
          cf.setValue(name, control.getObject(name));
        }
      }
      cf.control.setLockValues(false);
      XMLControl child = control.getChildControl("model");
      if(child!=null) {
        cf.model = child.loadObject(cf.model);
      }
      return obj;
    }
  }

  /**
   * Creates an OSP control and establishes communication between the control and the model.
   *
   * Custom buttons are usually added to this control to invoke actions in the model.
   *
   * @param model Object
   * @return AnimationControl
   */
  public static OSPControl createApp(Object model) {
    OSPControl control = new OSPControl(model);
    return control;
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
