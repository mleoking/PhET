/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.opensourcephysics.display.*;
import org.opensourcephysics.numerics.*;

/**
 * A panel that displays and controls functional curve fits to a Dataset.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class DatasetCurveFitter extends JPanel {

  // instance fields
  Dataset dataset; // the Dataset to fit
  KnownFunction fit; // the Function to fit to the data
  FunctionDrawer drawer;
  Color color = Color.red;
  JCheckBox autofitCheckBox;
  String[] fitNames;
  JComboBox fitDropDown;
  JTextField equation;
  JToolBar toolbar = new JToolBar();
  JTable paramTable;
  TableCellRenderer cellRenderer;
  SpinCellEditor spinCellEditor; // uses number-crawler spinner
  Map namedFits = new HashMap();

  /**
   * Constructs a DatasetCurveFitter for the specified Dataset.
   *
   * @param data the dataset
   */
  public DatasetCurveFitter(Dataset data) {
    createGUI();
    setData(data);
  }

  /**
   * Gets the function drawer.
   *
   * @return the drawer
   */
  public Drawable getDrawer() {
    return drawer;
  }

  /**
   * Gets the data.
   *
   * @return the dataset
   */
  public Dataset getData() {
    return dataset;
  }

  /**
   * Sets the dataset.
   *
   * @param data the dataset
   */
  public void setData(Dataset data) {
    dataset = data;
    fit();
  }

  /**
   * Sets the color.
   *
   * @param newColor the color
   */
  public void setColor(Color newColor) {
    color = newColor;
    if (drawer != null) {
      drawer.setColor(newColor);
    }
  }

  /**
   * Fits the current function to the current data.
   */
  public void fit() {
    if (drawer == null) createFit();
    if (autofitCheckBox.isSelected()) {
      if (fit instanceof KnownPolynomial) {
        KnownPolynomial poly = (KnownPolynomial)fit;
        poly.fitData(dataset.getValidXPoints(), dataset.getValidYPoints());
      }
      drawer.functionChanged = true;
      paramTable.repaint();
    }
    firePropertyChange("fit", null, null);
  }

  // _______________________ protected & private methods __________________________

  /**
   * Creates the GUI.
   */
  protected void createGUI() {
    setLayout(new BorderLayout());
    // create autofit checkbox
    autofitCheckBox = new JCheckBox(DatasetRes.getString("Checkbox.Autofit.Label"), true);
    autofitCheckBox.setOpaque(false);
    autofitCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fit();
      }
    });
    fitNames = new String[] {
               DatasetRes.getString("Function.Poly1.Name"),
               DatasetRes.getString("Function.Poly2.Name"),
               DatasetRes.getString("Function.Poly.Name")+ " 3",
               DatasetRes.getString("Function.Poly.Name")+ " 4"};
    fitDropDown = new JComboBox(fitNames);
    fitDropDown.setMaximumSize(fitDropDown.getMinimumSize());
    fitDropDown.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        createFit();
      }
    });
    // create equation field
    equation = new JTextField();
    equation.setEditable(false);
    equation.setEnabled(true);
    equation.setBackground(Color.white);
    // create toolbar
    toolbar = new JToolBar();
    toolbar.setFloatable(false);
    add(toolbar, BorderLayout.NORTH);
    // create table
    cellRenderer = new ParamCellRenderer();
    spinCellEditor = new SpinCellEditor();
    paramTable = new ParamTable(new ParamTableModel());
    paramTable.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        // clear selection if pressed on the name column
        if (paramTable.getSelectedColumn() == 0) {
          paramTable.clearSelection();
        }
      }
    });
    add(new JScrollPane(paramTable), BorderLayout.CENTER);
  }

  /**
   * Creates a new fit and drawer.
   */
  protected void createFit() {
    String name = (String)fitDropDown.getSelectedItem();
    fit = (KnownFunction)namedFits.get(name);
    if (fit == null) {
      for (int i = 0; i < fitNames.length; i++) {
        if (fitNames[i].equals(name)) {
          switch (i) {
            case 1: // poly fit degree 2
              fit = new KnownPolynomial(new double[] {0, 0, 0});
              break;
            case 2: // poly fit degree 3
              fit = new KnownPolynomial(new double[] {0, 0, 0, 0});
              break;
            case 3: // poly fit degree 4
              fit = new KnownPolynomial(new double[] {0, 0, 0, 0, 0});
              break;
            default: // poly fit degree 1
              fit = new KnownPolynomial(new double[] {0, 0});
          }
          namedFits.put(name, fit);
        }
      }
    }
    // assemble display
    toolbar.removeAll();
    toolbar.add(fitDropDown);
    toolbar.addSeparator();
    toolbar.add(equation);
    toolbar.add(autofitCheckBox);
    FunctionDrawer prev = drawer;
    drawer = new FunctionDrawer(fit);
    drawer.setColor(color);
    paramTable.tableChanged(null);
    // construct equation string
    String depVar = dataset.getColumnName(1);
    String indepVar = dataset.getColumnName(0);
    equation.setText(depVar + " = " + fit.getEquation(indepVar));
    firePropertyChange("drawer", prev, drawer);
    fit();
  }

  // _______________________ inner classes __________________________

  /**
   * A table to display and edit parameters.
   */
  class ParamTable extends JTable {
    public ParamTable(ParamTableModel model) {
      super(model);
      setPreferredScrollableViewportSize(new Dimension(60, 50));
      setGridColor(Color.blue);
      JTableHeader header = getTableHeader();
      header.setForeground(Color.blue);
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
      return cellRenderer;
    }

    public TableCellEditor getCellEditor(int row, int column) {
      spinCellEditor.index = row;
      return spinCellEditor;
    }

  };

  /**
   * A class to provide model data for the parameters table.
   */
  class ParamTableModel extends AbstractTableModel {

    public String getColumnName(int col) {
      return col == 0? DatasetRes.getString("Table.Heading.Parameter"):
          DatasetRes.getString("Table.Heading.Value");
    }

    public int getRowCount() {
      return fit == null? 0: fit.getParameterCount();
    }

    public int getColumnCount() {
      return 2;
    }

    public Object getValueAt(int row, int col) {
      if (col == 0) return fit.getParameterName(row);
      return new Double(fit.getParameterValue(row));
    }

    public boolean isCellEditable(int row, int col) {
      return col != 0;
    }

    public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

  }

  /**
   * A cell renderer for the parameter table.
   */
  class ParamCellRenderer extends JLabel implements TableCellRenderer {
    Color lightBlue = new Color(204, 204, 255);
    Color lightGray = javax.swing.UIManager.getColor("Panel.background");
    Font labelFont = getFont();
    Font fieldFont = new JTextField().getFont();

    // Constructor
    public ParamCellRenderer() {
      super();
      setOpaque(true); // make background visible
      setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 2));
    }

    // Returns a label for the specified cell.
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
      setHorizontalAlignment(col == 0? SwingConstants.RIGHT: SwingConstants.LEFT);
      if(value instanceof String) { // parameter name string
        setFont(labelFont);
        setBackground(lightGray);
        setForeground(Color.black);
        setText(value.toString());
      }
      else { // Double value
        setFont(fieldFont);
        setBackground(isSelected? lightBlue: Color.white);
        setForeground(isSelected? Color.red: Color.black);
        Format format = spinCellEditor.field.format;
        setText(format.format(value));
      }
      return this;
    }
  }

  /**
   * A cell editor that uses a JSpinner with a number crawler model.
   */
  class SpinCellEditor extends AbstractCellEditor implements TableCellEditor {
    JPanel panel = new JPanel(new BorderLayout());
    SpinnerNumberCrawlerModel crawlerModel = new SpinnerNumberCrawlerModel(1);
    JSpinner spinner;
    NumberField field;
    int index;

    // Constructor.
    SpinCellEditor() {
      panel.setOpaque(false);
      spinner = new JSpinner(crawlerModel);
      spinner.setToolTipText(DatasetRes.getString("Table.Spinner.ToolTip"));
      spinner.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          autofitCheckBox.setSelected(false);
          double val = ((Double)spinner.getValue()).doubleValue();
          field.setValue(val);
          fit.setParameterValue(index, val);
          drawer.functionChanged = true;
          DatasetCurveFitter.this.firePropertyChange("fit", null, null);
        }
      });
      field = new NumberField(10);
      spinner.setEditor(field);
      field.addMouseListener(new MouseInputAdapter() {
        public void mousePressed(MouseEvent e) {
          int mask = MouseEvent.BUTTON3_DOWN_MASK;
          if (e.isPopupTrigger() ||
              (e.getModifiersEx() & mask) == mask ||
              (e.isControlDown())) {
            JPopupMenu popup = new JPopupMenu();
            // create popup menu and add view name items
            // inner popup menu listener class
            ActionListener listener = new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                // set the percent delta
                double percent = Double.parseDouble(e.getActionCommand());
                crawlerModel.setPercentDelta(percent);
                crawlerModel.refreshDelta();
             }
            };
            ButtonGroup buttonGroup = new ButtonGroup();
            JMenu choices = new JMenu(DatasetRes.getString("Menu.StepSize"));
            popup.add(choices);
            double percent = 10.0;
            for (int i = 0; i < 3; i++) {
              String val = String.valueOf(percent);
              JMenuItem item = new JRadioButtonMenuItem(val + "%");
              item.setActionCommand(val);
              item.addActionListener(listener);
              choices.add(item);
              buttonGroup.add(item);
              if (percent == crawlerModel.getPercentDelta()) {
                item.setSelected(true);
              }
              percent /= 10;
            }
            // show the popup
            popup.show(field, e.getX(), e.getY());
          }
        }
      });
      field.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          JComponent comp = (JComponent) e.getSource();
          if(e.getKeyCode()==KeyEvent.VK_ENTER) {
            spinner.setValue(new Double(field.getValue()));
            comp.setBackground(Color.white);
            crawlerModel.refreshDelta();
          }
          else {
            comp.setBackground(Color.yellow);
          }
        }
      });
      panel.add(spinner, BorderLayout.CENTER);
    }

    // Gets the component to be displayed while editing.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      spinner.setValue(value);
      crawlerModel.refreshDelta();
      return panel;
    }

    // Determines when editing starts.
    public boolean isCellEditable(EventObject e) {
      if(e instanceof MouseEvent) {
        MouseEvent me = (MouseEvent) e;
        if(me.getClickCount()==2) {
          return true;
        }
      } else if(e instanceof ActionEvent) {
        return true;
      }
      return false;
    }

    // Called when editing is completed.
    public Object getCellEditorValue() {
      if (field.getBackground() == Color.yellow) {
        fit.setParameterValue(index, field.getValue());
        drawer.functionChanged = true;
        DatasetCurveFitter.this.firePropertyChange("fit", null, null);
        field.setBackground(Color.white);
      }
      return null;
    }
  }

  /**
   * A number spinner model with a settable delta.
   */
  class SpinnerNumberCrawlerModel extends AbstractSpinnerModel {
    double val = 0;
    double delta;
    double percentDelta = 10;

    public SpinnerNumberCrawlerModel(double initialDelta) {
      delta = initialDelta;
    }

    public Object getValue() {
      return new Double(val);
    }

    public Object getNextValue() {
      return new Double(val + delta);
    }

    public Object getPreviousValue() {
      return new Double(val - delta);
    }

    public void setValue(Object value) {
      if (value != null) {
        val = ((Double)value).doubleValue();
        fireStateChanged();
      }
    }

    public void setPercentDelta(double percent) {
      percentDelta = percent;
    }

    public double getPercentDelta() {
      return percentDelta;
    }

    // refresh delta based on current value and percent
    public void refreshDelta() {
      if (val != 0) {
        delta = Math.abs(val * percentDelta / 100);
      }
    }

  }

  /**
   * A polynomial that implements KnownFunction.
   */
  class KnownPolynomial
      extends PolynomialLeastSquareFit implements KnownFunction {
    String[] names = {"a", "b", "c", "d", "e", "f"};

    KnownPolynomial(double[] xdata, double[] ydata, int degree) {
      super(xdata, ydata, degree);
    }

    KnownPolynomial(double[] coeffs) {
      super(coeffs);
    }

    /**
     * Gets the parameter count.
     * @return the number of parameters
     */
    public int getParameterCount() {
      return coefficients.length;
    }

    /**
     * Gets a parameter name.
     *
     * @param i the parameter index
     * @return the name of the parameter
     */
    public String getParameterName(int i) {
      return names[i];
    }

    /**
     * Gets a parameter value.
     *
     * @param i the parameter index
     * @return the value of the parameter
     */
    public double getParameterValue(int i) {
      return coefficients[coefficients.length-i-1];
    }

    /**
     * Sets a parameter value.
     *
     * @param i the parameter index
     * @param value the value
     */
    public void setParameterValue(int i, double value) {
      coefficients[coefficients.length-i-1] = value;
    }

    /**
     * Gets the equation.
     *
     * @param indepVarName the name of the independent variable
     * @return the equation
     */
    public String getEquation(String indepVarName) {
      StringBuffer eqn = new StringBuffer();
      int end = coefficients.length-1;
      for (int i = 0; i <=end; i++) {
        eqn.append(getParameterName(i));
        if (end-i > 0) {
          eqn.append("*");
          eqn.append(indepVarName);
          if (end-i > 1) {
            eqn.append("^");
            eqn.append(end - i);
          }
          eqn.append(" + ");
        }
      }
      return eqn.toString();
    }
  }

  /**
   * A JTextField that accepts only numbers.
   */
  class NumberField extends JTextField {

    // instance fields
    protected NumberFormat format = NumberFormat.getInstance();
    protected double prevValue;

    public NumberField(int columns) {
      super(columns);
      if (format instanceof DecimalFormat) {
        ((DecimalFormat)format).applyPattern("0.000E0");
      }
      setForeground(Color.black);
    }

    public double getValue() {
      if (getText().equals(format.format(prevValue)))
        return prevValue;
      double retValue;
      try {
        retValue = format.parse(getText()).doubleValue();
      }
      catch (ParseException e) {
        Toolkit.getDefaultToolkit().beep();
        setValue(prevValue);
        return prevValue;
      }
      return retValue;
    }

    public void setValue(double value) {
      if (!isVisible())
        return;
      setText(format.format(value));
      prevValue = value;
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
