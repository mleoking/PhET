package edu.colorado.phet.common.motion.graphs;

import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:23 AM
 */

public class TimeSeriesGraphSetNode extends PNode {
    private GraphSetNode graphSetNode;
    private PSwing timeSeriesControlPanelNode;
    private boolean constructed = false;
    private PhetPPath background;

    public TimeSeriesGraphSetNode( GraphSetModel graphSetModel, TimeSeriesModel timeSeriesModel, double minDT, double maxDT ) {
        setBounds( 0, 0, 800, 600 );
        graphSetNode = new GraphSetNode( graphSetModel );
//        TimeSeriesControlPanel timeSeriesControlPanel = new TimeSeriesControlPanel( timeSeriesModel );
//        TimeSeriesPlaybackPanel timeSeriesControlPanel = new TimeSeriesPlaybackPanel( timeSeriesModel );
        TimeSeriesControlPanel timeSeriesControlPanel = new TimeSeriesControlPanel( timeSeriesModel, minDT, maxDT );
        timeSeriesControlPanelNode = new PSwing( timeSeriesControlPanel );

        background = new PhetPPath( new JLabel().getBackground());
        addChild( background );
        addChild( graphSetNode );
        addChild( timeSeriesControlPanelNode );

        PropertyChangeListener relayout = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                relayout();
            }
        };
        addPropertyChangeListener( PNode.PROPERTY_BOUNDS, relayout );
        addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, relayout );
        addPropertyChangeListener( PNode.PROPERTY_VISIBLE, relayout );

        constructed = true;
        relayout();
    }

    protected void internalUpdateBounds( double x, double y, double width, double height ) {
        relayout();
    }

    private void relayout() {
        if ( constructed ) {
//            background.setPathTo( null );
            Rectangle2D bounds = getBounds();
//            System.out.println( "TSGSN::bounds = " + bounds );
            graphSetNode.setBounds( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() - timeSeriesControlPanelNode.getFullBounds().getHeight() );
            timeSeriesControlPanelNode.setOffset( bounds.getX() + bounds.getWidth() / 2.0 - timeSeriesControlPanelNode.getFullBounds().getWidth() / 2.0, graphSetNode.getFullBounds().getMaxY() );
            background.setPathTo(bounds);
//            System.out.println( "bounds.getMaxY() = " + bounds.getMaxY() + ", tscpn.getmaxy=" + timeSeriesControlPanelNode.getFullBounds().getMaxY() );
        }
    }

    public void setFlowLayout() {
        graphSetNode.setFlowLayout();
    }

    public void setAlignedLayout() {
        graphSetNode.setAlignedLayout();
    }

    public void forceRepaintGraphs() {
        graphSetNode.forceRepaintGraphs();
    }

    public void forceRelayout() {
        graphSetNode.forceRelayout();
    }
}
