/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display3d.core;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.NumberFormat;
import org.opensourcephysics.display3d.core.interaction.*;

/**
 * <p>Title: CameraInspector</p>
 * <p>Description: This class creates an inspector for the camera.
 * The inspector is provided in a javax.swing.JPanel, so that
 * it can be used in other applications. A convenience class
 * is  provided to create a JFrame that contains the inspector.
 * </p>
 *
 * @author Francisco Esquembre
 * @version July 2005
 * @see Camera
 */
public class CameraInspector extends JPanel implements InteractionListener {
  private DrawingPanel3D panel = null;
  private Camera camera = null;
  private NumberFormat format = new java.text.DecimalFormat("0.000");
  private JTextField xField, yField, zField;
  private JTextField focusxField, focusyField, focuszField;
  private JTextField azimuthField, altitudeField;
  private JTextField rotationField, distanceField;
  private JRadioButton perspectiveRB, noperspectiveRB, planarxyRB, planarxzRB, planaryzRB;

  /**
   * Creates a frame and, inside it, a CameraInspector.
   * @param panel DrawingPanel3D The drawing panel 3D with the inspected camera
   * @return JFrame
   */
  static public JFrame createFrame(DrawingPanel3D panel) {
    return new CameraInspectorFrame("Camera inspector", new CameraInspector(panel));
  }

