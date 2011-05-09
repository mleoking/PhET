//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDensityAndBuoyancyPlayAreaComponent;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityAndBuoyancyObject3D;
import edu.colorado.phet.flashcommon.AbstractMethodError;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;
import edu.colorado.phet.flexcommon.model.NumericProperty;
import edu.colorado.phet.flexcommon.model.StringProperty;
import edu.colorado.phet.flexcommon.model.Vector2D;

/**
 * Abstract base class for "something movable that behaves like an object in the play area", including Scales and Blocks.
 */
public class DensityAndBuoyancyObject {

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
    //TODO: Remove reference to parent object, since it is a design weakness that
    // introduces unnecessary coupling.  Would be better if model listened for notification of removal, and if this
    // class had a method for setting the fluid density.
    private var model: DensityAndBuoyancyModel;

    private var body: b2Body; // our reference to the Box2D "body" in the physics engine
    private var submergedVolume: Number = 0.0;
    private var contactImpulseMap: Object = new Object();  //Mapping from other DensityAndBuoyancyObjects to the physical impulse (force * time) between them, which is provided by box2d
    private var labelProperty: StringProperty;
    private const removalListeners: Array = new Array();
    private var _userControlled: Boolean = false;

    private var lastPosition: b2Vec2; // last position, used for velocity
    private var velocity: b2Vec2 = new b2Vec2();
    private var _inScene: BooleanProperty = new BooleanProperty( false );  //Flag to indicate whether a block is currently in the away3d scene.  If so, its box2d companion model will be updated.
    private const _nameVisible: BooleanProperty = new BooleanProperty( false );
    private var _name: String = "name";

    private var shouldOverrideVelocity: Boolean = false; //If true, the object was thrown and should maintain the same velocity after letting go as before

    public function DensityAndBuoyancyObject( x: Number, y: Number, z: Number, model: DensityAndBuoyancyModel, __density: Number, mass: Number, __volume: Number, __material: Material ) {
        this._material = __material;
        //REVIEW why aren't you using a Units object to supply units, as in CustomObjectPropertiesPanel?
        //TODO: why aren't units internationalized?, see #2810
        this._volume = new NumericProperty( FlexSimStrings.get( "properties.volume", "Volume" ), "m\u00b3", __volume );
        //TODO: why aren't units internationalized?, see #2810
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

        //TODO: why aren't units internationalized?, see #2810
        this.x = new NumericProperty( "x", "m", x );
        this.y = new NumericProperty( "y", "m", y );
        this._z = new NumericProperty( "z", "m", z );

        lastPosition = new b2Vec2( x, y );

        this.model = model;
    }

    public function get forceVectors(): Array {
        return _forceVectors;
    }

    private function getLabelString(): String {
        //TODO: why aren't units internationalized?, see #2810
        return FlexSimStrings.get( "properties.massKilogramValue", "{0} kg", [DensityAndBuoyancyConstants.format( getMass() )] );
    }

    protected function getLabelProperty(): StringProperty {
        return labelProperty;
    }

    public function addMaterialListener( listener: Function ): void {
        materialListeners.push( listener );
    }

    //Listen to changes in the color transform (flash instance that is used to colorize the object at painting time)
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

    protected function notifyColorTransformListeners(): void {
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

    public function getGravityForceArrowModel(): Vector2D {
        return gravityForceArrowModel;
    }

    public function getBuoyancyForceArrowModel(): Vector2D {
        return buoyancyForceArrowModel;
    }

    public function getX(): Number {
        return x.value;
    }

    //TODO: Z, x and y should be provided by similar interfaces (function get)
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
        _inScene.value = false;
    }

    //Update this model position after the Box2D physics engine has stepped
    public function updatePositionFromBox2D(): void {
        setPosition( body.GetPosition().x / DensityAndBuoyancyConstants.SCALE_BOX2D, body.GetPosition().y / DensityAndBuoyancyConstants.SCALE_BOX2D );
    }

    public function setPosition( x: Number, y: Number ): void {
        this.x.value = x;
        this.y.value = y;

        updatePosition();
    }

    private function updatePosition(): void {
        //Transform from the Box2D model to the actual model.  Transformation is through a uniform-aspect-ratio scale with no offset, so represented with a single multiplier.
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

    //When the body for this object changes, make sure the old body gets removed from the box2D model
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

    /**
     * Callback from box2d when a contact between two bodies has been identified.  The results are stored so that a time-averaged value can be used (since there are many model steps per time step).
     * @param contact
     */
    public function registerContact( contact: b2ContactResult ): void {
        var other: b2Body = contact.shape1.GetBody();

        //Determine whether this block is being pushed or doing the pushing
        var sign: Number = 1.0;
        if ( other == body ) {
            other = contact.shape2.GetBody();
            sign = -1.0;
        }

        //Make an entry in the map if there wasn't one already
        if ( contactImpulseMap[other] == undefined ) {
            contactImpulseMap[other] = new b2Vec2( 0, 0 );
        }

        //Add the new impulse term to the sum of impulses.
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

    //Abstract method
    //Create the node for this DensityAndBuoyancyObject which will be shown in the specified canvas, and will show the readouts as specified by the massReadoutsVisible Property
    public function createNode( canvas: AbstractDensityAndBuoyancyPlayAreaComponent, massReadoutsVisible: BooleanProperty ): DensityAndBuoyancyObject3D {
        throw new AbstractMethodError();
    }

    public function onFrameStep( dt: Number ): void {
        velocityArrowModel.setValue( body.GetLinearVelocity().x, body.GetLinearVelocity().y );
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

    //If the object was thrown it should maintain the same velocity after letting go as before
    public function beforeModelStep( dt: Number ): void {
        if ( shouldOverrideVelocity ) {
            shouldOverrideVelocity = false;
            body.SetLinearVelocity( velocity.Copy() );
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

    //Set the volume of this object that is underwater for purposes of computing the buoyant force
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
     * Return the drag force to be used in the model.  When showing this value in the view, showing the actual physical drag force yields creates this problem:
     * When holding block A on top of Block b, with block b underwater, there is displayed a fluid drag force on block b, even though it is not moving.
     * @return
     */
    public function getDragForce( velocity: b2Vec2 ): b2Vec2 {
        if ( _userControlled ) {
            return new b2Vec2();
        }
        else {
            var dragForce: b2Vec2 = velocity.Copy();
            dragForce.Multiply( -600 * submergedVolume * (model.fluidDensity.value / Material.WATER.getDensity()) );
            return dragForce;
        }
    }

    public function getContactForceArrowModel(): Vector2D {
        return contactForceArrowModel;
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

    //Update the corresponding box2D model based on the state of this model object
    public function updateBox2DModel(): void {
        throw new AbstractMethodError();
    }

    public function addRemovalListener( removalListener: Function ): void {
        removalListeners.push( removalListener );
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

    //True if it is possible for the user to drag the object
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
        return "x = " + x.value + ", y=" + y.value;//Not all blocks have names so don't show it in the default toString
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