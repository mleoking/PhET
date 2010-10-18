package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.flexcommon.FlexSimStrings;

public class DensityObject {

    private var _volume: NumericProperty;
    private var _mass: NumericProperty;
    private var _density: NumericProperty;
    private var _material: Material;
    private var materialListeners: Array = new Array();
    private var colorTransformListeners: Array = new Array();

    private var x: NumericProperty;
    private var y: NumericProperty;
    private var _z: NumericProperty;
    private var velocityArrowModel: ArrowModel = new ArrowModel( 0, 0 );
    private var gravityForceArrowModel: ArrowModel = new ArrowModel( 0, 0 );
    private var buoyancyForceArrowModel: ArrowModel = new ArrowModel( 0, 0 );
    private var dragForceArrowModel: ArrowModel = new ArrowModel( 0, 0 );
    private var contactForceArrowModel: ArrowModel = new ArrowModel( 0, 0 );
    private var model: DensityModel;

    private var body: b2Body;
    private var submergedVolume: Number = 0.0;
    private var contactImpulseMap: Object = new Object();
    private var labelProperty: StringProperty;
    private const removalListeners: Array = new Array();
    private var _userControlled: Boolean = false;

    public function DensityObject( x: Number, y: Number, z: Number, model: DensityModel, __density: Number, mass: Number, __volume: Number, __material: Material ) {
        this._material = __material;
        this._volume = new NumericProperty( FlexSimStrings.get( "properties.volume", "Volume" ), "m\u00b3", __volume );
        this._mass = new NumericProperty( FlexSimStrings.get( "properties.mass", "Mass" ), "kg", mass );
        this._density = new NumericProperty( FlexSimStrings.get( "properties.density", "Density" ), "kg/m\u00b3", __density );
        this.labelProperty = new StringProperty( getLabelString() );//Showing one decimal point is a good tradeoff between readability and complexity);

        function massChanged(): void {
            if ( isDensityFixed() ) {
                setVolume( getMass() / density );
            }
            else {
                //a change in mass or volume causes a change in density
                setDensity( getMass() / volume );
            }
            labelProperty.value = getLabelString();
        }

        getMassProperty().addListener( massChanged );

        function volumeChanged(): void {
            if ( isDensityFixed() ) {
                setMass( volume * density );
            }
            else { //custom object
                //a change in mass or volume causes a change in density
                setDensity( getMass() / volume );
            }
        }

        getVolumeProperty().addListener( volumeChanged );

        function densityChanged(): void {
            //this should change the mass

            setMass( volume * density );

            //TODO: Switch into "custom object" mode
            //This could be confusing because it will switch the behavior of the other sliders

            // We should be setting the material elsewhere. This was "snapping" to a material
            //            var changed:Boolean = false;
            //            for each (var s:Material in Material.SELECTABLE_MATERIALS) {
            //                if (s.getDensity() == getDensity()) {
            //                    material = s;
            //                    changed = true;
            //                }
            //            }
            //            if (!changed) {
            //                material = new Material("Custom", getDensity(), true);
            //            }
        }

        getDensityProperty().addListener( densityChanged );

        this.x = new NumericProperty( "x", "m", x );
        this.y = new NumericProperty( "y", "m", y );
        this._z = new NumericProperty( "z", "m", z );

        this.model = model;
    }

    private function getLabelString(): String {
        return FlexSimStrings.get( "properties.massKilogramValue", "{0} kg", [DensityConstants.format( getMass() )] );
    }

    protected function getLabelProperty(): StringProperty {
        return labelProperty;
    }

    public function addMaterialListener( listener: Function ): void {
        materialListeners.push( listener );
    }

    public function addColorTransformListener( listener: Function ): void {
        colorTransformListeners.push( listener );
    }

    public function set material( material: Material ): void {
        if ( !this._material.equals( material ) ) {
            this._material = material;
            this._density.value = material.getDensity();
            for each ( var listener: Function in materialListeners ) {
                listener();
            }
        }
    }

    public function notifyColorTransformListeners(): void {
        for each ( var listener: Function in colorTransformListeners ) {
            listener();
        }
    }

    public function get material(): Material {
        return _material;
    }

    private function isDensityFixed(): Boolean {
        return !_material.isCustom();
    }

    public function getVolumeProperty(): NumericProperty {
        return _volume;
    }

    public function getMassProperty(): NumericProperty {
        return _mass;
    }

    public function getDensityProperty(): NumericProperty {
        return _density;
    }

    public function getVelocityArrowModel(): ArrowModel {
        return velocityArrowModel;
    }

    public function getGravityForceArrowModel(): ArrowModel {
        return gravityForceArrowModel;
    }

    public function getBuoyancyForceArrowModel(): ArrowModel {
        return buoyancyForceArrowModel;
    }

    public function getDragForceArrowModel(): ArrowModel {
        return dragForceArrowModel;
    }

    public function getX(): Number {
        return x.value;
    }

    public function getY(): Number {
        return y.value;
    }

    public function getZ(): Number {
        return _z.value;
    }

    public function set z( z: Number ): void {
        this._z.value = z;
    }

    public function remove(): void {
        model.getWorld().DestroyBody( getBody() );
        body = null;
        for each( var removalListener: Function in removalListeners ) {
            removalListener();
        }
    }

