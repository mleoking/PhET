/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.module;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.control.FaradayControlPanel;
import edu.colorado.phet.faraday.control.panel.DeveloperControlsPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.control.panel.TurbinePanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.view.*;


/**
 * GeneratorModule is the "Generator" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GeneratorModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double PICKUP_COIL_BACK_LAYER = 1;
    private static final double B_FIELD_LAYER = 2;
    private static final double TURBINE_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double PICKUP_COIL_FRONT_LAYER = 5;
    private static final double FIELD_METER_LAYER = 6;

    // Locations
    private static final Point TURBINE_LOCATION = new Point( 285, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 550, TURBINE_LOCATION.y );
    private static final Point COMPASS_LOCATION = new Point( 350, 175 );
    private static final Point FIELD_METER_LOCATION = new Point( 450, 460 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Turbine
    private static final Dimension TURBINE_SIZE = FaradayConstants.BAR_MAGNET_SIZE;
    private static final double TURBINE_STRENGTH = 0.75 * FaradayConstants.TURBINE_STRENGTH_MAX;
    private static final double TURBINE_DIRECTION = 0.0; // radians
    private static final double TURBINE_SPEED = 0.0;
    
    // Pickup Coil
    private static final int PICKUP_COIL_NUMBER_OF_LOOPS = 2;
    private static final double PICKUP_COIL_LOOP_AREA = FaradayConstants.DEFAULT_PICKUP_LOOP_AREA;
    private static final double PICKUP_COIL_DIRECTION = 0.0; // radians
    private static final double PICKUP_COIL_DISTANCE_EXPONENT = 2.0;
    private static final double PICKUP_COIL_FUDGE_FACTOR = 1.0;  // see PickupCoil.setFudgeFactor, 1 because magnet is never inside coil
    private static final double LIGHTBULB_GLASS_MIN_ALPHA = 0.35;
    
    // Scaling -- values depend on the distance between pickup coil and turbine!
    private static final double LIGHTBULB_GLOW_SCALE = 15.0;
    private static final double LIGHTBULB_SCALE = 2.5;
    private static final double VOLTMETER_SCALE = 3.3;
    private static final double ELECTRON_SPEED_SCALE = 2.5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Turbine _turbineModel;
    private Compass _compassModel;
    private FieldMeter _fieldMeterModel;
    private PickupCoil _pickupCoilModel;
    private Lightbulb _lightbulbModel;
    private Voltmeter _voltmeterModel;
    private PickupCoilGraphic _pickupCoilGraphic;
    private BFieldOutsideGraphic _bFieldOutsideGraphic;
    private PickupCoilPanel _pickupCoilPanel;
    private TurbinePanel _turbinePanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public GeneratorModule() {

        super( FaradayStrings.TITLE_GENERATOR_MODULE );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Turbine
        _turbineModel = new Turbine();
        _turbineModel.setSize( TURBINE_SIZE.getWidth(), TURBINE_SIZE.getHeight() );
        _turbineModel.setMaxStrength( FaradayConstants.TURBINE_STRENGTH_MAX );
        _turbineModel.setMinStrength( FaradayConstants.TURBINE_STRENGTH_MIN );
        _turbineModel.setStrength( TURBINE_STRENGTH );
        _turbineModel.setLocation( TURBINE_LOCATION );
        _turbineModel.setDirection( TURBINE_DIRECTION );
        _turbineModel.setSpeed( TURBINE_SPEED );
        model.addModelElement( _turbineModel );
        
        // Compass
        _compassModel = new Compass( _turbineModel, getClock() ); 
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setBehavior( Compass.SIMPLE_BEHAVIOR );
        _compassModel.setEnabled( false );
        model.addModelElement( _compassModel );
           
        // Field Meter
        _fieldMeterModel = new FieldMeter( _turbineModel );
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil( _turbineModel, PICKUP_COIL_DISTANCE_EXPONENT );
        _pickupCoilModel.setNumberOfLoops( PICKUP_COIL_NUMBER_OF_LOOPS );
        _pickupCoilModel.setLoopArea( PICKUP_COIL_LOOP_AREA );
        _pickupCoilModel.setDirection( PICKUP_COIL_DIRECTION );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        _pickupCoilModel.setFudgeFactor( PICKUP_COIL_FUDGE_FACTOR );
        model.addModelElement( _pickupCoilModel );
       
        // Lightbulb
        _lightbulbModel = new Lightbulb( _pickupCoilModel );
        _lightbulbModel.setEnabled( true );
        _lightbulbModel.setScale( LIGHTBULB_SCALE );
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

        // Turbine
        TurbineGraphic turbineGraphic = new TurbineGraphic( apparatusPanel, _turbineModel );
        apparatusPanel.addChangeListener( turbineGraphic );
        apparatusPanel.addGraphic( turbineGraphic, TURBINE_LAYER );
        
        // Pickup Coil
        _pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, model,
                _pickupCoilModel, _lightbulbModel, _voltmeterModel );
        _pickupCoilGraphic.setDraggingEnabled( false );
        _pickupCoilGraphic.getLightbulbGraphic().setGlassMinAlpha( LIGHTBULB_GLASS_MIN_ALPHA );
        _pickupCoilGraphic.getLightbulbGraphic().setGlassGlowScale( LIGHTBULB_GLOW_SCALE );
        _pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( ELECTRON_SPEED_SCALE );
        apparatusPanel.addChangeListener( _pickupCoilGraphic );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );
        
        // B-field outside the magnet
        _bFieldOutsideGraphic = new BFieldOutsideGraphic( apparatusPanel, _turbineModel, FaradayConstants.GRID_SPACING, FaradayConstants.GRID_SPACING, false /* inMagnetPlane */ );
        _bFieldOutsideGraphic.setNeedleSize( FaradayConstants.GRID_NEEDLE_SIZE );
        _bFieldOutsideGraphic.setGridBackground( APPARATUS_BACKGROUND );
        _bFieldOutsideGraphic.setVisible( false );
        apparatusPanel.addChangeListener( _bFieldOutsideGraphic );
        apparatusPanel.addGraphic( _bFieldOutsideGraphic, B_FIELD_LAYER );
        super.setBFieldOutsideGraphic( _bFieldOutsideGraphic );
        
        // CompassGraphic
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
        compassGraphic.getCollisionDetector().add( _pickupCoilGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        {
            FaradayControlPanel controlPanel = new FaradayControlPanel( this );
            setControlPanel( controlPanel );
            
            // Turbine controls
            _turbinePanel = new TurbinePanel( _turbineModel, _compassModel, _fieldMeterModel, _bFieldOutsideGraphic );
            controlPanel.addControlFullWidth( _turbinePanel );
            
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
                        _pickupCoilGraphic, null, _pickupCoilGraphic.getLightbulbGraphic() );
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
        
        // Turbine model
        _turbineModel.setStrength( TURBINE_STRENGTH );
        _turbineModel.setLocation( TURBINE_LOCATION );
        _turbineModel.setDirection( TURBINE_DIRECTION );
        _turbineModel.setSpeed( TURBINE_SPEED );
        
        // Compass model
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setEnabled( true );
        
        // Pickup Coil model
        _pickupCoilModel.setNumberOfLoops( PICKUP_COIL_NUMBER_OF_LOOPS );
        _pickupCoilModel.setLoopArea( PICKUP_COIL_LOOP_AREA );
        _pickupCoilModel.setDirection( PICKUP_COIL_DIRECTION );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
       
        // Lightbulb
        _lightbulbModel.setEnabled( true );
        
        // Volt Meter
        _voltmeterModel.setEnabled( false );
        
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
            _turbinePanel.setBFieldControlVisible( false );
        }
        else {
            _bFieldOutsideGraphic.setVisible( false );
        }
        
        // Field Meter view
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Control panel
        _turbinePanel.update();
        _pickupCoilPanel.update();
    }
}
