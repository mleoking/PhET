package edu.colorado.phet.semiconductor.macro.energyprobe;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.simpleobservable.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Feb 2, 2004
 * Time: 8:22:50 PM
 * Copyright (c) Feb 2, 2004 by Sam Reid
 */
public class Lead extends SimpleObservable {
    PhetVector tip;

    public Lead( PhetVector tip ) {
        this.tip = tip;
        translate( 0, 0 );
    }

    public PhetVector getTipLocation() {
        return tip;
    }

    public void translate( double x, int y ) {
        tip = new PhetVector( tip.getX() + x, tip.getY() + y );
        updateObservers();
    }

}
