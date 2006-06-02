/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.piccolo.help.HintNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 1, 2006
 * Time: 11:34:47 PM
 * Copyright (c) Jun 1, 2006 by Sam Reid
 */

public class WiggleMeInSpace {
    private EC3Module module;
    private HintNode hintNode;

    public WiggleMeInSpace( final EC3Module module ) {
        this.module = module;
        hintNode = new HintNode( "<html>Press the Arrow Keys<br>to apply thrust!<html>" );
        hintNode.setColor( Color.white );
        hintNode.setShadowColor( Color.darkGray );
        hintNode.setShadowOffset( 1, 1 );
        module.getEnergyConservationModel().addEnergyModelListener( new EnergyConservationModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                super.gravityChanged();
                if( module.getEnergyConservationModel().getGravity() == 0.0 ) {
                    startHint();
                    module.getEnergyConservationModel().removeEnergyModelListener( this );
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

    private void startHint() {
        module.getEnergyConservationCanvas().requestFocus();
        EC3RootNode root = module.getEnergyConservationCanvas().getRootNode();
        root.addScreenChild( hintNode );
        hintNode.setOffset( module.getEnergyConservationCanvas().getWidth() / 2, hintNode.getFullBounds().getHeight() / 2 );
        hintNode.animateToLocation( module.getEnergyConservationCanvas().getWidth() / 2, (int)( module.getEnergyConservationCanvas().getHeight() * 1.0 / 4.0 ) );
        module.getEnergyConservationModel().bodyAt( 0 ).addListener( new Body.Listener() {
            public void thrustChanged() {
                hintNode.setVisible( false );
            }
        } );

    }

    public void start() {
    }
}
