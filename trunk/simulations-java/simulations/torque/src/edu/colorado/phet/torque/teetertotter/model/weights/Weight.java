// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.torque.teetertotter.model.ModelObject;

/**
 * Base class for all objects that can be placed on the balance.
 *
 * @author John Blanco
 */
public abstract class Weight extends ModelObject {
    private final double mass;

    public Weight( Shape shape, double mass ) {
        super( shape );
        this.mass = mass;
    }

    public abstract void translate( ImmutableVector2D modelDelta );
}
