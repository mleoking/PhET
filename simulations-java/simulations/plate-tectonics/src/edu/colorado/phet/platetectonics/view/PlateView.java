// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

/**
 * A view (node) that displays everything physical related to a plate model
 */
public class PlateView extends Node {
    private PlateModel model;
    private TerrainNode terrainNode;

    public PlateView( PlateModel model, final PlateTectonicsModule module ) {
        this.model = model;

        terrainNode = new TerrainNode( model, module );
        attachChild( terrainNode );

        // a quick water test
        final float waterWidth = PlateTectonicsConstants.X_SAMPLES / PlateTectonicsConstants.RESOLUTION;
        float waterHeight = PlateTectonicsConstants.Y_SAMPLES / PlateTectonicsConstants.RESOLUTION;
        attachChild( new Geometry( "Water", new Quad( waterWidth, waterHeight, true ) ) {{
            setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
//                setBoolean( "UseMaterialColors", true );

                setColor( "Color", new ColorRGBA( 0.2f, 0.6f, 0.9f, 0.5f ) );
//            setColor( "Diffuse", MoleculeShapesConstants.LONE_PAIR_SHELL_COLOR );
//                setFloat( "Shininess", 1f ); // [0,128]

                // allow transparency
                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                setTransparent( true );
            }} );
            setQueueBucket( Bucket.Transparent ); // allow it to be transparent
            setLocalTranslation( -waterWidth / 2, 0, 0 );
            rotate( -FastMath.PI / 2, 0, 0 );
        }} );
    }

    public void updateView() {
        terrainNode.updateView();
    }
}
