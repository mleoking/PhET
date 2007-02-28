package edu.colorado.phet.ec3.test.phys1d;

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

    public ParticleStage( CubicSpline2D cubicSpline2D ) {
        addCubicSpline2D( cubicSpline2D );
    }

    public void addCubicSpline2D( CubicSpline2D cubicSpline2D ) {
        splines.add( cubicSpline2D );
    }

    public int numCubicSpline2Ds() {
        return splines.size();
    }

    public CubicSpline2D getCubicSpline2D( int i ) {
        return (CubicSpline2D)splines.get( i );
    }
}
