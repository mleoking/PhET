// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.activities;

import edu.umd.cs.piccolo.activities.PActivity;

/**
 * Adapter pattern for PActivityDelegate.
 */
class PActivityDelegateAdapter implements PActivity.PActivityDelegate {

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
