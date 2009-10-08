package edu.colorado.phet.densityflex{

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

import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.ColorTransform;

import mx.core.UIComponent;

public class FlexView3D extends UIComponent {

    //engine variables
    private var scene:Scene3D;
    private var camera:HoverCamera3D;
    private var fogfilter:FogFilter;
    private var renderer:IRenderer;
    private var view:View3D;

    //scene objects
    private var cube:Cube;

    //navigation variables
    private var moving:Boolean = false;
    private var startMouseX:Number;
    private var startMouseY:Number;
    private var startMiddle : Number3D;
    private var selectedObject : AbstractPrimitive;

    private var poolWidth : Number = 1500;
    private var poolHeight : Number = 750;
    private var poolDepth : Number = 500;
    private var waterHeight: Number = 550;
    private var volume : Number = poolWidth * poolDepth * waterHeight;
    private var far : Number = 5000;

    private var poolTop : Plane;
    private var poolFront : Plane;

    private var invalid : Boolean = true;

    private var marker : ObjectContainer3D;

    [Embed(source="density-flex/images/spheretex.png")]
    private var spheretex : Class;

    public function FlexView3D() {
        super();
    }

    override protected function createChildren():void {
        super.createChildren();
    }

    public function init() : void {
        initEngine();
        initObjects();
        initListeners();

        addChild(view);
    }

    override protected function updateDisplayList( unscaledWidth:Number, unscaledHeight:Number ):void {
        super.updateDisplayList(unscaledWidth, unscaledHeight);
        if ( view != null ) {
            view.x = unscaledWidth / 2;
            view.y = unscaledHeight / 2;
        }
    }

    public function initEngine():void {
        scene = new Scene3D();

        camera = new HoverCamera3D({ focus: 90, distance: 2000, mintiltangle: 0, maxtitlangle: 90 });
        camera.targetpanangle = camera.panangle = 180;
        camera.targettiltangle = camera.tiltangle = 8;
        camera.hover();

        //renderer = Renderer.BASIC;
        //renderer = Renderer.CORRECT_Z_ORDER;
        renderer = Renderer.INTERSECTING_OBJECTS;
        //renderer = new QuadrantRenderer();

        view = new View3D({scene:scene, camera:camera, renderer:renderer});

        var sp : Sprite = new Sprite();
        sp.graphics.beginFill(0x000000);
        sp.graphics.drawRect(0, 0, 5000, 5000);
        sp.graphics.endFill();
        addChild(sp);
        addChild(view);
    }

    public function updateWater() : void {
        var cubeVolume : Number = cube.width * cube.height * cube.depth;
        var idealHeight : Number = volume / (poolWidth * poolDepth);
        var highestHeight : Number = (volume + cubeVolume) / (poolWidth * poolDepth);

        if ( cube.y - cube.height / 2 > -poolHeight + idealHeight ) {
            waterHeight = idealHeight;
        }
        else if ( cube.y + cube.height / 2 < -poolHeight + highestHeight ) {
            waterHeight = highestHeight;
        }
        else {
            var bottomHeight : Number = poolHeight + (cube.y - cube.height / 2);
            var partialVolume : Number = volume - (bottomHeight * poolWidth * poolDepth);
            var partialHeight : Number = partialVolume / (poolWidth * poolDepth - cube.width * cube.depth);
            waterHeight = bottomHeight + partialHeight;
        }

        poolFront.y = -poolHeight + waterHeight / 2;
        poolFront.height = waterHeight;
        poolTop.y = -poolHeight + waterHeight;
    }

    public function initObjects():void {

        poolFront = new Plane({ y: -poolHeight + waterHeight / 2, width: poolWidth, height: waterHeight, rotationX: 90, material: new ShadingColorMaterial(0x0088FF, {alpha: 0.4}) });
        scene.addChild(poolFront);
        poolFront.mouseEnabled = false;
        poolTop = new Plane({ y: -poolHeight + waterHeight, z: poolDepth / 2, width: poolWidth, height: poolDepth, material: new ShadingColorMaterial(0x0088FF, {alpha: 0.4}) });
        scene.addChild(poolTop);
        poolTop.mouseEnabled = false;

        // back of pool
        scene.addChild(new Plane({ y: -poolHeight / 2, z:poolDepth, width: poolWidth, height: poolHeight, rotationX: 90, material: new ShadingColorMaterial(0xAAAAAA) }));

        // bottom of pool
        scene.addChild(new Plane({ y: -poolHeight, z:poolDepth / 2, width: poolWidth, height: poolDepth, material: new ShadingColorMaterial(0xAAAAAA) }));

        // sides of pool
        scene.addChild(new Plane({ x: poolWidth / 2, y: -poolHeight / 2, z: poolDepth / 2, width: poolHeight, height: poolDepth, rotationZ: 90, material: new ShadingColorMaterial(0xAAAAAA) }));
        scene.addChild(new Plane({ x: -poolWidth / 2, y: -poolHeight / 2, z: poolDepth / 2, width: poolHeight, height: poolDepth, rotationZ: -90, material: new ShadingColorMaterial(0xAAAAAA) }));

        // ground behind pool
        scene.addChild(new Plane({ z: ( (far - poolDepth) / 2 ) + poolDepth, width: poolWidth, height: far - poolDepth, material: new ShadingColorMaterial(0x00AA00) }));

        // ground to the sides of the pool
        scene.addChild(new Plane({ x: far / 2 + poolWidth / 2, z: far / 2, width: far, height: far, material: new ShadingColorMaterial(0x00AA00) }));
        scene.addChild(new Plane({ x: -far / 2 - poolWidth / 2, z: far / 2, width: far, height: far, material: new ShadingColorMaterial(0x00AA00) }));

        // front of earth beneath the pool
        scene.addChild(new Plane({ y: -far / 2 - poolHeight, width: poolWidth, height: far, rotationX: 90, material: new ShadingColorMaterial(0xAA7733) }));

        // front of earth to the sides
        scene.addChild(new Plane({ x: far / 2 + poolWidth / 2, y: -far / 2, width: far, height: far, rotationX: 90, material: new ShadingColorMaterial(0xAA7733) }));
        scene.addChild(new Plane({ x: -far / 2 - poolWidth / 2, y: -far / 2, width: far, height: far, rotationX: 90, material: new ShadingColorMaterial(0xAA7733) }));

        //scene.addChild(new Sphere({ x: -450, y: 300, z: 201, radius: 100, segmentsH: 10, segmentsW: 10, material: new PhongBitmapMaterial((new spheretex() as BitmapAsset).bitmapData) }));
        scene.addChild(new Cylinder({ x: 450, y: 300, z: 151, rotationZ: 90, radius: 50, height: 750, segmentsH: 1, segmentsW: 25, material: new ShadingColorMaterial(0xAA7755) }));

        // the cube
        cube = new Block(50, 200, new ColorTransform(1, 0, 0));
        cube.x = 450;
        cube.y = 0;
        scene.addChild(cube);

        var block1 : Block = new Block(10, 100, new ColorTransform(0, 1, 0));
        block1.x = 150;
        block1.y = 0;
        scene.addChild(block1);

        var block2 : Block = new Block(100, 300, new ColorTransform(0, 0, 1));
        block2.x = -150;
        block2.y = 0;
        scene.addChild(block2);

        var block3 : Block = new Block(50, 200, new ColorTransform(1, 1, 1));
        block3.x = -450;
        block3.y = 0;
        scene.addChild(block3);

        var light:DirectionalLight3D = new DirectionalLight3D({color:0xFFFFFF, ambient:0.2, diffuse:0.75, specular:0.1});
        light.x = 10000;
        light.z = -35000;
        light.y = 50000;
        scene.addChild(light);

        marker = new ObjectContainer3D();
        marker.addChild(new Cube({ z: 50, width: 20, height: 20, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial(0x9999CC) }));
        marker.addChild(new Cube({ z: 150, width: 20, height: 20, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial(0xCC9999) }));
        marker.addChild(new Cube({ z: -50, width: 5, height: 5, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial(0xFFFFFF) }));
        //			scene.addChild( marker );

    }

    public function initListeners():void {
        addEventListener(Event.ENTER_FRAME, onEnterFrame);
        view.addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
        view.addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
        view.addEventListener(Event.RESIZE, onResize);
        view.addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
        view.stage.addEventListener(Event.RESIZE, onResize);
        onResize();
    }

    public function medianFrontScreenPoint( m : Mesh ) : Number3D {
        var num : Number = 0;
        var kx : Number = 0;
        var ky : Number = 0;
        var kz : Number = 0;
        var front : Number = Infinity;
        var v : Vertex;
        for each( v in m.vertices ) {
            if ( v.z < front ) {
                front = v.z;
            }
        }
        for each( v in m.vertices ) {
            if ( v.z > front ) {
                continue;
            }
            num += 1.0;
            var sv : ScreenVertex = camera.screen(m, v);
            kx += sv.x;
            ky += sv.y;
            kz += sv.z;
        }
        return new Number3D(kx / num, ky / num, kz / num);
    }

    private var time : int = 0;

    public function onEnterFrame( event:Event ):void {
        if ( invalid ) {
            invalid = false;
            view.render();
        }
    }

    public function onMouseDown( event:MouseEvent ) : void {
        startMouseX = view.stage.mouseX - view.x;
        startMouseY = view.stage.mouseY - view.y;
        if ( view.mouseObject == cube || view.mouseObject is Block || view.mouseObject is Sphere || view.mouseObject is Cylinder ) {
            moving = true;
            startMiddle = medianFrontScreenPoint(view.mouseObject as AbstractPrimitive);
            selectedObject = view.mouseObject as AbstractPrimitive;
        }
        view.stage.addEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);
    }

    public function onMouseMove( event : MouseEvent ) : void {
        if ( moving ) {
            var offsetX : Number = startMiddle.x - startMouseX;
            var offsetY : Number = startMiddle.y - startMouseY;
            var mX : Number = view.stage.mouseX - view.x;
            var mY : Number = view.stage.mouseY - view.y;
            var screenCubeCenterX : Number = mX + offsetX;
            var screenCubeCenterY : Number = mY + offsetY;
            var projected : Number3D = camera.unproject(screenCubeCenterX, screenCubeCenterY);
            projected.add(projected, new Number3D(camera.x, camera.y, camera.z));
            var cameraVertex : Vertex = new Vertex(camera.x, camera.y, camera.z);
            var rayVertex : Vertex = new Vertex(projected.x, projected.y, projected.z);
            var cubePlane : Plane3D = new Plane3D();
            cubePlane.fromNormalAndPoint(new Number3D(0, 0, -1), new Number3D(0, 0, -100));
            var intersection : Vertex = cubePlane.getIntersectionLine(cameraVertex, rayVertex);
            selectedObject.x = intersection.x;
            selectedObject.y = intersection.y;

            marker.x = intersection.x;
            marker.y = intersection.y;
            marker.z = intersection.z;

            updateWater();
            invalid = true;
        }
        invalid = true;
    }

    public function onMouseUp( event:MouseEvent ) : void {
        moving = false;
        view.stage.removeEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);
    }

    public function onStageMouseLeave( event:Event ) : void {
        moving = false;
        view.stage.removeEventListener(Event.MOUSE_LEAVE, onStageMouseLeave);
    }

    public function onResize( event:Event = null ) : void {
        view.x = stage.stageWidth / 2;
        view.y = stage.stageHeight / 2;
    }
}
}