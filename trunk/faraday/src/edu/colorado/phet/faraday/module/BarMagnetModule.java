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
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.model.PickupCoil;
import edu.colorado.phet.faraday.view.*;


/**
 * BarMagnetModule is "Two Coils" module for the Faraday's Law simulation.
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
    private BarMagnet _barMagnetModel;
    private PickupCoil _pickupCoilModel;
    
    // View
    private PickupWidget _pickupWidget;
    
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
        _barMagnetModel = new BarMagnet();
        _barMagnetModel.setStrength( BAR_MAGNET_STRENGTH );
        _barMagnetModel.setLocation( BAR_MAGNET_LOCATION );
        _barMagnetModel.setDirection( 0 );
        
        // Pickup Coil
        _pickupCoilModel = new PickupCoil();
        _pickupCoilModel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        _pickupCoilModel.setRadius( PICKUP_LOOP_RADIUS );
        _pickupCoilModel.setDirection( 0 );
        _pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        _pickupCoilModel.setMagnet( _barMagnetModel );
        model.addModelElement( _pickupCoilModel );
       
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Control Panel
        BarMagnetControlPanel controlPanel = new BarMagnetControlPanel( this );
        this.setControlPanel( controlPanel );

        // Apparatus Panel
        ApparatusPanel apparatusPanel = new ApparatusPanel2( model, clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Bar Magnet
        BarMagnetGraphic barMagnetGraphic = new BarMagnetGraphic( apparatusPanel, _barMagnetModel );
        apparatusPanel.addGraphic( barMagnetGraphic, BAR_MAGNET_LAYER );
        
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
        
        _barMagnetModel.addObserver( barMagnetGraphic );
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
        
        int areaScale = BarMagnetControlPanel.AREA_MIN_PERCENTAGE + (BarMagnetControlPanel.AREA_MAX_PERCENTAGE - BarMagnetControlPanel.AREA_MIN_PERCENTAGE)/2;
        controlPanel.setFieldLinesEnabled( false );
        controlPanel.setBarMagnetStrength( BAR_MAGNET_STRENGTH );
        controlPanel.setLoopAreaScale( areaScale );
        controlPanel.setNumberOfLoops( FaradayConfig.MIN_PICKUP_LOOPS );
        controlPanel.setBulbEnabled( true );
        controlPanel.setMeterEnabled( false );
    }

    //----------------------------------------------------------------------------
    // Controller methods
    //----------------------------------------------------------------------------
    
    public void flipBarMagnetPolarity() {
        //System.out.println( "flipBarMagnetPolarity" ); // DEBUG
        double direction = _barMagnetModel.getDirection();
        direction = ( direction + 180 ) % 360;
        _barMagnetModel.setDirection( direction );
    }
    
    public void setBarMagnetStrength( double value ) {
        //System.out.println( "setBarMagnetStrength " + value ); // DEBUG
        _barMagnetModel.setStrength( value );
    }
    
    public void setFieldLinesEnabled( boolean enable ) {
        //System.out.println( "setFieldLinesEnabled " + enable );
        // XXX
    }
    
    public void setNumberOfPickupLoops( int numberOfLoops ) {
        //System.out.println( "setNumberOfPickupLoops " + numberOfLoops );
        _pickupCoilModel.setNumberOfLoops( numberOfLoops ); 
    }
    
    public void scalePickupLoopArea( double scale ) {
        System.out.println( "scalePickupLoopArea " + scale );
        _pickupCoilModel.setRadius( PICKUP_LOOP_RADIUS * scale );
    }
    
    public void enableBulb() {
        System.out.println( "enableBulb" );
        _pickupWidget.enableBulb();
    }
    
    public void enableMeter() {
        System.out.println( "enableMeter" );
        _pickupWidget.enableMeter();
    }
    
}
