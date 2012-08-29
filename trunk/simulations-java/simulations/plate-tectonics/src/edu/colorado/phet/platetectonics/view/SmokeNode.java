// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateTectonicsModel;
import edu.colorado.phet.platetectonics.model.SmokePuff;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glVertexPointer;

/**
 * Handles the smoke puffs for the volcanoes
 */
public class SmokeNode extends GLNode {

    // record what model puff corresponds to what node, so we can remove them later
    private final Map<SmokePuff, SmokePuffNode> map = new HashMap<SmokePuff, SmokePuffNode>();
    private final LWJGLTransform modelViewTransform;

    public SmokeNode( final LWJGLTransform modelViewTransform, final ObservableList<SmokePuff> smokePuffs ) {
        this.modelViewTransform = modelViewTransform;

        smokePuffs.addElementAddedObserver( new VoidFunction1<SmokePuff>() {
            public void apply( SmokePuff smokePuff ) {
                addPuff( smokePuff );
            }
        } );
        smokePuffs.addElementRemovedObserver( new VoidFunction1<SmokePuff>() {
            public void apply( SmokePuff smokePuff ) {
                removePuff( smokePuff );
            }
        } );

        setRenderPass( RenderPass.TRANSPARENCY );
    }

    private void addPuff( final SmokePuff puff ) {
        SmokePuffNode node = new SmokePuffNode( modelViewTransform, puff );
        addChild( node );
        map.put( puff, node );
    }

    private void removePuff( SmokePuff puff ) {
        removeChild( map.get( puff ) );
    }

    /**
     * Smoke is composed of multiple puffs. This shows one, which is in a kind of a teardrop shape with a ruffled border. See the assets directory
     * for the Mathematica notebook with the formula and explanation
     */
    public static class SmokePuffNode extends GLNode {

        private static int NUM_SAMPLES = 80;

        private static FloatBuffer positionBuffer = BufferUtils.createFloatBuffer( ( NUM_SAMPLES + 1 ) * 3 );
        private final SmokePuff puff;

        static {
            // origin
            positionBuffer.put( new float[]{0, 2, 0} );

            for ( int i = 0; i < NUM_SAMPLES; i++ ) {
                float theta = (float) ( 2 * Math.PI * i / ( NUM_SAMPLES - 1 ) );
                Vector2F position = computeCloudShape( theta );
                positionBuffer.put( new float[]{position.y, -position.x, 0} );
            }
        }

        public SmokePuffNode( final LWJGLTransform modelViewTransform, final SmokePuff puff ) {
            this.puff = puff;
            requireEnabled( GL_BLEND );

            // positions and scales the smoke
            final SimpleObserver updateObserver = new SimpleObserver() {
                public void update() {
                    Vector3F viewCoordinates = modelViewTransform.transformPosition(
                            PlateTectonicsModel.convertToRadial( puff.position.get() ) );
                    transform.set( Matrix4F.translation( viewCoordinates.x, viewCoordinates.y, viewCoordinates.z ).times(
                            Matrix4F.scaling( puff.scale.get() ) ) );
                }
            };

            // listen to everything that could change
            puff.position.addObserver( updateObserver );
            puff.scale.addObserver( updateObserver );
        }

        @Override public void renderSelf( GLOptions options ) {
            super.renderSelf( options );

            glColor4f( 0, 0, 0, puff.alpha.get() );
            glDepthMask( false );

            // enable vertex handling
            glEnableClientState( GL_VERTEX_ARRAY );
            positionBuffer.rewind();
            glVertexPointer( 3, 0, positionBuffer );

            glDrawArrays( GL_TRIANGLE_FAN, 0, NUM_SAMPLES + 1 );

            glDisableClientState( GL_VERTEX_ARRAY );
            glDepthMask( true );
        }

        // it's basically the equation for a circle, but stretched in a certain way. similar to the teardrop curve
        private static Vector2F computeCloudShape( float theta ) {
            float smokeFactor = (float) ( 1 + Math.cos( 20 * theta ) / 20 );
            // in the future, just scale this amout if you want the tip pointier or less pointy
            final double tipScale = 1;

            final double tipAmount = tipScale * Math.max( 0, Math.cos( theta ) );

            final double tipXPosition = 1 + tipScale; // 1 is from the radius of the circle

            return new Vector2F( smokeFactor * Math.cos( theta ) + tipAmount,
                                 Math.sin( theta ) * ( smokeFactor - tipAmount * tipAmount ) ).minus( new Vector2F( tipXPosition, 0 ) );
        }
    }
}
