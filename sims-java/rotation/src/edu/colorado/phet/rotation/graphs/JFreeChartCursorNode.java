package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jan 29, 2007
 * Time: 8:25:49 AM
 * Copyright (c) Jan 29, 2007 by Sam Reid
 */

public class JFreeChartCursorNode extends PNode {
    private JFreeChartNode jFreeChartNode;
    private PhetPPath path;
    private double time;
    private double width = 9;

    public JFreeChartCursorNode( final JFreeChartNode jFreeChartNode ) {
        this.jFreeChartNode = jFreeChartNode;
        path = new PhetPPath( new Color( 50, 50, 200, 80 ), new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{10.0f, 5.0f}, 0 ), Color.darkGray );
        addChild( path );
        update();
        //todo: add a listener for jfreechartnode chart area bounds change.

        jFreeChartNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                update();
            }
        } );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            Point2D pressPoint;
            double pressTime;

            public void mousePressed( PInputEvent event ) {
                pressPoint = event.getPositionRelativeTo( JFreeChartCursorNode.this );
                pressTime = time;
            }

            public Point2D localToPlotDifferential( double dx, double dy ) {
                Point2D pt1 = new Point2D.Double( 0, 0 );
                Point2D pt2 = new Point2D.Double( dx, dy );
                localToGlobal( pt1 );
                localToGlobal( pt2 );
                jFreeChartNode.globalToLocal( pt1 );
                jFreeChartNode.globalToLocal( pt2 );
                pt1 = jFreeChartNode.nodeToPlot( pt1 );
                pt2 = jFreeChartNode.nodeToPlot( pt2 );
                return new Point2D.Double( pt2.getX() - pt1.getX(), pt2.getY() - pt1.getY() );
            }

            public void mouseDragged( PInputEvent event ) {
                Point2D d = event.getPositionRelativeTo( JFreeChartCursorNode.this );
                Point2D dx = new Point2D.Double( d.getX() - pressPoint.getX(), d.getY() - pressPoint.getY() );
                Point2D diff = localToPlotDifferential( dx.getX(), dx.getY() );
                time = pressTime + diff.getX();
                time = MathUtil.clamp( jFreeChartNode.getChart().getXYPlot().getDomainAxis().getRange().getLowerBound(), time, jFreeChartNode.getChart().getXYPlot().getDomainAxis().getRange().getUpperBound() );

                update();
            }
        } );

        //todo: remove this timer when wired up to listen to target changes properly
        new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } ).start();
    }

    private void update() {
        Point2D topCenterModel = jFreeChartNode.plotToNode( new Point2D.Double( time, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound() ) );
        Point2D bottomCenterModel = jFreeChartNode.plotToNode( new Point2D.Double( time, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getLowerBound() ) );

        jFreeChartNode.localToGlobal( topCenterModel );
        jFreeChartNode.localToGlobal( bottomCenterModel );

        globalToLocal( topCenterModel );
        globalToLocal( bottomCenterModel );//todo: should one of these transforms be through the parent?

        path.setPathTo( new Rectangle2D.Double( topCenterModel.getX() - width / 2, topCenterModel.getY(), width, bottomCenterModel.getY() - topCenterModel.getY() ) );
    }
}