// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fourier.view.discrete;

import java.awt.*;

import edu.colorado.phet.common.charts.Range2D;

/**
 * DiscreteSumChart is the "Sum" chart in the Discrete module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DiscreteSumChart extends WaveformChart {

    private static final double Y_MAJOR_TICK_SPACING = 5.0;
    private static final double Y_MINOR_TICK_SPACING = 1.0;

    public DiscreteSumChart( Component component, Range2D range, Dimension chartSize ) {
        super( component, range, chartSize, Y_MAJOR_TICK_SPACING );

        // Horizontal gridlines for minor ticks
        getHorizonalGridlines().setMinorGridlinesVisible( true );
        getHorizonalGridlines().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
        getHorizonalGridlines().setMinorGridlinesColor( Color.BLACK );
        getHorizonalGridlines().setMinorGridlinesStroke( new BasicStroke( 0.25f ) );
    }
}
