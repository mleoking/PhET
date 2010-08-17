package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityModule;
import edu.colorado.phet.densityandbuoyancy.view.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.ScaleNode;
import edu.colorado.phet.flexcommon.FlexSimStrings;

/**
 * This class represents the model object for a scale.
 */
public class Scale extends Cuboid {

    private var totalImpulse:Number = 0;

    public static var SCALE_DENSITY:Number = 2.0 * 1000;

    private static const SCALE_SCALE:Number = 1.7;
    public static var SCALE_WIDTH:Number = 1.0 / 10 * SCALE_SCALE;
    public static var SCALE_HEIGHT:Number = 1 / 3.0 / 10 * SCALE_SCALE;
    public static var SCALE_DEPTH:Number = 1.0 / 10 * SCALE_SCALE;

    public function Scale(x:Number, y:Number, model:DensityModel, mass:Number):void {
        super(SCALE_DENSITY, SCALE_WIDTH, SCALE_HEIGHT, SCALE_DEPTH, x, y, model, Substance.CUSTOM);
    }

    public function getScaleReadout():String {
        // TODO: localize
        // scaled by DT-frame because we are measuring the 'normal impulses'
        var num:Number = totalImpulse / DensityModel.DT_FRAME / DensityConstants.GRAVITY / 1000;//todo: why the 1000?  does this handle units properly?
        var numStr:String = String(Math.round(num * 100) / 100);
        const readoutValue:String = String(numStr).substr(0, 7);
        return FlexSimStrings.get("properties.massValue","{0} kg",[readoutValue]);
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

        totalImpulse += point.normalImpulse;
    }

    override public function resetContacts():void {
        super.resetContacts();

        totalImpulse = 0;
    }

    override public function createNode(view:AbstractDensityModule):DensityObjectNode {
        return new ScaleNode(this, view);
    }
}
}