package edu.colorado.phet.densityandbuoyancy.view.away3d {
import Box2D.Dynamics.b2Body;

import away3d.containers.ObjectContainer3D;

import edu.colorado.phet.densityandbuoyancy.model.BooleanProperty;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.*;

public class DensityObjectNode extends ObjectContainer3D implements Pickable {
    private var densityObject: DensityObject;

    /**
     * the depth of the object so arrows will render just outside of the object
     * this is in the away3d scale
     */
    protected var frontZProperty: NumericProperty = new NumericProperty( "ZZZZ", "FakeUnits, FIX ME", 0 ); // TODO refactor so we can opt out of units

    private var _module: AbstractDensityModule;

    protected var densityObjectReadoutNode: DensityObjectReadoutNode;

    /**
     * Whether this object is currently movable (and thus pickable), or not
     */
    private var pickable: BooleanProperty = new BooleanProperty( true );
    private var arrowNodes: Array = new Array();

    public function DensityObjectNode( densityObject: DensityObject, module: AbstractDensityModule ) {
        super();
        this.densityObject = densityObject;
        this._module = module;
        densityObject.getYProperty().addListener( updateGeometry );
        densityObject.getXProperty().addListener( updateGeometry );
        densityObject.addRemovalListener( remove );

        densityObjectReadoutNode = new DensityObjectReadoutNode( densityObject, getFontReadoutSize() );
    }

    public function get module(): AbstractDensityModule {
        return _module;
    }

    public function addArrowNode( arrowNode: ArrowNode ): void {
        var listener: Function = function(): void {
            arrowNode.z = frontZProperty.value - 1E-6 * arrowNode.offset + z;//Offset so they don't overlap in z
            trace( frontZProperty.value );
        };
        frontZProperty.addListener( listener );
        listener();
        arrowNodes.push( arrowNode );
        module.overlayViewport.scene.addChild( arrowNode );
    }

    public function getDensityObject(): DensityObject {
        return densityObject;
    }

    public function remove(): void {
        module.removeDensityObject( this );
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
        densityObjectReadoutNode.setReadoutText( str );
    }

    protected function getFontReadoutSize(): Number {
        return 34;
    }

    public function isPickableProperty(): BooleanProperty {
        return pickable;
    }

    public function addOverlayObjects(): void {
        module.overlayViewport.scene.addChild( densityObjectReadoutNode.textReadout );
    }

    public function removeOverlayObjects(): void {
        module.overlayViewport.scene.removeChild( densityObjectReadoutNode.textReadout );
        for each ( var arrowNode: ArrowNode in arrowNodes ) {
            module.overlayViewport.scene.removeChild( arrowNode );
        }
        arrowNodes = new Array();
    }
}
}