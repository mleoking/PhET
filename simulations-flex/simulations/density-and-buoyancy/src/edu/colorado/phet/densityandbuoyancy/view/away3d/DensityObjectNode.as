package edu.colorado.phet.densityandbuoyancy.view.away3d {
import Box2D.Dynamics.b2Body;

import away3d.containers.ObjectContainer3D;

import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.*;

import flash.text.TextFormat;

public class DensityObjectNode extends ObjectContainer3D implements Pickable {
    private var densityObject: DensityObject;

    /**
     * the depth of the object so arrows will render just outside of the object
     * this is in the away3d scale
     */
    protected var frontZProperty: NumericProperty = new NumericProperty( "ZZZZ", "FakeUnits, FIX ME", 0 ); // TODO refactor so we can opt out of units

    private var _view: AbstractDensityModule;

    protected var textReadout: TextFieldMesh;

    public function DensityObjectNode( densityObject: DensityObject, view: AbstractDensityModule ) {
        super();
        this.densityObject = densityObject;
        this._view = view;
        densityObject.getYProperty().addListener( updateGeometry );
        densityObject.getXProperty().addListener( updateGeometry );
        densityObject.addRemovalListener( remove );

        this.textReadout = new TextFieldMesh( "hello", createLabelTextFormat() );
        addChild( textReadout );
    }

    public function get view(): AbstractDensityModule {
        return _view;
    }

    public function addArrowNode( arrowNode: ArrowNode ): void {
        var listener: Function = function(): void {
            arrowNode.z = frontZProperty.value - 1E-6 * arrowNode.offset;//Offset so they don't overlap in z
            trace( frontZProperty.value );
        };
        frontZProperty.addListener( listener );
        listener();
        addChild( arrowNode );
    }

    public function getDensityObject(): DensityObject {
        return densityObject;
    }

    public function remove(): void {
        view.removeObject( this );
    }

    public function setPosition( x: Number, y: Number ): void {
        throw new Error( "Abstract method error" );
    }

    public function getBody(): b2Body {
        throw new Error( "Abstract method error" );
    }

    public function updateGeometry(): void {
        throw new Error( "Abstract method error" );
    }

    protected function setReadoutText( str: String ): void {
        textReadout.text = str;
    }

    protected function createLabelTextFormat(): TextFormat {
        var format: TextFormat = new TextFormat();
        format.size = getFontReadoutSize();
        format.bold = true;
        format.font = "Arial";
        return format;
    }

    protected function getFontReadoutSize(): Number {
        return 34;
    }
}
}