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
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
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
    private static final double CONTROL_PANEL_LAYER = 2;
    private static final double SLIDER_LAYER = 3;
    private static final double VALUE_LAYER = 4;
    
    private static final Font VALUE_FONT = new Font( "SansSerif", Font.PLAIN, 15 );
    private static final Color VALUE_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle _parentBounds;
    private Turbine _turbineModel;
    private BarMagnetGraphic _barMagnetGraphic;
    private FaradaySlider _speedSlider;
    private PhetTextGraphic _speedValue;
    
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
        
        // Control panel
        {
            Shape shape = new Rectangle( 0, 0, 130, 60 );
            PhetShapeGraphic controlPanel = new PhetShapeGraphic( component );
            addGraphic( controlPanel, CONTROL_PANEL_LAYER );
            controlPanel.setShape( shape );
            controlPanel.setPaint( Color.YELLOW );
            controlPanel.setBorderColor( Color.BLACK );
            controlPanel.setStroke( new BasicStroke( 2f ) );
            controlPanel.centerRegistrationPoint();
            controlPanel.setLocation( 0, 0 );
        }
        
        // Speed slider
        {
            _speedSlider = new FaradaySlider( component, 100 );
            addGraphic( _speedSlider, SLIDER_LAYER );
            _speedSlider.setMinimum( -100 );
            _speedSlider.setMaximum( 100 );
            _speedSlider.setValue( 0 );
            _speedSlider.addTick( -100 );
            _speedSlider.addTick( 100 );
            _speedSlider.addTick( 0 );
            _speedSlider.centerRegistrationPoint();
            _speedSlider.setLocation( 0, 25 );
            _speedSlider.addChangeListener( new SliderListener() );
        }
        
        // Speed value
        {
            _speedValue = new PhetTextGraphic( component, VALUE_FONT, "", VALUE_COLOR );
            addGraphic( _speedValue, VALUE_LAYER );
            _speedValue.setLocation( -40, -28 );
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
                _barMagnetGraphic.rotate( _turbineModel.getDirection() );
                _barMagnetGraphic.scale( 0.5, 0.5 ); //XXX rescale the image file and remove this line
            }
            
            // Update the speed value.
            {
                int speed = (int) ( _turbineModel.getSpeed() * 100.0 );
                _speedValue.setText( "Speed = " + String.valueOf( speed ) + "%" ); //XXX i18n
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
            if ( event.getSource() == _speedSlider ) {
                // Read the value.
                double speed = _speedSlider.getValue() / 100.0;
                // Update the model.
                _turbineModel.setSpeed( speed );
            }
        }
    }
}