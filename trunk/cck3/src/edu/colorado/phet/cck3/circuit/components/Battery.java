/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:39 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class Battery extends CircuitComponent {
    public Battery( Point2D start, AbstractVector2D dir, double length, double height, KirkhoffListener kl ) {
        super( kl, start, dir, length, height );
        setVoltageDrop( 9.0 );
    }

    public void setVoltageDrop( double voltageDrop ) {
        super.setVoltageDrop( voltageDrop );
        super.fireKirkhoffChange();
    }
}
