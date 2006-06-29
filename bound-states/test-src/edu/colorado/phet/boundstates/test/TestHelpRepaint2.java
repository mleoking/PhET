/* Copyright 2004, Sam Reid */
package edu.colorado.phet.boundstates.test;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

/**
 * TestHelpRepaint demonstrates a problem with help items on Macintosh.
 * When the simulation clock is running, turning help on results
 * in the help items being partially painted.  And help items
 * (or parts of help items) that fall outside the PCanvas are
 * not painted. Other parts of the interface (eg, the Help button
 * in the control panel) are sometimes not properly painted.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestHelpRepaint2 {

    private static final int CLOCK_RATE = 10000; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick

    private static final Color BACKGROUND = new Color( 208, 255, 252 ); // light blue

    private static final double MIN_X = 0;
    private static final double MAX_X = 200;
    private static final double DX = 1;
    private static final double MIN_Y = -100;
    private static final double MAX_Y = 100;

    public static void main( final String[] args ) throws InterruptedException {

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {

                try {
                    TestHelpRepaint2.TestApplication app = new TestHelpRepaint2.TestApplication( args );
                    app.startApplication();
                    RepaintManager.setCurrentManager( new DebugRepaintManager() );
                }
                catch( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );

//        TestApplication app = new TestApplication( args );
//        app.startApplication();
    }

    static class DebugRepaintManager extends RepaintManager {
        public DebugRepaintManager() {
            super();
            setDoubleBufferingEnabled( false );
        }

        public void paintDirtyRegions() {
            super.paintDirtyRegions();
        }

        public void validateInvalidComponents() {
            super.validateInvalidComponents();
        }

        public boolean isDoubleBufferingEnabled() {
            return super.isDoubleBufferingEnabled();
        }

        public void setDoubleBufferingEnabled( boolean aFlag ) {
            super.setDoubleBufferingEnabled( aFlag );
        }

        public Dimension getDoubleBufferMaximumSize() {
            return super.getDoubleBufferMaximumSize();
        }

        public void setDoubleBufferMaximumSize( Dimension d ) {
            super.setDoubleBufferMaximumSize( d );
        }

        public synchronized String toString() {
            return super.toString();
        }

        public synchronized void addInvalidComponent( JComponent invalidComponent ) {
            super.addInvalidComponent( invalidComponent );
        }

        public void markCompletelyClean( JComponent aComponent ) {
            super.markCompletelyClean( aComponent );
        }

        public void markCompletelyDirty( JComponent aComponent ) {
            super.markCompletelyDirty( aComponent );
        }

        public synchronized void removeInvalidComponent( JComponent component ) {
            super.removeInvalidComponent( component );
        }

        public boolean isCompletelyDirty( JComponent aComponent ) {
            return super.isCompletelyDirty( aComponent );
        }

        public synchronized void addDirtyRegion( JComponent c, int x, int y, int w, int h ) {
            super.addDirtyRegion( c, x, y, w, h );
        }

        public Image getOffscreenBuffer( Component c, int proposedWidth, int proposedHeight ) {
            return super.getOffscreenBuffer( c, proposedWidth, proposedHeight );
        }

        public Image getVolatileOffscreenBuffer( Component c, int proposedWidth, int proposedHeight ) {
            return super.getVolatileOffscreenBuffer( c, proposedWidth, proposedHeight );
        }

        public Rectangle getDirtyRegion( JComponent aComponent ) {
            return super.getDirtyRegion( aComponent );
        }
    }

    private static class TestApplication extends PiccoloPhetApplication {

        public TestApplication( String[] args ) throws InterruptedException {
            super( args, "TestHelpRepaint", "description", "0.1", new FrameSetup.CenteredWithSize( 1024, 768 ) );

            // Clock
            IClock clock = new SwingClock( 1000 / TestHelpRepaint2.CLOCK_RATE, new TimingStrategy.Constant( TestHelpRepaint2.MODEL_RATE ) );

            // Modules
            Module moduleOne = new TestHelpRepaint2.TestModule( "One", clock );
            addModule( moduleOne );
            Module moduleTwo = new TestHelpRepaint2.TestModule( "Two", clock );
            addModule( moduleTwo );
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
            _canvas.setBackground( TestHelpRepaint2.BACKGROUND );
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
                xAxis.setRange( TestHelpRepaint2.MIN_X, TestHelpRepaint2.MAX_X );
                plot.setDomainAxis( xAxis );

                // Y axis
                ValueAxis yAxis = new NumberAxis( "Y" );
                yAxis.setRange( TestHelpRepaint2.MIN_Y, TestHelpRepaint2.MAX_Y );
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
                chart.setBackgroundPaint( TestHelpRepaint2.BACKGROUND );

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
            ;
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
            for( double x = TestHelpRepaint2.MIN_X; x <= TestHelpRepaint2.MAX_X + TestHelpRepaint2.DX; x += TestHelpRepaint2.DX )
            {
                double y = TestHelpRepaint2.MAX_Y * Math.sin( 3 * x - t );
                _series.add( x, y );
            }
            _series.setNotify( true );
        }

    } // class TestModule
}
