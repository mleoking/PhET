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

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.LightBulb;
import edu.colorado.phet.faraday.model.PickupCoil;
import edu.colorado.phet.faraday.model.VoltMeter;


/**
 * PickupCoilGraphic is the graphical representation of a pickup coil,
 * with devices (lightbulb and voltmeter ) for displaying the effects of induction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoilGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PickupCoil _coilModel;
    private PhetImageGraphic _coilFront, _coilBack;
    private LightBulbGraphic _bulb;
    private VoltMeterGraphic _meter;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param coilModel the model element to be observed
     */
    public PickupCoilGraphic( Component component, PickupCoil coilModel, LightBulb lightBulbModel, VoltMeter voltMeterModel ) {
        super( component );
        
        _coilModel = coilModel;
        _coilModel.addObserver( this );
        
        // Lightbulb
        _bulb = new LightBulbGraphic( component, lightBulbModel );
        _bulb.setLocation( 0, -170 );
        addGraphic( _bulb );

        // Voltmeter
        _meter = new VoltMeterGraphic( component, voltMeterModel );
        _meter.setLocation( 0, -95 );
        _meter.scale( 0.3 );
        addGraphic( _meter );
        
        // Interactivity
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {

            public void translationOccurred( TranslationEvent e ) {
                double x = _coilModel.getX() + e.getDx();
                double y = _coilModel.getY() + e.getDy();
                _coilModel.setLocation( x, y );
            }
        } );
        
        setBulbEnabled( true );
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _coilModel.removeObserver( this );
        _coilModel = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Enables the lightbulb.
     * Disables the voltmeter as a side effect.
     * 
     * @param enabled true to enable, false to disable.
     */
    public void setBulbEnabled( boolean enabled ) {
        _bulb.setVisible( enabled );
        _meter.setVisible( !enabled );
    }

    /**
     * Enables the voltmeter.
     * Disables the lightbulb as a side effect.
     * 
     * @param enabled true to enable, false to disable.
     */
    public void setMeterEnabled( boolean enabled ) {
        _bulb.setVisible( !enabled );
        _meter.setVisible( enabled );
    }
    
    //----------------------------------------------------------------------------
    // Override inherited methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates when we become visible.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        if( isVisible() ) {
            
            // Position this composite graphic.
            setLocation( (int) _coilModel.getX(), (int) _coilModel.getY() );

            // Get the coil's EMF.
            double emf = _coilModel.getEMF();
            // System.out.println( "emf = " + emf ); // DEBUG
            
//            // Set the light intensity.
//            double intensity = MathUtil.clamp( 0, Math.abs( emf/100000 ), 1 ); // XXX HACK
//            _bulb.setIntensity( intensity );
            
//            // Set the voltmeter reading.
//            double value = MathUtil.clamp( -1, (int)( emf/100000 ), 1 );  // XXX HACK
//            _meter.setValue( value );
            
            // Set the number of loops in the coil.
            {
                if ( _coilFront != null ) {
                    removeGraphic( _coilFront );
                }
                if ( _coilBack != null ) {
                    removeGraphic( _coilBack );
                }
                
                Component component = getComponent();
                int numberOfLoops = _coilModel.getNumberOfLoops();
                if( numberOfLoops == 1 ) {
                    _coilBack = new PhetImageGraphic( component, FaradayConfig.COIL1_BACK_IMAGE );
                    _coilFront = new PhetImageGraphic( component, FaradayConfig.COIL1_FRONT_IMAGE );
                }
                else {
                    _coilBack = new PhetImageGraphic( component, FaradayConfig.COIL2_BACK_IMAGE );
                    _coilFront = new PhetImageGraphic( component, FaradayConfig.COIL2_FRONT_IMAGE );
                }
                addGraphic( _coilBack );
                addGraphic( _coilFront );

                // Registration point at center.
                // Assumes both images are the same size.
                int rx = _coilFront.getImage().getWidth() / 2;
                int ry = _coilFront.getImage().getHeight() / 2;
                _coilFront.setRegistrationPoint( rx, ry );
                _coilBack.setRegistrationPoint( rx, ry );
            }
            
            // Set the area of the loops.
            // Assumes both images are the same size and the loop orientation is vertical.
            double scale = (2 * _coilModel.getRadius()) / _coilFront.getImage().getHeight();
            _coilFront.clearTransform();
            _coilFront.scale( scale );
            _coilBack.clearTransform();
            _coilBack.scale( scale );
            
        }
    }
}