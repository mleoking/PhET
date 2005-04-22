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
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.FaradayControlPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.control.panel.ScalePanel;
import edu.colorado.phet.faraday.control.panel.TurbinePanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.util.Vector2D;
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
    private static final double PICKUP_COIL_BACK_LAYER = 1;
    private static final double COMPASS_GRID_LAYER = 2;
    private static final double TURBINE_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double PICKUP_COIL_FRONT_LAYER = 5;
    private static final double FIELD_METER_LAYER = 6;

    // Locations
    private static final Point TURBINE_LOCATION = new Point( 285, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 550, TURBINE_LOCATION.y );
    private static final Point COMPASS_LOCATION = new Point( 350, 175 );
    private static final Point FIELD_METER_LOCATION = new Point( 450, 460 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 240, 60 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Turbine
    private static final double TURBINE_STRENGTH = 0.75 * FaradayConfig.TURBINE_STRENGTH_MAX;
    private static final double TURBINE_DIRECTION = 0.0; // radians
    private static final double TURBINE_SPEED = 0.0;
    
    // Pickup Coil
    private static final int PICKUP_COIL_NUMBER_OF_LOOPS = 2;
    private static final double PICKUP_COIL_LOOP_AREA = 0.75 * FaradayConfig.MAX_PICKUP_LOOP_AREA;
    private static final double PICKUP_COIL_DIRECTION = 0.0; // radians
    
    // Scaling -- values depend on the distance between pickup coil and turbine!
    private static final double LIGHTBULB_SCALE = 2.5;
    private static final double VOLTMETER_SCALE = 3.3;
    private static final double ELECTRON_SPEED_SCALE = 3.0;
    
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
    private CompassGridGraphic _gridGraphic;
    private PickupCoilPanel _pickupCoilPanel;
    private TurbinePanel _turbinePanel;
    
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
        _turbineModel = new Turbine();
        _turbineModel.setMaxStrength( FaradayConfig.TURBINE_STRENGTH_MAX );
        _turbineModel.setMinStrength( FaradayConfig.TURBINE_STRENGTH_MIN );
        _turbineModel.setStrength( TURBINE_STRENGTH );
        _turbineModel.setLocation( TURBINE_LOCATION );
        _turbineModel.setDirection( TURBINE_DIRECTION );
        _turbineModel.setSpeed( TURBINE_SPEED );
        // Do NOT set the size -- size is set by the associated TurbineGraphic.
        model.addModelElement( _turbineModel );
        
        // Compass
        _compassModel = new Compass( _turbineModel ); 
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setBehavior( Compass.SIMPLE_BEHAVIOR );
        _compassModel.setEnabled( false );
        model.addModelElement( _compassModel );
           
        // Field Meter
        _fieldMeterModel = new FieldMeter( _turbineModel );
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil( _turbineModel );
        _pickupCoilModel.setNumberOfLoops( PICKUP_COIL_NUMBER_OF_LOOPS );
        _pickupCoilModel.setLoopArea( PICKUP_COIL_LOOP_AREA );
        _pickupCoilModel.setDirection( PICKUP_COIL_DIRECTION );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( _pickupCoilModel );
       
        // Lightbulb
        _lightbulbModel = new Lightbulb( _pickupCoilModel );
        _lightbulbModel.setEnabled( true );
        _lightbulbModel.setScale( LIGHTBULB_SCALE );
        
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
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );

        // Turbine
        TurbineGraphic turbineGraphic = new TurbineGraphic( apparatusPanel, _turbineModel );
        apparatusPanel.addChangeListener( turbineGraphic );
        apparatusPanel.addGraphic( turbineGraphic, TURBINE_LAYER );
        
        // Pickup Coil
        _pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, model,
                _pickupCoilModel, _lightbulbModel, _voltmeterModel, _turbineModel );
        _pickupCoilGraphic.setDraggingEnabled( false );
        _pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( ELECTRON_SPEED_SCALE );
        apparatusPanel.addChangeListener( _pickupCoilGraphic );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, _turbineModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        _gridGraphic.setRescalingEnabled( true );
        _gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        _gridGraphic.setGridBackground( APPARATUS_BACKGROUND );
        _gridGraphic.setVisible( false );
        apparatusPanel.addChangeListener( _gridGraphic );
        apparatusPanel.addGraphic( _gridGraphic, COMPASS_GRID_LAYER );
        super.setCompassGridGraphic( _gridGraphic );
        
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
            _turbinePanel = new TurbinePanel( _turbineModel, _compassModel, _fieldMeterModel, _gridGraphic );
            controlPanel.addFullWidth( _turbinePanel );
            
            // Spacer
            controlPanel.addVerticalSpace();
            
            // Pickup Coil controls
            _pickupCoilPanel = new PickupCoilPanel(
                    _pickupCoilModel, _pickupCoilGraphic, _lightbulbModel, _voltmeterModel );
            controlPanel.addFullWidth( _pickupCoilPanel );
            
            // Scaling calibration
            if ( FaradayConfig.DEBUG_ENABLE_SCALE_PANEL ) {
                controlPanel.addVerticalSpace();
                
                ScalePanel scalePanel = new ScalePanel( _lightbulbModel, _voltmeterModel, _pickupCoilGraphic, null );
                controlPanel.addFullWidth( scalePanel );
            }
            
            // Reset button
            controlPanel.addResetButton();
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, _turbineModel );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
    }
    
    //----------------------------------------------------------------------------
    // FaradayModule implementation
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
        _pickupCoilGraphic.getCoilGraphic().setElectronAnimationEnabled( true );
        
        // Compass Grid view
        _gridGraphic.setVisible( true );
        
        // Field Meter view
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Control panel
        _turbinePanel.update();
        _pickupCoilPanel.update();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     * It disappears when the turbine speed is changed.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic implements SimpleObserver {

        private Turbine _turbineModel;
        private double _turbineSpeed;

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         * @param turbineModel
         */
        public ThisWiggleMeGraphic( Component component, BaseModel model, Turbine turbineModel ) {
            super( component, model );

            _turbineModel = turbineModel;
            _turbineSpeed = turbineModel.getSpeed();
            turbineModel.addObserver( this );
            
            setText( SimStrings.get( "GeneratorModule.wiggleMe" ) );
            addArrow( WiggleMeGraphic.MIDDLE_LEFT, new Vector2D( -80, 0 ) );
            setRange( 25, 0 );
            setCycleDuration( 10 );
            setEnabled( true );
        }

        /*
         * @see edu.colorado.phet.common.util.SimpleObserver#update()
         * 
         * If the turbine speed changes, disable and unwire the wiggle me.
         */
        public void update() {
            if ( _turbineSpeed != _turbineModel.getSpeed()  ) {
                // Disable
                setEnabled( false );
                // Unwire
                _turbineModel.removeObserver( this );
            }
        }
    }
}
