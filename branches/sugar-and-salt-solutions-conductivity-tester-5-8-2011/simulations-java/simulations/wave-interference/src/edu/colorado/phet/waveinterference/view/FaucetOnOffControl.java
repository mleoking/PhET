// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.phetcommon.ShinyPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 11:31:42 PM
 */

public class FaucetOnOffControl extends PNode {
    public FaucetOnOffControl( FaucetGraphic faucetGraphic ) {
        FaucetOnOffControlPanel oscillatorOnOffControlPanel = new FaucetOnOffControlPanel( faucetGraphic );
        PSwing pSwing = new PSwing( new ShinyPanel( oscillatorOnOffControlPanel ) );
        addChild( pSwing );
    }
}
