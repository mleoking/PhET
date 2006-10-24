/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display.dialogs;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.opensourcephysics.display.DrawingPanel;

/**
 * This displays and sets DrawingPanel scale properties.
 *
 * @author Douglas Brown
 * @version 1.0
 * 02 Aug 2003
 */
public class ScaleInspector extends JDialog {
  // instance fields
  protected DrawingPanel plotPanel;
  protected JPanel dataPanel;
  protected JLabel xMinLabel;
  protected JLabel xMaxLabel;
  protected JLabel yMinLabel;
  protected JLabel yMaxLabel;
  protected DecimalField xMinField;
  protected DecimalField xMaxField;
  protected DecimalField yMinField;
  protected DecimalField yMaxField;
  protected JCheckBox xAutoscaleCheckBox;
  protected JCheckBox yAutoscaleCheckBox;
  protected JButton okButton;
  protected JButton cancelButton;

  /**
   * Constructs a PanelInspector.
   *
   * @param panel the track plotting panel
   */
  public ScaleInspector(DrawingPanel panel) {
    super((Frame) null, true); // modal dialog with no owner
    plotPanel = panel;
    setTitle(DialogsRes.SCALE_SCALE);
    setResizable(false);
    createGUI();
    pack();
  }

  // _____________________________ private methods ____________________________

