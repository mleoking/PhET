package edu.colorado.phet.cck3;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Jul 10, 2006
 * Time: 3:19:44 PM
 * Copyright (c) Jul 10, 2006 by Sam Reid
 */

public class MyPhetPNode extends PhetPNode {
    private PhetPCanvas pSwingCanvas;

    public MyPhetPNode( PhetPCanvas pSwingCanvas, PSwing pSwing ) {
        super( pSwing );
        this.pSwingCanvas = pSwingCanvas;
    }

    public void setVisible( boolean isVisible ) {
        if( isVisible != getVisible() ) {
            super.setVisible( isVisible );
            if( isVisible ) {
                pSwingCanvas.addScreenChild( this );
            }
            else {
                pSwingCanvas.removeScreenChild( this );
            }
        }
    }
}
