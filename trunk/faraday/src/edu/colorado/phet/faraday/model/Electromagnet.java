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
import edu.colorado.phet.common.util.SimpleObserver;


/**
 * Electromagnet is the model of an electromagnet.
 * It is derived from the DipoleMagnet model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Electromagnet extends DipoleMagnet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractCoil _coilModel;
    private boolean _isFlipped;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Electromagnet( AbstractCoil coilModel ) {
        super();
        assert( coilModel != null );
        
        _coilModel = coilModel;
        _coilModel.addObserver( this );
        
        _isFlipped = false;
    }
    
    public void finalize() {
        _coilModel.removeObserver( this );
        _coilModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        // Get the voltage across the ends of the coil.
        double voltage = _coilModel.getVoltage();
        
        // Flip the polarity
        int sign = ( voltage < 0 ) ? -1 : +1;
        if ( sign == 1 && _isFlipped ) {
            flipPolarity();
            _isFlipped = false;
        }
        else if ( sign == -1 && !_isFlipped ) {
            flipPolarity();
            _isFlipped = true;
        }
        
        // Set the strength -- see Kirchhoff's rule.
        double strength = Math.abs( voltage );
        setStrength( strength );
    }
}
