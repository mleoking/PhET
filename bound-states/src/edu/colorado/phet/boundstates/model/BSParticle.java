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

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.persistence.BSSerializable;


/**
 * BSParticle is the model of a particle.
 * This class is Java Bean compliant to support persistence via XMLEncoder.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSParticle extends BSObservable implements BSSerializable {

    private double _mass;
    
    /**
     * Zero-arg constructor, for Java Bean compliance.
     * Creates a particle with the mass of an electron.
     */
    public BSParticle() {
        this( BSConstants.ELECTRON_MASS /* default mass */ );
    }
    
    /**
     * Creates a particle with a specified mass.
     * @param mass
     */
    public BSParticle( double mass ) {
        super();
        _mass = mass;
    }
    
    /**
     * Gets the mass.
     * @return mass
     */
    public double getMass() {
        return _mass;
    }
    
    /**
     * Sets the mass.
     * It's phyically incorrect to be able to change a particle's mass,
     * but in our make-believe world it's possible.
     * 
     * @param mass
     */
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
