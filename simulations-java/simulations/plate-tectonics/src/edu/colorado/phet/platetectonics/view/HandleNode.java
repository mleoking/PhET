// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.shapes.GridMesh;
import edu.colorado.phet.platetectonics.util.ColorMaterial;

import static edu.colorado.phet.lwjglphet.math.ImmutableVector3F.Z_UNIT;
import static org.lwjgl.opengl.GL11.*;

public class HandleNode extends GLNode {
    private static int radialRows = 30;
    private static int radialColumns = 30;
    private static int columns = radialColumns * 2 + 2; // radialColumns for each bend, with one for each base
    private static float smallRadius = 8;
    private static float largeRadius = 15;
    private static float middleLength = 70;
    private static float downLength = 30;
    private ImmutableVector3F[] positions;
    private GridMesh handleMesh;

    public HandleNode() {

        positions = new ImmutableVector3F[radialRows * columns];

        RingPositioner leftBase = new RingPositioner() {
            public ImmutableVector3F position( float sin, float cos ) {
                return new ImmutableVector3F(
                        -middleLength / 2 + cos,
                        -downLength,
                        -sin
                );
            }
        };
        RingPositioner rightBase = new RingPositioner() {
            public ImmutableVector3F position( float sin, float cos ) {
                return new ImmutableVector3F(
                        middleLength / 2 - cos,
                        -downLength,
                        -sin
                );
            }
        };
        int col = 0;
        apply( leftBase, col++ );
        for ( int radialCol = 0; radialCol < radialColumns; radialCol++ ) {
            apply( new CurvePositioner( ( (float) radialCol ) / ( (float) ( radialColumns - 1 ) ), false ), col++ );
        }
        for ( int radialCol = radialColumns - 1; radialCol >= 0; radialCol-- ) {
            apply( new CurvePositioner( ( (float) radialCol ) / ( (float) ( radialColumns - 1 ) ), true ), col++ );
        }
        apply( rightBase, col++ );

        handleMesh = new GridMesh( radialRows, columns, positions );
        addChild( handleMesh );
    }

    private void apply( RingPositioner positioner, int col ) {
        for ( int row = 0; row < radialRows; row++ ) {
            float radialRatio = ( (float) row ) / ( (float) ( radialRows - 1 ) );
            float sin = (float) ( smallRadius * Math.sin( 2 * Math.PI * radialRatio ) );
            float cos = (float) ( smallRadius * Math.cos( 2 * Math.PI * radialRatio ) );
            positions[row * columns + col] = positioner.position( sin, cos );
        }
    }

    private static interface RingPositioner {
        public ImmutableVector3F position( float sin, float cos );
    }

    private static class CurvePositioner implements RingPositioner {
        private final float ratio;
        private final boolean flip;

        private CurvePositioner( float ratio, boolean flip ) {
            this.ratio = ratio;
            this.flip = flip;
        }

        public ImmutableVector3F position( float sin, float cos ) {
            ImmutableVector3F direction = new ImmutableVector3F(
                    (float) -Math.cos( ratio * Math.PI / 2 ),
                    (float) Math.sin( ratio * Math.PI / 2 ),
                    0
            );
            ImmutableVector3F center = direction.times( largeRadius ).plus( new ImmutableVector3F(
                    -middleLength / 2 + largeRadius,
                    0,
                    0
            ) );

            // use the direction and Z_UNIT to span the plane our circle is in
            return center.plus( direction.times( -cos ) ).plus( Z_UNIT.times( -sin ) )

                    // flip the X component if "flip" is true
                    .componentTimes( new ImmutableVector3F( flip ? -1 : 1, 1, 1 ) );
        }
    }

    // TODO: we are using the rendering order very particularly. find a better way
    @Override public void renderSelf( GLOptions options ) {
        // render the back-facing parts
        handleMesh.setMaterial( new ColorMaterial( 1, 1, 1, 0.2f ) );
        glFrontFace( GL_CW );
        handleMesh.render( options );

        // then switch back to normal
        glFrontFace( GL_CCW );
        handleMesh.setMaterial( new ColorMaterial( 1, 1, 1, 0.4f ) );

        super.renderSelf( options );
    }

    // TODO: better handling for the enabling/disabling of lighting
    @Override protected void preRender( GLOptions options ) {
        super.preRender( options );

        glEnable( GL_BLEND );
        glEnable( GL_LIGHTING );
        glEnable( GL_COLOR_MATERIAL );
        glColorMaterial( GL_FRONT, GL_DIFFUSE );
        glEnable( GL_CULL_FACE );
    }

    @Override protected void postRender( GLOptions options ) {
        super.postRender( options );

        glDisable( GL_COLOR_MATERIAL );
        glDisable( GL_LIGHTING );
        glDisable( GL_CULL_FACE );

        // TODO: fix blend handling, this disable was screwing other stuff up
//        glDisable( GL_BLEND );
    }
}
