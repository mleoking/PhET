/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.module;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayResources;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.control.FaradayControlPanel;
import edu.colorado.phet.faraday.control.panel.BarMagnetPanel;
import edu.colorado.phet.faraday.control.panel.PickupCoilPanel;
import edu.colorado.phet.faraday.control.panel.ScalePanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.util.Vector2D;
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
    private static final double COMPASS_GRID_LAYER = 2;
    private static final double BAR_MAGNET_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double PICKUP_COIL_FRONT_LAYER = 5;
    private static final double FIELD_METER_LAYER = 6;

    // Locations
    private static final Point BAR_MAGNET_LOCATION = new Point( 200, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 400 );
    private static final Point COMPASS_LOCATION = new Point( 100, 525 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 150, 250 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Bar Magnet
    private static final double BAR_MAGNET_STRENGTH = 0.75 * FaradayConstants.BAR_MAGNET_STRENGTH_MAX;
    private static final double BAR_MAGNET_DIRECTION = 0.0; // radians
    
    // Pickup Coil parameters
    private static final int PICKUP_COIL_NUMBER_OF_LOOPS = 2;
    private static final double PICKUP_COIL_LOOP_AREA = 0.75 * FaradayConstants.MAX_PICKUP_LOOP_AREA;
    private static final double PICKUP_COIL_DIRECTION = 0.0; // radians
    private static final double PICKUP_COIL_DISTANCE_EXPONENT = 3.0;
    
    // Scaling
    private static final double LIGHTBULB_SCALE = 4.0;
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
    private CompassGridGraphic _gridGraphic;
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
        _barMagnetModel.setMaxStrength( FaradayConstants.BAR_MAGNET_STRENGTH_MAX );
        _barMagnetModel.setMinStrength( FaradayConstants.BAR_MAGNET_STRENGTH_MIN );
        _barMagnetModel.setStrength( BAR_MAGNET_STRENGTH );
        _barMagnetModel.setLocation( BAR_MAGNET_LOCATION );
        _barMagnetModel.setDirection( BAR_MAGNET_DIRECTION );
        // Do NOT set the size -- size is set by the associated BarMagnetGraphic.
        
        // Compass
        _compassModel = new Compass( _barMagnetModel ); 
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
        _pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( ELECTRON_SPEED_SCALE );
        apparatusPanel.addChangeListener( _pickupCoilGraphic );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getForeground(), PICKUP_COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( _pickupCoilGraphic.getBackground(), PICKUP_COIL_BACK_LAYER );

        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, _barMagnetModel, FaradayConstants.GRID_SPACING, FaradayConstants.GRID_SPACING );
        _gridGraphic.setRescalingEnabled( true );
        _gridGraphic.setNeedleSize( FaradayConstants.GRID_NEEDLE_SIZE );
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
                    _barMagnetModel, _compassModel, _fieldMeterModel, _barMagnetGraphic, _gridGraphic );
            _barMagnetPanel.setSeeInsideVisible( false );
            controlPanel.addControlFullWidth( _barMagnetPanel );
            
            // Spacer
            controlPanel.addVerticalSpace( FaradayControlPanel.DEFAULT_VERTICAL_SPACE );
            
            // Pickup Coil controls
            _pickupCoilPanel = new PickupCoilPanel( 
                    _pickupCoilModel, _pickupCoilGraphic, _lightbulbModel, _voltmeterModel );
            controlPanel.addControlFullWidth( _pickupCoilPanel );
            
            // Scaling calibration
            if ( FaradayConstants.DEBUG_ENABLE_SCALE_PANEL ) {
                controlPanel.addVerticalSpace( FaradayControlPanel.DEFAULT_VERTICAL_SPACE );
                
                ScalePanel scalePanel = new ScalePanel( _lightbulbModel, _voltmeterModel, _pickupCoilGraphic, null );
                controlPanel.addControlFullWidth( scalePanel );
            }
            
            // Reset button
            controlPanel.addResetAllButton( this );
        }

        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, _barMagnetModel, _pickupCoilModel );
        wiggleMe.setLocation( WIGGLE_ME_LOCATION );
        wiggleMe.setEnabled( false ); // per 4/27/2005 status meeting
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
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
        
        // Bar Magnet view
        _barMagnetGraphic.setTransparencyEnabled( false );
        
        // Pickup Coil view
        _pickupCoilGraphic.getCoilGraphic().setElectronAnimationEnabled( true );
        
        // Compass Grid view
        _gridGraphic.setVisible( true );
        
        // Field Meter view
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
        // Control panel
        _barMagnetPanel.update();
        _pickupCoilPanel.update();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
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
            
            setText( FaradayResources.getString( "PickupCoilModule.wiggleMe" ) );
            addArrow( WiggleMeGraphic.BOTTOM_CENTER, new Vector2D( 0, 75 ) );
            addArrow( WiggleMeGraphic.MIDDLE_RIGHT, new Vector2D( 75, 0 ) );
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