/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.view.RampPanel;

import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 9:57:09 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampModule extends Module {
    private SwingTimerClock clock;
    private RampPanel rampPanel;
    private RampModel rampModel;
    private RampControlPanel rampControlPanel;
    private RampObject[] rampObjects;

    public RampModule() {
        super( "The Ramp" );
        clock = new SwingTimerClock( 1.0, 30 );
        setModel( new BaseModel() );
        rampModel = new RampModel();
        rampObjects = new RampObject[]{
            new RampObject( "images/cabinet.gif", "File Cabinet", 0.8, 200, 0.3, 0.2 ),
            new RampObject( "images/fridge.gif", "Refrigerator", 0.35, 400, 0.7, 0.5 ),
            new RampObject( "images/phetbook.gif", "Textbook", 0.8, 10, 0.3, 0.25 ),
            new RampObject( "images/crate.gif", "Crate", 0.8, 300, 0.2, 0.2 ),
            new RampObject( "images/ollie.gif", "Sleepy Dog", 0.5, 25, 0.1, 0.1 ),
        };

        rampPanel = new RampPanel( this );
        setApparatusPanel( rampPanel );
        rampControlPanel = new RampControlPanel( this );
        setControlPanel( rampControlPanel );


    }

    public static void main( String[] args ) {
        RampModule module = new RampModule();
        FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 600, 600 ) );
        ApplicationModel applicationModel = new ApplicationModel( "The Ramp", "Ramp Application", "0", frameSetup, module, module.getClock() );
        PhetApplication application = new PhetApplication( applicationModel );
        application.startApplication();

//        module.runTest1();
        module.runTest2();
    }

    private void runTest1() {
        RepaintDebugGraphic.enable( rampPanel, clock );
        final long origTime = System.currentTimeMillis();
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                long time = System.currentTimeMillis();
                double dt = time - origTime;
//                double angularFrequency = 0.4;
                double angularFrequency = 0.001;
                double phase = 0;
                double amplitude = 10;
                double offset = 10;
                double x = Math.sin( angularFrequency * dt + phase ) * amplitude + offset;
                rampModel.getBlock().setPosition( x );
            }
        } );
    }

    private void runTest2() {
        RepaintDebugGraphic.enable( rampPanel, clock );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                rampModel.stepInTime( event.getDt() );
//                rampModel.getBlock().setPosition( x );
            }
        } );

    }

    public RampPanel getRampPanel() {
        return rampPanel;
    }

    public AbstractClock getClock() {
        return clock;
    }

    public RampModel getRampModel() {
        return rampModel;
    }

    public void reset() {
        rampModel.reset();
    }

    public void setObject( RampObject rampObject ) {
        try {
            getRampPanel().getBlockGraphic().setImage( rampObject.getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        rampModel.getBlock().setMass( rampObject.getMass() );
        rampModel.getBlock().setStaticFriction( rampObject.getStaticFriction() );
        rampModel.getBlock().setKineticFriction( rampObject.getKineticFriction() );
    }

    public RampObject[] getRampObjects() {
        return rampObjects;
    }
}
