/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.module;

import java.awt.Color;
import java.awt.Point;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.control.FaradayControlPanel;
import edu.colorado.phet.faraday.control.panel.DeveloperControlsPanel;
import edu.colorado.phet.faraday.control.panel.ElectromagnetPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.model.PickupCoil.VariableNumberOfSamplePointsStrategy;
import edu.colorado.phet.faraday.view.*;


/**
 * TransformerModule is the "Transformer" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TransformerModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double ELECTROMAGNET_BACK_LAYER = 1;
    private static final double PICKUP_COIL_BACK_LAYER = 2;
    private static final double B_FIELD_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double ELECTROMAGNET_FRONT_LAYER = 5;
    private static final double PICKUP_COIL_FRONT_LAYER = 6;
    private static final double FIELD_METER_LAYER = 7;

    // Locations
    private static final Point ELECTROMAGNET_LOCATION = new Point( 200, 400 );
    private static final Point COMPASS_LOCATION = new Point( 100, 525 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 400 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Battery
    private static final double BATTERY_AMPLITUDE = 1.0;
    
    // AC Power Supply
    private static final double AC_MAX_AMPLITUDE = 0.5;
    private static final double AC_FREQUENCY = 0.5;
    
    // Electromagnet
    private static final int ELECTROMAGNET_NUMBER_OF_LOOPS = FaradayConstants.ELECTROMAGNET_LOOPS_MAX;
    private static final double ELECTROMAGNET_LOOP_RADIUS = 50.0;  // Fixed loop radius
    private static final double ELECTROMAGNET_DIRECTION = 0.0; // radians
    
    // Pickup Coil
    private static final int PICKUP_COIL_NUMBER_OF_LOOPS = 2;
    private static final double PICKUP_COIL_LOOP_AREA = 0.75 * FaradayConstants.MAX_PICKUP_LOOP_AREA;
    private static final double PICKUP_COIL_DIRECTION = 0.0; // radians
    private static final double PICKUP_COIL_DISTANCE_EXPONENT = 2.0;
    private static final double PICKUP_COIL_FUDGE_FACTOR = 0.56; // see PickupCoil.setFudgeFactor
    private static final double LIGHTBULB_GLASS_MIN_ALPHA = 0.35;
    
    // Scaling
    private static final double LIGHTBULB_GLOW_SCALE = 15.0;
    private static final double LIGHT_RAYS_SCALE = 10.0;
    private static final double VOLTMETER_SCALE = 12.0;
    private static final double ELECTRON_SPEED_SCALE = VOLTMETER_SCALE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Battery _batteryModel;
    private ACPowerSupply _acPowerSupplyModel;
    private SourceCoil _sourceCoilModel;
    private Electromagnet _electromagnetModel;
    private Compass _compassModel;
    private FieldMeter _fieldMeterModel;
    private PickupCoil _pickupCoilModel;
    private Lightbulb _lightbulbModel;
    private Voltmeter _voltmeterModel;
    private ElectromagnetGraphic _electromagnetGraphic;
    private PickupCoilGraphic _pickupCoilGraphic;
    private BFieldOutsideGraphic _bFieldOutsideGraphic;
    private ElectromagnetPanel _electromagnetPanel;
    private PickupCoilPanel _pickupCoilPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public TransformerModule() {

        super( FaradayStrings.TITLE_TRANSFORMER_MODULE );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Battery
        _batteryModel = new Battery();
        _batteryModel.setMaxVoltage( FaradayConstants.BATTERY_VOLTAGE_MAX  );
        _batteryModel.setAmplitude( BATTERY_AMPLITUDE );
        
        // AC Source 
        _acPowerSupplyModel = new ACPowerSupply();
        _acPowerSupplyModel.setMaxVoltage( FaradayConstants.AC_VOLTAGE_MAX );
        _acPowerSupplyModel.setMaxAmplitude( AC_MAX_AMPLITUDE );
        _acPowerSupplyModel.setFrequency( AC_FREQUENCY );
        _acPowerSupplyModel.setEnabled( false );
        model.addModelElement( _acPowerSupplyModel );
        
        // Source Coil
        _sourceCoilModel = new SourceCoil();
        _sourceCoilModel.setNumberOfLoops( ELECTROMAGNET_NUMBER_OF_LOOPS );
        _sourceCoilModel.setRadius( ELECTROMAGNET_LOOP_RADIUS );
        _sourceCoilModel.setDirection( ELECTROMAGNET_DIRECTION );
        
        // Electromagnet
        AbstractCurrentSource currentSource = null;
        if ( _batteryModel.isEnabled() ) {
            currentSource = _batteryModel;
        }
        else if ( _acPowerSupplyModel.isEnabled() ) {
            currentSource = _acPowerSupplyModel;
        }
        _electromagnetModel = new Electromagnet( _sourceCoilModel, currentSource );
        _electromagnetModel.setMaxStrength( FaradayConstants.ELECTROMAGNET_STRENGTH_MAX );
        _electromagnetModel.setLocation( ELECTROMAGNET_LOCATION );
        _electromagnetModel.setDirection( ELECTROMAGNET_DIRECTION );
        // Do NOT set the strength! -- strength will be set based on the source coil model.
        // Do NOT set the size! -- size will be based on the source coil model.
        _electromagnetModel.update();
        
        // Compass model
        _compassModel = new Compass( _electromagnetModel, getClock() );
        _compassModel.setBehavior( Compass.INCREMENTAL_BEHAVIOR );
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setEnabled( false );
        model.addModelElement( _compassModel );
        
        // Field Meter
        _fieldMeterModel = new FieldMeter( _electromagnetModel );
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil( _electromagnetModel, PICKUP_COIL_DISTANCE_EXPONENT );
        _pickupCoilModel.setNumberOfLoops( PICKUP_COIL_NUMBER_OF_LOOPS );
        _pickupCoilModel.setLoopArea( PICKUP_COIL_LOOP_AREA );
        _pickupCoilModel.setDirection( PICKUP_COIL_DIRECTION );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        _pickupCoilModel.setFudgeFactor( PICKUP_COIL_FUDGE_FACTOR );
        final double ySpacing = _electromagnetModel.getHeight() / 20;
        _pickupCoilModel.setSamplePointsStrategy( new VariableNumberOfSamplePointsStrategy( ySpacing ) );
        model.addModelElement( _pickupCoilModel );
       
        // Lightbulb
        _lightbulbModel = new Lightbulb( _pickupCoilModel );
        _lightbulbModel.setEnabled( true );
        _lightbulbModel.setScale( LIGHT_RAYS_SCALE );
        _lightbulbModel.setOffWhenCurrentChangesDirection( true );
        
        // Volt Meter
        _voltmeterModel = new Voltmeter( _pickupCoilModel );
        _voltmeterModel.setJiggleEnabled( true );
        _voltmeterModel.setEnabled( false );
        _voltmeterModel.setScale( VOLTMETER_SCALE );
        model.addModelElement( _voltmeterModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( getClock() );
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
                _pickupCoilModel, _lightbulbModel, _voltmeterModel );
        _pickupCoilGraphic.getLightbulbGraphic().setGlassMinAlpha( LIGHTBULB_GLASS_MIN_ALPHA );
        _pickupCoilGraphic.getLightbulbGraphic().setGlassGlowScale( LIGHTBULB_GLOW_SCALE );
        _pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( ELECTRON_SPEED_SCALE );
        apparatusPanel.addChangeListener( _pickupCoilGraphic );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );
        
        // B-field outside the magnet
        _bFieldOutsideGraphic = new BFieldOutsideGraphic( apparatusPanel, 
                _electromagnetModel, FaradayConstants.GRID_SPACING, FaradayConstants.GRID_SPACING, true /* inMagnetPlane */ );
        _bFieldOutsideGraphic.setNeedleSize( FaradayConstants.GRID_NEEDLE_SIZE );
        _bFieldOutsideGraphic.setGridBackground( APPARATUS_BACKGROUND );
        _bFieldOutsideGraphic.setVisible( false );
        apparatusPanel.addChangeListener( _bFieldOutsideGraphic );
        apparatusPanel.addGraphic( _bFieldOutsideGraphic, B_FIELD_LAYER );
        super.setBFieldOutsideGraphic( _bFieldOutsideGraphic );
        
        // Compass
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, _compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, _fieldMeterModel );
        fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        apparatusPanel.addChangeListener( fieldMeterGraphic );
        apparatusPanel.addGraphic( fieldMeterGraphic, FIELD_METER_LAYER );
        
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
            FaradayControlPanel controlPanel = new FaradayControlPanel( this );
            setControlPanel( controlPanel );
            
            // Electromagnet controls
            _electromagnetPanel = new ElectromagnetPanel( _electromagnetModel,
                    _sourceCoilModel, _batteryModel, _acPowerSupplyModel, _compassModel, _fieldMeterModel,
                    _electromagnetGraphic, _bFieldOutsideGraphic );
            controlPanel.addControlFullWidth( _electromagnetPanel );
            
            // Spacer
            controlPanel.addVerticalSpace( FaradayControlPanel.DEFAULT_VERTICAL_SPACE );
            
            // Pickup Coil controls
            _pickupCoilPanel = new PickupCoilPanel( 
                    _pickupCoilModel, _pickupCoilGraphic, _lightbulbModel, _voltmeterModel );
            controlPanel.addControlFullWidth( _pickupCoilPanel );
            
            // Scaling calibration
            if ( PhetApplication.instance().isDeveloperControlsEnabled() ) {
                controlPanel.addVerticalSpace( FaradayControlPanel.DEFAULT_VERTICAL_SPACE );
                
                DeveloperControlsPanel developerControlsPanel = new DeveloperControlsPanel( 
                        _pickupCoilModel, _lightbulbModel, _voltmeterModel, 
                        _pickupCoilGraphic, _electromagnetGraphic, _pickupCoilGraphic.getLightbulbGraphic() );
                controlPanel.addControlFullWidth( developerControlsPanel );
            }
            
            // Reset button
            controlPanel.addResetAllButton( this );
        }
        
        reset();
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        
        // Battery model
        _batteryModel.setAmplitude( BATTERY_AMPLITUDE );
        _batteryModel.setEnabled( true );
        
        // AC Power Supply model
        _acPowerSupplyModel.setMaxAmplitude( AC_MAX_AMPLITUDE );
        _acPowerSupplyModel.setFrequency( AC_FREQUENCY );
        _acPowerSupplyModel.setEnabled( false );
        
        // Source Coil model
        _sourceCoilModel.setNumberOfLoops( ELECTROMAGNET_NUMBER_OF_LOOPS );
        _sourceCoilModel.setRadius( ELECTROMAGNET_LOOP_RADIUS );
        _sourceCoilModel.setDirection( ELECTROMAGNET_DIRECTION );
        
        // Electromagnet model
        _electromagnetModel.setLocation( ELECTROMAGNET_LOCATION );
        _electromagnetModel.setDirection( ELECTROMAGNET_DIRECTION );
        if ( _batteryModel.isEnabled() ) {
            _electromagnetModel.setCurrentSource( _batteryModel );
        }
        else {
            _electromagnetModel.setCurrentSource( _acPowerSupplyModel );
        }
        _electromagnetModel.update();
        
        // Compass model
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setEnabled( false );
        
        // Pickup Coil model
        _pickupCoilModel.setNumberOfLoops( PICKUP_COIL_NUMBER_OF_LOOPS );
        _pickupCoilModel.setLoopArea( PICKUP_COIL_LOOP_AREA );
        _pickupCoilModel.setDirection( PICKUP_COIL_DIRECTION );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
       
        // Lightbulb
        _lightbulbModel.setEnabled( true );
        
        // Volt Meter
        _voltmeterModel.setEnabled( false );
        
        // Electromagnet view
        if ( FaradayConstants.HIDE_ELECTRONS_FEATURE ) {
            _electromagnetGraphic.getCoilGraphic().setElectronAnimationEnabled( false );
            _electromagnetPanel.setElectronsControlVisible( false );
        }
        else {
            _electromagnetGraphic.getCoilGraphic().setElectronAnimationEnabled( true );
        }
        
        // Pickup Coil view
        if ( FaradayConstants.HIDE_ELECTRONS_FEATURE ) {
            _pickupCoilGraphic.getCoilGraphic().setElectronAnimationEnabled( false );
            _pickupCoilPanel.setElectronsControlVisible( false );
        }
        else {
            _pickupCoilGraphic.getCoilGraphic().setElectronAnimationEnabled( true );
        }
        
        // B-field view outside the magnet
        if ( FaradayConstants.HIDE_BFIELD_FEATURE ) {
            _bFieldOutsideGraphic.setVisible( false );
            _electromagnetPanel.setBFieldControlVisible( false );
        }
        else {
            _bFieldOutsideGraphic.setVisible( true );
        }
        
        // Field Meter view
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Control panel
        _electromagnetPanel.update();
        _pickupCoilPanel.update();
    }
}
