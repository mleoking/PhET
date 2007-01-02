package edu.colorado.phet.rotation.tests;

import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.*;
import edu.colorado.phet.rotation.model.*;
import edu.colorado.phet.rotation.util.BufferedPhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestCombinedModelPlot2 {
    private JFrame frame;
    private Timer timer;
    private RotationModel rotationModel;

    private SimulationVariable xVariable;
    private SimulationVariable vVariable;
    private SimulationVariable aVariable;

    private PositionDriven positionDriven;
    private VelocityDriven velocityDriven;
    private AccelerationDriven accelDriven;

    private CombinedControlGraph combinedControlGraph;
    private PhetPCanvas phetPCanvas;
    private XYPlot xGraph;
    private XYPlot vGraph;
    private XYPlot aGraph;

    public TestCombinedModelPlot2() {

        new PhetLookAndFeel().initLookAndFeel();

        frame = new JFrame();
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        rotationModel = new RotationModel();
        phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setBackground( new Color( 200, 240, 200 ) );
        xVariable = new SimulationVariable( rotationModel.getLastState().getAngle() );
        vVariable = new SimulationVariable( rotationModel.getLastState().getAngularVelocity() );
        aVariable = new SimulationVariable( rotationModel.getLastState().getAngularAcceleration() );

        positionDriven = new PositionDriven( xVariable.getValue() );
        velocityDriven = new VelocityDriven( vVariable.getValue() );
        accelDriven = new AccelerationDriven( aVariable.getValue() );

        XYPlotFactory factory = new XYPlotFactory();
        xGraph = factory.createXYPlot( "position", "meters", 10 );
        vGraph = factory.createXYPlot( "vel", "meters/sec", 2 );
        aGraph = factory.createXYPlot( "acc", "meters/sec/sec", 0.25 );
        combinedControlGraph = new CombinedControlGraph( phetPCanvas, new XYPlot[]{xGraph, vGraph, aGraph,} );
        rotationModel.setUpdateStrategy( positionDriven );
        DefaultGraphTimeSeries graphTimeSeries = new DefaultGraphTimeSeries();
        addDefaultControlSet( 0, "position", "a units", "A_abbr", xVariable, graphTimeSeries, phetPCanvas, combinedControlGraph, positionDriven );
        addDefaultControlSet( 1, "velocity", "b units", "b_abbr", vVariable, graphTimeSeries, phetPCanvas, combinedControlGraph, velocityDriven );
        addDefaultControlSet( 2, "acceleration", "c units", "c_abbr", aVariable, graphTimeSeries, phetPCanvas, combinedControlGraph, accelDriven );

        combinedControlGraph.setBounds( 0, 0, 400, 500 );
        phetPCanvas.addScreenChild( combinedControlGraph );
        combinedControlGraph.setOffset( 200, 0 );

        frame.setContentPane( phetPCanvas );
        timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                step();
            }
        } );
        phetPCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        relayout();
    }

    private void relayout() {
        combinedControlGraph.setBounds( 0, 0, phetPCanvas.getWidth(), phetPCanvas.getHeight() );
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
        new TestCombinedModelPlot2().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }

    public void addDefaultControlSet( int subplotIndex, String title, String units, String abbreviation, final SimulationVariable simulationVariable, GraphTimeSeries graphTimeSeries, PSwingCanvas pSwingCanvas, CombinedControlGraph combinedControlGraph, final UpdateStrategy updateStrategy ) {
        PNode sliderThumb = new PText( title );

        GraphControlNode graphControlNode = new GraphControlNode( pSwingCanvas, simulationVariable, graphTimeSeries );
        ZoomSuiteNode zoomSuiteNode = new ZoomSuiteNode();
        final CombinedChartSlider combinedChartSlider = new CombinedChartSlider( combinedControlGraph.getChartNode(), sliderThumb, subplotIndex );
        combinedChartSlider.addListener( new CombinedChartSlider.Listener() {
            public void valueChanged() {
                simulationVariable.setValue( combinedChartSlider.getValue() );
            }
        } );
        simulationVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                combinedChartSlider.setValue( simulationVariable.getValue() );
            }
        } );

        PNode leftControl = new PNode();
        leftControl.addChild( graphControlNode );
        leftControl.addChild( combinedChartSlider );
        combinedChartSlider.setOffset( graphControlNode.getFullBounds().getWidth(), 0 );

        combinedChartSlider.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                rotationModel.setUpdateStrategy( updateStrategy );
            }
        } );

        CombinedControlGraph.ControlSet controlSet = new CombinedControlGraph.ControlSet( subplotIndex, graphControlNode, zoomSuiteNode );
        combinedControlGraph.addControlSet( controlSet, combinedChartSlider );
    }
}
