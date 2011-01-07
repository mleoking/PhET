// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.greenhouse.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class Ice {
    private Shape shape = new Rectangle2D.Double( 0, 0, 3, 3 );

    public boolean contains( Point2D.Double location ) {
        return shape.contains( location );
    }

    public Shape getShape() {
        return shape;
    }
}
