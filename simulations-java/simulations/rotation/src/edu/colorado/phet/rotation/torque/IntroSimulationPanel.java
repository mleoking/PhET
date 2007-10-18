package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.model.RotationPlatform;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 5, 2007
 * Time: 1:51:54 PM
 */
public class IntroSimulationPanel extends PhetPCanvas {
    private IntroModule introModule;
    private TorqueSimPlayAreaNode playAreaNode;

    public IntroSimulationPanel( IntroModule introModule, JFrame parentFrame ) {
        this.introModule = introModule;
        playAreaNode = new TorqueSimPlayAreaNode( introModule.getTorqueModel(), introModule.getVectorViewModel(), introModule.getAngleUnitModel() );
        addScreenChild( playAreaNode );

        updateLayout();
//        addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                updateLayout();
//            }
//        } );
        introModule.getClock().addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                repaint();
            }
        } );
    }

    protected void updateLayout() {

        int padX = 50;
        int padY = 50;

        playAreaNode.setScale( 1.0 );

        double sx = ( getWidth() - padX * 2 ) / ( playAreaNode.getPlatformNode().getFullBounds().getWidth() );
        double sy = ( getHeight() - padY * 2 ) / ( playAreaNode.getPlatformNode().getFullBounds().getHeight() );
        double scale = Math.min( sx, sy );
        if ( scale > 0 ) {
            playAreaNode.scale( scale );
        }
        playAreaNode.setOffset( scale * getRotationPlatform().getRadius() + padX / 2, scale * getRotationPlatform().getRadius() + padY / 2 );

    }

    private RotationPlatform getRotationPlatform() {
        return introModule.getTorqueModel().getRotationPlatform();
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new JPanel();
    }

}
