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

public class TemperatureMaterial extends GLMaterial implements EarthMaterial {
    private static final int width = 256;
    private static final int height = 256;

    // TODO: check byte orders?
    private static final ByteBuffer buffer = BufferUtils.createByteBuffer( 4 * width * height );

    static {
        buffer.rewind();
        for ( int row = 0; row < height; row++ ) {
            for ( int col = 0; col < width; col++ ) {
                byte tempIndex = (byte) col;
                buffer.put( new byte[] { (byte) ( col * ( 256 - 64 ) / 256 + 64 ), (byte) 64, (byte) 64, (byte) 255 } );
//                    buffer.put( new byte[] { (byte)row, (byte)col, 0, (byte) 255 } );
//                buffer.put( new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255 } );
            }
        }
    }

    public static ImmutableVector2F temperatureMap( float temperature ) {
        float minTemp = CrustModel.ZERO_CELSIUS;
        float maxTemp = 1500;
        float maxMaxTemp = CrustModel.ZERO_CELSIUS + 6100 + 300;

//        float tempRatio = ( temperature - minTemp ) / ( maxTemp - minTemp );
//        float x;
//        float constant = 1300;
//        if ( temperature <= 1300 ) {
//            x = 100f + ( 1f - tempRatio ) * 155f;
//        }
//        else {
//            float start = 100f + ( 1f - ( 1300 - minTemp ) / ( maxTemp - minTemp ) ) * 155f;
//            float end = 50f;
//            float ratio = ( temperature - 1300 ) / ( maxMaxTemp - 1300 );
//            x = start + ( end - start ) * ratio;
//        }
//        x = (float) MathUtil.clamp( 0.0, x / 255.0, 1.0 ); // clamp it in the normal range
        float x = ( temperature - minTemp ) / ( maxMaxTemp - minTemp );
        x = (float) Math.pow( x, 0.4f );
        x = (float) MathUtil.clamp( 0.05, x, 1 );
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
        return temperatureMap( temperature );
    }
}
