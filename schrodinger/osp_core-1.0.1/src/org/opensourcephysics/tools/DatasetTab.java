/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.beans.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.display.*;
import org.opensourcephysics.display.dialogs.ScaleInspector;

/**
 * This is a panel that serves as a DatasetTool tab.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class DatasetTab extends JPanel {

  // instance fields
  protected final Dataset original; // the original Dataset
  protected HighlightableDataset dataset = new HighlightableDataset();
  protected HighlightableDataset displayData = new HighlightableDataset();
  protected JSplitPane[] splitPanes;
  protected Plot plot;
  protected DatasetDataTable dataTable;
  protected DatasetStatisticsTable statsTable;
  protected DatasetCurveFitter curveFitter;
  protected String[] shapeNames;
  protected int[] shapeNumbers;
  protected JSpinner shapeSpinner, sizeSpinner;
  protected JCheckBox markerCheckBox, lineCheckBox;
  protected JCheckBox fitCheckBox, statsCheckBox;
  protected JButton markerColorButton, lineColorButton, fitColorButton;
  protected int buttonHeight = 28;
  protected SelectionBox selectionBox = new SelectionBox();
  protected Point zoomPoint;

  /**
   * Constructs a DatasetTab for the specified dataset.
   *
   * @param data the dataset
   */
  public DatasetTab(Dataset data) {
    original = data;
    XMLControl xml = new XMLControlElement(original);
    xml.loadObject(dataset);
    createGUI();
  }

  /**
   * Gets the dataset.
   *
   * @return the dataset
   */
  protected Dataset getDataset() {
    original.setMarkerShape(dataset.getMarkerShape());
    original.setMarkerSize(dataset.getMarkerSize());
    original.setConnected(dataset.isConnected());
    original.setLineColor(dataset.getLineColor());
    original.setMarkerColor(dataset.getFillColor(),
                           dataset.getEdgeColor());
    return original;
  }

  // _______________________ protected & private methods __________________________

  /**
   * Creates the GUI.
   */
  protected void createGUI() {
    setLayout(new BorderLayout());
    int paneCount = 3;
    splitPanes = new JSplitPane[paneCount];
    splitPanes[0] = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPanes[0].setResizeWeight(1);
    splitPanes[1] = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPanes[1].setResizeWeight(1);
    splitPanes[2] = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    // create shape data and action
    shapeNames = new String[] {
      DatasetRes.getString("Shape.Circle"),
      DatasetRes.getString("Shape.Square"),
      DatasetRes.getString("Shape.Pixel"),
      DatasetRes.getString("Shape.Bar"),
      DatasetRes.getString("Shape.Post"),
      DatasetRes.getString("Shape.Area")
    };
    shapeNumbers = new int[] {
      Dataset.CIRCLE, Dataset.SQUARE, Dataset.PIXEL, Dataset.BAR, Dataset.POST, Dataset.AREA
    };
    // create shape spinner
    SpinnerModel model = new SpinnerListModel(shapeNames);
    shapeSpinner = new JSpinner(model);
    shapeSpinner.setToolTipText(DatasetRes.getString("Spinner.MarkerShape.ToolTip"));
    Dimension dim = shapeSpinner.getPreferredSize();
    shapeSpinner.setMaximumSize(new Dimension(dim.width+20, 100));
    shapeSpinner.setPreferredSize(new Dimension(dim.width+10, dim.height));
    // set initial value before adding change listener
    int shape = dataset.getMarkerShape();
    for(int i = 0;i<shapeNumbers.length;i++) {
      if(shapeNumbers[i] == shape) {
        shapeSpinner.setValue(shapeNames[i]);
      }
    }
    shapeSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        String shape = shapeSpinner.getValue().toString();
        for(int i = 0;i<shapeNames.length;i++) {
          if(shapeNames[i].equals(shape)) {
            dataset.setMarkerShape(shapeNumbers[i]);
            refresh();
          }
        }
      }
    });
    // create size spinner
    SpinnerModel sizemodel = new SpinnerNumberModel(2, 1, 6, 1);
    sizeSpinner = new JSpinner(sizemodel);
    sizeSpinner.setToolTipText(DatasetRes.getString("Spinner.MarkerSize.ToolTip"));
    dim = sizeSpinner.getPreferredSize();
    sizeSpinner.setMaximumSize(new Dimension(dim.width, 100));
    sizeSpinner.setMinimumSize(new Dimension(dim.width, 100));
    // set initial value before adding change listener
    sizeSpinner.setValue(new Integer(dataset.getMarkerSize()));
    sizeSpinner.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int size = ((Integer)sizeSpinner.getValue()).intValue();
        dataset.setMarkerSize(size);
        refresh();
      }
    });
    // create marker checkbox
    markerCheckBox = new JCheckBox(DatasetRes.getString("Checkbox.Markers.Label"));
    boolean hasMarker = dataset.getMarkerShape() != Dataset.NO_MARKER;
    markerCheckBox.setToolTipText(DatasetRes.getString("Checkbox.Markers.ToolTip"));
    markerCheckBox.setSelected(hasMarker);
    markerCheckBox.setOpaque(false);
    markerCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (markerCheckBox.isSelected()) {
          String shape = shapeSpinner.getValue().toString();
          for(int i = 0;i<shapeNames.length;i++) {
            if(shapeNames[i].equals(shape)) {
              dataset.setMarkerShape(shapeNumbers[i]);
              break;
            }
          }
        }
        else {
          dataset.setMarkerShape(Dataset.NO_MARKER);
        }
        refresh();
      }
    });
    // create line checkbox
    lineCheckBox = new JCheckBox(DatasetRes.getString("Checkbox.Lines.Label"));
    lineCheckBox.setSelected(dataset.isConnected());
    lineCheckBox.setToolTipText(DatasetRes.getString("Checkbox.Lines.ToolTip"));
    lineCheckBox.setOpaque(false);
    lineCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dataset.setConnected(lineCheckBox.isSelected());
        refresh();
      }
    });
    // create color action
    final Action colorAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        String s = DatasetRes.getString("ColorChooser.Marker.Title");
        if(button==lineColorButton) s = DatasetRes.getString("ColorChooser.Line.Title");
        if(button==fitColorButton) s = DatasetRes.getString("ColorChooser.Fit.Title");
        Color color = JColorChooser.showDialog(null, s, button.getBackground());
        if(color!=null) {
          if(button==markerColorButton) {
            // give fill some transparency
            Color fill = new Color(color.getRed(), color.getGreen(),
                                   color.getBlue(), 100);
            Color edge = color;
            dataset.setMarkerColor(fill, edge);
          } else if(button==lineColorButton) {
            dataset.setLineColor(color);
          }
          else if (button == fitColorButton) {
            curveFitter.setColor(color);
          }
          refresh();
        }
      }
    };
    // create marker color button
    markerColorButton = createButton(DatasetRes.getString("Button.MarkerColor.Label"), buttonHeight);
    markerColorButton.setToolTipText(DatasetRes.getString("Button.MarkerColor.ToolTip"));
    markerColorButton.addActionListener(colorAction);
    // create line color button
    lineColorButton = createButton(DatasetRes.getString("Button.LineColor.Label"), buttonHeight);
    lineColorButton.setToolTipText(DatasetRes.getString("Button.LineColor.ToolTip"));
    lineColorButton.addActionListener(colorAction);
    // create fit color button
    fitColorButton = createButton(DatasetRes.getString("Button.FitColor.Label"), buttonHeight);
    fitColorButton.setToolTipText(DatasetRes.getString("Button.FitColor.ToolTip"));
    fitColorButton.addActionListener(colorAction);
    // create stats action
    Action statsAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        boolean vis = statsCheckBox.isSelected();
        splitPanes[2].setDividerLocation(vis? statsTable.getHeight() + 4: 0);
      }
    };
    // create stats checkbox
    statsCheckBox = new JCheckBox(DatasetRes.getString("Checkbox.Statistics.Label"), false);
    statsCheckBox.setOpaque(false);
    statsCheckBox.setToolTipText(DatasetRes.getString("Checkbox.Statistics.ToolTip"));
    statsCheckBox.addActionListener(statsAction);
    // create fit action
    Action fitAction = new AbstractAction() {
      public void actionPerformed(ActionEvent e) {
        // hide/remove curveFit
        splitPanes[1].setDividerSize(splitPanes[2].getDividerSize());
        splitPanes[1].setDividerLocation(1.0);
        plot.removeDrawables(FunctionDrawer.class);
        // restore if checked
        boolean vis = fitCheckBox.isSelected();
        splitPanes[1].setEnabled(vis);
        if (vis) {
          int max = splitPanes[1].getDividerLocation();
          int h = curveFitter.getPreferredSize().height;
          splitPanes[1].setDividerSize(splitPanes[0].getDividerSize());
          splitPanes[1].setDividerLocation(max - h);
          plot.addDrawable(curveFitter.getDrawer());
        }
        refresh();
      }
    };
    // create fit checkbox
    fitCheckBox = new JCheckBox(DatasetRes.getString("Checkbox.Fits.Label"), false);
    fitCheckBox.setOpaque(false);
    fitCheckBox.setToolTipText(DatasetRes.getString("Checkbox.Fits.ToolTip"));
    fitCheckBox.addActionListener(fitAction);
    // create plotting panel
    plot = new Plot(dataset);
    plot.addDrawable(dataset);
    plot.setTitle(dataset.getName());
    plot.addDrawable(selectionBox);
    MouseInputListener mouseSelector = new MouseInputAdapter() {
      ArrayList rowsInside = new ArrayList(); // points inside selectionBox
      public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        int mask = MouseEvent.BUTTON3_DOWN_MASK;
        boolean rightClick = e.isPopupTrigger() || (e.getModifiersEx() & mask) == mask ||
           (e.isControlDown() && System.getProperty("os.name", "").indexOf("Mac")>-1);
        selectionBox.xstart = p.x;
        selectionBox.ystart = p.y;
        rowsInside.clear();
        if(rightClick) {
          if (selectionBox.isZoomable()) {
            plot.zoomInItem.setText(DatasetRes.getString("MenuItem.ZoomToBox"));
          }
          else {
            zoomPoint = e.getPoint();
            plot.zoomInItem.setText(DatasetRes.getString("MenuItem.ZoomIn"));
          }
        }
        else {
          selectionBox.setSize(0, 0);
        }
        if (!(e.isControlDown() || e.isShiftDown() || rightClick)) {
          dataTable.clearSelection();
        }
      }
      public void mouseDragged(MouseEvent e) {
        int mask = MouseEvent.BUTTON3_DOWN_MASK;
        boolean rightButton = (e.getModifiersEx() & mask) == mask ||
           (e.isControlDown() && System.getProperty("os.name", "").indexOf("Mac")>-1);
        if (rightButton) return;
        Dataset data = dataTable.getData();
        Point mouse = e.getPoint();
        selectionBox.visible = true;
        selectionBox.setSize(mouse.x - selectionBox.xstart,
                             mouse.y - selectionBox.ystart);
        double[] xpoints = data.getXPoints();
        double[] ypoints = data.getYPoints();
        for(int i = 0;i<xpoints.length;i++) {
          double xp = plot.xToPix(xpoints[i]);
          double yp = plot.yToPix(ypoints[i]);
          Integer index = new Integer(i);
          if (selectionBox.contains(xp, yp)) {
            if (!rowsInside.contains(index)) { // needs to be added
              rowsInside.add(index);
              dataTable.getSelectionModel().addSelectionInterval(i, i);
            }
          }
          else if (rowsInside.contains(index)) { // needs to be removed
            dataTable.getSelectionModel().removeSelectionInterval(i, i);
            rowsInside.remove(index);
          }
        }
        plot.repaint();
      }
      public void mouseReleased(MouseEvent e) {
        plot.repaint();
      }
    };
    plot.addMouseListener(mouseSelector);
    plot.addMouseMotionListener(mouseSelector);
    // create toolbar
    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);
    toolbar.setBorder(BorderFactory.createEtchedBorder());
    toolbar.addSeparator(new Dimension(4, 4));
    toolbar.add(markerCheckBox);
    toolbar.add(markerColorButton);
    toolbar.add(shapeSpinner);
    toolbar.add(sizeSpinner);
    toolbar.addSeparator();
    toolbar.add(lineCheckBox);
    toolbar.add(lineColorButton);
    toolbar.addSeparator();
    toolbar.add(fitCheckBox);
    toolbar.add(fitColorButton);
    toolbar.addSeparator();
    // add glue to move components to right end of toolbar
    toolbar.add(Box.createHorizontalGlue());
    toolbar.add(statsCheckBox);
    // create curve fitter
    curveFitter = new DatasetCurveFitter(dataset);
    curveFitter.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("drawer")) {
          plot.removeDrawables(FunctionDrawer.class);
          plot.addDrawable((FunctionDrawer)e.getNewValue());
        }
        plot.repaint();
      }
    });
    // create data table
    dataTable = new DatasetDataTable(dataset);
    dataTable.setRowNumberVisible(true);
    dataTable.setColumnSelectionAllowed(false);
    JScrollPane dataScroller = new JScrollPane(dataTable);
    dataTable.refreshTable();
    dataTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
      public void columnAdded(TableColumnModelEvent e) {}
      public void columnRemoved(TableColumnModelEvent e) {}
      public void columnSelectionChanged(ListSelectionEvent e) {}
      public void columnMarginChanged(ChangeEvent e) {}
      public void columnMoved(TableColumnModelEvent e) {
        selectionBox.setSize(0, 0);
        refresh();
        // construct equation string
        String depVar = displayData.getColumnName(1);
        String indepVar = displayData.getColumnName(0);
        curveFitter.equation.setText(depVar + " = " +
                                     curveFitter.fit.getEquation(indepVar));
      }
    });
    dataTable.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        // clear selection if pressed on the row column
        if (dataTable.getSelectedColumn() == 0) {
          dataTable.removeRowSelectionInterval(0, dataTable.getRowCount()-1);
          dataTable.data.clearHighlights();
        }
      }
    });
    ListSelectionModel selectionModel = dataTable.getSelectionModel();
    selectionModel.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        try {
          curveFitter.setData(dataTable.getWorkingData());
        }
        catch (Exception ex) {
        }
      }
    });
    // create statistics table
    statsTable = new DatasetStatisticsTable(dataTable);
    JScrollPane statsScroller = new JScrollPane(statsTable);
    // assemble components
    add(toolbar, BorderLayout.NORTH);
    add(splitPanes[0], BorderLayout.CENTER);
    splitPanes[0].setLeftComponent(splitPanes[1]);
    splitPanes[0].setRightComponent(splitPanes[2]);
    splitPanes[1].setTopComponent(plot);
    splitPanes[1].setBottomComponent(curveFitter);
    splitPanes[2].setTopComponent(statsScroller);
    splitPanes[2].setBottomComponent(dataScroller);
    splitPanes[0].setOneTouchExpandable(true);
    splitPanes[2].setDividerSize(2);
    splitPanes[2].setEnabled(false);
    splitPanes[1].setDividerSize(splitPanes[2].getDividerSize());
  }

  /**
   * Initializes this panel.
   */
  protected void init() {
    splitPanes[0].setDividerLocation(0.7);
    splitPanes[1].setDividerLocation(1.0);
    splitPanes[2].setDividerLocation(0);
  }

  /**
   * Creates a button with a specified maximum height.
   *
   * @param text the button text
   * @param h the button height
   * @return the button
   */
  protected JButton createButton(String text, final int h) {
    JButton button = new JButton(text) {
      public Dimension getMaximumSize() {
        Dimension dim = super.getMaximumSize();
        dim.height = h;
        return dim;
      }
    };
    return button;
  }

  /**
   * Refreshes the display.
   */
  protected void refresh() {
    // refresh buttons
    markerColorButton.setForeground(dataset.getEdgeColor());
    markerColorButton.setEnabled(markerCheckBox.isSelected());
    shapeSpinner.setEnabled(markerCheckBox.isSelected());
    sizeSpinner.setEnabled(markerCheckBox.isSelected());
    lineColorButton.setForeground(dataset.getLineColor());
    lineColorButton.setEnabled(lineCheckBox.isSelected());
    fitColorButton.setForeground(curveFitter.color);
    fitColorButton.setEnabled(fitCheckBox.isSelected());
    // get Datatable data for curve fitting and plotting
    dataTable.dataset.setName(dataset.getName());
    curveFitter.setData(dataTable.getWorkingData());
    plot.removeDrawables(Dataset.class);
    displayData = dataTable.getData();
    plot.addDrawable(displayData);
    if (fitCheckBox.isSelected()) { // draw curve fit on top of dataset
      plot.removeDrawable(curveFitter.getDrawer());
      plot.addDrawable(curveFitter.getDrawer());
    }
    plot.setTitle(displayData.getName());
    plot.setXLabel(displayData.getColumnName(0));
    plot.setYLabel(displayData.getColumnName(1));
    repaint();
  }

  class SelectionBox extends Rectangle implements Drawable {
    boolean visible = true;
    int xstart, ystart;
    int zoomSize = 10;
    Color color = new Color(0, 255, 0, 127);
    public void setSize(int w, int h) {
      int xoffset = Math.min(0, w);
      int yoffset = Math.min(0, h);
      w = Math.abs(w);
      h = Math.abs(h);
      super.setLocation(xstart+xoffset, ystart+yoffset);
      super.setSize(w, h);
    }
    public void draw(DrawingPanel drawingPanel, Graphics g) {
      if (visible) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(color);
        g2.draw(this);
      }
    }
    public boolean isZoomable() {
      return (getBounds().width > zoomSize && getBounds().height > zoomSize);
    }
  }


  /**
   * Class to plot datasets. This overrides DrawingPanel zoom and popup methods.
   */
  class Plot extends PlottingPanel {

    JMenuItem zoomInItem;

    Plot(Dataset dataset) {
      super(dataset.getColumnName(0), dataset.getColumnName(1), "");
    }

    /**
     * Zooms out by a factor of two.
     */
    protected void zoomOut() {
      double dx = xmax-xmin;
      double dy = ymax-ymin;
      setPreferredMinMax(xmin-dx/2, xmax+dx/2, ymin-dy/2, ymax+dy/2);
      validImage = false;
      selectionBox.setSize(0, 0);
      repaint();
    }

    /**
     * Zooms in to the selection box.
     */
    protected void zoomIn() {
      int w = selectionBox.getBounds().width;
      int h = selectionBox.getBounds().height;
      if (selectionBox.isZoomable()) {
        int x = selectionBox.getBounds().x;
        int y = selectionBox.getBounds().y;
        double xmin = pixToX(x);
        double xmax = pixToX(x+w);
        double ymax = pixToY(y);
        double ymin = pixToY(y+h);
        setPreferredMinMax(xmin, xmax, ymin, ymax); // zoom both axes
        validImage = false;
        selectionBox.setSize(0, 0);
        repaint();
      }
      else if (zoomPoint != null) {
        double dx = xmax-xmin;
        double dy = ymax-ymin;
        double xcenter = pixToX(zoomPoint.x);
        double ycenter = pixToY(zoomPoint.y);
        setPreferredMinMax(xcenter-dx/4, xcenter+dx/4, ycenter-dy/4, ycenter+dy/4);
        validImage = false;
        selectionBox.setSize(0, 0);
        repaint();
      }
    }

    protected void buildPopupmenu() {
      popupmenu.setEnabled(true);
      // create zoom menu items
      zoomInItem = new JMenuItem(DatasetRes.getString("MenuItem.ZoomIn"));
      zoomInItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          plot.zoomIn();
        }
      });
      popupmenu.add(zoomInItem);
      JMenuItem item = new JMenuItem(DatasetRes.getString("MenuItem.ZoomOut"));
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          plot.zoomOut();
        }
      });
      popupmenu.add(item);
      item = new JMenuItem(DatasetRes.getString("MenuItem.ZoomToFit"));
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          plot.setAutoscaleX(true);
          plot.setAutoscaleY(true);
          selectionBox.setSize(0, 0);
          refresh();
        }
      });
      popupmenu.add(item);
      item = new JMenuItem(DatasetRes.getString("MenuItem.Scale"));
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          ScaleInspector plotInspector = new ScaleInspector(Plot.this);
          plotInspector.setLocationRelativeTo(Plot.this);
          plotInspector.updateDisplay();
          plotInspector.setVisible(true);
        }
      });
      popupmenu.add(item);
      popupmenu.addSeparator();
      item = new JMenuItem(DatasetRes.getString("MenuItem.Snapshot"));
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          snapshot();
        }
      });
      popupmenu.add(item);
      popupmenu.addSeparator();
      item = new JMenuItem(DatasetRes.getString("MenuItem.Inspect"));
      item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          showInspector();
        }
      });
      popupmenu.add(item);
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
