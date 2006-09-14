package edu.colorado.phet.cck3.phetgraphics_cck;

import edu.colorado.phet.common_cck.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetGraphic;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 29, 2004
 * Time: 3:31:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class CCKCompositePhetGraphic extends CompositePhetGraphic {
    public CCKCompositePhetGraphic( Component component ) {
        super( component );
        super.visible = false;
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        for( int i = 0; i < numGraphics(); i++ ) {
            PhetGraphic graphic = graphicAt( i );
            graphic.setVisible( visible );
        }
    }
}
