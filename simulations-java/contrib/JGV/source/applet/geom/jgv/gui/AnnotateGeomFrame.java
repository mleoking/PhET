//
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
// @author Daeron Meyer
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package geom.jgv.gui;

import java.awt.*;
import java.awt.event.*;
import geom.geometry.Plane;
import geom.geometry.Point;
import geom.jgv.controller.JGVController;
import geom.jgv.event.PickListener;
import geom.jgv.model.Annotation;
import geom.jgv.model.PickSet;

public class AnnotateGeomFrame
    extends Frame
    implements ActionListener, PickListener {

  private TextField objectNameField;
  private Button okayButton, deleteButton;
  private JGVController controller;
  private PickSet picked = null;
  private Annotation edit = null;

  public AnnotateGeomFrame(JGVController controller) {
    super("Add/Edit Annotation");
    this.controller = controller;
    this.setLayout(new BorderLayout());
    Label loadLabel = new Label("Add/Edit Annotation");
    okayButton = new Button("Okay");
    deleteButton = new Button("Delete");
    objectNameField = new TextField(20);
    Panel centerPanel = new Panel();
    centerPanel.setLayout(new BorderLayout());
    Panel southPanel = new Panel();
    southPanel.setLayout(new BorderLayout());
    this.add(loadLabel, "North");
    centerPanel.add(objectNameField, "South");
    this.add(centerPanel, "Center");
    southPanel.add(okayButton, "East");
    southPanel.add(deleteButton, "West");
    this.add(southPanel, "South");
    okayButton.addActionListener(this);
    deleteButton.addActionListener(this);
    objectNameField.addActionListener(this);
  }

  public void actionPerformed(ActionEvent e) {
    String objName = objectNameField.getText();
    if (e.getSource().equals(deleteButton)) {
      if (edit != null) {
        controller.deleteAnnotation(edit);
        edit = null;
      }
    } else if (!objName.trim().equals("")) {
      if (edit == null) {
        Annotation annotation = new Annotation();
        annotation.color = new geom.jgv.util.RGB(255,255,255);
        annotation.label = objName.trim();
        Plane plane = picked.getPickedFace().getPlane();
        Point pickPoint = picked.getAnnotationPoint();
        if (pickPoint != null) {
          annotation.x = pickPoint.x;
          annotation.y = pickPoint.y;
          annotation.z = pickPoint.z;
          annotation.onNode = "foo";
          controller.addAnnotation(annotation, "foo");
        }
      } else {
        edit.label = objName.trim();
        picked.getPickedFace().label = objName.trim();
        controller.refreshDisplay();
        edit = null;
      }
    }
    this.setVisible(false);
  }

  public void pickReceived(PickSet picked, Component cam) {
    if (picked.getPickedPoint() != null) {
      this.picked = picked;
      this.setSize(350,150);
      this.show();
      this.setVisible(true);
      this.objectNameField.requestFocus();
      this.toFront();
      if (picked.getPickedFace().parent instanceof Annotation) {
        edit = (Annotation) picked.getPickedFace().parent;
        if (edit.label != null) {
          this.objectNameField.setText(edit.label);
        } else {
          this.objectNameField.setText("");
        }
      }
    }
  }

  public void setVisible(boolean visible) {
    objectNameField.setText("");
    super.setVisible(visible);
    objectNameField.requestFocus();
  }

}