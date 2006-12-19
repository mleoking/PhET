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
    public static final int NUM_NEUTRONS = 124;
    public static final int NUM_PROTONS = 83;
    public final static double MIN_POTENTIAL_ENERGY = -113;

    public Lead207( Point2D position ) {
        super( position, Lead207.NUM_PROTONS, Lead207.NUM_NEUTRONS );
    }

    public double getMinPotentialEnergy() {
        return MIN_POTENTIAL_ENERGY;
    }
}
