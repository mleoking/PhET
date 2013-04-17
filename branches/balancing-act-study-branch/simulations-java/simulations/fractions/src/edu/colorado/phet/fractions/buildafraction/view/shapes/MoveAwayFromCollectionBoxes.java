// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * Activity that moves a ContainerNode away from the collection boxes when it overlaps.
 *
 * @author Sam Reid
 */
class MoveAwayFromCollectionBoxes extends PActivityDelegateAdapter {
    private final ContainerNode containerNode;
    private final ShapeSceneNode shapeSceneNode;

    MoveAwayFromCollectionBoxes( final ShapeSceneNode shapeSceneNode, final ContainerNode containerNode ) {
        this.shapeSceneNode = shapeSceneNode;
        this.containerNode = containerNode;
    }

    public void activityFinished( final PActivity activity ) { shapeSceneNode.moveContainerNodeAwayFromCollectionBoxes( containerNode ); }
}