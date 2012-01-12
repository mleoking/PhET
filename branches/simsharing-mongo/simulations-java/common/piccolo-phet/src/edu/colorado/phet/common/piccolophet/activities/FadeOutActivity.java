// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.activities;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * Gradually fades out a node by reducing its transparency to zero.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FadeOutActivity extends PActivity {

    private final PNode node;
    private final float deltaTransparency;

    /**
     * Constructor.
     *
     * @param node     the node whose visibility will be toggled
     * @param duration duration of this activity, in milliseconds
     * @param stepRate amount of time that this activity should delay between steps, in milliseconds
     */
    public FadeOutActivity( PNode node, long duration, long stepRate ) {
        super( duration );
        this.node = node;
        setStepRate( stepRate );
        deltaTransparency = node.getTransparency() / ( duration / stepRate );
    }

    @Override
    protected void activityStep( long time ) {
        super.activityStep( time );
        float transparency = Math.max( 0, node.getTransparency() - deltaTransparency );
        node.setTransparency( transparency );
    }

    @Override
    protected void activityFinished() {
        super.activityFinished();
        node.setTransparency( 0 );
    }
}