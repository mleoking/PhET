/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.control.BarMagnetControlPanel;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.PickupCoilGraphic;


/**
 * BarMagnetModule is "Two Coils" module for the Faraday's Law simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BarMagnetModule extends Module implements ActionListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double BAR_MAGNET_LAYER = 1;
    private static final double PICKUP_COIL_LAYER = 2;
    private static final double HELP_LAYER = Double.MAX_VALUE;

    // Locations of model components

    // Locations of view components
    private static final Point BAR_MAGNET_LOCATION = new Point( 200, 100 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 125 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = FaradayConfig.APPARATUS_BACKGROUND;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BarMagnet _barMagnetModel;
    
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

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Clock
        AbstractClock clock = appModel.getClock();

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        // Bar Magnet model
        _barMagnetModel = new BarMagnet();
        _barMagnetModel.setStrength( 50 );
        _barMagnetModel.setLocation( BAR_MAGNET_LOCATION );
        _barMagnetModel.setDirection( 0 );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Control Panel
        this.setControlPanel( new BarMagnetControlPanel( this ) );

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel();
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Pickup Coil
        PickupCoilGraphic pickupCoilGraphic = new PickupCoilGraphic( apparatusPanel );
        pickupCoilGraphic.setLocation( PICKUP_COIL_LOCATION );
        apparatusPanel.addGraphic( pickupCoilGraphic, PICKUP_COIL_LAYER );
        
        // Bar Magnet
        BarMagnetGraphic barMagnetGraphic = new BarMagnetGraphic( apparatusPanel, _barMagnetModel );
        apparatusPanel.addGraphic( barMagnetGraphic, BAR_MAGNET_LAYER );
        
        //----------------------------------------------------------------------------
        // Observers
        //----------------------------------------------------------------------------
        
        _barMagnetModel.addObserver( barMagnetGraphic );
        
        //----------------------------------------------------------------------------
        // Listeners
        //----------------------------------------------------------------------------
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }

    //----------------------------------------------------------------------------
    // Event Handling
    //----------------------------------------------------------------------------
    
    /**
     */
    public void actionPerformed( ActionEvent event ) {
        if ( event.getActionCommand().equals( BarMagnetControlPanel.FLIP_POLARITY_COMMAND ) ) {
           System.out.println( "flip polarity" );
           double direction = _barMagnetModel.getDirection();
           direction += 180;  // XXX modulo ?
           _barMagnetModel.setDirection( direction );
        }
        else {
            throw new IllegalStateException( "unexpected event: " + event );
        }
    }
}
