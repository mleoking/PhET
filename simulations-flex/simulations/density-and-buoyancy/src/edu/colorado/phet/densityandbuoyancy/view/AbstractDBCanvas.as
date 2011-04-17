//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import Box2D.Common.Math.b2Vec2;

import away3d.cameras.Camera3D;
import away3d.containers.*;
import away3d.core.base.*;
import away3d.core.draw.*;
import away3d.core.geom.*;
import away3d.core.math.*;
import away3d.materials.*;
import away3d.primitives.*;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.away3d.DensityObjectNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.GroundNode;
import edu.colorado.phet.densityandbuoyancy.view.away3d.Pickable;
import edu.colorado.phet.densityandbuoyancy.view.units.LinearUnit;
import edu.colorado.phet.densityandbuoyancy.view.units.Units;
import edu.colorado.phet.flashcommon.AbstractMethodError;
import edu.colorado.phet.flashcommon.ApplicationLifecycle;
import edu.colorado.phet.flashcommon.MathUtil;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.model.BooleanProperty;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.core.UIComponent;

/**
 * Base class for the canvas, subclassed for each sim. Contains all of the drawables in the play area (but not the
 * control panels)
 */
public class AbstractDBCanvas extends UIComponent {
    //model
    protected var _model: DensityAndBuoyancyModel;

    // we use two viewports so we can layer things that go in front
    public const mainViewport: Away3DViewport = new Away3DViewport();
    public const overlayViewport: Away3DViewport = new Away3DViewport();

    //navigation variables
    private var moving: Boolean = false;
    private var cachedY: Number;
    private var startMouseX: Number;
    private var startMouseY: Number;
    private var startMiddle: Number3D;
    private var selectedObject: AbstractPrimitive;

    //Extent of the 3d geometry for the ground in x and z directions
    public static const FAR: Number = 5000;

    // references to the water Away3D surfaces, so we can change the water height visually
    private var waterTop: Plane;
    private var waterFront: Plane;
    private var groundNode: GroundNode; // our visual display of the ground
    private var _running: Boolean = true;//true if the module associated with this canvas is running
    private var marker: ObjectContainer3D; // testing object to verify locations in 3D
    private var waterVolumeIndicator: PoolVolumeIndicator;

    //Away3d must render at least once before we can obtain screen coordinates for vertices.
    public var renderedOnce: Boolean = false;
    private var renderListeners: Array = new Array();

    private const kgString: String = FlexSimStrings.get( "properties.massKilogram", "kg" );
    private const lString: String = FlexSimStrings.get( "properties.volumeLiter", "L" );
    private const densityString: String = FlexSimStrings.get( "properties.kilogramPerLiter", "kg/L" );
    //Special units since Liters are not SI
    private var _units: Units = new Units( "kg/L", new LinearUnit( kgString, 1.0 ), new LinearUnit( lString, 1000.0 ), new LinearUnit( densityString, 1.0 / 1000.0 ) );
    public const massReadoutsVisible: BooleanProperty = new BooleanProperty( true );
    protected var extendedPool: Boolean;

