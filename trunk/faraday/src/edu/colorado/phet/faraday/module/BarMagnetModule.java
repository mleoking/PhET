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
import edu.colorado.phet.faraday.model.HollywoodMagnet;
import edu.colorado.phet.faraday.model.PickupCoil;
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
    
    public static final int LOOP_RADIUS_MIN = 50;
    public static final int LOOP_RADIUS_MAX = 150;
    private static final double LOOP_RADIUS = 100;
    
    private static final int GRID_X_SPACING = 40;
    private static final int GRID_Y_SPACING = 40;
    private static final Dimension GRID_NEEDLE_SIZE = new Dimension( 25, 5 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private HollywoodMagnet _magnetModel;
    private PickupCoil _pickupCoilModel;
    
    // View
    private PickupCoilGraphic _pickupCoilGraphic;
    private GridGraphic _gridGraphic;
    
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

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Clock
        AbstractClock clock = appModel.getClock();

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Bar Magnet
        _magnetModel = new HollywoodMagnet();
        _magnetModel.setStrength( MAGNET_STRENGTH );
        _magnetModel.setLocation( MAGNET_LOCATION );
        _magnetModel.setDirection( 0 );
        _magnetModel.setSize( MAGNET_SIZE );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil();
        _pickupCoilModel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        _pickupCoilModel.setRadius( LOOP_RADIUS );
        _pickupCoilModel.setDirection( 0 );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        _pickupCoilModel.setMagnet( _magnetModel );
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
        
        // Pickup Coil
        _pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, _pickupCoilModel );
        apparatusPanel.addGraphic( _pickupCoilGraphic, COIL_BACK_LAYER ); // XXX
        
        // Grid
        _gridGraphic = new GridGraphic( apparatusPanel, _magnetModel, GRID_X_SPACING, GRID_Y_SPACING );
        _gridGraphic.setLocation( GRID_LOCATION );
        _gridGraphic.setNeedleSize( GRID_NEEDLE_SIZE );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, _magnetModel );
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
        // Observers
        //----------------------------------------------------------------------------
        
        _magnetModel.addObserver( magnetGraphic );
        _magnetModel.addObserver( _gridGraphic );
        _magnetModel.addObserver( compassGraphic );

        _pickupCoilModel.addObserver( _pickupCoilGraphic );
        
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
        _controlPanel.setCompassGridEnabled( false );
        _controlPanel.setMagnetStrength( MAGNET_STRENGTH );
        _controlPanel.setLoopRadius( (int)LOOP_RADIUS );
        _controlPanel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        _controlPanel.setBulbEnabled( true );
        _controlPanel.setMeterEnabled( false );
        _controlPanel.setCompassGridEnabled( false );
        _gridGraphic.setVisible( false );
    }
    
    /**
     * Flips the magnet's polarity.
     */
    public void flipMagnetPolarity() {
        //System.out.println( "flipMagnetPolarity" ); // DEBUG
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
        //System.out.println( "setMagnetStrength " + strength ); // DEBUG
        _magnetModel.setStrength( strength );
    }
    
    /**
     * Enables/disables the compass grid.
     * 
     * @param enable true to enable, false to disable
     */
    public void setCompassGridEnabled( boolean enable ) {
        System.out.println( "setCompassGridEnabled " + enable ); // DEBUG
        _gridGraphic.resetSpacing();
        _gridGraphic.setVisible( enable );
    }
    
    /**
     * Sets the number of loops in the pickup coil.
     * 
     * @param numberOfLoops the number of loops
     */
    public void setNumberOfPickupLoops( int numberOfLoops ) {
        //System.out.println( "setNumberOfPickupLoops " + numberOfLoops ); // DEBUG
        _pickupCoilModel.setNumberOfLoops( numberOfLoops ); 
    }
    
    /**
     * Sets the radius used for all loops in the pickup coil.
     * 
     * @param radius the radius
     */
    public void setPickupLoopRadius( double radius ) {
        //System.out.println( "setPickupLoopRadius " + radius ); // DEBUG
        _pickupCoilModel.setRadius( radius );
    }
    
    /**
     * Enables the light bulb.
     */
    public void setBulbEnabled( boolean enabled ) {
        //System.out.println( "setBulbEnabled " + enabled ); // DEBUG
        _pickupCoilGraphic.setBulbEnabled( enabled );
    }
    
    /**
     * Enables the volt meter.
     */
    public void setMeterEnabled( boolean enabled ) {
        //System.out.println( "setMeterEnabled " + enabled ); // DEBUG
        _pickupCoilGraphic.setMeterEnabled( enabled );
    }
}
