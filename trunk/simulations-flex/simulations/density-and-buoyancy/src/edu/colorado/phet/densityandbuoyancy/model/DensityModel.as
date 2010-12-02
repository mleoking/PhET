package edu.colorado.phet.densityandbuoyancy.model {
import Box2D.Collision.Shapes.b2PolygonDef;
import Box2D.Collision.b2AABB;
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.b2Body;
import Box2D.Dynamics.b2BodyDef;
import Box2D.Dynamics.b2World;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.view.DebugText;
import edu.colorado.phet.flexcommon.FlexSimStrings;

public class DensityModel {
    private var densityObjects: Array;

    private var poolWidth: Number = DensityConstants.POOL_WIDTH_X;
    private var poolHeight: Number;
    private var poolDepth: Number = DensityConstants.POOL_DEPTH_Z;
    private var waterHeight: Number;

    public const fluidDensity: NumericProperty = new NumericProperty( FlexSimStrings.get( "properties.fluidDensity", "Fluid Density" ), "kg/m\u00b3", Material.WATER.getDensity() );

    public static var DISPLAY_SCALE: Number = 1000.0;

    private static var BOUNDS: Number = 50;
    private var volume: Number;

    public static var STEPS_PER_FRAME: Number = 10;

    public static var FRAMES_PER_SECOND: Number = 30.0;//See the Application in mxml, value has to be duplicated there
    public static var DT_PER_FRAME: Number = 1.0 / FRAMES_PER_SECOND;
    public static var DT_PER_STEP: Number = DT_PER_FRAME / STEPS_PER_FRAME;

    private var world: b2World;

    private var contactHandler: ContactHandler;
    private const densityObjectCreationListeners: Array = new Array();
    private const densityObjectDestructionListeners: Array = new Array();
    private var time: Number = 0;//This time value is not reset, and is only used internally for debugging

    /**
     * Whether we should enable scales to move with the geometry (or not). Setting this to true will cause scales to
     * move according to forces from other objects
     */
    public var scalesMovableProperty: BooleanProperty = new BooleanProperty( false );
    private var showExactLiquidColor: Boolean;

    public function DensityModel( volume: Number, extendedPool: Boolean, showExactLiquidColor: Boolean = false ) {
        this.volume = volume;
        this.showExactLiquidColor = showExactLiquidColor;
        this.waterHeight = volume / poolWidth / poolDepth;
        densityObjects = new Array();
        if ( extendedPool ) {
            poolHeight = DensityConstants.POOL_HEIGHT_Y_EXTENDED;
        }
        else {
            poolHeight = DensityConstants.POOL_HEIGHT_Y;
        }

        initWorld();
        createGround();
        createDragBounds();
    }

    public function reset(): void {
        fluidDensity.reset();
        for each ( var densityObject: DensityObject in densityObjects ) {
            densityObject.reset();
        }
    }

    public function addDensityObject( densityObject: DensityObject ): void {
        trace( "Added object: " + densityObject.toString() );
        densityObjects.push( densityObject );
        for each ( var listener: Function in densityObjectCreationListeners ) {
            listener( densityObject );
        }
        densityObject.inScene = true;
    }

    public function teardown(): void {
        clearDensityObjects();
    }

    public function clearDensityObjects(): void {
        for each ( var densityObject: DensityObject in densityObjects ) {
            densityObject.remove();
            for each ( var object: Function in densityObjectDestructionListeners ) {
                object( densityObject );
            }
        }
        densityObjects = new Array();
    }

    public function removeDensityObject( densityObject: DensityObject ): void {
        densityObject.remove();
        for each ( var object: Function in densityObjectDestructionListeners ) {
            object( densityObject );
        }
        //Hooray, my code to remove an element from an as3 array
        var newArray: Array = new Array();
        for each ( var d: DensityObject in densityObjects ) {
            if ( d != densityObject ) {
                newArray.push( d );
            }
        }
        densityObjects = newArray;
    }

    private function createGround(): void {
        var groundBodyDef: b2BodyDef = new b2BodyDef();
        var groundBody: b2Body = world.CreateBody( groundBodyDef );

        var groundShapeDef: b2PolygonDef = new b2PolygonDef();

        //Bottom of the pool
        groundShapeDef.SetAsOrientedBox( BOUNDS / 2 * DensityConstants.SCALE_BOX2D, 500 * DensityConstants.SCALE_BOX2D, new b2Vec2( 0, -500 * DensityConstants.SCALE_BOX2D - poolHeight * DensityConstants.SCALE_BOX2D ), 0 );
        groundBody.CreateShape( groundShapeDef );

        //Left side of the pool
        groundShapeDef.SetAsOrientedBox( BOUNDS / 2 * DensityConstants.SCALE_BOX2D, BOUNDS * DensityConstants.SCALE_BOX2D, new b2Vec2( -(poolWidth / 2 + BOUNDS / 2) * DensityConstants.SCALE_BOX2D, -BOUNDS * DensityConstants.SCALE_BOX2D ), 0 );
        groundBody.CreateShape( groundShapeDef );

        //Right side of the pool
        groundShapeDef.SetAsOrientedBox( BOUNDS / 2 * DensityConstants.SCALE_BOX2D, BOUNDS * DensityConstants.SCALE_BOX2D, new b2Vec2( (poolWidth / 2 + BOUNDS / 2) * DensityConstants.SCALE_BOX2D, -BOUNDS * DensityConstants.SCALE_BOX2D ), 0 );
        groundBody.CreateShape( groundShapeDef );
    }

    private function createDragBounds(): void {
        var bodyDef: b2BodyDef = new b2BodyDef();
        var body: b2Body = world.CreateBody( bodyDef );

        var shapeDef: b2PolygonDef = new b2PolygonDef();

        //100m high and 100m wide
        const wallWidth: Number = 1 * DensityConstants.SCALE_BOX2D;
        const wallHeight: Number = 1 * DensityConstants.SCALE_BOX2D;
        const leftBound: Number = (-Math.abs( Scale.GROUND_SCALE_X ) - Scale.SCALE_WIDTH / 2) * DensityConstants.SCALE_BOX2D;
        shapeDef.SetAsOrientedBox( wallWidth, wallHeight, new b2Vec2( -wallWidth + leftBound, 0 ), 0 );
        body.CreateShape( shapeDef );

        const rightBound: Number = (Math.abs( Scale.GROUND_SCALE_X ) + Scale.SCALE_WIDTH / 2) * DensityConstants.SCALE_BOX2D;
        shapeDef.SetAsOrientedBox( wallWidth, wallHeight, new b2Vec2( wallWidth + rightBound, 0 ), 0 );
        body.CreateShape( shapeDef );
    }

    private function initWorld(): void {
        var worldBox: b2AABB = new b2AABB();
        worldBox.lowerBound.Set( -BOUNDS * DensityConstants.SCALE_BOX2D, -BOUNDS * DensityConstants.SCALE_BOX2D );
        worldBox.upperBound.Set( BOUNDS * DensityConstants.SCALE_BOX2D, BOUNDS * DensityConstants.SCALE_BOX2D );
        world = new b2World( worldBox, new b2Vec2( 0, 0 )/*we handle gravity ourselves*/, false/*don't sleep*/ );

        contactHandler = new ContactHandler();
        world.SetContactListener( contactHandler );
    }

    public function getDensityObjects(): Array {
        return densityObjects;
    }

    public function stepFrame(): void {
        DebugText.clear();

        for each( densityObject in densityObjects ) {
            densityObject.resetContacts();
        }

        for ( var i: Number = 0; i < STEPS_PER_FRAME; i++ ) {

            updateWaterHeight();
            var waterY: Number = -poolHeight + waterHeight;
            for each( var cuboid: Cuboid in getCuboids() ) {
                cuboid.beforeModelStep( DT_PER_STEP / STEPS_PER_FRAME );

                var body: b2Body = cuboid.getBody();
                var submergedVolume: Number;
                if ( waterY > cuboid.getTopY() ) {
                    submergedVolume = cuboid.volume;
                }
                else {
                    if ( waterY < cuboid.getBottomY() ) {
                        submergedVolume = 0;
                    }
                    else {
                        submergedVolume = (waterY - cuboid.getBottomY() ) * cuboid.getWidth() * cuboid.getDepth();
                    }
                }
                // TODO: allow liquid density to be something other than 1.00
                cuboid.setSubmergedVolume( submergedVolume );

                // Apply Forces
                const gravityForce: b2Vec2 = cuboid.getGravityForce().Copy();
                gravityForce.Multiply( DensityConstants.SCALE_BOX2D );
                body.ApplyForce( gravityForce, body.GetPosition() );

                const buoyancyForce: b2Vec2 = cuboid.getBuoyancyForce().Copy();
                buoyancyForce.Multiply( DensityConstants.SCALE_BOX2D );
                body.ApplyForce( buoyancyForce, body.GetPosition() );

                const dragForce: b2Vec2 = cuboid.getDragForce().Copy();
                dragForce.Multiply( DensityConstants.SCALE_BOX2D );
                body.ApplyForce( dragForce, body.GetPosition() );
            }

            world.Step( DT_PER_STEP, 10 );
            for each( var densityObject: DensityObject in densityObjects ) {
                densityObject.updatePositionFromBox2D();
                densityObject.box2DStepped();
            }

            time = time + DT_PER_STEP;
        }
        //        for each(var c2:Cuboid in getCuboids()) {
        //            trace(time + "\t" + c2.getY() + "\t" + c2.getBody().GetPosition().y+"\t"+c2.getBody().GetLinearVelocity().y);//+"\t for "+getCuboids().length+" cuboids");
        //        }
        for each( densityObject in densityObjects ) {
            densityObject.onFrameStep( DT_PER_FRAME );
        }
    }

    private function getCuboids(): Array {
        var cuboids: Array = new Array();
        for each ( var object: Object in densityObjects ) {
            if ( object is Cuboid ) {
                cuboids.push( object );
            }
        }
        return cuboids;
    }

    public function updateWaterHeight(): void {
        waterHeight = computeWaterHeight();
    }

    //See diagram here:
    //https://docs.google.com/drawings/edit?id=1es4N9_NNgmyZhTmRok_2VTMYCFpnztOf97Z-sOsTEnM
    /**
     * Options discussed by SR and JO for water height computation with arbitrary objects
     *
     * water height calculations for spheres and arbitrary polygonal objects can be broken into a few different algorithms:
     * 1) break everything into "cuboids", more specifically object markers with a change in cross-section-area. We can
     * break a sphere into cuboids such that the sum of volume of the cuboids is equal to the volume of the sphere. It
     * should be possible to break arbitrary polygonal objects into this AND keep the same volume, so JO recommends this
     * method for its simplicity in the engine and for performance. Slight loss in precision, but can trade performance
     * for more precision.
     * 2) break everything into tetrahedrons. works with exact water height for cubes and polygonal objects, but would
     * require a large number of tetrahedrons to approximate a sphere.
     * 3) break spheres (or other objects) into layers, and assume it has a constant cross-sectional area for the entire
     * layer. (JO: this is effectively the same as #1)
     * 4) analytically (or with numerical integration) compute the water heights? not recommended
     */
    public function computeWaterHeight(): Number {
        var sortedHeights: Array = getSortedObjectMarkers();
        var currentHeight: Number = 0;//This accumulates the water height and is eventually returned
        var remainingVolume: Number = volume;
        var crossSectionArea: Number = poolWidth * poolDepth;//Current cross sectional area (in y-z plane) at curHeight in the pool

        for each ( var objectMarker: ObjectMarker in sortedHeights ) {
            var nextY: Number = objectMarker.y + poolHeight;//Translates origin so that y=0 is at the floor
            var proposedHeight: Number = remainingVolume / crossSectionArea + currentHeight;//Height if the current cross section
            if ( proposedHeight < nextY ) {
                currentHeight = proposedHeight;
                remainingVolume = 0;
                break;
            }
            var heightGain: Number = nextY - currentHeight;
            remainingVolume -= crossSectionArea * heightGain;
            currentHeight = nextY;
            crossSectionArea += objectMarker.area;
        }

        // fill it up the rest of the way
        currentHeight += remainingVolume / crossSectionArea;
        return currentHeight;
    }

    private function getSortedObjectMarkers(): Array {
        var sortedHeights: Array = new Array();
        for ( var key: String in densityObjects ) {
            var cuboid: Cuboid = densityObjects[key];
            var top: ObjectMarker = new ObjectMarker();
            top.y = cuboid.getTopY();
            top.area = cuboid.getWidth() * cuboid.getDepth();
            var bottom: ObjectMarker = new ObjectMarker();
            bottom.y = cuboid.getBottomY();
            bottom.area = -top.area;
            sortedHeights.push( top );
            sortedHeights.push( bottom );
        }
        sortedHeights.sortOn( ["y"], [Array.NUMERIC] );
        return sortedHeights;
    }

    public function getPoolHeight(): Number {
        return poolHeight;
    }

    public function getWaterHeight(): Number {
        return waterHeight;
    }

    public function getPoolWidth(): Number {
        return poolWidth;
    }

    //Z-axis, into the screen
    public function getPoolDepth(): Number {
        return poolDepth;
    }

    public function getWorld(): b2World {
        return world;
    }

    public function addDensityObjectCreationListener( addDensityObject: Function ): void {
        densityObjectCreationListeners.push( addDensityObject );
    }

    public function getShowExactLiquidColor(): Boolean {
        return showExactLiquidColor;
    }
}
}

class ObjectMarker {
    public var y: Number;
    public var area: Number;
}