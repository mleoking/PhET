//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import Box2D.Dynamics.b2Body;

import away3d.containers.ObjectContainer3D;

import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyObject;
import edu.colorado.phet.densityandbuoyancy.view.*;
import edu.colorado.phet.flashcommon.AbstractMethodError;
import edu.colorado.phet.flexcommon.model.BooleanProperty;
import edu.colorado.phet.flexcommon.model.NumericProperty;

/**
 * Base class for deep hierarchy of 3D objects in the play area that can be moved by the user (including blocks and scales).
 */
public class DensityAndBuoyancyObjectNode extends ObjectContainer3D implements Pickable {
    private var densityObject: DensityAndBuoyancyObject;

    /**
     * the depth of the object so arrows will render just outside of the object
     * this is in the away3d scale
     */
    //REVIEW protected?
    //REVIEW don't understand the above comment or implications of the TODO
    public var frontZProperty: NumericProperty = new NumericProperty( "ZZZZ", "FakeUnits, FIX ME", 0 ); // TODO refactor so we can opt out of units

    private var _canvas: AbstractDensityAndBuoyancyPlayAreaComponent;

    protected var densityObjectReadoutNode: DensityObjectReadoutNode;

    /**
     * Whether this object is currently movable (and thus pickable), or not
     */
    private var pickable: BooleanProperty = new BooleanProperty( true );
    private var arrowNodes: Array = new Array();
    private var _mousePressed: Boolean = false;

    public function DensityAndBuoyancyObjectNode( densityObject: DensityAndBuoyancyObject, canvas: AbstractDensityAndBuoyancyPlayAreaComponent ) {
        super();
        this.densityObject = densityObject;
        this._canvas = canvas;
        densityObject.getYProperty().addListener( updateGeometry );
        densityObject.getXProperty().addListener( updateGeometry );
        densityObject.addRemovalListener( remove );

        densityObjectReadoutNode = new DensityObjectReadoutNode( densityObject, getFontReadoutSize() );
    }

    public function get canvas(): AbstractDensityAndBuoyancyPlayAreaComponent {
        return _canvas;
    }

    public function addArrowNode( arrowNode: ArrowNode ): void {
        var listener: Function = function(): void {
            arrowNode.z = frontZProperty.value - 1E-6 * arrowNode.offset + z;//Offset so they don't overlap in z
            trace( frontZProperty.value );
        };
        frontZProperty.addListener( listener );
        listener();
        arrowNodes.push( arrowNode );
        canvas.overlayViewport.scene.addChild( arrowNode );
    }

    public function getDensityObject(): DensityAndBuoyancyObject {
        return densityObject;
    }

    public function remove(): void {
        canvas.removeDensityObject( this );
    }

    public function setPosition( x: Number, y: Number ): void {
        throw new AbstractMethodError();
    }

    public function getBody(): b2Body {
        throw new AbstractMethodError();
    }

    public function updateGeometry(): void {
        throw new AbstractMethodError();
    }

    protected function setReadoutText( str: String ): void {
        densityObjectReadoutNode.setReadoutText( str );
    }

    protected function getFontReadoutSize(): Number {
        return 34;
    }

    public function isPickableProperty(): BooleanProperty {
        return pickable;
    }

    public function addOverlayObjects(): void {
        canvas.overlayViewport.scene.addChild( densityObjectReadoutNode.textReadout );
        for each ( var arrowNode: ArrowNode in arrowNodes ) {
            canvas.addChild( arrowNode.vectorValueNode );
        }
    }

    public function removeOverlayObjects(): void {
        canvas.overlayViewport.scene.removeChild( densityObjectReadoutNode.textReadout );
        for each ( var arrowNode: ArrowNode in arrowNodes ) {
            canvas.overlayViewport.scene.removeChild( arrowNode );
            canvas.removeChild( arrowNode.vectorValueNode );
        }
        arrowNodes = new Array();
    }

    public function get densityObjectNode(): DensityAndBuoyancyObjectNode {
        return this;
    }

    public function set mousePressed( b: Boolean ): void {
        this._mousePressed = b;
        densityObject.userControlled = b;
    }
}
}