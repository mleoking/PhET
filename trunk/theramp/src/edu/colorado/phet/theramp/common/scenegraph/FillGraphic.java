/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:14:50 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class FillGraphic extends AbstractGraphic {
    private Shape shape;

    public FillGraphic( Shape shape ) {
        this.shape = shape;
    }

    public void paint( Graphics2D graphics2D ) {
        super.setup( graphics2D );
        graphics2D.fill( shape );
        super.restore( graphics2D );
    }

    public boolean containsLocal( double x, double y ) {
        return shape.contains( x, y );
    }

    public Rectangle2D getLocalBounds() {
        return shape.getBounds2D();
    }

}
