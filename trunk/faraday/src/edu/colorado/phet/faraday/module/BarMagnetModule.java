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
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.BarMagnetControlPanel;
import edu.colorado.phet.faraday.control.DeveloperPanel;
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
public class BarMagnetModule extends Module implements ICompassGridModule {

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
    private static final Point MAGNET_LOCATION = new Point( 400, 300 );
    private static final Point COMPASS_LOCATION = new Point( 150, 200 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;

    // Magnet parameters
    private static final double MAGNET_STRENGTH = 0.75 * FaradayConfig.MAGNET_STRENGTH_MAX;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private CompassGridGraphic _gridGraphic;
    
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
        magnetModel.setDirection( 0 /* radians */ );
        magnetModel.setSize( FaradayConfig.BAR_MAGNET_SIZE );
        model.addModelElement( magnetModel );
        
        // Compass model
        Compass compassModel = new Compass( magnetModel );
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( true );
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
        _gridGraphic = new CompassGridGraphic( apparatusPanel, magnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        _gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        _gridGraphic.setAlphaEnabled( ! APPARATUS_BACKGROUND.equals( Color.BLACK ) );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, magnetModel );
        fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        fieldMeterGraphic.setVisible( false );
        apparatusPanel.addGraphic( fieldMeterGraphic, METER_LAYER );
        
        // Debugger
//        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
//        debugger.setLocationColor( Color.GREEN );
//        debugger.setLocationStrokeWidth( 1 );
//        debugger.add( compassGraphic );
//        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );
        
        // Collision detection
        magnetGraphic.getCollisionDetector().add( compassGraphic );
        compassGraphic.getCollisionDetector().add( magnetGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        BarMagnetControlPanel controlPanel = new BarMagnetControlPanel( this, 
                magnetModel, compassModel, magnetGraphic, fieldMeterGraphic );
        this.setControlPanel( controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }

    //----------------------------------------------------------------------------
    // ICompassGridModule implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setGridSpacing(int, int)
     */
    public void setGridSpacing( int xSpacing, int ySpacing ) {
        _gridGraphic.setSpacing( xSpacing, ySpacing );
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridXSpacing()
     */
    public int getGridXSpacing() {
        return _gridGraphic.getXSpacing();
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridYSpacing()
     */
    public int getGridYSpacing() {
        return _gridGraphic.getYSpacing();
    }  
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setGridNeedleSize(Dimension)
     */
    public void setGridNeedleSize( Dimension size ) {
        _gridGraphic.setNeedleSize( size );
    }

    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#getGridNeedleSize()
     */
    public Dimension getGridNeedleSize() {
        return _gridGraphic.getNeedleSize();
    }
    
    /*
     * @see edu.colorado.phet.faraday.module.ICompassGridModule#setAlphaEnabled(boolean)
     */
    public void setAlphaEnabled( boolean enabled ) {
        _gridGraphic.setAlphaEnabled( enabled );
    }
}
