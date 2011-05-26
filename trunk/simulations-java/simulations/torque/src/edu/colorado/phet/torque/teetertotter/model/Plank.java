// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.geom.Rectangle2D;

/**
 * This is the pivot point where the teeter-totter is balanced.
 *
 * @author Sam Reid
 */
public class Plank extends ModelObject {

    public static final double LENGTH = 4;//meters
    public static final double HEIGHT = 0.05; //meters

    public Plank( double y ) {
        super( new Rectangle2D.Double( -LENGTH / 2, y, LENGTH, HEIGHT ) );
    }

    public double getLength() {
        return LENGTH;
    }
}
