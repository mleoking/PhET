//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import away3d.cameras.Camera3D;
import away3d.core.base.Vertex;
import away3d.materials.ColorMaterial;

import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Vector2D;
import edu.colorado.phet.densityandbuoyancy.view.Away3DViewport;
import edu.colorado.phet.densityandbuoyancy.view.VectorValueNode;
import edu.colorado.phet.flexcommon.model.BooleanProperty;
import edu.colorado.phet.flexcommon.model.NumericProperty;

/**
 * Displays a 2D vector arrow as a 3D geometry in Away3D.
 */
public class ArrowNode extends MyMesh {
    private var _arrowModel: Vector2D;
    private const ARROW_HEIGHT: Number = 200;
    private const scaleFromModelToView: Number = 2;
    private var _visibilityProperty: BooleanProperty;
    private static var numArrowNodes: Number = 0;
    public var offset: Number;
    private var densityObject: DensityObject;
    private var tipVertex: Vertex;
    private var _arrowHeadLeftCornerVertex: Vertex;
    private var _arrowHeadRightCornerVertex: Vertex;
    private var _vectorValueNode: VectorValueNode;
    private var _color: *;
    private var offsetX: NumericProperty;
    private var right: Boolean;

    public function ArrowNode( densityObject: DensityObject, arrowModel: Vector2D, color: *, visibilityProperty: BooleanProperty, mainCamera: Camera3D, mainViewport: Away3DViewport, valueVisibilityProperty: BooleanProperty, offsetX: NumericProperty, right: Boolean ) {
        super( combine( {material:new ColorMaterial( color, {alpha: 0.75} )}, null ) );
        this._color = color;
        this.densityObject = densityObject;
        this.offsetX = offsetX;
        this.right = right;
        densityObject.getXProperty().addListener( updateLocation );
        densityObject.getYProperty().addListener( updateLocation );
        offset = numArrowNodes + 1;
        numArrowNodes += 1;

        this._arrowModel = arrowModel;
        this._visibilityProperty = visibilityProperty;
        this.mouseEnabled = false; // don't want to click on arrows, but instead the objects behind them
        arrowModel.addListener( doUpdate );
        doUpdate();

        function updateVisibility(): void {
            visible = visibilityProperty.value;
        }

        visibilityProperty.addListener( updateVisibility );
        updateVisibility();
        updateLocation();

        this._vectorValueNode = new VectorValueNode( mainCamera, this, mainViewport, valueVisibilityProperty, right );
        offsetX.addListener( updateLocation );
    }

    private function updateLocation(): void {
        this.x = densityObject.getX() * DensityAndBuoyancyModel.DISPLAY_SCALE + offsetX.value;
        this.y = densityObject.getY() * DensityAndBuoyancyModel.DISPLAY_SCALE;
    }

    public function doUpdate(): void {
        this.scaleY = _arrowModel.getMagnitude() / ARROW_HEIGHT * scaleFromModelToView;
        this.rotationZ = -_arrowModel.getAngle() * 180.0 / Math.PI;
    }

    override protected function build(): void {
        super.build();
        const width: Number = 100;
        const height: Number = ARROW_HEIGHT;
        const fractionUsedByArrowhead: Number = 0.25;
        const arrowHeadWidth: Number = 150;
        const arrowHeadHeight: Number = height * fractionUsedByArrowhead;
        const bodyHeight: Number = height - arrowHeadHeight;

        v( -width / 2, 0, 0 );
        v( width / 2, 0, 0 );
        v( width / 2, bodyHeight, 0 );
        v( -width / 2, bodyHeight, 0 );

        var arrowTipLeftIndex: Number = v( -arrowHeadWidth / 2, bodyHeight, 0 );
        var arrowTipRightIndex: Number = v( arrowHeadWidth / 2, bodyHeight, 0 );
        var arrowTipIndex: Number = v( 0, height, 0 );

        tipVertex = getVertexArray()[arrowTipIndex];
        trace( "tv = " + tipVertex );
        _arrowHeadLeftCornerVertex = getVertexArray()[arrowTipLeftIndex];
        _arrowHeadRightCornerVertex = getVertexArray()[arrowTipRightIndex];

        uv( 0, 0 );
        uv( 1, 1 - fractionUsedByArrowhead );
        uv( ( 1 + arrowHeadWidth / width) / 2, 0 );
        uv( ( 1 - arrowHeadWidth / width) / 2, 1 - fractionUsedByArrowhead );
        uv( 0, 1 - fractionUsedByArrowhead );
        uv( 1, 1 - fractionUsedByArrowhead );
        uv( 0.5, 1 );

        f( 0, 1, 2, 0, 1, 2 );
        f( 0, 2, 3, 0, 2, 3 );
        f( 3, 2, 6, 3, 2, 6 );
        f( 2, 5, 6, 2, 5, 6 );
        f( 4, 3, 6, 4, 3, 6 );

        type = "edu.colorado.phet.densityandbuoyancy.view.away3d.ArrowNode";
        url = "density";
    }

    public function get tip(): Vertex {
        return tipVertex;
    }

    public function get arrowModel(): Vector2D {
        return _arrowModel;
    }

    public function get visibilityProperty(): BooleanProperty {
        return _visibilityProperty;
    }

    public function get vectorValueNode(): VectorValueNode {
        return _vectorValueNode;
    }

    public function get color(): * {
        return _color;
    }

    public function get arrowHeadLeftCornerVertex(): Vertex {
        return _arrowHeadLeftCornerVertex;
    }

    public function get arrowHeadRightCornerVertex(): Vertex {
        return _arrowHeadRightCornerVertex;
    }

}
}