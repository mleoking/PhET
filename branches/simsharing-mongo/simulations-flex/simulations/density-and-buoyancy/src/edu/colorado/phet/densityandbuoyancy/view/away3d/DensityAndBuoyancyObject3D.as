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
public class DensityAndBuoyancyObject3D extends ObjectContainer3D implements Pickable {
    private var densityObject: DensityAndBuoyancyObject;

    // TODO refactor so we can opt out of units
    public var frontZProperty: NumericProperty = new NumericProperty( "ZZZZ", "FakeUnits, FIX ME", 0 );//Front of the object (in away3d coordinates) so objects can be placed in front of it

    private var _canvas: AbstractDensityAndBuoyancyPlayAreaComponent;

    protected var densityObjectReadoutNode: DensityObjectReadout;

    /**
     * Whether this object is currently movable (and thus pickable), or not
     */
    private var pickable: BooleanProperty = new BooleanProperty( true );
    private var arrowNodes: Array = new Array();
    private var _mousePressed: Boolean = false;

    public function DensityAndBuoyancyObject3D( densityObject: DensityAndBuoyancyObject, canvas: AbstractDensityAndBuoyancyPlayAreaComponent ) {
        super();
        this.densityObject = densityObject;
        this._canvas = canvas;
        densityObject.getYProperty().addListener( updateGeometry );
        densityObject.getXProperty().addListener( updateGeometry );
        densityObject.addRemovalListener( remove );

        densityObjectReadoutNode = new DensityObjectReadout( densityObject, getFontReadoutSize() );
    }

    public function get canvas(): AbstractDensityAndBuoyancyPlayAreaComponent {
        return _canvas;
    }

    public function addArrowNode( arrowNode: ArrowMesh ): void {
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
        for each ( var arrowNode: ArrowMesh in arrowNodes ) {
            canvas.addChild( arrowNode.vectorValueNode );
        }
    }

    public function removeOverlayObjects(): void {
        canvas.overlayViewport.scene.removeChild( densityObjectReadoutNode.textReadout );
        for each ( var arrowNode: ArrowMesh in arrowNodes ) {
            canvas.overlayViewport.scene.removeChild( arrowNode );
            canvas.removeChild( arrowNode.vectorValueNode );
        }
        arrowNodes = new Array();
    }

    public function get densityObjectNode(): DensityAndBuoyancyObject3D {
        return this;
    }

    public function set mousePressed( b: Boolean ): void {
        this._mousePressed = b;
        densityObject.userControlled = b;
    }
}
}