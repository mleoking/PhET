package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;


public class Glacier implements ModelElement {
    
    public Glacier() {
        //XXX
    }
    
    public void cleanup() {}
    
    public double getIceThickness( double x ) {
        return 0; //XXX
    }
    
    public double getIceVelocity( Point2D position ) {
        return 0; //XXX
    }
    
    public double getAccumulation( double x ) {
        return 0; //XXX
    }
    
    public double getAblation( double x ) {
        return 0; //XXX
    }
    
    public double getGlacialBudget( double x ) {
        return 0; //XXX
    }

    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
    }
}
