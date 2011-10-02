// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.X_SAMPLES;
import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.Y_SAMPLES;

public class CrossSectionNode extends Geometry {
    private final PlateTectonicsModule module;

    public CrossSectionNode( final PlateModel model, final PlateTectonicsModule module ) {
        this.module = module;
        setMesh( new Mesh() {{
            final int vertexCount = X_SAMPLES * 2;
            final FloatBuffer positionBuffer = BufferUtils.createFloatBuffer( vertexCount * 3 );
            final FloatBuffer normalBuffer = BufferUtils.createFloatBuffer( vertexCount * 3 );
            final FloatBuffer textureBuffer = BufferUtils.createFloatBuffer( vertexCount * 2 );
            final IntBuffer indexBuffer = BufferUtils.createIntBuffer( vertexCount );

            // since we are using a triangle strip in the most efficient way, we just write out the list of vertices
            for ( int i = 0; i < vertexCount; i++ ) {
                indexBuffer.put( i );
            }

            final Runnable setPositions = new Runnable() {
                public void run() {
                    // reset the buffers
                    positionBuffer.clear();
                    normalBuffer.clear();
                    textureBuffer.clear();

                    // scan through all of our "top" vertices
                    for ( int i = 0; i < X_SAMPLES; i++ ) {
                        float modelX = getModelX( i );
                        float modelZ = 0;
                        Vector3f modelTop = new Vector3f( modelX, (float) model.getElevation( modelX, modelZ ), modelZ );
                        Vector3f modelBottom = new Vector3f( modelX, getBaseY(), modelZ );

                        Vector3f viewTop = module.getModelViewTransform().modelToView( modelTop );
                        Vector3f viewBottom = module.getModelViewTransform().modelToView( modelBottom );

                        positionBuffer.put( new float[] { viewTop.x, viewTop.y, viewTop.z } );
                        positionBuffer.put( new float[] { viewBottom.x, viewBottom.y, viewBottom.z } );

                        normalBuffer.put( new float[] { 0, 0, 1, 0, 0, 1 } ); // consolidated the two normals facing towards the camera

                        textureBuffer.put( new float[] { 0, 0, 0, 0 } ); // TODO: actual texture coordinates!
                    }
                }
            };
            setPositions.run();

            model.modelChanged.addUpdateListener( new UpdateListener() {
                public void update() {
                    setPositions.run();

                    updateBound();
                    updateCounts();

                    getBuffer( Type.Position ).updateData( positionBuffer );
                    getBuffer( Type.Normal ).updateData( normalBuffer );
                    getBuffer( Type.TexCoord ).updateData( textureBuffer );
                }
            }, false );

            setMode( Mode.TriangleStrip );
            setBuffer( VertexBuffer.Type.Position, 3, positionBuffer );
            setBuffer( VertexBuffer.Type.Normal, 3, normalBuffer );
            setBuffer( VertexBuffer.Type.TexCoord, 2, textureBuffer );
            setBuffer( VertexBuffer.Type.Index, 3, indexBuffer );
            updateBound();
            updateCounts();
        }} );
        setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
            // TODO: texturing!
            setColor( "Color", ColorRGBA.Gray );
        }} );
    }

    private float getModelX( float xIndex ) {
        // TODO: refactor this to combine constraints with TerrainNode!
        // center our x samples, and apply the resolution
        return module.getModelViewTransform().viewToModelDeltaX( ( xIndex - ( (float) X_SAMPLES - 1 ) / 2 ) / PlateTectonicsConstants.RESOLUTION );
    }

    private float getBaseY() {
        // TODO: improve base Y, since this would only render samples up to the sea level!
        return module.getModelViewTransform().viewToModelDeltaY( ( -Y_SAMPLES ) / PlateTectonicsConstants.RESOLUTION );
    }
}
