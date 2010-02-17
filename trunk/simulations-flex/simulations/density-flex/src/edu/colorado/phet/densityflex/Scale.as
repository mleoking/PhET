package edu.colorado.phet.densityflex {
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;


/**
 * This class represents the model object for a scale.
 */
public class Scale extends Cuboid {

    private var force : Number = 0;

    private static var SCALE_DENSITY : Number = 2.0;

    public function Scale( x:Number, y:Number, model : DensityModel ) : void {
        super(SCALE_DENSITY, 3, 1, 3, x, y, model);
    }

    public function getScaleReadout():String {
        // TODO: localize
        // scaled by DT-frame because we are measuring the 'normal impulses'
        var num : Number = (force / DensityModel.DT_FRAME);
        var numStr : String = String(Math.round(num * 100) / 100);
        var ret : String = String( numStr ).substr(0, 7) + " N";
        return ret;
    }


    override public function registerContact( point:b2ContactResult ):void {
        super.registerContact(point);

        // has at least some impulse and is pointing in the correct direction...
        // TODO: verify that point.normal.y wouldn't ever be switched!
        if ( point.normalImpulse > 0.000001 && point.normal.y < 0 ) {
            if ( point.shape1.GetBody().IsStatic() || point.shape2.GetBody().IsStatic() ) {
                return;
            }
            //DebugText.debug(String(point.normalImpulse) + " (" + String(point.normal.x) + ", " + String(point.normal.y) + ") " + String(point.shape1.GetBody()) + "-" + String(point.shape2.GetBody()) + String(point.shape1.GetBody().GetUserData()) + ", " + String(point.shape2.GetBody().GetUserData()));
            force += point.normalImpulse;
        }
    }

    override public function resetContacts():void {
        super.resetContacts();

        force = 0;
    }
}
}