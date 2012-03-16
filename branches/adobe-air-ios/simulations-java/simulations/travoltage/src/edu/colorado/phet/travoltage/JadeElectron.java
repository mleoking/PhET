// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import org.cove.jade.primitives.CircleParticle;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 6:03:05 PM
 */
public class JadeElectron extends CircleParticle {

    private ArrayList listeners = new ArrayList();

    public JadeElectron( double x, double y, double radius ) {
        super( x, y, radius );
    }

    public void setLocation( double x, double y ) {
        setPos( x, y );
        notifyElectronMoved();
    }

    public static interface Listener {
        void electronMoved();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyElectronMoved() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.electronMoved();
        }
    }

    public Point2D.Double getPosition() {
        return new Point2D.Double( curr.x, curr.y );
    }

}
