// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 * Makes it so a node cannot be disabled while picking.  Note that if the same property is wired up to different activities,
 * they can overwrite the same value and yield a result that is difficult to interpret/use properly.
 *
 * @author Sam Reid
 */
public class UpdateAnimatingFlag implements PActivityDelegate {

    private final BooleanProperty animating;

    public UpdateAnimatingFlag( final BooleanProperty animating ) { this.animating = animating; }

    public void activityStarted( final PActivity activity ) { animating.set( true ); }

    public void activityStepped( final PActivity activity ) { }

    public void activityFinished( final PActivity activity ) { animating.set( false ); }
}