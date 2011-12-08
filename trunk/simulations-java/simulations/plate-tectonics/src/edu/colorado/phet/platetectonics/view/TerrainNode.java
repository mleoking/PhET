// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.BufferUtils;

/**
 * Displays the top terrain of a plate model, within the bounds of the specified grid
 */
public class TerrainNode extends Geometry {
    private final PlateTectonicsTab module;
//    private TerrainNode.TerrainTextureImage image;

    //Textures taken from jME3-testdata.jar in Textures/Terrain/splat/ TODO: use our own textures!
    private static final String GRASS = "plate-tectonics/images/textures/grass.jpg";
    private static final String DIRT = "plate-tectonics/images/textures/dirt.jpg";
    private static final String ROAD = "plate-tectonics/images/textures/road.jpg";
    private FloatBuffer texture2;

    public TerrainNode( final Terrain terrain, PlateModel model, final PlateTectonicsTab module ) {
        this.module = module;

        Vector3f[] positions = computePositions( terrain );
        texture2 = BufferUtils.createFloatBuffer( terrain.numXSamples * terrain.numZSamples * 2 );

        // use the gridded mesh to handle the terrain
        final GridMesh gridMesh = new GridMesh( terrain.numZSamples, terrain.numXSamples, positions );
        updateSphericalCoordinates( gridMesh, terrain );
        updateHeightmap( terrain, gridMesh );
        setMesh( gridMesh );

        // use a terrain-style texture. this allows us to blend between three different textures
        setMaterial( new Material( module.getAssetManager(), "plate-tectonics/materials/Surface.j3md" ) {{
            setFloat( "TextureScale", 1 / 5000f );

            /** 1.2) Add GRASS texture into the red layer (Tex1). */
            Texture grass = module.getAssetManager().loadTexture( GRASS );
            grass.setWrap( WrapMode.Repeat );
            setTexture( "Tex1", grass );

            /** 1.3) Add DIRT texture into the green layer (Tex2) */
            Texture dirt = module.getAssetManager().loadTexture( DIRT );
            dirt.setWrap( WrapMode.Repeat );
            setTexture( "Tex2", dirt );

            /** 1.4) Add ROAD texture into the blue layer (Tex3) */
            Texture rock = module.getAssetManager().loadTexture( ROAD );
            rock.setWrap( WrapMode.Repeat );
            setTexture( "Tex3", rock );

//            setBoolean( "useTriPlanarMapping", true );
//            getAdditionalRenderState().setFaceCullMode( FaceCullMode.Off );
        }} );

        model.modelChanged.addUpdateListener( new UpdateListener() {
                                                  public void update() {
                                                      gridMesh.updateGeometry( computePositions( terrain ) );
                                                      updateSphericalCoordinates( gridMesh, terrain );
                                                      updateHeightmap( terrain, gridMesh );
                                                  }
                                              }, false );
    }

    // put our (constant) x and z values into the TexCoord mesh buffer. this is only needed on startup
    private void updateSphericalCoordinates( GridMesh gridMesh, Terrain terrain ) {
        int vertexCount = terrain.numXSamples * terrain.numZSamples;
        FloatBuffer texture1 = BufferUtils.createFloatBuffer( vertexCount * 2 );

        for ( int zIndex = 0; zIndex < terrain.numZSamples; zIndex++ ) {
            float z = terrain.zData[zIndex];
            for ( int xIndex = 0; xIndex < terrain.numXSamples; xIndex++ ) {
                float x = terrain.xData[xIndex];
                texture1.put( new float[] { x, z } ); // store x and z coordinates in the main texture
            }
        }

        gridMesh.getBuffer( Type.TexCoord ).updateData( texture1 );
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

    // put our elevation information into the x coordinate of the TexCoord2 mesh buffer
    private void updateHeightmap( Terrain terrain, GridMesh gridMesh ) {
        // also update the texture height-map whenever this is done
        texture2.clear();
        for ( int zIndex = 0; zIndex < terrain.numZSamples; zIndex++ ) {
            for ( int xIndex = 0; xIndex < terrain.numXSamples; xIndex++ ) {
                texture2.put( new float[] { terrain.getElevation( xIndex, zIndex ), 0 } ); // store elevation in the other texture
            }
        }

        // update (or set) the buffer
        VertexBuffer buffer = gridMesh.getBuffer( Type.TexCoord2 );
        if ( buffer == null ) {
            gridMesh.setBuffer( VertexBuffer.Type.TexCoord2, 2, texture2 );
        }
        else {
            buffer.updateData( texture2 );
        }
    }
}