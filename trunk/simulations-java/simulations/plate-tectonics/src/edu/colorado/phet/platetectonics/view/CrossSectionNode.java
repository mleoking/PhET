// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;
import edu.colorado.phet.platetectonics.util.Grid3D;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * Displays a cross section of the plate model, within the specified grid bounds
 */
public class CrossSectionNode extends Geometry {
    private final Grid3D grid;

    public CrossSectionNode( final PlateModel model, final PlateTectonicsModule module, final Grid3D grid ) {
        this.grid = grid;
        setMesh( new Mesh() {{
            final int vertexCount = grid.getNumXSamples() * 2;
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
                    int numXSamples = grid.getNumXSamples();
                    for ( int i = 0; i < numXSamples; i++ ) {
                        float modelX = grid.getXSample( i );
                        float modelZ = getFrontZ();
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

    private float getBaseY() {
        return grid.getYSample( 0 );
    }

    private float getFrontZ() {
        // pick out the "front" Z sample, which is actually at the end of the array
        return grid.getZSample( grid.getNumZSamples() - 1 );
    }
}
