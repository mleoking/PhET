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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.VoltMeter;


/**
 * VoltMeterGraphic is the graphic representation of a voltmeter.
 * The meter's needle moves on a relative scale.
 * Registration point is at bottom-center of the meter body.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltMeterGraphic extends CompositePhetGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private VoltMeter _voltMeterModel;
    private double _value; // -1...+1
    private PhetShapeGraphic _needle;

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
        assert( component != null );
        assert( voltMeterModel != null );

        _voltMeterModel = voltMeterModel;
        _voltMeterModel.addObserver( this );
        
        _value = 0.0;
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Meter body
        {
            PhetImageGraphic body = new PhetImageGraphic( component, FaradayConfig.METER_BODY_IMAGE );
            addGraphic( body );
            int rx = body.getImage().getWidth() / 2;
            int ry = body.getImage().getHeight();
            body.setRegistrationPoint( rx, ry );
        }
        
        // Screw that holds the needle in place.
        {
            int diameter = 31;
            PhetShapeGraphic screw = new PhetShapeGraphic( component );
            addGraphic( screw );
            screw.setShape( new Ellipse2D.Double( 0, 0, diameter, diameter ) );
            screw.setPaint( Color.BLUE );
            int rx = diameter / 2 + 1;
            int ry = rx;
            screw.setRegistrationPoint( rx, ry );
            screw.setLocation( 2, -70 );
        }
        
        // Needle
        {
            Point2D tail = new Point2D.Double( 0, 0 );
            Point2D tip = new Point2D.Double( 0, -200 );
            Dimension headSize = new Dimension( 40, 30 );
            int tailWidth = 10;
            Arrow arrow = new Arrow( tail, tip, headSize.height, headSize.width, tailWidth );
            _needle = new PhetShapeGraphic( component );
            addGraphic( _needle );
            _needle.setShape( arrow.getShape() );
            _needle.setPaint( Color.BLUE );
            _needle.setLocation( 2, -70 );
        }
        
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
        if ( ! (value >= -1.0 && value <= 1.0 ) ) {
            throw new IllegalArgumentException( "meter value must be between -1.0 and +1.0: " + value );
        }
        if ( value != _value ) {
            _value = value;
            double angle = 90 * value;
            _needle.clearTransform();
            _needle.rotate( Math.toRadians( angle ) );
        }
    }

    /**
     * Gets the current meter reading.
     * 
     * @return a value between -1 and +1 inclusive
     */
    public double getValue() {
        return _value;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        setVisible( _voltMeterModel.isEnabled() );
        if ( isVisible() ) {
            double voltage = _voltMeterModel.getVoltage();
            double scale = MathUtil.clamp( -1, ( voltage / FaradayConfig.MAX_EMF ), 1 );
            if ( scale == Double.NaN ) {
                System.out.println( "WARNING: VoltMeterGraphic.update - scale=NaN" );
            }
            else {
                setValue( scale );
            }
        }
    }
}