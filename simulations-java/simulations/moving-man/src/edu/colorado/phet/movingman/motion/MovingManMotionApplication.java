package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:38:34 AM
 */
public class MovingManMotionApplication {
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                runApp();
            }
        } );
    }

    private static void runApp() {
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

        CursorModel cursorModel = new CursorModel( motionModel.getTimeSeriesModel() );

        GraphSetNode graphSetNode = new GraphSetNode( new GraphSetModel( new GraphSuite( new MinimizableControlGraph[]{
                new MinimizableControlGraph( "x", new MotionControlGraph( phetPCanvas, motionModel.getXVariable(), "x", "Position", -10, 10, Color.blue,
                                                                          new PImage( GraphSuiteSet.loadBlueArrow() ), motionModel, true, cursorModel, motionModel.getPositionDriven() ) ),
                new MinimizableControlGraph( "v", new MotionControlGraph( phetPCanvas, motionModel.getVVariable(), "v", "Velocity", -1, 1, Color.red,
                                                                          new PImage( GraphSuiteSet.loadRedArrow() ), motionModel, true, cursorModel, motionModel.getVelocityDriven() ) ),
                new MinimizableControlGraph( "a", new MotionControlGraph( phetPCanvas, motionModel.getAVariable(), "a", "Acceleration", -0.01, 0.01, Color.green,
                                                                          new PImage( GraphSuiteSet.loadGreenArrow() ), motionModel, true, cursorModel, motionModel.getAccelDriven() ) )
        } ) ) );
        graphSetNode.setAlignedLayout();
        graphSetNode.setBounds( 0, 0, 800, 600 );
        graphSetNode.setOffset( 0, 200 );
        phetPCanvas.addScreenChild( graphSetNode );
        phetPCanvas.requestFocus();
        phetPCanvas.addKeyListener( new PDebugKeyHandler() );
    }

}