    /**
     * @param extendedPool Whether the pool should be deeper than normal
     * @param showExactLiquidColor Whether we show realistic liquid colors, or a fake oil color
     */
    public function AbstractDBCanvas( extendedPool: Boolean, showExactLiquidColor: Boolean = false ) {
        super();
        this.extendedPool = extendedPool;
        _model = createModel( showExactLiquidColor );

        _model.addDensityObjectCreationListener( addDensityObject );
        waterVolumeIndicator = new PoolVolumeIndicator( _model );
        waterVolumeIndicator.visible = false;//only show it after its location is correct

        //Set this canvas to use the full width and height of its parent component
        percentWidth = 100;
        percentHeight = 100;

        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            initEngine();
            initObjects();
            initListeners();

            addChild( mainViewport.view );

            //Don't intercept mouse events in the overlay scene, though we're not sure why this was happening in the first place
            overlayViewport.view.mouseEnabled = false;
            overlayViewport.view.mouseChildren = false;

            addChild( overlayViewport.view );
            addChild( waterVolumeIndicator );
        } );
    }

    //REVIEW BuoyancyCanvas overrides this without calling super, DensityCanvas does not override.
    // So I'm guessing that this implementation is appropriate for DensityCanvas.  If that's the
    // case, then make this abstract (throw new Error), and move this implementation to DensityCanvas.
    protected function createModel( showExactLiquidColor: Boolean ): DensityAndBuoyancyModel {
        return new DensityAndBuoyancyModel( DensityAndBuoyancyConstants.litersToMetersCubed( 100.0 ), extendedPool );
    }

    override protected function updateDisplayList( unscaledWidth: Number, unscaledHeight: Number ): void {
        super.updateDisplayList( unscaledWidth, unscaledHeight );
        mainViewport.updateDisplayList( unscaledWidth, unscaledHeight );
        overlayViewport.updateDisplayList( unscaledWidth, unscaledHeight );
    }

    //REVIEW doc, especially since Away3DViewport.initEngine is undocumented
    public function initEngine(): void {
        mainViewport.initEngine();
        overlayViewport.initEngine();
    }

    //REVIEW doc
    public function initObjects(): void {
        var poolHeight: Number = _model.getPoolHeight() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        var waterHeight: Number = _model.getWaterHeight() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        var poolWidth: Number = _model.getPoolWidth() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        var poolDepth: Number = _model.getPoolDepth() * DensityAndBuoyancyModel.DISPLAY_SCALE;

        // NOTE: if the ground is not matching up with the objects resting on the ground (or the bottom of the pool), it is due to the ground being shifted by this amount
        waterFront = new Plane( { y: -poolHeight + waterHeight / 2 + DensityAndBuoyancyConstants.VERTICAL_GROUND_OFFSET_AWAY_3D, width: poolWidth, height: waterHeight, rotationX: 90, material: new ShadingColorMaterial( 0x0088FF, {alpha: 0.4} ) } );
        mainViewport.scene.addChild( waterFront );
        waterFront.mouseEnabled = false;
        waterTop = new Plane( { y: -poolHeight + waterHeight + DensityAndBuoyancyConstants.VERTICAL_GROUND_OFFSET_AWAY_3D, z: poolDepth / 2, width: poolWidth, height: poolDepth, material: new ShadingColorMaterial( 0x0088FF, {alpha: 0.4} ) } );
        mainViewport.scene.addChild( waterTop );
        waterTop.mouseEnabled = false;

        // when the fluid density changes, let's change the water color.
        model.fluidDensity.addListener( function(): void {
            var waterMaterial: ShadingColorMaterial;
            var density: Number = model.fluidDensity.value;

            //On the first tab, when the user selects the 'oil' radio button, the fluid should visually change color
            //Even though on the 2nd tab oil and water aren't that far apart in density space, and look visually similar
            if ( density == Material.OLIVE_OIL.getDensity() && model.getShowExactLiquidColor() ) {
                waterMaterial = new ShadingColorMaterial( Material.OLIVE_OIL.tickColor, {alpha: 0.4 * Math.sqrt( density / Material.WATER.getDensity() )} );
            }
            else {
                if ( density <= Material.WATER.getDensity() ) {
                    waterMaterial = new ShadingColorMaterial( 0x0088FF, {alpha: 0.4 * Math.sqrt( density / Material.WATER.getDensity() )} );
                }
                else {
                    var green: uint = Math.round( MathUtil.scale( density, Material.WATER.getDensity(), DensityAndBuoyancyConstants.MAX_FLUID_DENSITY, 0x88, 0x33 ) );
                    var blue: uint = Math.round( MathUtil.scale( density, Material.WATER.getDensity(), DensityAndBuoyancyConstants.MAX_FLUID_DENSITY, 0xFF, 0x33 ) );
                    var alpha: Number = MathUtil.scale( density, Material.WATER.getDensity(), DensityAndBuoyancyConstants.MAX_FLUID_DENSITY, 0.4, 0.8 );
                    waterMaterial = new ShadingColorMaterial( uint( (green << 8) + blue ), {alpha: alpha} );
                }
            }
            waterFront.material = waterMaterial;
            waterTop.material = waterMaterial;
        } );

        // add grass, earth and pool inside
        groundNode = new GroundNode( _model );
        mainViewport.scene.addChild( groundNode );

        mainViewport.addLight();
        overlayViewport.addLight();

        // debugging marker
        marker = new ObjectContainer3D();
        marker.addChild( new Cube( { z: 50, width: 20, height: 20, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial( 0x9999CC ) } ) );
        marker.addChild( new Cube( { z: 150, width: 20, height: 20, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial( 0xCC9999 ) } ) );
        marker.addChild( new Cube( { z: -50, width: 5, height: 500, depth: 100, segmentsW: 1, segmentsH: 10, material: new ShadingColorMaterial( 0xFFFFFF ) } ) );
        //        scene.addChild(marker);//For debugging
    }

    //REVIEW doc - called by the model when an object is added
    private function addDensityObject( densityObject: DensityObject ): void {
        const densityObjectNode: DensityObjectNode = createDensityObjectNode( densityObject );
        mainViewport.scene.addChild( densityObjectNode );
        densityObjectNode.addOverlayObjects();
    }

    //REVIEW doc - called by the model when an object is added
    public function removeDensityObject( densityObjectNode: DensityObjectNode ): void {
        mainViewport.scene.removeChild( densityObjectNode );
        densityObjectNode.removeOverlayObjects();
    }

    //REVIEW doc - called by the model when an object is created
    protected function createDensityObjectNode( densityObject: DensityObject ): DensityObjectNode {
        return densityObject.createNode( this, massReadoutsVisible );
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

    //REVIEW doc - what does this do, what's the algorithm used?
    /**
     * @param m A 3D mesh of points in view space
     * @return The median point of the front of the mesh, in _screen_ coordinates
     */
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
            var sv: ScreenVertex = mainCamera.screen( m, v );
            kx += sv.x;
            ky += sv.y;
            kz += sv.z;
        }
        return new Number3D( kx / num, ky / num, kz / num );
    }

    //REVIEW doc
    public function onEnterFrame( event: Event ): void {
        if ( !_running ) {
            return;
        }

        // run the physics
        _model.stepFrame();

        // if we are dragging an object, set its position
        if ( moving && selectedObject is Pickable ) {
            var pickable: Pickable = (selectedObject as Pickable);
            pickable.getBody().SetXForm( new b2Vec2( pickable.getBody().GetPosition().x, cachedY ), 0 );
            pickable.getBody().SetLinearVelocity( new b2Vec2( 0, 0 ) );
            pickable.updateGeometry();
        }

        // update the water display to the model
        waterFront.y = (-_model.getPoolHeight() + _model.getWaterHeight() / 2) * DensityAndBuoyancyModel.DISPLAY_SCALE;
        waterFront.height = _model.getWaterHeight() * DensityAndBuoyancyModel.DISPLAY_SCALE;//this is positive from the bottom of the pool
        waterTop.y = (-_model.getPoolHeight() + _model.getWaterHeight()) * DensityAndBuoyancyModel.DISPLAY_SCALE;

        updateWaterVolumeIndicater();

        // render and fire listeners
        mainViewport.view.render();
        overlayViewport.view.render();
        for each ( var listener: Function in renderListeners ) {
            listener();
        }
        renderedOnce = true;
    }

    private function updateWaterVolumeIndicater(): void {
        if ( renderedOnce ) {
            var screenVertex: ScreenVertex = mainCamera.screen( groundNode,
                                                                new Vertex( _model.getPoolWidth() * DensityAndBuoyancyModel.DISPLAY_SCALE / 2,
                                                                            (-_model.getPoolHeight() + _model.getWaterHeight()) * DensityAndBuoyancyModel.DISPLAY_SCALE,
                                                                            0 ) );
            waterVolumeIndicator.x = screenVertex.x + mainViewport.view.x;
            waterVolumeIndicator.y = screenVertex.y + mainViewport.view.y;
            waterVolumeIndicator.visible = true;//Now can show the water volume indicator after it is at the right location
        }
        waterVolumeIndicator.setWaterHeight( _model.getWaterHeight() );
    }

    /**
     * @return Whether the object can be selected by the mouse and moved
     */
    private function canCurrentlyMoveObject( ob: Object3D ): Boolean {
        return ob is Pickable && (ob as Pickable).isPickableProperty().value;
    }

    public function onMouseDown( event: MouseEvent ): void {
        // record the start position of the drag, so we can calculate offsets and figure out the correct 3d position
        startMouseX = stage.mouseX - mainViewport.view.x;
        startMouseY = stage.mouseY - mainViewport.view.y;

        if ( canCurrentlyMoveObject( mainViewport.view.mouseObject ) ) {
            moving = true;
            startMiddle = medianFrontScreenPoint( mainViewport.view.mouseObject as AbstractPrimitive );
            selectedObject = mainViewport.view.mouseObject as AbstractPrimitive;
            if ( selectedObject is Pickable ) {
                const pickable: Pickable = (selectedObject as Pickable);
                cachedY = pickable.getBody().GetPosition().y;
                pickable.densityObjectNode.mousePressed = true;
            }
        }
        stage.addEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
    }

    public function get mainCamera(): Camera3D {
        return mainViewport.camera;
    }

    /**
     * Drag an object if one has been grabbed.
     * @param event
     */
    public function onMouseMove( event: MouseEvent ): void {
        if ( moving ) {
            // get the starting mouse offset
            var offsetX: Number = startMiddle.x - startMouseX;
            var offsetY: Number = startMiddle.y - startMouseY;

            // get the mouse screen coordinates from our viewport origin
            var mX: Number = stage.mouseX - mainViewport.view.x;
            var mY: Number = stage.mouseY - mainViewport.view.y;

            // where the block should be dragged to, in screen coordinates
            var screenCubeCenterX: Number = mX + offsetX;
            var screenCubeCenterY: Number = mY + offsetY;

            // get a vector that points in the direction to the block front-center. shouldn't matter if this is normalized
            var projected: Number3D = mainCamera.unproject( screenCubeCenterX, screenCubeCenterY );

            // add in the camera offset
            projected.add( projected, new Number3D( mainCamera.x, mainCamera.y, mainCamera.z ) );

            // get our camera and camera + ray vertices. these are on the line from the camera to our ideal block location
            var cameraVertex: Vertex = new Vertex( mainCamera.x, mainCamera.y, mainCamera.z );
            var rayVertex: Vertex = new Vertex( projected.x, projected.y, projected.z );

            // construct the plane where blocks are in 3D. this is somewhat ambiguous what z-depth it has, but it seems to work
            // TODO: potentially improve the z-depth here for better drag support
            var cubePlane: Plane3D = new Plane3D();
            cubePlane.fromNormalAndPoint( new Number3D( 0, 0, -1 ), new Number3D( 0, 0, -100 ) );

            // intersect our ray and the plane. this is the 3D point our block should be in
            var intersection: Vertex = cubePlane.getIntersectionLine( cameraVertex, rayVertex );

            // set the view object's position. this will forward through to our model.
            if ( selectedObject is Pickable ) {
                var pickable: Pickable = selectedObject as Pickable;
                pickable.setPosition( intersection.x, intersection.y );
                cachedY = pickable.getBody().GetPosition().y;
            }

            // for debugging purposes, put our marker at this location. this helps identify the 3D point that we are pointing at
            marker.x = intersection.x;
            marker.y = intersection.y;
            marker.z = intersection.z;
        }
    }

    public function onMouseUp( event: MouseEvent ): void {
        moving = false;
        stage.removeEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
        if ( selectedObject is Pickable ) {
            (selectedObject as Pickable).densityObjectNode.mousePressed = false;
        }
    }

    //REVIEW doc - when is this called?
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

    //REVIEW start what? the animation clock?
    public function start(): void {
        _running = true;
    }

    public function resetAll(): void {
        model.reset();
        _running = true;
        if ( moving ) {
            moving = false;
            stage.removeEventListener( Event.MOUSE_LEAVE, onStageMouseLeave );
        }
        massReadoutsVisible.reset();
    }

    public function get model(): DensityAndBuoyancyModel {
        return _model;
    }

    public function get container(): AbstractDBContainer {
        throw new AbstractMethodError();
    }

    public function get units(): Units {
        return _units;
    }

    public function get running(): Boolean {
        return _running;
    }

    public function set running( b: Boolean ): void {
        _running = b;
    }

    public function setMassReadoutsVisible( selected: Boolean ): void {
        massReadoutsVisible.value = selected;
    }

    public function addRenderListener( updateGraphics: Function ): void {
        renderListeners.push( updateGraphics );
    }
}
}