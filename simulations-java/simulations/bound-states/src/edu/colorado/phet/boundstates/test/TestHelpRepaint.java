/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.test;

import java.awt.*;
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
import edu.colorado.phet.common.phetcommon.application.*;
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
 * TestHelpRepaint demonstrates a problem with help items on Macintosh.
 * When the simulation clock is running, turning help on results
 * in the help items being partially painted.  And help items
 * (or parts of help items) that fall outside the PCanvas are
 * not painted. Other parts of the interface (eg, the Help button
 * in the control panel) are sometimes not properly painted.
 * <p>
 * The workaround is to force a paint of the entire component tree
 * when the help state changes. Set setHelpEnabled and paintImmediately.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestHelpRepaint {

    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick

    private static final Color BACKGROUND = new Color( 208, 255, 252 ); // light blue

    private static final double MIN_X = 0;
    private static final double MAX_X = 200;
    private static final double DX = 1;
    private static final double MIN_Y = -100;
    private static final double MAX_Y = 100;

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, "bound-states" ), new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                try {
                    return new TestApplication( config );
                }
                catch( Exception e ) {
                    return null;
                }
            }
        } );
    }

    private static class TestApplication extends PiccoloPhetApplication {

        public TestApplication( PhetApplicationConfig config ) throws InterruptedException {
            super( config );

            // Clock
            IClock clock = new SwingClock( 1000 / CLOCK_RATE, new TimingStrategy.Constant( MODEL_RATE ) );

            // Modules
            Module moduleOne = new TestModule( "One", clock );
            addModule( moduleOne );
            Module moduleTwo = new TestModule( "Two", clock );
            addModule( moduleTwo );
        }
    }

    private static class TestModule extends PiccoloModule {

        private PhetPCanvas _canvas;
        private XYSeries _series;
        private JFreeChartNode _chartNode;

        public TestModule( String name, IClock clock ) {
            super( name, clock, false /* startsPaused */);

            // Canvas
            _canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
            setSimulationPanel( _canvas );
            _canvas.setBackground( BACKGROUND );
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
                xAxis.setRange( MIN_X, MAX_X );
                plot.setDomainAxis( xAxis );

                // Y axis
                ValueAxis yAxis = new NumberAxis( "Y" );
                yAxis.setRange( MIN_Y, MAX_Y );
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
                chart.setBackgroundPaint( BACKGROUND );

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
            JCheckBox checkBox = new JCheckBox( name );;
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
            updateData( 0 /* t=0 */);
        }

        public boolean hasHelp() {
            return true;
        }

        /* When the help state is changed, immediately repaint everything */
        public void setHelpEnabled( boolean enabled ) {
            super.setHelpEnabled( enabled );
            PhetFrame frame = PhetApplication.instance().getPhetFrame();
            paintImmediately( frame );
        }

        /* Immediately paints a component and all of its children */
        private void paintImmediately( Component component ) {

            // Paint the component
            if ( component instanceof JComponent ) {
                JComponent jcomponent = (JComponent)component;
                jcomponent.paintImmediately( jcomponent.getBounds() );
            }

            // Recursively paint children
            if ( component instanceof Container ) {
                Container container = (Container)component;
                int numberOfChildren = container.getComponentCount();
                for ( int i = 0; i < numberOfChildren; i++ ) {
                    Component child = container.getComponent( i );
                    paintImmediately( child );
                }
            }
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
            for ( double x = MIN_X; x <= MAX_X + DX; x += DX ) {
                double y = MAX_Y * Math.sin( 3 * x - t );
                _series.add( x, y );
            }
            _series.setNotify( true );
        }

    } // class TestModule
}
