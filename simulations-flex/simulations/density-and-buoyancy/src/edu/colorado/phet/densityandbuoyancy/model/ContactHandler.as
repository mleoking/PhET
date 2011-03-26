//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2ContactListener;

/**
 * This Box2D listener keeps track of contact points between DensityObjects.
 */
public class ContactHandler extends b2ContactListener {

    public function ContactHandler() {
        super();
    }

    override public function Result( point: b2ContactResult ): void {
        if ( point.shape1.GetBody().GetUserData() is DensityObject ) {
            point.shape1.GetBody().GetUserData().registerContact( point );
        }
        if ( point.shape2.GetBody().GetUserData() is DensityObject ) {
            point.shape2.GetBody().GetUserData().registerContact( point );
        }
    }
}
}