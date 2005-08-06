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
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesPlaybackPanel;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;

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
    private TimeSeriesPlaybackPanel rampMediaPanel;

    private ArrayList listeners = new ArrayList();

    public static final double FORCE_LENGTH_SCALE = 0.1;//1.0;

    public RampModule( AbstractClock clock ) {
        super( "The Ramp", clock );
        setModel( new BaseModel() );
        rampModel = new RampModel( this, clock );
        rampObjects = new RampObject[]{
                new RampObject( "images/cabinet.gif", "File Cabinet", 0.8, 100, 0.3, 0.2, 0.4 ),
                new RampObject( "images/fridge.gif", "Refrigerator", 0.35, 175, 0.7, 0.5, 0.4 ),
                new RampObject( "images/crate.gif", "Crate", 0.8, 300, 0.2, 0.2, 0.3 ),
                new RampObject( "images/piano.png", "Piano", 0.8, 225, 0.6, 0.6, 0.8, 20 ),
//            new RampObject( "images/ollie.gif", "Sleepy Dog", 0.5, 30, 0.1, 0.1, 0.35 ),
        };
        rampPanel = new RampPanel( this );
        super.setPhetPCanvas( rampPanel );


        rampControlPanel = new RampControlPanel( this );
        setControlPanel( rampControlPanel );
        setObject( rampObjects[0] );

        rampMediaPanel = new TimeSeriesPlaybackPanel( getRampTimeSeriesModel() );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                getRampPhysicalModel().setupForces();
                updateGraphics( event );
            }
        } );
        rampModel.getBlock().addListener( new CollisionHandler( this ) );
    }

    private TimeSeriesModel getRampTimeSeriesModel() {
        return rampModel.getRampTimeSeriesModel();
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
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    module.doReset();
                    module.getPhetPCanvas().addComponentListener( new ComponentListener() {
                        public void componentHidden( ComponentEvent e ) {
                        }

                        public void componentMoved( ComponentEvent e ) {
                        }

                        public void componentResized( ComponentEvent e ) {
                            System.out.println( "module.getApparatusPanel().getSize( ) = " + module.getPhetPCanvas().getSize() );
                        }

                        public void componentShown( ComponentEvent e ) {
                            System.out.println( "module.getApparatusPanel().getSize( ) = " + module.getPhetPCanvas().getSize() );
                        }
                    } );
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

        System.out.println( "new Date( ) = " + new Date() );
    }

    public RampPanel getRampPanel() {
        return rampPanel;
    }

    public RampPhysicalModel getRampPhysicalModel() {
        return getRampModel().getRampPhysicalModel();
    }

    private RampModel getRampModel() {
        return rampModel;
    }

    public void reset() {
        if( resetDialogOk() ) {
            doReset();
        }
    }

    private boolean resetDialogOk() {
        int answer = JOptionPane.showConfirmDialog( getApparatusPanel(), "Are you sure you'd like to reset?", "Confirm Reset", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
        return answer == JOptionPane.OK_OPTION;
    }

    private void doReset() {
        rampModel.reset();
        rampPanel.reset();
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
        return getRampModel().getBlock();
    }

    public void clearHeat() {
        cueFirefighter();
//        rampModel.clearHeat();
    }

    public void cueFirefighter() {
        new FireDog( this ).putOutFire();
    }

    public void setAppliedForce( double appliedForce ) {
        getRampModel().setAppliedForce( appliedForce );
    }

    public double getRampAngle() {
        return getRampPhysicalModel().getRampAngle();
    }

    public void setRampAngle( double value ) {
        getRampPhysicalModel().setRampAngle(value);
    }

    public double getGlobalMinPosition() {
        return getRampPhysicalModel().getGlobalMinPosition();
    }

    public double getGlobalMaxPosition() {
        return getRampPhysicalModel().getGlobalMaxPosition();
//        return getRampPhysicalModel().getGround().getLength()+getRampPhysicalModel().getRamp().getLength();
    }

    public double getGlobalBlockPosition() {
        return getRampPhysicalModel().getGlobalBlockPosition();
    }

    public void setGlobalBlockPosition( double position ) {
        getRampPhysicalModel().setGlobalBlockPosition(position);
    }

    public static interface Listener {
        void objectChanged();
    }

    public RampObject[] getRampObjects() {
        return rampObjects;
    }

    public void record() {
        rampModel.record();
    }

    public void playback() {
        rampModel.playback();
    }

    public void repaintBackground() {
        rampPanel.repaintBackground();
    }

    public void setCursorsVisible( boolean b ) {
        System.out.println( "RampModule.setCursorsVisible: NOOP" );
    }

    public void updateModel( double dt ) {
        getRampPhysicalModel().stepInTime( dt );
    }

    public void updatePlots( RampPhysicalModel state, double recordTime ) {
        getRampPlotSet().updatePlots( state, recordTime );
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return getRampModel().getRampTimeSeriesModel();
    }

    public void setMass( double value ) {
        getRampModel().setMass( value );
    }

    public RampPlotSet getRampPlotSet() {
        return rampPanel.getRampPlotSet();
    }

    public RampControlPanel getRampControlPanel() {
        return rampControlPanel;
    }
}
