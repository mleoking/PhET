/** Sam Reid*/
package edu.colorado.phet.cck3.model.components;

import edu.colorado.phet.cck3.model.CircuitChangeListener;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:33:56 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public abstract class CircuitComponent extends Branch {
    private double length;
    private double height;

    public CircuitComponent( CircuitChangeListener kl, Point2D start, AbstractVector2D dir, double length, double height ) {
        super( kl );
        this.length = length;
        this.height = height;
        Junction startJunction = new Junction( start.getX(), start.getY() );
        Point2D dest = dir.getInstanceOfMagnitude( length ).getDestination( start );
        Junction endJunction = new Junction( dest.getX(), dest.getY() );
        super.setStartJunction( startJunction );
        super.setEndJunction( endJunction );
    }

    protected CircuitComponent( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height ) {
        super( kl, startJunction, endjJunction );
        this.length = length;
        this.height = height;
    }

    protected void setLength( double length ) {
        this.length = length;
    }

    public double getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }

    public double getComponentLength() {
        return getLength();
    }

    public void setHeight( double height ) {
        this.height = height;
    }

}
