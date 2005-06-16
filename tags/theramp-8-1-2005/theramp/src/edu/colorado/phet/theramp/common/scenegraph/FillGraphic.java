/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:14:50 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class FillGraphic extends AbstractGraphic {
    private Shape shape;
    private boolean ensureNoBadPathException = true;

    public FillGraphic( Shape shape ) {
        this( shape, null );
    }

    public FillGraphic( Shape shape, Color color ) {
        super();
        this.shape = shape;
        setColor( color );
    }

    public void paint( Graphics2D graphics2D ) {
        super.setup( graphics2D );
        if( ensureNoBadPathException ) {
            Area myClip = new Area( graphics2D.getClip() );
            myClip.intersect( new Area( shape ) );
            graphics2D.fill( myClip );
        }
        else {
            graphics2D.fill( shape );
        }
        super.restore( graphics2D );
    }

    public boolean containsMousePointLocal( double x, double y ) {
        return isVisible() && shape.contains( x, y );
    }

    public Rectangle2D getLocalBounds() {
        return shape.getBounds2D();
    }

}
