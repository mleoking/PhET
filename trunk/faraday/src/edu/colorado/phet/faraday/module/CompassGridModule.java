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
import edu.colorado.phet.faraday.control.BarMagnetControlPanel;
import edu.colorado.phet.faraday.control.CompassGridControlPanel;
import edu.colorado.phet.faraday.model.BarMagnet;
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
    private static final Point GRID_LOCATiON = new Point( 0, 0 );
    private static final Point BAR_MAGNET_LOCATION = new Point( 300, 400 );

    // Locations of view components

    // Colors
    private static final Color APPARATUS_BACKGROUND = FaradayConfig.APPARATUS_BACKGROUND;

    private static final double BAR_MAGNET_STRENGTH = 500;
    private static final int GRID_X_SPACING = 100;
    private static final int GRID_Y_SPACING = 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private BarMagnet _barMagnetModel;
    
    // View
    private GridGraphic _gridGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * @param name
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
        _barMagnetModel = new BarMagnet();
        _barMagnetModel.setStrength( BAR_MAGNET_STRENGTH );
        _barMagnetModel.setLocation( BAR_MAGNET_LOCATION );
        _barMagnetModel.setDirection( 0 );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Control Panel
        CompassGridControlPanel controlPanel = new CompassGridControlPanel( this );
        this.setControlPanel( controlPanel );

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );

        // Bar Magnet
        BarMagnetGraphic barMagnetGraphic = new BarMagnetGraphic( apparatusPanel, _barMagnetModel );
        apparatusPanel.addGraphic( barMagnetGraphic, BAR_MAGNET_LAYER );
        
        // Grid
        _gridGraphic = new GridGraphic( apparatusPanel, _barMagnetModel, GRID_X_SPACING, GRID_Y_SPACING );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        //----------------------------------------------------------------------------
        // Observers
        //----------------------------------------------------------------------------
        
        _barMagnetModel.addObserver( barMagnetGraphic );
        _barMagnetModel.addObserver( _gridGraphic );
        
        //----------------------------------------------------------------------------
        // Listeners
        //----------------------------------------------------------------------------
        
        apparatusPanel.addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                resetGridSpacing();
//            }
            
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
        
        int strengthScale = BarMagnetControlPanel.STRENGTH_MIN_PERCENTAGE + (BarMagnetControlPanel.STRENGTH_MAX_PERCENTAGE - BarMagnetControlPanel.STRENGTH_MIN_PERCENTAGE)/2;
        controlPanel.setBarMagnetStrengthScale( strengthScale );
        controlPanel.setGridDensity( GRID_X_SPACING, GRID_Y_SPACING );
        controlPanel.setGridFollowsMagnent( false );
    }
   
    //----------------------------------------------------------------------------
    // Controller methods
    //----------------------------------------------------------------------------

    public void flipBarMagnetPolarity() {
        System.out.println( "flipBarMagnetPolarity" ); // DEBUG
        double direction = _barMagnetModel.getDirection();
        direction = ( direction + 180 ) % 360;
        _barMagnetModel.setDirection( direction );
    }
    
    public void scaleBarMagnetStrength( double scale ) {
        System.out.println( "scaleBarMagnetStrength " + scale ); // DEBUG
        // XXX
    }
    
    public void setGridSpacing( int x, int y ) {
        System.out.println( "setGridSpacing " + x + "x" + y ); // DEBUG
        _gridGraphic.setSpacing( x, y );
    }
    
    public void resetGridSpacing() {
        System.out.println( "resetGridSpacing" );
        int x = _gridGraphic.getXSpacing();
        int y = _gridGraphic.getYSpacing();
        _gridGraphic.setSpacing( x, y );
    }
    
    public void setGridFollowsMagnet( boolean value ) {
        System.out.println( "setGridFollowsMagnet " + value ); // DEBUG
        // XXX
    }
}
