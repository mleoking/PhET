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
    
    AbstractVoltageSource _voltageSourceModel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Electromagnet( AbstractVoltageSource voltageSourceModel ) {
        super();
        assert( voltageSourceModel != null );
        
        _voltageSourceModel = voltageSourceModel;
        _voltageSourceModel.addObserver( this );
    }
    
    public void finalize() {
        _voltageSourceModel.removeObserver( this );
        _voltageSourceModel = null;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        double strength = _voltageSourceModel.getVoltage(); //XXX hack
//        if ( strength < 0 ) {
//            System.out.println( "Electromagnet.update: flipping polarity" ); //DEBUG
//            flipPolarity();
//            strength = Math.abs( strength );
//        }
        setStrength( Math.abs( strength ) );
    }
}
