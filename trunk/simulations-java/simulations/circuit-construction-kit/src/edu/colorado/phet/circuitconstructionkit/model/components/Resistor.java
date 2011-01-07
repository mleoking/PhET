// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.components;

import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:17 PM
 */
public class Resistor extends CircuitComponent {

    public Resistor(Point2D start, ImmutableVector2D dir, double length, double height, CircuitChangeListener kl) {
        super(kl, start, dir, length, height);
        setKirkhoffEnabled(false);
        setResistance(10);
        setKirkhoffEnabled(true);
    }

    public Resistor(CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height) {
        super(kl, startJunction, endjJunction, length, height);
    }

    public Resistor(double resistance) {
        this(new Point2D.Double(), new Vector2D(), 1, 1, new CircuitChangeListener() {
            public void circuitChanged() {
            }
        });
        setKirkhoffEnabled(false);
        setResistance(resistance);
        setKirkhoffEnabled(true);
    }

}
