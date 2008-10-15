/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumwaveinterference.tests.ui;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.DeprecatedPhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.*;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


/**
 * TestAppStartup tests a start up problem with PhET simulations.
 * This problem seems to occur on Windows in simulatons that use
 * Piccolo and JFreeChart. The "play area" is painted, but all
 * other module panels (control panel, clock controls, etc)
 * are blank.  If the user takes some action to cause a repaint
 * (eg, opening a dialog) then the blank panels are repainted.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestAppStartup extends DeprecatedPhetApplicationLauncher {

    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 0.1; // model time: dt per clock tick

    private static final double MIN_X = 0;
    private static final double MAX_X = 200;
    private static final double DX = 10;
    private static final double MIN_Y = 0;
    private static final double MAX_Y = 100;

    private static final Color BACKGROUND = new Color( 208, 255, 252 ); // light blue

    public static void main( final String[] args ) {
        try {
            TestAppStartup app = new TestAppStartup( args );
            app.startApplication();
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public TestAppStartup( String[] args ) throws InterruptedException {
        super( args, "TestAppStartUp", "description", "0.1", new FrameSetup.CenteredWithSize( 1024, 768 ) );

        // Add one module to the application...
        IClock clock = new SwingClock( 1000 / CLOCK_RATE, new TimingStrategy.Constant( MODEL_RATE ) );
        Module module = new TestModule( clock );
        addModule( module );
    }

    private static class TestModule extends PiccoloModule {

        private XYSeries _series; // data model

        private PhetPCanvas _canvas;
        private PSwing _pButton;
        private JFreeChartNode _chartNode;

        public TestModule( IClock clock ) {
            super( "TestModule", clock );

            setLogoPanel( null );

            // Model
            {
                _series = new XYSeries( "Random data" );

                getClock().addClockListener( new ClockAdapter() {
                    // update the model when the clock ticks
                    public void clockTicked( ClockEvent event ) {
                        updateModel();
                    }
                } );
            }

            // Play area
            {
                // Canvas
                _canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
                setPhetPCanvas( _canvas );
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

                // PSwing button
                JButton jButton = new JButton( "Press Me" );
                jButton.setOpaque( false );
                _pButton = new PSwing( jButton );
                parentNode.addChild( _pButton );
                jButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        Frame frame = PhetApplication.instance().getPhetFrame();
                        JOptionPane.showMessageDialog( frame, "Press OK" );
                    }
                } );

                // Chart
                XYDataset dataset = new XYSeriesCollection( _series );
                XYPlot plot = new XYPlot();
                plot.setDataset( dataset );
                ValueAxis xAxis = new NumberAxis( "X" );
                xAxis.setRange( MIN_X, MAX_X );
                plot.setDomainAxis( xAxis );
                ValueAxis yAxis = new NumberAxis( "Y" );
                yAxis.setRange( MIN_Y, MAX_Y );
                plot.setRangeAxis( yAxis );
                plot.setRenderer( new StandardXYItemRenderer() );
                JFreeChart chart = new JFreeChart( plot );
                chart.setBackgroundPaint( BACKGROUND );
                _chartNode = new JFreeChartNode( chart );
                parentNode.addChild( _chartNode );

                layoutCanvas();
            }

            // Control panel
            {
                ControlPanel controlPanel = new ControlPanel( this );
                setControlPanel( controlPanel );

                // Misc controls that do nothing
                controlPanel.addControl( new JCheckBox( "See no evil" ) );
                controlPanel.addControl( new JCheckBox( "Hear no evil" ) );
                controlPanel.addControl( new JCheckBox( "Speak no evil" ) );
                controlPanel.addControlFullWidth( new JSeparator() );
                controlPanel.addControl( new JCheckBox( "short clowns" ) );
                controlPanel.addControl( new JCheckBox( "tall clowns" ) );
                controlPanel.addControl( new JCheckBox( "fat clowns" ) );
                controlPanel.addControl( new JCheckBox( "skinny clowns" ) );
                controlPanel.addControl( new JButton( "Release the Clowns" ) );
                controlPanel.addControlFullWidth( new JSeparator() );
                controlPanel.addControl( new JCheckBox( "lights" ) );
                controlPanel.addControl( new JCheckBox( "camera" ) );
                controlPanel.addControl( new JCheckBox( "action" ) );
                controlPanel.addControlFullWidth( new JSeparator() );
                controlPanel.addControl( new JButton( "Check email..." ) );
                controlPanel.addControl( new JButton( "Take a nap" ) );
                controlPanel.addControlFullWidth( new JSeparator() );

                // Reset button
                JButton resetButton = new JButton( "Reset All" );
                controlPanel.addControl( resetButton );
                resetButton.addActionListener( new ActionListener() {
                    // reset after confirming
                    public void actionPerformed( ActionEvent e ) {
                        Frame frame = PhetApplication.instance().getPhetFrame();
                        int rval = JOptionPane.showConfirmDialog( frame, "Reset all settings?", "Confirm", JOptionPane.YES_NO_OPTION );
                        if( rval == JOptionPane.YES_OPTION ) {
                            // TODO reset controls here...
                        }
                    }
                } );
            }
        }

        private void layoutCanvas() {

            final double margin = 10;

            // PSwing button
            _pButton.setOffset( margin, margin );

            // Chart
            double x = margin;
            double y = margin + _pButton.getFullBounds().getHeight() + margin;
            double w = _canvas.getWidth() - ( 2 * margin );
            double h = _canvas.getHeight() - ( 2 * margin ) - y;
            _chartNode.setBounds( 0, 0, w, h );
            _chartNode.setOffset( x, y );
        }

        private void updateModel() {
            Thread.currentThread().setPriority( Thread.MAX_PRIORITY );
            // Randomly generate some data...
            _series.setNotify( false );
            _series.clear();
            for( double x = MIN_X; x <= MAX_X + DX; x += DX ) {
                double y = MIN_Y + ( Math.random() * ( MAX_Y - MIN_Y ) );
                _series.add( x, y );
            }
            _series.setNotify( true );
        }
    }
}
