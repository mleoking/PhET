/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:04:09 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class LegNode extends LimbNode {
    ArrayList angleHistory = new ArrayList();
    double angle = 0;
    private static final int MAX_HISTORY_SIZE = 20;

    public LegNode() {
        super( "images/leg2.gif", new Point( 30, 27 ) );
    }

    public void rotateAboutPivot( double dTheta ) {
        this.angle += dTheta;
        angleHistory.add( new Double( angle ) );
        while( angleHistory.size() > MAX_HISTORY_SIZE ) {
            angleHistory.remove( 0 );
        }
        super.rotateAboutPivot( dTheta );
        notifyListeners();
    }

    private ArrayList listeners = new ArrayList();

    public double[] getAngleHistory() {
        double[]d = new double[angleHistory.size()];
        for( int i = 0; i < d.length; i++ ) {
            d[i] = ( (Double)angleHistory.get( i ) ).doubleValue();
        }
        return d;
    }

    public static interface Listener {
        void legRotated();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.legRotated();
        }
    }
}
