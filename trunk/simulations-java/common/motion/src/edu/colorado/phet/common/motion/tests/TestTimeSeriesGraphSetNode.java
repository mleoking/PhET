package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestTimeSeriesGraphSetNode {
    private JFrame frame;
    private TimeSeriesGraphSetNode timeSeriesGraphSetNode;
    private PhetPCanvas pSwingCanvas;
    private ConstantDtClock clock;

    public TestTimeSeriesGraphSetNode() {
        frame = new JFrame();
        frame.setSize( 1024, 768 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        pSwingCanvas = new PhetPCanvas();
        frame.setContentPane( pSwingCanvas );
        clock = new ConstantDtClock( 30, 1 );
        final TestMotionModel testMotionModel = new TestMotionModel( clock );
        timeSeriesGraphSetNode = new TimeSeriesGraphSetNode( new GraphSetModel( new TestGraphSet( pSwingCanvas, testMotionModel ).getGraphSuite( 0 ) ), new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), clock ) );
        pSwingCanvas.getLayer().addChild( timeSeriesGraphSetNode );
        pSwingCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );

        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                testMotionModel.stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    class TestGraphSet extends GraphSuiteSet {
        private MinimizableControlGraph positionGraph;

        public TestGraphSet( PhetPCanvas pSwingCanvas, final TestMotionModel motionModel ) {
            positionGraph = new MinimizableControlGraph( "x", new MotionControlGraph( pSwingCanvas, motionModel.getXVariable(), motionModel.getXTimeSeries(), "X", "Position", -Math.PI * 3, Math.PI * 3, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ), motionModel, true, motionModel.getTimeSeriesModel(), motionModel ) );
            addGraphSuite( new GraphSuite( new MinimizableControlGraph[]{positionGraph} ) );

//            motionModel.addListener( new MotionModel.Listener() {
//                public void steppedInTime() {
//                    positionGraph.addValue( motionModel.getTime(), motionModel.getPosition() );
//                }
//            } );
            positionGraph.addControlGraphListener( new ControlGraph.Adapter() {
                public void controlFocusGrabbed() {
                    motionModel.setPositionDriven();
                }
            } );
        }
    }

    class TestMotionModel extends SingleBodyMotionModel {
        public TestMotionModel( ConstantDtClock clock ) {
            super( clock );
        }
    }

    private void relayout() {
        timeSeriesGraphSetNode.setBounds( 0, 0, pSwingCanvas.getWidth(), pSwingCanvas.getHeight() );
    }

    public static void main( String[] args ) {
        new TestTimeSeriesGraphSetNode().start();
    }

    private void start() {
        frame.setVisible( true );
        clock.start();
    }
}
