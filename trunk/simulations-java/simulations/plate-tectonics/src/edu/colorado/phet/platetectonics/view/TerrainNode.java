// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.shapes.GridMesh;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.ColorMaterial;
import edu.colorado.phet.platetectonics.util.LWJGLModelViewTransform;

import static org.lwjgl.opengl.GL11.*;

/**
 * Displays the top terrain of a plate model, within the bounds of the specified grid
 */
public class TerrainNode extends GridMesh {
    private final PlateTectonicsTab module;
//    private TerrainNode.TerrainTextureImage image;

    //Textures taken from jME3-testdata.jar in Textures/Terrain/splat/ TODO: use our own textures!
    private static final String GRASS = "plate-tectonics/images/textures/grass.jpg";
    private static final String DIRT = "plate-tectonics/images/textures/dirt.jpg";
    private static final String ROAD = "plate-tectonics/images/textures/road.jpg";
    private FloatBuffer texture2;

    public TerrainNode( final Terrain terrain, PlateModel model, final PlateTectonicsTab module ) {
        super( terrain.numZSamples, terrain.numXSamples, computePositions( terrain, module.getModelViewTransform() ) );
        this.module = module;

        texture2 = BufferUtils.createFloatBuffer( terrain.numXSamples * terrain.numZSamples * 2 );

        // use the gridded mesh to handle the terrain
        final GridMesh gridMesh = new GridMesh( terrain.numZSamples, terrain.numXSamples, positions );
//        updateSphericalCoordinates( gridMesh, terrain );
//        updateHeightmap( terrain, gridMesh );

        // TODO: add in terrain texture
        setMaterial( new ColorMaterial( 0.7f, 0.65f, 0.45f, 1 ) );

        // use a terrain-style texture. this allows us to blend between three different textures
//        setMaterial( new Material( module.getAssetManager(), "plate-tectonics/materials/Surface.j3md" ) {{
//            setFloat( "TextureScale", 1 / 5000f );
//
//            /** 1.2) Add GRASS texture into the red layer (Tex1). */
//            Texture grass = module.getAssetManager().loadTexture( GRASS );
//            grass.setWrap( WrapMode.Repeat );
//            setTexture( "Tex1", grass );
//
//            /** 1.3) Add DIRT texture into the green layer (Tex2) */
//            Texture dirt = module.getAssetManager().loadTexture( DIRT );
//            dirt.setWrap( WrapMode.Repeat );
//            setTexture( "Tex2", dirt );
//
//            /** 1.4) Add ROAD texture into the blue layer (Tex3) */
//            Texture rock = module.getAssetManager().loadTexture( ROAD );
//            rock.setWrap( WrapMode.Repeat );
//            setTexture( "Tex3", rock );
//
////            setBoolean( "useTriPlanarMapping", true );
////            getAdditionalRenderState().setFaceCullMode( FaceCullMode.Off );
//        }} );

        model.modelChanged.addUpdateListener( new UpdateListener() {
                                                  public void update() {
                                                      gridMesh.updateGeometry( computePositions( terrain, module.getModelViewTransform() ) );
//                                                      updateSphericalCoordinates( gridMesh, terrain );
//                                                      updateHeightmap( terrain, gridMesh );
                                                  }
                                              }, false );
    }

    // put our (constant) x and z values into the TexCoord mesh buffer. this is only needed on startup
    // TODO: use this for shading the texture
//    private void updateSphericalCoordinates( GridMesh gridMesh, Terrain terrain ) {
//        int vertexCount = terrain.numXSamples * terrain.numZSamples;
//        textureBuffer.rewind();
//
//        for ( int zIndex = 0; zIndex < terrain.numZSamples; zIndex++ ) {
//            float z = terrain.zData[zIndex];
//            for ( int xIndex = 0; xIndex < terrain.numXSamples; xIndex++ ) {
//                float x = terrain.xData[xIndex];
//                textureBuffer.put( new float[] { x, z } ); // store x and z coordinates in the main texture
//            }
//        }
//    }


    @Override protected void preRender( GLOptions options ) {
        super.preRender( options );

        glEnable( GL_LIGHTING );
    }

    @Override protected void postRender( GLOptions options ) {
        super.postRender( options );

        glDisable( GL_LIGHTING );
    }

    private static ImmutableVector3F[] computePositions( Terrain terrain, LWJGLModelViewTransform modelViewTransform ) {
        ImmutableVector3F[] positions = new ImmutableVector3F[terrain.numXSamples * terrain.numZSamples];
        ImmutableVector3F[] xRadials = new ImmutableVector3F[terrain.numXSamples];
        ImmutableVector3F[] zRadials = new ImmutableVector3F[terrain.numZSamples];

        for ( int i = 0; i < terrain.numXSamples; i++ ) {
            xRadials[i] = PlateModel.getXRadialVector( terrain.xData[i] );
        }
        for ( int i = 0; i < terrain.numZSamples; i++ ) {
            zRadials[i] = PlateModel.getZRadialVector( terrain.zData[i] );
        }

        for ( int zIndex = terrain.numZSamples - 1; zIndex >= 0; zIndex-- ) {
            ImmutableVector3F zRadial = zRadials[zIndex];
            for ( int xIndex = 0; xIndex < terrain.numXSamples; xIndex++ ) {
                float elevation = terrain.getElevation( xIndex, zIndex );
                ImmutableVector3F cartesianModelVector = PlateModel.convertToRadial( xRadials[xIndex], zRadial, elevation );
                ImmutableVector3F viewVector = modelViewTransform.modelToView( cartesianModelVector );
                positions[zIndex * terrain.numXSamples + xIndex] = viewVector;
            }
        }
        return positions;
    }

    // put our elevation information into the x coordinate of the TexCoord2 mesh buffer
    // TODO: use this for shading the texture
//    private void updateHeightmap( Terrain terrain, GridMesh gridMesh ) {
//        // also update the texture height-map whenever this is done
//        texture2.clear();
//        for ( int zIndex = 0; zIndex < terrain.numZSamples; zIndex++ ) {
//            for ( int xIndex = 0; xIndex < terrain.numXSamples; xIndex++ ) {
//                texture2.put( new float[] { terrain.getElevation( xIndex, zIndex ), 0 } ); // store elevation in the other texture
//            }
//        }
//
//        // update (or set) the buffer
//        VertexBuffer buffer = gridMesh.getBuffer( Type.TexCoord2 );
//        if ( buffer == null ) {
//            gridMesh.setBuffer( VertexBuffer.Type.TexCoord2, 2, texture2 );
//        }
//        else {
//            buffer.updateData( texture2 );
//        }
//    }
}