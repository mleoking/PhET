// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.components;

import java.awt.geom.Point2D;

import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:18:33 PM
 */
public class SeriesAmmeter extends CircuitComponent {
    public SeriesAmmeter( CircuitChangeListener kl, Point2D start, Vector2D dir, double length, double height ) {
        super( kl, start, dir, length, height );
    }

    public SeriesAmmeter( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height ) {
        super( kl, startJunction, endjJunction, length, height );
    }
}
