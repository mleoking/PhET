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

public class Rubidium extends Nucleus {
    public static final int numNeutrons = 56;
    public static final int numProtons = 37;

    public Rubidium( Point2D position ) {
        super( position, numProtons, numNeutrons );
    }
}
