/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.MMTimer;
import edu.colorado.phet.movingman.model.Man;
import edu.colorado.phet.movingman.model.TimeListenerAdapter;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceSeries;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;

import java.awt.*;

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
                   String name, String varname, String silderImageLoc, Color foregroundColor ) {
        super( movingManApparatusPanel, new Range2D( 0, -10, 20, 10 ), name, silderImageLoc, foregroundColor );
//        super( movingManApparatusPanel, new Range2D( 0, -10,5, 10 ), name );
        this.module = module;
        this.varname = varname;
        getChart().getVerticalGridlines().setMajorTickSpacing( 10 );
        getChart().getVerticalTicks().setMajorTickSpacing( 5 );
        getChart().getVerticalTicks().setMinorTickSpacing( 5 );
        getChart().getVerticalGridlines().setMajorTickSpacing( 5 );
        getChart().getHorizontalTicks().setMajorTickSpacing( 2 );
        getChart().getHorizonalGridlines().setMajorTickSpacing( 2 );
        getChart().getXAxis().setMajorTickSpacing( 2 );
        module.getTimeModel().addListener( new TimeListenerAdapter() {
            public void recordingPaused() {
                setPlaybackTime( module.getTimeModel().getRecordTimer().getTime() );
                setCursorVisible( true );

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
            public void cursorDragged( double modelX ) {
                handleCursorMoved( modelX );
            }
        } );
        module.getTimeModel().getPlaybackTimer().addListener( new MMTimer.Listener() {
            public void timeChanged() {
                setPlaybackTime( module.getTimeModel().getPlaybackTimer().getTime() );
            }
        } );
        addListener( new PlotDeviceListenerAdapter() {
            public void bufferChanged() {
                movingManApparatusPanel.repaintBackground();
            }
        } );
        module.getMan().addListener( new Man.Adapter() {
            public void collided( Man man ) {
                if( movingManApparatusPanel.getGraphic().getActiveUnit() == getChartSlider().getSliderGraphic() ) {
                    movingManApparatusPanel.getGraphic().clearActiveUnit();
                }
            }
        } );
        setPaintYLines( new double[]{5, 10} );
//        setBackground( Color.blue );
    }

    private void handleCursorMoved( double time ) {
        double maxTime = module.getTimeModel().getRecordTimer().getTime();
        if( time > maxTime ) {
            time = maxTime;
        }
        else if( time < 0 ) {
            time = 0;
        }
        module.setReplayTime( time );
    }

    public void valueChanged( double value ) {
    }

    public void updateSlider() {
    }

    public void setTextValue( double x ) {
        for( int i = 0; i < numPlotDeviceData(); i++ ) {
            PlotDeviceSeries series = plotDeviceSeriesAt( i );
            series.setReadoutValue( x );
        }
    }

    public String getVarname() {
        return varname;
    }


    public void setSelected( boolean selected ) {
//        getChartSlider().setSelected(selected);
        getChartSlider().setSelected( selected );
    }
}
