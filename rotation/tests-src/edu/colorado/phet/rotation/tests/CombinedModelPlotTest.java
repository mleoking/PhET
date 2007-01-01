package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.AbstractChartSlider;
import edu.colorado.phet.rotation.graphs.CombinedChartSlider;
import edu.colorado.phet.rotation.graphs.CombinedGraph;
import edu.colorado.phet.rotation.graphs.XYPlotFactory;
import edu.colorado.phet.rotation.model.*;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CombinedModelPlotTest {
    private JFrame frame;
    private Timer timer;
    private RotationModel rotationModel;

    private SimulationVariable xVariable;
    private SimulationVariable vVariable;
    private SimulationVariable aVariable;

    private PositionDriven positionDriven;
    private VelocityDriven velocityDriven;
    private AccelerationDriven accelDriven;
    private CombinedChartSlider xChartSlider;
    private CombinedGraph combinedGraph;
    private PhetPCanvas phetPCanvas;
    private XYPlot xGraph;
    private XYPlot vGraph;
    private XYPlot aGraph;
    private CombinedChartSlider vChartSlider;
    private CombinedChartSlider aChartSlider;

    public CombinedModelPlotTest() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        rotationModel = new RotationModel();
        phetPCanvas = new PhetPCanvas();
        xVariable = new SimulationVariable( rotationModel.getLastState().getAngle() );
        vVariable = new SimulationVariable( rotationModel.getLastState().getAngularVelocity() );
        aVariable = new SimulationVariable( rotationModel.getLastState().getAngularAcceleration() );

        positionDriven = new PositionDriven( xVariable.getValue() );
        velocityDriven = new VelocityDriven( vVariable.getValue() );
        accelDriven = new AccelerationDriven( aVariable.getValue() );

        XYPlotFactory factory = new XYPlotFactory();
        xGraph = factory.createXYPlot( "position", "meters" );
        vGraph = factory.createXYPlot( "vel", "meters/sec" );
        aGraph = factory.createXYPlot( "acc", "meters/sec/sec" );
        combinedGraph = new CombinedGraph( new XYPlot[]{
                xGraph,
                vGraph,
                aGraph,
        } );
        combinedGraph.getChartNode().updateChartRenderingInfo();
        xChartSlider = new CombinedChartSlider( combinedGraph.getChartNode(), new PText( "HELLO" ), 0 );
        xChartSlider.addListener( new AbstractChartSlider.Listener() {
            public void valueChanged() {
                xVariable.setValue( xChartSlider.getValue() );
            }
        } );
        xChartSlider.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                rotationModel.setUpdateStrategy( positionDriven );
            }
        } );
        xVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                xChartSlider.setValue( xVariable.getValue() );
            }
        } );
        combinedGraph.addControl( xChartSlider );

        vChartSlider = new CombinedChartSlider( combinedGraph.getChartNode(), new PText( "Velocity" ), 1 );
        vChartSlider.addListener( new AbstractChartSlider.Listener() {
            public void valueChanged() {
                vVariable.setValue( vChartSlider.getValue() );
            }
        } );
        vChartSlider.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                rotationModel.setUpdateStrategy( velocityDriven );
            }
        } );
        vVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                vChartSlider.setValue( vVariable.getValue() );
            }
        } );
        combinedGraph.addControl( vChartSlider );

        aChartSlider = new CombinedChartSlider( combinedGraph.getChartNode(), new PText( "Acceleration" ), 2 );
        aChartSlider.addListener( new AbstractChartSlider.Listener() {
            public void valueChanged() {
                aVariable.setValue( aChartSlider.getValue() );
            }
        } );
        aChartSlider.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                rotationModel.setUpdateStrategy( accelDriven );
            }
        } );
        aVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                aChartSlider.setValue( aVariable.getValue() );
            }
        } );
        combinedGraph.addControl( aChartSlider );

        combinedGraph.setBounds( 0, 0, 700, 500 );

        phetPCanvas.addScreenChild( combinedGraph );
        combinedGraph.setOffset( 100, 0 );

        frame.setContentPane( phetPCanvas );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }
        } );
    }

    private void step() {
        positionDriven.setPosition( xVariable.getValue() );
        velocityDriven.setVelocity( vVariable.getValue() );
        accelDriven.setAcceleration( aVariable.getValue() );

        rotationModel.stepInTime( 1.0 );
        xVariable.setValue( rotationModel.getLastState().getAngle() );
        vVariable.setValue( rotationModel.getLastState().getAngularVelocity() );
        aVariable.setValue( rotationModel.getLastState().getAngularAcceleration() );

        ( (XYSeriesCollection)xGraph.getDataset() ).getSeries( 0 ).add( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngle() );
        ( (XYSeriesCollection)vGraph.getDataset() ).getSeries( 0 ).add( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngularVelocity() );
        ( (XYSeriesCollection)aGraph.getDataset() ).getSeries( 0 ).add( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngularAcceleration() );
    }

    public static void main( String[] args ) {
        new CombinedModelPlotTest().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
