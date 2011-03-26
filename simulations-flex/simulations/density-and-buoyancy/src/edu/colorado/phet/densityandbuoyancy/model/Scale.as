//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Dynamics.Contacts.b2ContactResult;
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.view.AbstractDBCanvas;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.ScaleNode;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

/**
 * This class represents the model object for a scale, which measures the mass in kg (in density) or weight in Newtons (in buoyancy)
 */
public class Scale extends Cuboid {

    protected var totalImpulse: Number = 0;//in SI

    //Relative sizes for each dimension
    private static const REL_SCALE_WIDTH: Number = 0.1;
    private static const REL_SCALE_HEIGHT: Number = 0.05;
    private static const REL_SCALE_DEPTH: Number = 0.1;
    private static const REL_VOLUME: Number = REL_SCALE_WIDTH * REL_SCALE_HEIGHT * REL_SCALE_DEPTH;
    private static const DESIRED_VOLUME: Number = DensityAndBuoyancyConstants.litersToMetersCubed( 2.0 );

    private static const SIZE_SCALE: Number = Math.pow( DESIRED_VOLUME / REL_VOLUME, 1.0 / 3.0 );
    public static const SCALE_WIDTH: Number = REL_SCALE_WIDTH * SIZE_SCALE;
    public static const SCALE_HEIGHT: Number = REL_SCALE_HEIGHT * SIZE_SCALE;
    public static const SCALE_DEPTH: Number = REL_SCALE_DEPTH * SIZE_SCALE;
    public static const SCALE_VOLUME: Number = SCALE_WIDTH * SCALE_HEIGHT * SCALE_DEPTH;

    public static const SCALE_WEIGHT: Number = 50;//Newtons
    public static const SCALE_DENSITY: Number = SCALE_WEIGHT / (SCALE_VOLUME * 9.8);//9.8 is little g

    private const scaleReadoutListeners: Array = new Array();
    public static const GROUND_SCALE_X_LEFT: Number = -DensityAndBuoyancyConstants.POOL_WIDTH_X / 2 - DensityAndBuoyancyConstants.LARGE_BLOCK_WIDTH - Scale.SCALE_WIDTH / 2;
    public static const GROUND_SCALE_X_RIGHT: Number = DensityAndBuoyancyConstants.POOL_WIDTH_X / 2 + DensityAndBuoyancyConstants.LARGE_BLOCK_WIDTH + Scale.SCALE_WIDTH / 2;//Scale appears to the right in buoyancy because there is more room there
    public static const GROUND_SCALE_Y: Number = Scale.SCALE_HEIGHT / 2;

    public static const POOL_SCALE_X: Number = DensityAndBuoyancyConstants.POOL_WIDTH_X / 2 - Scale.SCALE_WIDTH * 1.5;
    public static var POOL_SCALE_Y: Number;

    public function Scale( x: Number, y: Number, model: DensityModel ): void {
        super( SCALE_DENSITY, SCALE_WIDTH, SCALE_HEIGHT, SCALE_DEPTH, x, y, model, Material.CUSTOM );
//        trace( "scale volume = " + DensityAndBuoyancyConstants.metersCubedToLiters( SCALE_WIDTH * SCALE_HEIGHT * SCALE_DEPTH ) );
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
        var mass: Number = force / DensityAndBuoyancyConstants.GRAVITY;
        return FlexSimStrings.get( "properties.massKilogramValue", "{0} kg", [DensityAndBuoyancyConstants.format( mass )] );
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

        totalImpulse += point.normalImpulse / DensityAndBuoyancyConstants.SCALE_BOX2D;//convert back to SI from box2d units
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