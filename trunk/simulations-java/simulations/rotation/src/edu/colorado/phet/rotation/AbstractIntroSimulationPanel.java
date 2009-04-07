package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.FullRepaintCanvas;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Dec 1, 2007 at 7:42:12 AM
 */
public class AbstractIntroSimulationPanel extends FullRepaintCanvas {
    private RotationPlayAreaNode playAreaNode;
    private PNode introSimControlPanelPSwing;
    private RotationPlatform rotationPlatform;

    public AbstractIntroSimulationPanel( final JComponent contentPane, ConstantDtClock clock, Module module ) {
        super( clock, module );
        addKeyListener( new PDebugKeyHandler() );
        requestFocus();
    }

    protected void init( RotationPlayAreaNode playAreaNode, PNode introSimControlPanelPSwing, RotationPlatform rotationPlatform ) {
        this.playAreaNode = playAreaNode;
        this.introSimControlPanelPSwing = introSimControlPanelPSwing;
        this.rotationPlatform = rotationPlatform;
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
        playAreaNode.setOffset( getWidth() *0.6, scale * getRotationPlatform().getRadius() + padY / 2 );
        introSimControlPanelPSwing.setOffset( 0, getHeight() - introSimControlPanelPSwing.getFullBounds().getHeight() );
    }

    protected RotationPlatform getRotationPlatform() {
        return rotationPlatform;
    }

}
