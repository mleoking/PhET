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
import java.awt.Dimension;
import java.awt.Point;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.BarMagnetControlPanel;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.util.IRescaler;
import edu.colorado.phet.faraday.util.MagneticFieldRescaler;
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
    private static final double GRID_LAYER = 1;
    private static final double BAR_MAGNET_LAYER = 2;
    private static final double COMPASS_LAYER = 3;
    private static final double FIELD_METER_LAYER = 4;
    private static final double DEBUG_LAYER = FaradayConfig.DEBUG_LAYER;
    private static final double HELP_LAYER = FaradayConfig.HELP_LAYER;

    // Locations
    private static final Point MAGNET_LOCATION = new Point( 400, 300 );
    private static final Point COMPASS_LOCATION = new Point( 150, 200 );
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
    public BarMagnetModule( ApplicationModel appModel ) {
        
        super( SimStrings.get( "BarMagnetModule.title" ) );
        assert( appModel != null );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Clock
        AbstractClock clock = appModel.getClock();

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
        barMagnetModel.setSize( FaradayConfig.BAR_MAGNET_SIZE );
        model.addModelElement( barMagnetModel );

        // Rescaler
        IRescaler rescaler = new MagneticFieldRescaler( barMagnetModel );
        
        // Compass model
        Compass compassModel = new Compass( barMagnetModel );
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( true );
        model.addModelElement( compassModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Bar Magnet
        BarMagnetGraphic barMagnetGraphic = new BarMagnetGraphic( apparatusPanel, barMagnetModel );
        apparatusPanel.addChangeListener( barMagnetGraphic );
        apparatusPanel.addGraphic( barMagnetGraphic, BAR_MAGNET_LAYER );
        
        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, barMagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setRescaler( rescaler );
        gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        gridGraphic.setAlphaEnabled( ! APPARATUS_BACKGROUND.equals( Color.BLACK ) );
        apparatusPanel.addChangeListener( gridGraphic );
        apparatusPanel.addGraphic( gridGraphic, GRID_LAYER );
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
        
        // Debugger
//        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
//        debugger.setLocationColor( Color.GREEN );
//        debugger.add( compassGraphic );
//        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );
        
        // Collision detection
        barMagnetGraphic.getCollisionDetector().add( compassGraphic );
        compassGraphic.getCollisionDetector().add( barMagnetGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        BarMagnetControlPanel controlPanel = new BarMagnetControlPanel( this, 
                barMagnetModel, compassModel, barMagnetGraphic, gridGraphic, fieldMeterGraphic );
        this.setControlPanel( controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }
}
