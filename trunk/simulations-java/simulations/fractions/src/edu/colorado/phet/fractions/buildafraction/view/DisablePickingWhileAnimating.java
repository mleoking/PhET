// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 * Makes it so a node cannot be disabled while picking.
 *
 * @author Sam Reid
 */
public class DisablePickingWhileAnimating implements PActivityDelegate {
    private final PNode node;
    private boolean pickable;
    private boolean childrenPickable;

    public DisablePickingWhileAnimating( final PNode node ) {
        this.node = node;
        pickable = node.getPickable();
        childrenPickable = node.getChildrenPickable();
    }

    @Override public void activityStarted( final PActivity activity ) {
        node.setPickable( false );
        node.setChildrenPickable( false );
    }

    @Override public void activityStepped( final PActivity activity ) {
    }

    @Override public void activityFinished( final PActivity activity ) {
        node.setPickable( pickable );
        node.setChildrenPickable( childrenPickable );
    }
}