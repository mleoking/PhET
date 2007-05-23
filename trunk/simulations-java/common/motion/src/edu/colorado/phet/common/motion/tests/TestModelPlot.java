package edu.colorado.phet.common.motion.tests;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 1:11:33 AM
 *
 */

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.model.MotionModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestModelPlot {
    private JFrame frame;
    private Timer timer;
    private PhetPCanvas phetPCanvas;

    private MotionModel rotationModel;

    private ControlGraph xGraph;
    private ControlGraph vGraph;
    private ControlGraph aGraph;

    public TestModelPlot() {
        new PhetLookAndFeel().initLookAndFeel();
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        rotationModel = new MotionModel();
        phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setBackground( new Color( 200, 240, 200 ) );

        xGraph = new ControlGraph( phetPCanvas, rotationModel.getXVariable(), "theta", "Angle", -10, 10, Color.blue, PImageFactory.create( "images/blue-arrow.png" ) );
        xGraph.addListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                rotationModel.setPositionDriven();
            }
        } );

        vGraph = new ControlGraph( phetPCanvas, rotationModel.getVVariable(), "omega", "Angular Velocity", -5, 5, Color.red, PImageFactory.create( "images/red-arrow.png" ) );
        vGraph.addListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                rotationModel.setVelocityDriven();
            }
        } );

        aGraph = new ControlGraph( phetPCanvas, rotationModel.getAVariable(), "alpha", "Angular Acceleration", -1, 1, Color.green, PImageFactory.create( "images/green-arrow.png" ) );
        aGraph.addListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                rotationModel.setAccelerationDriven();
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
        rotationModel.stepInTime( 1.0 );

        xGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAngle() );
        vGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getVelocity() );
        aGraph.addValue( rotationModel.getLastState().getTime(), rotationModel.getLastState().getAcceleration() );
    }

    public static void main( String[] args ) {
        new TestModelPlot().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
