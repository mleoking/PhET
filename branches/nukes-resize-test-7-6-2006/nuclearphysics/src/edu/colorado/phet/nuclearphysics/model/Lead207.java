/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class Lead207 extends ProfileableNucleus {
    public static final int numNeutrons = 124;
    public static final int numProtons = 83;
    public final static double MIN_POTENTIAL_ENERGY = -113;

    public Lead207( Point2D position ) {
        super( position, Lead207.numProtons, Lead207.numNeutrons );
    }

    public double getMinPotentialEnergy() {
        return MIN_POTENTIAL_ENERGY;
    }
}
