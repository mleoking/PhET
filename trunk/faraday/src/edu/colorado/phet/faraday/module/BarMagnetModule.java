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
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private AbstractMagnet _magnetModel;
    private AbstractCompass _compassModel;
    
    // View
    private BarMagnetGraphic _magnetGraphic;
    private CompassGridGraphic _gridGraphic;
    private FieldMeterGraphic _fieldMeterGraphic;
    
    // Control
    private BarMagnetControlPanel _controlPanel;
    
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
        _magnetModel = new BarMagnet();
        _magnetModel.setStrength( MAGNET_STRENGTH );
        _magnetModel.setLocation( MAGNET_LOCATION );
        _magnetModel.setDirection( 0 );
        _magnetModel.setSize( FaradayConfig.BAR_MAGNET_SIZE );
        model.addModelElement( _magnetModel );
        
        // Compass model
        _compassModel = new Compass( _magnetModel );
        _compassModel.setLocation( COMPASS_LOCATION );
        model.addModelElement( _compassModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );

        // Bar Magnet
        _magnetGraphic = new BarMagnetGraphic( apparatusPanel, _magnetModel );
        apparatusPanel.addGraphic( _magnetGraphic, MAGNET_LAYER );
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, _magnetModel, 
                FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        _gridGraphic.setLocation( GRID_LOCATION );
        _gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, _compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        _fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, _magnetModel );
        _fieldMeterGraphic.setLocation( 100, 100 );
        apparatusPanel.addGraphic( _fieldMeterGraphic, METER_LAYER );
        
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
        _controlPanel = new BarMagnetControlPanel( this );
        this.setControlPanel( _controlPanel );
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // Initalize
        //----------------------------------------------------------------------------
        
        reset();
    }
   
    //----------------------------------------------------------------------------
    // Controller methods
    //----------------------------------------------------------------------------

    /**
     * Resets everything to the initial values.
     */
    public void reset() {
        // Set state.
        _magnetModel.setStrength( MAGNET_STRENGTH );
        _magnetModel.setSize( FaradayConfig.BAR_MAGNET_SIZE );
        _compassModel.setEnabled( true );
        
        // Synchronize control panel.
        _controlPanel.setMagnetStrength( _magnetModel.getStrength() );
        _controlPanel.setMagnetSize( _magnetModel.getSize() );
        _controlPanel.setMagnetTransparencyEnabled( false );
        _controlPanel.setMeterEnabled( true );
        _controlPanel.setCompassEnabled( _compassModel.isEnabled() );
        
        // Debug controls
        _controlPanel.setGridSpacing( FaradayConfig.GRID_SPACING );
        _controlPanel.setGridNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
    }
    
    /**
     * Flips the magnet's polarity.
     */
    public void flipMagnetPolarity() {
        double direction = _magnetModel.getDirection();
        direction = ( direction + 180 ) % 360;
        _magnetModel.setDirection( direction );
        _compassModel.startMovingNow();
    }
    
    /**
     * Sets the magnet's strength.
     * 
     * @param strength the strength
     */
    public void setMagnetStrength( double strength ) {
        _magnetModel.setStrength( strength );
    }
    
    /**
     * Set the transparency of the magnet graphic.
     * 
     * @param enabled true for transparent, false for opaque
     */
    public void setMagnetTransparencyEnabled( boolean enabled ) {
        _magnetGraphic.setTransparencyEnabled( enabled );
    }
    
    /**
     * Enables and disables the Field Meter.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setMeterEnabled( boolean enabled ) {
        _fieldMeterGraphic.setVisible( enabled );
    }
    
    /**
     * Enables and disables the compass.
     * 
     * @param enabled true to enable, false to disable
     */
    public void setCompassEnabled( boolean enabled ) {
        _compassModel.setEnabled( enabled );
    }
    
    /**
     * Sets the magnet's size.
     * 
     * @param width the width
     * @param height the height
     */
    public void setMagnetSize( Dimension size ) {
        _magnetModel.setSize( size );
    }
    
    /**
     * Sets the spacing betweeen compasses in the grid.
     * 
     * @param spacing the amount of space between needles in the X and Y directions
     */
    public void setGridSpacing( int spacing ) {
        _gridGraphic.setSpacing( spacing, spacing );
    }
    
    /**
     * Sets the size of the compass needles in the grid.
     * 
     * @param size the size
     */
    public void setGridNeedleSize( Dimension size ) {
        _gridGraphic.setNeedleSize( size );
    }
}
