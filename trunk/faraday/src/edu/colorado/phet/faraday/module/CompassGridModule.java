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
import edu.colorado.phet.faraday.control.CompassGridControlPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.view.*;


/**
 * CompassGridModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassGridModule extends Module {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double GRID_LAYER = 1;
    private static final double MAGNET_LAYER = 2;
    private static final double COMPASS_LAYER = 3;
    private static final double DEBUG_LAYER = FaradayConfig.DEBUG_LAYER;
    private static final double HELP_LAYER = FaradayConfig.HELP_LAYER;

    // Locations of model components
    private static final Point MAGNET_LOCATION = new Point( 350, 350 );

    // Locations of view components
    private static final Point GRID_LOCATION = new Point( 0, 0 );
    private static final Point COMPASS_LOCATION = new Point( 100, 400 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = FaradayConfig.APPARATUS_BACKGROUND;

    public static final double MAGNET_STRENGTH_MIN = 100;
    public static final double MAGNET_STRENGTH_MAX = 999;
    private static final double MAGNET_STRENGTH = 350;
    
    public static final Dimension MAGNET_SIZE_MIN = new Dimension( 10, 10 );
    public static final Dimension MAGNET_SIZE_MAX = new Dimension( 500, 200 );
    private static final Dimension MAGNET_SIZE = new Dimension( 250, 50 );
    
    public static final int GRID_X_SPACING_MIN = 20;
    public static final int GRID_X_SPACING_MAX = 200;
    private static final int GRID_X_SPACING = 40;
    
    public static final int GRID_Y_SPACING_MIN = 20;
    public static final int GRID_Y_SPACING_MAX = 200;
    private static final int GRID_Y_SPACING = 40;
    
    public static final Dimension GRID_NEEDLE_SIZE_MIN = new Dimension( 1, 4 );
    public static final Dimension GRID_NEEDLE_SIZE_MAX = new Dimension( 100, 50 );
    private static final Dimension GRID_NEEDLE_SIZE = new Dimension( 25, 5 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private AbstractMagnet _magnetModel;
    
    // View
    private CompassGridGraphic _gridGraphic;
    
    // Control
    private CompassGridControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public CompassGridModule( ApplicationModel appModel ) {
        
        super( SimStrings.get( "CompassGridModule.title" ) );
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
        if ( FaradayConfig.HOLLYWOOD_MAGNET ) {
            System.out.println( "*** HOLLYWOOD_MAGNET is enabled ***" ); // DEBUG
            _magnetModel = new HollywoodMagnet();
        }
        else {
            _magnetModel = new BarMagnet();
        }
        _magnetModel.setStrength( MAGNET_STRENGTH );
        _magnetModel.setLocation( MAGNET_LOCATION );
        _magnetModel.setDirection( 0 );
        _magnetModel.setSize( MAGNET_SIZE );
        model.addModelElement( _magnetModel );
        
        // Compass model
        AbstractCompass compassModel = null;
        if ( FaradayConfig.HOLLYWOOD_COMPASS ) {
            System.out.println( "*** HOLLYWOOD_COMPASS is enabled ***" ); // DEBUG
            compassModel = new HollywoodCompass( _magnetModel );
        }
        else {
            compassModel = new Compass( _magnetModel );
        }
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
        BarMagnetGraphic magnetGraphic = new BarMagnetGraphic( apparatusPanel, _magnetModel );
        apparatusPanel.addGraphic( magnetGraphic, MAGNET_LAYER );
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, _magnetModel, GRID_X_SPACING, GRID_Y_SPACING );
        _gridGraphic.setLocation( GRID_LOCATION );
        _gridGraphic.setNeedleSize( GRID_NEEDLE_SIZE );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Probe
        FieldProbeGraphic probeGraphic = new FieldProbeGraphic( apparatusPanel, _magnetModel );
        probeGraphic.setLocation( 100, 100 );
        apparatusPanel.addGraphic( probeGraphic, DEBUG_LAYER );
        
        // Debugger
        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
        debugger.setLocationColor( Color.GREEN );
//        debugger.add( probeGraphic );
        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new CompassGridControlPanel( this );
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
        _controlPanel.setBarMagnetStrength( MAGNET_STRENGTH );
        _controlPanel.setBarMagnetSize( MAGNET_SIZE );
        _controlPanel.setGridSpacing( GRID_X_SPACING, GRID_Y_SPACING );
        _controlPanel.setGridNeedleSize( GRID_NEEDLE_SIZE );
    }
    
    /**
     * Flips the magnet's polarity.
     */
    public void flipMagnetPolarity() {
        double direction = _magnetModel.getDirection();
        direction = ( direction + 180 ) % 360;
        _magnetModel.setDirection( direction );
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
     * @param x space between compasses in the X direction
     * @param y space between compasses in the Y direction
     */
    public void setGridSpacing( int x, int y ) {
        _gridGraphic.setSpacing( x, y );
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
