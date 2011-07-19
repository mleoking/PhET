// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.tests;

/**
 * User: Sam Reid
 * Date: Dec 30, 2006
 * Time: 1:11:33 AM
 *
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

public class TestMotionGraphs {
    private JFrame frame;
    private Timer timer;
    private PhetPCanvas phetPCanvas;

    private SingleBodyMotionModel singleBodyMotionModel;

    private ControlGraph xGraph;
    private ControlGraph vGraph;
    private ControlGraph aGraph;

    public TestMotionGraphs() {
        new PhetLookAndFeel().initLookAndFeel();
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        singleBodyMotionModel = new SingleBodyMotionModel( new ConstantDtClock( 30, 1 ) );
        phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setBackground( new Color( 200, 240, 200 ) );

        xGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( singleBodyMotionModel.getXVariable() ), "Angle", -10, 10, singleBodyMotionModel.getTimeSeriesModel() );
        xGraph.addListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                singleBodyMotionModel.setPositionDriven();
            }
        } );

        vGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( singleBodyMotionModel.getVVariable() ), "Angular Velocity", -5, 5, singleBodyMotionModel.getTimeSeriesModel() );
        vGraph.addListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                singleBodyMotionModel.setVelocityDriven();
            }
        } );

        aGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( singleBodyMotionModel.getAVariable() ), "Angular Acceleration", -1, 1, singleBodyMotionModel.getTimeSeriesModel() );
        aGraph.addListener( new ControlGraph.Adapter() {
            public void controlFocusGrabbed() {
                singleBodyMotionModel.setAccelerationDriven();
            }
        } );

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
        singleBodyMotionModel.stepInTime( 1.0 );

        xGraph.addValue( singleBodyMotionModel.getLastPosition().getTime(), singleBodyMotionModel.getLastPosition().getValue() );
        vGraph.addValue( singleBodyMotionModel.getLastVelocity().getTime(), singleBodyMotionModel.getLastVelocity().getValue() );
        vGraph.addValue( singleBodyMotionModel.getLastAcceleration().getTime(), singleBodyMotionModel.getLastAcceleration().getValue() );
    }

    public static void main( String[] args ) {
        new TestMotionGraphs().start();
    }

    private void start() {
        frame.setVisible( true );
        timer.start();
    }
}
