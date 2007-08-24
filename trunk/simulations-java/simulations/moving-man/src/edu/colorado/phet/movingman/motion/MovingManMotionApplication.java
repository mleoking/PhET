package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;
import edu.colorado.phet.movingman.MovingManApplication;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:38:34 AM
 */
public class MovingManMotionApplication {
    private JFrame frame;
    private ConstantDtClock clock;

    public MovingManMotionApplication() {
        frame = new JFrame( "Moving Man Motion Application" );
        frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height - 400 );
        PhetPCanvas phetPCanvas = new BufferedPhetPCanvas();
        phetPCanvas.setZoomEventHandler( new PZoomEventHandler() );
        JPanel contentPane = new JPanel( new BorderLayout() );
        contentPane.add( phetPCanvas, BorderLayout.CENTER );
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        clock = new ConstantDtClock( 30, 1.0 );
        final SingleBodyMotionModel motionModel = new SingleBodyMotionModel( clock );

        System.out.println( "motionModel.getTimeSeriesModel().getMode() = " + motionModel.getTimeSeriesModel().getMode() + " ispaused=" + motionModel.getTimeSeriesModel().isPaused() );

        MovingManNode movingManNode = new MovingManNode( motionModel );
        movingManNode.scale( 50 );
        movingManNode.translate( 10.5, 0 );
        phetPCanvas.addScreenChild( movingManNode );

        motionModel.setPositionDriven();

        int MAX_T = 500;
        motionModel.setMaxAllowedRecordTime( MAX_T );
        System.out.println( "motionModel.getTimeSeriesModel().getMode() = " + motionModel.getTimeSeriesModel().getMode() + " ispaused=" + motionModel.getTimeSeriesModel().isPaused() );
        ControlGraphSeries xSeries = new ControlGraphSeries( "X", Color.blue, "x", "m", new BasicStroke( 2 ), true, null, motionModel.getXVariable() );
        MovingManGraph xGraph = new MovingManGraph(
                phetPCanvas, xSeries, SimStrings.get( "variables.position.abbreviation" ), "x", -10, 10,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getPositionDriven(), MAX_T, motionModel );

        ControlGraphSeries vSeries = new ControlGraphSeries( "V", Color.red, "v", "m/s", new BasicStroke( 2 ), true, null, motionModel.getVVariable() );
        MovingManGraph vGraph = new MovingManGraph(
                phetPCanvas, vSeries, SimStrings.get( "variables.velocity.abbreviation" ), "x", -1, 1,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getVelocityDriven(), MAX_T, motionModel );

        ControlGraphSeries aSeries = new ControlGraphSeries( "A", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, motionModel.getAVariable() );
        MovingManGraph aGraph = new MovingManGraph(
                phetPCanvas, aSeries, SimStrings.get( "variables.position.abbreviation" ), "x", -0.01, 0.01,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getAccelDriven(), MAX_T, motionModel );

        GraphSetNode graphSetNode = new GraphSetNode( new GraphSetModel( new GraphSuite( new MinimizableControlGraph[]{
                new MinimizableControlGraph( SimStrings.get( "variables.position.abbreviation" ), xGraph ),
                new MinimizableControlGraph( SimStrings.get( "variables.velocity.abbreviation" ), vGraph ),
                new MinimizableControlGraph( SimStrings.get( "variables.acceleration.abbreviation" ), aGraph )
        } ) ) );

        graphSetNode.setAlignedLayout();
        graphSetNode.setBounds( 0, 0, 800, 600 );
        graphSetNode.setOffset( 0, 200 );
        phetPCanvas.addScreenChild( graphSetNode );
        phetPCanvas.requestFocus();
        phetPCanvas.addKeyListener( new PDebugKeyHandler() );

        TimeSeriesControlPanel timeControlPanel = new TimeSeriesControlPanel( motionModel.getTimeSeriesModel(), 0.1, 1.0 );
        contentPane.add( timeControlPanel, BorderLayout.SOUTH );
    }

    private void start() {
        frame.setVisible( true );
        clock.start();
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                SimStrings.getInstance().addStrings( MovingManApplication.localizedStringsPath );
                new MovingManMotionApplication().start();
            }
        } );
    }
}
