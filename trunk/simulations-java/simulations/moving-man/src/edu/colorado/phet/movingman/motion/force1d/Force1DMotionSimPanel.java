package edu.colorado.phet.movingman.motion.force1d;

import java.io.IOException;

import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSetNode;
import edu.colorado.phet.common.motion.graphs.GraphSuite;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.movingman.motion.AbstractMotionSimPanel;
import edu.colorado.phet.movingman.motion.movingman.MovingManGraph;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModel;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:29:00 PM
 */
public class Force1DMotionSimPanel extends AbstractMotionSimPanel {
    private Force1DPlayAreaNode movingManNode;
    private GraphSetNode graphSetNode;

    public Force1DMotionSimPanel( ForceModel forceModel ) {
        try {
            movingManNode = new Force1DPlayAreaNode( forceModel );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        addScreenChild( movingManNode );

        MovingManGraph xGraph = getXGraph( forceModel );
        MovingManGraph vGraph = getVGraph( forceModel );
        MovingManGraph aGraph = getAGraph( forceModel );

        MovingManGraph forceGraph = new MovingManGraph( this, forceModel.getAppliedForceSeries(), "f", "f", -1000, 1000,
                                                        forceModel, true, forceModel.getTimeSeriesModel(), forceModel, MovingManMotionModel.MAX_T, forceModel, 200 );

        graphSetNode = new GraphSetNode( new GraphSetModel( new GraphSuite( new MinimizableControlGraph[]{
                new MinimizableControlGraph( "Forces", forceGraph ),
                new MinimizableControlGraph( SimStrings.get( "variables.position.abbreviation" ), xGraph, true ),
                new MinimizableControlGraph( SimStrings.get( "variables.velocity.abbreviation" ), vGraph, true ),
                new MinimizableControlGraph( SimStrings.get( "variables.acceleration.abbreviation" ), aGraph, true )
        } ) ) );
        addScreenChild( graphSetNode );

        graphSetNode.setAlignedLayout();
        updateLayout();
    }

    protected void updateLayout() {
        super.updateLayout();
        movingManNode.setTransform( 22.0, getWidth() );

        int insetX = 2;
        graphSetNode.setBounds( insetX, movingManNode.getFullBounds().getMaxY(), getWidth() - 2 * insetX, getHeight() - movingManNode.getFullBounds().getMaxY() );
    }
}
