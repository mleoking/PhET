/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 10, 2005
 * Time: 8:32:43 AM
 * Copyright (c) May 10, 2005 by Sam Reid
 */

public class RampTimeModel implements ClockTickListener {
    private ArrayList timeSlices = new ArrayList();
    private Mode recordMode = new Record();
    private Mode ignoreMode = new Ignore();
    private Mode playbackMode = new Playback();
    private RampPhysicalModel physicalModel;
    private Mode mode;

    public RampTimeModel( RampPhysicalModel physicalModel ) {
        this.physicalModel = physicalModel;
        mode = ignoreMode;
    }

    public void clockTicked( ClockTickEvent event ) {
        mode.clockTicked( event );
    }

    abstract class Mode implements ClockTickListener {
    }

    class Record extends Mode {

        public void clockTicked( ClockTickEvent event ) {
            physicalModel.stepInTime( event.getDt() );
            RampPhysicalModel state = physicalModel.getState();
            timeSlices.add( state );
            System.out.println( "timeSlices.size() = " + timeSlices.size() );
        }
    }

    class Playback extends Mode {
        int index = 0;

        public void clockTicked( ClockTickEvent event ) {
            if( index >= timeSlices.size() ) {
                index = 0;
            }
            if( timeSlices.size() > 0 ) {
                System.out.println( "index = " + index );

                RampPhysicalModel state = (RampPhysicalModel)timeSlices.get( index );
                physicalModel.setState( state );
                index++;
            }
        }
    }

    class Ignore extends Mode {

        public void clockTicked( ClockTickEvent event ) {
            physicalModel.stepInTime( event.getDt() );
        }
    }

    public void record() {
        this.mode = recordMode;
    }

    public void playback() {
        this.mode = playbackMode;
    }
}
