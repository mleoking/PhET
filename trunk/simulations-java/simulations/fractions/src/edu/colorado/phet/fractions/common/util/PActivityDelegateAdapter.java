// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.util;

import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

/**
 * adapter class that provides no-op defaults for start,step and finish
 */
public class PActivityDelegateAdapter implements PActivityDelegate {

    public void activityStarted( PActivity activity ) {
        // Override to change, does nothing by default.
    }

    public void activityStepped( PActivity activity ) {
        // Override to change, does nothing by default.
    }

    public void activityFinished( PActivity activity ) {
        // Override to change, does nothing by default.
    }
}
