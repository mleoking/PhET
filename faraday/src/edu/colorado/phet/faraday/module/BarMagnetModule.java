/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.module;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.BarMagnetControlPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.view.*;


/**
 * BarMagnetModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetModule extends Module {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Rendering layers
    private static final double GRID_LAYER = 1;
    private static final double COIL_BACK_LAYER = 2;
    private static final double MAGNET_LAYER = 3;
    private static final double COIL_FRONT_LAYER = 4;
    private static final double COMPASS_LAYER = 5;
    private static final double DEBUG_LAYER = FaradayConfig.DEBUG_LAYER;
    private static final double HELP_LAYER = FaradayConfig.HELP_LAYER;

    // Locations of model components

    // Locations of view components
    private static final Point MAGNET_LOCATION = new Point( 200, 300 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 300 );
    private static final Point GRID_LOCATION = new Point( 0, 0 );
    private static final Point COMPASS_LOCATION = new Point( 100, 500 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = FaradayConfig.APPARATUS_BACKGROUND;

    public static final double MAGNET_STRENGTH_MIN = 100;
    public static final double MAGNET_STRENGTH_MAX = 999;
    private static final double MAGNET_STRENGTH = 350;
    
    private static final Dimension MAGNET_SIZE = new Dimension( 250, 50 );
    
    public static final int LOOP_RADIUS_MIN = 75;
    public static final int LOOP_RADIUS_MAX = 150;
    private static final double LOOP_RADIUS = 100;
    
    private static final int GRID_X_SPACING = 40;
    private static final int GRID_Y_SPACING = 40;
    private static final Dimension GRID_NEEDLE_SIZE = new Dimension( 25, 5 );
    
    private static final double LIGHT_BULB_RESISTANCE = 20; // ohms
    private static final double VOLTMETER_RESISTANCE = 20; // ohms
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private AbstractMagnet _magnetModel;
    private LightBulb _lightBulbModel;
    private VoltMeter _voltMeterModel;
    private PickupCoil _pickupCoilModel;
    
    // View
    private PickupCoilGraphic _pickupCoilGraphic;
    private CompassGridGraphic _gridGraphic;
    
    // Control
    private BarMagnetControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public BarMagnetModule( ApplicationModel appModel ) {

        super( SimStrings.get( "BarMagnetModule.title" ) );
        assert( appModel != null );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Clock
        AbstractClock clock = appModel.getClock();

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Bar Magnet
        _magnetModel = new BarMagnet();
        _magnetModel.setStrength( MAGNET_STRENGTH );
        _magnetModel.setLocation( MAGNET_LOCATION );
        _magnetModel.setDirection( 0 );
        _magnetModel.setSize( MAGNET_SIZE );
        model.addModelElement( _magnetModel );
        
        // Compass
        AbstractCompass compassModel = new Compass( _magnetModel ); 
        compassModel.setLocation( COMPASS_LOCATION );
        model.addModelElement( compassModel );
        
        // Lightbulb
        _lightBulbModel = new LightBulb( LIGHT_BULB_RESISTANCE );
        model.addModelElement( _lightBulbModel );
        
        // Volt Meter
        _voltMeterModel = new VoltMeter( VOLTMETER_RESISTANCE );
        model.addModelElement( _voltMeterModel );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil( _magnetModel );
        _pickupCoilModel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        _pickupCoilModel.setRadius( LOOP_RADIUS );
        _pickupCoilModel.setDirection( 0 );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( _pickupCoilModel );
       
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Bar Magnet
        BarMagnetGraphic magnetGraphic = new BarMagnetGraphic( apparatusPanel, _magnetModel );
        apparatusPanel.addGraphic( magnetGraphic, MAGNET_LAYER );
        
        // Pickup AbstractCoil
        _pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, _pickupCoilModel, _lightBulbModel, _voltMeterModel );
        apparatusPanel.addGraphic( _pickupCoilGraphic, COIL_FRONT_LAYER ); // XXX
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, _magnetModel, GRID_X_SPACING, GRID_Y_SPACING );
        _gridGraphic.setLocation( GRID_LOCATION );
        _gridGraphic.setNeedleSize( GRID_NEEDLE_SIZE );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Debugger
        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
//        debugger.add( _pickupCoilGraphic );
        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );

        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new BarMagnetControlPanel( this );
        this.setControlPanel( _controlPanel );
        
        //----------------------------------------------------------------------------
        // Listeners
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // Initalize
        //----------------------------------------------------------------------------
        
        reset();
    }

    //----------------------------------------------------------------------------
    // Controller methods
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        // Set state.
        _magnetModel.setStrength( MAGNET_STRENGTH );
        _pickupCoilModel.setRadius( (int)LOOP_RADIUS );
        _pickupCoilModel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        _pickupCoilModel.setResistor( _lightBulbModel );
        _lightBulbModel.setEnabled( true );
        _voltMeterModel.setEnabled( false );
        _gridGraphic.setVisible( true );
        
        // Synchronize control panel.
        _controlPanel.setMagnetStrength( _magnetModel.getStrength() );
        _controlPanel.setLoopRadius( _pickupCoilModel.getRadius() );
        _controlPanel.setNumberOfLoops( _pickupCoilModel.getNumberOfLoops() );
        _controlPanel.setBulbEnabled( _lightBulbModel.isEnabled() );
        _controlPanel.setMeterEnabled( _voltMeterModel.isEnabled() );
        _controlPanel.setCompassGridEnabled( _gridGraphic.isVisible() );
    }
    
    /**
     * Flips the magnet's polarity.
     */
    public void flipMagnetPolarity() {
        double direction = _magnetModel.getDirection();
        direction = ( direction + 180 ) % 360;
        _magnetModel.setDirection( direction );
    }
    
    /**
     * Sets the magnet's strength.
     * 
     * @param strength the strength value
     */
    public void setMagnetStrength( double strength ) {
        _magnetModel.setStrength( strength );
    }
    
    /**
     * Enables/disables the compass grid.
     * 
     * @param enable true to enable, false to disable
     */
    public void setCompassGridEnabled( boolean enable ) {
        _gridGraphic.resetSpacing();
        _gridGraphic.setVisible( enable );
    }
    
    /**
     * Sets the number of loops in the pickup coil.
     * 
     * @param numberOfLoops the number of loops
     */
    public void setNumberOfPickupLoops( int numberOfLoops ) {
        _pickupCoilModel.setNumberOfLoops( numberOfLoops ); 
    }
    
    /**
     * Sets the radius used for all loops in the pickup coil.
     * 
     * @param radius the radius
     */
    public void setPickupLoopRadius( double radius ) {
        _pickupCoilModel.setRadius( radius );
    }
    
    /**
     * Enables the light bulb.
     */
    public void setBulbEnabled( boolean enabled ) {
        _lightBulbModel.setEnabled( enabled );
        _voltMeterModel.setEnabled( !enabled );
        if ( enabled ) {
            _pickupCoilModel.setResistor( _lightBulbModel );
        }
        else {
            _pickupCoilModel.setResistor( _voltMeterModel );
        }
    }
    
    /**
     * Enables the volt meter.
     */
    public void setMeterEnabled( boolean enabled ) {
        setBulbEnabled( !enabled );
    }
}