  /**
   * Creates the visible components for the clip.
   */
  private void createGUI() {
    JPanel inspectorPanel = new JPanel(new BorderLayout());
    setContentPane(inspectorPanel);
    JPanel controlPanel = new JPanel(new BorderLayout());
    inspectorPanel.add(controlPanel, BorderLayout.SOUTH);
    // create labels, fields and check boxes
    // xMin
    xMinLabel = new JLabel(DialogsRes.SCALE_MIN);
    xMinField = new DecimalField(5, 1);
    xMinField.setMaximumSize(xMinField.getPreferredSize());
    xMinField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        double xMax = plotPanel.getPreferredXMax();
        plotPanel.setPreferredMinMaxX(xMinField.getValue(), xMax);
        plotPanel.repaint();
        updateDisplay();
        xMinField.selectAll();
      }
    });
    xMinField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        xMinField.selectAll();
      }
      public void focusLost(FocusEvent e) {
        double xMax = plotPanel.getPreferredXMax();
        plotPanel.setPreferredMinMaxX(xMinField.getValue(), xMax);
        plotPanel.repaint();
        updateDisplay();
      }
    });
    xAutoscaleCheckBox = new JCheckBox(DialogsRes.SCALE_AUTO);
    xAutoscaleCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        xMinField.setEnabled(!xAutoscaleCheckBox.isSelected());
        plotPanel.setAutoscaleX(xAutoscaleCheckBox.isSelected());
        plotPanel.scale();
        updateDisplay();
        plotPanel.repaint();
      }
    });
    // xMax
    xMaxLabel = new JLabel(DialogsRes.SCALE_MAX);
    xMaxField = new DecimalField(5, 1);
    xMaxField.setMaximumSize(xMaxField.getPreferredSize());
    xMaxField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        double xMin = plotPanel.getPreferredXMin();
        plotPanel.setPreferredMinMaxX(xMin, xMaxField.getValue());
        plotPanel.repaint();
        updateDisplay();
        xMaxField.selectAll();
      }
    });
    xMaxField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        xMaxField.selectAll();
      }
      public void focusLost(FocusEvent e) {
        double xMin = plotPanel.getPreferredXMin();
        plotPanel.setPreferredMinMaxX(xMin, xMaxField.getValue());
        plotPanel.repaint();
        updateDisplay();
      }
    });
    // yMin
    yMinLabel = new JLabel(DialogsRes.SCALE_MIN);
    yMinField = new DecimalField(5, 1);
    yMinField.setMaximumSize(yMinField.getPreferredSize());
    yMinField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        double yMax = plotPanel.getPreferredYMax();
        plotPanel.setPreferredMinMaxY(yMinField.getValue(), yMax);
        plotPanel.repaint();
        updateDisplay();
        yMinField.selectAll();
      }
    });
    yMinField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        yMinField.selectAll();
      }
      public void focusLost(FocusEvent e) {
        double yMax = plotPanel.getPreferredYMax();
        plotPanel.setPreferredMinMaxY(yMinField.getValue(), yMax);
        plotPanel.repaint();
        updateDisplay();
      }
    });
    yAutoscaleCheckBox = new JCheckBox(DialogsRes.SCALE_AUTO);
    yAutoscaleCheckBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        yMinField.setEnabled(!yAutoscaleCheckBox.isSelected());
        plotPanel.setAutoscaleY(yAutoscaleCheckBox.isSelected());
        plotPanel.scale();
        updateDisplay();
        plotPanel.repaint();
      }
    });
    // yMax
    yMaxLabel = new JLabel(DialogsRes.SCALE_MAX);
    yMaxField = new DecimalField(5, 1);
    yMaxField.setMaximumSize(yMaxField.getPreferredSize());
    yMaxField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        double yMin = plotPanel.getPreferredYMin();
        plotPanel.setPreferredMinMaxY(yMin, yMaxField.getValue());
        plotPanel.repaint();
        updateDisplay();
        yMaxField.selectAll();
      }
    });
    yMaxField.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        yMaxField.selectAll();
      }
      public void focusLost(FocusEvent e) {
        double yMin = plotPanel.getPreferredYMin();
        plotPanel.setPreferredMinMaxY(yMin, yMaxField.getValue());
        plotPanel.repaint();
        updateDisplay();
      }
    });
    // create panels and add labels, fields and check boxes
    JPanel xPanel = new JPanel(new GridLayout(3, 1));
    String title = "x"; // plotPanel.getXLabel();
    xPanel.setBorder(BorderFactory.createTitledBorder(title));
    JPanel yPanel = new JPanel(new GridLayout(3, 1));
    title = "y"; // plotPanel.getYLabel();
    yPanel.setBorder(BorderFactory.createTitledBorder(title));
    dataPanel = new JPanel(new GridLayout(2, 1));
    dataPanel.setBorder(BorderFactory.createEtchedBorder());
    controlPanel.add(dataPanel, BorderLayout.CENTER);
    Box box;
    box = Box.createHorizontalBox();
    box.add(Box.createHorizontalGlue());
    box.add(xMinLabel);
    box.add(xMinField);
    xPanel.add(xAutoscaleCheckBox);
    xPanel.add(box);
    box = Box.createHorizontalBox();
    box.add(Box.createHorizontalGlue());
    box.add(xMaxLabel);
    box.add(xMaxField);
    xPanel.add(box);
    box = Box.createHorizontalBox();
    box.add(Box.createHorizontalGlue());
    box.add(yMinLabel);
    box.add(yMinField);
    yPanel.add(yAutoscaleCheckBox);
    yPanel.add(box);
    box = Box.createHorizontalBox();
    box.add(Box.createHorizontalGlue());
    box.add(yMaxLabel);
    box.add(yMaxField);
    yPanel.add(box);
    dataPanel.add(xPanel);
    dataPanel.add(yPanel);
    // set alignments
    xMinLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
    xMaxLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
    yMinLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
    yMaxLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
    xMinField.setAlignmentX(Component.RIGHT_ALIGNMENT);
    xMaxField.setAlignmentX(Component.RIGHT_ALIGNMENT);
    yMinField.setAlignmentX(Component.RIGHT_ALIGNMENT);
    yMaxField.setAlignmentX(Component.RIGHT_ALIGNMENT);
    xAutoscaleCheckBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
    yAutoscaleCheckBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
    // create cancel button
    cancelButton = new JButton(DialogsRes.SCALE_CANCEL);
    cancelButton.setForeground(new Color(0, 0, 102));
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        revert();
        setVisible(false);
      }
    });
    // create ok button
    okButton = new JButton(DialogsRes.SCALE_OK);
    okButton.setForeground(new Color(0, 0, 102));
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    // create buttonbar and add buttons
    // JPanel buttonbar = new JPanel(new GridLayout(1, 2));
    JPanel buttonbar = new JPanel();
    controlPanel.add(buttonbar, BorderLayout.SOUTH);
    buttonbar.add(okButton);
    // buttonbar.add(cancelButton);
  }

  /**
   * Updates this inpector to reflect the current settings.
   */
  public void updateDisplay() {
    xAutoscaleCheckBox.setSelected(plotPanel.isAutoscaleX());
    xMinField.setEnabled(!xAutoscaleCheckBox.isSelected());
    xMinField.setValue(plotPanel.getPreferredXMin());
    xMaxField.setValue(plotPanel.getPreferredXMax());
    yAutoscaleCheckBox.setSelected(plotPanel.isAutoscaleY());
    yMinField.setEnabled(!yAutoscaleCheckBox.isSelected());
    yMinField.setValue(plotPanel.getPreferredYMin());
    yMaxField.setValue(plotPanel.getPreferredYMax());
  }

  /**
   * Reverts to the previous clip settings.
   */
  private void revert() {}
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
