// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.Grid3D;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;

/**
 * Displays a cross section of the plate model, within the specified grid bounds
 */
public class CrossSectionNode extends Geometry {
    private final PlateModel model;
    private final Grid3D grid;
    private final Grid3D textureGrid;

    public CrossSectionNode( final PlateModel model, final PlateTectonicsTab module, final Grid3D grid ) {
        this.model = model;
        this.grid = grid;

        // lower resolution grid for texture handling
        this.textureGrid = grid.withSamples( grid.getNumXSamples() / 2, grid.getNumYSamples() / 2, grid.getNumZSamples() / 2 );
//        this.textureGrid = grid.withSamples( grid.getNumXSamples() * 2, grid.getNumYSamples() * 2, grid.getNumZSamples() * 2 );
//        this.textureGrid = grid;

        // create a mesh using a triangle strip
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

                    float modelZ = getFrontZ();
                    float modelBottomY = getBaseY();
                    float textureBottomY = ( modelBottomY - grid.getBounds().getMinY() ) / grid.getBounds().getHeight();

                    // scan left to right, alternating top (crust elevation) and bottom (lower bounds) vertices
                    for ( int i = 0; i < numXSamples; i++ ) {
                        float modelX = grid.getXSample( i );
                        float modelTopY = (float) model.getElevation( modelX, modelZ );

                        Vector3f modelTop = new Vector3f( modelX, modelTopY, modelZ );
                        Vector3f modelBottom = new Vector3f( modelX, modelBottomY, modelZ );

                        Vector3f roundedModelTop = PlateModel.convertToRadial( modelTop );
                        Vector3f roundedModelBottom = PlateModel.convertToRadial( modelBottom );

                        Vector3f viewTop = module.getModelViewTransform().modelToView( roundedModelTop );
                        Vector3f viewBottom = module.getModelViewTransform().modelToView( roundedModelBottom );

                        positionBuffer.put( new float[] { viewTop.x, viewTop.y, viewTop.z } );
                        positionBuffer.put( new float[] { viewBottom.x, viewBottom.y, viewBottom.z } );

                        normalBuffer.put( new float[] { 0, 0, 1, 0, 0, 1 } ); // consolidated the two normals facing towards the camera

                        // scale our entire texture across the unit square. TODO (possible bug): check for texture size changes and handle there?
                        float textureX = ( modelX - grid.getBounds().getMinX() ) / grid.getBounds().getWidth();
                        float textureTopY = ( modelTopY - grid.getBounds().getMinY() ) / grid.getBounds().getHeight();
                        textureBuffer.put( new float[] {
                                textureX, textureTopY,
                                textureX, textureBottomY } );
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
            setTexture( "ColorMap", new Texture2D() {{
                setImage( new CrossSectionTextureImage( textureGrid.getNumXSamples(), textureGrid.getNumYSamples() ) );
            }} );
        }} );
    }

    private float getBaseY() {
        return grid.getYSample( 0 );
    }

    private float getFrontZ() {
        // pick out the "front" Z sample, which is actually at the end of the array
        return grid.getZSample( grid.getNumZSamples() - 1 );
    }

    private class CrossSectionTextureImage extends Image {
        public CrossSectionTextureImage( int width, int height ) {
            super( Format.RGBA8, Math.max( width, height ), Math.max( width, height ), ByteBuffer.allocateDirect( 4 * Math.max( width, height ) * Math.max( width, height ) ) );
            model.modelChanged.addUpdateListener( new UpdateListener() {
                                                      public void update() {
                                                          updateCrossSection();
                                                      }
                                                  }, true );
        }

        private static final double minDensityToShow = 2500;
        private static final double maxDensityToShow = 3500;

        public void updateCrossSection() {
            int Y_SAMPLES = textureGrid.getNumYSamples();
            ByteBuffer buffer = data.get( 0 );
            buffer.clear();
//            System.out.println( "width = " + width );
//            System.out.println( "height = " + height );
            byte[] byteRow = new byte[width * 4];
            for ( int y = 0; y < height; y++ ) {
                float modelY = textureGrid.getYSample( y );
                for ( int x = 0; x < width; x++ ) {
                    // TODO: investigate this code, why it is needed in the other location. Probably deals with square-ness of textures?
                    // TODO: (possible bug) we might need to double-check that we don't go up to a power of 256 texture somehow, so we should figure out the texture coordinates better
//                    if ( Y_SAMPLES - y - 1 >= Y_SAMPLES ) {
//                        // since we don't care about data past this point, just zero it out
//                        buffer.put( new byte[] { 0, 0, 0, 0 } );
//                        continue;
//                    }
                    double c = 100 + ( 1 - ( model.getDensity( textureGrid.getXSample( x ), modelY ) - minDensityToShow ) / ( maxDensityToShow - minDensityToShow ) ) * 155;

                    // MathUtils.clamp has too many slow checks in front of it
                    byte d = (byte) ( c < 0 ? 0 : ( c > 255 ? 255 : c ) );

                    int offset = 4 * x;
                    byteRow[offset] = d;
                    byteRow[offset + 1] = d;
                    byteRow[offset + 2] = d;
                    byteRow[offset + 3] = (byte) 255;
                }
                buffer.put( byteRow );
            }
            setUpdateNeeded();
        }

//        final Function.LinearFunction densityFunction = new Function.LinearFunction( minDensityToShow, maxDensityToShow, 255, 100 );

        // TODO: cleanup. moved from TestDensityRenderer (and modified a bit)
        private byte[] getColor( double density ) {
            //When surface density and temperature, use clay color
//            Color clay = new Color( 255, 222, 156 );

            // TODO: improve coloring function, make it calculate ONLY density or temperature, and hook up radio button controls
            //When it gets hotter, turn down the G & B channels to make redder

//        System.out.println( "density = " + density );

//            double x2 = densityFunction.evaluate( density );
            double x = 100 + ( 1 - ( density - minDensityToShow ) / ( maxDensityToShow - minDensityToShow ) ) * ( 155 );
//            System.out.println( "x = " + x +", x2 = "+x2);
            byte d = (byte) MathUtil.clamp( 0, x, 255 );

//        System.out.println( "temperature = " + temperature );

//            int tempChannel = (int) MathUtil.clamp( 0, new Function.LinearFunction( 280, 300, d, 255 ).evaluate( temperature ), 255 );
            return new byte[] { d, d, d, (byte) 255 };
        }
    }
}
