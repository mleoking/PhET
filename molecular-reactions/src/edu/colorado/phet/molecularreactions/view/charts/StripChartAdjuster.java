/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.charts;

import edu.colorado.phet.molecularreactions.util.StripChart;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * StripChartAdjuster
 * <p>
 * Adjusts the visible range of a StripChart. Used in conjunction with
 * a control like a JScrollBar
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StripChartAdjuster implements AdjustmentListener {
    private StripChart stripChart;

    public StripChartAdjuster( StripChart stripChart ) {
        this.stripChart = stripChart;
    }

    public void adjustmentValueChanged( AdjustmentEvent e ) {
        int value = Math.max( e.getValue(), 0 );
        stripChart.setMinX( value );
    }
}
