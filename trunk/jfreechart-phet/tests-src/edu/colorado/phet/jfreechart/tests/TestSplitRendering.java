/* Copyright 2004, Sam Reid */
package edu.colorado.phet.jfreechart.tests;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Dec 14, 2005
 * Time: 4:50:58 PM
 * Copyright (c) Dec 14, 2005 by Sam Reid
 */

public class TestSplitRendering {
    private XYSeriesCollection dataset = new XYSeriesCollection();
    public ChartFrame chartFrame;
    public Timer timer;
    public XYSeries series;
    public static BufferedImage image;
    private JFreeChart plot;

    public TestSplitRendering() {
        series = new XYSeries( "Name" );
        dataset.addSeries( series );
        plot = createScatterPlot( "Test Plot", "X-axis", "Y-axis", dataset, PlotOrientation.HORIZONTAL, true, false, false );
        final MyXYPlot p = (MyXYPlot)plot.getXYPlot();

        p.getDomainAxis().setRange( -1, 1 );
        p.getRangeAxis().setRange( -1, 1 );
        p.getDomainAxis().setAutoRange( false );
        p.getRangeAxis().setAutoRange( false );
        chartFrame = new ChartFrame( "Test Plot", plot );
        chartFrame.setDefaultCloseOperation( ChartFrame.EXIT_ON_CLOSE );
        chartFrame.pack();

        image = plot.createBufferedImage( chartFrame.getChartPanel().getWidth(), chartFrame.getChartPanel().getHeight() );


        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }

        } );

        JFrame controlFrame = new JFrame( "Controls" );
        controlFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JPanel controlPanel = new JPanel();
        final JCheckBox backOff = new JCheckBox( "Quick Render" );
        backOff.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                p.renderBackground=!backOff.isSelected();
            }
        } );
        controlPanel.add(backOff);
        controlFrame.setContentPane( controlPanel );
        controlFrame.pack();
        controlFrame.setVisible( true );
        controlFrame.setLocation( 0,chartFrame.getY()+chartFrame.getHeight());
    }

    double startTime = System.currentTimeMillis() / 1000.0;

    private void step() {
        double t = System.currentTimeMillis() / 1000.0 - startTime;
        double r = Math.cos( t / 6 );
        double theta = Math.cos( t / 5 ) * 2 * Math.PI;
        AbstractVector2D v = Vector2D.Double.parseAngleAndMagnitude( r, theta );
        series.add( v.getX(), v.getY() );
    }

    public static void main( String[] args ) {
        new TestSplitRendering().start();
    }

    private void start() {
        chartFrame.setVisible( true );
        timer.start();
    }

    static class MyXYPlot extends XYPlot {
        private boolean renderData = true;
        private boolean renderBackground = true;

        public MyXYPlot( XYDataset dataset, NumberAxis xAxis, NumberAxis yAxis, XYItemRenderer o ) {
            super( dataset, xAxis, yAxis, o );
        }

        public void draw( Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info ) {
            if( renderBackground ) {
                super.draw( g2, area, anchor, parentState, info );
            }
            else if( renderData ) {
                drawForDataOnly( g2, area, anchor, parentState, info );
            }
        }

        private void drawForDataOnly( Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info ) {
            if( !renderBackground ) {
                g2.drawRenderedImage( image, new AffineTransform() );
            }
            // if the plot area is too small, just return...
            boolean b1 = ( area.getWidth() <= MINIMUM_WIDTH_TO_DRAW );
            boolean b2 = ( area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW );
            if( b1 || b2 ) {
                return;
            }

            // record the plot area...
            if( info != null ) {
                info.setPlotArea( area );
            }

            // adjust the drawing area for the plot insets (if any)...
            RectangleInsets insets = getInsets();
            insets.trim( area );

            AxisSpace space = calculateAxisSpace( g2, area );
            Rectangle2D dataArea = space.shrink( area, null );
            super.getAxisOffset().trim( dataArea );

            if( info != null ) {
                info.setDataArea( dataArea );
            }

            if( anchor != null && !dataArea.contains( anchor ) ) {
                anchor = null;
            }
            CrosshairState crosshairState = new CrosshairState();
            crosshairState.setCrosshairDistance( Double.POSITIVE_INFINITY );
            crosshairState.setAnchor( anchor );
            crosshairState.setCrosshairX( getDomainCrosshairValue() );
            crosshairState.setCrosshairY( getRangeCrosshairValue() );
            Shape originalClip = g2.getClip();
            Composite originalComposite = g2.getComposite();

            g2.clip( dataArea );
            g2.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, getForegroundAlpha()
                    )
            );

            // now draw annotations and render data items...
            boolean foundData = false;

            for( int i = getDatasetCount() - 1; i >= 0; i-- ) {
                foundData = render( g2, dataArea, i, info, crosshairState )
                            || foundData;
            }

            g2.setClip( originalClip );
            g2.setComposite( originalComposite );
        }

        public boolean render( Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CrosshairState crosshairState ) {
            if( renderData ) {
                return super.render( g2, dataArea, index, info, crosshairState );
            }
            else {
                return false;
            }
        }
    }

    public static JFreeChart createScatterPlot( String title,
                                                String xAxisLabel,
                                                String yAxisLabel,
                                                XYDataset dataset,
                                                PlotOrientation orientation,
                                                boolean legend,
                                                boolean tooltips,
                                                boolean urls ) {

        if( orientation == null ) {
            throw new IllegalArgumentException( "Null 'orientation' argument." );
        }
        NumberAxis xAxis = new NumberAxis( xAxisLabel );
        xAxis.setAutoRangeIncludesZero( false );
        NumberAxis yAxis = new NumberAxis( yAxisLabel );
        yAxis.setAutoRangeIncludesZero( false );

        MyXYPlot plot = new MyXYPlot( dataset, xAxis, yAxis, null );

        XYToolTipGenerator toolTipGenerator = null;
        if( tooltips ) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }

        XYURLGenerator urlGenerator = null;
        if( urls ) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new FastXYLineAndShapeRenderer( false, true );
        renderer.setBaseToolTipGenerator( toolTipGenerator );
        renderer.setURLGenerator( urlGenerator );
        plot.setRenderer( renderer );
        plot.setOrientation( orientation );

        JFreeChart chart = new JFreeChart(
                title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend
        );

        return chart;
    }

    static class FastXYLineAndShapeRenderer extends XYLineAndShapeRenderer {
        public FastXYLineAndShapeRenderer( boolean lines, boolean shapes ) {
            super( lines, shapes );
        }

        protected void drawSecondaryPass( Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, Rectangle2D dataArea, ValueAxis rangeAxis, CrosshairState crosshairState, EntityCollection entities ) {

            Shape entityArea = null;

            // get the data point...
            double x1 = dataset.getXValue( series, item );
            double y1 = dataset.getYValue( series, item );
            if( Double.isNaN( y1 ) || Double.isNaN( x1 ) ) {
                return;
            }

            RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
            double transX1 = domainAxis.valueToJava2D( x1, dataArea, xAxisLocation );
            double transY1 = rangeAxis.valueToJava2D( y1, dataArea, yAxisLocation );

            if( getItemShapeVisible( series, item ) ) {
                Rectangle2D.Double shape = (Rectangle2D.Double)getItemShape( series, item );
                shape = new Rectangle2D.Double( shape.x, shape.y, shape.width, shape.height );
                PlotOrientation orientation = plot.getOrientation();
                if( orientation == PlotOrientation.HORIZONTAL ) {
                    shape.x += transY1;
                    shape.y += transX1;
                }
//                else if( orientation == PlotOrientation.VERTICAL ) {
//                    shape = ShapeUtilities.createTranslatedShape(
//                            shape, transX1, transY1
//                    );
//                }
                entityArea = shape;
                if( shape.intersects( dataArea ) ) {
                    if( getItemShapeFilled( series, item ) ) {
                        if( this.getUseFillPaint() ) {
                            g2.setPaint( getItemFillPaint( series, item ) );
                        }
                        else {
                            g2.setPaint( getItemPaint( series, item ) );
                        }
                        g2.fill( shape );
                    }
//                    if( this.getDrawOutlines() ) {
//                        if( getUseOutlinePaint() ) {
//                            g2.setPaint( getItemOutlinePaint( series, item ) );
//                        }
//                        else {
//                            g2.setPaint( getItemPaint( series, item ) );
//                        }
//                        g2.setStroke( getItemOutlineStroke( series, item ) );
//                        g2.draw( shape );
//                    }
                }
            }

            updateCrosshairValues(
                    crosshairState, x1, y1, transX1, transY1, plot.getOrientation()
            );

            // add an entity for the item...
            if( entities != null ) {
                addEntity(
                        entities, entityArea, dataset, series, item, transX1, transY1
                );
            }
        }
    }
}
