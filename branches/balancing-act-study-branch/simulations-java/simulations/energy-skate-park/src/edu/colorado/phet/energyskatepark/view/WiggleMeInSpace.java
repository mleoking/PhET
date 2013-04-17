// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import java.awt.Color;

import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.view.piccolo.EnergySkateParkRootNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * User: Sam Reid
 * Date: Jun 1, 2006
 * Time: 11:34:47 PM
 */

public class WiggleMeInSpace {
    private final AbstractEnergySkateParkModule module;
    private final MotionHelpBalloon hintNode;
    private boolean hintDone = false;

    public WiggleMeInSpace( final AbstractEnergySkateParkModule module ) {
        this.module = module;
        hintNode = new MotionHelpBalloon( module.getDefaultHelpPane(), EnergySkateParkResources.getString( "invitaiton.arrow-keys" ) );
        hintNode.setTextColor( Color.white );
        hintNode.setShadowTextColor( Color.darkGray );
        hintNode.setShadowTextOffset( 1 );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                if ( module.getEnergySkateParkModel().getGravity() == 0.0 && !hintDone ) {
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
        getRootNode().removeScreenChild( hintNode );
    }

    private void startHint() {
        module.getEnergySkateParkSimulationPanel().requestFocus();
        getRootNode().addScreenChild( hintNode );
        hintNode.setOffset( module.getEnergySkateParkSimulationPanel().getWidth() / 2, hintNode.getFullBounds().getHeight() / 2 );
        hintNode.animateTo( module.getEnergySkateParkSimulationPanel().getWidth() / 2, (int) ( module.getEnergySkateParkSimulationPanel().getHeight() * 1.0 / 4.0 ) );
        module.getEnergySkateParkModel().getBody( 0 ).addListener( new Body.ListenerAdapter() {
            public void thrustChanged() {
                if ( module.getEnergySkateParkModel().getBody( 0 ).getThrust().magnitude() > 0 ) {
                    hintNode.setVisible( false );
                    hintDone = true;
                }
            }
        } );
    }

    private EnergySkateParkRootNode getRootNode() {
        return module.getEnergySkateParkSimulationPanel().getRootNode();
    }

    public void start() {
    }
}
