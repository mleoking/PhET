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
 * A ReflectionStrategy that reflects photons whose wavelengths
 * are between two cutoff points. Probably misnamed. It's really
 * more of a notch.
 */
public class BandPass implements ReflectionStrategy {

    private double cutoffLow;
    private double cutoffHigh;

    public BandPass( double cutoffLow, double cutoffHigh ) {
        this.cutoffLow = cutoffLow;
        this.cutoffHigh = cutoffHigh;
    }

    public boolean reflects( Photon photon ) {
        return ( photon.getWavelength() >= cutoffLow
                 && photon.getWavelength() <= cutoffHigh );
    }
}
