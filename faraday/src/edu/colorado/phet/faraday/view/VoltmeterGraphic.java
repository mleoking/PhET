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
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.model.Voltmeter;


/**
 * VoltmeterGraphic is the graphic representation of a voltmeter.
 * The meter's needle moves on a relative scale.
 * Registration point is at bottom-center of the meter body.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class VoltmeterGraphic extends CompositePhetGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Voltmeter _voltmeterModel;
    private AbstractMagnet _magnetModel;
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
     * @param voltmeterModel
     * @param magnetModel
     */
    public VoltmeterGraphic( Component component, Voltmeter voltmeterModel, AbstractMagnet magnetModel ) {
        super( component );
        assert( component != null );
        assert( voltmeterModel != null );
        assert( magnetModel != null );

        _voltmeterModel = voltmeterModel;
        _voltmeterModel.addObserver( this );
        _magnetModel = magnetModel; // No need to observe magnet.
        
        _value = 0.0;
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Meter body
        {
            PhetImageGraphic body = new PhetImageGraphic( component, FaradayConfig.VOLTMETER_IMAGE );
            addGraphic( body );
            int rx = body.getImage().getWidth() / 2;
            int ry = body.getImage().getHeight();
            body.setRegistrationPoint( rx, ry );
        }
        
        // Screw that holds the needle in place.
        {
            int diameter = 11;
            PhetShapeGraphic screw = new PhetShapeGraphic( component );
            addGraphic( screw );
            screw.setShape( new Ellipse2D.Double( 0, 0, diameter, diameter ) );
            screw.setPaint( Color.BLUE );
            int rx = ( diameter / 2 ) + 1;
            int ry = rx;
            screw.setRegistrationPoint( rx, ry );
            screw.setLocation( 0, -23 );
        }
        
        // Needle
        {
            Point2D tail = new Point2D.Double( 0, 0 );
            Point2D tip = new Point2D.Double( 0, -70 );
            Dimension headSize = new Dimension( 12, 15 );
            int tailWidth = 3;
            Arrow arrow = new Arrow( tail, tip, headSize.height, headSize.width, tailWidth );
            _needle = new PhetShapeGraphic( component );
            addGraphic( _needle );
            _needle.setShape( arrow.getShape() );
            _needle.setPaint( Color.BLUE );
            _needle.setLocation( 0, -23 );
        }
        
        update();
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _voltmeterModel.removeObserver( this );
        _voltmeterModel = null;
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
        setVisible( _voltmeterModel.isEnabled() );
        if ( isVisible() ) {
            
            // Convert the voltage to a value in the range -1...+1.
            double value = _voltmeterModel.getVoltage() / FaradayConfig.MAX_EMF;

            // Rescale the value to improve the visual effect.
            double sign = ( value < 0 ) ? -1 : +1;
            value = sign * FaradayUtils.rescale( Math.abs(value), _magnetModel.getStrength() );
            value = MathUtil.clamp( -1, value, +1 );

            // Set the meter value.
            setValue( value );
        }
    }
}