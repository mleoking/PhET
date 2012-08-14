// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 * Makes it so a node cannot be picked while animating, that causes glitchy looking problems and makes nodes end up in the wrong place.
 *
 * @author Sam Reid
 */
public class DisablePickingWhileAnimating implements PActivityDelegate {
    private final PNode node;
    private final boolean pickableAfterActivityFinished;

    public DisablePickingWhileAnimating( final PNode node, final boolean pickableAfterActivityFinished ) {
        this.node = node;
        this.pickableAfterActivityFinished = pickableAfterActivityFinished;
    }

    public void activityStarted( final PActivity activity ) {
        node.setPickable( false );
        node.setChildrenPickable( false );
    }

    public void activityStepped( final PActivity activity ) {
        node.setPickable( false );
        node.setChildrenPickable( false );
    }

    public void activityFinished( final PActivity activity ) {
        node.setPickable( pickableAfterActivityFinished );
        node.setChildrenPickable( pickableAfterActivityFinished );
    }
}