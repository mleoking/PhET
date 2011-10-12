// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.ByteBuffer;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;

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
    private final Terrain terrain;
    private final PlateTectonicsModule module;
    private TerrainNode.TerrainTextureImage image;

    //Textures taken from jME3-testdata.jar in Textures/Terrain/splat/ TODO: use our own textures!
    private static final String GRASS = "plate-tectonics/images/textures/grass.jpg";
    private static final String DIRT = "plate-tectonics/images/textures/dirt.jpg";
    private static final String ROAD = "plate-tectonics/images/textures/road.jpg";

    public TerrainNode( final Terrain terrain, PlateModel model, final PlateTectonicsModule module ) {
        this.terrain = terrain;
        this.module = module;

        Vector3f[] positions = computePositions( terrain );

        // use the gridded mesh to handle the terrain
        final GridMesh gridMesh = new GridMesh( terrain.numZSamples, terrain.numXSamples, positions );
        setMesh( gridMesh );

        // use a terrain-style texture. this allows us to blend between three different textures
        setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Terrain/Terrain.j3md" ) {{

            /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
            setTexture( "Alpha", new Texture2D() {{
                image = new TerrainTextureImage( terrain.numXSamples, terrain.numZSamples );
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

//            setBoolean( "useTriPlanarMapping", true );
        }} );

        model.modelChanged.addUpdateListener( new UpdateListener() {
                                                  public void update() {
                                                      gridMesh.updateGeometry( computePositions( terrain ) );
                                                      image.updateTerrain();
                                                  }
                                              }, false );
    }

    private Vector3f[] computePositions( Terrain terrain ) {
        Vector3f[] positions = new Vector3f[terrain.numXSamples * terrain.numZSamples];
        Vector3f[] xRadials = new Vector3f[terrain.numXSamples];
        Vector3f[] zRadials = new Vector3f[terrain.numZSamples];

        for ( int i = 0; i < terrain.numXSamples; i++ ) {
            xRadials[i] = PlateModel.getXRadialVector( terrain.xData[i] );
        }
        for ( int i = 0; i < terrain.numZSamples; i++ ) {
            zRadials[i] = PlateModel.getZRadialVector( terrain.zData[i] );
        }

        for ( int zIndex = terrain.numZSamples - 1; zIndex >= 0; zIndex-- ) {
            Vector3f zRadial = zRadials[zIndex];
            for ( int xIndex = 0; xIndex < terrain.numXSamples; xIndex++ ) {
                float elevation = terrain.getElevation( xIndex, zIndex );
                Vector3f cartesianModelVector = PlateModel.convertToRadial( xRadials[xIndex], zRadial, elevation );
                Vector3f viewVector = module.getModelViewTransform().modelToView( cartesianModelVector );
                positions[zIndex * terrain.numXSamples + xIndex] = viewVector;
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
            int numZSamples = terrain.numZSamples;
            for ( int z = 0; z < numZSamples; z++ ) {
                // since we don't care about data past this point, just zero it out
                if ( z >= height ) {
                    byte[] bytes = new byte[] { 0, 0, 0, 0 };
                    for ( int x = 0; x < width; x++ ) {
                        buffer.put( bytes );
                    }
                    continue;
                }
                for ( int x = 0; x < terrain.numXSamples; x++ ) {
                    double elevation = terrain.getElevation( x, z );
                    int stonyness = MathUtil.clamp( 0, (int) ( ( elevation - 3200 ) * ( 255f / 400f ) ), 255 ); // tree line at 3400 km
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
    }
}