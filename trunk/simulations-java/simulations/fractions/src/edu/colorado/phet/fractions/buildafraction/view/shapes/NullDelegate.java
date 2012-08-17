// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 * @author Sam Reid
 */
class NullDelegate implements PActivityDelegate {
    public void activityStarted( final PActivity activity ) { }

    public void activityStepped( final PActivity activity ) { }

    public void activityFinished( final PActivity activity ) { }
}