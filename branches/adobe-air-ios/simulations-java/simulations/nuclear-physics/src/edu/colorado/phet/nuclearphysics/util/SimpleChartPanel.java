// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.util;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 * This class simplifies the creation of a JFreeChart chart panel, which has a
 * long and involved constructor.
 *
 * @author John Blanco
 */
public class SimpleChartPanel extends ChartPanel {

    public SimpleChartPanel(JFreeChart chart, int width, int height){
        
        super(chart, width, height, width, height, width, height, true, false, false, false, false, false);
        
    }
}
