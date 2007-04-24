
package edu.colorado.phet.forces1d.model;

import edu.colorado.phet.forces1d.Force1DApplication;
import edu.colorado.phet.forces1d.common.plotdevice.PlotDeviceModel;


/**
 * User: Sam Reid
 * Date: Nov 27, 2004
 * Time: 8:01:44 PM
 *
 */
public class Force1DPlotDeviceModel extends PlotDeviceModel {
    private Force1DApplication module;
    private Force1DModel model;

    public Force1DPlotDeviceModel( Force1DApplication module, final Force1DModel model, double maxTime, double timeScale ) {
        super( maxTime, timeScale );
        this.module = module;
        this.model = model;
        addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingStarted() {
                setPaused( false );
            }

            public void recordingPaused() {
                setPaused( true );
            }
        } );
//        addListener( new ListenerAdapter() {
//            public void recordingStarted() {
//                resetRecordPointer();  //todo this would be nice if it worked...
//            }
//        } );
    }

    public void resetRecordPointer() {
        super.resetRecordPointer();
        int index = super.getPlaybackIndex();
        model.resetRecordPointer( index );
    }



//    private void clearFutureData() {
//        double recordTime = super.getPlaybackTimer().getTime();
//        super.getRecordingTimer().setTime( recordTime );
//        //todo then clear future data.
//    }

    public void reset() {
        module.reset();
    }

    protected void stepRecord( double dt ) {
        model.stepRecord( dt );
    }

    protected void stepPlayback( double time, int index ) {
//        System.out.println( "playback, dt=" + dt );
        model.stepPlayback( time, index );
    }

    public void cursorMovedToTime( double modelX, int index ) {
        module.cursorMovedToTime( modelX, index );
    }


    public void clearData() {
        module.clearData();
    }
}
