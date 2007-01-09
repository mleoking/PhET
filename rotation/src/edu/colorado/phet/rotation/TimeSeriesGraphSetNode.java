package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.GraphSetNode;
import edu.colorado.phet.rotation.graphs.TimeSeriesControlPanel;
import edu.colorado.phet.rotation.graphs.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:23 AM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

public class TimeSeriesGraphSetNode extends PNode {
    private GraphSetNode graphSetNode;
    private PSwing timeSeriesControlPanelNode;
    private Rectangle2D.Double bounds = new Rectangle2D.Double( 0, 0, 800, 600 );

    public TimeSeriesGraphSetNode( PSwingCanvas pSwingCanvas, GraphSetModel graphSetModel, TimeSeriesModel timeSeriesModel ) {
        graphSetNode = new GraphSetNode( graphSetModel );
        TimeSeriesControlPanel timeSeriesControlPanel = new TimeSeriesControlPanel( timeSeriesModel );
        timeSeriesControlPanelNode = new PSwing( pSwingCanvas, timeSeriesControlPanel );

        addChild( graphSetNode );
        addChild( timeSeriesControlPanelNode );
        relayout();
    }

    private void relayout() {
        graphSetNode.setBounds( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() - timeSeriesControlPanelNode.getFullBounds().getHeight() );
//        timeSeriesControlPanelNode.setOffset( bounds.getX(), graphSetNode.getFullBounds().getMaxY() );
        timeSeriesControlPanelNode.setOffset( bounds.getX() + bounds.getWidth() / 2.0 - timeSeriesControlPanelNode.getFullBounds().getWidth() / 2.0, graphSetNode.getFullBounds().getMaxY() );
    }

    public boolean setBounds( double x, double y, double width, double height ) {
        this.bounds = new Rectangle2D.Double( x, y, width, height );
        boolean ok = super.setBounds( x, y, width, height );
        relayout();
//        relayout();
        relayout();
        return ok;
    }
}
