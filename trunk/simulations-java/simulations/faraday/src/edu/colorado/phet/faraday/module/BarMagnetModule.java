/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.module;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.control.FaradayControlPanel;
import edu.colorado.phet.faraday.control.panel.BarMagnetPanel;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.model.FieldMeter;
import edu.colorado.phet.faraday.util.Vector2D;
import edu.colorado.phet.faraday.view.*;


/**
 * BarMagnetModule is the "Bar Magnet" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BarMagnetModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double B_FIELD_LAYER = 1;
    private static final double BAR_MAGNET_LAYER = 2;
    private static final double COMPASS_LAYER = 3;
    private static final double FIELD_METER_LAYER = 4;
    private static final double EARTH_LAYER = 5;

    // Locations
    private static final Point BAR_MAGNET_LOCATION = new Point( 450, 300 );
    private static final Point COMPASS_LOCATION = new Point( 150, 300 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point WIGGLE_ME_LOCATION = new Point( 250, 175 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Bar Magnet
    private static final Dimension BAR_MAGNET_SIZE = FaradayConstants.BAR_MAGNET_SIZE;
    private static final double BAR_MAGNET_STRENGTH = 0.75 * FaradayConstants.BAR_MAGNET_STRENGTH_MAX;
    private static final double BAR_MAGNET_DIRECTION = 0.0; // radians
         
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BarMagnet _barMagnetModel;
    private Compass _compassModel;
    private FieldMeter _fieldMeterModel;
    private BarMagnetGraphic _barMagnetGraphic;
    private EarthGraphic _earthGraphic;
    private BFieldOutsideGraphic _bFieldOutsideGraphic;
    private BFieldInsideGraphic _bFieldInsideGraphic;
    private BarMagnetPanel _barMagnetPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BarMagnetModule( boolean wiggleMeEnabled ) {
        
        super( FaradayStrings.TITLE_BAR_MAGNET_MODULE );
        
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
        
        // Compass model
        _compassModel = new Compass( _barMagnetModel, getClock() );
        _compassModel.setLocation( COMPASS_LOCATION );
        _compassModel.setBehavior( Compass.KINEMATIC_BEHAVIOR );
        model.addModelElement( _compassModel );
        
        // Field Meter
        _fieldMeterModel = new FieldMeter( _barMagnetModel );
        _fieldMeterModel.setLocation( FIELD_METER_LOCATION );
        _fieldMeterModel.setEnabled( false );
        
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
        
        // Earth
        _earthGraphic = new EarthGraphic( apparatusPanel, _barMagnetModel );
        apparatusPanel.addGraphic( _earthGraphic, EARTH_LAYER );
        
        // B-field inside the magnet
        _bFieldInsideGraphic = new BFieldInsideGraphic( apparatusPanel, _barMagnetModel );
        _bFieldInsideGraphic.setNeedleSize( FaradayConstants.GRID_NEEDLE_SIZE );
        apparatusPanel.addGraphic( _bFieldInsideGraphic, BAR_MAGNET_LAYER );
        
        // B-field outside the magnet
        _bFieldOutsideGraphic = new BFieldOutsideGraphic( apparatusPanel, _barMagnetModel, FaradayConstants.GRID_SPACING, FaradayConstants.GRID_SPACING, false /* inMagnetPlane */ );
        _bFieldOutsideGraphic.setNeedleSize( FaradayConstants.GRID_NEEDLE_SIZE );
        _bFieldOutsideGraphic.setGridBackground( APPARATUS_BACKGROUND );
        apparatusPanel.addChangeListener( _bFieldOutsideGraphic );
        apparatusPanel.addGraphic( _bFieldOutsideGraphic, B_FIELD_LAYER );
        super.setBFieldOutsideGraphic( _bFieldOutsideGraphic );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, _compassModel );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, _fieldMeterModel );
        fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        apparatusPanel.addChangeListener( fieldMeterGraphic );
        apparatusPanel.addGraphic( fieldMeterGraphic, FIELD_METER_LAYER );
        
        // Collision detection
        _barMagnetGraphic.getCollisionDetector().add( compassGraphic );
        compassGraphic.getCollisionDetector().add( _barMagnetGraphic );
        
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
                    _barMagnetModel, _compassModel, _fieldMeterModel,
                    _bFieldInsideGraphic, _bFieldOutsideGraphic, _earthGraphic );
            controlPanel.addControlFullWidth( _barMagnetPanel );
            
            // Reset button
            controlPanel.addResetAllButton( this ); 
        }
        
        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Wiggle Me
        if ( wiggleMeEnabled ) {
            ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, _barMagnetModel, _compassModel );
            wiggleMe.setLocation( WIGGLE_ME_LOCATION );
            apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
        }
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
        _compassModel.setEnabled( true );
        
        // B-field inside magnet
        if ( FaradayConstants.HIDE_BFIELD_FEATURE ) {
            _bFieldInsideGraphic.setVisible( false );
            _barMagnetPanel.setSeeInsideControlVisible( false );
        }
        else {
            _bFieldInsideGraphic.setVisible( false );
        }

        // Earth view
        _earthGraphic.setVisible( false );
        
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
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setShowEarthVisible( boolean visible ) {
        _barMagnetPanel.setShowEarthControlVisible( visible );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     * It disappears when the bar magnet or compass is moved.
     */
    private static class ThisWiggleMeGraphic extends WiggleMeGraphic implements SimpleObserver {

        private BarMagnet _barMagnetModel;
        private Point2D _barMagnetLocation;
        private Compass _compassModel;
        private Point2D _compassLocation;

        /**
         * Sole constructor.
         * 
         * @param component
         * @param model
         * @param barMagnetModel
         * @param compassModel
         */
        public ThisWiggleMeGraphic( Component component, BaseModel model, BarMagnet barMagnetModel, Compass compassModel ) {
            super( component, model );

            _barMagnetModel = barMagnetModel;
            _barMagnetLocation = barMagnetModel.getLocation();
            barMagnetModel.addObserver( this );
            
            _compassModel = compassModel;
            _compassLocation = compassModel.getLocation();
            compassModel.addObserver( this );
            
            setText( FaradayStrings.WIGGLEME_BAR_MAGNET );
            addArrow( WiggleMeGraphic.BOTTOM_LEFT, new Vector2D( -40, 50 ) );
            addArrow( WiggleMeGraphic.BOTTOM_RIGHT, new Vector2D( 40, 50 ) );
            setRange( 20, 10 );
            setEnabled( true );
        }

        /*
         * @see edu.colorado.phet.common.util.SimpleObserver#update()
         * 
         * If the bar magnet or compass is moved, disable and unwire the wiggle me.
         */
        public void update() {
            if ( !_barMagnetLocation.equals( _barMagnetModel.getLocation() ) ||
                 !_compassLocation.equals( _compassModel.getLocation() ) ) {
                // Disable
                setEnabled( false );
                // Unwire
                _barMagnetModel.removeObserver( this );
                _compassModel.removeObserver( this );
            }
        }
    }
}
