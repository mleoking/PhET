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
    // Class data
    //----------------------------------------------------------------------------

    private static final double COIL_BACK_LAYER = 0;
    private static final double ELECTRONS_BACK_LAYER = 1;
    private static final double COIL_FRONT_LAYER = 2;
    private static final double ELECTRONS_FRONT_LAYER = 3;
    private static final double LIGHTBULB_LAYER = 4;
    private static final double VOLTMETER_LAYER = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PickupCoil _coilModel;
    private LightBulb _lightBulbModel;
    private PhetImageGraphic _coilFront, _coilBack;
    private PhetImageGraphic _electronsFront, _electronsBack;
    private LightBulbGraphic _lightBulbGraphic;
    private VoltMeterGraphic _voltMeterGraphic;

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
        assert( component != null );
        assert( coilModel != null );
        assert( lightBulbModel != null );
        assert( voltMeterModel != null );
        
        _coilModel = coilModel;
        _coilModel.addObserver( this );
        _lightBulbModel = lightBulbModel;
        _lightBulbModel.addObserver( this );
        
        // Coil -- these are set in update method
        _coilFront = _coilBack = null;
        
        // Lightbulb
        _lightBulbGraphic = new LightBulbGraphic( component, lightBulbModel );

        // Voltmeter
        _voltMeterGraphic = new VoltMeterGraphic( component, voltMeterModel );
        _voltMeterGraphic.scale( 0.3 ); // XXX scale in PhotoShop
        
        // Interactivity
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {

            public void translationOccurred( TranslationEvent e ) {
                double x = _coilModel.getX() + e.getDx();
                double y = _coilModel.getY() + e.getDy();
                _coilModel.setLocation( x, y );
            }
        } );
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _coilModel.removeObserver( this );
        _coilModel = null;
        _lightBulbModel.removeObserver( this );
        _lightBulbModel = null;
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
     
            // Set the number of loops in the coil.
            {
                clear(); // remove all graphics
                addGraphic( _lightBulbGraphic, LIGHTBULB_LAYER );
                addGraphic( _voltMeterGraphic, VOLTMETER_LAYER );
                
                Component component = getComponent();
                int numberOfLoops = _coilModel.getNumberOfLoops();
                if( numberOfLoops == 1 ) {
                    _coilBack = new PhetImageGraphic( component, FaradayConfig.COIL1_BACK_IMAGE );
                    _coilFront = new PhetImageGraphic( component, FaradayConfig.COIL1_FRONT_IMAGE );
                    _electronsBack = new PhetImageGraphic( component, FaradayConfig.ELECTRONS1_BACK_IMAGE );
                    _electronsFront = new PhetImageGraphic( component, FaradayConfig.ELECTRONS1_FRONT_IMAGE );
                }
                else {
                    _coilBack = new PhetImageGraphic( component, FaradayConfig.COIL2_BACK_IMAGE );
                    _coilFront = new PhetImageGraphic( component, FaradayConfig.COIL2_FRONT_IMAGE );
                    _electronsBack = new PhetImageGraphic( component, FaradayConfig.ELECTRONS2_BACK_IMAGE );
                    _electronsFront = new PhetImageGraphic( component, FaradayConfig.ELECTRONS2_FRONT_IMAGE );
                }
                addGraphic( _coilBack, COIL_BACK_LAYER );
                addGraphic( _electronsBack, ELECTRONS_BACK_LAYER );
                addGraphic( _coilFront, COIL_FRONT_LAYER );
                addGraphic( _electronsFront, ELECTRONS_FRONT_LAYER );

                // Registration point at center.
                // Assumes all coil-related images are the same size.
                int rx = _coilFront.getImage().getWidth() / 2;
                int ry = _coilFront.getImage().getHeight() / 2;
                _coilFront.setRegistrationPoint( rx, ry );
                _coilBack.setRegistrationPoint( rx, ry );
                _electronsFront.setRegistrationPoint( rx, ry );
                _electronsBack.setRegistrationPoint( rx, ry );
            }
            
            // Set the area of the loops.
            // Assumes both images are the same size and the loop orientation is vertical.
            double scale = (2 * _coilModel.getRadius()) / _coilFront.getImage().getHeight();
            _coilFront.clearTransform();
            _coilFront.scale( scale );
            _coilBack.clearTransform();
            _coilBack.scale( scale );
            _electronsFront.clearTransform();
            _electronsFront.scale( scale );
            _electronsBack.clearTransform();
            _electronsBack.scale( scale );
            
            // Position the bulb and meter so that they are at the top of the coil.
            int x = 0;
            int y = (int) -( _coilFront.getBounds().getHeight()/2 );
            _lightBulbGraphic.setLocation( x, y );
            _voltMeterGraphic.setLocation( x, y );
            
            // Show electrons only if the lightbulb is enabled.
            _electronsFront.setVisible( _lightBulbModel.isEnabled() );
            _electronsBack.setVisible( _lightBulbModel.isEnabled() );
        }
    }
}