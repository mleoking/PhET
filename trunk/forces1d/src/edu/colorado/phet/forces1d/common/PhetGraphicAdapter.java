/** Sam Reid*/
package edu.colorado.phet.forces1d.common;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 13, 2004
 * Time: 1:06:40 PM
 * Copyright (c) Nov 13, 2004 by Sam Reid
 */
public abstract class PhetGraphicAdapter extends PhetGraphic {
    private Graphic graphic;

    public PhetGraphicAdapter( Component component, Graphic delegate ) {
        super( component );
        this.graphic = delegate;
    }

    protected abstract Rectangle determineBounds();

    public void paint( Graphics2D g ) {
        graphic.paint( g );
    }
}
