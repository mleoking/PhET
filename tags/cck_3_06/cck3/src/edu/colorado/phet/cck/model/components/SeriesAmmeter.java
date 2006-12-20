/** Sam Reid*/
package edu.colorado.phet.cck.model.components;

import edu.colorado.phet.cck.model.CircuitChangeListener;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:18:33 PM
 * Copyright (c) Jun 14, 2004 by Sam Reid
 */
public class SeriesAmmeter extends CircuitComponent {
    public SeriesAmmeter( CircuitChangeListener kl, Point2D start, AbstractVector2D dir, double length, double height ) {
        super( kl, start, dir, length, height );
    }

    public SeriesAmmeter( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height ) {
        super( kl, startJunction, endjJunction, length, height );
    }
}
