/** Sam Reid*/
package edu.colorado.phet.cck3.model.components;

import edu.colorado.phet.cck3.model.CCKModel;
import edu.colorado.phet.cck3.model.CircuitChangeListener;
import edu.colorado.phet.cck3.model.Junction;
import edu.colorado.phet.common_cck.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 1:03:09 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class Switch extends CircuitComponent {
    boolean closed;
    public static final double OPEN_RESISTANCE = Double.parseDouble( "10E10" );

    public Switch( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl ) {
        super( kl, start, dir, length, height );
        setKirkhoffEnabled( false );
        super.setResistance( OPEN_RESISTANCE );
        setKirkhoffEnabled( true );
    }

    public Switch( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, boolean closed, double length, double height ) {
        super( kl, startJunction, endjJunction, length, height );
        setKirkhoffEnabled( false );
        this.closed = !closed;//to guarantee a change in setClosed.
        setClosed( closed );
        setKirkhoffEnabled( true );
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed( boolean closed ) {
        if( closed != this.closed ) {
            this.closed = closed;
            if( closed ) {
                super.setResistance( CCKModel.MIN_RESISTANCE ); //a resistance change fires a kirkhoff update.
            }
            else {
//                super.setResistance( Double.POSITIVE_INFINITY );
                super.setResistance( OPEN_RESISTANCE );
            }
//            fireKirkhoffChange();
//            System.out.println( "switch.getResistance() = " + getResistance() );
        }
    }
}
