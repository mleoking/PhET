/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;

import java.awt.*;

/**
 * Use this to position a PhetGraphic with respect to another PhetGraphic.
 */

public class TitleLayout {

    public static void layout( final PhetGraphic title, final PhetGraphic target ) {
        PhetGraphicListener phetGraphicListener = new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                target.setBoundsDirty();
                Rectangle targetBounds = target.getBounds();
                if( targetBounds != null ) {
                    int x = targetBounds.x;
                    int y = targetBounds.y - title.getHeight();
                    title.setLocation( x, y );
                }
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                title.setVisible( phetGraphic.isVisible() );
            }
        };
        target.addPhetGraphicListener( phetGraphicListener );
        phetGraphicListener.phetGraphicChanged( target );
        phetGraphicListener.phetGraphicVisibilityChanged( target );

    }
}
