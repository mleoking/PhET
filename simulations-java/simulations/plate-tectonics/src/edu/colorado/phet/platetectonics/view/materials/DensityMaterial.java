// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.materials;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.lwjglphet.GLMaterial;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.model.CrustModel;

import static org.lwjgl.opengl.GL11.*;

public class DensityMaterial extends GLMaterial implements EarthMaterial {
    private static final int width = 256;
    private static final int height = 256;

    private static final ByteBuffer buffer = BufferUtils.createByteBuffer( 4 * width * height );

    static {
        buffer.rewind();
        for ( int row = 0; row < height; row++ ) {
            for ( int col = 0; col < width; col++ ) {
                byte densityIndex = (byte) col;
                buffer.put( new byte[] { densityIndex, densityIndex, densityIndex, (byte) 255 } );
//                    buffer.put( new byte[] { (byte)row, (byte)col, 0, (byte) 255 } );
//                buffer.put( new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255 } );
            }
        }
    }

    public static ImmutableVector2F densityMap( float density ) {
        float minDensityToShow = 2500;
        float maxDensityToShow = 3500;
        float maxMaxDensityToShow = CrustModel.CENTER_DENSITY;

        float densityRatio = ( density - minDensityToShow ) / ( maxDensityToShow - minDensityToShow );
        float x;
        if ( density <= 3300 ) {
            x = 100f + ( 1f - densityRatio ) * 155f;
        }
        else {
            float start = 100f + ( 1f - ( 3300 - minDensityToShow ) / ( maxDensityToShow - minDensityToShow ) ) * 155f;
            float end = 50f;
            float ratio = ( density - 3300 ) / ( maxMaxDensityToShow - 3300 );
            x = start + ( end - start ) * ratio;
        }
        x = (float) MathUtil.clamp( 0.0, x / 255.0, 1.0 ); // clamp it in the normal range
        return new ImmutableVector2F( x, 0.5f );

//        float minDensityToShow = 2500;
//        float maxDensityToShow = CrustModel.CENTER_DENSITY;
//        float v = 1 - ( density - minDensityToShow ) / ( maxDensityToShow - minDensityToShow );
//        return new ImmutableVector2F( v, 0.5f );
    }

    @Override public void before( GLOptions options ) {
        // TODO: somehow we need the "white" color, since it's probably blending with our texture. investigate
        glColor4f( 1, 1, 1, 1 );
        glEnable( GL_TEXTURE_2D );
        glShadeModel( GL_FLAT );
        buffer.rewind();
        glTexImage2D( GL_TEXTURE_2D, 0, 4, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer );
        glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP );
        glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP );
        glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
        glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
    }

    @Override public void after( GLOptions options ) {
        glShadeModel( GL_SMOOTH );
        glDisable( GL_TEXTURE_2D );
    }

    public ImmutableVector2F getTextureCoordinates( float density, float temperature, ImmutableVector2F position ) {
        return densityMap( density );
    }
}
