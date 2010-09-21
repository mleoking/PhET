package edu.colorado.phet.circuitconstructionkit.controls;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 16, 2006
 * Time: 10:15:13 AM
 */

public class ShowReadoutPanel extends JPanel {
    private JCheckBox showValues;
    private CCKModule module;

    public ShowReadoutPanel(final CCKModule module) {
        this.module = module;
        showValues = new JCheckBox(CCKResources.getString("CCK3ControlPanel.ShowValuesCheckBox"));
        add(showValues);

        showValues.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                module.setReadoutsVisible(showValues.isSelected());
            }
        });
    }
}
