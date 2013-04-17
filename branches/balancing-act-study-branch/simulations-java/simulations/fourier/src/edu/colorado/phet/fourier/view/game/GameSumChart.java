// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fourier.view.game;

import java.awt.*;

import edu.colorado.phet.common.charts.Range2D;
import edu.colorado.phet.fourier.view.discrete.DiscreteSumChart;

/**
 * GameSumChart is the "Sum" chart in the Game module.
 * Identical to the "Sum" chart in the Discrete module, but adds an autoscale interface.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameSumChart extends DiscreteSumChart {

    public GameSumChart( Component component, Range2D range, Dimension chartSize ) {
        super( component, range, chartSize );
        autoscaleY( range.getMaxY() );
    }

    // Rescales the Y-axis range, tick marks and gridlines.
    public void autoscaleY( double maxY ) {

        // Range
        if ( maxY < 4 / Math.PI ) {
            maxY = 4 / Math.PI;
        }
        Range2D range = getRange();
        range.setMaxY( maxY );
        range.setMinY( -maxY );
        setRange( range );

        // Y axis ticks and gridlines
        {
            double tickSpacing;
            if ( range.getMaxY() < 2 ) {
                tickSpacing = 0.5;
            }
            else if ( range.getMaxY() < 5 ) {
                tickSpacing = 1.0;
            }
            else {
                tickSpacing = 5.0;
            }
            getVerticalTicks().setMajorTickSpacing( tickSpacing );
            getHorizonalGridlines().setMajorTickSpacing( tickSpacing );
        }
    }
}
