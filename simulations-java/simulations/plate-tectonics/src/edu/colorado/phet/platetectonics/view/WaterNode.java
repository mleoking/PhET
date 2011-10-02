// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;
import edu.colorado.phet.platetectonics.util.Grid3D;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class WaterNode extends Node {
    private final PlateModel model;
    private final PlateTectonicsModule module;
    private final Grid3D grid;

    public WaterNode( PlateModel model, PlateTectonicsModule module, Grid3D grid ) {
        this.model = model;
        this.module = module;
        this.grid = grid;

        // a quick water test
        // TODO: transformations are too verbose. way to have "X.toViewDeltaX( bounds.width )" ?
        final float waterWidth = module.getModelViewTransform().modelToViewDeltaX( grid.getBounds().getWidth() );
        final float waterTopHeight = module.getModelViewTransform().modelToViewDeltaX( grid.getBounds().getDepth() );
        final float waterSideHeight = -module.getModelViewTransform().modelToViewDeltaY( grid.getBounds().getMinY() );
        final float waterLeftX = module.getModelViewTransform().modelToViewDeltaX( grid.getBounds().getMinX() );

        final Material waterMaterial = new Material( module.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
//                setBoolean( "UseMaterialColors", true );

            setColor( "Color", new ColorRGBA( 0.2f, 0.6f, 0.9f, 0.5f ) );
//            setColor( "Diffuse", MoleculeShapesConstants.LONE_PAIR_SHELL_COLOR );
//                setFloat( "Shininess", 1f ); // [0,128]

            // allow transparency
            getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
            setTransparent( true );
        }};

        attachChild( new Geometry( "Water Top", new Quad( waterWidth, waterTopHeight, true ) ) {{
            setMaterial( waterMaterial );
            setQueueBucket( Bucket.Transparent ); // allow it to be transparent
            setLocalTranslation( waterLeftX, 0, 0 );
            rotate( -FastMath.PI / 2, 0, 0 );
        }} );

        attachChild( new Geometry( "Water Front", new WaterFrontMesh() ) {{
            setMaterial( waterMaterial );
            setQueueBucket( Bucket.Transparent ); // allow it to be transparent
            setLocalTranslation( waterLeftX, -waterSideHeight, -0.01f );
        }} );
    }

    private class WaterFrontMesh extends Mesh {
        public WaterFrontMesh() {
            // TODO: update front water mesh at each timestep!
        }
    }
}
