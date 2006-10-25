/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;


public class SolarSystemModel extends AbstractHydrogenAtom {
    
    public SolarSystemModel( Point2D position ) {
        super( position, 0 /* orientation */ );
    }
    
    public void stepInTime( double dt ) {
        //XXX
    }
}
