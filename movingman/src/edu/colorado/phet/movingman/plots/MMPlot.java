/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 30, 2005
 * Time: 4:50:44 AM
 * Copyright (c) Mar 30, 2005 by Sam Reid
 */

public class MMPlot extends PlotDevice {
    public MMPlot( MovingManApparatusPanel movingManApparatusPanel ) {
        super( movingManApparatusPanel, movingManApparatusPanel.getBuffer(), new Range2D( 0, -10, 20, 10 ) );
        getChart().getVerticalGridlines().setMajorTickSpacing( 10 );
        getChart().getVerticalTicks().setMajorTickSpacing( 5 );
        getChart().getVerticalTicks().setMinorTickSpacing( 5 );
        getChart().getVerticalGridlines().setMajorTickSpacing( 5 );
    }

    public void setMagnitude( int i ) {
    }

    public void valueChanged( double x ) {
    }

    public void setShift( double shift ) {
    }

    public void updateSlider() {

    }

    public void cursorMovedToTime( double time, int index ) {
    }

    public void setCursorVisible( boolean visible ) {
    }


    public void setTextValue( double x ) {
    }

    public class ChartButton {
        public Dimension getPreferredSize() {
            return null;
        }

        public void setLocation( Object x, int y ) {
        }

        public Object getX() {
            return null;
        }
    }
}
