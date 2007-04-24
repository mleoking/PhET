package edu.colorado.phet.rotation.tests.combined;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.XYPlotFactory;
import edu.colorado.phet.rotation.graphs.combined.AbstractChartSlider;
import edu.colorado.phet.rotation.graphs.combined.CombinedChartSlider;
import edu.colorado.phet.rotation.graphs.combined.CombinedControlGraph;
import edu.colorado.phet.rotation.model.*;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestCombinedModelPlot {
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
    private CombinedControlGraph combinedControlGraph;
    private PhetPCanvas phetPCanvas;
    private XYPlot xGraph;
    private XYPlot vGraph;
    private XYPlot aGraph;
    private CombinedChartSlider vChartSlider;
    private CombinedChartSlider aChartSlider;

    public TestCombinedModelPlot() {
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
        combinedControlGraph = new CombinedControlGraph( new XYPlot[]{xGraph, vGraph, aGraph,} );
        xChartSlider = new CombinedChartSlider( combinedControlGraph.getChartNode(), new PText( "HELLO" ), 0 );
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
        combinedControlGraph.addControl( xChartSlider );

        vChartSlider = new CombinedChartSlider( combinedControlGraph.getChartNode(), new PText( "Velocity" ), 1 );
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
        combinedControlGraph.addControl( vChartSlider );

        aChartSlider = new CombinedChartSlider( combinedControlGraph.getChartNode(), new PText( "Acceleration" ), 2 );
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
        combinedControlGraph.addControl( aChartSlider );

        combinedControlGraph.setBounds( 0, 0, 700, 500 );

        phetPCanvas.addScreenChild( combinedControlGraph );
        combinedControlGraph.setOffset( 100, 0 );

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
        new TestCombinedModelPlot().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
