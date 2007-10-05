package edu.colorado.phet.rotation.torque;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.rotation.RotationLayout;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 5, 2007
 * Time: 1:51:54 PM
 */
public class IntroSimulationPanel extends PhetPCanvas {
    private IntroModule introModule;

    public IntroSimulationPanel( IntroModule introModule, JFrame parentFrame ) {
        this.introModule = introModule;
        TorqueSimPlayAreaNode playAreaNode = new TorqueSimPlayAreaNode( introModule.getTorqueModel(), introModule.getVectorViewModel(), introModule.getAngleUnitModel() );
        addScreenChild( playAreaNode );

        final RotationLayout layout = new RotationLayout( this, playAreaNode, new PNode(), new PNode(), playAreaNode.getPlatformNode(), playAreaNode.getOriginNode(), introModule.getTorqueModel().getRotationPlatform(),0.5 );
        layout.layout();
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                layout.layout();
            }
        } );
        introModule.getClock().addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                repaint( );
            }
        });
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new JPanel();
    }

}
