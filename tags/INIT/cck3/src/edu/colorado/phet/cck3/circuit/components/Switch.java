/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 1:03:09 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class Switch extends CircuitComponent {
    boolean closed;
    double OPEN_RESISTANCE = Double.parseDouble( "10E10" );

    public Switch( Point2D start, AbstractVector2D dir, double length, double height, KirkhoffListener kl ) {
        super( kl, start, dir, length, height );
        super.setResistance( OPEN_RESISTANCE );
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed( boolean closed ) {
        if( closed != this.closed ) {
            this.closed = closed;
            if( closed ) {
                super.setResistance( .0001 ); //a resistance change fires a kirkhoff update.
            }
            else {
//                super.setResistance( Double.POSITIVE_INFINITY );
                super.setResistance( OPEN_RESISTANCE );
            }
//            fireKirkhoffChange();
            System.out.println( "switch.getResistance() = " + getResistance() );
        }
    }
}
