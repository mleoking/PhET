// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.circuitconstructionkit.model.mna.MNAAdapter;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Developer dialog for CCK Settings.
 */
public class CCKDeveloperDialog extends JDialog {
    private final MNAAdapter solver;

    public CCKDeveloperDialog(PhetFrame phetFrame, CCKModule cckModule) {
        super(phetFrame, "CCK Developer Dialog", false);
        solver = cckModule.getCCKModel().getCircuitSolver();
        JPanel contentPane = new VerticalLayoutPanel();
        JLabel textArea = new JLabel("<html>These developer controls govern the behavior of the timestep subdivision algorithm, <br>" +
                "and subsequently the tradeoff between accuracy and speed in the CCK model.  Their effects will only be visible in <br>" +
                "a circuit which requires timestep subdivisions. Model parameters are are 10^ slider values.  See #2241</html>");
        contentPane.add(textArea);
        contentPane.add(new JSeparator());

        contentPane.add(new JTextArea("threshold for determining whether 2 states are similar enough;\n" +
                "any error less than errorThreshold will be tolerated"));
        final LinearValueControl errorThresholdControl = new LinearValueControl(-10, -1, Math.log10(solver.getErrorThreshold()), "error threshold EXP", "0.000", "");
        errorThresholdControl.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                solver.setErrorThreshold(Math.pow(10, errorThresholdControl.getValue()));
                displayValues();
            }
        });
        contentPane.add(errorThresholdControl);
        contentPane.add(new JSeparator());

        contentPane.add(new JTextArea("lowest possible value for DT, independent of how the error scales with reduced time step"));
        final LinearValueControl minDTControl = new LinearValueControl(-10, -1, Math.log10(solver.getMinDT()), "min dt EXP", "0.000", "");
        minDTControl.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                solver.setMinDT(Math.pow(10, minDTControl.getValue()));
            }
        });
        contentPane.add(minDTControl);
        setContentPane(contentPane);

        pack();
        SwingUtils.centerDialogInParent(this);
    }

    private void displayValues() {
        System.out.println("errorThreshold = " + solver.getErrorThreshold() + ", minDT = " + solver.getMinDT());
    }
}
