//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import away3d.materials.*;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.model.Scale;
import edu.colorado.phet.densityandbuoyancy.view.*;

/**
 * ScaleNode renders graphics and provides interactivity for 3D scales for measuring masses or weights.
 *
 * Is rendered with a base, the top (which would move in a real scale), and the stand (connection between the two)
 */
public class ScaleObject3D extends CuboidObject3D {

    private var _scale: Scale;
    private var _view: AbstractDensityAndBuoyancyPlayAreaComponent;

    private var base: PickableCube;
    private var top: PickableCube;
    private var stand: PickableCube;

    public function ScaleObject3D( scale: Scale, view: AbstractDensityAndBuoyancyPlayAreaComponent ): void {
        super( scale, view );
        this._scale = scale;
        this._view = view;

        var totalWidth: Number = getCuboid().getWidth() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        var totalHeight: Number = getCuboid().getHeight() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        var totalDepth: Number = getCuboid().getDepth() * DensityAndBuoyancyModel.DISPLAY_SCALE;

        base = new PickableCube( this );
        base.width = totalWidth;
        base.height = totalHeight / 2;
        base.depth = totalDepth;
        base.segmentsH = 2;
        base.segmentsW = 2;
        base.y = -totalHeight / 4;
        addChild( base );

        top = new PickableCube( this );
        top.width = totalWidth;
        top.height = totalHeight / 8;
        top.depth = totalDepth;
        top.segmentsH = 2;
        top.segmentsW = 2;
        top.y = 7 * totalHeight / 16;
        addChild( top );

        stand = new PickableCube( this );
        stand.width = totalWidth / 5;
        stand.height = totalHeight - base.height - top.height;
        stand.depth = totalDepth / 5;
        stand.y = base.y + base.height / 2 + stand.height / 2;
        addChild( stand );

        updateText();
        updateGeometry();

        var brightMaterial: ColorMaterial = new ColorMaterial( 0xFFFFFF );
        var sideMaterial: ColorMaterial = new ColorMaterial( 0xCCCCCC );
        var hidMaterial: ColorMaterial = new ColorMaterial( 0x999999 );

        base.cubeMaterials.left = base.cubeMaterials.right = base.cubeMaterials.top = base.cubeMaterials.bottom = base.cubeMaterials.front = sideMaterial;
        base.cubeMaterials.top = hidMaterial;

        base.cubeMaterials.back = sideMaterial;

        top.material = sideMaterial;
        top.cubeMaterials.back = brightMaterial;

        stand.material = sideMaterial;

        scale.addScaleReadoutListener( updateText );

        isPickableProperty().initialValue = _scale.getModel().scalesMovableProperty.value;
        _scale.getModel().scalesMovableProperty.addListener( function(): void {
            isPickableProperty().value = _scale.getModel().scalesMovableProperty.value;
        } );
    }

    public function updateText(): void {
        setReadoutText( _scale.getScaleReadout() );
    }

    override public function updateGeometry(): void {
        super.updateGeometry();
        densityObjectReadoutNode.x = base.x - base.width / 2 + x;
        densityObjectReadoutNode.y = base.y - base.height / 2 + y;
        densityObjectReadoutNode.z = base.z - base.depth / 2 - DensityAndBuoyancyConstants.FUDGE_FACTOR_DZ + z;
        updateText();
    }

    override protected function getFontReadoutSize(): Number {
        return 36;
    }

    //REVIEW doc - where is this object's origin, and where are we trying to position it?
    override public function setPosition( x: Number, y: Number ): void {
        // clamp the scale bounds manually because we set its mass to 0 while dragging which prevents intersection correction

        var scaleGroundHeight: Number = DensityAndBuoyancyModel.DISPLAY_SCALE * Scale.SCALE_HEIGHT / 2;
        var rightBound: Number = DensityAndBuoyancyModel.DISPLAY_SCALE * ((_view.model.getPoolWidth() - Scale.SCALE_WIDTH) / 2);
        var bottomBound: Number = DensityAndBuoyancyModel.DISPLAY_SCALE * (-_view.model.getPoolHeight() + Scale.SCALE_HEIGHT / 2);
        var poolSide: Number = (x > 0 ? 1 : -1) * rightBound;

        var belowGround: Boolean = y < scaleGroundHeight;
        var belowPool: Boolean = y < bottomBound;
        var offside: Boolean = Math.abs( x ) > rightBound;

        if ( !offside && belowPool ) {
            y = bottomBound;
        } else if ( offside && belowPool ) {
            y = bottomBound;
            x = poolSide;
        } else if ( offside && belowGround ) {
            if ( (Math.abs( x ) - rightBound) / -(y - scaleGroundHeight) < 1 ) {
                // if we are closer to the side of the pool than the top of the ground
                x = poolSide;
            }
            else {
                y = scaleGroundHeight;
            }
        }
        super.setPosition( x, y );
    }
}
}