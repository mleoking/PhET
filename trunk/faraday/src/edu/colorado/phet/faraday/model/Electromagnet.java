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
    
    SourceCoil _coilModel;
    AbstractVoltageSource _voltageSourceModel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Electromagnet( SourceCoil coilModel, AbstractVoltageSource voltageSourceModel ) {
        super();
        assert( coilModel != null );
        assert( voltageSourceModel != null );
        
        _coilModel = coilModel;
        _coilModel.addObserver( this );
        
        _voltageSourceModel = voltageSourceModel;
        _voltageSourceModel.addObserver( this );
    }
    
    public void finalize() {
        _coilModel.removeObserver( this );
        _coilModel = null;
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
        double voltage = _voltageSourceModel.getVoltage();
        double numberOfLoops = _coilModel.getNumberOfLoops();
        double strength = voltage; //XXX hack, not right
        if ( strength < 0 ) {
            System.out.println( "Electromagnet.update: flipping polarity" ); //DEBUG
            flipPolarity();
            strength = Math.abs( strength );
        }
        setStrength( strength );
    }
}
