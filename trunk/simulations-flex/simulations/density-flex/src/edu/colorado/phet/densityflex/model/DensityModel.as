package edu.colorado.phet.densityflex.model {
import Box2D.Collision.Shapes.b2PolygonDef;
import Box2D.Collision.b2AABB;
import Box2D.Common.Math.b2Vec2;
import Box2D.Dynamics.b2Body;
import Box2D.Dynamics.b2BodyDef;
import Box2D.Dynamics.b2World;

import edu.colorado.phet.densityflex.DensityConstants;
import edu.colorado.phet.densityflex.view.DebugText;

public class DensityModel {
    private var densityObjects:Array;
    public static var ACCELERATION_DUE_TO_GRAVITY:Number = 9.8;

    private var poolWidth:Number = DensityConstants.POOL_WIDTH_X;
    private var poolHeight:Number = DensityConstants.POOL_HEIGHT_Y;
    private var poolDepth:Number = DensityConstants.POOL_DEPTH_Z;
    private var waterHeight:Number = 5.0 / 6.0 * poolHeight;
    public static var DISPLAY_SCALE:Number = 1000.0;

    //    private var poolWidth:Number = 15;
    //    private var poolHeight:Number = 7.5;
    //    private var poolDepth:Number = 5;
    //    private var waterHeight:Number = 5.5;
    //    public static var DISPLAY_SCALE:Number = 100.0;

    private static var BOUNDS:Number = 50;
    private var volume:Number = poolWidth * poolDepth * waterHeight;

    public static var STEPS_PER_FRAME:Number = 10;

    public static var DT_FRAME:Number = 1 / 3.0;//TODO: Fix this scale, should be about 1/30
    public static var DT_STEP:Number = DT_FRAME / STEPS_PER_FRAME;

    private var world:b2World;

    private var contactHandler:ContactHandler;
    private const densityObjectCreationListeners:Array = new Array();
    private const densityObjectDestructionListeners:Array = new Array();

    public function DensityModel() {
        densityObjects = new Array();

        initWorld();
        createGround();
    }

    public function addDensityObject(densityObject:DensityObject):void {
        densityObjects.push(densityObject);
        for each (var listener:Function in densityObjectCreationListeners) {
            listener(densityObject);
        }
    }

    public function clearDensityObjects():void {
        while (densityObjects.length > 0) {
            removeDensityObject(densityObjects[0]);
        }
    }

    private function removeDensityObject(densityObject:DensityObject):void {
        densityObject.remove();
        for each (var object:Function in densityObjectDestructionListeners) {
            object(densityObject);
        }
        densityObjects.splice(densityObjects.indexOf(densityObject), 1);
    }

    private function createGround():void {
        var groundBodyDef:b2BodyDef = new b2BodyDef();
        groundBodyDef.position.Set(0, 0);

        var groundBody:b2Body = world.CreateBody(groundBodyDef);

        var groundShapeDef:b2PolygonDef = new b2PolygonDef();
        groundShapeDef.SetAsOrientedBox(poolWidth / 2 * DensityConstants.SCALE_BOX2D, 50 * DensityConstants.SCALE_BOX2D, new b2Vec2(0, -50 * DensityConstants.SCALE_BOX2D - poolHeight * DensityConstants.SCALE_BOX2D), 0);
        groundBody.CreateShape(groundShapeDef);

        groundShapeDef.SetAsOrientedBox(BOUNDS / 2 * DensityConstants.SCALE_BOX2D, poolHeight * DensityConstants.SCALE_BOX2D, new b2Vec2(-(poolWidth / 2 + BOUNDS / 2) * DensityConstants.SCALE_BOX2D, -poolHeight * DensityConstants.SCALE_BOX2D), 0);
        groundBody.CreateShape(groundShapeDef);

        groundShapeDef.SetAsOrientedBox(BOUNDS / 2 * DensityConstants.SCALE_BOX2D, poolHeight * DensityConstants.SCALE_BOX2D, new b2Vec2((poolWidth / 2 + BOUNDS / 2) * DensityConstants.SCALE_BOX2D, -poolHeight * DensityConstants.SCALE_BOX2D), 0);
        groundBody.CreateShape(groundShapeDef);
    }

    private function initWorld():void {
        var worldBox:b2AABB = new b2AABB();
        worldBox.lowerBound.Set(-BOUNDS * DensityConstants.SCALE_BOX2D, -BOUNDS * DensityConstants.SCALE_BOX2D);
        worldBox.upperBound.Set(BOUNDS * DensityConstants.SCALE_BOX2D, BOUNDS * DensityConstants.SCALE_BOX2D);
        world = new b2World(worldBox, new b2Vec2(0, 0)/*we handle gravity ourselves*/, false/*don't sleep*/);

        contactHandler = new ContactHandler();
        world.SetContactListener(contactHandler);
    }

    public function getDensityObjects():Array {
        return densityObjects;
    }

    public function step():void {
        DebugText.clear();

        for each(densityObject in densityObjects) {
            densityObject.resetContacts();
        }

        for (var i:Number = 0; i < STEPS_PER_FRAME; i++) {

            world.Step(DT_STEP, 10);
            var densityObject:DensityObject;
            for each(densityObject in densityObjects) {
                densityObject.updatePositionFromBox2D();
            }
            updateWater();
            var waterY:Number = -poolHeight + waterHeight;
            for each(var cuboid:Cuboid in getCuboids()) {
                var body:b2Body = cuboid.getBody();

                // gravity?
                const gravityForce:b2Vec2 = cuboid.getGravityForce().Copy();
                gravityForce.Multiply(DensityConstants.SCALE_BOX2D);
                body.ApplyForce(gravityForce, body.GetPosition());
                trace("gravity = " + gravityForce.y);

                var submergedVolume:Number;
                if (waterY > cuboid.getTopY()) {
                    submergedVolume = cuboid.getVolume();
                }
                else if (waterY < cuboid.getBottomY()) {
                    submergedVolume = 0;
                }
                else {
                    submergedVolume = (waterY - cuboid.getBottomY() ) * cuboid.getWidth() * cuboid.getDepth();
                }
                // TODO: add in liquid density
                cuboid.setSubmergedVolume(submergedVolume);

                //TODO: make sure the scale gets fixed for the forces
                const buoyancyForce:b2Vec2 = cuboid.getBuoyancyForce().Copy();
                buoyancyForce.Multiply(DensityConstants.SCALE_BOX2D);
                body.ApplyForce(buoyancyForce, body.GetPosition());

                const dragForce:b2Vec2 = cuboid.getDragForce().Copy();
                dragForce.Multiply(DensityConstants.SCALE_BOX2D);
                body.ApplyForce(dragForce, body.GetPosition());
            }
        }

        for each(densityObject in densityObjects) {
            densityObject.modelStepped();
        }
    }

    private function getCuboids():Array {
        var cuboids:Array = new Array();
        for each (var object:Object in densityObjects) {
            if (object is Cuboid) {
                cuboids.push(object);
            }
        }
        return cuboids;
    }

    public function updateWater():void {
        var cuboid:Cuboid;
        var sortedHeights:Array = new Array();
        for (var key:String in densityObjects) {
            cuboid = densityObjects[key];
            var top:Object = new Object();
            top.y = cuboid.getTopY();
            top.pos = 1;
            top.block = cuboid;
            var bottom:Object = new Object();
            bottom.y = cuboid.getBottomY();
            bottom.pos = 0;
            bottom.block = cuboid;
            sortedHeights.push(top);
            sortedHeights.push(bottom);
        }
        sortedHeights.sortOn(["y"], [Array.NUMERIC]);

        var curHeight:Number = 0;
        var volumeToGo:Number = volume;
        var crossSection:Number = poolWidth * poolDepth;

        for (var i:String in sortedHeights) {
            var ob:Object = sortedHeights[i];
            var pos:Number = ob.pos;
            var by:Number = ob.y + poolHeight;
            cuboid = ob.block;
            var idealHeight:Number = volumeToGo / crossSection + curHeight;
            if (idealHeight < by) {
                curHeight = idealHeight;
                volumeToGo = 0;
                break;
            }
            var heightGain:Number = by - curHeight;
            volumeToGo -= crossSection * heightGain;
            curHeight = by;
            if (pos == 0) {
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

    public function getPoolHeight():Number {
        return poolHeight;
    }

    public function getWaterHeight():Number {
        return waterHeight;
    }

    public function getPoolWidth():Number {
        return poolWidth;
    }

    //Z-axis, into the screen
    public function getPoolDepth():Number {
        return poolDepth;
    }

    public function getWorld():b2World {
        return world;
    }

    public function addDensityObjectCreationListener(addDensityObject:Function):void {
        densityObjectCreationListeners.push(addDensityObject);
    }
}
}