/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;


/**
 * BSParticle is the model of a particle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSParticle extends BSObservable {

    private double _mass;
    
    public BSParticle( double mass ) {
        super();
        setMass( mass );
    }
    
    public double getMass() {
        return _mass;
    }
    
    public void setMass( double mass ) {
        if ( mass != _mass ) {
            if ( mass <= 0 ) {
                throw new IllegalArgumentException( "invalid mass: " + mass );
            }
            _mass = mass;
            notifyObservers();
        }
    }
}
