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

import java.awt.*;

import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
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
    
    private static final double BAR_MAGNET_LAYER = 1;
    private static final double PIVOT_LAYER = 2;
    private static final double WATER_LAYER = 3;
    private static final double FAUCET_LAYER = 4;
    private static final double SLIDER_LAYER = 5;
    
    private static final Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 15 );
    private static final Color VALUE_COLOR = Color.BLACK;
    
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
    private FaradaySlider _flowSlider;
    
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
            faucet.setLocation( -215, -350 );
        }
        
        // Water
        {
            _waterGraphic = new PhetShapeGraphic( component );
            addGraphic( _waterGraphic, WATER_LAYER );
            _waterGraphic.setPaint( WATER_COLOR );
            
            _waterShape = new Rectangle( 0, 0, 0, 0 );
            _waterGraphic.setShape( _waterShape );
            
            _waterGraphic.setLocation( -97, -245 );
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
        
        // Water Flow slider
        {
            _flowSlider = new FaradaySlider( component, 65 );
            addGraphic( _flowSlider, SLIDER_LAYER );
            _flowSlider.setMinimum( 0 );
            _flowSlider.setMaximum( 100 );
            _flowSlider.setValue( 0 );
            _flowSlider.centerRegistrationPoint();
            _flowSlider.setLocation( -160, -322 );
            _flowSlider.addChangeListener( new SliderListener() );
        }
        
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
            
            // Update the bar magnet.
            {
                _barMagnetGraphic.clearTransform();
                _barMagnetGraphic.rotate( -( _turbineModel.getDirection() ) );
                _barMagnetGraphic.scale( 0.5, 0.5 ); //XXX rescale the image file and remove this line
            }
            
            // Update the water flow.
            {
                double speed = _turbineModel.getSpeed();
                
                // Amount of water
                int waterWidth = (int) ( speed * MAX_WATER_WIDTH );
                _waterShape.setBounds( -( waterWidth / 2 ), 0, waterWidth, _parentBounds.height );
                _waterGraphic.setShape( _waterShape );
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
                double speed = _flowSlider.getValue() / 100.0;
                // Update the model.
                _turbineModel.setSpeed( speed );
            }
        }
    }
}