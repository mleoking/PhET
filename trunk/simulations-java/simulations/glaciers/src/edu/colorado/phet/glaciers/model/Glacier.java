package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;


public class Glacier implements ModelElement {
    
    public Glacier() {
        //XXX
    }
    
    public void cleanup() {}
    
    public double getIceThickness( double x, double t ) {
        return 0; //XXX
    }
    
    public double getIceVelocity( double x, double z, double t ) {
        return 0; //XXX
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

    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
