package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.GraphSetNode;
import edu.colorado.phet.rotation.timeseries.TimeSeriesControlPanel;
import edu.colorado.phet.rotation.timeseries.TimeSeriesModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private Rectangle2D.Double layoutBounds = null;

    public TimeSeriesGraphSetNode( PSwingCanvas pSwingCanvas, GraphSetModel graphSetModel, TimeSeriesModel timeSeriesModel ) {
        setBounds( 0, 0, 800, 600 );
        graphSetNode = new GraphSetNode( graphSetModel );
        TimeSeriesControlPanel timeSeriesControlPanel = new TimeSeriesControlPanel( timeSeriesModel );
        timeSeriesControlPanelNode = new PSwing( pSwingCanvas, timeSeriesControlPanel );

        addChild( graphSetNode );
        addChild( timeSeriesControlPanelNode );
        //todo: this is a hack to ensure the layout is correct after the sim comes up.
        Timer timer = new Timer( 2000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                layoutBounds = null;
                relayout();
            }
        } );
        timer.start();

    }

    private void relayout() {
        Rectangle2D bounds = getBounds();
        System.out.println( "TSGSN::bounds = " + bounds );
        if( layoutBounds == null || !bounds.equals( layoutBounds ) ) {
            graphSetNode.setBounds( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() - timeSeriesControlPanelNode.getFullBounds().getHeight() );
            timeSeriesControlPanelNode.setOffset( bounds.getX() + bounds.getWidth() / 2.0 - timeSeriesControlPanelNode.getFullBounds().getWidth() / 2.0, graphSetNode.getFullBounds().getMaxY() );
            layoutBounds = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
        }
    }

    protected void layoutChildren() {
        super.layoutChildren();
        relayout();
    }

}
