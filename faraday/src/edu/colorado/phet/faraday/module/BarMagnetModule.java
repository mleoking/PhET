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
import java.awt.Point;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.BarMagnetControlPanel;
import edu.colorado.phet.faraday.model.AbstractCompass;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.model.Compass;
import edu.colorado.phet.faraday.view.*;


/**
 * BarMagnetModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetModule extends Module {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double GRID_LAYER = 1;
    private static final double MAGNET_LAYER = 2;
    private static final double COMPASS_LAYER = 3;
    private static final double METER_LAYER = 4;
    private static final double DEBUG_LAYER = FaradayConfig.DEBUG_LAYER;
    private static final double HELP_LAYER = FaradayConfig.HELP_LAYER;

    // Locations
    private static final Point MAGNET_LOCATION = new Point( 350, 350 );
    private static final Point COMPASS_LOCATION = new Point( 100, 400 );
    private static final Point GRID_LOCATION = new Point( 0, 0 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;

    // Magnet parameters
    private static final double MAGNET_STRENGTH = 200;

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
        AbstractMagnet magnetModel = new BarMagnet();
        magnetModel.setStrength( MAGNET_STRENGTH );
        magnetModel.setLocation( MAGNET_LOCATION );
        magnetModel.setDirection( 0 );
        magnetModel.setSize( FaradayConfig.BAR_MAGNET_SIZE );
        model.addModelElement( magnetModel );
        
        // Compass model
        AbstractCompass compassModel = new Compass( magnetModel );
        compassModel.setLocation( COMPASS_LOCATION );
        model.addModelElement( compassModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );

        // Bar Magnet
        BarMagnetGraphic magnetGraphic = new BarMagnetGraphic( apparatusPanel, magnetModel );
        apparatusPanel.addGraphic( magnetGraphic, MAGNET_LAYER );
        
        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, magnetModel, 
                FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setLocation( GRID_LOCATION );
        gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        apparatusPanel.addGraphic( gridGraphic, GRID_LAYER );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, magnetModel );
        fieldMeterGraphic.setLocation( 100, 100 );
        apparatusPanel.addGraphic( fieldMeterGraphic, METER_LAYER );
        
        // Debugger
        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
        debugger.setLocationColor( Color.GREEN );
        debugger.setLocationStrokeWidth( 1 );
//        debugger.add( __fieldMeterGraphic );
        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        BarMagnetControlPanel controlPanel = new BarMagnetControlPanel( this, 
                magnetModel, compassModel, magnetGraphic, gridGraphic, fieldMeterGraphic, apparatusPanel );
        this.setControlPanel( controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }  
}
