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
import edu.colorado.phet.faraday.control.MagnetAndCoilControlPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.util.IRescaler;
import edu.colorado.phet.faraday.util.MagneticFieldRescaler;
import edu.colorado.phet.faraday.view.*;
import edu.colorado.phet.faraday.view.BarMagnetGraphic;
import edu.colorado.phet.faraday.view.CompassGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.PickupCoilGraphic;


/**
 * MagnetAndCoilModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MagnetAndCoilModule extends Module implements ICompassGridModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Rendering layers
    private static final double GRID_LAYER = 1;
    private static final double COIL_BACK_LAYER = 2;
    private static final double BAR_MAGNET_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double COIL_FRONT_LAYER = 5;
    private static final double DEBUG_LAYER = FaradayConfig.DEBUG_LAYER;
    private static final double HELP_LAYER = FaradayConfig.HELP_LAYER;

    // Locations
    private static final Point MAGNET_LOCATION = new Point( 200, 400 );
    private static final Point PICKUP_COIL_LOCATION = new Point( 500, 400 );
    private static final Point COMPASS_LOCATION = new Point( 150, 200 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Pickup Coil parameters
    private static final int NUMBER_OF_LOOPS = 2;
    private static final double LOOP_RADIUS = 0.75 * FaradayConfig.MAX_PICKUP_RADIUS;
    
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
    public MagnetAndCoilModule( ApplicationModel appModel ) {

        super( SimStrings.get( "MagnetAndCoilModule.title" ) );
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
        
        // Compass
        Compass compassModel = new Compass( barMagnetModel ); 
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( true );
        compassModel.setEnabled( false );
        model.addModelElement( compassModel );
        
        // Pickup Coil
        PickupCoil pickupCoilModel = new PickupCoil( barMagnetModel );
        pickupCoilModel.setNumberOfLoops( NUMBER_OF_LOOPS );
        pickupCoilModel.setRadius( LOOP_RADIUS );
        pickupCoilModel.setDirection( 0 /* radians */ );
        pickupCoilModel.setMaxVoltage( FaradayConfig.MAX_PICKUP_EMF );
        pickupCoilModel.setLocation( PICKUP_COIL_LOCATION);
        model.addModelElement( pickupCoilModel );
       
        // Lightbulb
        Lightbulb lightbulbModel = new Lightbulb( pickupCoilModel );
        lightbulbModel.setEnabled( true );
        
        // Volt Meter
        Voltmeter voltmeterModel = new Voltmeter( pickupCoilModel );
        voltmeterModel.setRescaler( rescaler );
        voltmeterModel.setRotationalKinematicsEnabled( true );
        voltmeterModel.setEnabled( false );
        model.addModelElement( voltmeterModel );
        
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
        
        // Pickup AbstractCoil
        PickupCoilGraphic pickupCoilGraphic = 
            new PickupCoilGraphic( apparatusPanel, model, pickupCoilModel, lightbulbModel, voltmeterModel, rescaler );
        apparatusPanel.addChangeListener( pickupCoilGraphic );
        apparatusPanel.addGraphic( pickupCoilGraphic.getForeground(), COIL_FRONT_LAYER );
        apparatusPanel.addGraphic( pickupCoilGraphic.getBackground(), COIL_BACK_LAYER );
        
        // Grid
        _gridGraphic = new CompassGridGraphic( apparatusPanel, barMagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        _gridGraphic.setRescaler( rescaler );
        _gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        _gridGraphic.setAlphaEnabled( ! APPARATUS_BACKGROUND.equals( Color.BLACK ) );
        _gridGraphic.setVisible( false );
        apparatusPanel.addChangeListener( _gridGraphic );
        apparatusPanel.addGraphic( _gridGraphic, GRID_LAYER );
        
        // CompassGraphic
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );

        // Debugger
//        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
//        debugger.add( pickupCoilGraphic.getForeground(), Color.RED, Color.RED );
//        debugger.add( pickupCoilGraphic.getBackground(), Color.GREEN, Color.GREEN );
//        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );

        // Collision detection
        barMagnetGraphic.getCollisionDetector().add( compassGraphic );
        barMagnetGraphic.getCollisionDetector().add( pickupCoilGraphic );
        compassGraphic.getCollisionDetector().add( barMagnetGraphic );
        compassGraphic.getCollisionDetector().add( pickupCoilGraphic );
        pickupCoilGraphic.getCollisionDetector().add( barMagnetGraphic );
        pickupCoilGraphic.getCollisionDetector().add( compassGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------
        
        // Control Panel
        MagnetAndCoilControlPanel controlPanel = new MagnetAndCoilControlPanel( this, 
            barMagnetModel, compassModel, pickupCoilModel, lightbulbModel, voltmeterModel,
            barMagnetGraphic, _gridGraphic, pickupCoilGraphic.getCoilGraphic() );
        this.setControlPanel( controlPanel );
        
        //----------------------------------------------------------------------------
        // Listeners
        //----------------------------------------------------------------------------

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