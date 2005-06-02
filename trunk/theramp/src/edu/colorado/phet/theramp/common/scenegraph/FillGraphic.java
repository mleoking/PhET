/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;

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

    public boolean contains( double x, double y ) {
        return shape.contains( x, y );
    }

    public double getWidth() {
        return shape.getBounds2D().getWidth();
    }

    public double getHeight() {
        return shape.getBounds2D().getHeight();
    }

    public void setCursorHand() {
        addMouseListener( new CursorHand() );
    }
}
