// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.ByteBuffer;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;

/**
 * Displays the top terrain of a plate model
 */
public class TerrainNode extends Geometry {
    private final int xSamples;
    private final int zSamples;
    private GridMesh gridMesh;
    private final PlateModel model;
    private final PlateTectonicsModule module;
    private TerrainNode.TerrainTextureImage image;

    public TerrainNode( PlateModel model, final PlateTectonicsModule module ) {
        this.model = model;
        this.module = module;
        xSamples = PlateTectonicsConstants.X_SAMPLES;
        zSamples = PlateTectonicsConstants.Y_SAMPLES;
        Vector3f[] positions = computePositions( model );

        // use the gridded mesh to handle the terrain
        gridMesh = new GridMesh( zSamples, xSamples, positions );
        setMesh( gridMesh );

        // use a terrain-style texture. this allows us to blend between three different textures
        setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Terrain/Terrain.j3md" ) {{

            /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
            setTexture( "Alpha", new Texture2D() {{
                int width = xSamples;
                int height = zSamples;
                image = new TerrainTextureImage( width, height );
                setImage( image );
            }} );

            /** 1.2) Add GRASS texture into the red layer (Tex1). */
            Texture grass = module.getAssetManager().loadTexture( "Textures/Terrain/splat/grass.jpg" );
            grass.setWrap( WrapMode.Repeat );
            setTexture( "Tex1", grass );
            setFloat( "Tex1Scale", 64f );

            /** 1.3) Add DIRT texture into the green layer (Tex2) */
            Texture dirt = module.getAssetManager().loadTexture( "Textures/Terrain/splat/dirt.jpg" );
            dirt.setWrap( WrapMode.Repeat );
            setTexture( "Tex2", dirt );
            setFloat( "Tex2Scale", 32f );

            /** 1.4) Add ROAD texture into the blue layer (Tex3) */
            Texture rock = module.getAssetManager().loadTexture( "Textures/Terrain/splat/road.jpg" );
            rock.setWrap( WrapMode.Repeat );
            setTexture( "Tex3", rock );
            setFloat( "Tex3Scale", 128f );
        }} );
    }

    public void updateView() {
        // TODO: refactor to a listener on the model? or something else?
        gridMesh.updateGeometry( computePositions( model ) );
        image.updateTerrain();
    }

    private double getElevationAtPixel( int xIndex, int zIndex ) {
        return model.getElevation( getModelX( xIndex ), getModelZ( zIndex ) );
    }

    private Vector3f[] computePositions( PlateModel model ) {
        Vector3f[] positions = new Vector3f[xSamples * zSamples];
        for ( int zIndex = 0; zIndex < zSamples; zIndex++ ) {
            for ( int xIndex = 0; xIndex < xSamples; xIndex++ ) {
                float x = getModelX( xIndex );
                float z = getModelZ( zIndex );
                float elevation = (float) model.getElevation( x, z );
                Vector3f modelVector = new Vector3f( x, elevation, z );
                Vector3f viewVector = module.getModelViewTransform().modelToView( modelVector );
                positions[zIndex * xSamples + xIndex] = viewVector;
            }
        }
        return positions;
    }

    private float getModelX( float xIndex ) {
        // center our x samples, and apply the resolution
        return module.getModelViewTransform().viewToModelDeltaX( ( xIndex - ( (float) xSamples ) / 2 ) / PlateTectonicsConstants.RESOLUTION );
    }

    private float getModelZ( float zIndex ) {
        // z samples go into negative z, and apply the resolution
        return module.getModelViewTransform().viewToModelDeltaZ( ( zIndex - ( (float) zSamples ) ) / PlateTectonicsConstants.RESOLUTION );
    }

    /*---------------------------------------------------------------------------*
    * update the texture solely based on elevation
    *----------------------------------------------------------------------------*/
    private class TerrainTextureImage extends com.jme3.texture.Image {
        public TerrainTextureImage( int width, int height ) {
            // TODO: simplify, and make it so that we actually use the entire texture space?
            // TODO: (performance) fix to use less texture space, and ideally less computation related to that
            super( Format.RGBA8, Math.max( width, height ), Math.max( width, height ), ByteBuffer.allocateDirect( 4 * Math.max( width, height ) * Math.max( width, height ) ) );
            updateTerrain();
        }

        public void updateTerrain() {
            ByteBuffer buffer = data.get( 0 );
            buffer.clear();
            for ( int z = 0; z < height; z++ ) {
                for ( int x = 0; x < width; x++ ) {
                    // TODO: add in subsampling?
                    double elevation = getElevationAtPixel( x, z );
                    int stonyness = MathUtil.clamp( 0, (int) ( ( elevation - 10000 ) / 20 ) + 255, 255 ); // fully stony at 10km
                    int beachyness = MathUtil.clamp( 0, (int) ( -( elevation - 300 ) ), 255 );
                    buffer.put( (byte) ( 255 - stonyness - beachyness ) ); // grass
                    buffer.put( (byte) stonyness ); // stone
                    buffer.put( (byte) beachyness ); // beach (cobbles right now)
                    buffer.put( (byte) 1 );
                }
            }
            setUpdateNeeded();
        }
    }
}
