package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityModule;
import edu.colorado.phet.densityandbuoyancy.view.DensityObjectNode;
import edu.colorado.phet.flexcommon.FlexSimStrings;

public class DensityObject {

    private var volume:NumericProperty;
    private var mass:NumericProperty;
    private var density:NumericProperty;
    private var _substance:Substance;
    private var substanceListeners:Array = new Array();

    private var x:NumericProperty;
    private var y:NumericProperty;
    private var _z:NumericProperty;
    private var velocityArrowModel:ArrowModel = new ArrowModel(0, 0);
    private var gravityForceArrowModel:ArrowModel = new ArrowModel(0, 0);
    private var buoyancyForceArrowModel:ArrowModel = new ArrowModel(0, 0);
    private var dragForceArrowModel:ArrowModel = new ArrowModel(0, 0);
    private var contactForceArrowModel:ArrowModel = new ArrowModel(0, 0);
    private var model:DensityModel;

    private var body:b2Body;
    private var submergedVolume:Number = 0.0;
    private var contactImpulseMap:Object = new Object();
    private var labelProperty:StringProperty;
    private const removalListeners:Array = new Array();

    public function DensityObject(x:Number, y:Number, z:Number, model:DensityModel, density:Number, mass:Number, volume:Number, __substance:Substance) {
        this._substance = __substance;
        this.volume = new NumericProperty(FlexSimStrings.get("properties.volume","Volume"), "m\u00b3", volume);
        this.mass = new NumericProperty(FlexSimStrings.get("properties.mass","Mass"), "kg", mass);
        this.density = new NumericProperty(FlexSimStrings.get("properties.density","Density"), "kg/m\u00b3", density);
        this.labelProperty = new StringProperty(String(getMass().toFixed(1)) + " kg");//Showing one decimal point is a good tradeoff between readability and complexity);

        function massChanged():void {
            if (isDensityFixed()) {
                setVolume(getMass() / getDensity());
            }
            else {
                //a change in mass or volume causes a change in density
                setDensity(getMass() / getVolume());
            }
            labelProperty.value = String(getMass().toFixed(1)) + " kg";
        }

        getMassProperty().addListener(massChanged);

        function volumeChanged():void {
            if (isDensityFixed()) {
                setMass(getVolume() * getDensity());
            }
            else { //custom object
                //a change in mass or volume causes a change in density
                setDensity(getMass() / getVolume());
            }
        }

        getVolumeProperty().addListener(volumeChanged);

        function densityChanged():void {
            //this should change the mass

            setMass(getVolume() * getDensity());

            //TODO: Switch into "custom object" mode
            //This could be confusing because it will switch the behavior of the other sliders

            var changed:Boolean = false;
            for each (var s:Substance in Substance.OBJECT_SUBSTANCES) {
                if (s.getDensity() == getDensity()) {
                    substance = s;
                    changed = true;
                }
            }
            if (!changed) {
                substance = new Substance("Custom", getDensity(), true);
            }
        }

        getDensityProperty().addListener(densityChanged);

        this.x = new NumericProperty("x", "m", x);
        this.y = new NumericProperty("y", "m", y);
        this._z = new NumericProperty("z", "m", z);

        this.model = model;
        this.density.value = density;
        //        this.listeners = new Array();
    }

    protected function getLabelProperty():StringProperty {
        return labelProperty;
    }

    public function addSubstanceListener(listener:Function):void {
        substanceListeners.push(listener);
    }

    public function set substance(substance:Substance):void {
        if (!this._substance.equals(substance)) {
            this._substance = substance;
            this.density.value = substance.getDensity();
            for each (var listener:Function in substanceListeners) {
                listener();
            }
        }
    }

    public function get substance():Substance {
        return _substance;
    }

    private function isDensityFixed():Boolean {
        return !_substance.isCustom();
    }

    public function getVolumeProperty():NumericProperty {
        return volume;
    }

    public function getMassProperty():NumericProperty {
        return mass;
    }

    public function getDensityProperty():NumericProperty {
        return density;
    }

    public function getSubstance():Substance {
        return substance;
    }

    public function getVelocityArrowModel():ArrowModel {
        return velocityArrowModel;
    }

    public function getGravityForceArrowModel():ArrowModel {
        return gravityForceArrowModel;
    }

    public function getBuoyancyForceArrowModel():ArrowModel {
        return buoyancyForceArrowModel;
    }

    public function getDragForceArrowModel():ArrowModel {
        return dragForceArrowModel;
    }

    public function getX():Number {
        return x.value;
    }

    public function getY():Number {
        return y.value;
    }

    public function getZ():Number {
        return _z.value;
    }

    public function set z(z:Number):void {
        this._z.value = z;
    }

    public function updatePositionFromBox2D():void {
        setPosition(body.GetPosition().x / DensityConstants.SCALE_BOX2D, body.GetPosition().y / DensityConstants.SCALE_BOX2D);
        //        trace("block y = " + getY());
    }

