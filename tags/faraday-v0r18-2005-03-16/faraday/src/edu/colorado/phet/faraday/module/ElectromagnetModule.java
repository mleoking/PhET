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

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.control.panel.ElectromagnetPanel;
import edu.colorado.phet.faraday.model.*;
import edu.colorado.phet.faraday.util.IRescaler;
import edu.colorado.phet.faraday.util.CompassGridRescaler;
import edu.colorado.phet.faraday.view.CompassGraphic;
import edu.colorado.phet.faraday.view.CompassGridGraphic;
import edu.colorado.phet.faraday.view.ElectromagnetGraphic;
import edu.colorado.phet.faraday.view.FieldMeterGraphic;


/**
 * TransformerModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ElectromagnetModule extends FaradayModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double GRID_LAYER = 1;
    private static final double ELECTROMAGNET_BACK_LAYER = 2;
    private static final double ELECTROMAGNET_LAYER = 3;
    private static final double COMPASS_LAYER = 4;
    private static final double ELECTROMAGNET_FRONT_LAYER = 5;
    private static final double METER_LAYER = 6;
    private static final double DEBUG_LAYER = FaradayConfig.DEBUG_LAYER;
    private static final double HELP_LAYER = FaradayConfig.HELP_LAYER;

    // Locations
    private static final Point ELECTROMAGNET_LOCATION = new Point( 400, 400 );
    private static final Point COMPASS_LOCATION = new Point( 150, 200 );
    private static final Point FIELD_METER_LOCATION = new Point( 150, 400 );
    private static final Point SOURCE_COIL_LOCATION = new Point( 500, 400 );

    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.BLACK;
    
    // Source Coil
    private static final int NUMBER_OF_LOOPS = FaradayConfig.ELECTROMAGNET_LOOPS_MAX;
    private static final double LOOP_RADIUS = 50.0;  // Fixed loop radius
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param appModel the application model
     */
    public ElectromagnetModule( AbstractClock clock ) {

        super( SimStrings.get( "ElectromagnetModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------
        
        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
     
        // Battery
        Battery batteryModel = new Battery();
        batteryModel.setMaxVoltage( FaradayConfig.BATTERY_VOLTAGE_MAX  );
        batteryModel.setAmplitude( 1.0 );
        batteryModel.setEnabled( false );
        
        // AC Source
        ACSource acSourceModel = new ACSource();
        acSourceModel.setMaxVoltage( FaradayConfig.AC_VOLTAGE_MAX );
        acSourceModel.setMaxAmplitude( 0.5 );
        acSourceModel.setFrequency( 0.5 );
        acSourceModel.setEnabled( true );
        model.addModelElement( acSourceModel );
        
        // Source Coil
        SourceCoil sourceCoilModel = new SourceCoil();
        sourceCoilModel.setLocation( SOURCE_COIL_LOCATION );
        sourceCoilModel.setNumberOfLoops( NUMBER_OF_LOOPS );
        sourceCoilModel.setRadius( LOOP_RADIUS );
        sourceCoilModel.setDirection( 0 /* radians */ );
        if ( batteryModel.isEnabled() ) {
            sourceCoilModel.setVoltageSource( batteryModel );
        }
        else {
            sourceCoilModel.setVoltageSource( acSourceModel );
        }
        
        // Electromagnet
        Electromagnet electromagnetModel = new Electromagnet( sourceCoilModel );
        electromagnetModel.setMaxStrength( FaradayConfig.ELECTROMAGNET_STRENGTH_MAX );
        electromagnetModel.setLocation( ELECTROMAGNET_LOCATION );
        electromagnetModel.setDirection( 0 /* radians */ );
        // Do NOT set the strength! -- strength will be set based on the source coil model.
        // Do NOT set the size! -- size will be based on the source coil appearance.
        electromagnetModel.update();
         
        // Rescaler
        IRescaler rescaler = new CompassGridRescaler( electromagnetModel );
        
        // Compass model
        Compass compassModel = new Compass( electromagnetModel );
        compassModel.setLocation( COMPASS_LOCATION );
        compassModel.setRotationalKinematicsEnabled( false );
        model.addModelElement( compassModel );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        this.setApparatusPanel( apparatusPanel );
        
        // Electromagnet
        ElectromagnetGraphic electromagnetGraphic = new ElectromagnetGraphic( apparatusPanel, model, 
                electromagnetModel, sourceCoilModel, batteryModel, acSourceModel );
        apparatusPanel.addChangeListener( electromagnetGraphic );
        apparatusPanel.addGraphic( electromagnetGraphic.getForeground(), ELECTROMAGNET_FRONT_LAYER );
        apparatusPanel.addGraphic( electromagnetGraphic.getBackground(), ELECTROMAGNET_BACK_LAYER );
        
        // Grid
        CompassGridGraphic gridGraphic = new CompassGridGraphic( apparatusPanel, 
                electromagnetModel, FaradayConfig.GRID_SPACING, FaradayConfig.GRID_SPACING );
        gridGraphic.setRescaler( rescaler );
        gridGraphic.setNeedleSize( FaradayConfig.GRID_NEEDLE_SIZE );
        gridGraphic.setAlphaEnabled( ! APPARATUS_BACKGROUND.equals( Color.BLACK ) );
        apparatusPanel.addChangeListener( gridGraphic );
        apparatusPanel.addGraphic( gridGraphic, GRID_LAYER );
        super.setCompassGridGraphic( gridGraphic );
        
        // Compass
        CompassGraphic compassGraphic = new CompassGraphic( apparatusPanel, compassModel );
        compassGraphic.setLocation( COMPASS_LOCATION );
        apparatusPanel.addChangeListener( compassGraphic );
        apparatusPanel.addGraphic( compassGraphic, COMPASS_LAYER );
        
        // Field Meter
        FieldMeterGraphic fieldMeterGraphic = new FieldMeterGraphic( apparatusPanel, electromagnetModel );
        fieldMeterGraphic.setLocation( FIELD_METER_LOCATION );
        fieldMeterGraphic.setVisible( false );
        apparatusPanel.addChangeListener( fieldMeterGraphic );
        apparatusPanel.addGraphic( fieldMeterGraphic, METER_LAYER );


//        // XXX test bounds & registration point of FaradaySlider
//        FaradaySlider testSlider = new FaradaySlider( apparatusPanel, 100 );
//        addGraphic( testSlider, DEBUG_LAYER );
//        {
//            PhetGraphic background = new PhetShapeGraphic( apparatusPanel, new Rectangle( 0, 0, 200, 50 ), Color.GRAY );
//            testSlider.setBackground( background );
//            testSlider.setMinimum( -100 );
//            testSlider.setMaximum( 100 );
//            testSlider.setValue( 0 );
//            testSlider.addTick( -100 );
//            testSlider.addTick( 100 );
//            testSlider.addTick( 0 );
//            testSlider.setLocation( 100, 100 );
//        }
        
//        // XXX Slider test
//        {
//            PhetGraphic knob = new PhetShapeGraphic( apparatusPanel, new Ellipse2D.Double( 0, 0, 25, 25 ), Color.RED );
//            PhetGraphic track = new PhetShapeGraphic( apparatusPanel, new Rectangle( 0, 0, 165, 5 ), Color.YELLOW );
//            PhetGraphic background = new PhetShapeGraphic( apparatusPanel, new Rectangle( 0, 0, 200, 50 ), Color.BLUE );
//            GraphicSlider slider = new GraphicSlider( apparatusPanel );
//            slider.setBackground( background );
//            slider.setTrack( track );
//            slider.setKnob( knob );
//            slider.setLocation( 50, 400 );
//            slider.rotate( Math.PI / 4 );
//            slider.setMinimum( -100 );
//            slider.setMaximum( 100 );
//            slider.setValue( 75 );
//            apparatusPanel.addGraphic( slider, DEBUG_LAYER );
//        }

        // Debugger
//        DebuggerGraphic debugger = new DebuggerGraphic( apparatusPanel );
//        debugger.setLocationColor( Color.GREEN );
//        debugger.add( testSlider );
//        apparatusPanel.addGraphic( debugger, DEBUG_LAYER );

        // Collision detection
        electromagnetGraphic.getCollisionDetector().add( compassGraphic );
        compassGraphic.getCollisionDetector().add( electromagnetGraphic );
        
        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        {
            ControlPanel controlPanel = new ControlPanel( this );
            ElectromagnetPanel electromagnetPanel = new ElectromagnetPanel(
                    sourceCoilModel, batteryModel, acSourceModel, compassModel,
                    electromagnetGraphic, gridGraphic, fieldMeterGraphic );
            controlPanel.addFullWidth( electromagnetPanel );
            this.setControlPanel( controlPanel );
        }
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }
}