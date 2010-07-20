package edu.colorado.phet.densityflex.model {
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;

import away3d.core.base.Object3D;

import edu.colorado.phet.densityflex.view.DensityObjectNode;
import edu.colorado.phet.densityflex.view.DensityView;
import edu.colorado.phet.densityflex.view.ScaleNode;

/**
 * This class represents the model object for a scale.
 */
public class Scale extends Cuboid {

    private var force:Number = 0;

    public static var SCALE_DENSITY:Number = 2.0;

    public static var SCALE_WIDTH:Number = 3.0;
    public static var SCALE_HEIGHT:Number = 1.0;
    public static var SCALE_DEPTH:Number = 3.0;

    public function Scale(x:Number, y:Number, model:DensityModel):void {
        super(SCALE_DENSITY, SCALE_WIDTH, SCALE_HEIGHT, SCALE_DEPTH, x, y, model);
    }

    public function getScaleReadout():String {
        // TODO: localize
        // scaled by DT-frame because we are measuring the 'normal impulses'
        var num:Number = (force / DensityModel.DT_FRAME);
        var numStr:String = String(Math.round(num * 100) / 100);
        var ret:String = String(numStr).substr(0, 7) + " N";
        return ret;
    }


    override public function registerContact(point:b2ContactResult):void {
        super.registerContact(point);

        var body1:b2Body = point.shape1.GetBody();
        var body2:b2Body = point.shape2.GetBody();

        if (body1.IsStatic() || body2.IsStatic()) {
            // this is our scale in contact with the ground
            return;
        }

        if (!(body1.GetUserData() is DensityObject && body2.GetUserData() is DensityObject)) {
            // not between movable models!
            return;
        }

        var model1:DensityObject = body1.GetUserData() as DensityObject;
        var model2:DensityObject = body2.GetUserData() as DensityObject;

        var topModel:DensityObject = model1.getY() > model2.getY() ? model1 : model2;

        if (this == topModel) {
            // only show readings if pressed from top.
            // TODO: check whether this is acceptable, not physical! (scales can show negative numbers if accelerated from below)
            return;
        }

        force += point.normalImpulse;
    }

    override public function resetContacts():void {
        super.resetContacts();

        force = 0;
    }

    override public function createNode(view:DensityView):DensityObjectNode {
        return new ScaleNode(this, view);
    }
}
}