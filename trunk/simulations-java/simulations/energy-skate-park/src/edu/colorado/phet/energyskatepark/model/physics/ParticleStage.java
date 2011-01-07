// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model.physics;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.common.spline.ParametricFunction2D;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 3:19:27 PM
 */

public class ParticleStage implements Serializable {
    private ArrayList splines = new ArrayList();
    private ArrayList listeners = new ArrayList();

    public ParticleStage() {
    }

    public ParticleStage( ParametricFunction2D parametricFunction2D ) {
        addCubicSpline2D( parametricFunction2D );
    }

    public void addCubicSpline2D( ParametricFunction2D parametricFunction2D ) {
        splines.add( parametricFunction2D );
        notifySplineAdded();
    }

    public ParametricFunction2D getCubicSpline2D( int i ) {
        return (ParametricFunction2D)splines.get( i );
    }

    public int getCubicSpline2DCount() {
        return splines.size();
    }

    public boolean isSplineUserControlled() {
        return false;
    }

    public static interface Listener {
        void splineAdded();

        void splineRemoved();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void notifySplineRemoved() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.splineRemoved();
        }
    }

    private void notifySplineAdded() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.splineAdded();
        }
    }

    public String toStringSerialization() {
        String str = "";
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
