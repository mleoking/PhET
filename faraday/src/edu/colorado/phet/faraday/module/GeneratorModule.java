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
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.control.panel.ScalePanel;
import edu.colorado.phet.faraday.control.panel.TurbinePanel;
import edu.colorado.phet.faraday.control.panel.VerticalSpacePanel;
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
    private static final double COMPASS_GRID_LAYER = 1;
    private static final double PICKUP_COIL_BACK_LAYER = 2;
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
    
    // Pickup Coil parameters
    private static final int NUMBER_OF_LOOPS = 2;
    private static final double LOOP_AREA = 0.75 * FaradayConfig.MAX_PICKUP_LOOP_AREA;
    
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
        // Do NOT set the size -- size is set by the associated TurbineGraphic.
        model.addModelElement( turbineModel );
        
        // Compass
        Compass compassModel = new Compass( turbineModel ); 
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( false );
        compassModel.setEnabled( false );
        model.addModelElement( compassModel );
        
        // Pickup Coil
        PickupCoil pickupCoilModel = new PickupCoil( turbineModel );
        pickupCoilModel.setNumberOfLoops( NUMBER_OF_LOOPS );
        pickupCoilModel.setLoopArea( LOOP_AREA );
        pickupCoilModel.setDirection( 0 /* radians */ );
        pickupCoilModel.setMaxVoltage( FaradayConfig.MAX_PICKUP_EMF );
        pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( pickupCoilModel );
       
        // Lightbulb
        Lightbulb lightbulbModel = new Lightbulb( pickupCoilModel );
        lightbulbModel.setEnabled( true );
        lightbulbModel.setScale( 2.5 ); // depends on distance between pickup coil and turbine!
        
        // Volt Meter
        Voltmeter voltmeterModel = new Voltmeter( pickupCoilModel );
        voltmeterModel.setRotationalKinematicsEnabled( true );
        voltmeterModel.setEnabled( false );
        voltmeterModel.setScale( 3.3 ); // depends on distance between pickup coil and turbine!
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
        pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( 3.0 );
        apparatusPanel.addChangeListener( pickupCoilGraphic );
        apparatusPanel.addGraphic( pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );
        
        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, turbineModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setRescalingEnabled( true );
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

        // Collision detection
        compassGraphic.getCollisionDetector().add( pickupCoilGraphic );
        
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
            
            if ( FaradayConfig.DEBUG_ENABLE_SCALE_PANEL ) {
                controlPanel.addFullWidth( new VerticalSpacePanel( FaradayConfig.CONTROL_PANEL_SPACER_HEIGHT ) );
                
                ScalePanel scalePanel = new ScalePanel( lightbulbModel, voltmeterModel, pickupCoilGraphic, null );
                controlPanel.addFullWidth( scalePanel );
            }
            
            this.setControlPanel( controlPanel );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, turbineModel );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
    }
    
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
            setLocation( 240, 60 );
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
