// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 12:23:20 AM
 */

public class CCKHelpSuite extends PhetPNode {
    private MotionHelpBalloon motionHelpBalloon;
    private CCKSimulationPanel cckSimulationPanel;
    private CCKModule module;
    private PhetPNode helpNode = new PhetPNode();

    public CCKHelpSuite(CCKSimulationPanel cckSimulationPanel, CCKModule module) {
        this.cckSimulationPanel = cckSimulationPanel;
        this.module = module;
        motionHelpBalloon = new MotionHelpBalloon(cckSimulationPanel, CCKStrings.getString("CCK3Module.GrabAWire"));
        motionHelpBalloon.setArrowVisible(true);
        motionHelpBalloon.setBalloonVisible(true);
        motionHelpBalloon.setFont(new PhetFont(Font.BOLD, 18));
        motionHelpBalloon.setArrowTailPosition(MotionHelpBalloon.RIGHT_CENTER);
        addChild(motionHelpBalloon);
        cckSimulationPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                motionHelpBalloon.setVisible(false);
            }
        });
        module.getCircuit().addCircuitListener(new CircuitListenerAdapter() {
            public void branchAdded(Branch branch) {
                motionHelpBalloon.setVisible(false);
            }
        });

        helpNode = new CCKHelpNode(cckSimulationPanel, module);
        addChild(helpNode);
        setHelpEnabled(false);
    }

    public void applicationStarted() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final PNode node = cckSimulationPanel.getWireMaker();
                motionHelpBalloon.animateTo(node);
            }
        });
    }

    public void setHelpEnabled(boolean enabled) {
        helpNode.setVisible(enabled);
    }
}
