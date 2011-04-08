// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.activities;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * A Piccolo activity that toggles a nodes visibility, making the node appear to "flash".
 * When the activity is finished, the node's visibility is returned to the state it was
 * in when the activity started.
 * <p/>
 * Example usage:
 * <code>
 * PNode node = ... // make sure this node is added to the scene graph before adding the activity!
 * PActivity activity = new ToggleVisibilityActivity( node, 5000, 500 ); // flash every half second for 5 seconds
 * node.getRoot().addActivity( activity ); // Must schedule the activity with the root for it to run.
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToggleVisibilityActivity extends PActivity {

    private final PNode node;
    private boolean wasVisible;

    /**
     * Constructor.
     *
     * @param node     the node whose visibility will be toggled
     * @param duration duration of this activity, in milliseconds
     * @param stepRate amount of time that this activity should delay between steps, in milliseconds
     */
    public ToggleVisibilityActivity( PNode node, long duration, long stepRate ) {
        super( duration );
        this.node = node;
        setStepRate( stepRate );
    }

    @Override
    protected void activityStarted() {
        super.activityStarted();
        wasVisible = node.getVisible();
    }

    @Override
    protected void activityStep( long time ) {
        super.activityStep( time );
        node.setVisible( !node.getVisible() );
    }

    @Override
    protected void activityFinished() {
        super.activityFinished();
        node.setVisible( wasVisible );
    }

}