  /**
   * Creates a JPanel with a CameraInspector
   * @param panel DrawingPanel3D The drawing panel 3D with the inspected camera
   */
  public CameraInspector(DrawingPanel3D panel) {
    this.panel = panel;
    this.camera = panel.getCamera();
    panel.addInteractionListener(this);
    ActionListener fieldListener = new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        String cmd = evt.getActionCommand();
        JTextField field = (JTextField) evt.getSource();
        double value = 0.0;
        try {
          value = format.parse(field.getText()).doubleValue();
        } catch(java.text.ParseException exc) {
          value = 0.0;
        }
        if(cmd.equals("x")) {
          camera.setXYZ(value, camera.getY(), camera.getZ());
        } else if(cmd.equals("y")) {
          camera.setXYZ(camera.getX(), value, camera.getZ());
        } else if(cmd.equals("z")) {
          camera.setXYZ(camera.getX(), camera.getY(), value);
        } else if(cmd.equals("focus x")) {
          camera.setFocusXYZ(value, camera.getFocusY(), camera.getFocusZ());
        } else if(cmd.equals("focus y")) {
          camera.setFocusXYZ(camera.getFocusX(), value, camera.getFocusZ());
        } else if(cmd.equals("focus z")) {
          camera.setFocusXYZ(camera.getFocusX(), camera.getFocusY(), value);
        } else if(cmd.equals("azimuth")) {
          camera.setAzimuth(value);
        } else if(cmd.equals("altitude")) {
          camera.setAltitude(value);
        } else if(cmd.equals("rotation")) {
          camera.setRotation(value);
        } else if(cmd.equals("screen")) {
          camera.setDistanceToScreen(value);
        }
        updateFields();
      }
    };
    ActionListener buttonListener = new ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        String cmd = evt.getActionCommand();
        if(cmd.equals("reset")) {
          camera.reset();
          updateFields();
        } else if(cmd.equals("perspective")) {
          camera.setProjectionMode(Camera.MODE_PERSPECTIVE);
        } else if(cmd.equals("no_perspective")) {
          camera.setProjectionMode(Camera.MODE_NO_PERSPECTIVE);
        } else if(cmd.equals("planarXY")) {
          camera.setProjectionMode(Camera.MODE_PLANAR_XY);
        } else if(cmd.equals("planarXZ")) {
          camera.setProjectionMode(Camera.MODE_PLANAR_XZ);
        } else if(cmd.equals("planarYZ")) {
          camera.setProjectionMode(Camera.MODE_PLANAR_YZ);
        }
      }
    };
    setLayout(new BorderLayout());
    JPanel projectionPanel = new JPanel(new GridLayout(2, 3));
    projectionPanel.setBorder(new TitledBorder("Projection mode"));
    ButtonGroup group = new ButtonGroup();
    perspectiveRB = new JRadioButton("Perspective");
    perspectiveRB.setActionCommand("perspective");
    perspectiveRB.addActionListener(buttonListener);
    projectionPanel.add(perspectiveRB);
    group.add(perspectiveRB);
    planarxyRB = new JRadioButton("Planar XY");
    planarxyRB.setActionCommand("planarXY");
    planarxyRB.addActionListener(buttonListener);
    projectionPanel.add(planarxyRB);
    group.add(planarxyRB);
    planaryzRB = new JRadioButton("Planar YZ");
    planaryzRB.setActionCommand("planarYZ");
    planaryzRB.addActionListener(buttonListener);
    projectionPanel.add(planaryzRB);
    group.add(planaryzRB);
    noperspectiveRB = new JRadioButton("No perspective");
    noperspectiveRB.setActionCommand("no_perspective");
    noperspectiveRB.addActionListener(buttonListener);
    projectionPanel.add(noperspectiveRB);
    group.add(noperspectiveRB);
    planarxzRB = new JRadioButton("Planar XZ");
    planarxzRB.setActionCommand("planarXZ");
    planarxzRB.addActionListener(buttonListener);
    projectionPanel.add(planarxzRB);
    group.add(planarxzRB);
    // JLabel topLabel = new JLabel("Projection mode");
    // topLabel.setHorizontalAlignment(JLabel.CENTER);
    // add(topLabel,BorderLayout.NORTH);
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.add(projectionPanel, BorderLayout.CENTER);
    add(projectionPanel, BorderLayout.NORTH);
    JPanel labelPanel = new JPanel(new GridLayout(0, 1));
    JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
    JPanel label2Panel = new JPanel(new GridLayout(0, 1));
    JPanel field2Panel = new JPanel(new GridLayout(0, 1));
    xField = createRow(labelPanel, fieldPanel, "X", fieldListener);
    yField = createRow(labelPanel, fieldPanel, "Y", fieldListener);
    zField = createRow(labelPanel, fieldPanel, "Z", fieldListener);
    focusxField = createRow(label2Panel, field2Panel, "Focus X", fieldListener);
    focusyField = createRow(label2Panel, field2Panel, "Focus Y", fieldListener);
    focuszField = createRow(label2Panel, field2Panel, "Focus Z", fieldListener);
    azimuthField = createRow(labelPanel, fieldPanel, "Azimuth", fieldListener);
    altitudeField = createRow(labelPanel, fieldPanel, "Altitude", fieldListener);
    // createRow (label2Panel, field2Panel, null,fieldListener); // emptyrow
    rotationField = createRow(label2Panel, field2Panel, "Rotation", fieldListener);
    distanceField = createRow(label2Panel, field2Panel, "Screen", fieldListener);
    // createRow (label2Panel, field2Panel, null,fieldListener); // emptyrow
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.add(labelPanel, BorderLayout.WEST);
    leftPanel.add(fieldPanel, BorderLayout.CENTER);
    JPanel rightPanel = new JPanel(new BorderLayout());
    rightPanel.add(label2Panel, BorderLayout.WEST);
    rightPanel.add(field2Panel, BorderLayout.CENTER);
    JPanel centerPanel = new JPanel(new GridLayout(1, 0));
    centerPanel.setBorder(new TitledBorder("Camera parameters"));
    centerPanel.add(leftPanel);
    centerPanel.add(rightPanel);
    add(centerPanel, BorderLayout.CENTER);
    JButton resetButton = new JButton("Reset camera");
    resetButton.setActionCommand("reset");
    resetButton.addActionListener(buttonListener);
    add(resetButton, BorderLayout.SOUTH);
    updateFields();
  }

  /**
   * Sets the format for the fields in the inspector
   * @param format NumberFormat
   */
  public void setFormat(NumberFormat format) {
    this.format = format;
  }

  /**
   * Public as result of the implementation. Not to be used directly by final users.
   * @param _event InteractionEvent
   */
  public void interactionPerformed(InteractionEvent _event) {
    if(_event.getSource()!=panel) {
      return;
    }
    if(_event.getInfo()!=null) {
      return; // It is not changing the camera
    }
    updateFields();
  }

  public void updateFields() {
    switch(camera.getProjectionMode()) {
    default :
    case Camera.MODE_PERSPECTIVE :
      perspectiveRB.setSelected(true);
      break;
    case Camera.MODE_NO_PERSPECTIVE :
      noperspectiveRB.setSelected(true);
      break;
    case Camera.MODE_PLANAR_XY :
      planarxyRB.setSelected(true);
      break;
    case Camera.MODE_PLANAR_XZ :
      planarxzRB.setSelected(true);
      break;
    case Camera.MODE_PLANAR_YZ :
      planaryzRB.setSelected(true);
      break;
    }
    xField.setText(format.format(camera.getX()));
    yField.setText(format.format(camera.getY()));
    zField.setText(format.format(camera.getZ()));
    focusxField.setText(format.format(camera.getFocusX()));
    focusyField.setText(format.format(camera.getFocusY()));
    focuszField.setText(format.format(camera.getFocusZ()));
    azimuthField.setText(format.format(camera.getAzimuth()));
    altitudeField.setText(format.format(camera.getAltitude()));
    rotationField.setText(format.format(camera.getRotation()));
    distanceField.setText(format.format(camera.getDistanceToScreen()));
  }

  static private JTextField createRow(JPanel labelParent, JPanel fieldParent, String labelText, ActionListener listener) {
    if(labelText==null) { // create an empty row
      labelParent.add(new JLabel());
      fieldParent.add(new JLabel());
      return null;
    }
    if(labelText.length()<14) {}
    JLabel label = new JLabel(labelText);
    label.setHorizontalAlignment(JLabel.CENTER);
    label.setBorder(new EmptyBorder(0, 3, 0, 3));
    JTextField field = new JTextField(4);
    field.setActionCommand(labelText.trim().toLowerCase());
    field.addActionListener(listener);
    labelParent.add(label);
    fieldParent.add(field);
    return field;
  }
}

class CameraInspectorFrame extends JFrame {
  private CameraInspector inspector;

  public CameraInspectorFrame(String title, CameraInspector anInspector) {
    super(title);
    inspector = anInspector;
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(inspector, BorderLayout.CENTER);
    pack();
  }

  public void setVisible(boolean vis) {
    super.setVisible(vis);
    if(vis) {
      inspector.updateFields();
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
