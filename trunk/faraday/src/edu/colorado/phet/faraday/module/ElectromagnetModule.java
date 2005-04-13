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
import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.panel.ElectromagnetPanel;
import edu.colorado.phet.faraday.control.panel.ScalePanel;
import edu.colorado.phet.faraday.control.panel.VerticalSpacePanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.util.Vector2D;
import edu.colorado.phet.faraday.view.*;


/**
 * TransformerModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectromagnetModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double COMPASS_GRID_LAYER = 1;
    private static final double ELECTROMAGNET_BACK_LAYER = 2;
    private static final double COMPASS_LAYER = 3;
    private static final double ELECTROMAGNET_FRONT_LAYER = 4;
    private static final double FIELD_METER_LAYER = 5;

    // Locations
    private static final Point ELECTROMAGNET_LOCATION = new Point( 400, 400 );
    private static final Point COMPASS_LOCATION = new Point( 150, 200 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 500, 150 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Source Coil
    private static final int NUMBER_OF_LOOPS = FaradayConfig.ELECTROMAGNET_LOOPS_MAX;
    private static final double LOOP_RADIUS = 50.0;  // Fixed loop radius
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public ElectromagnetModule( AbstractClock clock ) {

        super( SimStrings.get( "ElectromagnetModule.title" ), clock );

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
        batteryModel.setEnabled( true );
        
        // AC Power Supply
        ACPowerSupply acPowerSupplyModel = new ACPowerSupply();
        acPowerSupplyModel.setMaxVoltage( FaradayConfig.AC_VOLTAGE_MAX );
        acPowerSupplyModel.setMaxAmplitude( 0.5 );
        acPowerSupplyModel.setFrequency( 0.5 );
        acPowerSupplyModel.setEnabled( false );
        model.addModelElement( acPowerSupplyModel );
        
        // Source Coil
        SourceCoil sourceCoilModel = new SourceCoil();
        sourceCoilModel.setNumberOfLoops( NUMBER_OF_LOOPS );
        sourceCoilModel.setRadius( LOOP_RADIUS );
        sourceCoilModel.setDirection( 0 /* radians */ );
        if ( batteryModel.isEnabled() ) {
            sourceCoilModel.setVoltageSource( batteryModel );
        }
        else {
            sourceCoilModel.setVoltageSource( acPowerSupplyModel );
        }
        
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
        model.addModelElement( compassModel );
        
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
        
        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, 
                electromagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setRescalingEnabled( true );
        gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        gridGraphic.setGridBackground( APPARATUS_BACKGROUND );
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
        compassGraphic.getCollisionDetector().add( electromagnetGraphic );
        
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
            
            if ( FaradayConfig.DEBUG_ENABLE_SCALE_PANEL ) {
                controlPanel.addFullWidth( new VerticalSpacePanel( FaradayConfig.CONTROL_PANEL_SPACER_HEIGHT ) );
                
                ScalePanel scalePanel = new ScalePanel( null, null, null, electromagnetGraphic );
                controlPanel.addFullWidth( scalePanel );
            }
            
            this.setControlPanel( controlPanel );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, batteryModel, electromagnetModel );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
    }
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     * It disappears when the electromagnet battery's voltage is changed, 
     * or the battery is disabled, or the electromagnet is moved.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic implements SimpleObserver {

        private Battery _batteryModel;
        private double _batteryVoltage;
        private Electromagnet _electromagnetModel;
        private Point2D _electromagnetLocation;

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         * @param batteryModel
         * @param electromagnetModel
         */
        public ThisWiggleMeGraphic( Component component, BaseModel model, 
                Battery batteryModel, Electromagnet electromagnetModel ) {
            super( component, model );

            _batteryModel = batteryModel;
            _batteryVoltage = _batteryModel.getVoltage();
            _batteryModel.addObserver( this );
            _electromagnetModel = electromagnetModel;
            _electromagnetLocation = _electromagnetModel.getLocation();
            _electromagnetModel.addObserver( this );
            
            setText( SimStrings.get( "ElectromagnetModule.wiggleMe" ) );
            addArrow( WiggleMeGraphic.BOTTOM_LEFT, new Vector2D( -50, 50 ) );
            setRange( 25, 0 );
            setCycleDuration( 5 );
            setEnabled( true );
        }

        /*
         * @see edu.colorado.phet.common.util.SimpleObserver#update()
         * 
         * If the battery voltage changes or the battery is disabled, disable and unwire the wiggle me.
         */
        public void update() {
            if ( _batteryVoltage != _batteryModel.getVoltage() || 
                    ! _batteryModel.isEnabled() ||
                    ! _electromagnetLocation.equals( _electromagnetModel.getLocation() )  ) {
                // Disable
                setEnabled( false );
                // Unwire
                _batteryModel.removeObserver( this );
                _electromagnetModel.removeObserver( this );
            }
        }
    }
}