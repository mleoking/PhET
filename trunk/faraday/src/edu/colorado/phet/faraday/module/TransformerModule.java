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
import edu.colorado.phet.faraday.control.panel.ElectromagnetPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.control.panel.VerticalSpacePanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.util.IRescaler;
import edu.colorado.phet.faraday.util.CompassGridRescaler;
import edu.colorado.phet.faraday.view.*;


/**
 * TransformerModule is the "Transformer" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TransformerModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double GRID_LAYER = 1;
    private static final double ELECTROMAGNET_BACK_LAYER = 2;
    private static final double PICKUP_COIL_BACK_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double ELECTROMAGNET_FRONT_LAYER = 5;
    private static final double PICKUP_COIL_FRONT_LAYER = 6;
    private static final double METER_LAYER = 7;
    private static final double DEBUG_LAYER = FaradayConfig.DEBUG_LAYER;
    private static final double HELP_LAYER = FaradayConfig.HELP_LAYER;

    // Locations
    private static final Point ELECTROMAGNET_LOCATION = new Point( 200, 400 );
    private static final Point COMPASS_LOCATION = new Point( 150, 200 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 400 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Electromagnet
    private static final int ELECTROMAGNET_NUMBER_OF_LOOPS = FaradayConfig.ELECTROMAGNET_LOOPS_MAX;
    private static final double ELECTROMAGNET_LOOP_RADIUS = 50.0;  // Fixed loop radius
    
    // Pickup Coil
    private static final int PICKUP_NUMBER_OF_LOOPS = 2;
    private static final double PICKUP_LOOP_RADIUS = 0.75 * FaradayConfig.MAX_PICKUP_RADIUS;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public TransformerModule( AbstractClock clock ) {

        super( SimStrings.get( "TransformerModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Battery
        Battery batteryModel = new Battery();
        batteryModel.setMaxVoltage( FaradayConfig.BATTERY_VOLTAGE_MAX  );
        batteryModel.setAmplitude( 1.0 );
        
        // AC Source 
        ACPowerSupply acPowerSupplyModel = new ACPowerSupply();
        acPowerSupplyModel.setMaxVoltage( FaradayConfig.AC_VOLTAGE_MAX );
        acPowerSupplyModel.setMaxAmplitude( 0.5 );
        acPowerSupplyModel.setFrequency( 0.5 );
        acPowerSupplyModel.setEnabled( false );
        model.addModelElement( acPowerSupplyModel );
        
        // Source Coil
        SourceCoil sourceCoilModel = new SourceCoil();
        sourceCoilModel.setNumberOfLoops( ELECTROMAGNET_NUMBER_OF_LOOPS );
        sourceCoilModel.setRadius( ELECTROMAGNET_LOOP_RADIUS );
        sourceCoilModel.setDirection( 0 /* radians */ );
        sourceCoilModel.setVoltageSource( batteryModel );
        
        // Electromagnet
        Electromagnet electromagnetModel = new Electromagnet( sourceCoilModel );
        electromagnetModel.setMaxStrength( FaradayConfig.ELECTROMAGNET_STRENGTH_MAX );
        electromagnetModel.setLocation( ELECTROMAGNET_LOCATION );
        electromagnetModel.setDirection( 0 /* radians */ );
        // Do NOT set the strength! -- strength will be set based on the source coil model.
        // Do NOT set the size! -- size will be based on the source coil appearance.
        electromagnetModel.update();
         
        // Rescaler
        IRescaler rescaler = new CompassGridRescaler( electromagnetModel );
        
        // Compass model
        Compass compassModel = new Compass( electromagnetModel );
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( false );
        compassModel.setEnabled( false );
        model.addModelElement( compassModel );
        
        // Pickup Coil
        PickupCoil pickupCoilModel = new PickupCoil( electromagnetModel );
        pickupCoilModel.setNumberOfLoops( PICKUP_NUMBER_OF_LOOPS );
        pickupCoilModel.setRadius( PICKUP_LOOP_RADIUS );
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
        
        // Electromagnet
        ElectromagnetGraphic electromagnetGraphic = new ElectromagnetGraphic( apparatusPanel, model, 
                electromagnetModel, sourceCoilModel, batteryModel, acPowerSupplyModel );
        apparatusPanel.addChangeListener( electromagnetGraphic );
        apparatusPanel.addGraphic( electromagnetGraphic.getForeground(), ELECTROMAGNET_FRONT_LAYER );
        apparatusPanel.addGraphic( electromagnetGraphic.getBackground(), ELECTROMAGNET_BACK_LAYER );
        
        // Pickup Coil
        PickupCoilGraphic pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, model, 
                pickupCoilModel, lightbulbModel, voltmeterModel, electromagnetModel );
        apparatusPanel.addChangeListener( pickupCoilGraphic );
        apparatusPanel.addGraphic( pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );
        
        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, 
                electromagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setRescaler( rescaler );
        gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        gridGraphic.setAlphaEnabled( ! APPARATUS_BACKGROUND.equals( Color.BLACK ) );
        gridGraphic.setVisible( false );
        apparatusPanel.addChangeListener( gridGraphic );
        apparatusPanel.addGraphic( gridGraphic, GRID_LAYER );
        super.setCompassGridGraphic( gridGraphic );
        
        // Compass
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, electromagnetModel );
        fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        fieldMeterGraphic.setVisible( false );
        apparatusPanel.addChangeListener( fieldMeterGraphic );
        apparatusPanel.addGraphic( fieldMeterGraphic, METER_LAYER );
        
        // Debugger
//      DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
//      debugger.setLocationColor( Color.GREEN );
//      debugger.add( fieldMeterGraphic );
//      apparatusPanel.addGraphic( debugger, DEBUG_LAYER );
        
        // Collision detection
        electromagnetGraphic.getCollisionDetector().add( compassGraphic );
//        electromagnetGraphic.getCollisionDetector().add( pickupCoilGraphic );
//        pickupCoilGraphic.getCollisionDetector().add( electromagnetGraphic );
        pickupCoilGraphic.getCollisionDetector().add( compassGraphic );
        compassGraphic.getCollisionDetector().add( electromagnetGraphic );
        compassGraphic.getCollisionDetector().add( pickupCoilGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        {
            ControlPanel controlPanel = new ControlPanel( this );
            
            ElectromagnetPanel electromagnetPanel = new ElectromagnetPanel(
                    sourceCoilModel, batteryModel, acPowerSupplyModel, compassModel,
                    electromagnetGraphic, gridGraphic, fieldMeterGraphic );
            controlPanel.addFullWidth( electromagnetPanel );
            
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
