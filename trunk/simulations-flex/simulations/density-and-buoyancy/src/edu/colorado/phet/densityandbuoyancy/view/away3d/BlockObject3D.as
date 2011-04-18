//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import away3d.materials.*;

import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.model.Block;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.view.*;
import edu.colorado.phet.flexcommon.model.BooleanProperty;
import edu.colorado.phet.flexcommon.model.StringProperty;

import flash.display.Bitmap;
import flash.display.BitmapData;
import flash.geom.Rectangle;

import mx.core.BitmapAsset;

/**
 * BlockNode renders graphics and provides interactivity for Blocks.
 */
public class BlockObject3D extends CubeObject3D implements Pickable {

    private var block: Block;

    private var sideMaterial: BitmapMaterial;

    private var textureBitmap: Bitmap; // the texture being used (wood bitmap, wall (custom) bitmap, etc.)
    private var label: StringProperty;
    private var readoutFontScale: Number; //REVIEW doc

    public function BlockObject3D( block: Block, canvas: AbstractDensityAndBuoyancyPlayAreaComponent, label: StringProperty, massReadoutVisible: BooleanProperty, readoutFontScale: Number = 1 ): void {
        this.label = label;
        this.block = block;
        this.readoutFontScale = readoutFontScale;

        super( block, canvas );

        var cube: PickableCube = getCube();
        cube.cubeMaterials.back = cube.cubeMaterials.left = cube.cubeMaterials.right = cube.cubeMaterials.top = cube.cubeMaterials.bottom = cube.cubeMaterials.front = sideMaterial;
        addChild( cube );

        label.addListener( updateText );
        block.addMaterialListener( updateMaterial );
        block.addColorTransformListener( updateMaterial );

        updateMaterial();
        updateGeometry();

        massReadoutVisible.addListener( function (): void {
            densityObjectReadoutNode.visible = massReadoutVisible.value;
        } );
        densityObjectReadoutNode.visible = massReadoutVisible.value;
    }

    private function updateText(): void {
        setReadoutText( label.value );
        updateGeometry();
    }

    private function updateMaterial(): void {
        // update the bitmap we use as a background
        if ( block.material.isCustom() ) {
            textureBitmap = getCustomBitmap();
        }
        else {
            textureBitmap = block.material.textureBitmap;
        }

        // update the text field
        updateText();

        sideMaterial = new BitmapMaterial( textureBitmap.bitmapData );
        sideMaterial.alpha = block.material.alpha;

        // TODO: possibly change tiling for textures that are not symmetric
        var cube: PickableCube = getCube();
        cube.cubeMaterials.back = cube.cubeMaterials.left = cube.cubeMaterials.right = cube.cubeMaterials.top = cube.cubeMaterials.bottom = cube.cubeMaterials.front = sideMaterial;
    }

    //REVIEW doc
    private function getCustomBitmap(): Bitmap {
        var wallData: BitmapData = (new Material.customObjectTexture() as BitmapAsset).bitmapData;
        wallData.colorTransform( new Rectangle( 0, 0, wallData.width, wallData.height ), block.colorTransform );
        return new Bitmap( wallData );
    }

    public override function updateGeometry(): void {
        super.updateGeometry();
        densityObjectReadoutNode.x = getCube().x - getCube().width / 2 + x;
        densityObjectReadoutNode.y = getCube().y - getCube().height / 2 + y;
        densityObjectReadoutNode.z = getCube().z - getCube().depth / 2 - DensityAndBuoyancyConstants.FUDGE_FACTOR_DZ + z;
    }

    override protected function getFontReadoutSize(): Number {
        return super.getFontReadoutSize() * readoutFontScale;
    }
}
}