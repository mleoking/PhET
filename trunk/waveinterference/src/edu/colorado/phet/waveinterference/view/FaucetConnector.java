/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.phetcommon.VerticalPipeConnector;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 12:02:41 AM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */

public class FaucetConnector extends VerticalPipeConnector {
    public FaucetConnector( FaucetControlPanelPNode faucetControlPanelPNode, FaucetGraphic faucetGraphic ) {
        super( faucetControlPanelPNode, faucetGraphic.getImagePNode() );
    }
}
