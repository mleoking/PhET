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
import edu.colorado.phet.faraday.model.HollywoodMagnet;
import edu.colorado.phet.faraday.model.PickupCoil;
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
    private static final double PICKUP_WIDGET_LAYER = 1;
    private static final double BAR_MAGNET_LAYER = 2;
    private static final double HELP_LAYER = Double.MAX_VALUE;

    // Locations of model components

    // Locations of view components
    private static final Point BAR_MAGNET_LOCATION = new Point( 200, 100 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 125 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = FaradayConfig.APPARATUS_BACKGROUND;

    private static final double BAR_MAGNET_STRENGTH = 500;
    private static final double PICKUP_LOOP_RADIUS = 200;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model
    private HollywoodMagnet _magnetModel;
    private PickupCoil _pickupCoilModel;
    
    // View
    private PickupWidget _pickupWidget;
    
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
        _magnetModel.setStrength( BAR_MAGNET_STRENGTH );
        _magnetModel.setLocation( BAR_MAGNET_LOCATION );
        _magnetModel.setDirection( 0 );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil();
        _pickupCoilModel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        _pickupCoilModel.setRadius( PICKUP_LOOP_RADIUS );
        _pickupCoilModel.setDirection( 0 );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        _pickupCoilModel.setMagnet( _magnetModel );
        model.addModelElement( _pickupCoilModel );
       
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new BarMagnetControlPanel( this );
        this.setControlPanel( _controlPanel );

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Bar Magnet
        BarMagnetGraphic magnetGraphic = new BarMagnetGraphic( apparatusPanel, _magnetModel );
        apparatusPanel.addGraphic( magnetGraphic, BAR_MAGNET_LAYER );
        
        // Pickup Coil
        CoilGraphic coilGraphic = new CoilGraphic( apparatusPanel, _pickupCoilModel );
        
        // Voltmeter
        VoltMeterGraphic meterGraphic = new VoltMeterGraphic( apparatusPanel, _pickupCoilModel );
        
        // Light Bulb 
        LightBulbGraphic bulbGraphic = new LightBulbGraphic( apparatusPanel, _pickupCoilModel );
        
        // Pickup Coil
        _pickupWidget = 
            new PickupWidget( apparatusPanel, coilGraphic, meterGraphic, bulbGraphic, _pickupCoilModel );
        apparatusPanel.addGraphic( _pickupWidget, PICKUP_WIDGET_LAYER );
        
        //----------------------------------------------------------------------------
        // Observers
        //----------------------------------------------------------------------------
        
        _magnetModel.addObserver( magnetGraphic );
        _pickupCoilModel.addObserver( coilGraphic );
        _pickupCoilModel.addObserver( meterGraphic );
        _pickupCoilModel.addObserver( bulbGraphic );
        _pickupCoilModel.addObserver( _pickupWidget );
        
        //----------------------------------------------------------------------------
        // Listeners
        //----------------------------------------------------------------------------
        
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
     * Resets everything to the initial state.
     */
    public void reset() {
        int areaScale = BarMagnetControlPanel.AREA_MIN_PERCENTAGE + (BarMagnetControlPanel.AREA_MAX_PERCENTAGE - BarMagnetControlPanel.AREA_MIN_PERCENTAGE)/2;
        _controlPanel.setCompassGridEnabled( false );
        _controlPanel.setMagnetStrength( BAR_MAGNET_STRENGTH );
        _controlPanel.setLoopAreaScale( areaScale );
        _controlPanel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        _controlPanel.setBulbEnabled( true );
        _controlPanel.setMeterEnabled( false );
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
     * @param strength the strength value
     */
    public void setMagnetStrength( double strength ) {
        //System.out.println( "setMagnetStrength " + strength ); // DEBUG
        _magnetModel.setStrength( strength );
    }
    
    /**
     * Enables/disables the compass grid.
     * 
     * @param enable true to enable, false to disable
     */
    public void setCompassGridEnabled( boolean enable ) {
        //System.out.println( "setCompassGridEnabled " + enable ); // DEBUG
        // XXX
    }
    
    /**
     * Sets the number of loops in the pickup coil.
     * 
     * @param numberOfLoops the number of loops
     */
    public void setNumberOfPickupLoops( int numberOfLoops ) {
        //System.out.println( "setNumberOfPickupLoops " + numberOfLoops ); // DEBUG
        _pickupCoilModel.setNumberOfLoops( numberOfLoops ); 
    }
    
    /**
     * XXX
     * 
     * @param scale
     */
    public void scalePickupLoopArea( double scale ) {
        //System.out.println( "scalePickupLoopArea " + scale ); // DEBUG
        _pickupCoilModel.setRadius( PICKUP_LOOP_RADIUS * scale );
    }
    
    /**
     * Enables the light bulb.
     */
    public void setBulbEnabled( boolean enabled ) {
        //System.out.println( "setBulbEnabled " + enabled ); // DEBUG
        _pickupWidget.setBulbEnabled( enabled );
    }
    
    /**
     * Enables the volt meter.
     */
    public void setMeterEnabled( boolean enabled ) {
        //System.out.println( "setMeterEnabled " + enabled ); // DEBUG
        _pickupWidget.setMeterEnabled( enabled );
    }
}
