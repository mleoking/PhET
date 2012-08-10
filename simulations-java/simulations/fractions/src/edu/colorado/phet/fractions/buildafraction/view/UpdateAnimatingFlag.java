// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 * Makes it so a node cannot be disabled while picking.
 *
 * @author Sam Reid
 */
public class UpdateAnimatingFlag implements PActivityDelegate {

    private final BooleanProperty animating;

    public UpdateAnimatingFlag( final BooleanProperty animating ) {
        this.animating = animating;
    }

    @Override public void activityStarted( final PActivity activity ) {
        animating.set( true );
    }

    @Override public void activityStepped( final PActivity activity ) {
    }

    @Override public void activityFinished( final PActivity activity ) {
        animating.set( false );
    }
}