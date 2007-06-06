package edu.colorado.phet.common.jfreechartphet.test;

/**
 * User: Sam Reid
 * Date: Jan 22, 2007
 * Time: 11:56:07 PM
 */

import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.DynamicJFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.SeriesView;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.SeriesData;
import edu.colorado.phet.common.jfreechartphet.piccolo.dynamic.IncrementalPPathSeriesView;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolox.pswing.PSwing;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestDynamicJFreeChartNode {
    private JFrame frame;
    private Timer timer;
    private double t0 = System.currentTimeMillis();
    private PhetPCanvas phetPCanvas;
    private DynamicJFreeChartNode dynamicJFreeChartNode;
    private PSwing pSwing;

    public TestDynamicJFreeChartNode() {
        frame = new JFrame();
        frame.setSize( 1280 , 768 - 100 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        phetPCanvas = new PhetPCanvas();
        frame.setContentPane( phetPCanvas );

        JFreeChart chart = ChartFactory.createXYLineChart( "title", "x", "y", new XYSeriesCollection(), PlotOrientation.VERTICAL, false, false, false );
        dynamicJFreeChartNode = new DynamicJFreeChartNode( phetPCanvas, chart );
        dynamicJFreeChartNode.addSeries( "sine", Color.blue );
        chart.getXYPlot().getRangeAxis().setAutoRange( false );
        chart.getXYPlot().getRangeAxis().setRange( -1, 1 );
        chart.getXYPlot().getDomainAxis().setAutoRange( false );
        chart.getXYPlot().getDomainAxis().setRange( 0, 1 );

        phetPCanvas.addScreenChild( dynamicJFreeChartNode );

        JPanel panel = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
        panel.add( createButton( "JFreeChart Series", buttonGroup, true, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setJFreeChartSeries();
            }
        } ) );
        panel.add( createButton( "Piccolo Series", buttonGroup, false, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setPiccoloSeries();
            }
        } ) );
        panel.add( createButton( "Incremental Piccolo Series", buttonGroup, false, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setViewFactory( new DynamicJFreeChartNode.SeriesViewFactory() {
                    public SeriesView createSeriesView( DynamicJFreeChartNode dynamicJFreeChartNode, SeriesData seriesData ) {
                        return new IncrementalPPathSeriesView( dynamicJFreeChartNode, seriesData );
                    }
                } );
            }
        } ) );
        panel.add( createButton( "Buffered Series", buttonGroup, false, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setBufferedSeries();
            }
        } ) );
        panel.add( createButton( "Buffered Immediate", buttonGroup, false, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dynamicJFreeChartNode.setBufferedImmediateSeries();
            }
        } ) );
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
        final JCheckBox debug = new JCheckBox( "Show Regions", PDebug.debugBounds );
        debug.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PDebug.debugRegionManagement = debug.isSelected();
            }
        } );
        panel.add( jCheckBox );
        panel.add( debug );
        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clear();
            }
        } );
        panel.add( clear );
        pSwing = new PSwing( panel );
        phetPCanvas.addScreenChild( pSwing );

        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateGraph();
            }
        } );
        phetPCanvas.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_F1 && e.isAltDown() ) {
                    PDebug.debugRegionManagement = !PDebug.debugRegionManagement;
                }
            }
        } );
        phetPCanvas.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                phetPCanvas.requestFocus();
            }
        } );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        relayout();
    }

    protected void updateGraph() {
//        double t = ( System.currentTimeMillis() - t0 ) / 1000.0;
        double t = ( System.currentTimeMillis() - t0 ) / 500.0;
        double frequency = 1.0 / 10.0;
//        double y = Math.sin( t * 2 * Math.PI * frequency );
        double y = 0;
        Point2D.Double pt = new Point2D.Double( t / 100.0, y );
        dynamicJFreeChartNode.addValue( pt.getX(), pt.getY() );

//        dynamicJFreeChartNode.updateChartRenderingInfo();
//        System.out.println( "dynamicJFreeChartNode.getDataArea( ) = " + dynamicJFreeChartNode.getDataArea() );
    }

    public DynamicJFreeChartNode getDynamicJFreeChartNode() {
        return dynamicJFreeChartNode;
    }

    public PhetPCanvas getPhetPCanvas() {
        return phetPCanvas;
    }

    public PSwing getPSwing() {
        return pSwing;
    }

    private void clear() {
        dynamicJFreeChartNode.clear();
        t0 = System.currentTimeMillis();
    }

    private JComponent createButton( String text, ButtonGroup buttonGroup, boolean enabled, ActionListener actionListener ) {
        JRadioButton jRadioButton = new JRadioButton( text, enabled );
        buttonGroup.add( jRadioButton );
        jRadioButton.addActionListener( actionListener );
        if( enabled ) {
            actionListener.actionPerformed( null );
        }
        return jRadioButton;
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

    public double getInitialTime() {
        return t0;
    }

    public static void main( String[] args ) {
        new TestDynamicJFreeChartNode().start();
    }
}
