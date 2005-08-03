/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.timeseries.RampTimeSeriesModel;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesPlaybackPanel;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 9:57:09 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampModule extends Module {
    private RampPanel rampPanel;
    private RampModel rampModel;
    private RampControlPanel rampControlPanel;
    private RampObject[] rampObjects;
//    public static final double FORCE_LENGTH_SCALE = 0.15;//1.0;
    public static final double FORCE_LENGTH_SCALE = 0.1;//1.0;
    private RampTimeSeriesModel rampTimeSeriesModel;
    private TimeSeriesPlaybackPanel rampMediaPanel;
    private RampPlotSet rampPlotSet;
    private ArrayList listeners = new ArrayList();

    public RampModule( AbstractClock clock ) {
        super( "The Ramp", clock );
        setModel( new BaseModel() );
        rampModel = new RampModel();
        rampModel.reset();
        rampTimeSeriesModel = new RampTimeSeriesModel( this );
        clock.addClockTickListener( rampTimeSeriesModel );
        rampObjects = new RampObject[]{
            new RampObject( "images/cabinet.gif", "File Cabinet", 0.8, 100, 0.3, 0.2, 0.4 ),
            new RampObject( "images/fridge.gif", "Refrigerator", 0.35, 400, 0.7, 0.5, 0.4 ),
            new RampObject( "images/crate.gif", "Crate", 0.8, 300, 0.2, 0.2, 0.3 ),
            new RampObject( "images/piano.png", "Piano", 0.8, 225, 0.6, 0.6, 0.8, 20 ),
//            new RampObject( "images/ollie.gif", "Sleepy Dog", 0.5, 30, 0.1, 0.1, 0.35 ),
        };
        rampPanel = new RampPanel( this );
        super.setPhetPCanvas( rampPanel );

        rampPlotSet = new RampPlotSet( this );

        rampControlPanel = new RampControlPanel( this );
        setControlPanel( rampControlPanel );
        setObject( rampObjects[0] );

        rampMediaPanel = new TimeSeriesPlaybackPanel( rampTimeSeriesModel );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                updateGraphics( event );
            }
        } );
    }

    public void updateGraphics( ClockTickEvent event ) {
        super.updateGraphics( event );
        rampPanel.updateGraphics();
    }

    public static void main( String[] args ) {
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.apply();
        PhetLookAndFeel.setLookAndFeel();
        SwingTimerClock clock = new SwingTimerClock( 1.0 / 30.0, 30 );
        FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 600, 600 ) );
        PhetApplication application = new PhetApplication( args, "The Ramp", "Ramp Application", "0", clock, true, frameSetup );
        final RampModule module = new RampModule( clock );

        application.setModules( new Module[]{module} );
        application.getPhetFrame().getBasicPhetPanel().setAppControlPanel( module.rampMediaPanel );
        application.startApplication();
//        module.updateGraphics( new ClockTickEvent( clock, 0 ) );
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    module.reset();
                }
            } );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }

//        clock.addClockTickListener( new ClockTickListener() {
//            public void clockTicked( ClockTickEvent event ) {
//                module.getPhetPCanvas().paintImmediately();
//            }
//        } );
//        RepaintDebugGraphic.enable( module.getApparatusPanel(), clock );

    }

    public RampPanel getRampPanel() {
        return rampPanel;
    }

    public RampModel getRampModel() {
        return rampModel;
    }

    public void reset() {
        rampTimeSeriesModel.reset();
        rampModel.reset();
        rampPlotSet.reset();
    }

    public void setObject( RampObject rampObject ) {
        rampModel.setObject( rampObject );
        getRampPanel().setObject( rampObject );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.objectChanged();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public Block getBlock() {
        return rampModel.getBlock();
    }

    public void clearHeat() {
        cueFirefighter();
//        rampModel.clearHeat();
    }

    public void cueFirefighter() {
        new FireDog( this ).putOutFire();
    }

    public static interface Listener {
        void objectChanged();
    }

    public RampObject[] getRampObjects() {
        return rampObjects;
    }

    public void record() {
        rampTimeSeriesModel.setRecordMode();
        rampTimeSeriesModel.setPaused( false );
    }

    public void playback() {
        rampTimeSeriesModel.setPlaybackMode();
        rampTimeSeriesModel.setPaused( false );
    }

    public void repaintBackground() {
        System.out.println( "RampModule.repaintBackground: NOOP" );
        rampPlotSet.repaintBackground();
    }

    public void setCursorsVisible( boolean b ) {
        System.out.println( "RampModule.setCursorsVisible: NOOP" );
    }

    public void updateModel( double dt ) {
        getRampModel().stepInTime( dt );
    }

    public void updatePlots( RampModel state, double recordTime ) {
        rampPlotSet.updatePlots( state, recordTime );
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return rampTimeSeriesModel;
    }

    public void setMass( double value ) {
        rampModel.setMass( value );
    }

    public RampPlotSet getRampPlotSet() {
        return rampPlotSet;
    }
}
