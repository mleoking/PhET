// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 * Activity that moves a ContainerNode away from the collection boxes when it overlaps.
 *
 * @author Sam Reid
 */
class MoveAwayFromCollectionBoxes implements PActivityDelegate {
    private final ContainerNode containerNode;
    private final ShapeSceneNode shapeSceneNode;

    MoveAwayFromCollectionBoxes( final ShapeSceneNode shapeSceneNode, final ContainerNode containerNode ) {
        this.shapeSceneNode = shapeSceneNode;
        this.containerNode = containerNode;
    }

    @Override public void activityStarted( final PActivity activity ) { }

    @Override public void activityStepped( final PActivity activity ) { }

    @Override public void activityFinished( final PActivity activity ) { shapeSceneNode.moveContainerNodeAwayFromCollectionBoxes( containerNode ); }
}