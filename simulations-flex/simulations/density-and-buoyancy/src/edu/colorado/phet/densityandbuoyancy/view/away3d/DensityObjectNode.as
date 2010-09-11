package edu.colorado.phet.densityandbuoyancy.view.away3d {
import edu.colorado.phet.densityandbuoyancy.view.*;

import Box2D.Dynamics.b2Body;

import away3d.containers.ObjectContainer3D;

import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;

import edu.colorado.phet.densityandbuoyancy.view.away3d.ArrowNode;

import edu.colorado.phet.densityandbuoyancy.view.away3d.Pickable;

import flash.text.TextFormat;

public class DensityObjectNode extends ObjectContainer3D implements Pickable {
    private var densityObject:DensityObject;
    private static var numArrowNodes:Number = 0;


    private var _view:AbstractDensityModule;

    protected var textReadout:TextFieldMesh;

    public function DensityObjectNode( densityObject:DensityObject, view:AbstractDensityModule ) {
        super();
        this.densityObject = densityObject;
        this._view = view;
        densityObject.getYProperty().addListener( updateGeometry );
        densityObject.getXProperty().addListener( updateGeometry );
        densityObject.addRemovalListener( remove );

        this.textReadout = new TextFieldMesh( "hello", createLabelTextFormat() );
        addChild(textReadout);
    }

    public function get view():AbstractDensityModule {
        return _view;
    }

    //Override to specify the depth of the object so arrows will render just outside of the object
    public function getArrowOriginZ():Number {
        return 0;
    }

    public function addArrowNode( arrowNode:ArrowNode ):void {
        numArrowNodes = numArrowNodes + 1;
        arrowNode.z = -getArrowOriginZ() * DensityModel.DISPLAY_SCALE - 1E-6 * numArrowNodes;//Offset so they don't overlap in z
        addChild( arrowNode );
    }

    public function getDensityObject():DensityObject {
        return densityObject;
    }

    public function remove():void {
        view.removeObject( this );
    }

    public function setPosition( x:Number, y:Number ):void {
        throw new Error( "Abstract method error" );
    }

    public function getBody():b2Body {
        throw new Error( "Abstract method error" );
    }

    public function updateGeometry():void {
        throw new Error( "Abstract method error" );
    }

    protected function setReadoutText( str : String ):void {
        textReadout.text = str;
    }

    protected function createLabelTextFormat():TextFormat {
        var format:TextFormat = new TextFormat();
        format.size = getFontReadoutSize();
        format.bold = true;
        format.font = "Arial";
        return format;
    }

    protected function getFontReadoutSize():Number {
        return 34;
    }
}
}