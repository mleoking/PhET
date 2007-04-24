package edu.colorado.phet.cck;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Jul 10, 2006
 * Time: 3:19:44 PM
 *
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
