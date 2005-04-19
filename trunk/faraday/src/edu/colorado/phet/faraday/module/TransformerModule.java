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
import java.awt.Component;
import java.awt.Point;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.panel.ElectromagnetPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.control.panel.ScalePanel;
import edu.colorado.phet.faraday.control.panel.VerticalSpacePanel;
import edu.colorado.phet.faraday.model.*;
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
    private static final double ELECTROMAGNET_BACK_LAYER = 1;
    private static final double PICKUP_COIL_BACK_LAYER = 2;
    private static final double COMPASS_GRID_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double ELECTROMAGNET_FRONT_LAYER = 5;
    private static final double PICKUP_COIL_FRONT_LAYER = 6;
    private static final double FIELD_METER_LAYER = 7;

    // Locations
    private static final Point ELECTROMAGNET_LOCATION = new Point( 200, 400 );
    private static final Point COMPASS_LOCATION = new Point( 350, 525 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 400 );
    private static final Point CHALLENGE_LOCATION = new Point( 250, 50 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Electromagnet
    private static final int ELECTROMAGNET_NUMBER_OF_LOOPS = FaradayConfig.ELECTROMAGNET_LOOPS_MAX;
    private static final double ELECTROMAGNET_LOOP_RADIUS = 50.0;  // Fixed loop radius
    
    // Pickup Coil
    private static final int PICKUP_NUMBER_OF_LOOPS = 2;
    private static final double PICKUP_LOOP_AREA = 0.75 * FaradayConfig.MAX_PICKUP_LOOP_AREA;
    
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
        
        // Compass model
        Compass compassModel = new Compass( electromagnetModel );
        compassModel.setBehavior( Compass.INCREMENTAL_BEHAVIOR );
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setEnabled( false );
        model.addModelElement( compassModel );
        
        // Pickup Coil
        PickupCoil pickupCoilModel = new PickupCoil( electromagnetModel );
        pickupCoilModel.setNumberOfLoops( PICKUP_NUMBER_OF_LOOPS );
        pickupCoilModel.setLoopArea( PICKUP_LOOP_AREA );
        pickupCoilModel.setDirection( 0 /* radians */ );
        pickupCoilModel.setMaxVoltage( FaradayConfig.MAX_PICKUP_EMF );
        pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( pickupCoilModel );
       
        // Lightbulb
        Lightbulb lightbulbModel = new Lightbulb( pickupCoilModel );
        lightbulbModel.setEnabled( true );
        lightbulbModel.setScale( 2.5 );
        
        // Volt Meter
        Voltmeter voltmeterModel = new Voltmeter( pickupCoilModel );
        voltmeterModel.setRotationalKinematicsEnabled( true );
        voltmeterModel.setEnabled( false );
        voltmeterModel.setScale( 3.0 );
        model.addModelElement( voltmeterModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Electromagnet
        final ElectromagnetGraphic electromagnetGraphic = new ElectromagnetGraphic( apparatusPanel, model, 
                electromagnetModel, sourceCoilModel, batteryModel, acPowerSupplyModel );
        apparatusPanel.addChangeListener( electromagnetGraphic );
        apparatusPanel.addGraphic( electromagnetGraphic.getForeground(), ELECTROMAGNET_FRONT_LAYER );
        apparatusPanel.addGraphic( electromagnetGraphic.getBackground(), ELECTROMAGNET_BACK_LAYER );
        
        // Pickup Coil
        PickupCoilGraphic pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, model, 
                pickupCoilModel, lightbulbModel, voltmeterModel, electromagnetModel );
        pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( 3.0 );
        apparatusPanel.addChangeListener( pickupCoilGraphic );
        apparatusPanel.addGraphic( pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );
        
        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, 
                electromagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setRescalingEnabled( true );
        gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        gridGraphic.setGridBackground( APPARATUS_BACKGROUND );
        gridGraphic.setVisible( false );
        apparatusPanel.addChangeListener( gridGraphic );
        apparatusPanel.addGraphic( gridGraphic, COMPASS_GRID_LAYER );
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
        apparatusPanel.addGraphic( fieldMeterGraphic, FIELD_METER_LAYER );
        
        // Collision detection
        electromagnetGraphic.getCollisionDetector().add( compassGraphic );
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
            
            if ( FaradayConfig.DEBUG_ENABLE_SCALE_PANEL ) {
                controlPanel.addFullWidth( new VerticalSpacePanel( FaradayConfig.CONTROL_PANEL_SPACER_HEIGHT ) );
                
                ScalePanel scalePanel = new ScalePanel( lightbulbModel, voltmeterModel, pickupCoilGraphic, electromagnetGraphic );
                controlPanel.addFullWidth( scalePanel );
            }
            
            this.setControlPanel( controlPanel );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Challenge
        ThisChallengeGraphic challenge = new ThisChallengeGraphic( apparatusPanel, model, lightbulbModel );
        challenge.setLocation( CHALLENGE_LOCATION );
        apparatusPanel.addGraphic( challenge, HELP_LAYER );
    }
    
    /**
     * ThisChallengeGraphic is the "challenge" for this module.
     * It disappears when the lightbulb lights, or the lightbulb is disabled.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class ThisChallengeGraphic extends ChallengeGraphic implements SimpleObserver {

        private Lightbulb _lightbulbModel;
        private int _count;

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         * @param turbineModel
         */
        public ThisChallengeGraphic( Component component, BaseModel model, Lightbulb lightbulbModel ) {
            super( component, model );

            _lightbulbModel = lightbulbModel;
            lightbulbModel.addObserver( this );
            
            _count = 0;
            
            setText( SimStrings.get( "TransformerModule.challenge" ), null, Color.RED );
            setRange( 20, 20 );
            setEnabled( true );
        }

        /*
         * @see edu.colorado.phet.common.util.SimpleObserver#update()
         * 
         * If the lightbulb lights or the lightbulb is disabled, disable and unwire the challenge.
         */
        public void update() {
            /*
             * A bit of a hack here... 
             * The lightbulb lights when we first enter the module, before the user has done anything.
             * So we need to wait for the second time that the lightbulb lights.
             */
            if ( ( _lightbulbModel.getIntensity() != 0 && ++_count == 2 ) || !_lightbulbModel.isEnabled() ) {
                // Disable
                setEnabled( false );
                // Unwire
                _lightbulbModel.removeObserver( this );
            }
        }
    }
}
