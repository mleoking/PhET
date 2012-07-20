// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.semiconductor.macro.energyprobe;


import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Feb 2, 2004
 * Time: 8:22:50 PM
 */
public class Lead extends SimpleObservable {
    MutableVector2D tip;

    public Lead( MutableVector2D tip ) {
        this.tip = tip;
        translate( 0, 0 );
    }

    public MutableVector2D getTipLocation() {
        return tip;
    }

    public void translate( double x, int y ) {
        tip = new MutableVector2D( tip.getX() + x, tip.getY() + y );
        notifyObservers();
    }

}
