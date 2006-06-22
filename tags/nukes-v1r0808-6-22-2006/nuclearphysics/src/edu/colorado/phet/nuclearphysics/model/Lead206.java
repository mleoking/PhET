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

public class Lead206 extends Nucleus {
    public static final int numNeutrons = 124;
    public static final int numProtons = 82;

    public Lead206( Point2D position ) {
        super( position, Lead206.numProtons, Lead206.numNeutrons );
    }
}
