// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Simple model of a rectangle.
 * Origin is at the upper-left corner.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Rectangle {

    // mutable properties that can be directly observed
    public final Property<Double> x, y, width, height;
    public final Property<Color> fillColor, strokeColor;

    public Rectangle( double x, double y, double width, double height, Color fillColor, Color strokeColor ) {
        this.x = new Property<Double>( x );
        this.y = new Property<Double>( y );
        this.width = new Property<Double>( width );
        this.height = new Property<Double>( height );
        this.fillColor = new Property<Color>( fillColor );
        this.strokeColor = new Property<Color>( strokeColor );
    }

    public void reset() {
        x.reset();
        y.reset();
        width.reset();
        height.reset();
        fillColor.reset();
        strokeColor.reset();
    }
}