    public function updatePositionFromBox2D(): void {
        setPosition( body.GetPosition().x / DensityConstants.SCALE_BOX2D, body.GetPosition().y / DensityConstants.SCALE_BOX2D );
    }

    public function setPosition( x: Number, y: Number ): void {
        this.x.value = x;
        this.y.value = y;

        var newX: Number = x * DensityConstants.SCALE_BOX2D;
        var newY: Number = y * DensityConstants.SCALE_BOX2D;
        if ( body.GetPosition().x != newX || body.GetPosition().y != newY ) {
            body.SetXForm( new b2Vec2( newX, newY ), 0 );
        }
    }

    public function getBody(): b2Body {
        return body;
    }

    public function setBody( body: b2Body ): void {
        if ( this.body != null ) {
            //delete from world
            getModel().getWorld().DestroyBody( this.body );
        }
        this.body = body;
    }

    public function getModel(): DensityModel {
        return model;
    }

    public function registerContact( contact: b2ContactResult ): void {
        var other: b2Body = contact.shape1.GetBody();
        var sign: Number = 1.0;
        if ( other == body ) {
            other = contact.shape2.GetBody();
            sign = -1.0;
        }
        if ( contactImpulseMap[other] == undefined ) {
            contactImpulseMap[other] = new b2Vec2( 0, 0 );
        }
        var term: b2Vec2 = contact.normal.Copy();
        term.Multiply( sign * contact.normalImpulse );
        contactImpulseMap[other].Add( term );
        //        trace("Force element: " + contactImpulseMap[other].x + ", " + contactImpulseMap[other].y);
    }

    /**
     * @return In model scale (SI)
     */
    private function getNetContactForce(): b2Vec2 {
        var sum: b2Vec2 = new b2Vec2();
        for each ( var object: Object in contactImpulseMap ) {
            sum.Add( object as b2Vec2 );
        }
        sum.Multiply( 1.0 / DensityModel.DT_PER_FRAME / DensityConstants.SCALE_BOX2D );//to convert to force
        //        trace("Force sum: " + sum.x + ", " + sum.y);
        return sum;
    }

    public function resetContacts(): void {
        contactImpulseMap = new Object();
    }

    public function createNode( view: AbstractDBCanvas, massReadoutsVisible: BooleanProperty ): DensityObjectNode {
        throw new Error();
    }

    public function modelStepped(): void {
        velocityArrowModel.setValue( body.GetLinearVelocity().x, body.GetLinearVelocity().y );
        gravityForceArrowModel.setValue( getGravityForce().x, getGravityForce().y );
        //        trace("Gravity y = " + getGravityForce().y);
        buoyancyForceArrowModel.setValue( getBuoyancyForce().x, getBuoyancyForce().y );
        dragForceArrowModel.setValue( getDragForce().x, getDragForce().y );
        contactForceArrowModel.setValue( getNetContactForce().x, getNetContactForce().y );
    }

    public function getMass(): Number {
        return _mass.value;
    }

    public function getGravityForce(): b2Vec2 {
        return new b2Vec2( 0, -DensityConstants.GRAVITY * getMass() );
    }

    //Set the submerged volume before calling this
    public function getBuoyancyForce(): b2Vec2 {
        return new b2Vec2( 0, DensityConstants.GRAVITY * submergedVolume * model.fluidDensity.value );
    }

    public function setSubmergedVolume( submergedVolume: Number ): void {
        this.submergedVolume = submergedVolume;
    }

    public function getDragForce(): b2Vec2 {
        if ( _userControlled ) {
            return new b2Vec2();
        }
        else {
            var dragForce: b2Vec2 = body.GetLinearVelocity().Copy();
            dragForce.Multiply( -800 * submergedVolume * (model.fluidDensity.value / Material.WATER.getDensity()) );
            return dragForce;
        }
    }

    public function getContactForceArrowModel(): ArrowModel {
        return contactForceArrowModel;
    }

    public function copy( model: DensityModel ): DensityObject {
        throw new Error( "bad copy on DensityObject" );
    }

    public function setDensity( density: Number ): void {
        this._density.value = density;
        updateBox2DModel();
    }

    public function get density(): Number {
        return _density.value;
    }

    public function setVolume( value: Number ): void {
        this._volume.value = value;
        updateBox2DModel();
    }

    public function get volume(): Number {
        return this._volume.value;
    }

    public function setMass( number: Number ): void {
        _mass.value = number;
        updateBox2DModel();
    }

    public function reset(): void {
        _density.reset();
        _volume.reset();
        _mass.reset();
        x.reset();
        y.reset();
        _z.reset();
        updateBox2DModel();
    }

    public function getYProperty(): NumericProperty {
        return y;
    }

    public function getXProperty(): NumericProperty {
        return x;
    }

    public function updateBox2DModel(): void {
        throw new Error( "Abstract method error" );
    }

    public function addRemovalListener( removalListener: Function ): void {
        removalListeners.push( removalListener );
    }

    //Abstract
    public function box2DStepped(): void {
    }

    public function set userControlled( userControlled: Boolean ): void {
        _userControlled = userControlled;
        updateBox2DModel();
    }

    public function get userControlled(): Boolean {
        return _userControlled;
    }

    public function isMovable(): Boolean {
        return !userControlled;
    }

}
}