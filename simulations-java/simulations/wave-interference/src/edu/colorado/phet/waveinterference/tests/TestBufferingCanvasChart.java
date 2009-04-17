
package edu.colorado.phet.waveinterference.tests;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.JButton;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetTestApplication;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDebug;

// for debugging #1404
public class TestBufferingCanvasChart extends Module {
    
    // bug occurs when BUFFERED_CANVAS == true && BUFFERED_CHART == false
    private static final boolean BUFFERED_CANVAS = true;
    private static final boolean BUFFERED_CHART = false;
    
    private PhetPCanvas canvas;
    private Point2D nextDetectorLocation;

    public TestBufferingCanvasChart() {
        super( "Test Buffering", new SwingClock( 30, 1 ) );
        
        // play area
        canvas = ( BUFFERED_CANVAS ? new BufferedPhetPCanvas() : new PhetPCanvas() );
        
        setSimulationPanel( canvas );
        
        // control panel
        setControlPanel( new ControlPanel() );
        JButton addDetector = new JButton( "Add Detector" );
        addDetector.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addDetector();
            }
        } );
        getControlPanel().addControl( addDetector );
        
        // add 1 detector
        nextDetectorLocation = new Point2D.Double( 50, 50 );
        addDetector();
    }

    private void addDetector() {
        PNode intensityReader = new MyInteractiveNode(); //DoNothingIntensityReader( getClock() );
        intensityReader.setOffset( nextDetectorLocation.getX(), nextDetectorLocation.getY() );
        canvas.addScreenChild( intensityReader );
        nextDetectorLocation.setLocation( nextDetectorLocation.getX() + 50, nextDetectorLocation.getY() + 50 );
    }
    
    private static class MyInteractiveNode extends PhetPNode {
        public MyInteractiveNode() {
            PNode chartNode = new MyChartNode();
            chartNode.addInputEventListener( new PDragEventHandler() );
            addChild( chartNode );
            PNode shapeNode = new MyShapeNode();
            addChild( shapeNode );
            shapeNode.addInputEventListener( new PDragEventHandler() );
            // shape to left of chart, vertically aligned
            shapeNode.setOffset( chartNode.getFullBoundsReference().getMaxX() + 10, 
                    ( chartNode.getFullBoundsReference().getHeight() - shapeNode.getFullBoundsReference().getHeight() ) / 2 );
        }
    }
    
    private static class MyChartNode extends PNode {
        public MyChartNode() {
            super();
            JFreeChart chart = ChartFactory.createScatterPlot( "title", "x", "y", new XYSeriesCollection(), PlotOrientation.VERTICAL, 
                    false /* legend */, false /* tooltips */, false /* urls */);
            chart.setBorderVisible( true );
            JFreeChartNode chartNode = new JFreeChartNode( chart, BUFFERED_CHART );
            addChild( chartNode );
            chartNode.setBounds( 0, 0, 200, 100 );
        }
    }
    
    private static class MyShapeNode extends PPath {
        public MyShapeNode() {
            super();
            setPaint( Color.RED );
            setPathTo( new Ellipse2D.Double( 0, 0, 40, 40 ) );
        }
    }

    public static void main( String[] args ) {
        PDebug.debugRegionManagement = true;
        PhetTestApplication phetApplication = new PhetTestApplication( args );
        phetApplication.addModule( new TestBufferingCanvasChart() );
        phetApplication.startApplication();
    }
}
