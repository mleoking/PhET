/*, 2003.*/
package edu.colorado.phet.semiconductor.phetcommon.view;

import java.awt.*;

/**
 * Convenience class for common rendering options.
 */
public class BasicGraphicsSetup implements GraphicsSetup {
    RenderingHints renderingHints;

    public BasicGraphicsSetup() {
        this.renderingHints = new RenderingHints( null );
        setAntialias( true );
        setBicubicInterpolation();
    }

    public void setAntialias( boolean antialias ) {
        if ( antialias ) {
            renderingHints.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        else {
            renderingHints.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        }
    }

    public void setBicubicInterpolation() {
        renderingHints.put( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
    }

    public void setup( Graphics2D graphics ) {
        graphics.setRenderingHints( renderingHints );
    }

}
