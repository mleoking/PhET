package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;

import java.awt.*;

/**
 * Class for rendering HTML Text.
 */
public class ShadowHTMLGraphic extends CompositePhetGraphic {
    public ShadowHTMLGraphic( Component component, String html, Font font, Color color, int dx, int dy, Color shadowColor ) {
        super( component );
        HTMLGraphic shadowGraphic = new HTMLGraphic( component, font, html, shadowColor );
        shadowGraphic.setLocation( dx, dy );
        HTMLGraphic foregroundGraphic = new HTMLGraphic( component, font, html, color );
        addGraphic( shadowGraphic );
        addGraphic( foregroundGraphic );
    }
}
