/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Component;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.VoltMeter;


/**
 * VoltMeterGraphic is the graphic representation of a voltmeter.
 * The meter's needle moves on a relative scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltMeterGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final double MAX_VOLTAGE = 120; // XXX
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private VoltMeter _voltMeterModel;
    private PhetImageGraphic _body;
    private PhetImageGraphic _needle;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * The registration point is at the bottom center of the meter's body.
     * 
     * @param component the parent Component
     */
    public VoltMeterGraphic( Component component, VoltMeter voltMeterModel ) {
        super( component );

        _voltMeterModel = voltMeterModel;
        _voltMeterModel.addObserver( this );
        
        // Create the graphics components.
        _body = new PhetImageGraphic( component, FaradayConfig.METER_BODY_IMAGE );
        _needle = new PhetImageGraphic( component, FaradayConfig.METER_NEEDLE_IMAGE );
        super.addGraphic( _body );
        super.addGraphic( _needle );

        // Set the meter's registration point to bottom center.
        {
            int rx = _body.getImage().getWidth() / 2;
            int ry = _body.getImage().getHeight();
            _body.setRegistrationPoint( rx, ry );
        }
        
        // Set the needle's registration point.
        {
            int w = _needle.getImage().getWidth();
            int h = _needle.getImage().getHeight();
            int x = w / 2;
            int y = h - 5;
            _needle.setRegistrationPoint( x, y );
        }

        _needle.setLocation( 0, -65 );
        
        update();
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _voltMeterModel.removeObserver( this );
        _voltMeterModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the value displayed by the meter.
     * This is a relative value, and indicates how much the needle is deflected from the zero position.
     * 
     * @param value the relative value, between -1.0 and 1.0.
     * @throws IllegalArgumentException if value is out of range
     */
    public void setValue( double value ) {
        if ( value < -1.0 || value > 1.0 ) {
            throw new IllegalArgumentException( "meter value must be between -1.0 and +1.0: " + value );
        }
        double angle = 90 * value;
        _needle.clearTransform();
        _needle.rotate( Math.toRadians( angle ) );
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        if ( isVisible() ) {
            double voltage = _voltMeterModel.getVoltage();
            System.out.println( "VoltMeterGraphic.update: voltage=" + voltage ); // DEBUG
            double scale = MathUtil.clamp( -1, voltage/MAX_VOLTAGE, 1 ); // XXX
            setValue( scale );
        }
    }
}