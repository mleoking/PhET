package edu.colorado.phet.common.jfreechartphet.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDebug;

/**
 * For debugging Unfuddle #1404.
 * 
 * When an unbuffered JFreeChartNode is displayed on a BufferedPhetPCanvas,
 * rendering doesn't work correctly on Mac OS 10.5 with Java 1.5.0_16.
 * In this example, the red circles disappear.  They are still in the scene,
 * because the cursor changes and they are visible while dragging.
 */
public class TestBufferingCanvasChart extends JFrame {
    
    // bug occurs when BUFFERED_CANVAS == true && BUFFERED_CHART == false
    private static final boolean BUFFERED_CANVAS = true;
    private static final boolean BUFFERED_CHART = false;
    
    private static final Dimension CANVAS_SIZE = new Dimension( 800, 600 );
    private static final Dimension CHART_SIZE = new Dimension( 200, 100 );
    private static final int CIRCLE_DIAMETER = 40;
    
    private PCanvas canvas;
    private Point2D nextWidgetLocation;

    public TestBufferingCanvasChart() {
        super( "Test Buffering" );
        
        // play area
        canvas = ( BUFFERED_CANVAS ? new BufferedPhetPCanvas() : new PhetPCanvas() );
        canvas.setPreferredSize( CANVAS_SIZE );
        
        // control panel
        JButton addButton = new JButton( "Add Widget" );
        addButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addWidget();
            }
        } );
        JPanel controlPanel = new JPanel();
        controlPanel.add( addButton );
        
        // layout
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.EAST );
        getContentPane().add( panel );
        pack();
        
        // add 1 detector
        nextWidgetLocation = new Point2D.Double( 50, 50 );
        addWidget();
    }

    private void addWidget() {
        PNode intensityReader = new MyWidgetNode();
        intensityReader.setOffset( nextWidgetLocation.getX(), nextWidgetLocation.getY() );
        canvas.getLayer().addChild( intensityReader );
        nextWidgetLocation.setLocation( nextWidgetLocation.getX() + 50, nextWidgetLocation.getY() + 50 );
    }
    
    private static class MyWidgetNode extends PhetPNode {
        public MyWidgetNode() {
            // chart
            PNode chartNode = new MyChartNode();
            addChild( chartNode );
            chartNode.addInputEventListener( new PDragEventHandler() );
            chartNode.addInputEventListener( new CursorHandler() );
            // circle
            PNode circleNode = new MyCircleNode();
            addChild( circleNode );
            circleNode.addInputEventListener( new PDragEventHandler() );
            circleNode.addInputEventListener( new CursorHandler() );
            // layout: circle to left of chart, vertically aligned
            circleNode.setOffset( chartNode.getFullBoundsReference().getMaxX() + 10, 
                    ( chartNode.getFullBoundsReference().getHeight() - circleNode.getFullBoundsReference().getHeight() ) / 2 );
        }
    }
    
    private static class MyChartNode extends PNode {
        public MyChartNode() {
            super();
            JFreeChart chart = ChartFactory.createXYLineChart( "title", "x", "y", new XYSeriesCollection(), PlotOrientation.VERTICAL, false /* legend */, false /* tooltips */, false /* urls */);
            chart.setBorderVisible( true );
            JFreeChartNode chartNode = new JFreeChartNode( chart, BUFFERED_CHART );
            addChild( chartNode );
            chartNode.setBounds( 0, 0, CHART_SIZE.width, CHART_SIZE.height );
        }
    }
    
    private static class MyCircleNode extends PPath {
        public MyCircleNode() {
            super();
            setPathTo( new Ellipse2D.Double( 0, 0, CIRCLE_DIAMETER, CIRCLE_DIAMETER ) );
            setPaint( Color.RED );
        }
    }

    public static void main( String[] args ) {
        PDebug.debugRegionManagement = true;
        JFrame frame = new TestBufferingCanvasChart();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
