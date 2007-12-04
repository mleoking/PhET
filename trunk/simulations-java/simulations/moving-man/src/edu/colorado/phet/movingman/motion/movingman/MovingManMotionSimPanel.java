package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSetNode;
import edu.colorado.phet.common.motion.graphs.GraphSuite;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.PDebugKeyHandler;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:44:35 PM
 */
public class MovingManMotionSimPanel extends BufferedPhetPCanvas {
    private MovingManNode movingManNode;
    private GraphSetNode graphSetNode;

    public MovingManMotionSimPanel( MovingManMotionModule movingManMotionModule ) {
        final SingleBodyMotionModel motionModel = movingManMotionModule.getSingleBodyMotionModel();
        movingManNode = new MovingManNode( motionModel );
        addScreenChild( movingManNode );


        MovingManGraph xGraph = new MovingManGraph(
                this, movingManMotionModule.getXSeries(), SimStrings.get( "variables.position.abbreviation" ), "x", -10, 10,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getPositionDriven(), MovingManMotionModule.MAX_T, motionModel );


        MovingManGraph vGraph = new MovingManGraph(
                this, movingManMotionModule.getVSeries(), SimStrings.get( "variables.velocity.abbreviation" ), "x", -0.1, 0.1,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getVelocityDriven(), MovingManMotionModule.MAX_T, motionModel );


        MovingManGraph aGraph = new MovingManGraph(
                this, movingManMotionModule.getASeries(), SimStrings.get( "variables.position.abbreviation" ), "x", -0.01, 0.01,
                motionModel, true, motionModel.getTimeSeriesModel(), motionModel.getAccelDriven(), MovingManMotionModule.MAX_T, motionModel );

        graphSetNode = new GraphSetNode( new GraphSetModel( new GraphSuite( new MinimizableControlGraph[]{
                new MinimizableControlGraph( SimStrings.get( "variables.position.abbreviation" ), xGraph ),
                new MinimizableControlGraph( SimStrings.get( "variables.velocity.abbreviation" ), vGraph ),
                new MinimizableControlGraph( SimStrings.get( "variables.acceleration.abbreviation" ), aGraph )
        } ) ) );

        graphSetNode.setAlignedLayout();
        graphSetNode.setBounds( 0, 0, 800, 600 );
        graphSetNode.setOffset( 0, 200 );
        addScreenChild( graphSetNode );
        requestFocus();
        addKeyListener( new PDebugKeyHandler() );

//        addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                updateLayout();
//            }
//        } );

        TimeSeriesControlPanel timeControlPanel = new TimeSeriesControlPanel( motionModel.getTimeSeriesModel(), 0.1, 1.0 );
        add( timeControlPanel, BorderLayout.SOUTH );
        updateLayout();
    }

    protected void updateLayout() {
        super.updateLayout();
        movingManNode.setTransform( 21.0, getWidth() );

        int insetX = 2;
        graphSetNode.setBounds( insetX, movingManNode.getFullBounds().getMaxY(), getWidth() - 2 * insetX, getHeight() - movingManNode.getFullBounds().getMaxY() );
    }
}
