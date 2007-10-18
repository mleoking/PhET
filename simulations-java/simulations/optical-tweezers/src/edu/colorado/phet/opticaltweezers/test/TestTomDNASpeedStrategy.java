/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.test;

import edu.colorado.phet.opticaltweezers.model.TomDNASpeedStrategy;

/**
 * TestTomDNASpeedStrategy tests that getSpeed and getForce yeild consistence results, where:
 * 
 * speed = f( atp, force )
 * force = f( atp, speed )
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestTomDNASpeedStrategy {

    public static final void main( String[] args ) {
        
        TomDNASpeedStrategy s = new TomDNASpeedStrategy.TomDNASpeedStrategyA();
        
        for ( double atp = 0; atp < 11.0; atp++ ) {
            for ( double force = 0; force < 10; force++ ) {
                double speed = s.getSpeed( atp, force );
                double checkForce = s.getForce( atp, speed );
                if ( Math.abs( force - checkForce ) > 0.001 ) {
                    System.err.println( "failed for atp=" + atp + " force=" + force );
                }
            }
        }
    }
}
