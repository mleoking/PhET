/* Copyright 2005, University of Colorado */

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
import java.awt.Font;
import java.awt.Rectangle;
import java.text.MessageFormat;

import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.FaradaySlider;
import edu.colorado.phet.faraday.model.Turbine;


/**
 * TurbineGraphic is the graphical representation of a simple turbine.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TurbineGraphic extends GraphicLayerSet implements SimpleObserver, ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double WATER_WHEEL_LAYER = 1;
    private static final double WATER_LAYER = 2;
    private static final double FAUCET_LAYER = 3;
    private static final double SLIDER_LAYER = 4;
    private static final double BAR_MAGNET_LAYER = 5;
    private static final double PIVOT_LAYER = 6;
    private static final double RPM_LAYER = 7;
    
    private static final Color RPM_COLOR = Color.GREEN;
    private static final Font RPM_VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 15 );
    private static final Font RPM_UNITS_FONT = new Font( "SansSerif", Font.PLAIN, 12 );
    
    private static final double MAX_WATER_WIDTH = 40.0;
    private static final Color WATER_COLOR = new Color( 194, 234, 255, 180 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle _parentBounds;
    private Turbine _turbineModel;
    private PhetShapeGraphic _waterGraphic;
    private Rectangle _waterShape;
    private BarMagnetGraphic _barMagnetGraphic;
    private PhetImageGraphic _waterWheelGraphic;
    private PhetTextGraphic _rpmValue;
    private FaradaySlider _flowSlider;
    private double _previousSpeed;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param turbineModel the turbine that this graphic represents
     */
    public TurbineGraphic( Component component, Turbine turbineModel ) {
        super( component );
        
        assert( turbineModel != null );
        
        _turbineModel = turbineModel;
        _turbineModel.addObserver( this );
        
        _parentBounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() );
        
        // Faucet 
        {
            PhetImageGraphic faucet = new PhetImageGraphic( component, FaradayConfig.FAUCET_IMAGE );
            addGraphic( faucet, FAUCET_LAYER );
            faucet.setLocation( -265, -350 );
        }
        
        // Water
        {
            _waterGraphic = new PhetShapeGraphic( component );
            addGraphic( _waterGraphic, WATER_LAYER );
            _waterGraphic.setPaint( WATER_COLOR );
            
            _waterShape = new Rectangle( 0, 0, 0, 0 );
            _waterGraphic.setShape( _waterShape );
            
            _waterGraphic.setLocation( -112, -245 );
        }
        
        // Water Flow slider
        {
            _flowSlider = new FaradaySlider( component, 65 );
            addGraphic( _flowSlider, SLIDER_LAYER );
            _flowSlider.setMinimum( 0 );
            _flowSlider.setMaximum( 100 );
            _flowSlider.setValue( 0 );
            _flowSlider.centerRegistrationPoint();
            _flowSlider.setLocation( -195, -322 );
            _flowSlider.addChangeListener( new SliderListener() );
        }
        
        // Water Wheel
        {
            _waterWheelGraphic = new PhetImageGraphic( component, FaradayConfig.WATER_WHEEL_IMAGE );
            _waterWheelGraphic.centerRegistrationPoint();
            _waterWheelGraphic.setLocation( 0, 0 );
            addGraphic( _waterWheelGraphic, WATER_WHEEL_LAYER );
        }
        
        // Bar magnet
        {
            _barMagnetGraphic = new BarMagnetGraphic( component, turbineModel );
            addGraphic( _barMagnetGraphic, BAR_MAGNET_LAYER );

            /*
             * WORKAROUND:
             * BarMagnetGraphic was designed to live directly on the apparatus panel,
             * and handle its own mouse events. De-register all listeners and observers
             * so that we can handle updating the BarMagnetGraphic ourselves.
             */
            turbineModel.removeObserver( _barMagnetGraphic );
            _barMagnetGraphic.removeAllMouseInputListeners();
            _barMagnetGraphic.setLocation( 0, 0 );
        }
        
        // Pivot point
        {
            PhetImageGraphic pivotGraphic = new PhetImageGraphic( component, FaradayConfig.TURBINE_PIVOT_IMAGE );
            addGraphic( pivotGraphic, PIVOT_LAYER );
            pivotGraphic.centerRegistrationPoint();
            pivotGraphic.setLocation( 0, 0 );
        }

        // RPM readout
        {
            String valueString = String.valueOf( (int) ( _turbineModel.getRPM() ) );
            _rpmValue = new PhetTextGraphic( component, RPM_VALUE_FONT, valueString, RPM_COLOR );
            addGraphic( _rpmValue, RPM_LAYER );
            _rpmValue.centerRegistrationPoint();
            _rpmValue.setLocation( 0, 10 );

            String unitsString = SimStrings.get( "TurbineGraphic.rpm" );
            PhetTextGraphic rpmUnits = new PhetTextGraphic( component, RPM_UNITS_FONT, unitsString, RPM_COLOR );
            addGraphic( rpmUnits, RPM_LAYER );
            rpmUnits.centerRegistrationPoint();
            rpmUnits.setLocation( 0, 22 );
        }
        
        _previousSpeed = 0.0;
        
        update();
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _turbineModel.removeObserver( this );
        _turbineModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the bar magnet graphic, required by some control panels.
     * 
     * @return the bar magnet graphic
     */
    public BarMagnetGraphic getBarMagnetGraphic() {
        return _barMagnetGraphic;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        if ( isVisible() ) {

            // Location
            setLocation( (int) _turbineModel.getX(), (int) _turbineModel.getY() );
            
            double speed = _turbineModel.getSpeed();
            
            // If the turbine is moving...
            if ( speed != 0 ) {
                
                // Rotate the water wheel.
                double direction = _turbineModel.getDirection();

                _barMagnetGraphic.clearTransform();
                _barMagnetGraphic.rotate( direction );
                _barMagnetGraphic.scale( 0.5, 0.5 ); //XXX rescale the image file and remove this line

                _waterWheelGraphic.clearTransform();
                _waterWheelGraphic.rotate( direction );
            }
            
            // If the speed has changed...
            if ( speed != _previousSpeed ) {
                
                _previousSpeed = speed;
                
                // Update the RPM readout.
                {
                    int rpms = (int) _turbineModel.getRPM();

                    // Set the text
                    _rpmValue.setText( String.valueOf( rpms ) );

                    // Center justify
                    _rpmValue.centerRegistrationPoint();
                }

                // Update the water flow.
                {
                    if ( speed == 0 ) {
                        _waterGraphic.setShape( null );
                    }
                    else {
                        int waterWidth = (int) Math.abs( speed * MAX_WATER_WIDTH );
                        _waterShape.setBounds( -( waterWidth / 2 ), 0, waterWidth, _parentBounds.height );
                        _waterGraphic.setShape( _waterShape );
                    }
                }
            }

            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.view.ApparatusPanel2.ChangeListener#canvasSizeChanged(edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent)
     */
    public void canvasSizeChanged( edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent event ) {
        _parentBounds.setBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );   
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * SliderListener handles changes to the speed slider.
     */
    private class SliderListener implements ChangeListener {
        
        /** Sole constructor */
        public SliderListener() {
            super();
        }

        /**
         * Handles amplitude slider changes.
         * 
         * @param event the event
         */
        public void stateChanged( javax.swing.event.ChangeEvent event ) {  
            if ( event.getSource() == _flowSlider ) {
                // Read the value.
                double speed = -( _flowSlider.getValue() / 100.0 );  // counterclockwise
                // Update the model.
                _turbineModel.setSpeed( speed );
            }
        }
    }
}