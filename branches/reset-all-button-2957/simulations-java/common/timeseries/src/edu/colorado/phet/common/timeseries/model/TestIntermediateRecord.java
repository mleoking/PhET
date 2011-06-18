// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.timeseries.model;

import junit.framework.TestCase;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * Created by: Sam
 * Dec 2, 2007 at 10:33:17 PM
 */
public class TestIntermediateRecord extends TestCase {
    public void testIntermediateRecord() throws InterruptedException {
        RecordableModel recordableModel = new RecordableModel() {
            double time = 0;

            public void stepInTime( double simulationTimeChange ) {
                time += simulationTimeChange;
                System.out.println( "Time Stepped: time="+time );
            }

            public Object getState() {
                return new Double( time );
            }

            public void setState( Object o ) {
                this.time = ( (Double) o ).doubleValue();
                System.out.println( "Time Set: time="+time );
            }

            public void resetTime() {
                time = 0;
            }

            public void clear() {
                System.out.println( "cleared" );
            }
        };
        final ConstantDtClock clock = new ConstantDtClock( 30, 1.0 );
        final TimeSeriesModel timeSeriesModel = new TimeSeriesModel( recordableModel, clock );
        clock.addClockListener( timeSeriesModel );
        timeSeriesModel.startRecording();
        Thread.sleep( 2000 );
        timeSeriesModel.setPaused( true );
        timeSeriesModel.setPlaybackTime( 30 );
        timeSeriesModel.setPlaybackMode();
        timeSeriesModel.setPaused( false );
        Thread.sleep(100);
        timeSeriesModel.startRecording();
        Thread.sleep(2000);
        timeSeriesModel.setPlaybackTime( 0.0);
        timeSeriesModel.setPlaybackMode();
        timeSeriesModel.setPaused( false );
    }

    public static void main( String[] args ) throws InterruptedException {
        new TestIntermediateRecord().testIntermediateRecord();
    }
}
