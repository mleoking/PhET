// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateTectonicsModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.color4f;
import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static org.lwjgl.opengl.GL11.*;

/**
 * Highlights a box with dashed outlines where a dropped crust piece will go.
 * <p/>
 * This actually uses a decent chunk of vertices, because the actual on-screen shape is curved.
 */
public class BoxHighlightNode extends GLNode {
    public final Bounds3D bounds;
    private LWJGLTransform modelViewTransform;
    private Property<Color> color;

    public BoxHighlightNode( Bounds3D bounds, LWJGLTransform modelViewTransform, Property<Color> color ) {
        this.bounds = bounds;
        this.modelViewTransform = modelViewTransform;
        this.color = color;

        requireEnabled( GL_BLEND );
    }

    /*---------------------------------------------------------------------------*
    * convenience methods for mapping from our "planar" box into the "radial" space
    *----------------------------------------------------------------------------*/

    private Vector3F transformPoint( float x, float y, float z ) {
        return transformPoint( new Vector3F( x, y, z ) );
    }

    private Vector3F transformPoint( Vector3F planarPoint ) {
        return modelViewTransform.transformPosition( PlateTectonicsModel.convertToRadial( planarPoint ) );
    }

    private Vector3F[] transformedLine( Vector3F start, Vector3F end, int samples ) {
        Vector3F[] result = new Vector3F[samples];
        for ( int i = 0; i < samples; i++ ) {
            float ratio = (float) i / ( (float) ( samples - 1 ) );
            result[i] = transformPoint( start.times( 1 - ratio ).plus( end.times( ratio ) ) );
        }
        return result;
    }

    private void drawFromTo( Vector3F start, Vector3F end, int samples ) {
        for ( Vector3F v : transformedLine( start, end, samples ) ) {
            vertex3f( v );
        }
    }

    private void quadLine( Vector3F[] top, Vector3F[] bottom ) {
        glBegin( GL_TRIANGLE_STRIP );
        for ( int i = 0; i < top.length; i++ ) {
            vertex3f( top[i] );
            vertex3f( bottom[i] );
        }
        glEnd();
    }

    @Override public void renderSelf( GLOptions options ) {
        Vector3F leftBottomBack = new Vector3F( bounds.getMinX(), bounds.getMinY(), bounds.getMinZ() );
        Vector3F leftBottomFront = new Vector3F( bounds.getMinX(), bounds.getMinY(), bounds.getMaxZ() );
        Vector3F leftTopBack = new Vector3F( bounds.getMinX(), bounds.getMaxY(), bounds.getMinZ() );
        Vector3F leftTopFront = new Vector3F( bounds.getMinX(), bounds.getMaxY(), bounds.getMaxZ() );
        Vector3F rightBottomBack = new Vector3F( bounds.getMaxX(), bounds.getMinY(), bounds.getMinZ() );
        Vector3F rightBottomFront = new Vector3F( bounds.getMaxX(), bounds.getMinY(), bounds.getMaxZ() );
        Vector3F rightTopBack = new Vector3F( bounds.getMaxX(), bounds.getMaxY(), bounds.getMinZ() );
        Vector3F rightTopFront = new Vector3F( bounds.getMaxX(), bounds.getMaxY(), bounds.getMaxZ() );

        int zSectionQuantity = 10;
        int xSectionQuantity = 20;

        glEnable( GL_LINE_STIPPLE );
//        glDisable( GL_DEPTH_TEST );
//        glLineWidth( 1.5f );
        glLineStipple( 1, (short) 0xFF00 ); // this is essentially the dash pattern
        color4f( color.get() ); // whether it is highlighted or not

        // front face
        quadLine( transformedLine( leftTopFront, rightTopFront, xSectionQuantity ),
                  transformedLine( leftBottomFront, rightBottomFront, xSectionQuantity ) );

        // top face
        for ( int i = 0; i < zSectionQuantity - 1; i++ ) {
            float ratioA = ( (float) i ) / ( (float) ( zSectionQuantity - 1 ) );
            float ratioB = ( (float) ( i + 1 ) ) / ( (float) ( zSectionQuantity - 1 ) );
            quadLine( transformedLine( leftTopBack.times( ratioA ).plus( leftTopFront.times( 1 - ratioA ) ),
                                       rightTopBack.times( ratioA ).plus( rightTopFront.times( 1 - ratioA ) ), xSectionQuantity ),
                      transformedLine( leftTopBack.times( ratioB ).plus( leftTopFront.times( 1 - ratioB ) ),
                                       rightTopBack.times( ratioB ).plus( rightTopFront.times( 1 - ratioB ) ), xSectionQuantity ) );
        }

        // outlines along all of the relevant edges
        glColor4f( 0, 0, 0, 1 );
        glBegin( GL_LINE_STRIP );
        drawFromTo( leftBottomBack, leftTopBack, 2 );
        drawFromTo( leftTopBack, rightTopBack, xSectionQuantity );
        drawFromTo( rightTopBack, rightBottomBack, 2 );
        drawFromTo( rightBottomBack, leftBottomBack, xSectionQuantity );
        glEnd();
        glBegin( GL_LINE_STRIP );
        drawFromTo( leftBottomFront, leftTopFront, 2 );
        drawFromTo( leftTopFront, rightTopFront, xSectionQuantity );
        drawFromTo( rightTopFront, rightBottomFront, 2 );
        drawFromTo( rightBottomFront, leftBottomFront, xSectionQuantity );
        glEnd();
        glBegin( GL_LINE_STRIP );
        drawFromTo( leftBottomBack, leftBottomFront, zSectionQuantity );
        glEnd();
        glBegin( GL_LINE_STRIP );
        drawFromTo( leftTopBack, leftTopFront, zSectionQuantity );
        glEnd();
        glBegin( GL_LINE_STRIP );
        drawFromTo( rightBottomBack, rightBottomFront, zSectionQuantity );
        glEnd();
        glBegin( GL_LINE_STRIP );
        drawFromTo( rightTopBack, rightTopFront, zSectionQuantity );
        glEnd();

        glDisable( GL_LINE_STIPPLE );
//        glEnable( GL_DEPTH_TEST );
        glLineWidth( 1 );
    }
}
