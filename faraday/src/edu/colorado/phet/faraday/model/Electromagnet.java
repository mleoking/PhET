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

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;


/**
 * Electromagnet is the model of an electro-magnet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Electromagnet extends AbstractMagnet {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Electromagnet() {
        super();
    }
    
    //----------------------------------------------------------------------------
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------

    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getStrength(java.awt.geom.Point2D)
     */
    public AbstractVector2D getStrength( Point2D p ) {
        assert( p != null );
        return new ImmutableVector2D.Double( 0, 0 ); // XXX
    }
}
