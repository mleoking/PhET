package edu.colorado.phet.energyskatepark.test.phys1d;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 3:19:27 PM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */

public class ParticleStage {
    private ArrayList splines = new ArrayList();

    public ParticleStage() {
    }

    public ParticleStage( ParametricFunction2D parametricFunction2D ) {
        addCubicSpline2D( parametricFunction2D );
    }

    public void addCubicSpline2D( ParametricFunction2D parametricFunction2D ) {
        splines.add( parametricFunction2D );
        notifySplineAdded();
    }

    public int numCubicSpline2Ds() {
        return splines.size();
    }

    public ParametricFunction2D getCubicSpline2D( int i ) {
        return (ParametricFunction2D)splines.get( i );
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void splineAdded();

        void splineRemoved();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifySplineRemoved() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.splineRemoved();
        }
    }

    public void notifySplineAdded() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.splineAdded();
        }
    }

    public String toStringSerialization() {
        String str = new String();
        for( int i = 0; i < splines.size(); i++ ) {
            ParametricFunction2D parametricFunction2D = (ParametricFunction2D)splines.get( i );
            str += parametricFunction2D.toStringSerialization();
        }
        return str;
    }

    public void clear() {
        while( splines.size() > 0 ) {
            removeSpline( splines.size() - 1 );
        }

    }

    private void removeSpline( int i ) {
        splines.remove( i );
        notifySplineRemoved();
    }

}
