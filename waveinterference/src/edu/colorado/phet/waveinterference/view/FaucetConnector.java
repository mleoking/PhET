/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.phetcommon.VerticalConnector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 12:02:41 AM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */

public class FaucetConnector extends VerticalConnector {
    public FaucetConnector( FaucetControlPanelPNode faucetControlPanelPNode, FaucetGraphic target ) {
        super( faucetControlPanelPNode, target.getImagePNode() );
        target.addPropertyChangeListener( "fullBounds", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
        target.addPropertyChangeListener( "bounds", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
    }
}
