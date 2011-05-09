// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WirePopupMenu extends JPopupMenu {
    public WirePopupMenu(final CCKModel model, final Branch branch) {
        JMenuItem item = new JMenuItem(CCKResources.getString("InteractiveBranchGraphic.RemoveMenuItem"));
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.getCircuit().removeBranch(branch);
                model.getCircuit().removedUnusedJunctions(branch.getStartJunction());
                model.getCircuit().removedUnusedJunctions(branch.getEndJunction());
            }
        });
        add(item);
    }
}
