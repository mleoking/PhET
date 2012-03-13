// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.SmokePuff;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;

import static org.lwjgl.opengl.GL11.*;

public class SmokeNode extends GLNode {

    private final PlateTectonicsTab tab;

    private final Map<SmokePuff, SmokePuffNode> map = new HashMap<SmokePuff, SmokePuffNode>();

    public SmokeNode( final PlateTectonicsTab tab, final ObservableList<SmokePuff> smokePuffs ) {
        this.tab = tab;

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
        SmokePuffNode node = new SmokePuffNode( tab, puff );
        addChild( node );
        map.put( puff, node );
    }

    private void removePuff( SmokePuff puff ) {
        removeChild( map.get( puff ) );
    }

    public static class SmokePuffNode extends GLNode {

        private static int NUM_SAMPLES = 80;

        private static FloatBuffer positionBuffer = BufferUtils.createFloatBuffer( ( NUM_SAMPLES + 1 ) * 3 );
        private final SmokePuff puff;

        static {
            // origin
            positionBuffer.put( new float[] { 0, 2, 0 } );

            for ( int i = 0; i < NUM_SAMPLES; i++ ) {
                float theta = (float) ( 2 * Math.PI * i / ( NUM_SAMPLES - 1 ) );
                ImmutableVector2F position = computeCloudShape( theta );
                positionBuffer.put( new float[] { position.y, -position.x, 0 } );
            }
        }

        public SmokePuffNode( final PlateTectonicsTab tab, final SmokePuff puff ) {
            this.puff = puff;
            requireEnabled( GL_BLEND );

            // positions and scales the smoke
            final SimpleObserver updateObserver = new SimpleObserver() {
                public void update() {
                    ImmutableVector3F viewCoordinates = tab.getModelViewTransform().transformPosition(
                            PlateModel.convertToRadial( puff.position.get() ) );
                    transform.set( ImmutableMatrix4F.translation( viewCoordinates.x, viewCoordinates.y, viewCoordinates.z ).times(
                            ImmutableMatrix4F.scaling( puff.scale.get() ) ) );
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
        private static ImmutableVector2F computeCloudShape( float theta ) {
            float smokeFactor = (float) ( 1 + Math.cos( 20 * theta ) / 20 );
            // in the future, just scale this amout if you want the tip pointier or less pointy
            final double tipScale = 1;

            final double tipAmount = tipScale * Math.max( 0, Math.cos( theta ) );

            final double tipXPosition = 1 + tipScale; // 1 is from the radius of the circle

            return new ImmutableVector2F( smokeFactor * Math.cos( theta ) + tipAmount,
                                          Math.sin( theta ) * ( smokeFactor - tipAmount * tipAmount ) ).minus( new ImmutableVector2F( tipXPosition, 0 ) );
        }
    }
}
