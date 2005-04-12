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
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.util.Vector2D;
import edu.colorado.phet.faraday.view.*;


/**
 * BarMagnetModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double COMPASS_GRID_LAYER = 1;
    private static final double BAR_MAGNET_LAYER = 2;
    private static final double COMPASS_LAYER = 3;
    private static final double FIELD_METER_LAYER = 4;

    // Locations
    private static final Point BAR_MAGNET_LOCATION = new Point( 450, 300 );
    private static final Point COMPASS_LOCATION = new Point( 150, 300 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public BarMagnetModule( AbstractClock clock ) {
        
        super( SimStrings.get( "BarMagnetModule.title" ), clock );

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
        barMagnetModel.setLocation( BAR_MAGNET_LOCATION );
        barMagnetModel.setDirection( 0 /* radians */ );
        // Do NOT set the size -- size is set by the associated BarMagnetGraphic.
        
        // Compass model
        Compass compassModel = new Compass( barMagnetModel );
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( true );
        model.addModelElement( compassModel );
        
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
        
        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, barMagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setRescalingEnabled( true );
        gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        gridGraphic.setGridBackground( APPARATUS_BACKGROUND );
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
        compassGraphic.getCollisionDetector().add( barMagnetGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        {
            ControlPanel controlPanel = new ControlPanel( this );
            BarMagnetPanel barMagnetPanel = new BarMagnetPanel( 
                    barMagnetModel, compassModel, 
                    barMagnetGraphic, gridGraphic, fieldMeterGraphic );
            controlPanel.addFullWidth( barMagnetPanel );
            setControlPanel( controlPanel );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        // Wiggle Me
        ThisWiggleMeGraphic wiggleMe = new ThisWiggleMeGraphic( apparatusPanel, model, barMagnetModel, compassModel );
        apparatusPanel.addGraphic( wiggleMe, HELP_LAYER );
    }
    
    /**
     * ThisWiggleMeGraphic is the wiggle me for this module.
     * It disappears when the bar magnet or compass is moved.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
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
            
            setText( SimStrings.get( "BarMagnetModule.wiggleMe" ) );
            addArrow( WiggleMeGraphic.BOTTOM_LEFT, new Vector2D( -40, 50 ) );
            addArrow( WiggleMeGraphic.BOTTOM_RIGHT, new Vector2D( 40, 50 ) );
            setLocation( 250, 175 );
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
