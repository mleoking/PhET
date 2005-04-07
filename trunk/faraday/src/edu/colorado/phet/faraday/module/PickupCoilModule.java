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
import edu.colorado.phet.faraday.control.panel.BarMagnetPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.control.panel.ScalePanel;
import edu.colorado.phet.faraday.control.panel.VerticalSpacePanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.util.Vector2D;
import edu.colorado.phet.faraday.view.*;


/**
 * PickupCoilModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PickupCoilModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Rendering layers
    private static final double COMPASS_GRID_LAYER = 1;
    private static final double PICKUP_COIL_BACK_LAYER = 2;
    private static final double BAR_MAGNET_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double PICKUP_COIL_FRONT_LAYER = 5;
    private static final double FIELD_METER_LAYER = 6;

    // Locations
    private static final Point MAGNET_LOCATION = new Point( 200, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 400 );
    private static final Point COMPASS_LOCATION = new Point( 150, 200 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );

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
     * @param appModel the application model
     */
    public PickupCoilModule( AbstractClock clock ) {

        super( SimStrings.get( "PickupCoilModule.title" ), clock );
        
        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Bar Magnet
        BarMagnet barMagnetModel = new BarMagnet();
        barMagnetModel.setMaxStrength( FaradayConfig.BAR_MAGNET_STRENGTH_MAX );
        barMagnetModel.setMinStrength( FaradayConfig.BAR_MAGNET_STRENGTH_MIN );
        barMagnetModel.setStrength( 0.75 * FaradayConfig.BAR_MAGNET_STRENGTH_MAX );
        barMagnetModel.setLocation( MAGNET_LOCATION );
        barMagnetModel.setDirection( 0 /* radians */ );
        // Do NOT set the size -- size is set by the associated BarMagnetGraphic.
        
        // Compass
        Compass compassModel = new Compass( barMagnetModel ); 
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( true );
        compassModel.setEnabled( false );
        model.addModelElement( compassModel );
        
        // Pickup Coil
        PickupCoil pickupCoilModel = new PickupCoil( barMagnetModel );
        pickupCoilModel.setNumberOfLoops( NUMBER_OF_LOOPS );
        pickupCoilModel.setLoopArea( LOOP_AREA );
        pickupCoilModel.setDirection( 0 /* radians */ );
        pickupCoilModel.setMaxVoltage( FaradayConfig.MAX_PICKUP_EMF );
        pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( pickupCoilModel );
       
        // Lightbulb
        Lightbulb lightbulbModel = new Lightbulb( pickupCoilModel );
        lightbulbModel.setEnabled( true );
        
        // Volt Meter
        Voltmeter voltmeterModel = new Voltmeter( pickupCoilModel );
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

        // Bar Magnet
        BarMagnetGraphic barMagnetGraphic = new BarMagnetGraphic( apparatusPanel, barMagnetModel );
        apparatusPanel.addChangeListener( barMagnetGraphic );
        apparatusPanel.addGraphic( barMagnetGraphic, BAR_MAGNET_LAYER );
        
        // Pickup Coil
        PickupCoilGraphic pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel, model, 
                pickupCoilModel, lightbulbModel, voltmeterModel, barMagnetModel );
        apparatusPanel.addChangeListener( pickupCoilGraphic );
        apparatusPanel.addGraphic( pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );

        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, barMagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
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
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, barMagnetModel );
        fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        fieldMeterGraphic.setVisible( false );
        apparatusPanel.addChangeListener( fieldMeterGraphic );
        apparatusPanel.addGraphic( fieldMeterGraphic, FIELD_METER_LAYER );

        // Collision detection
        barMagnetGraphic.getCollisionDetector().add( compassGraphic );
        barMagnetGraphic.getCollisionDetector().add( pickupCoilGraphic );
        compassGraphic.getCollisionDetector().add( barMagnetGraphic );
        compassGraphic.getCollisionDetector().add( pickupCoilGraphic );
        pickupCoilGraphic.getCollisionDetector().add( barMagnetGraphic );
        pickupCoilGraphic.getCollisionDetector().add( compassGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        {
            ControlPanel controlPanel = new ControlPanel( this );

            BarMagnetPanel barMagnetPanel = new BarMagnetPanel(
                    barMagnetModel, compassModel, barMagnetGraphic, gridGraphic, fieldMeterGraphic );
            barMagnetPanel.setSeeInsideVisible( false );
            barMagnetPanel.setFieldMeterEnabled( false );
            controlPanel.addFullWidth( barMagnetPanel );
            
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
        
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, barMagnetModel, pickupCoilModel );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
    }
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     * It disappears when the bar magnet or pickup coil is moved.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic implements SimpleObserver {

        private BarMagnet _barMagnetModel;
        private Point2D _barMagnetLocation;
        private PickupCoil _pickupCoilModel;
        private Point2D _pickupCoilLocation;

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         * @param barMagnetModel
         * @param pickupCoilModel
         */
        public ThisWiggleMeGraphic( Component component, BaseModel model, BarMagnet barMagnetModel, PickupCoil pickupCoilModel ) {
            super( component, model );

            _barMagnetModel = barMagnetModel;
            _barMagnetLocation = barMagnetModel.getLocation();
            barMagnetModel.addObserver( this );
            
            _pickupCoilModel = pickupCoilModel;
            _pickupCoilLocation = pickupCoilModel.getLocation();
            pickupCoilModel.addObserver( this );
            
            setText( SimStrings.get( "PickupCoilModule.wiggleMe" ) );
            addArrow( WiggleMeGraphic.BOTTOM_CENTER, new Vector2D( 0, 75 ) );
            addArrow( WiggleMeGraphic.MIDDLE_RIGHT, new Vector2D( 75, 0 ) );
            setLocation( 150, 250 );
            setRange( 20, 10 );
            setEnabled( true );
        }

        /*
         * @see edu.colorado.phet.common.util.SimpleObserver#update()
         * 
         * If the bar magnet or pickup coil is moved, disable and unwire the wiggle me.
         */
        public void update() {
            if ( !_barMagnetLocation.equals( _barMagnetModel.getLocation() ) ||
                 !_pickupCoilLocation.equals( _pickupCoilModel.getLocation() ) ) {
                // Disable
                setEnabled( false );
                // Unwire
                _barMagnetModel.removeObserver( this );
                _pickupCoilModel.removeObserver( this );
            }
        }
    }
}