/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:18:33 PM
 * Copyright (c) Jun 14, 2004 by Sam Reid
 */
public class SeriesAmmeter extends CircuitComponent {
    public SeriesAmmeter( KirkhoffListener kl, Point2D start, AbstractVector2D dir, double length, double height ) {
        super( kl, start, dir, length, height );
    }
}
