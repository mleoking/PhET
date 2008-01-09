/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;


public class Glacier extends ClockAdapter {
    
    private static final double HEAD_X_COORDINATE = 0;
    
    public Glacier() {
        //XXX
    }
    
    public void cleanup() {}
    
    public double getHeadPosition( ) {
        return HEAD_X_COORDINATE; //XXX
    }
    
    public double getTerminusPosition() {
        return 0; //XXX
    }
    
    public double getEquilibriumLinePosition() {
        return 0; //XXX return x position
    }
    
    public double getIceThickness( double x, double t ) {
        return 0; //XXX
    }
    
    public Vector2D getIceVelocity( double x, double altitude, double t ) {
        return new Vector2D.Double( 0, 0 ); //XXX
    }
    
    public double getAccumulation( double x ) {
        return 0; //XXX
    }
    
    public double getAblation( double x ) {
        return 0; //XXX
    }
    
    public double getGlacialBudget( double x ) {
        return getAccumulation( x ) - getAblation( x );
    }
    
    public double getAgeOfIce( double x, double altitude ) {
        return 0; //XXX
    }

    public void simulationTimeChanged( ClockEvent event ) {
        //XXX
    }
}
