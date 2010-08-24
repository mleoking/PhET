package edu.colorado.phet.circuitconstructionkit.model.components;

import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:18:33 PM
 */
public class SeriesAmmeter extends CircuitComponent {
    public SeriesAmmeter(CircuitChangeListener kl, Point2D start, AbstractVector2DInterface dir, double length, double height) {
        super(kl, start, dir, length, height);
    }

    public SeriesAmmeter(CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height) {
        super(kl, startJunction, endjJunction, length, height);
    }
}
