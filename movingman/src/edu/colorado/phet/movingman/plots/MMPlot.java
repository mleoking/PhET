/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.MMTimer;
import edu.colorado.phet.movingman.model.TimeListenerAdapter;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;

/**
 * User: Sam Reid
 * Date: Mar 30, 2005
 * Time: 4:50:44 AM
 * Copyright (c) Mar 30, 2005 by Sam Reid
 */

public class MMPlot extends PlotDevice {
    private MovingManModule module;
    private String varname;

    public MMPlot( final MovingManModule module, final MovingManApparatusPanel movingManApparatusPanel,
                   String name, String varname ) {
        super( movingManApparatusPanel, new Range2D( 0, -10, 20, 10 ), name );
        this.module = module;
        this.varname = varname;
        getChart().getVerticalGridlines().setMajorTickSpacing( 10 );
        getChart().getVerticalTicks().setMajorTickSpacing( 5 );
        getChart().getVerticalTicks().setMinorTickSpacing( 5 );
        getChart().getVerticalGridlines().setMajorTickSpacing( 5 );
        module.getTimeModel().addListener( new TimeListenerAdapter() {
            public void recordingPaused() {
                setCursorVisible( true );
                setCursorLocation( module.getTimeModel().getRecordTimer().getTime() );
            }

            public void recordingStarted() {
                setCursorVisible( false );
            }

            public void recordingFinished() {
                setCursorVisible( true );
            }
        } );
        setCursorVisible( false );
        addListener( new PlotDeviceListenerAdapter() {
            public void cursorMoved( double modelX ) {
                handleCursorMoved( modelX );
            }
        } );
        module.getTimeModel().getPlaybackTimer().addListener( new MMTimer.Listener() {
            public void timeChanged() {
                setCursorLocation( module.getTimeModel().getPlaybackTimer().getTime() );
            }
        } );
        addListener( new PlotDeviceListenerAdapter() {
            public void bufferChanged() {
                movingManApparatusPanel.repaintBackground();
            }
        } );
    }

    private void handleCursorMoved( double time ) {
        double maxTime = module.getTimeModel().getRecordTimer().getTime();
        if( time > maxTime ) {
            time = maxTime;
        }
        else if( time < 0 ) {
            time = 0;
        }
        module.cursorMovedToTime( time );
    }

    public void valueChanged( double x ) {
    }

    public void setShift( double velocityOffset ) {}

    public void updateSlider() {
    }

    public void setTextValue( double x ) {}

    public String getVarname() {
        return varname;
    }


}
