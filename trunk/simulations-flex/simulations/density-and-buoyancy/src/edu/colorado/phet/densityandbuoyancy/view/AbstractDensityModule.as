package edu.colorado.phet.densityandbuoyancy.view {
import Box2D.Common.Math.b2Vec2;

import away3d.containers.*;
import away3d.core.base.*;
import away3d.core.draw.*;
import away3d.core.geom.*;
import away3d.core.math.*;
import away3d.materials.*;
import away3d.primitives.*;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.away3d.Bottle;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.GroundNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.Pickable;
import edu.colorado.phet.densityandbuoyancy.view.units.LinearUnit;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;
import edu.colorado.phet.flashcommon.MathUtil;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.core.UIComponent;

public class AbstractDensityModule extends UIComponent {
    //model
    protected var _model: DensityModel;

    protected var mainViewport: Away3DViewport = new Away3DViewport();
    protected var overlayViewport: Away3DViewport = new Away3DViewport();

    //navigation variables
    private var moving: Boolean = false;
    private var cachedY: Number;
    private var startMouseX: Number;
    private var startMouseY: Number;
    private var startMiddle: Number3D;
    private var selectedObject: AbstractPrimitive;

    public static const far: Number = 5000;

    private var waterTop: Plane;
    private var waterFront: Plane;
    private var running: Boolean = true;
    private var invalid: Boolean = true;
    private var marker: ObjectContainer3D;
    private var groundNode: GroundNode;

    protected var densityObjectNodeList: Array = new Array();

    private var waterVolumeIndicator: WaterVolumeIndicator;
    private var tickMarkSet: TickMarkSet;

    private var _units: Units = new Units( "kg/L", new LinearUnit( "kg", 1.0 ), new LinearUnit( "L", 1000.0 ), new LinearUnit( "kg/L", 1.0 / 1000.0 ) );

    public function AbstractDensityModule() {
        super();
        _model = new DensityModel();

        _model.addDensityObjectCreationListener( addDensityObject );
        waterVolumeIndicator = new WaterVolumeIndicator( _model );
        waterVolumeIndicator.visible = false;//only show it after its location is correct
    }

    public function init(): void {
        initEngine();
        initObjects();
        initListeners();

        addChild( mainViewport.view );

        //Don't intercept mouse events in the overlay scene, though we're not sure why this was happening in the first place
        overlayViewport.view.mouseEnabled = false;
        overlayViewport.view.mouseChildren = false;

        addChild( overlayViewport.view );
        tickMarkSet = new TickMarkSet( _model );
        addChild( tickMarkSet );

        addChild( waterVolumeIndicator );

        const bottle: Bottle = new Bottle();
        //set the location of the bottle to be out of the way of the interactive objects
        bottle.x = DensityConstants.POOL_WIDTH_X / 2 * DensityModel.DISPLAY_SCALE + bottle.width * 1.2;
        bottle.y = -bottle.height * 1.5;
        bottle.z = DensityConstants.VERTICAL_GROUND_OFFSET_AWAY_3D;
        mainViewport.scene.addChild( bottle );
    }

    override protected function updateDisplayList( unscaledWidth: Number, unscaledHeight: Number ): void {
        super.updateDisplayList( unscaledWidth, unscaledHeight );
        mainViewport.updateDisplayList( unscaledWidth, unscaledHeight );
        overlayViewport.updateDisplayList( unscaledWidth, unscaledHeight );
    }

    public function initEngine(): void {
        mainViewport.initEngine();
        overlayViewport.initEngine();
    }

    public function initObjects(): void {
        var poolHeight: Number = _model.getPoolHeight() * DensityModel.DISPLAY_SCALE;
        var waterHeight: Number = _model.getWaterHeight() * DensityModel.DISPLAY_SCALE;
        var poolWidth: Number = _model.getPoolWidth() * DensityModel.DISPLAY_SCALE;
        var poolDepth: Number = _model.getPoolDepth() * DensityModel.DISPLAY_SCALE;

        // NOTE: if the ground is not matching up with the objects resting on the ground (or the bottom of the pool), it is due to the ground being shifted by this amount
        waterFront = new Plane( { y: -poolHeight + waterHeight / 2 + DensityConstants.VERTICAL_GROUND_OFFSET_AWAY_3D, width: poolWidth, height: waterHeight, rotationX: 90, material: new ShadingColorMaterial( 0x0088FF, {alpha: 0.4} ) } );
        mainViewport.scene.addChild( waterFront );
        waterFront.mouseEnabled = false;
        waterTop = new Plane( { y: -poolHeight + waterHeight + DensityConstants.VERTICAL_GROUND_OFFSET_AWAY_3D, z: poolDepth / 2, width: poolWidth, height: poolDepth, material: new ShadingColorMaterial( 0x0088FF, {alpha: 0.4} ) } );
        mainViewport.scene.addChild( waterTop );
        waterTop.mouseEnabled = false;

        // when the fluid density changes, let's change the water color.
        model.fluidDensity.addListener( function(): void {
            var waterMaterial: ShadingColorMaterial;
            var density: Number = model.fluidDensity.value;
            if ( density <= Material.WATER.getDensity() ) {
                //                waterMaterial = new ShadingColorMaterial( 0x0088FF, {alpha: 0.15 + 0.25 * density / Material.WATER.getDensity() } );
                waterMaterial = new ShadingColorMaterial( 0x0088FF, {alpha: 0.4 * Math.sqrt( density / Material.WATER.getDensity() )} );
            }
            else {
                var green: uint = Math.round( MathUtil.scale( density, Material.WATER.getDensity(), DensityConstants.MAX_FLUID_DENSITY, 0x88, 0x33 ) );
                var blue: uint = Math.round( MathUtil.scale( density, Material.WATER.getDensity(), DensityConstants.MAX_FLUID_DENSITY, 0xFF, 0x33 ) );
                var alpha: Number = MathUtil.scale( density, Material.WATER.getDensity(), DensityConstants.MAX_FLUID_DENSITY, 0.4, 0.8 );
                waterMaterial = new ShadingColorMaterial( uint( (green << 8) + blue ), {alpha: alpha} );
            }
            waterFront.material = waterMaterial;
            waterTop.material = waterMaterial;
        } );

        // add grass, earth and pool inside
        groundNode = new GroundNode( _model );
        mainViewport.scene.addChild( groundNode );

        mainViewport.addLight();
        overlayViewport.addLight();

        marker = new ObjectContainer3D();
        marker.addChild( new Cube( { z: 50, width: 20, height: 20, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial( 0x9999CC ) } ) );
        marker.addChild( new Cube( { z: 150, width: 20, height: 20, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial( 0xCC9999 ) } ) );
        marker.addChild( new Cube( { z: -50, width: 5, height: 500, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial( 0xFFFFFF ) } ) );
        //        scene.addChild(marker);
    }

    private function addDensityObject( densityObject: DensityObject ): void {
        const densityObjectNode: DensityObjectNode = createDensityObjectNode( densityObject );
        mainViewport.scene.addChild( densityObjectNode );
        densityObjectNodeList.push( densityObjectNode );
    }

    protected function createDensityObjectNode( densityObject: DensityObject ): DensityObjectNode {
        return densityObject.createNode( this );
    }

    public function initListeners(): void {
        addEventListener( Event.ENTER_FRAME, onEnterFrame );
        stage.addEventListener( MouseEvent.MOUSE_DOWN, onMouseDown );
        stage.addEventListener( MouseEvent.MOUSE_UP, onMouseUp );
        stage.addEventListener( Event.RESIZE, onResize );
        stage.addEventListener( MouseEvent.MOUSE_MOVE, onMouseMove );
        stage.addEventListener( Event.RESIZE, onResize );
        onResize();
    }

    public function medianFrontScreenPoint( m: Mesh ): Number3D {
        var num: Number = 0;
        var kx: Number = 0;
        var ky: Number = 0;
        var kz: Number = 0;
        var front: Number = Infinity;
        var v: Vertex;
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
            var sv: ScreenVertex = mainViewport.camera.screen( m, v );
            kx += sv.x;
            ky += sv.y;
            kz += sv.z;
        }
        return new Number3D( kx / num, ky / num, kz / num );
    }

    public function onEnterFrame( event: Event ): void {
        if ( !running ) {
            return;
        }
        _model.step();
        if ( moving && selectedObject is Pickable ) {
            var pickable: Pickable = (selectedObject as Pickable);
            pickable.getBody().SetXForm( new b2Vec2( pickable.getBody().GetPosition().x, cachedY ), 0 );
            pickable.getBody().SetLinearVelocity( new b2Vec2( 0, 0 ) );
            pickable.updateGeometry();
        }
        waterFront.y = (-_model.getPoolHeight() + _model.getWaterHeight() / 2) * DensityModel.DISPLAY_SCALE;
        waterFront.height = _model.getWaterHeight() * DensityModel.DISPLAY_SCALE;//this is positive from the bottom of the pool
        waterTop.y = (-_model.getPoolHeight() + _model.getWaterHeight()) * DensityModel.DISPLAY_SCALE;

        updateWaterVolumeIndicater();

        mainViewport.view.render();
        overlayViewport.view.render();
        renderedOnce = true;
    }

    //Away3d must render at least once before we can obtain screen coordinates for vertices.
    private var renderedOnce: Boolean = false;

    private function updateWaterVolumeIndicater(): void {
        if ( renderedOnce ) {
            var screenVertex: ScreenVertex = mainViewport.camera.screen( groundNode, new Vertex( _model.getPoolWidth() * DensityModel.DISPLAY_SCALE / 2, (-_model.getPoolHeight() + _model.getWaterHeight()) * DensityModel.DISPLAY_SCALE, 0 ) );
            waterVolumeIndicator.x = screenVertex.x + mainViewport.view.x;
            waterVolumeIndicator.y = screenVertex.y + mainViewport.view.y;
            waterVolumeIndicator.visible = true;//Now can show the water volume indicator after it is at the right location

            tickMarkSet.updateCoordinates( mainViewport.camera, groundNode, mainViewport.view );
        }
        waterVolumeIndicator.setWaterHeight( _model.getWaterHeight() );
        //        water
    }

    /**
     * @return Whether the object can be selected by the mouse and moved
     */
    private function canCurrentlyMoveObject( ob: Object3D ): Boolean {
        return ob is Pickable && (ob as Pickable).isPickableProperty().value;
    }

    public function onMouseDown( event: MouseEvent ): void {
        startMouseX = stage.mouseX - mainViewport.view.x;
        startMouseY = stage.mouseY - mainViewport.view.y;
        if ( canCurrentlyMoveObject( mainViewport.view.mouseObject ) ) {
            moving = true;
            startMiddle = medianFrontScreenPoint( mainViewport.view.mouseObject as AbstractPrimitive );
            selectedObject = mainViewport.view.mouseObject as AbstractPrimitive;
            if ( selectedObject is Pickable ) {
                cachedY = (selectedObject as Pickable).getBody().GetPosition().y;
            }
        }
        stage.addEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
    }

    public function onMouseMove( event: MouseEvent ): void {
        if ( moving ) {
            var offsetX: Number = startMiddle.x - startMouseX;
            var offsetY: Number = startMiddle.y - startMouseY;
            var mX: Number = stage.mouseX - mainViewport.view.x;
            var mY: Number = stage.mouseY - mainViewport.view.y;
            var screenCubeCenterX: Number = mX + offsetX;
            var screenCubeCenterY: Number = mY + offsetY;
            var projected: Number3D = mainViewport.camera.unproject( screenCubeCenterX, screenCubeCenterY );
            projected.add( projected, new Number3D( mainViewport.camera.x, mainViewport.camera.y, mainViewport.camera.z ) );
            var cameraVertex: Vertex = new Vertex( mainViewport.camera.x, mainViewport.camera.y, mainViewport.camera.z );
            var rayVertex: Vertex = new Vertex( projected.x, projected.y, projected.z );
            var cubePlane: Plane3D = new Plane3D();
            cubePlane.fromNormalAndPoint( new Number3D( 0, 0, -1 ), new Number3D( 0, 0, -100 ) );
            var intersection: Vertex = cubePlane.getIntersectionLine( cameraVertex, rayVertex );
            if ( selectedObject is Pickable ) {
                var pickable: Pickable = selectedObject as Pickable;
                pickable.setPosition( intersection.x, intersection.y );
                cachedY = pickable.getBody().GetPosition().y;
            }

            marker.x = intersection.x;
            marker.y = intersection.y;
            marker.z = intersection.z;

            invalid = true;
        }
        invalid = true;
    }

    public function onMouseUp( event: MouseEvent ): void {
        moving = false;
        stage.removeEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
    }

    public function onStageMouseLeave( event: Event ): void {
        moving = false;
        stage.removeEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
    }

    public function onResize( event: Event = null ): void {
        //Centers the view
        mainViewport.onResize( stage );
        overlayViewport.onResize( stage );

        updateWaterVolumeIndicater();
    }

    public function removeObject( ob: DensityObjectNode ): void {
        mainViewport.scene.removeChild( ob );
    }

    public function pause(): void {
        running = false;
    }

    public function start(): void {
        running = true;
    }

    public function resetAll(): void {
        model.fluidDensity.reset(); // TODO: better reset functionality!
        running = true;
        if ( moving ) {
            moving = false;
            stage.removeEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
        }
    }

    public function get model(): DensityModel {
        return _model;
    }

    public function get canvas(): AbstractDensityAndBuoyancyCanvas {
        throw new Error( "called canvas() on abstract module" );
    }

    public function get units(): Units {
        return _units;
    }

    public function showScales(): Boolean {
        return false;
    }
}
}