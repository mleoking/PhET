/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.phetcommon.ShinyPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 11:31:42 PM
 * Copyright (c) Apr 12, 2006 by Sam Reid
 */

public class FaucetOnOffControl extends PNode {
    public FaucetOnOffControl( PSwingCanvas pSwingCanvas, FaucetGraphic faucetGraphic ) {
        FaucetOnOffControlPanel oscillatorOnOffControlPanel = new FaucetOnOffControlPanel( faucetGraphic );
        PSwing pSwing = new PSwing( pSwingCanvas, new ShinyPanel( oscillatorOnOffControlPanel ) );
        addChild( pSwing );
    }
}
