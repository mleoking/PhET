/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;

import java.awt.*;

/**
 * CurrentVsVoltageGraphPanel
 * <p/>
 * An ApparatusPanel2 that contains a CurrentVsVoltageGraph, for putting in Swing
 * Containters
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GraphPanel extends ApparatusPanel2 {
    public GraphPanel( AbstractClock clock ) {
        super( clock );
        setUseOffscreenBuffer( true );
        setDisplayBorder( false );
    }

    public void setGraph( Chart graph, Insets insets ) {
        setPreferredSize( new Dimension( (int)graph.getChartSize().getWidth() + insets.left + insets.right,
                                         (int)graph.getChartSize().getHeight() + insets.top + insets.bottom ) );
        graph.setLocation( insets.left, insets.top );
        addGraphic( graph );
    }
}