    public function remove():void {
        model.getWorld().DestroyBody(getBody());
        body = null;
        for each(var removalListener:Function in removalListeners) {
            removalListener();
        }
    }

    public function setPosition(x:Number, y:Number):void {
        this.x.value = x;
        this.y.value = y;

        if (body.GetPosition().x != x * DensityConstants.SCALE_BOX2D || body.GetPosition().y != y * DensityConstants.SCALE_BOX2D) {
            body.SetXForm(new b2Vec2(x * DensityConstants.SCALE_BOX2D, y * DensityConstants.SCALE_BOX2D), 0);
        }
    }

    public function getBody():b2Body {
        return body;
    }

    public function setBody(body:b2Body):void {
        if (this.body != null) {
            //delete from world
            getModel().getWorld().DestroyBody(this.body);
        }
        this.body = body;
    }

    public function getModel():DensityModel {
        return model;
    }

    public function registerContact(contact:b2ContactResult):void {
        var other:b2Body = contact.shape1.GetBody();
        var sign:Number = 1.0;
        if (other == body) {
            other = contact.shape2.GetBody();
            sign = -1.0;
        }
        if (contactImpulseMap[other] == undefined) {
            contactImpulseMap[other] = new b2Vec2(0, 0);
        }
        var term:b2Vec2 = contact.normal.Copy();
        term.Multiply(sign * contact.normalImpulse);
        contactImpulseMap[other].Add(term);
        //        trace("Force element: " + contactImpulseMap[other].x + ", " + contactImpulseMap[other].y);
    }

    private function getNetContactForce():b2Vec2 {
        var sum:b2Vec2 = new b2Vec2();
        for each (var object:Object in contactImpulseMap) {
            sum.Add(object as b2Vec2);
        }
        sum.Multiply(1.0 / DensityModel.DT_FRAME);//to convert to force
        //        trace("Force sum: " + sum.x + ", " + sum.y);
        return sum;
    }

    public function resetContacts():void {
        contactImpulseMap = new Object();
    }

    public function createNode(view:AbstractDensityModule):DensityObjectNode {
        throw new Error();
    }

    public function modelStepped():void {
        velocityArrowModel.setValue(body.GetLinearVelocity().x / DensityConstants.SCALE_BOX2D, body.GetLinearVelocity().y / DensityConstants.SCALE_BOX2D);
        gravityForceArrowModel.setValue(getGravityForce().x / DensityConstants.SCALE_BOX2D, getGravityForce().y / DensityConstants.SCALE_BOX2D);
        //        trace("Gravity y = " + getGravityForce().y);
        buoyancyForceArrowModel.setValue(getBuoyancyForce().x / DensityConstants.SCALE_BOX2D, getBuoyancyForce().y / DensityConstants.SCALE_BOX2D);
        dragForceArrowModel.setValue(getDragForce().x / DensityConstants.SCALE_BOX2D, getDragForce().y / DensityConstants.SCALE_BOX2D);
        contactForceArrowModel.setValue(getNetContactForce().x / DensityConstants.SCALE_BOX2D, getNetContactForce().y / DensityConstants.SCALE_BOX2D);
    }

    public function getMass():Number {
        return mass.value;
    }

    public function getGravityForce():b2Vec2 {
        return new b2Vec2(0, -DensityModel.ACCELERATION_DUE_TO_GRAVITY * getMass());
    }

    //Set the submerged volume before calling this
    public function getBuoyancyForce():b2Vec2 {
        return new b2Vec2(0, DensityModel.ACCELERATION_DUE_TO_GRAVITY * submergedVolume * Substance.WATER.getDensity());
    }

    public function setSubmergedVolume(submergedVolume:Number):void {
        this.submergedVolume = submergedVolume;
    }

    public function getDragForce():b2Vec2 {
        var dragForce:b2Vec2 = body.GetLinearVelocity().Copy();
        dragForce.Multiply(-300 * submergedVolume);
        return dragForce;
    }

    public function getContactForceArrowModel():ArrowModel {
        return contactForceArrowModel;
    }

    public function copy(model:DensityModel):DensityObject {
        throw new Error("bad copy on DensityObject");
    }

    public function setDensity(density:Number):void {
        this.density.value = density;
        updateBox2DModel();
    }

    public function getDensity():Number {
        return density.value;
    }

    public function setVolume(value:Number):void {
        this.volume.value = value;
        updateBox2DModel();
    }

    public function getVolume():Number {
        return this.volume.value;
    }

    private function setMass(number:Number):void {
        mass.value = number;
        updateBox2DModel();
    }

    public function reset():void {
        density.reset();
        volume.reset();
        mass.reset();
        x.reset();
        y.reset();
        _z.reset();
        updateBox2DModel();
    }

    public function getYProperty():NumericProperty {
        return y;
    }

    public function updateBox2DModel():void {
        throw new Error("Abstract method error");
    }

    public function addRemovalListener(removalListener:Function):void {
        removalListeners.push(removalListener);
    }
}
}