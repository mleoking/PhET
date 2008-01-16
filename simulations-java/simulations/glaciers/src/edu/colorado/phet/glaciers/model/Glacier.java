/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * Glacier is the model of the glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Glacier extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double HEAD_X_COORDINATE = 0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners; // list of GlacierListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Glacier() {
        super();
        _listeners = new ArrayList();
    }
    
    public void cleanup() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
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

    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent event ) {
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface GlacierListener {
        //XXX what goes here?...
        public void somethingChanged();//XXX
    }
    
    public static class GlacierAdapter implements GlacierListener {
        //XXX default implementation goes here
        public void somethingChanged() {}; //XXX
    }
    
    public void addGlacierListener( GlacierListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeGlacierListener( GlacierListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification of changes
    //----------------------------------------------------------------------------
    
    //XXX this is just an example...
    private void notifySomethingChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( ( GlacierListener ) i.next() ).somethingChanged();
        }
    }
}
