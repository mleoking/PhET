/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.mirror;

import edu.colorado.phet.quantum.model.Photon;

/**
 * A ReflectionStrategy that reflects a specified fraction of photons
 */ 
public class Partial implements ReflectionStrategy {

    private double reflectivity;

    public Partial( double reflectivity ) {
        this.reflectivity = reflectivity;
    }

    public double getReflectivity() {
        return reflectivity;
    }

    /**
     * Sets the reflectivity. Valid values are 0 to 1.
     *
     * @param reflectivity
     */
    public void setReflectivity( double reflectivity ) {
        if( reflectivity < 0 || reflectivity > 1 ) {
            throw new IllegalArgumentException( "Reflectivity not between 0 and 1.0" );
        }
        this.reflectivity = reflectivity;
    }

    public boolean reflects( Photon photon ) {
        boolean result = false;
        if( reflectivity == 0.0 ) {
            result = false;
        }
        else if( reflectivity == 1.0 ) {
            result = true;
        }
        else {
            double r = Math.random();
            if( r < reflectivity ) {
                result = true;
            }
        }
        return result;
    }
}
