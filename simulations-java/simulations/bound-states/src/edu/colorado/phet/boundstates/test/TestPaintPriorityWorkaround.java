/* Copyright 2006, Univeriity of Colorado */
package edu.colorado.phet.boundstates.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.model.clock.*;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * TestHelpRepaint2 demonstrates a problem with help items on Macintosh.
 * When the simulation clock is running, turning help on results
 * in the help items being partially painted.  And help items
 * (or parts of help items) that fall outside the PCanvas are
 * not painted. Other parts of the interface (eg, the Help button
 * in the control panel) are sometimes not properly painted.
 * <p>
 * Sam Reid developed the workaround shown herein. Here's his description:
 * <p>
 * The problem is most elegantly summarized in a bug report I referred to
 * earlier:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4473503
 *
 * This bug report acknowledges that swing repaint requests go on the
 * EventQueue with MEDIUM priority while AWT repaint requests go on the queue
 * with LOW priority.  This means our application sometimes never gets around
 * to AWT painting.  Swing painting occurs in opaque components, sometimes our
 * non-opaque paint requests get transferred up to the parent Component.
 *
 * I first tried this workaround, overriding the main repaint method in
 * PhetFrame:
 *
 * //Don't use this solution, read on...
 * public void repaint( long tm, int x, int y, int width, int height ) {
 *         super.repaint( tm, x, y, width, height );
 *         dispatchEvent( new PaintEvent( this, PaintEvent.UPDATE, new
 * Rectangle( x, y, width, height ) ) );
 * }
 *
 * This still calls the super.repaint, in case other things need to happen, and
 * uses the same mechanism for getting the repaint to happen (by calling
 * handleEvent() on the ComponentPeer).  This works great on XP but not at all
 * on mac (as if dispatchEvent does a no-op).  I investigated this, and the
 * PaintEvent propagates to apple.awt.ComponentModel, but after that I stopped
 * looking.
 *
 * Here is a less elegant (but more practical) workaround:
 *
 * public void repaint( long tm, int x, int y, int width, int height ) {
 *         super.repaint( tm, x, y, width, height );//just in case other important stuff happens here.
 *         update( getGraphics() );
 * }
 *
 * Pros:
 * 1. This workaround produces the desired behavior on xp and mac.
 * 2. PhetFrame.paint() is only called a few times in a few sample applications
 * I tried; BoundStates and TestHelpRepaint.  This solution could be a
 * debilitating performance problem if it was called every 30 ms.
 * 3. This solution is only one line of code, and only needs to be done in a
 * few places (I recommend on a per-application basis).
 *
 * Cons:
 * 1. This workaround ignores a great deal of AWT paint infrastructure,
 * including the Toolkit, the EventQueue and the Component.dispatchEvent().
 * 2. This workaround could be a performance problem, paint requests are not
 * coalesced.  I recommend putting a debug statement by the call to update() to
 * make sure it's not being called all the time.
 * 3. This workaround could draw incorrectly, if something needs to be done to
 * the Graphics (setting up transforms, etc) before drawing on it.
 * 4. This workaround would need to be applied to each parent component for
 * which this problem is exhibited.
 *
 * It would be nice if the AWT exposed the functionality we wanted, say, to
 * just add a PaintEvent with priority MEDIUM, but it looks closed off to me.
 * We could develop our own handler that copies the functionality that we lose
 * there: coalescing events, etc. but this would probably be too complicated
 * and may not be necessary for our usage.  Alternatively, we could build in a
 * mechanism to count the rate of calls to update() and make sure it's not too
 * high (maybe more than 1 per 100 millis on average means we should stop
 * calling update).  But these kind of heuristics can be sticky business;
 * better to try without them first.
 *
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPaintPriorityWorkaround {

    private static final int CLOCK_RATE = 10000; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick

    private static final Color BACKGROUND = new Color( 208, 255, 252 ); // light blue

    private static final double MIN_X = 0;
    private static final double MAX_X = 200;
    private static final double DX = 1;
    private static final double MIN_Y = -100;
    private static final double MAX_Y = 100;

    public static void main( final String[] args ) throws InterruptedException {
        new PhetApplicationConfig(args, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                try {
                    return new TestApplication( config );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, "bound-states").launchSim();
    }

    private static class TestApplication extends PiccoloPhetApplication {

        public TestApplication( PhetApplicationConfig config ) throws InterruptedException {
            super( config);

            // Clock
            IClock clock = new SwingClock( 1000 / TestPaintPriorityWorkaround.CLOCK_RATE, new TimingStrategy.Constant( TestPaintPriorityWorkaround.MODEL_RATE ) );

            // Modules
            Module moduleOne = new TestPaintPriorityWorkaround.TestModule( "One", clock );
            addModule( moduleOne );
            Module moduleTwo = new TestPaintPriorityWorkaround.TestModule( "Two", clock );
            addModule( moduleTwo );
            getPhetFrame().addFileMenuItem( new JMenuItem( "hello" ) );
        }

        // This is the WORKAROUND
        protected PhetFrame createPhetFrame() {
            return new PhetFrame( this ) {
                public void repaint( long tm, int x, int y, int width, int height ) {
                    super.repaint( tm, x, y, width, height );//just in case other important stuff happens here.
                    update( getGraphics() );
                }
            };
        }
    }

    private static class TestModule extends PiccoloModule {

        private PhetPCanvas _canvas;
        private XYSeries _series;
        private JFreeChartNode _chartNode;

        public TestModule( String name, IClock clock ) {
            super( name, clock, false /* startsPaused */ );

            // Canvas
            _canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
            setSimulationPanel( _canvas );
            _canvas.setBackground( TestPaintPriorityWorkaround.BACKGROUND );
            _canvas.addComponentListener( new ComponentAdapter() {

                public void componentResized( ComponentEvent event ) {
                    // update the canvas layout when its size changes
                    updateLayout();
                }
            } );

            // Parent of all nodes, working is screen coordinates
            PNode parentNode = new PNode();
            _canvas.addScreenChild( parentNode );

            // Chart to display time-varying data
            {
                // Series
                final int seriesIndex = 0;
                _series = new XYSeries( "Random data" );

                // Plot
                XYPlot plot = new XYPlot();

                // X axis
                ValueAxis xAxis = new NumberAxis( "X" );
                xAxis.setRange( TestPaintPriorityWorkaround.MIN_X, TestPaintPriorityWorkaround.MAX_X );
                plot.setDomainAxis( xAxis );

                // Y axis
                ValueAxis yAxis = new NumberAxis( "Y" );
                yAxis.setRange( TestPaintPriorityWorkaround.MIN_Y, TestPaintPriorityWorkaround.MAX_Y );
                plot.setRangeAxis( yAxis );

                // Dataset
                XYSeriesCollection dataset = new XYSeriesCollection();
                dataset.addSeries( _series );
                plot.setDataset( seriesIndex, dataset );

                // Renderer
                XYItemRenderer renderer = new StandardXYItemRenderer();
                renderer.setPaint( Color.RED );
                renderer.setStroke( new BasicStroke( 2f ) );
                plot.setRenderer( seriesIndex, renderer );

                // Chart
                JFreeChart chart = new JFreeChart( plot );
                chart.setBackgroundPaint( TestPaintPriorityWorkaround.BACKGROUND );

                _chartNode = new JFreeChartNode( chart );
                parentNode.addChild( _chartNode );
            }

            // Blue square, draggable
            PPath blueSquare = new PPath();
            blueSquare.setPaint( Color.BLUE );
            blueSquare.setPathTo( new Rectangle2D.Double( 0, 0, 150, 150 ) );
            blueSquare.addInputEventListener( new PDragEventHandler() );
            blueSquare.addInputEventListener( new CursorHandler() );
            blueSquare.setOffset( 300, 200 );
            parentNode.addChild( blueSquare );

            // Green square, draggable
            PPath greenSquare = new PPath();
            greenSquare.setPaint( Color.GREEN );
            greenSquare.setPathTo( new Rectangle2D.Double( 0, 0, 150, 150 ) );
            greenSquare.addInputEventListener( new PDragEventHandler() );
            greenSquare.addInputEventListener( new CursorHandler() );
            greenSquare.setOffset( 100, 200 );
            parentNode.addChild( greenSquare );

            // Control panel
            JCheckBox checkBox = new JCheckBox( name );
            {
                ControlPanel controlPanel = new ControlPanel();
                setControlPanel( controlPanel );

                JPanel strut = new JPanel();
                strut.setLayout( new BoxLayout( strut, BoxLayout.X_AXIS ) );
                strut.add( Box.createHorizontalStrut( 150 ) );
                controlPanel.addControlFullWidth( strut );

                // Misc controls that do nothing
                controlPanel.addControl( checkBox );
            }

            // Help items
            {
                HelpPane helpPane = getDefaultHelpPane();

                HelpBalloon clockHelp = new HelpBalloon( helpPane, "Clock controls", HelpBalloon.BOTTOM_LEFT, 80 );
                helpPane.add( clockHelp );
                clockHelp.pointAt( getClockControlPanel() );

                HelpBalloon blueSquareHelp = new HelpBalloon( helpPane, "Drag me", HelpBalloon.LEFT_CENTER, 30 );
                helpPane.add( blueSquareHelp );
                blueSquareHelp.pointAt( blueSquare, _canvas );

                HelpBalloon greenSquareHelp = new HelpBalloon( helpPane, "Drag me", HelpBalloon.RIGHT_CENTER, 30 );
                helpPane.add( greenSquareHelp );
                greenSquareHelp.pointAt( greenSquare, _canvas );

                HelpBalloon checkBoxHelp = new HelpBalloon( helpPane, "Check me", HelpBalloon.RIGHT_CENTER, 30 );
                helpPane.add( checkBoxHelp );
                checkBoxHelp.pointAt( checkBox );
            }

            // update the data when the clock ticks
            getClock().addClockListener( new ClockAdapter() {

                public void clockTicked( ClockEvent event ) {
                    final double t = event.getSimulationTime();
                    updateData( t );
                }
            } );

            // Default layout and data...
            updateLayout();
            updateData( 0 /* t=0 */ );
        }

        public boolean hasHelp() {
            return true;
        }

        // Called whenever the simulation's window size is changed
        private void updateLayout() {

            final double margin = 10;

            // Adjust the chart size
            double x = margin;
            double y = margin;
            double w = _canvas.getWidth() - ( 2 * margin );
            double h = _canvas.getHeight() - ( 2 * margin ) - y;
            _chartNode.setBounds( 0, 0, w, h );
            _chartNode.setOffset( x, y );
        }

        // Called whenever the clock ticks
        private void updateData( final double t ) {
            // Generate some data for a time-varying function...
            _series.setNotify( false );
            _series.clear();
            for( double x = TestPaintPriorityWorkaround.MIN_X; x <= TestPaintPriorityWorkaround.MAX_X + TestPaintPriorityWorkaround.DX; x += TestPaintPriorityWorkaround.DX )
            {
                double y = TestPaintPriorityWorkaround.MAX_Y * Math.sin( 3 * x - t );
                _series.add( x, y );
            }
            _series.setNotify( true );
        }

    } // class TestModule
}
