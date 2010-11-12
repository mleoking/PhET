/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;

/**
 * Creates 2D projections of shapes that are related to the 3D capacitor model.
 * All Shapes are in the global view coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitorShapeFactory {
    
    private final Capacitor capacitor;
    private final ModelViewTransform mvt;
    private final BoxShapeFactory boxShapeFactory;
    
    public CapacitorShapeFactory( Capacitor capacitor, ModelViewTransform mvt ) {
        this.capacitor = capacitor;
        this.mvt = mvt;
        this.boxShapeFactory = new BoxShapeFactory( mvt );
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
