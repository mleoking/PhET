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

    // Locations
    private static final Point MAGNET_LOCATION = new Point( 200, 300 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 300 );
    private static final Point GRID_LOCATION = new Point( 0, 0 );
    private static final Point COMPASS_LOCATION = new Point( 100, 500 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;

    // Magnet parameters
    private static final double MAGNET_STRENGTH = 200;
    
    // Pickup Coil parameters
    public static final int LOOP_RADIUS_MIN = 75;
    public static final int LOOP_RADIUS_MAX = 150;
    private static final double LOOP_RADIUS = 100;
    
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
        AbstractMagnet magnetModel = new BarMagnet();
        magnetModel.setStrength( MAGNET_STRENGTH );
        magnetModel.setLocation( MAGNET_LOCATION );
        magnetModel.setDirection( 0 );
        magnetModel.setSize( FaradayConfig.BAR_MAGNET_SIZE );
        model.addModelElement( magnetModel );
        
        // Compass
        Compass compassModel = new Compass( magnetModel ); 
        compassModel.setLocation( COMPASS_LOCATION );
        model.addModelElement( compassModel );
        
        // Pickup Coil
        PickupCoil pickupCoilModel = new PickupCoil( magnetModel );
        pickupCoilModel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        pickupCoilModel.setRadius( LOOP_RADIUS );
        pickupCoilModel.setDirection( 0 );
        pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( pickupCoilModel );
       
        // Lightbulb
        LightBulb lightBulbModel = new LightBulb( pickupCoilModel );
        lightBulbModel.setEnabled( true );
        
        // Volt Meter
        VoltMeter voltMeterModel = new VoltMeter( pickupCoilModel );
        voltMeterModel.setEnabled( false );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Bar Magnet
        BarMagnetGraphic magnetGraphic = new BarMagnetGraphic( apparatusPanel, magnetModel );
        apparatusPanel.addGraphic( magnetGraphic, MAGNET_LAYER );
        
        // Pickup AbstractCoil
        PickupCoilGraphic pickupCoilGraphic = 
            new PickupCoilGraphic( apparatusPanel, pickupCoilModel, lightBulbModel, voltMeterModel );
        apparatusPanel.addGraphic( pickupCoilGraphic.getForeground(), COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( pickupCoilGraphic.getBackground(), COIL_BACK_LAYER );
        
        // Grid
        CompassGridGraphic gridGraphic = 
            new CompassGridGraphic( apparatusPanel, magnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setLocation( GRID_LOCATION );
        gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        apparatusPanel.addGraphic( gridGraphic, GRID_LAYER );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
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
        MagnetAndCoilControlPanel controlPanel = new MagnetAndCoilControlPanel( this, 
            magnetModel, compassModel, pickupCoilModel, lightBulbModel, voltMeterModel,
            magnetGraphic, gridGraphic );
        this.setControlPanel( controlPanel );
        
        //----------------------------------------------------------------------------
        // Listeners
        //----------------------------------------------------------------------------

        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }
}