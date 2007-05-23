package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:38:34 AM
 */
public class MovingManMotionApplication {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test Moving Man Node" );
        frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 400 );
        PhetPCanvas phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setZoomEventHandler( new PZoomEventHandler() );
        frame.setContentPane( phetPCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        final MotionModel motionModel = new MotionModel();

        MovingManNode movingManNode = new MovingManNode( motionModel );
        movingManNode.scale( 50 );
        movingManNode.translate( 10.5, 0 );
        phetPCanvas.addScreenChild( movingManNode );

        frame.setVisible( true );
        SwingClock swingClock = new SwingClock( 30, 1.0 );
        swingClock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                motionModel.stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
        swingClock.start();
        motionModel.setVelocityDriven();
        motionModel.setVelocity( 0.1 );

        final ControlGraph controlGraph = new ControlGraph( phetPCanvas, motionModel.getXVariable(), "x", "Position", -10, 10 );
        controlGraph.addListener( new ControlGraph.Adapter() {
            //This method is called when the user makes an input event that indicates
            //that this component should be "in control" of the simulation
            public void controlFocusGrabbed() {
                motionModel.setPositionDriven();
            }
        } );
        motionModel.addListener( new MotionModel.Listener() {
            public void steppedInTime() {
                controlGraph.addValue( motionModel.getTime(), motionModel.getXVariable().getValue() );
            }
        } );

        MinimizableControlGraph positionGraph = new MinimizableControlGraph( "X", controlGraph );
        GraphSetNode graphSetNode = new GraphSetNode( new GraphSetModel( new GraphSuite( new MinimizableControlGraph[]{positionGraph, createVelocityGraph( phetPCanvas, motionModel ), createAccelGraph( phetPCanvas, motionModel )} ) ) );
        graphSetNode.setAlignedLayout();
        graphSetNode.setBounds( 0, 0, 800, 600 );
        graphSetNode.setOffset( 0, 200 );
        phetPCanvas.addScreenChild( graphSetNode );
        phetPCanvas.requestFocus();
        phetPCanvas.addKeyListener( new PDebugKeyHandler() );
    }

    private static MinimizableControlGraph createAccelGraph( PhetPCanvas phetPCanvas, final MotionModel motionModel ) {
        final ControlGraph controlGraph = new ControlGraph( phetPCanvas, motionModel.getAVariable(), "a", "Acceleration", -0.01, 0.01 );
        controlGraph.addListener( new ControlGraph.Adapter() {
            //This method is called when the user makes an input event that indicates
            //that this component should be "in control" of the simulation
            public void controlFocusGrabbed() {
                motionModel.setAccelerationDriven();
            }
        } );
        motionModel.addListener( new MotionModel.Listener() {
            public void steppedInTime() {
                controlGraph.addValue( motionModel.getTime(), motionModel.getAVariable().getValue() );
            }
        } );
        return new MinimizableControlGraph( "A", controlGraph );
    }

    private static MinimizableControlGraph createVelocityGraph( PhetPCanvas phetPCanvas, final MotionModel motionModel ) {
        final ControlGraph controlGraph = new ControlGraph( phetPCanvas, motionModel.getVVariable(), "v", "Velocity", -2, 2 );
        controlGraph.addListener( new ControlGraph.Adapter() {
            //This method is called when the user makes an input event that indicates
            //that this component should be "in control" of the simulation
            public void controlFocusGrabbed() {
                motionModel.setVelocityDriven();
            }
        } );
        motionModel.addListener( new MotionModel.Listener() {
            public void steppedInTime() {
                controlGraph.addValue( motionModel.getTime(), motionModel.getVVariable().getValue() );
            }
        } );
        return new MinimizableControlGraph( "V", controlGraph );
    }
}
