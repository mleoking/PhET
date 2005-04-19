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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

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
    // Instance data
    //----------------------------------------------------------------------------
    
    private Battery _batteryModel;
    private ACPowerSupply _acPowerSupplyModel;
    private SourceCoil _sourceCoilModel;
    private Electromagnet _electromagnetModel;
    private Compass _compassModel;
    private PickupCoil _pickupCoilModel;
    private Lightbulb _lightbulbModel;
    private Voltmeter _voltmeterModel;
    private ElectromagnetGraphic _electromagnetGraphic;
    private PickupCoilGraphic _pickupCoilGraphic;
    private CompassGridGraphic _gridGraphic;
    private FieldMeterGraphic _fieldMeterGraphic;
    private ElectromagnetPanel _electromagnetPanel;
    private PickupCoilPanel _pickupCoilPanel;
    
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
        _batteryModel = new Battery();
        _batteryModel.setMaxVoltage( FaradayConfig.BATTERY_VOLTAGE_MAX  );
        _batteryModel.setAmplitude( 1.0 );
        
        // AC Source 
        _acPowerSupplyModel = new ACPowerSupply();
        _acPowerSupplyModel.setMaxVoltage( FaradayConfig.AC_VOLTAGE_MAX );
        _acPowerSupplyModel.setMaxAmplitude( 0.5 );
        _acPowerSupplyModel.setFrequency( 0.5 );
        _acPowerSupplyModel.setEnabled( false );
        model.addModelElement( _acPowerSupplyModel );
        
        // Source Coil
        _sourceCoilModel = new SourceCoil();
        _sourceCoilModel.setNumberOfLoops( ELECTROMAGNET_NUMBER_OF_LOOPS );
        _sourceCoilModel.setRadius( ELECTROMAGNET_LOOP_RADIUS );
        _sourceCoilModel.setDirection( 0 /* radians */ );
        _sourceCoilModel.setVoltageSource( _batteryModel );
        
        // Electromagnet
        _electromagnetModel = new Electromagnet( _sourceCoilModel );
        _electromagnetModel.setMaxStrength( FaradayConfig.ELECTROMAGNET_STRENGTH_MAX );
        _electromagnetModel.setLocation( ELECTROMAGNET_LOCATION );
        _electromagnetModel.setDirection( 0 /* radians */ );
        // Do NOT set the strength! -- strength will be set based on the source coil model.
        // Do NOT set the size! -- size will be based on the source coil appearance.
        _electromagnetModel.update();
        
        // Compass model
        _compassModel = new Compass( _electromagnetModel );
        _compassModel.setBehavior( Compass.INCREMENTAL_BEHAVIOR );
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setEnabled( false );
        model.addModelElement( _compassModel );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil( _electromagnetModel );
        _pickupCoilModel.setNumberOfLoops( PICKUP_NUMBER_OF_LOOPS );
        _pickupCoilModel.setLoopArea( PICKUP_LOOP_AREA );
        _pickupCoilModel.setDirection( 0 /* radians */ );
        _pickupCoilModel.setMaxVoltage( FaradayConfig.MAX_PICKUP_EMF );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( _pickupCoilModel );
       
        // Lightbulb
        _lightbulbModel = new Lightbulb( _pickupCoilModel );
        _lightbulbModel.setEnabled( true );
        _lightbulbModel.setScale( 2.5 );
        
        // Volt Meter
        _voltmeterModel = new Voltmeter( _pickupCoilModel );
        _voltmeterModel.setRotationalKinematicsEnabled( true );
        _voltmeterModel.setEnabled( false );
        _voltmeterModel.setScale( 3.0 );
        model.addModelElement( _voltmeterModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Electromagnet
        _electromagnetGraphic = new ElectromagnetGraphic( apparatusPanel, model, 
                _electromagnetModel, _sourceCoilModel, _batteryModel, _acPowerSupplyModel );
        apparatusPanel.addChangeListener( _electromagnetGraphic );
        apparatusPanel.addGraphic( _electromagnetGraphic.getForeground(), ELECTROMAGNET_FRONT_LAYER );
        apparatusPanel.addGraphic( _electromagnetGraphic.getBackground(), ELECTROMAGNET_BACK_LAYER );
        
        // Pickup Coil
        _pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, model, 
                _pickupCoilModel, _lightbulbModel, _voltmeterModel, _electromagnetModel );
        _pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( 3.0 );
        apparatusPanel.addChangeListener( _pickupCoilGraphic );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, 
                _electromagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        _gridGraphic.setRescalingEnabled( true );
        _gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        _gridGraphic.setGridBackground( APPARATUS_BACKGROUND );
        _gridGraphic.setVisible( false );
        apparatusPanel.addChangeListener( _gridGraphic );
        apparatusPanel.addGraphic( _gridGraphic, COMPASS_GRID_LAYER );
        super.setCompassGridGraphic( _gridGraphic );
        
        // Compass
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, _compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        _fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, _electromagnetModel );
        _fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        _fieldMeterGraphic.setVisible( false );
        apparatusPanel.addChangeListener( _fieldMeterGraphic );
        apparatusPanel.addGraphic( _fieldMeterGraphic, FIELD_METER_LAYER );
        
        // Collision detection
        _electromagnetGraphic.getCollisionDetector().add( compassGraphic );
        _pickupCoilGraphic.getCollisionDetector().add( compassGraphic );
        compassGraphic.getCollisionDetector().add( _electromagnetGraphic );
        compassGraphic.getCollisionDetector().add( _pickupCoilGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        {
            ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );
            
            // Electromagnet controls
            _electromagnetPanel = new ElectromagnetPanel(
                    _sourceCoilModel, _batteryModel, _acPowerSupplyModel, _compassModel,
                    _electromagnetGraphic, _gridGraphic, _fieldMeterGraphic );
            controlPanel.addFullWidth( _electromagnetPanel );
            
            // Spacer
            VerticalSpacePanel spacePanel = new VerticalSpacePanel( FaradayConfig.CONTROL_PANEL_SPACER_HEIGHT );
            controlPanel.addFullWidth( spacePanel );
            
            // Pickup Coil controls
            _pickupCoilPanel = new PickupCoilPanel( 
                    _pickupCoilModel, _pickupCoilGraphic, _lightbulbModel, _voltmeterModel );
            controlPanel.addFullWidth( _pickupCoilPanel );
            
            // Scaling calibration
            if ( FaradayConfig.DEBUG_ENABLE_SCALE_PANEL ) {
                controlPanel.addFullWidth( new VerticalSpacePanel( FaradayConfig.CONTROL_PANEL_SPACER_HEIGHT ) );
                
                ScalePanel scalePanel = new ScalePanel( _lightbulbModel, _voltmeterModel, _pickupCoilGraphic, _electromagnetGraphic );
                controlPanel.addFullWidth( scalePanel );
            }
            
            // Reset button
            JButton resetButton = new JButton( SimStrings.get( "Reset.button" ) );
            resetButton.addActionListener( new ActionListener() { 
                public void actionPerformed( ActionEvent e ) {
                    reset();
                }
            } );
            controlPanel.add( resetButton );
        }
        
        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Challenge
        ThisChallengeGraphic challenge = new ThisChallengeGraphic( apparatusPanel, model, _lightbulbModel );
        challenge.setLocation( CHALLENGE_LOCATION );
        apparatusPanel.addGraphic( challenge, HELP_LAYER );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    /**
     * Handles the "Reset" button, resets everything thing to the initial state.
     */
    private void reset() {
        
        // Battery model
        _batteryModel.setAmplitude( 1.0 );
        _batteryModel.setEnabled( true );
        
        // AC Power Supply model
        _acPowerSupplyModel.setMaxAmplitude( 0.5 );
        _acPowerSupplyModel.setFrequency( 0.5 );
        _acPowerSupplyModel.setEnabled( false );
        
        // Source Coil model
        _sourceCoilModel.setNumberOfLoops( ELECTROMAGNET_NUMBER_OF_LOOPS );
        _sourceCoilModel.setRadius( ELECTROMAGNET_LOOP_RADIUS );
        _sourceCoilModel.setDirection( 0 /* radians */ );
        if ( _batteryModel.isEnabled() ) {
            _sourceCoilModel.setVoltageSource( _batteryModel );
        }
        else {
            _sourceCoilModel.setVoltageSource( _acPowerSupplyModel );
        }
        
        // Electromagnet model
        _electromagnetModel.setLocation( ELECTROMAGNET_LOCATION );
        _electromagnetModel.setDirection( 0 /* radians */ );
        // Do NOT set the strength! -- strength will be set based on the source coil model.
        // Do NOT set the size! -- size will be based on the source coil appearance.
        _electromagnetModel.update();
        
        // Compass model
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setEnabled( true );
        
        // Pickup Coil model
        _pickupCoilModel.setNumberOfLoops( PICKUP_NUMBER_OF_LOOPS );
        _pickupCoilModel.setLoopArea( PICKUP_LOOP_AREA );
        _pickupCoilModel.setDirection( 0 /* radians */ );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
       
        // Lightbulb
        _lightbulbModel.setEnabled( true );
        
        // Volt Meter
        _voltmeterModel.setEnabled( false );
        
        // Electromagnet view
        _electromagnetGraphic.getCoilGraphic().setElectronAnimationEnabled( true );
        
        // Pickup Coil view
        _pickupCoilGraphic.getCoilGraphic().setElectronAnimationEnabled( true );
        
        // Compass Grid view
        _gridGraphic.setVisible( true );
        
        // Field Meter view
        _fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        _fieldMeterGraphic.setVisible( false );
        
        // Control panel
        _electromagnetPanel.update();
        _pickupCoilPanel.update();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
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
