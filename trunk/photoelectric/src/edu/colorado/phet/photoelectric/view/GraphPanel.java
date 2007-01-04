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

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.ApparatusPanel2;

import java.awt.*;

/**
 * GraphPanel
 * <p/>
 * An ApparatusPanel2 that contains a PhotoelectricGraph instance, for putting in Swing
 * Containters
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GraphPanel extends ApparatusPanel2 {
    private PhotoelectricGraph graph;

    public GraphPanel( IClock clock ) {
        super( clock );
        setUseOffscreenBuffer( true );
        setDisplayBorder( false );
    }

    public void setGraph( PhotoelectricGraph graph, Insets insets ) {
        setPreferredSize( new Dimension( (int)graph.getChartSize().getWidth() + insets.left + insets.right,
                                         (int)graph.getChartSize().getHeight() + insets.top + insets.bottom ) );
        graph.setLocation( insets.left, insets.top );
        addGraphic( graph );
        this.graph = graph;
    }

    public void clearGraph() {
        graph.clearData();
    }

    public PhotoelectricGraph getGraph() {
        return graph;
    }
}
