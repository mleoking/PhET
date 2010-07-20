package edu.colorado.phet.densityflex.model {
import Box2D.Collision.b2ContactPoint;
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;
import Box2D.Dynamics.b2ContactListener;

public class ContactHandler extends b2ContactListener {

    public function ContactHandler() {
        super();
    }

    // TODO: add remove? get better data structure?

    override public virtual function Add(point:b2ContactPoint):void {
        super.Add(point);
    }

    override public virtual function Persist(point:b2ContactPoint):void {
        super.Persist(point);
    }

    override public virtual function Remove(point:b2ContactPoint):void {
        super.Remove(point);
    }

    override public virtual function Result(point:b2ContactResult):void {
        if (point.shape1.GetBody().GetUserData() is DensityObject) {
            point.shape1.GetBody().GetUserData().registerContact(point);
        }
        if (point.shape2.GetBody().GetUserData() is DensityObject) {
            point.shape2.GetBody().GetUserData().registerContact(point);
        }
        //        if ( point.normalImpulse > 0.000001 && point.normal.y < 0 ) {
        //            if ( point.shape1.GetBody().IsStatic() || point.shape2.GetBody().IsStatic() ) {
        //                return;
        //            }
        //            var scale : Block = null;
        //
        //            // TODO: handle scales on top of scales? not currently supported
        //            for each( var scaleBody : b2Body in scaleBodies ) {
        //                if ( scaleBody == point.shape1.GetBody() ) {
        //                    scale = point.shape1.GetBody().GetUserData();
        //                }
        //                if ( scaleBody == point.shape2.GetBody() ) {
        //                    scale = point.shape2.GetBody().GetUserData();
        //                }
        //            }
        //            if ( scale == null ) {
        //                return;
        //            }
        //            //DebugText.debug(String(point.normalImpulse) + " (" + String(point.normal.x) + ", " + String(point.normal.y) + ") " + String(point.shape1.GetBody()) + "-" + String(point.shape2.GetBody()) + String(point.shape1.GetBody().GetUserData()) + ", " + String(point.shape2.GetBody().GetUserData()));
        //            DebugText.debug(String(point.normalImpulse));
        //        }
    }
}
}