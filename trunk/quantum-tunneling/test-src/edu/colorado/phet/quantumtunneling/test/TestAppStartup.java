/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.quantumtunneling.piccolo.JFreeChartNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;


/**
 * TestAppStartup
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestAppStartup extends PhetApplication {
    
    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 0.1; // model time: dt per clock tick
    private static final String CLOCK_DISPLAY_PATTERN = "0.0"; // should match precision of MODEL_RATE
    
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
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public TestAppStartup( String[] args ) throws InterruptedException
    {
        super( args, "TestAppStartUp", "description", "0.1", new FrameSetup.CenteredWithSize( 1024, 768 ) );
        
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
            super( "TestModule", clock, true /* clockStartsPaused */ );
            
            setLogoPanel( null );
            
            // Model
            {
                _series = new XYSeries( "Chaos" );
                
                // Update the data model when the clock ticks...
                ClockListener clockListener = new ClockAdapter() {
                    private DecimalFormat _clockFormat = new DecimalFormat( CLOCK_DISPLAY_PATTERN );
                    
                    public void clockTicked( ClockEvent event ) {
                        updateSeries();
                    }
                    
                    private void updateSeries() {
                        _series.setNotify( false );
                        _series.clear();
                        for ( double x = MIN_X; x <= MAX_X + DX; x += DX ) {
                            double y = MIN_Y + ( Math.random() * ( MAX_Y - MIN_Y ) );
                            _series.add( x, y );
                        }
                        _series.setNotify( true );
                    }
                };
                getClock().addClockListener( clockListener );
            }
            
            // Play area
            {
                // Canvas
                _canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
                setPhetPCanvas( _canvas );
                _canvas.setBackground( BACKGROUND );
                _canvas.addComponentListener( new ComponentAdapter() { 
                    public void componentResized( ComponentEvent event ) {
                        layoutCanvas();
                    }
                } );
                
                // Parent of all nodes
                PNode parentNode = new PNode();
                _canvas.addScreenChild( parentNode );
               
                // PSwing button
                JButton jButton = new JButton( "Press Me" );
                jButton.setOpaque( false );
                _pButton = new PSwing( _canvas, jButton );
                parentNode.addChild( _pButton );
                jButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        JOptionPane.showMessageDialog( PhetApplication.instance().getPhetFrame(), 
                                "<html>That's enough singing for now, lads...<br>looks like there's dirty work afoot.</html>" );
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
            }
            
            // Control panel
            {
                ControlPanel controlPanel = new ControlPanel( this );
                setControlPanel( controlPanel );
                
                // Misc controls that do nothing
                controlPanel.addControl( new JCheckBox( "See no evil" ) );
                controlPanel.addControl( new JCheckBox( "Hear no evil" ) );
                controlPanel.addControl( new JCheckBox( "Speak no evil" ) );
                controlPanel.addControl( new JButton( "Release the Clowns" ) );
                controlPanel.addControl( new JButton( "Check email..." ) );
                controlPanel.addControl( new JButton( "Take a nap" ) );
                controlPanel.addControlFullWidth( new JSeparator() );
                
                // Reset button
                JButton resetButton = new JButton( "Reset All" );
                controlPanel.addControl( resetButton );
                resetButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        Frame frame = PhetApplication.instance().getPhetFrame();
                        int rval = JOptionPane.showConfirmDialog( frame, "Reset all settings?", "Confirm", JOptionPane.YES_NO_OPTION );
                        if ( rval == JOptionPane.YES_OPTION ) {
                            // TODO reset controls here...  
                        }
                    }
                } );
            }
            
            layoutCanvas();
            
            // Start the clock
            getClock().start();
        }
        
        private void layoutCanvas() {
            
            final double margin = 10;
            
            AffineTransform t1 = new AffineTransform();
            t1.translate( margin, margin );
            _pButton.setTransform( t1 );
            
            double x = margin;
            double y = margin + _pButton.getFullBounds().getHeight() + margin;
            double w = _canvas.getWidth() - ( 2 * margin );
            double h = _canvas.getHeight() - ( 2 * margin ) - y;
            _chartNode.setBounds( 0, 0, w, h );
            AffineTransform t2 = new AffineTransform();
            t2.translate( x, y );
            _chartNode.setTransform( t2 );
        }
    }
}
