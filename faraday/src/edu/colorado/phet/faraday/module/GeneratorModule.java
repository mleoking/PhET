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
import java.awt.Point;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.control.panel.TurbinePanel;
import edu.colorado.phet.faraday.control.panel.VerticalSpacePanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.util.IRescaler;
import edu.colorado.phet.faraday.util.CompassGridRescaler;
import edu.colorado.phet.faraday.view.*;


/**
 * GeneratorModule is the "Generator" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GeneratorModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double COMPASS_GRID_LAYER = 1;
    private static final double PICKUP_COIL_BACK_LAYER = 2;
    private static final double TURBINE_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double PICKUP_COIL_FRONT_LAYER = 5;
    private static final double FIELD_METER_LAYER = 6;
    private static final double DEBUG_LAYER = FaradayConfig.DEBUG_LAYER;
    private static final double HELP_LAYER = FaradayConfig.HELP_LAYER;

    // Locations
    private static final Point TURBINE_LOCATION = new Point( 285, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 550, TURBINE_LOCATION.y );
    private static final Point COMPASS_LOCATION = new Point( 350, 175 );
    private static final Point FIELD_METER_LOCATION = new Point( 450, 460 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Pickup Coil parameters
    private static final int NUMBER_OF_LOOPS = 2;
    private static final double LOOP_RADIUS = 0.75 * FaradayConfig.MAX_PICKUP_RADIUS;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public GeneratorModule( AbstractClock clock ) {

        super( SimStrings.get( "GeneratorModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Turbine
        Turbine turbineModel = new Turbine();
        turbineModel.setMaxStrength( FaradayConfig.TURBINE_STRENGTH_MAX );
        turbineModel.setMinStrength( FaradayConfig.TURBINE_STRENGTH_MIN );
        turbineModel.setStrength( 0.75 * FaradayConfig.TURBINE_STRENGTH_MAX );
        turbineModel.setLocation( TURBINE_LOCATION );
        turbineModel.setDirection( 0 /* radians */ );
        turbineModel.setSize( FaradayConfig.TURBINE_SIZE );
        model.addModelElement( turbineModel );
        
        // Rescaler
        IRescaler rescaler = new CompassGridRescaler( turbineModel );
        
        // Compass
        Compass compassModel = new Compass( turbineModel ); 
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( false );
        compassModel.setEnabled( false );
        model.addModelElement( compassModel );
        
        // Pickup Coil
        PickupCoil pickupCoilModel = new PickupCoil( turbineModel );
        pickupCoilModel.setNumberOfLoops( NUMBER_OF_LOOPS );
        pickupCoilModel.setRadius( LOOP_RADIUS );
        pickupCoilModel.setDirection( 0 /* radians */ );
        pickupCoilModel.setMaxVoltage( FaradayConfig.MAX_PICKUP_EMF );
        pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( pickupCoilModel );
       
        // Lightbulb
        Lightbulb lightbulbModel = new Lightbulb( pickupCoilModel );
        lightbulbModel.setEnabled( true );
        
        // Volt Meter
        Voltmeter voltmeterModel = new Voltmeter( pickupCoilModel );
        voltmeterModel.setRescaler( rescaler );
        voltmeterModel.setRotationalKinematicsEnabled( true );
        voltmeterModel.setEnabled( false );
        model.addModelElement( voltmeterModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );

        // Turbine
        TurbineGraphic turbineGraphic = new TurbineGraphic( apparatusPanel, turbineModel );
        apparatusPanel.addChangeListener( turbineGraphic );
        apparatusPanel.addGraphic( turbineGraphic, TURBINE_LAYER );
        
        // Pickup Coil
        PickupCoilGraphic pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, model,
                pickupCoilModel, lightbulbModel, voltmeterModel, turbineModel );
        pickupCoilGraphic.setDraggingEnabled( false );
        apparatusPanel.addChangeListener( pickupCoilGraphic );
        apparatusPanel.addGraphic( pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );
        
        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, turbineModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setRescaler( rescaler );
        gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        gridGraphic.setGridBackground( APPARATUS_BACKGROUND );
        gridGraphic.setVisible( false );
        apparatusPanel.addChangeListener( gridGraphic );
        apparatusPanel.addGraphic( gridGraphic, COMPASS_GRID_LAYER );
        super.setCompassGridGraphic( gridGraphic );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );

        // Field Meter
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, turbineModel );
        fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        fieldMeterGraphic.setVisible( false );
        apparatusPanel.addChangeListener( fieldMeterGraphic );
        apparatusPanel.addGraphic( fieldMeterGraphic, FIELD_METER_LAYER );
        
        // Debugger
//        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
//        debugger.add( _speedSlider );
//        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );

        // Collision detection
        compassGraphic.getCollisionDetector().add( pickupCoilGraphic );
        pickupCoilGraphic.getCollisionDetector().add( compassGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        {
            ControlPanel controlPanel = new ControlPanel( this );
            
            TurbinePanel turbinePanel = new TurbinePanel( turbineModel, compassModel, gridGraphic, fieldMeterGraphic );
            controlPanel.addFullWidth( turbinePanel );
            
            controlPanel.addFullWidth( new VerticalSpacePanel( FaradayConfig.CONTROL_PANEL_SPACER_HEIGHT ) );
            
            PickupCoilPanel pickupCoilPanel = new PickupCoilPanel(
                    pickupCoilModel, pickupCoilGraphic, lightbulbModel, voltmeterModel );
            controlPanel.addFullWidth( pickupCoilPanel );
            
            this.setControlPanel( controlPanel );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }
}
