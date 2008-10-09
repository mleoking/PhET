//
// Copyright (C) 1998-2000 Geometry Technologies, Inc. <info@geomtech.com>
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
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

import geom.jgv.controller.JGVController;
import java.awt.*;
import java.awt.event.*;

public class LoadGeomFrame
    extends Frame
    implements ActionListener {

    private TextField objectNameField;
    private Button okayButton;
    private JGVController controller;

    public LoadGeomFrame(JGVController controller) {
        super("Load New Object");
        this.controller = controller;
        this.setLayout(new BorderLayout());
        Label loadLabel = new Label("Load New Object");
        okayButton = new Button("Okay");
        objectNameField = new TextField(20);
        Panel centerPanel = new Panel();
        centerPanel.setLayout(new BorderLayout());
        Panel southPanel = new Panel();
        southPanel.setLayout(new BorderLayout());
        this.add(loadLabel, "North");
        centerPanel.add(objectNameField, "South");
        this.add(centerPanel, "Center");
        southPanel.add(okayButton, "East");
        this.add(southPanel, "South");
        okayButton.addActionListener(this);
        objectNameField.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        String objName = objectNameField.getText();
        if (!objName.trim().equals("")) {
          controller.loadGeom(objectNameField.getText());
        }
        this.setVisible(false);
    }

    public void setVisible(boolean visible) {
        objectNameField.setText("");
        super.setVisible(visible);
        objectNameField.requestFocus();
    }
}