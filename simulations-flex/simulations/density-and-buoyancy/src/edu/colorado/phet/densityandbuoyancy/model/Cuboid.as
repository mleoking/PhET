//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Collision.Shapes.b2PolygonDef;
import Box2D.Dynamics.b2BodyDef;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;

/**
 * Model for all cuboid (block-shaped) objects. Scales are represented in the physical model as a block, so they are Cuboids too
 */
public class Cuboid extends DensityObject {

    protected var width: Number;
    protected var height: Number;
    protected var depth: Number;
    private var shapeDef: b2PolygonDef = new b2PolygonDef();
    private var bodyDef: b2BodyDef = new b2BodyDef();
    private var shapeChangeListeners: Array = new Array();

    public function Cuboid( density: Number, width: Number, height: Number, depth: Number, x: Number, y: Number, model: DensityAndBuoyancyModel, __material: Material ) {
        super( x, y, depth / 2 + DensityAndBuoyancyConstants.DEFAULT_BLOCK_WATER_OFFSET, model, density, density * width * height * depth, width * height * depth, __material );
        this.width = width;
        this.height = height;
        this.depth = depth;

        updateBox2DModel();

        function volumeChangeListener(): void {
            updateDimensions();
        }

        getVolumeProperty().addListener( volumeChangeListener );

        setVolume( width * height * depth );
        inSceneProperty.addListener( updateShapeDef );
    }

    public override function updateBox2DModel(): void {
        updateBodyDef();
        updateShapeDef();
    }

    protected function isStatic(): Boolean {
        return !isMovable();
    }

    //Updates the box2d BodyDef based on the position, mass, etc.
    private function updateBodyDef(): void {
        bodyDef.position.Set( getX() * DensityAndBuoyancyConstants.SCALE_BOX2D, getY() * DensityAndBuoyancyConstants.SCALE_BOX2D );
        bodyDef.fixedRotation = true;
        if ( isStatic() ) {
            // if a scale is being pushed by forces while the user is controlling it, it will display a wrong force value (weight)
            bodyDef.massData.mass = 0;//Fixes a bug where the scale would read out the wrong value if the user was controlling it, due to incorrect inclusion of fluid drag forces
        }
        else {
            bodyDef.massData.mass = getMass();
        }
        bodyDef.massData.center.SetZero();
        bodyDef.massData.I = 1.0; // rotational inertia shouldn't matter
    }

    //Updates the box2d ShapeDef based on the cuboid's friction, density, shape, etc.
    private function updateShapeDef(): void {
        if ( inScene ) {//only add to box2d if block is visible in away3d
            shapeDef.friction = 0.3;
            shapeDef.restitution = 0;
            shapeDef.density = density;
            setBody( getModel().getWorld().CreateBody( bodyDef ) );
            shapeDef.SetAsBox( width / 2 * DensityAndBuoyancyConstants.SCALE_BOX2D, height / 2 * DensityAndBuoyancyConstants.SCALE_BOX2D );
            getBody().CreateShape( shapeDef );
            getBody().SetUserData( this );
            notifyShapeChanged();
        }
    }

    public function setSize( width: Number, height: Number ): void {
        this.width = width;
        this.height = height;
        updateShapeDef();
    }

    // TODO: move this up to DensityObject
    public function addShapeChangeListener( shapeChangeListener: Function ): void {
        shapeChangeListeners.push( shapeChangeListener );
    }

    private function notifyShapeChanged(): void {
        for each ( var shapeChangeListener: Function in shapeChangeListeners ) {
            shapeChangeListener();
        }
    }

    //TODO: these getters should be rewritten with 'function get' syntax
    public function getWidth(): Number {
        return width;
    }

    public function getHeight(): Number {
        return height;
    }

    public function getDepth(): Number {
        return depth;
    }

    public function getTopY(): Number {
        return getY() + height / 2;
    }

    public function getBottomY(): Number {
        return getY() - height / 2;
    }

    public function updateDimensions(): void {
        this.height = Math.pow( volume, 1.0 / 3.0 );
        this.width = Math.pow( volume, 1.0 / 3.0 );
        this.depth = Math.pow( volume, 1.0 / 3.0 );
        z = depth / 2 + DensityAndBuoyancyConstants.DEFAULT_BLOCK_WATER_OFFSET;//put block edge equal to pool front
        updateShapeDef();
    }
}
}