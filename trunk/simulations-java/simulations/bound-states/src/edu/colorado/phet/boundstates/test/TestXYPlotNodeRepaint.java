/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.XYPlotNode;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.*;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.phetgraphics.test.DeprecatedPhetApplicationLauncher;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * TestXYPlotNodeRepaint tests problems with repainting.
 * If the Piccolo tree contains an XYPlotNode, it draws on top of things
 * that it should be behind.  This occurs while dragging some object that
 * is on top of the XYPlotNode.
 * <p>
 * In this example, try dragging either of the two rectangles.
 * You'll notice that the XYPlotNode will draw its plot on top of the
 * rectangle that is not being dragged.
 * <p>
 * The problem was fixed in XYPlotNode, by setting the clip
 * using PPaintContext.pushClip instead of Graphics2D.setClip.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestXYPlotNodeRepaint extends DeprecatedPhetApplicationLauncher {

    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick

    private static final double MIN_X = 0;
    private static final double MAX_X = 200;
    private static final double DX = 1;
    private static final double MIN_Y = -100;
    private static final double MAX_Y = 100;

    private static final Color BACKGROUND = new Color( 208, 255, 252 ); // light blue

    public static void main( final String[] args ) {
        try {
            TestXYPlotNodeRepaint app = new TestXYPlotNodeRepaint( args );
            app.startApplication();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public TestXYPlotNodeRepaint( String[] args ) throws InterruptedException {
        super( args, "TestXYPlotNodeRepaint", "description", "0.1", new FrameSetup.CenteredWithSize( 1024, 768 ) );

        // Add one module to the application...
        IClock clock = new SwingClock( 1000 / CLOCK_RATE, new TimingStrategy.Constant( MODEL_RATE ) );
        Module module = new TestModule( clock );
        addModule( module );
    }

    private static class TestModule extends PiccoloModule {

        private PhetPCanvas _canvas;
        private XYSeries _series;
        private JFreeChartNode _chartNode;
        private XYPlotNode _plotNode;

        public TestModule( IClock clock ) {
            super( "TestModule", clock, true /* clock starts paused */ );

            setLogoPanel( null );

            // Canvas
            _canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
            setSimulationPanel( _canvas );
            _canvas.setBackground( BACKGROUND );
            _canvas.addComponentListener( new ComponentAdapter() {

                public void componentResized( ComponentEvent event ) {
                    // layout the canvas when its size changes
                    layoutCanvas();
                }
            } );

            // Parent of all nodes
            PNode parentNode = new PNode();
            _canvas.addScreenChild( parentNode );

            // Chart node, for drawing the chart's background, contains no data.
            XYPlot emptyPlot = createPlot();
            JFreeChart chart = new JFreeChart( emptyPlot );
            chart.setBackgroundPaint( BACKGROUND );
            _chartNode = new JFreeChartNode( chart );
            parentNode.addChild( _chartNode );

            // Series
            final int seriesIndex = 0;
            _series = new XYSeries( "Random data" );
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _series );

            // Plot
            XYPlot plot = createPlot();
            plot.setDataset( seriesIndex, dataset );
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( Color.RED );
            renderer.setStroke( new BasicStroke( 2f ) );
            plot.setRenderer( seriesIndex, renderer );

            // Plot node, draws data on top of the static chart
            _plotNode = new XYPlotNode( plot );
            parentNode.addChild( _plotNode );

            // Blue square, draggable
            PPath blueSquare = new PPath();
            blueSquare.setPaint( Color.BLUE );
            blueSquare.setPathTo( new Rectangle2D.Double( 0, 0, 150, 150 ) );
            blueSquare.addInputEventListener( new PDragEventHandler() );
            blueSquare.addInputEventListener( new CursorHandler() );
            blueSquare.setOffset( 300, 200 );
            parentNode.addChild( blueSquare );

            // Green square, draggable
            PPath redSquare = new PPath();
            redSquare.setPaint( Color.GREEN );
            redSquare.setPathTo( new Rectangle2D.Double( 0, 0, 150, 150 ) );
            redSquare.addInputEventListener( new PDragEventHandler() );
            redSquare.addInputEventListener( new CursorHandler() );
            redSquare.setOffset( 100, 200 );
            parentNode.addChild( redSquare );

            // update the data when the clock ticks
            getClock().addClockListener( new ClockAdapter() {
                public void clockTicked( ClockEvent event ) {
                    final double t = event.getSimulationTime();
                    updateData( t );
                }
            } );

            // Default layout and data set...
            layoutCanvas();
            updateData( 0 /* t=0 */ );
        }

        // Creates the plot used by both the static chart and the XYPlotNode
        private XYPlot createPlot() {
            XYPlot plot = new XYPlot();
            ValueAxis xAxis = new NumberAxis( "X" );
            xAxis.setRange( MIN_X, MAX_X );
            plot.setDomainAxis( xAxis );
            ValueAxis yAxis = new NumberAxis( "Y" );
            yAxis.setRange( MIN_Y, MAX_Y );
            plot.setRangeAxis( yAxis );
            plot.setRenderer( new StandardXYItemRenderer() );
            return plot;
        }

        // Called whenever the simulation's window size is changed
        private void layoutCanvas() {

            final double margin = 10;

            // Chart
            double x = margin;
            double y = margin;
            double w = _canvas.getWidth() - ( 2 * margin );
            double h = _canvas.getHeight() - ( 2 * margin ) - y;
            _chartNode.setBounds( 0, 0, w, h );
            _chartNode.setOffset( x, y );
            _chartNode.updateChartRenderingInfo();

            // Plot bounds
            ChartRenderingInfo chartInfo = _chartNode.getChartRenderingInfo();
            PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();
            // Careful! getDataArea returns a direct reference!
            Rectangle2D dataAreaRef = plotInfo.getDataArea();
            Rectangle2D localBounds = new Rectangle2D.Double();
            localBounds.setRect( dataAreaRef );
            Rectangle2D plotBounds = _chartNode.localToGlobal( localBounds );

            // Plot node
            _plotNode.setOffset( 0, 0 );
            _plotNode.setDataArea( plotBounds );
        }

        // Called whenever the clock ticks
        private void updateData( final double t ) {
            // Generate some data for a time-varying function...
            _series.setNotify( false );
            _series.clear();
            for ( double x = MIN_X; x <= MAX_X + DX; x += DX ) {
                double y = MAX_Y * Math.sin( 3 * x - t );
                _series.add( x, y );
            }
            _series.setNotify( true );
        }
    }
}
