//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Collision.b2ContactPoint;
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2ContactListener;

public class ContactHandler extends b2ContactListener {

    public function ContactHandler() {
        super();
    }

    override public virtual function Add( point: b2ContactPoint ): void {
        super.Add( point );
    }

    override public virtual function Persist( point: b2ContactPoint ): void {
        super.Persist( point );
    }

    override public virtual function Remove( point: b2ContactPoint ): void {
        super.Remove( point );
    }

    override public virtual function Result( point: b2ContactResult ): void {
        if ( point.shape1.GetBody().GetUserData() is DensityObject ) {
            point.shape1.GetBody().GetUserData().registerContact( point );
        }
        if ( point.shape2.GetBody().GetUserData() is DensityObject ) {
            point.shape2.GetBody().GetUserData().registerContact( point );
        }
    }
}
}