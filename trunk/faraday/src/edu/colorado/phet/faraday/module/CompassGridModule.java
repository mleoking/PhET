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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.CompassGridControlPanel;
import edu.colorado.phet.faraday.model.HollywoodMagnet;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.GridGraphic;


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
    private static final double BAR_MAGNET_LAYER = 2;
    private static final double HELP_LAYER = Double.MAX_VALUE;

    // Locations of model components
    private static final Point BAR_MAGNET_LOCATION = new Point( 350, 350 );

    // Locations of view components
    private static final Point GRID_LOCATION = new Point( 0, 0 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = FaradayConfig.APPARATUS_BACKGROUND;

    public static final double MAGNET_STRENGTH_MIN = 100;
    public static final double MAGNET_STRENGTH_MAX = 999;
    private static final double MAGNET_STRENGTH = 350;
    
    public static final Dimension MAGNET_SIZE_MIN = new Dimension( 10, 10 );
    public static final Dimension MAGNET_SIZE_MAX = new Dimension( 500, 200 );
    private static final Dimension MAGNET_SIZE = new Dimension( 250, 50 );
    
    public static final int X_SPACING_MIN = 20;
    public static final int X_SPACING_MAX = 200;
    private static final int X_SPACING = 25;
    
    public static final int Y_SPACING_MIN = 20;
    public static final int Y_SPACING_MAX = 200;
    private static final int Y_SPACING = 25;
    
    public static final Dimension NEEDLE_SIZE_MIN = new Dimension( 1, 4 );
    public static final Dimension NEEDLE_SIZE_MAX = new Dimension( 100, 50 );
    private static final Dimension NEEDLE_SIZE = new Dimension( 25, 5 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private HollywoodMagnet _magnetModel;
    
    // View
    private GridGraphic _gridGraphic;
    
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

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Clock
        AbstractClock clock = appModel.getClock();

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Bar Magnet
        _magnetModel = new HollywoodMagnet();
        _magnetModel.setStrength( MAGNET_STRENGTH );
        _magnetModel.setLocation( BAR_MAGNET_LOCATION );
        _magnetModel.setDirection( 0 );
        _magnetModel.setSize( MAGNET_SIZE );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );

        // Bar Magnet
        BarMagnetGraphic magnetGraphic = new BarMagnetGraphic( apparatusPanel, _magnetModel );
        apparatusPanel.addGraphic( magnetGraphic, BAR_MAGNET_LAYER );
        
        // Grid
        _gridGraphic = new GridGraphic( apparatusPanel, _magnetModel, X_SPACING, Y_SPACING );
        _gridGraphic.setLocation( 0, 0 );
        _gridGraphic.setNeedleSize( NEEDLE_SIZE );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new CompassGridControlPanel( this );
        this.setControlPanel( _controlPanel );
        
        //----------------------------------------------------------------------------
        // Observers
        //----------------------------------------------------------------------------
        
        _magnetModel.addObserver( magnetGraphic );
        _magnetModel.addObserver( _gridGraphic );
        
        //----------------------------------------------------------------------------
        // Listeners
        //----------------------------------------------------------------------------
        
        // We don't really know how to lay out the compass grid until the 
        // apparatus panel is displayed.
        apparatusPanel.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                resetGridSpacing();
            }
        });
        
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
        // System.out.println( "reset" ); // DEBUG
        _controlPanel.setBarMagnetStrength( MAGNET_STRENGTH );
        _controlPanel.setBarMagnetSize( MAGNET_SIZE );
        _controlPanel.setGridSpacing( X_SPACING, Y_SPACING );
        _controlPanel.setGridNeedleSize( NEEDLE_SIZE );
    }
    
    /**
     * Flips the magnet's polarity.
     */
    public void flipMagnetPolarity() {
        //System.out.println( "flipMagnetPolarity" ); // DEBUG
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
        //System.out.println( "setMagnetStrength " + strength ); // DEBUG
        _magnetModel.setStrength( strength );
    }
    
    /**
     * Sets the magnet's size.
     * 
     * @param width the width
     * @param height the height
     */
    public void setMagnetSize( Dimension size ) {
        //System.out.println( "setBarMagnetSize " + size );
        _magnetModel.setSize( size );
    }
    
    /**
     * Sets the spacing betweeen compasses in the grid.
     * 
     * @param x space between compasses in the X direction
     * @param y space between compasses in the Y direction
     */
    public void setGridSpacing( int x, int y ) {
        //System.out.println( "setGridSpacing " + x + "x" + y ); // DEBUG
        _gridGraphic.setSpacing( x, y );
    }
    
    /**
     * Resets the grid spacing.
     * This needs to be called when the apparatus panel changes size.
     */
    public void resetGridSpacing() {
        //System.out.println( "resetGridSpacing" );  // DEBUG
        int x = _gridGraphic.getXSpacing();
        int y = _gridGraphic.getYSpacing();
        _gridGraphic.setSpacing( x, y );
    }
    
    /**
     * Sets the size of the compass needles in the grid.
     * 
     * @param size the size
     */
    public void setGridNeedleSize( Dimension size ) {
        //System.out.println( "setNeedleSize " + size ); // DEBUG
        _gridGraphic.setNeedleSize( size );
    }
}
