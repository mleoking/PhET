package edu.colorado.phet.rotation.tests;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 1:11:33 AM
 * Copyright (c) Dec 30, 2006 by Sam Reid
 */

import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.rotation.graphs.ControlGraph;
import edu.colorado.phet.rotation.model.*;
import edu.colorado.phet.rotation.util.BufferedPhetPCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ModelPlotTest {
    private JFrame frame;
    private Timer timer;
    private RotationModel rotationModel;
    private ControlGraph xGraph;
    private SimulationVariable xVariable;
    private SimulationVariable vVariable;
    private SimulationVariable aVariable;
    private ControlGraph vGraph;
    private ControlGraph aGraph;
    private PositionDriven positionDriven;
    private VelocityDriven velocityDriven;
    private AccelerationDriven accelDriven;
    private PhetPCanvas phetPCanvas;

    public ModelPlotTest() {
        new PhetLookAndFeel().initLookAndFeel();
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        rotationModel = new RotationModel();
        phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setBackground( new Color( 200, 240, 200 ) );
        xVariable = new SimulationVariable( rotationModel.getLastState().getAngle() );
        vVariable = new SimulationVariable( rotationModel.getLastState().getAngularVelocity() );
        aVariable = new SimulationVariable( rotationModel.getLastState().getAngularAcceleration() );
        xGraph = new ControlGraph( phetPCanvas, xVariable, "Angle", 10, Color.blue );
        positionDriven = new PositionDriven( xVariable.getValue() );
        velocityDriven = new VelocityDriven( vVariable.getValue() );
        accelDriven = new AccelerationDriven( aVariable.getValue() );
        xGraph.addListener( new ControlGraph.Listener() {
            public void mousePressed() {
                System.out.println( "ModelPlotTest.mousePressed: XGraph" );
                rotationModel.setUpdateStrategy( positionDriven );
            }

            public void valueChanged() {
            }
        } );
        vGraph = new ControlGraph( phetPCanvas, vVariable, "Angular Velocity", 5, Color.red );
        vGraph.addListener( new ControlGraph.Listener() {
            public void mousePressed() {
                System.out.println( "ModelPlotTest.mousePressed: VGraph" );
                rotationModel.setUpdateStrategy( velocityDriven );
            }

            public void valueChanged() {
            }
        } );
        aGraph = new ControlGraph( phetPCanvas, aVariable, "Angular Acceleration", 1, Color.green );
        aGraph.addListener( new ControlGraph.Listener() {
            public void mousePressed() {
                System.out.println( "ModelPlotTest.mousePressed: AGraph" );
                rotationModel.setUpdateStrategy( accelDriven );
            }

            public void valueChanged() {
            }
        } );
        vGraph.setOffset( 0, xGraph.getFullBounds().getMaxY() );
        aGraph.setOffset( 0, vGraph.getFullBounds().getMaxY() );

        phetPCanvas.addScreenChild( xGraph );
        phetPCanvas.addScreenChild( vGraph );
        phetPCanvas.addScreenChild( aGraph );

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
        xGraph.setBounds( 0, 0.0 * phetPCanvas.getHeight() / 3.0, phetPCanvas.getWidth(), phetPCanvas.getHeight() / 3.0 );
        vGraph.setBounds( 0, 1.0 * phetPCanvas.getHeight() / 3.0, phetPCanvas.getWidth(), phetPCanvas.getHeight() / 3.0 );
        aGraph.setBounds( 0, 2.0 * phetPCanvas.getHeight() / 3.0, phetPCanvas.getWidth(), phetPCanvas.getHeight() / 3.0 );
    }

    private void step() {
        positionDriven.setPosition( xVariable.getValue() );
        velocityDriven.setVelocity( vVariable.getValue() );
        accelDriven.setAcceleration( aVariable.getValue() );

        rotationModel.stepInTime( 1.0 );
        xVariable.setValue( rotationModel.getLastState().getAngle() );
        vVariable.setValue( rotationModel.getLastState().getAngularVelocity() );
        aVariable.setValue( rotationModel.getLastState().getAngularAcceleration() );

        xGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngle() );
        vGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngularVelocity() );
        aGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngularAcceleration() );
    }

    public static void main( String[] args ) {
        new ModelPlotTest().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
