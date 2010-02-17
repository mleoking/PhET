package edu.colorado.phet.densityflex {
import Box2D.Collision.Shapes.b2PolygonDef;
import Box2D.Collision.b2AABB;
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.b2Body;
import Box2D.Dynamics.b2BodyDef;
import Box2D.Dynamics.b2World;

import flash.geom.ColorTransform;

public class DensityModel {
    private var cuboids : Array;
    private var poolWidth : Number = 15;
    private var poolHeight : Number = 7.5;
    private var poolDepth : Number = 5;
    private var waterHeight : Number = 5.5;
    private static var BOUNDS : Number = 50;
    private var volume : Number = poolWidth * poolDepth * waterHeight;

    public static var STEPS_PER_FRAME : Number = 10;

    public static var DT_FRAME : Number = 1 / 30.0;
    public static var DT_STEP : Number = DT_FRAME / STEPS_PER_FRAME;

    public static var DISPLAY_SCALE : Number = 100.0;

    private var world : b2World;

    private var contactHandler : ContactHandler;

    public function DensityModel() {
        cuboids = new Array();

        initWorld();
        createGround();
    }

    public function initializeTab1SameMass():void {
        cuboids.push(Block.newBlockSizeMass(3, 4.0, -4.5, 0, new ColorTransform(0.5, 0.5, 0), this));
        cuboids.push(Block.newBlockSizeMass(2, 4.0, -1.5, 0, new ColorTransform(0, 0, 1), this));
        cuboids.push(Block.newBlockSizeMass(1.5, 4.0, 1.5, 0, new ColorTransform(0, 1, 0), this));
        cuboids.push(Block.newBlockSizeMass(1, 4.0, 4.5, 0, new ColorTransform(1, 0, 0), this));
        cuboids.push(new Scale(-9.5, Scale.SCALE_HEIGHT / 2, this));
        cuboids.push(new Scale(4.5, Scale.SCALE_HEIGHT / 2 - poolHeight, this));
    }

    public function initializeTab1SameVolume():void {
        cuboids.push(Block.newBlockDensitySize(0.5, 2, -4.5, 0, new ColorTransform(0.5, 0.5, 0), this));
        cuboids.push(Block.newBlockDensitySize(1, 2, -1.5, 0, new ColorTransform(0, 0, 1), this));
        cuboids.push(Block.newBlockDensitySize(2, 2, 1.5, 0, new ColorTransform(0, 1, 0), this));
        cuboids.push(Block.newBlockDensitySize(4, 2, 4.5, 0, new ColorTransform(1, 0, 0), this));
        cuboids.push(new Scale(-9.5, Scale.SCALE_HEIGHT / 2, this));
        cuboids.push(new Scale(4.5, Scale.SCALE_HEIGHT / 2 - poolHeight, this));
    }

    public function clearCuboids() : void {
        for each( var cuboid : Cuboid in cuboids ) {
            world.DestroyBody(cuboid.getBody());
            cuboid.remove();
        }
        cuboids = new Array();
    }

    private function createGround():void {
        var groundBodyDef:b2BodyDef = new b2BodyDef();
        groundBodyDef.position.Set(0, 0);

        var groundBody:b2Body = world.CreateBody(groundBodyDef);

        var groundShapeDef:b2PolygonDef = new b2PolygonDef();
        groundShapeDef.SetAsOrientedBox(poolWidth / 2, 50, new b2Vec2(0, -50 - poolHeight), 0);
        groundBody.CreateShape(groundShapeDef);

        groundShapeDef.SetAsOrientedBox(BOUNDS / 2, poolHeight, new b2Vec2(-(poolWidth / 2 + BOUNDS / 2), -poolHeight), 0);
        groundBody.CreateShape(groundShapeDef);

        groundShapeDef.SetAsOrientedBox(BOUNDS / 2, poolHeight, new b2Vec2((poolWidth / 2 + BOUNDS / 2), -poolHeight), 0);
        groundBody.CreateShape(groundShapeDef);
    }

    private function initWorld():void {
        var worldBox : b2AABB = new b2AABB();
        worldBox.lowerBound.Set(-BOUNDS, -BOUNDS);
        worldBox.upperBound.Set(BOUNDS, BOUNDS);
        var gravity : b2Vec2 = new b2Vec2(0, 0);
        var doSleep : Boolean = false;
        world = new b2World(worldBox, gravity, doSleep);

        contactHandler = new ContactHandler();
        world.SetContactListener(contactHandler);
    }

    public function getCuboids() : Array {
        return cuboids;
    }

    public function step() : void {
        DebugText.clear();

        for each( cuboid in cuboids ) {
            cuboid.resetContacts();
        }

        for ( var i : Number = 0; i < STEPS_PER_FRAME; i++ ) {

            world.Step(DT_STEP, 10);
            var cuboid : Cuboid;
            for each( cuboid in cuboids ) {
                cuboid.update();
            }
            updateWater();
            var waterY : Number = -poolHeight + waterHeight;
            for each( cuboid in cuboids ) {
                var body : b2Body = cuboid.getBody();

                // gravity?
                body.ApplyForce(new b2Vec2(0, -9.8 * cuboid.getVolume() * cuboid.getDensity()), body.GetPosition());

                if ( waterY < cuboid.getBottomY() ) {
                    continue;
                }
                var submergedVolume : Number;
                if ( waterY > cuboid.getTopY() ) {
                    submergedVolume = cuboid.getVolume();
                }
                else {
                    submergedVolume = (waterY - cuboid.getBottomY() ) * cuboid.getWidth() * cuboid.getDepth();
                }
                // TODO: add in liquid density

                body.ApplyForce(new b2Vec2(0, 9.8 * submergedVolume), body.GetPosition());

                var dragForce:b2Vec2 = body.GetLinearVelocity().Copy();
                dragForce.Multiply(-2 * submergedVolume);
                body.ApplyForce(dragForce, body.GetPosition());
            }
        }
    }

    public function updateWater() : void {
        var cuboid : Cuboid;
        var sortedHeights : Array = new Array();
        for ( var key : String in cuboids ) {
            cuboid = cuboids[key];
            var top : Object = new Object();
            top.y = cuboid.getTopY();
            top.pos = 1;
            top.block = cuboid;
            var bottom : Object = new Object();
            bottom.y = cuboid.getBottomY();
            bottom.pos = 0;
            bottom.block = cuboid;
            sortedHeights.push(top);
            sortedHeights.push(bottom);
        }
        sortedHeights.sortOn(["y"], [Array.NUMERIC]);

        var curHeight : Number = 0;
        var volumeToGo : Number = volume;
        var crossSection : Number = poolWidth * poolDepth;

        for ( var i : String in sortedHeights ) {
            var ob : Object = sortedHeights[i];
            var pos : Number = ob.pos;
            var by : Number = ob.y + poolHeight;
            cuboid = ob.block;
            var idealHeight : Number = volumeToGo / crossSection + curHeight;
            if ( idealHeight < by ) {
                curHeight = idealHeight;
                volumeToGo = 0;
                break;
            }
            var heightGain : Number = by - curHeight;
            volumeToGo -= crossSection * heightGain;
            curHeight = by;
            if ( pos == 0 ) {
                // bottom of block
                crossSection -= cuboid.getWidth() * cuboid.getDepth();
            }
            else {
                // top of block
                crossSection += cuboid.getWidth() * cuboid.getDepth();
            }
        }

        // fill it up the rest of the way
        curHeight += volumeToGo / crossSection;

        waterHeight = curHeight;
    }

    public function getPoolHeight() : Number {
        return poolHeight;
    }

    public function getWaterHeight() : Number {
        return waterHeight;
    }

    public function getPoolWidth() : Number {
        return poolWidth;
    }

    public function getPoolDepth() : Number {
        return poolDepth;
    }

    public function getWorld():b2World {
        return world;
    }
}
}