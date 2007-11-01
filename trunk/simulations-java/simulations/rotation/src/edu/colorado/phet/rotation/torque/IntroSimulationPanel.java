package edu.colorado.phet.rotation.torque;

import javax.swing.*;

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

    public IntroSimulationPanel( IntroModule introModule ) {
        this.introModule = introModule;
        playAreaNode = new TorqueSimPlayAreaNode( introModule.getTorqueModel(), introModule.getVectorViewModel(), introModule.getAngleUnitModel() );
        playAreaNode.setOriginNodeVisible( false );
        addScreenChild( playAreaNode );

        updateLayout();
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
        playAreaNode.setOffset( getWidth() / 2, scale * getRotationPlatform().getRadius() + padY / 2 );
    }

    private RotationPlatform getRotationPlatform() {
        return introModule.getTorqueModel().getRotationPlatform();
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new JPanel();
    }

}
