package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ScaleNode;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

/**
 * This class represents the model object for a scale.
 */
public class Scale extends Cuboid {

    protected var totalImpulse: Number = 0;//in SI

    public static var SCALE_DENSITY: Number = 2.0 * 1000;

    private static const SCALE_SCALE: Number = 1.7;
    public static var SCALE_WIDTH: Number = 0.1 * SCALE_SCALE;
    public static var SCALE_HEIGHT: Number = 0.05 * SCALE_SCALE;
    public static var SCALE_DEPTH: Number = 0.1 * SCALE_SCALE;

    private const scaleReadoutListeners: Array = new Array();
    public static const GROUND_SCALE_X: Number = -DensityConstants.POOL_WIDTH_X / 2 - DensityConstants.LARGE_BLOCK_WIDTH - Scale.SCALE_WIDTH / 2;
    public static const GROUND_SCALE_Y: Number = Scale.SCALE_HEIGHT / 2;

    public static const POOL_SCALE_X: Number = DensityConstants.POOL_WIDTH_X / 2 - Scale.SCALE_WIDTH * 1.5;
    public static var POOL_SCALE_Y: Number;

    public function Scale( x: Number, y: Number, model: DensityModel ): void {
        super( SCALE_DENSITY, SCALE_WIDTH, SCALE_HEIGHT, SCALE_DEPTH, x, y, model, Material.CUSTOM );
        POOL_SCALE_Y = Scale.SCALE_HEIGHT / 2 - model.getPoolHeight();

        // set mass to make this mobile / immobile, and then listen to it
        getModel().scalesMovableProperty.addListener( updateBox2DModel );
        setMass( SCALE_WIDTH * SCALE_HEIGHT * SCALE_DEPTH * SCALE_DENSITY );
    }

    override public function isMovable(): Boolean {
        return super.isMovable() && getModel().scalesMovableProperty.value;
    }

    public function getScaleReadout(): String {
        // scaled by DT-frame because we are measuring the 'normal impulses'
        //impulse I=Fdt
        //F=I/dt
        var force: Number = totalImpulse / DensityModel.DT_PER_FRAME;
        var mass: Number = force / DensityConstants.GRAVITY;
        return FlexSimStrings.get( "properties.massKilogramValue", "{0} kg", [DensityConstants.format( mass )] );
    }

    override public function onFrameStep( dt: Number ): void {
        super.onFrameStep( dt );
        for each ( var scaleReadoutListener: Function in scaleReadoutListeners ) {
            scaleReadoutListener();
        }
    }

    /*
     * Fixed scale bug where it reported incorrect forces while being controlled by the user.
     * By setting the mass to zero, the physics engine can come up with the right normal forces.
     */
    override protected function isStatic(): Boolean {
        return super.isStatic() || userControlled;
    }

    override public function registerContact( point: b2ContactResult ): void {
        super.registerContact( point );

        var body1: b2Body = point.shape1.GetBody();
        var body2: b2Body = point.shape2.GetBody();

        if ( body1.IsStatic() && body2.IsStatic() ) {
            // this is our scale in contact with the ground
            return;
        }

        if ( !(body1.GetUserData() is DensityObject && body2.GetUserData() is DensityObject) ) {
            // not between movable models!
            return;
        }

        var model1: DensityObject = body1.GetUserData() as DensityObject;
        var model2: DensityObject = body2.GetUserData() as DensityObject;

        var topModel: DensityObject = model1.getY() > model2.getY() ? model1 : model2;

        //This conditional block is meant to avoid incorporating forces from underneath the scale
        if ( this == topModel ) {
            // only show readings if pressed from top.
            // TODO: check whether this is acceptable, not physical! (scales can show negative numbers if accelerated from below)
            //Maybe this block check should be skipped but it will be difficult to test when the scales can be moved
            //The important scenario to test is when the force from below is more than the normal force (e.g. the underblock is accelerating upwards)
            //Will need more thought once the scales are movable, and can be moved underwater.
            return;
        }

        totalImpulse += point.normalImpulse / DensityConstants.SCALE_BOX2D;//convert back to SI from box2d units
    }

    override public function resetContacts(): void {
        super.resetContacts();

        totalImpulse = 0;
    }

    override public function createNode( view: AbstractDBCanvas, massReadoutsVisible: BooleanProperty ): DensityObjectNode {
        return new ScaleNode( this, view );
    }

    public function addScaleReadoutListener( updateText: Function ): void {
        scaleReadoutListeners.push( updateText );
    }

    override public function toString(): String {
        return "Scale: " + super.toString();
    }
}
}