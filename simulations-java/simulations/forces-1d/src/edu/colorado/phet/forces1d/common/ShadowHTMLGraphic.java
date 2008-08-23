package edu.colorado.phet.forces1d.common;

import java.awt.*;

import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.HTMLGraphic;

/**
 * Class for rendering HTML Text.
 */
public class ShadowHTMLGraphic extends CompositePhetGraphic {
    private HTMLGraphic foregroundGraphic;
    private HTMLGraphic shadowGraphic;

    public ShadowHTMLGraphic( Component component, String html, Font font, Color color, int dx, int dy, Color shadowColor ) {
        super( component );
        shadowGraphic = new HTMLGraphic( component, font, html, shadowColor );
        shadowGraphic.setLocation( dx, dy );
        foregroundGraphic = new HTMLGraphic( component, font, html, color );
        addGraphic( shadowGraphic );
        addGraphic( foregroundGraphic );
    }

    public void setFont( Font font ) {
        shadowGraphic.setFont( font );
        foregroundGraphic.setFont( font );
    }

    public void setHTML( String html ) {
        shadowGraphic.setHtml( html );
        foregroundGraphic.setHtml( html );
    }

    public void setForeground( Color foreground ) {
        foregroundGraphic.setColor( foreground );
    }

    public void setBackground( Color background ) {
        shadowGraphic.setColor( background );
    }
}
