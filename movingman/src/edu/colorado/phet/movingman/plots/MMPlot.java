/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.movingman.plotdevice.PlotDevice;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;

/**
 * User: Sam Reid
 * Date: Mar 30, 2005
 * Time: 4:50:44 AM
 * Copyright (c) Mar 30, 2005 by Sam Reid
 */

public class MMPlot extends PlotDevice {
    public MMPlot( MovingManApparatusPanel movingManApparatusPanel, String name ) {
        super( movingManApparatusPanel, new Range2D( 0, -10, 20, 10 ), name );
        getChart().getVerticalGridlines().setMajorTickSpacing( 10 );
        getChart().getVerticalTicks().setMajorTickSpacing( 5 );
        getChart().getVerticalTicks().setMinorTickSpacing( 5 );
        getChart().getVerticalGridlines().setMajorTickSpacing( 5 );
    }

    public void valueChanged( double x ) {
    }

    public void setShift( double velocityOffset ) {}

    public void updateSlider() {
    }

    public void cursorMovedToTime( double time, int index ) {}

    public void setTextValue( double x ) {}

}
