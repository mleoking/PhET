// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;

public class TestDataAreaChange {
    private JFrame frame;
    private Timer timer;
    private PhetPCanvas phetPCanvas;
    private JFreeChartNode dynamicJFreeChartNode;
    private PSwing pSwing;

    public TestDataAreaChange() {
        frame = new JFrame();
        frame.setSize( 1280, 768 - 100 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new PhetPCanvas();
        frame.setContentPane( phetPCanvas );

        JFreeChart chart = ChartFactory.createXYLineChart( "title", "x", "y", new XYSeriesCollection(), PlotOrientation.VERTICAL, false, false, false );
//        dynamicJFreeChartNode = new DynamicJFreeChartNode2( phetPCanvas, chart );
        dynamicJFreeChartNode = new DynamicJFreeChartNode( phetPCanvas, chart );
//        dynamicJFreeChartNode = new JFreeChartNode( chart);

        chart.getXYPlot().getRangeAxis().setAutoRange( false );
        chart.getXYPlot().getRangeAxis().setRange( -1, 1 );
        chart.getXYPlot().getDomainAxis().setAutoRange( false );
        chart.getXYPlot().getDomainAxis().setRange( 0, 1 );

        phetPCanvas.addScreenChild( dynamicJFreeChartNode );

        JPanel panel = new JPanel();

        final JCheckBox jCheckBox = new JCheckBox( "buffered", dynamicJFreeChartNode.isBuffered() );
        jCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setBuffered( jCheckBox.isSelected() );
            }
        } );
        dynamicJFreeChartNode.addBufferedPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                jCheckBox.setSelected( dynamicJFreeChartNode.isBuffered() );
            }
        } );
        panel.add( jCheckBox );
        pSwing = new PSwing( panel );
        phetPCanvas.addScreenChild( pSwing );

        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.out.println( "dynamicJFreeChartNode.getDataArea( ) = " + dynamicJFreeChartNode.getDataArea() );
            }
        } );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        relayout();
    }

    protected void relayout() {
        pSwing.setOffset( 0, 0 );
        dynamicJFreeChartNode.setBounds( 0, pSwing.getFullBounds().getHeight(), phetPCanvas.getWidth(), phetPCanvas.getHeight() - pSwing.getFullBounds().getHeight() );
    }

    public void start() {
        frame.setVisible( true );
        timer.start();
        phetPCanvas.requestFocus();
    }

    public static void main( String[] args ) {
        new TestDataAreaChange().start();
    }
}
