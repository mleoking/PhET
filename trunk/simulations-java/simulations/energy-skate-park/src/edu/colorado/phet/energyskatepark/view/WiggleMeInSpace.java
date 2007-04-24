/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 1, 2006
 * Time: 11:34:47 PM
 *
 */

public class WiggleMeInSpace {
    private EnergySkateParkModule module;
    private MotionHelpBalloon hintNode;
    private boolean hintDone = false;

    public WiggleMeInSpace( final EnergySkateParkModule module ) {
        this.module = module;
        hintNode = new MotionHelpBalloon( module.getDefaultHelpPane(), EnergySkateParkStrings.getString( "html.press.the.arrow.keys.br.to.apply.thrust.html" ) );
        hintNode.setTextColor( Color.white );
        hintNode.setShadowTextColor( Color.darkGray );
        hintNode.setShadowTextOffset( 1 );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                if( module.getEnergySkateParkModel().getGravity() == 0.0 && !hintDone ) {
                    startHint();
                }
                else {
                    closeHint();
                }
            }
        } );
        hintNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                hintNode.setVisible( false );
            }
        } );
    }

    private void closeHint() {
//        hintNode.setVisible( false );
        getRootNode().removeScreenChild( hintNode );
    }

    private void startHint() {
        module.getEnergyConservationCanvas().requestFocus();
        getRootNode().addScreenChild( hintNode );
        hintNode.setOffset( module.getEnergyConservationCanvas().getWidth() / 2, hintNode.getFullBounds().getHeight() / 2 );
        hintNode.animateTo( module.getEnergyConservationCanvas().getWidth() / 2, (int)( module.getEnergyConservationCanvas().getHeight() * 1.0 / 4.0 ) );
        module.getEnergySkateParkModel().getBody( 0 ).addListener( new Body.ListenerAdapter() {
            public void thrustChanged() {
                if( module.getEnergySkateParkModel().getBody( 0 ).getThrust().getMagnitude() > 0 ) {
                    hintNode.setVisible( false );
                    hintDone = true;
                }
            }
        } );
//        hintNode.st
    }

    private EnergySkateParkRootNode getRootNode() {
        return module.getEnergyConservationCanvas().getRootNode();
    }

    public void start() {
    }
}
