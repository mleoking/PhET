/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;


/**
 * ElectroMagnet
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectroMagnet extends AbstractMagnet {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ElectroMagnet() {
        super();
    }
    
    //----------------------------------------------------------------------------
    // IMagnet implementation
    //----------------------------------------------------------------------------

    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getStrength(java.awt.geom.Point2D)
     */
    public double getStrength( Point2D p ) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getDirection(java.awt.geom.Point2D)
     */
    public double getDirection( Point2D p ) {
        // TODO Auto-generated method stub
        return 0;
    }
}
