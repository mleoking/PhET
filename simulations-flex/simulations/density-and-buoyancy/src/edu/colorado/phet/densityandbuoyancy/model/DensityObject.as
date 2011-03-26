//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;
import edu.colorado.phet.flexcommon.model.NumericProperty;
import edu.colorado.phet.flexcommon.model.StringProperty;

/**
 * Base class for "something movable that behaves like an object in the play area", including Scales and Blocks.
 */
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
    private var velocityArrowModel: Vector2D = new Vector2D( 0, 0 );
    private var gravityForceArrowModel: Vector2D = new Vector2D( 0, 0 );
    private var buoyancyForceArrowModel: Vector2D = new Vector2D( 0, 0 );
    private var contactForceArrowModel: Vector2D = new Vector2D( 0, 0 );

    private const _forceVectors: Array = [gravityForceArrowModel,buoyancyForceArrowModel,contactForceArrowModel];
    private var model: DensityAndBuoyancyModel;

    private var body: b2Body; // our reference to the Box2D "body" in the physics engine
    private var submergedVolume: Number = 0.0;
    private var contactImpulseMap: Object = new Object();
    private var labelProperty: StringProperty;
    private const removalListeners: Array = new Array();
    private var _userControlled: Boolean = false;

    private var lastPosition: b2Vec2; // last position, used for velocity
    private var velocity: b2Vec2 = new b2Vec2();
    private var _inScene: BooleanProperty = new BooleanProperty( false );
    private const _nameVisible: BooleanProperty = new BooleanProperty( false );
    private var _name: String = "name";

    private var shouldOverrideVelocity: Boolean = false;

    public function get forceVectors(): Array {
        return _forceVectors;
    }

    public function DensityObject( x: Number, y: Number, z: Number, model: DensityAndBuoyancyModel, __density: Number, mass: Number, __volume: Number, __material: Material ) {
        this._material = __material;
        this._volume = new NumericProperty( FlexSimStrings.get( "properties.volume", "Volume" ), "m\u00b3", __volume );
        this._mass = new NumericProperty( FlexSimStrings.get( "properties.mass", "Mass" ), "{0} kg", mass );
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

        function densityChanged(): void { //this should change the mass
            setMass( volume * density );
        }

        getDensityProperty().addListener( densityChanged );

        this.x = new NumericProperty( "x", "m", x );
        this.y = new NumericProperty( "y", "m", y );
        this._z = new NumericProperty( "z", "m", z );

        lastPosition = new b2Vec2( x, y );

        this.model = model;
    }

    private function getLabelString(): String {
        return FlexSimStrings.get( "properties.massKilogramValue", "{0} kg", [DensityAndBuoyancyConstants.format( getMass() )] );
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
            updateBox2DModel();
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

    public function getVelocityArrowModel(): Vector2D {
        return velocityArrowModel;
    }

    public function getGravityForceArrowModel(): Vector2D {
        return gravityForceArrowModel;
    }

    public function getBuoyancyForceArrowModel(): Vector2D {
        return buoyancyForceArrowModel;
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
        //        if ( getBody() == null ) { //TODO: track this down
        //            throw new Error( "null body" );
        //        }
        model.getWorld().DestroyBody( getBody() );
        body = null;
        for each( var removalListener: Function in removalListeners ) {
            removalListener();
        }
        _inScene.value = false;
    }

    public function updatePositionFromBox2D(): void {
        setPosition( body.GetPosition().x / DensityAndBuoyancyConstants.SCALE_BOX2D, body.GetPosition().y / DensityAndBuoyancyConstants.SCALE_BOX2D );
    }

    public function setPosition( x: Number, y: Number ): void {
        this.x.value = x;
        this.y.value = y;

        updatePosition();
    }

    private function updatePosition(): void {
        var newX: Number = x.value * DensityAndBuoyancyConstants.SCALE_BOX2D;
        var newY: Number = y.value * DensityAndBuoyancyConstants.SCALE_BOX2D;
        if ( body != null ) { //body is only non-null after inScene = true, so only update when possible 
            if ( body.GetPosition().x != newX || body.GetPosition().y != newY ) {
                body.SetXForm( new b2Vec2( newX, newY ), 0 );
            }
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

    public function getModel(): DensityAndBuoyancyModel {
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
        sum.Multiply( 1.0 / DensityAndBuoyancyModel.DT_PER_FRAME / DensityAndBuoyancyConstants.SCALE_BOX2D );//to convert to force
        return sum;
    }

    public function resetContacts(): void {
        contactImpulseMap = new Object();
    }

    public function createNode( view: AbstractDBCanvas, massReadoutsVisible: BooleanProperty ): DensityObjectNode {
        throw new Error();
    }

    public function onFrameStep( dt: Number ): void {
        velocityArrowModel.setValue( body.GetLinearVelocity().x, body.GetLinearVelocity().y );//todo: use estimated velocity here?
        gravityForceArrowModel.setValue( getGravityForce().x, getGravityForce().y );
        buoyancyForceArrowModel.setValue( getBuoyancyForce().x, getBuoyancyForce().y );
        contactForceArrowModel.setValue( getNetContactForce().x, getNetContactForce().y );
        mytrace( "FRAME" );

        //Estimate velocity for purposes of fluid drag calculation since body.GetLinearVelocity reflects an internal value, not a good final state (i.e. objects in contact may be at rest but report a nonzero velocity)
        velocity.x = DensityAndBuoyancyModel.STEPS_PER_FRAME * (x.value - lastPosition.x) / dt; // TODO: FIX so we don't have steps-per-frame here. No clue why
        velocity.y = DensityAndBuoyancyModel.STEPS_PER_FRAME * (y.value - lastPosition.y) / dt;

        mytrace( velocity.x + ", " + velocity.y );

        //Keep track of positions for velocity estimation
        lastPosition.x = x.value;
        lastPosition.y = y.value;

        mytrace( "y: " + y.value );
    }

    public function beforeModelStep( dt: Number ): void {
        mytrace( "STEP" );
        if ( shouldOverrideVelocity ) {
            shouldOverrideVelocity = false;
            body.SetLinearVelocity( velocity.Copy() );
            trace( "SET VELOCITY: " + velocity.x + ", " + velocity.y );
        }
    }

    public function getMass(): Number {
        return _mass.value;
    }

    public function getGravityForce(): b2Vec2 {
        return new b2Vec2( 0, -DensityAndBuoyancyConstants.GRAVITY * getMass() );
    }

    //Set the submerged volume before calling this
    public function getBuoyancyForce(): b2Vec2 {
        return new b2Vec2( 0, DensityAndBuoyancyConstants.GRAVITY * submergedVolume * model.fluidDensity.value );
    }

    public function setSubmergedVolume( submergedVolume: Number ): void {
        this.submergedVolume = submergedVolume;
    }

    private const debug: Boolean = false;

    private function mytrace( o: * ): void {
        if ( name == "woodBlock" && debug ) {
            trace( o );
        }
    }

    /**
     * Return the drag force to be used in the physical update.
     * @return
     */
    public function getPhysicalDragForce(): b2Vec2 {
        return getDragForce( body.GetLinearVelocity() );
    }

    /**
     * Return the drag force to be used in the view.  Showing the actual physical drag force yields creates this problem:
     * When holding block A on top of Block b, with block b underwater, there is displayed a fluid drag force on block b, even though it is not moving.
     * @return
     */
    public function getViewDragForce(): b2Vec2 {
        return getDragForce( velocity );
    }

    public function getDragForce( velocity: b2Vec2 ): b2Vec2 {
        if ( _userControlled ) {
            return new b2Vec2();
        }
        else {
            //var dragForce: b2Vec2 = body.GetLinearVelocity().Copy();
            var dragForce: b2Vec2 = velocity.Copy();
            //mytrace( dragForce.x + ", " + dragForce.y );
            dragForce.Multiply( -600 * submergedVolume * (model.fluidDensity.value / Material.WATER.getDensity()) );
            return dragForce;
        }
    }

    public function getContactForceArrowModel(): Vector2D {
        return contactForceArrowModel;
    }

    public function copy( model: DensityAndBuoyancyModel ): DensityObject {
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
        velocity.x = 0;
        velocity.y = 0;
        lastPosition.x = x.value;
        lastPosition.y = y.value;
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
        if ( userControlled != _userControlled ) {
            _userControlled = userControlled;

            if ( !userControlled ) {
                shouldOverrideVelocity = true;
            }

            updateBox2DModel();
        }
    }

    public function get userControlled(): Boolean {
        return _userControlled;
    }

    public function isMovable(): Boolean {
        return true;
    }

    public function set inScene( inScene: Boolean ): void {
        this._inScene.value = inScene;
        if ( inScene ) {
            updatePosition();
        }
    }

    public function get inScene(): Boolean {
        return _inScene.value;
    }

    public function get inSceneProperty(): BooleanProperty {
        return _inScene;
    }

    public function toString(): String {
        return "x = " + x.value + ", y=" + y.value;
    }

    public function set nameVisible( nameVisible: Boolean ): void {
        _nameVisible.value = nameVisible;
    }

    public function get nameVisibleProperty(): BooleanProperty {
        return _nameVisible;
    }

    public function get name(): String {
        return _name;
    }

    public function set name( value: String ): void {
        _name = value;
    }

}
}