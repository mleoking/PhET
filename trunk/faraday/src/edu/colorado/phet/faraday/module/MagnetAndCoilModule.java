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
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.MagnetAndCoilControlPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.view.*;


/**
 * MagnetAndCoilModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MagnetAndCoilModule extends Module {

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

    // Magnet parameters
    private static final double MAGNET_STRENGTH = 200;
    private static final Dimension MAGNET_SIZE = new Dimension( 250, 50 );
    
    // Pickup Coil parameters
    public static final int LOOP_RADIUS_MIN = 75;
    public static final int LOOP_RADIUS_MAX = 150;
    private static final double LOOP_RADIUS = 100;
    
    // Compass Grid parameters
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
    private AbstractCompass _compassModel;
    private LightBulb _lightBulbModel;
    private VoltMeter _voltMeterModel;
    private PickupCoil _pickupCoilModel;
    
    // View
    private BarMagnetGraphic _magnetGraphic;
    private PickupCoilGraphic _pickupCoilGraphic;
    private CompassGridGraphic _gridGraphic;
    
    // Control
    private MagnetAndCoilControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public MagnetAndCoilModule( ApplicationModel appModel ) {

        super( SimStrings.get( "MagnetAndCoilModule.title" ) );
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
        _compassModel = new Compass( _magnetModel ); 
        _compassModel.setLocation( COMPASS_LOCATION );
        model.addModelElement( _compassModel );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil( _magnetModel );
        _pickupCoilModel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        _pickupCoilModel.setRadius( LOOP_RADIUS );
        _pickupCoilModel.setDirection( 0 );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( _pickupCoilModel );
       
        // Lightbulb
        _lightBulbModel = new LightBulb( _pickupCoilModel );
        
        // Volt Meter
        _voltMeterModel = new VoltMeter( _pickupCoilModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Bar Magnet
        _magnetGraphic = new BarMagnetGraphic( apparatusPanel, _magnetModel );
        apparatusPanel.addGraphic( _magnetGraphic, MAGNET_LAYER );
        
        // Pickup AbstractCoil
        _pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, _pickupCoilModel, _lightBulbModel, _voltMeterModel );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getForeground(), COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getBackground(), COIL_BACK_LAYER );
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, _magnetModel, GRID_X_SPACING, GRID_Y_SPACING );
        _gridGraphic.setLocation( GRID_LOCATION );
        _gridGraphic.setNeedleSize( GRID_NEEDLE_SIZE );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, _compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Debugger
        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
//        debugger.add( _pickupCoilGraphic.getForeground(), Color.RED, Color.RED );
//        debugger.add( _pickupCoilGraphic.getBackground(), Color.GREEN, Color.GREEN );
        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );

        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        _controlPanel = new MagnetAndCoilControlPanel( this );
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
        _lightBulbModel.setEnabled( true );
        _voltMeterModel.setEnabled( false );
        _gridGraphic.setVisible( true );
        _compassModel.setEnabled( true );
        
        // Synchronize control panel.
        _controlPanel.setMagnetStrength( _magnetModel.getStrength() );
        _controlPanel.setLoopRadius( _pickupCoilModel.getRadius() );
        _controlPanel.setNumberOfLoops( _pickupCoilModel.getNumberOfLoops() );
        _controlPanel.setBulbEnabled( _lightBulbModel.isEnabled() );
        _controlPanel.setMeterEnabled( _voltMeterModel.isEnabled() );
        _controlPanel.setCompassGridEnabled( _gridGraphic.isVisible() );
        _controlPanel.setCompassEnabled( _compassModel.isEnabled() );
    }
    
    /**
     * Flips the magnet's polarity.
     */
    public void flipMagnetPolarity() {
        setSmoothingEnabled( false );
        double direction = _magnetModel.getDirection();
        direction = ( direction + 180 ) % 360;
        _magnetModel.setDirection( direction );
        _pickupCoilModel.updateEmf();
        setSmoothingEnabled( true );
    }
    
    /**
     * Sets the magnet's strength.
     * 
     * @param strength the strength value
     */
    public void setMagnetStrength( double strength ) {
        setSmoothingEnabled( false );
        _magnetModel.setStrength( strength );
        _pickupCoilModel.updateEmf();
        setSmoothingEnabled( true );
    }
    
    /**
     * Set the transparency of the magnet graphic.
     * 
     * @param enabled true for transparent, false for opaque
     */
    public void setMagnetTransparencyEnabled( boolean enabled ) {
        _magnetGraphic.setTransparencyEnabled( enabled );
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
     * Enables and disables the compass.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setCompassEnabled( boolean enabled ) {
        _compassModel.setEnabled( enabled );
    }
    
    /**
     * Sets the number of loops in the pickup coil.
     * 
     * @param numberOfLoops the number of loops
     */
    public void setNumberOfPickupLoops( int numberOfLoops ) {
        setSmoothingEnabled( false );
        _pickupCoilModel.setNumberOfLoops( numberOfLoops );
        _pickupCoilModel.updateEmf();
        setSmoothingEnabled( true );
    }
    
    /**
     * Sets the radius used for all loops in the pickup coil.
     * 
     * @param radius the radius
     */
    public void setPickupLoopRadius( double radius ) {
        setSmoothingEnabled( false );
        _pickupCoilModel.setRadius( radius );
        _pickupCoilModel.updateEmf();
        setSmoothingEnabled( true );
    }
    
    /**
     * Enables the light bulb.
     */
    public void setBulbEnabled( boolean enabled ) {
        _lightBulbModel.setEnabled( enabled );
        _voltMeterModel.setEnabled( !enabled );
    }
    
    /**
     * Enables the volt meter.
     */
    public void setMeterEnabled( boolean enabled ) {
        setBulbEnabled( !enabled );
    }
    
    /*
     * Enabled and disables "smoothing" of data in model elements.
     * When smoothing is enabled, a median value is used, effectively
     * eliminating spikes in the data history.  But sometimes these 
     * spike are desirable (for example, when flipping magnet polarity).
     * In these cases, we need to temporarily disable smoothing. 
     * 
     * @param enabled true to enable, false to disable
     */
    private void setSmoothingEnabled( boolean enabled ) {
        _pickupCoilModel.setSmoothingEnabled( enabled );
    }
}