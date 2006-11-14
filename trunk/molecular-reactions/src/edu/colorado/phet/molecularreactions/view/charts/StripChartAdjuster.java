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

import java.awt.event.AdjustmentListener;
import java.awt.event.AdjustmentEvent;

/**
 * StripChartAdjuster
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class StripChartAdjuster implements AdjustmentListener {
    private MoleculePopulationsStripChart stripChart;

    public StripChartAdjuster( MoleculePopulationsStripChart stripChart ) {
        this.stripChart = stripChart;
    }

    public void adjustmentValueChanged( AdjustmentEvent e ) {
        int value = e.getValue();
        stripChart.setMinX( value );
        System.out.println( "value = " + value );
    }
}
