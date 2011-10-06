// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.ByteBuffer;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;
import edu.colorado.phet.platetectonics.util.Grid3D;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;

/**
 * Displays the top terrain of a plate model, within the bounds of the specified grid
 */
public class TerrainNode extends Geometry {
    private GridMesh gridMesh;
    private final PlateModel model;
    private final PlateTectonicsModule module;
    private final Grid3D grid;
    private final Grid3D textureGrid;
    private TerrainNode.TerrainTextureImage image;
    private final int X_SAMPLES;
    private final int Z_SAMPLES;

    //Textures taken from jME3-testdata.jar in Textures/Terrain/splat/
    private static final String GRASS = "plate-tectonics/images/textures/grass.jpg";
    private static final String DIRT = "plate-tectonics/images/textures/dirt.jpg";
    private static final String ROAD = "plate-tectonics/images/textures/road.jpg";

    public TerrainNode( PlateModel model, final PlateTectonicsModule module, final Grid3D grid ) {
        this.model = model;
        this.module = module;
        this.grid = grid;

        // lower resolution grid for texture handling
        this.textureGrid = grid.withSamples( grid.getNumXSamples() / 2, grid.getNumYSamples() / 2, grid.getNumZSamples() / 2 );

        X_SAMPLES = grid.getNumXSamples();
        Z_SAMPLES = grid.getNumZSamples();
        Vector3f[] positions = computePositions( model );

        // use the gridded mesh to handle the terrain
        gridMesh = new GridMesh( Z_SAMPLES, X_SAMPLES, positions );
        setMesh( gridMesh );

        // use a terrain-style texture. this allows us to blend between three different textures
        setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Terrain/Terrain.j3md" ) {{

            /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
            setTexture( "Alpha", new Texture2D() {{
                image = new TerrainTextureImage( textureGrid.getNumXSamples(), textureGrid.getNumZSamples() );
                setImage( image );
            }} );

            /** 1.2) Add GRASS texture into the red layer (Tex1). */
            Texture grass = module.getAssetManager().loadTexture( GRASS );
            grass.setWrap( WrapMode.Repeat );
            setTexture( "Tex1", grass );
            setFloat( "Tex1Scale", 64f );

            /** 1.3) Add DIRT texture into the green layer (Tex2) */
            Texture dirt = module.getAssetManager().loadTexture( DIRT );
            dirt.setWrap( WrapMode.Repeat );
            setTexture( "Tex2", dirt );
            setFloat( "Tex2Scale", 32f );

            /** 1.4) Add ROAD texture into the blue layer (Tex3) */
            Texture rock = module.getAssetManager().loadTexture( ROAD );
            rock.setWrap( WrapMode.Repeat );
            setTexture( "Tex3", rock );
            setFloat( "Tex3Scale", 128f );
        }} );

        model.modelChanged.addUpdateListener( new UpdateListener() {
                                                  public void update() {
                                                      gridMesh.updateGeometry( computePositions( TerrainNode.this.model ) );
                                                      image.updateTerrain();
                                                  }
                                              }, false );
    }

    private Vector3f[] computePositions( PlateModel model ) {
        Vector3f[] positions = new Vector3f[X_SAMPLES * Z_SAMPLES];
        for ( int zIndex = 0; zIndex < Z_SAMPLES; zIndex++ ) {
            for ( int xIndex = 0; xIndex < X_SAMPLES; xIndex++ ) {
                float x = grid.getXSample( xIndex );
                float z = grid.getZSample( zIndex );
                float elevation = (float) model.getElevation( x, z );
                Vector3f modelVector = new Vector3f( x, elevation, z );
                Vector3f viewVector = module.getModelViewTransform().modelToView( modelVector );
                positions[zIndex * X_SAMPLES + xIndex] = viewVector;
            }
        }
        return positions;
    }

    /*---------------------------------------------------------------------------*
    * update the texture solely based on elevation
    *----------------------------------------------------------------------------*/
    private class TerrainTextureImage extends Image {
        public TerrainTextureImage( int width, int height ) {
            // TODO: simplify, and make it so that we actually use the entire texture space?
            // TODO: (performance) fix to use less texture space, and ideally less computation related to that
            super( Format.RGBA8, Math.max( width, height ), Math.max( width, height ), ByteBuffer.allocateDirect( 4 * Math.max( width, height ) * Math.max( width, height ) ) );
            updateTerrain();
        }

        // TODO: performance hotspot. ideally avoid by not having such a fine texture (coarser would be just fine!!!)
        public void updateTerrain() {
            ByteBuffer buffer = data.get( 0 );
            buffer.clear();
            int numZSamples = textureGrid.getNumZSamples();
            for ( int z = 0; z < numZSamples; z++ ) {
                // since we don't care about data past this point, just zero it out
                if ( z >= height ) {
                    byte[] bytes = new byte[] { 0, 0, 0, 0 };
                    for ( int x = 0; x < width; x++ ) {
                        buffer.put( bytes );
                    }
                    continue;
                }
                for ( int x = 0; x < width; x++ ) {
                    double elevation = getElevationAtPixel( x, z );
                    int stonyness = MathUtil.clamp( 0, (int) ( ( elevation - 15000 ) / 20 ) + 255, 255 ); // fully stony at 10km
                    int beachyness = MathUtil.clamp( 0, (int) ( -( elevation - 1500 ) / 3 ), 255 );
                    buffer.put( new byte[] {
                            (byte) ( 255 - stonyness - beachyness ), // grass
                            (byte) stonyness, // stone
                            (byte) beachyness, // beach (cobbles right now)
                            (byte) 1
                    } );
                }
            }
            setUpdateNeeded();
        }

        private double getElevationAtPixel( int xIndex, int zIndex ) {
            return model.getElevation( textureGrid.getXSample( xIndex ), textureGrid.getZSample( zIndex ) );
        }
    }
}