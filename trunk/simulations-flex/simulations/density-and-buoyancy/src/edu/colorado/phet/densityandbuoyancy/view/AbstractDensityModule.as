package edu.colorado.phet.densityandbuoyancy.view {
import Box2D.Common.Math.b2Vec2;

import away3d.cameras.*;
import away3d.containers.*;
import away3d.core.base.*;
import away3d.core.draw.*;
import away3d.core.filter.*;
import away3d.core.geom.*;
import away3d.core.math.*;
import away3d.core.render.*;
import away3d.lights.*;
import away3d.materials.*;
import away3d.primitives.*;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;

import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;

import mx.core.UIComponent;
import mx.events.SliderEvent;

public class AbstractDensityModule extends UIComponent {
    //model
    protected var _model:DensityModel;

    //engine variables
    private var scene:Scene3D;
    private var camera:HoverCamera3D;
    private var fogfilter:FogFilter;
    private var renderer:IRenderer;
    private var view:View3D;

    //navigation variables
    private var moving:Boolean = false;
    private var cachedY:Number;
    private var startMouseX:Number;
    private var startMouseY:Number;
    private var startMiddle:Number3D;
    private var selectedObject:AbstractPrimitive;

    public static const far:Number = 5000;

    private var waterTop:Plane;
    private var waterFront:Plane;
    private var running:Boolean = true;
    private var invalid:Boolean = true;
    private var marker:ObjectContainer3D;
    private var backgroundSprite:Sprite;
    private var groundNode:GroundNode;

    [Embed(source="../../../../../../data/density-and-buoyancy/images/spheretex.png")]
    private var spheretex:Class;

    protected var densityObjectNodeList:Array = new Array();

    private var waterVolumeIndicator:WaterVolumeIndicator;
    private var tickMarkSet:TickMarkSet;
    private var bottle:Bottle;

    public function AbstractDensityModule() {
        super();
        _model = new DensityModel();

        _model.addDensityObjectCreationListener(addDensityObject);
        waterVolumeIndicator = new WaterVolumeIndicator(_model);
        waterVolumeIndicator.visible = false;//only show it after its location is correct

        bottle = new Bottle();
        //set the location of the bottle to be out of the way of the interactive objects
        bottle.x = DensityConstants.POOL_WIDTH_X / 2 * DensityModel.DISPLAY_SCALE + bottle.height * DensityModel.DISPLAY_SCALE;
        bottle.y = DensityModel.DISPLAY_SCALE * bottle.height / 2;//TODO: render artifacts when underground
        bottle.z = -DensityConstants.VERTICAL_GROUND_OFFSET_AWAY_3D;
    }

    public function init():void {
        initEngine();
        initObjects();
        initListeners();

        backgroundSprite = new Sprite();
        backgroundSprite.graphics.beginFill(0x000000);
        backgroundSprite.graphics.drawRect(0, 0, 5000, 5000);
        backgroundSprite.graphics.endFill();
        //addChild(backgroundSprite);
        addChild(view);
        tickMarkSet = new TickMarkSet(_model);
        addChild(tickMarkSet);

        addChild(waterVolumeIndicator);
        scene.addChild(bottle);
    }

    override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        if (view != null) {
            view.x = unscaledWidth / 2;
            view.y = unscaledHeight / 2;
        }
    }

    public function initEngine():void {
        scene = new Scene3D();

        camera = new HoverCamera3D({ distance: 1600, mintiltangle: 0, maxtitlangle: 90 });
        camera.targetpanangle = camera.panangle = 180;
        camera.targettiltangle = camera.tiltangle = 8;
        camera.hover();

        //renderer = Renderer.BASIC;
        //renderer = Renderer.CORRECT_Z_ORDER;
        renderer = Renderer.INTERSECTING_OBJECTS;
        //renderer = new QuadrantRenderer();

        view = new View3D({scene:scene, camera:camera, renderer:renderer});
    }

    public function initObjects():void {
        var poolHeight:Number = _model.getPoolHeight() * DensityModel.DISPLAY_SCALE;
        var waterHeight:Number = _model.getWaterHeight() * DensityModel.DISPLAY_SCALE;
        var poolWidth:Number = _model.getPoolWidth() * DensityModel.DISPLAY_SCALE;
        var poolDepth:Number = _model.getPoolDepth() * DensityModel.DISPLAY_SCALE;

        // NOTE: if the ground is not matching up with the objects resting on the ground (or the bottom of the pool), it is due to the ground being shifted by this amount
        waterFront = new Plane({ y: -poolHeight + waterHeight / 2 + DensityConstants.VERTICAL_GROUND_OFFSET_AWAY_3D, width: poolWidth, height: waterHeight, rotationX: 90, material: new ShadingColorMaterial(0x0088FF, {alpha: 0.4}) });
        scene.addChild(waterFront);
        waterFront.mouseEnabled = false;
        waterTop = new Plane({ y: -poolHeight + waterHeight + DensityConstants.VERTICAL_GROUND_OFFSET_AWAY_3D, z: poolDepth / 2, width: poolWidth, height: poolDepth, material: new ShadingColorMaterial(0x0088FF, {alpha: 0.4}) });
        scene.addChild(waterTop);
        waterTop.mouseEnabled = false;

        // add grass, earth and pool inside
        groundNode = new GroundNode(_model);
        scene.addChild(groundNode);

        var light:DirectionalLight3D = new DirectionalLight3D({color:0xFFFFFF, ambient:0.2, diffuse:0.75, specular:0.1});
        light.x = 10000;
        light.z = -35000;
        light.y = 50000;
        scene.addChild(light);

        marker = new ObjectContainer3D();
        marker.addChild(new Cube({ z: 50, width: 20, height: 20, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial(0x9999CC) }));
        marker.addChild(new Cube({ z: 150, width: 20, height: 20, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial(0xCC9999) }));
        marker.addChild(new Cube({ z: -50, width: 5, height: 500, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial(0xFFFFFF) }));
        //        scene.addChild(marker);
    }

    private function addDensityObject(densityObject:DensityObject):void {
        const densityObjectNode:DensityObjectNode = createDensityObjectNode(densityObject);
        scene.addChild(densityObjectNode);
        densityObjectNodeList.push(densityObjectNode);
    }

    protected function createDensityObjectNode(densityObject:DensityObject):DensityObjectNode {
        return densityObject.createNode(this);
    }

    public function initListeners():void {
        addEventListener(Event.ENTER_FRAME, onEnterFrame);
        stage.addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
        stage.addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
        stage.addEventListener(Event.RESIZE, onResize);
        stage.addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
        stage.addEventListener(Event.RESIZE, onResize);
        onResize();
    }

    public function medianFrontScreenPoint(m:Mesh):Number3D {
        var num:Number = 0;
        var kx:Number = 0;
        var ky:Number = 0;
        var kz:Number = 0;
        var front:Number = Infinity;
        var v:Vertex;
        for each(v in m.vertices) {
            if (v.z < front) {
                front = v.z;
            }
        }
        for each(v in m.vertices) {
            if (v.z > front) {
                continue;
            }
            num += 1.0;
            var sv:ScreenVertex = camera.screen(m, v);
            kx += sv.x;
            ky += sv.y;
            kz += sv.z;
        }
        return new Number3D(kx / num, ky / num, kz / num);
    }

    public function onEnterFrame(event:Event):void {
        if (!running) {
            return;
        }
        _model.step();
        if (moving && selectedObject is Pickable) {
            var pickable:Pickable = (selectedObject as Pickable);
            pickable.getBody().SetXForm(new b2Vec2(pickable.getBody().GetPosition().x, cachedY), 0);
            pickable.getBody().SetLinearVelocity(new b2Vec2(0, 0));
            pickable.updateGeometry();
        }
        waterFront.y = (-_model.getPoolHeight() + _model.getWaterHeight() / 2) * DensityModel.DISPLAY_SCALE;
        waterFront.height = _model.getWaterHeight() * DensityModel.DISPLAY_SCALE;//this is positive from the bottom of the pool
        waterTop.y = (-_model.getPoolHeight() + _model.getWaterHeight()) * DensityModel.DISPLAY_SCALE;

        updateWaterHeightIndicator();

        // TODO: remove or update invalid
        view.render();
        renderedOnce = true;
    }

    private var renderedOnce:Boolean = false;

    private function updateWaterHeightIndicator():void {
        if (renderedOnce) {
            var screenVertex:ScreenVertex = camera.screen(groundNode, new Vertex(_model.getPoolWidth() * DensityModel.DISPLAY_SCALE / 2, (-_model.getPoolHeight() + _model.getWaterHeight()) * DensityModel.DISPLAY_SCALE, -20));
            waterVolumeIndicator.x = screenVertex.x + view.x;
            waterVolumeIndicator.y = screenVertex.y + view.y;
            waterVolumeIndicator.visible = true;//Now can show the water volume indicator after it is at the right location

            tickMarkSet.updateCoordinates(camera, groundNode, view);
        }
        waterVolumeIndicator.setWaterHeight(_model.getWaterHeight());
        //        water
    }

    public function onMouseDown(event:MouseEvent):void {
        startMouseX = stage.mouseX - view.x;
        startMouseY = stage.mouseY - view.y;
        if (view.mouseObject is Pickable) {
            moving = true;
            startMiddle = medianFrontScreenPoint(view.mouseObject as AbstractPrimitive);
            selectedObject = view.mouseObject as AbstractPrimitive;
            if (selectedObject is Pickable) {
                cachedY = (selectedObject as Pickable).getBody().GetPosition().y;
            }
        }
        stage.addEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);
    }

    public function onMouseMove(event:MouseEvent):void {
        if (moving) {
            var offsetX:Number = startMiddle.x - startMouseX;
            var offsetY:Number = startMiddle.y - startMouseY;
            var mX:Number = stage.mouseX - view.x;
            var mY:Number = stage.mouseY - view.y;
            var screenCubeCenterX:Number = mX + offsetX;
            var screenCubeCenterY:Number = mY + offsetY;
            var projected:Number3D = camera.unproject(screenCubeCenterX, screenCubeCenterY);
            projected.add(projected, new Number3D(camera.x, camera.y, camera.z));
            var cameraVertex:Vertex = new Vertex(camera.x, camera.y, camera.z);
            var rayVertex:Vertex = new Vertex(projected.x, projected.y, projected.z);
            var cubePlane:Plane3D = new Plane3D();
            cubePlane.fromNormalAndPoint(new Number3D(0, 0, -1), new Number3D(0, 0, -100));
            var intersection:Vertex = cubePlane.getIntersectionLine(cameraVertex, rayVertex);
            if (selectedObject is Pickable) {
                var pickable:Pickable = selectedObject as Pickable;
                pickable.setPosition(intersection.x, intersection.y);
                cachedY = pickable.getBody().GetPosition().y;
            }

            marker.x = intersection.x;
            marker.y = intersection.y;
            marker.z = intersection.z;

            invalid = true;
        }
        invalid = true;
    }

    public function onMouseUp(event:MouseEvent):void {
        moving = false;
        stage.removeEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);
    }

    public function onStageMouseLeave(event:Event):void {
        moving = false;
        stage.removeEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);
    }

    public function onResize(event:Event = null):void {
        //Centers the view
        view.x = stage.stageWidth / 2;
        view.y = stage.stageHeight / 2;

        camera.zoom = Math.min(stage.stageWidth / 100, stage.stageHeight / 65);

        updateWaterHeightIndicator();
    }

    public function removeObject(ob:DensityObjectNode):void {
        scene.removeChild(ob);
    }

    public function pause():void {
        running = false;
    }

    public function start():void {
        running = true;
    }

    public function resetAll():void {
        running = true;
        if (moving) {
            moving = false;
            stage.removeEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);
        }
    }

    public function testSliderChange(event:SliderEvent):void {
        //        camera.distance = event.value;
        camera.zoom = event.value;
        //        camera.moveCamera();
    }

    public function get model():DensityModel {
        return _model;
    }
}
}