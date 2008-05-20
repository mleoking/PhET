/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.control.FaradaySlider;
import edu.colorado.phet.faraday.model.Turbine;


/**
 * TurbineGraphic is the graphical representation of a simple turbine.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TurbineGraphic extends GraphicLayerSet implements SimpleObserver, ApparatusPanel2.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean TURBINE_IS_DRAGGABLE = false;
    
    private static final double WATER_WHEEL_LAYER = 1;
    private static final double WATER_LAYER = 2;
    private static final double FAUCET_LAYER = 3;
    private static final double SLIDER_LAYER = 4;
    private static final double BAR_MAGNET_LAYER = 5; // magnet in front of water!
    private static final double PIVOT_LAYER = 6;
    private static final double RPM_LAYER = 7;
    
    private static final Color RPM_COLOR = Color.GREEN;
    private static final Font RPM_VALUE_FONT = new PhetFont( 15 );
    private static final Font RPM_UNITS_FONT = new PhetFont( 12 );
    
    private static final double MAX_WATER_WIDTH = 40.0;
    private static final Color WATER_COLOR = new Color( 194, 234, 255, 180 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle _parentBounds;
    private Turbine _turbineModel;
    private PhetShapeGraphic _waterGraphic;
    private Rectangle2D _waterShape;
    private PhetImageGraphic _barMagnetGraphic;
    private PhetImageGraphic _waterWheelGraphic;
    private PhetTextGraphic _rpmValue;
    private FaradaySlider _flowSlider;
    private double _previousSpeed;
    
    //----------------------------------------------------------------------------
    // Constructors
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
            BufferedImage faucetImage = FaradayResources.getImage( FaradayConstants.FAUCET_IMAGE );
            PhetImageGraphic faucet = new PhetImageGraphic( component, faucetImage );
            addGraphic( faucet, FAUCET_LAYER );
            faucet.setLocation( -405, -350 );
            faucet.setIgnoreMouse( !TURBINE_IS_DRAGGABLE );
        }
        
        // Water
        {
            _waterGraphic = new PhetShapeGraphic( component );
            _waterGraphic.setIgnoreMouse( !TURBINE_IS_DRAGGABLE );
            addGraphic( _waterGraphic, WATER_LAYER );
            _waterGraphic.setPaint( WATER_COLOR );
            
            _waterShape = new Rectangle2D.Double( 0, 0, 0, 0 );
            _waterGraphic.setShape( _waterShape );
            
            _waterGraphic.setLocation( -112, -245 );
        }
        
        // Water Flow slider
        {
            _flowSlider = new FaradaySlider( component, 70 );
            addGraphic( _flowSlider, SLIDER_LAYER );
            _flowSlider.setMinimum( 0 );
            _flowSlider.setMaximum( 100 );
            _flowSlider.setValue( 0 );
            _flowSlider.centerRegistrationPoint();
            _flowSlider.setLocation( -160, -335 );
            _flowSlider.addChangeListener( new SliderListener() );
        }
        
        // Water Wheel
        {
            BufferedImage waterWheelImage = FaradayResources.getImage( FaradayConstants.WATER_WHEEL_IMAGE );
            _waterWheelGraphic = new PhetImageGraphic( component, waterWheelImage );
            _waterWheelGraphic.setIgnoreMouse( !TURBINE_IS_DRAGGABLE );
            _waterWheelGraphic.centerRegistrationPoint();
            _waterWheelGraphic.setLocation( 0, 0 );
            addGraphic( _waterWheelGraphic, WATER_WHEEL_LAYER );
        }
        
        // Bar magnet
        {
            BufferedImage barMagnetImage = FaradayResources.getImage( FaradayConstants.BAR_MAGNET_IMAGE );
            _barMagnetGraphic = new PhetImageGraphic( component, barMagnetImage );
            _barMagnetGraphic.setIgnoreMouse( !TURBINE_IS_DRAGGABLE );
            _barMagnetGraphic.centerRegistrationPoint();
            _barMagnetGraphic.setLocation( 0, 0 );
            addGraphic( _barMagnetGraphic, BAR_MAGNET_LAYER );
            
            // Scale the magnet image to match the model.
            final double xScale = _turbineModel.getWidth() / _barMagnetGraphic.getWidth();
            final double yScale = _turbineModel.getHeight() / _barMagnetGraphic.getHeight();
            _barMagnetGraphic.scale( xScale, yScale );
        }
        
        // Pivot point
        {
            BufferedImage turbinePivotImage = FaradayResources.getImage( FaradayConstants.TURBINE_PIVOT_IMAGE );
            PhetImageGraphic pivotGraphic = new PhetImageGraphic( component, turbinePivotImage );
            pivotGraphic.setIgnoreMouse( !TURBINE_IS_DRAGGABLE );
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
            _rpmValue.setIgnoreMouse( !TURBINE_IS_DRAGGABLE );

            PhetTextGraphic rpmUnits = new PhetTextGraphic( component, RPM_UNITS_FONT, FaradayStrings.UNITS_RPM, RPM_COLOR );
            addGraphic( rpmUnits, RPM_LAYER );
            rpmUnits.centerRegistrationPoint();
            rpmUnits.setLocation( 0, 22 );
            rpmUnits.setIgnoreMouse( !TURBINE_IS_DRAGGABLE );
        }
        
        _previousSpeed = 0.0;
        
        update();
    }

    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _turbineModel.removeObserver( this );
        _turbineModel = null;
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

                _waterWheelGraphic.clearTransform();
                _waterWheelGraphic.rotate( direction );
            }
            
            // If the speed has changed...
            if ( speed != _previousSpeed ) {
                
                _previousSpeed = speed;
                
                // Update the RPM readout.
                {
                    int rpms = (int) _turbineModel.getRPM();
                    _rpmValue.setText( String.valueOf( rpms ) );
                    _rpmValue.centerRegistrationPoint();
                }

                // Update the water flow.
                updateWater( speed );

                // Position the faucet slider.
                if ( -speed * 100 != _flowSlider.getValue() ) {
                    _flowSlider.setValue( (int)( -speed * 100 ) );
                }
            }

            repaint();
        }
    }
    
    /*
     * Updates the shape used to represent the column of water.
     * @param speed
     */
    private void updateWater( final double speed ) {
        if ( speed == 0 ) {
            _waterGraphic.setShape( null );
        }
        else {
            double waterWidth = Math.abs( speed * MAX_WATER_WIDTH );
            if ( waterWidth < 1 ) {
                waterWidth = 1; // must be at least 1 pixel wide
            }
            _waterShape.setRect( -( waterWidth / 2.0 ), 0, waterWidth, _parentBounds.height );
            _waterGraphic.setShape( _waterShape );
            setBoundsDirty();
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.view.ApparatusPanel2.ChangeListener#canvasSizeChanged(edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent)
     */
    public void canvasSizeChanged( ApparatusPanel2.ChangeEvent event ) {
        _parentBounds.setBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height ); 
        updateWater( _turbineModel.getSpeed() );
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