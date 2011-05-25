// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * An object in the model for the torque sim.  The shape contains its location.
 *
 * @author Sam Reid
 */
public class ModelObject {
    public final Property<Shape> shape;

    public ModelObject( Shape shape ) {
        this.shape = new Property<Shape>( shape );
    }
}
