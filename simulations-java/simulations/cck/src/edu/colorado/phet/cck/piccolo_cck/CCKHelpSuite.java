package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.common.CCKStrings;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Oct 5, 2006
 * Time: 12:23:20 AM
 */

public class CCKHelpSuite extends PhetPNode {
    private MotionHelpBalloon motionHelpBalloon;
    private CCKSimulationPanel cckSimulationPanel;
    private ICCKModule module;
    private PhetPNode helpNode = new PhetPNode();

    public CCKHelpSuite( CCKSimulationPanel cckSimulationPanel, ICCKModule module ) {
        this.cckSimulationPanel = cckSimulationPanel;
        this.module = module;
        motionHelpBalloon = new MotionHelpBalloon( cckSimulationPanel, CCKStrings.getString( "CCK3Module.GrabAWire" ) );
        motionHelpBalloon.setArrowVisible( true );
        motionHelpBalloon.setBalloonVisible( true );
        motionHelpBalloon.setFont( new PhetDefaultFont( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 18 ) );
        motionHelpBalloon.setArrowTailPosition( MotionHelpBalloon.RIGHT_CENTER );
        addChild( motionHelpBalloon );
        cckSimulationPanel.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                motionHelpBalloon.setVisible( false );
            }
        } );
        module.getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void branchAdded( Branch branch ) {
                motionHelpBalloon.setVisible( false );
            }
        } );

        helpNode = new CCKHelpNode( cckSimulationPanel, module );
        addChild( helpNode );
        setHelpEnabled( false );
    }

    public void applicationStarted() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                final PNode node = cckSimulationPanel.getWireMaker();
                motionHelpBalloon.animateTo( node );
            }
        } );
    }

    public void setHelpEnabled( boolean enabled ) {
        helpNode.setVisible( enabled );
    }
}
