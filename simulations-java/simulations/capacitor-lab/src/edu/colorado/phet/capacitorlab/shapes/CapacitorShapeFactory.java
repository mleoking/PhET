/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;

import edu.colorado.phet.capacitorlab.model.Capacitor;

/**
 * Creates 2D projections of shapes that are related to the 3D capacitor model.
 * All coordinates are in model world coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorShapeFactory {
    
    private final Capacitor capacitor;
    private final BoxShapeFactory boxShapeFactory;
    
    public CapacitorShapeFactory( Capacitor capacitor ) {
        this.capacitor = capacitor;
        this.boxShapeFactory = new BoxShapeFactory();
    }
    
    public Shape createTopPlateShape() {
        return null;//XXX
    }
    
    public Shape createBottomPlateShape() {
        return null;//XXX
    }
    
    public Shape createSpaceBetweenPlatesShape() {
        return null;//XXX
    }
}
