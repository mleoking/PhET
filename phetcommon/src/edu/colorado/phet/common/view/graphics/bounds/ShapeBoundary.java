/**
 * Class: ShapeBoundary
 * Package: edu.colorado.phet.common.examples.examples
 * Author: Another Guy
 * Date: Dec 18, 2003
 */
package edu.colorado.phet.common.view.graphics.bounds;

import java.awt.*;

/**
 * Wrap an Area around your Shape to make it mutable.
 */
public class ShapeBoundary implements Boundary {
    Shape shape;

    public ShapeBoundary( Shape shape ) {
        this.shape = shape;
    }

    public boolean contains( int x, int y ) {
        return shape.contains( x, y );
    }
}
