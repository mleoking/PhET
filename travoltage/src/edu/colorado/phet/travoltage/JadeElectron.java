/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import org.cove.jade.primitives.CircleParticle;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 6:03:05 PM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */
public class JadeElectron extends CircleParticle {

    public JadeElectron( double x, double y, double radius ) {
        super( x, y, radius );
    }

    private ArrayList listeners = new ArrayList();

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
//    public void update() {
//        electronNode.setOffset( curr.x - electronNode.getFullBounds().getWidth() / 2, curr.y - electronNode.getFullBounds().getHeight() / 2 );
//    }
//

    public Point2D.Double getPosition() {
        return new Point2D.Double( curr.x, curr.y );
    }

//
//    public void setLocation( Point2D location ) {
//        super.setPos( location.getX(), location.getY() );
//    }
//
//    public ElectronNodeJade getElectronNode() {
//        return electronNode;
//    }


}
