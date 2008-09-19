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
import edu.colorado.phet.faraday.control.panel.BarMagnetPanel;
import edu.colorado.phet.faraday.control.panel.DeveloperControlsPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.model.PickupCoil.VariableNumberOfSamplePointsStrategy;
import edu.colorado.phet.faraday.view.*;


/**
 * PickupCoilModule is the "Pickup Coil" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PickupCoilModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Rendering layers
    private static final double PICKUP_COIL_BACK_LAYER = 1;
    private static final double B_FIELD_LAYER = 2;
    private static final double BAR_MAGNET_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double PICKUP_COIL_FRONT_LAYER = 5;
    private static final double FIELD_METER_LAYER = 6;

    // Locations
    private static final Point BAR_MAGNET_LOCATION = new Point( 200, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 400 );
    private static final Point COMPASS_LOCATION = new Point( 100, 525 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Bar Magnet
    private static final Dimension BAR_MAGNET_SIZE = FaradayConstants.BAR_MAGNET_SIZE;
    private static final double BAR_MAGNET_STRENGTH = 0.75 * FaradayConstants.BAR_MAGNET_STRENGTH_MAX;
    private static final double BAR_MAGNET_DIRECTION = 0.0; // radians
    
    // Pickup Coil parameters
    private static final int PICKUP_COIL_NUMBER_OF_LOOPS = 2;
    private static final double PICKUP_COIL_LOOP_AREA = FaradayConstants.DEFAULT_PICKUP_LOOP_AREA;
    private static final double PICKUP_COIL_DIRECTION = 0.0; // radians
    private static final double PICKUP_COIL_DISTANCE_EXPONENT = 3.0;
    private static final double PICKUP_COIL_FUDGE_FACTOR = 0.77; // see PickupCoil.setFudgeFactor
    private static final double LIGHTBULB_GLASS_MIN_ALPHA = 0.35;
    
    // Scaling
    private static final double LIGHTBULB_GLOW_SCALE = 15.0;
    private static final double LIGHT_RAYS_SCALE = 4.0;
    private static final double VOLTMETER_SCALE = 4.0;
    private static final double ELECTRON_SPEED_SCALE = 20.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BarMagnet _barMagnetModel;
    private Compass _compassModel;
    private FieldMeter _fieldMeterModel;
    private PickupCoil _pickupCoilModel;
    private Lightbulb _lightbulbModel;
    private Voltmeter _voltmeterModel;
    private BarMagnetGraphic _barMagnetGraphic;
    private PickupCoilGraphic _pickupCoilGraphic;
    private BFieldOutsideGraphic _bFieldOutsideGraphic;
    private BarMagnetPanel _barMagnetPanel;
    private PickupCoilPanel _pickupCoilPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public PickupCoilModule() {

        super( FaradayStrings.TITLE_PICKUP_COIL_MODULE );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Bar Magnet
        _barMagnetModel = new BarMagnet();
        _barMagnetModel.setSize( BAR_MAGNET_SIZE.getWidth(), BAR_MAGNET_SIZE.getHeight() );
        _barMagnetModel.setMaxStrength( FaradayConstants.BAR_MAGNET_STRENGTH_MAX );
        _barMagnetModel.setMinStrength( FaradayConstants.BAR_MAGNET_STRENGTH_MIN );
        _barMagnetModel.setStrength( BAR_MAGNET_STRENGTH );
        _barMagnetModel.setLocation( BAR_MAGNET_LOCATION );
        _barMagnetModel.setDirection( BAR_MAGNET_DIRECTION );
        
        // Compass
        _compassModel = new Compass( _barMagnetModel, getClock() );
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setBehavior( Compass.KINEMATIC_BEHAVIOR );
        _compassModel.setEnabled( false );
        model.addModelElement( _compassModel );
        
        // Field Meter
        _fieldMeterModel = new FieldMeter( _barMagnetModel );
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil( _barMagnetModel, PICKUP_COIL_DISTANCE_EXPONENT );
        _pickupCoilModel.setNumberOfLoops( PICKUP_COIL_NUMBER_OF_LOOPS );
        _pickupCoilModel.setLoopArea( PICKUP_COIL_LOOP_AREA );
        _pickupCoilModel.setDirection( PICKUP_COIL_DIRECTION );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION );
        _pickupCoilModel.setFudgeFactor( PICKUP_COIL_FUDGE_FACTOR );
        final double ySpacing = _barMagnetModel.getHeight() / 10;
        _pickupCoilModel.setSamplePointsStrategy( new VariableNumberOfSamplePointsStrategy( ySpacing ) );
        model.addModelElement( _pickupCoilModel );
       
        // Lightbulb
        _lightbulbModel = new Lightbulb( _pickupCoilModel );
        _lightbulbModel.setEnabled( true );
        _lightbulbModel.setScale( LIGHT_RAYS_SCALE );
        
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

        // Bar Magnet
        _barMagnetGraphic = new BarMagnetGraphic( apparatusPanel, _barMagnetModel );
        apparatusPanel.addChangeListener( _barMagnetGraphic );
        apparatusPanel.addGraphic( _barMagnetGraphic, BAR_MAGNET_LAYER );
        
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
        _bFieldOutsideGraphic = new BFieldOutsideGraphic( apparatusPanel, _barMagnetModel, FaradayConstants.GRID_SPACING, FaradayConstants.GRID_SPACING, false /* inMagnetPlane */ );
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
        _barMagnetGraphic.getCollisionDetector().add( compassGraphic );
        _barMagnetGraphic.getCollisionDetector().add( _pickupCoilGraphic );
        compassGraphic.getCollisionDetector().add( _barMagnetGraphic );
        compassGraphic.getCollisionDetector().add( _pickupCoilGraphic );
        _pickupCoilGraphic.getCollisionDetector().add( _barMagnetGraphic );
        _pickupCoilGraphic.getCollisionDetector().add( compassGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Disable clock controls
        getClockControlPanel().setEnabled( false );
        
        // Control Panel
        {
            FaradayControlPanel controlPanel = new FaradayControlPanel( this );
            setControlPanel( controlPanel );
            
            // Bar Magnet controls
            _barMagnetPanel = new BarMagnetPanel(
                    _barMagnetModel, _compassModel, _fieldMeterModel, null, _bFieldOutsideGraphic, null );
            _barMagnetPanel.setSeeInsideControlVisible( false );
            controlPanel.addControlFullWidth( _barMagnetPanel );
            
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
        
        // Bar Magnet model
        _barMagnetModel.setStrength( BAR_MAGNET_STRENGTH );
        _barMagnetModel.setLocation( BAR_MAGNET_LOCATION );
        _barMagnetModel.setDirection( BAR_MAGNET_DIRECTION );
        
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
            _barMagnetPanel.setBFieldControlVisible( false );
        }
        else {
            _bFieldOutsideGraphic.setVisible( true );
        }
        
        // Field Meter view
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Control panel
        _barMagnetPanel.update();
        _pickupCoilPanel.update();
    }
}