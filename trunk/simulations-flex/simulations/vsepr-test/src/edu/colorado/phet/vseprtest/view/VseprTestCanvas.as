//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.vseprtest.view {
import away3d.containers.ObjectContainer3D;
import away3d.core.math.Number3D;
import away3d.materials.PhongColorMaterial;
import away3d.materials.PhongColorMaterialCache;
import away3d.materials.ShadingColorMaterial;
import away3d.primitives.Cylinder;
import away3d.primitives.Sphere;

import edu.colorado.phet.flashcommon.ApplicationLifecycle;

import flash.events.Event;

import mx.containers.Canvas;
import mx.controls.HSlider;
import mx.controls.Label;
import mx.core.UIComponent;

public class VseprTestCanvas extends Canvas {

    private var background: Canvas = new Canvas();
    private var context: Away3DContext = new Away3DContext();
    private var finishedLoading: Boolean = false;
    private var moleculeHolder: ObjectContainer3D = new ObjectContainer3D();

    private var spheres: Array = new Array();

    public function VseprTestCanvas() {
        super();
        percentWidth = 100;
        percentHeight = 100;
        setStyle( "fontSize", 12 );

        const initialSegments: Number = 6;

        background.percentWidth = 100;
        background.percentHeight = 100;
        addChild( background );

        const bondSegmentsW: Number = 8;

        function createSphere( color: int, x: Number = 0, y: Number = 0, z: Number = 0 ): Sphere {
            return new Sphere( {
                                   x:x,
                                   y:y,
                                   z:z,
                                   radius:atomRadius,
                                   segmentsW:initialSegments + 2,
                                   segmentsH:initialSegments,
                                   material:new PhongColorMaterial( color )}
            );
        }

        // add center atom
        var atomRadius: Number = 50;
        var mainAtom: Sphere = createSphere( 0x999999 );
        spheres.push( mainAtom );
        moleculeHolder.addChild( mainAtom );

        function addBondedAtom( dir: Number3D ): void {
            var holder: ObjectContainer3D = new ObjectContainer3D();

            // we build it on the Y-axis, then rotate
            var buildAxis: Number3D = new Number3D( 0, 1, 0 );

            // distance from center of middle atom to other atom
            var length: Number = dir.distance( new Number3D( 0, 0, 0 ) );

            var bondRadius: Number = 15;

            // how far a bond should extend past the tip of the atom-sphere to precisely "contact" the sphere
            var fitCompensationDistance: Number = atomRadius * (1 - Math.sqrt( 1 - Math.pow( bondRadius / atomRadius, 2 ) ));

            trace( "fitCompensationDistance: " + fitCompensationDistance );

            // length of displayed bond
            var displayedBondLength: Number = length + 2 * (fitCompensationDistance - atomRadius);

            // NOTE: cylinder needs us to pass double-height?
            holder.addChild( new Cylinder( {y:length / 2,openEnded:true,segmentsW:bondSegmentsW,radius: bondRadius, height: displayedBondLength * 2, material: new PhongColorMaterial( 0xFF0000, {} )} ) );
            moleculeHolder.addChild( holder );

            // argh mutability. This is: a.normalized.cross( b ), but they wanted it to take up a bunch of lines
            var crossProduct: Number3D = new Number3D();
            var normalized: Number3D = new Number3D();
            normalized.clone( dir );
            normalized.normalize();
            crossProduct.cross( dir, buildAxis ); // compute it

            holder.rotate( crossProduct, dir.getAngle( buildAxis ) * 180 / Math.PI ); // getAngle is radians, parameter needs degrees

            var otherAtom: Sphere = createSphere( 0xFFFFFF, dir.x, dir.y, dir.z );
            spheres.push( otherAtom );
            moleculeHolder.addChild( otherAtom );
        }

        addBondedAtom( new Number3D( 200, 0, 0 ) ); // right
        addBondedAtom( new Number3D( -200, 0, 0 ) ); // left
        function computePositionOf5( n: Number ): Number3D {
            return new Number3D( 0, 200 * Math.cos( 2 * Math.PI * (n / 5.0) ), 200 * Math.sin( 2 * Math.PI * (n / 5.0) ) )
        }

        addBondedAtom( computePositionOf5( 0 ) ); // up
        addBondedAtom( computePositionOf5( 1 ) ); // up
        addBondedAtom( computePositionOf5( 2 ) ); // up
        addBondedAtom( computePositionOf5( 3 ) ); // up
        addBondedAtom( computePositionOf5( 4 ) ); // up

        context.init();
        context.scene.addChild( moleculeHolder );
        context.addLight();

        var holder: UIComponent = new UIComponent();
        holder.addChild( context.view );
        addChild( holder );


        // listeners
        addEventListener( Event.ENTER_FRAME, onEnterFrame );
        addEventListener( Event.RESIZE, onResize );

        // we listen to the background, since it is set to 100% width and height, and once this component is added the resize will be called
        background.addEventListener( Event.RESIZE, function( evt: Event ): void {
            onResize( evt );
        } );

        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
            finishedLoading = true;
        } );

        var qualityLabel: Label = new Label();
        qualityLabel.text = "Quality";
        qualityLabel.setStyle( "color", 0xFFFFFF );
        addChild( qualityLabel );
        var slider: HSlider = new HSlider();
        slider.minimum = 6;
        slider.maximum = 20;
        slider.value = 6;
        slider.snapInterval = 1;
        slider.x = 50;
        addChild( slider );
        slider.addEventListener( Event.CHANGE, function( evt: Event ): void {
            for each ( var sphere: Sphere in spheres ) {
                sphere.segmentsW = slider.value + 2;
                sphere.segmentsH = slider.value;
            }
        } );

//        ApplicationLifecycle.addApplicationCompleteListener( function(): void {
//            var flexCommon: FlexCommon = new FlexCommon();
//            flexCommon.initialize( holder );
//        } );
    }

    public function onEnterFrame( event: Event ): void {
        if ( finishedLoading ) {
            moleculeHolder.rotate( new Number3D( 0, 1, 0 ), 5 );
            context.view.render();
        }
    }

    public function onResize( event: Event ): void {
        background.graphics.beginFill( 0x000000 );
        background.graphics.drawRect( 0, 0, background.width, background.height );
        context.view.x = background.width / 2;
        context.view.y = background.height / 2;
    }

}
}