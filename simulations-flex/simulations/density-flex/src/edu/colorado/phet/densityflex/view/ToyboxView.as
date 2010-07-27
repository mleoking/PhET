package edu.colorado.phet.densityflex.view {
import Box2D.Common.Math.b2Vec2;

import away3d.cameras.*;
import away3d.cameras.lenses.OrthogonalLens;
import away3d.containers.*;
import away3d.core.base.*;
import away3d.core.clip.Clipping;
import away3d.core.draw.*;
import away3d.core.filter.*;
import away3d.core.geom.*;
import away3d.core.math.*;
import away3d.core.render.*;
import away3d.lights.*;
import away3d.materials.*;
import away3d.primitives.*;

import edu.colorado.phet.densityflex.model.Block;
import edu.colorado.phet.densityflex.model.DensityModel;
import edu.colorado.phet.densityflex.model.DensityObject;
import edu.colorado.phet.densityflex.model.Scale;

import flash.display.Sprite;
import flash.events.Event;
import flash.events.MouseEvent;

import flash.geom.ColorTransform;

import mx.core.UIComponent;
import mx.events.SliderEvent;

public class ToyboxView extends UIComponent {
    //model
    protected var model:DensityModel;

    //engine variables
    private var scene:Scene3D;
    private var camera:Camera3D;
    private var renderer:IRenderer;
    private var view:View3D;

    public var densityView:DensityViewFull;

    public static const far:Number = 5000;
    public static const verticalGroundOffset:Number = -1;

    public var backgroundSprite:Sprite;

    protected var densityObjectNodeList:Array = new Array();

    public function ToyboxView() {
        super();
    }

    override protected function createChildren():void {
        super.createChildren();
    }

    public function init():void {
        initEngine();
        initObjects();
        initListeners();

        backgroundSprite = new Sprite();
        backgroundSprite.graphics.beginFill( 0xFFFFFF );
        backgroundSprite.graphics.drawRect( 0, 0, 5000, 5000 );
        backgroundSprite.graphics.endFill();
        addChild( backgroundSprite );
        addChild( view );

        var mask:Sprite = new Sprite();
        mask.graphics.beginFill( 0xFF0000 );
        mask.graphics.drawRect( 0, 0, 400, 125 ); // make changeable
        mask.graphics.endFill();
        addChild( mask );
        this.mask = mask;

        view.x = 200 + 30; // temporary shift
        view.y = 63;
    }

    private function initListeners():void {
        addEventListener( Event.ENTER_FRAME, onEnterFrame );
        stage.addEventListener( MouseEvent.MOUSE_DOWN, onMouseDown );
        stage.addEventListener( MouseEvent.MOUSE_UP, onMouseUp );
        stage.addEventListener( Event.RESIZE, onResize );
        stage.addEventListener( MouseEvent.MOUSE_MOVE, onMouseMove );
        stage.addEventListener( Event.RESIZE, onResize );
    }

    override protected function updateDisplayList( unscaledWidth:Number, unscaledHeight:Number ):void {
        super.updateDisplayList( unscaledWidth, unscaledHeight );
        if ( view != null ) {
            view.x = unscaledWidth / 2;
            view.y = unscaledHeight / 2;
        }
    }

    public function initEngine():void {
        scene = new Scene3D();

        //        camera = new HoverCamera3D( { focus: 90, distance: 2000, mintiltangle: 0, maxtitlangle: 90 } );
        //        camera.targetpanangle = camera.panangle = 180;
        //        camera.targettiltangle = camera.tiltangle = 8;
        //        camera.hover();

        camera = new Camera3D();

        //        camera.lens = new OrthogonalLens();
        camera.position = new Number3D( 200, 300, -800 );
        camera.lookAt( new Number3D( 200, 0, 0 ) );
        camera.zoom = 2.5;

        //renderer = Renderer.BASIC;
        //renderer = Renderer.CORRECT_Z_ORDER;
        renderer = Renderer.INTERSECTING_OBJECTS;
        //renderer = new QuadrantRenderer();

        view = new View3D( {scene:scene, camera:camera, renderer:renderer} );


    }

    public function initObjects():void {
        // NOTE: if the ground is not matching up with the objects resting on the ground (or the bottom of the pool), it is due to the ground being shifted by this amount
        var light:DirectionalLight3D = new DirectionalLight3D( {color:0xFFFFFF, ambient:0.2, diffuse:0.75, specular:0.1} );
        light.x = 10000;
        light.z = -35000;
        light.y = 50000;
        scene.addChild( light );

        //        scene.addChild( new Cube( {width: 50,height: 50, depth: 50, material:new ColorMaterial( 0x000000 )} ) );
        var model:DensityModel = new DensityModel();
        var a:Block = Block.newBlockDensitySize( 1.0 / 8.0, 2, -4.5, 0, new ColorTransform( 0.5, 0.5, 0 ), model );
        model.addDensityObject( a );
        var b:Block = Block.newBlockDensitySize( 0.5, 2, -1.5, 0, new ColorTransform( 0, 0, 1 ), model );
        model.addDensityObject( b );
        var c:Block = Block.newBlockDensitySize( 2, 2, 1.5, 0, new ColorTransform( 0, 1, 0 ), model );
        model.addDensityObject( c );
        var d:Block = Block.newBlockDensitySize( 4, 2, 4.5, 0, new ColorTransform( 1, 0, 0 ), model );
        model.addDensityObject( d );

        scene.addChild( new BlockNode( a, null ) );
        scene.addChild( new BlockNode( b, null ) );
        scene.addChild( new BlockNode( c, null ) );
        scene.addChild( new BlockNode( d, null ) );

    }

    public function onEnterFrame( event:Event ):void {
        view.render();
    }

    public function onMouseDown( event:MouseEvent ):void {
        var startMouseX = stage.mouseX - view.x;
        var startMouseY = stage.mouseY - view.y;
        if ( view.mouseObject is Pickable ) {
            densityView.createToyboxObject( view.mouseObject as DensityObjectNode );
//            startMiddle = medianFrontScreenPoint( view.mouseObject as AbstractPrimitive );
//            selectedObject = view.mouseObject as AbstractPrimitive;
//            if ( selectedObject is Pickable ) {
//                cachedY = (selectedObject as Pickable).getBody().GetPosition().y;
//            }
        }
        stage.addEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
    }

    public function onMouseMove( event:MouseEvent ):void {
        //        if ( moving ) {
        //            var offsetX:Number = startMiddle.x - startMouseX;
        //            var offsetY:Number = startMiddle.y - startMouseY;
        //            var mX:Number = stage.mouseX - view.x;
        //            var mY:Number = stage.mouseY - view.y;
        //            var screenCubeCenterX:Number = mX + offsetX;
        //            var screenCubeCenterY:Number = mY + offsetY;
        //            var projected:Number3D = camera.unproject( screenCubeCenterX, screenCubeCenterY );
        //            projected.add( projected, new Number3D( camera.x, camera.y, camera.z ) );
        //            var cameraVertex:Vertex = new Vertex( camera.x, camera.y, camera.z );
        //            var rayVertex:Vertex = new Vertex( projected.x, projected.y, projected.z );
        //            var cubePlane:Plane3D = new Plane3D();
        //            cubePlane.fromNormalAndPoint( new Number3D( 0, 0, -1 ), new Number3D( 0, 0, -100 ) );
        //            var intersection:Vertex = cubePlane.getIntersectionLine( cameraVertex, rayVertex );
        //            if ( selectedObject is Pickable ) {
        //                var pickable:Pickable = selectedObject as Pickable;
        //                pickable.setPosition( intersection.x, intersection.y );
        //                cachedY = pickable.getBody().GetPosition().y;
        //            }
        //
        //            marker.x = intersection.x;
        //            marker.y = intersection.y;
        //            marker.z = intersection.z;
        //
        //            invalid = true;
        //        }
        //        invalid = true;
    }

    public function onMouseUp( event:MouseEvent ):void {
        //        moving = false;
        //        stage.removeEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
    }

    public function onStageMouseLeave( event:Event ):void {
        //        moving = false;
        //        stage.removeEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
    }

    public function onResize( event:Event = null ):void {
        //        //Centers the view
        //        view.x = stage.stageWidth / 2;
        //        view.y = stage.stageHeight / 2;
        //
        //        camera.zoom = Math.min( stage.stageWidth / 100, stage.stageHeight / 65 );

    }

    public function removeObject( ob:CuboidNode ):void {
        scene.removeChild( ob );
    }

    public function reset():void {
        //        running = true;
        //        if ( moving ) {
        //            moving = false;
        //            stage.removeEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
        //        }
    }
}
}