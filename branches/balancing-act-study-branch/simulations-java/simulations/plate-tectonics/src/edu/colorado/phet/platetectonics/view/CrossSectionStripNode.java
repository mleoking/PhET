// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Iterator;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateTectonicsModel;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.util.MortalSimpleObserver;
import edu.colorado.phet.platetectonics.util.MortalUpdateListener;
import edu.colorado.phet.platetectonics.view.materials.EarthTexture;

import static org.lwjgl.opengl.GL11.*;

/**
 * Displays a strip of earth cross-section (we use strips both for model convenience and because we can efficiently render these)
 */
public class CrossSectionStripNode extends GLNode {
    private LWJGLTransform modelViewTransform;
    private Property<ColorMode> colorMode;
    public final CrossSectionStrip strip;

    // track how full our buffers are. since our strip can increase in size, we need to know this number beforehand
    private int capacity = 0;

    private FloatBuffer positionBuffer;
    private FloatBuffer textureBuffer;
    private FloatBuffer colorBuffer;

    private boolean checkDepth = true;
    private boolean transparencyInitialized = false;

    // remember CCW order
    public CrossSectionStripNode( LWJGLTransform modelViewTransform, final Property<ColorMode> colorMode, final CrossSectionStrip strip ) {
        this.modelViewTransform = modelViewTransform;
        this.colorMode = colorMode;
        this.strip = strip;

        // enable alpha blending if necessary
        strip.alpha.addObserver( new MortalSimpleObserver( strip.alpha, strip.disposed ) {
            public void update() {
                if ( !transparencyInitialized && strip.alpha.get() != 1 ) {
                    transparencyInitialized = true;
                    requireEnabled( GL_BLEND );
                }
            }
        } );

        // update our buffers when the model strip changes
        strip.changed.addUpdateListener( new MortalUpdateListener( strip.changed, strip.disposed ) {
            public void update() {
                updatePosition();
            }
        }, true );

        // because our buffers involve coloring the terrain with the relevant EarthMaterial, we need to update buffers on color mode changes
        colorMode.addObserver( new MortalSimpleObserver( colorMode, strip.disposed ) {
            public void update() {
                updatePosition();
            }
        } );

        // if we are moved to the front, ignore the depth test so we will draw OVER whatever is there
        // this allows us to not have to manually cull out the mantle behind subducting strips, etc.
        strip.moveToFrontNotifier.addUpdateListener( new MortalUpdateListener( strip.moveToFrontNotifier, strip.disposed ) {
            public void update() {
                checkDepth = false;
            }
        }, false );
    }

    // recreate buffers if we need more vertices
    private void checkSize() {
        final int neededCount = Math.max( 10, strip.getNumberOfVertices() );
        if ( capacity < neededCount ) {
            capacity = neededCount;
            positionBuffer = BufferUtils.createFloatBuffer( neededCount * 3 );
            textureBuffer = BufferUtils.createFloatBuffer( neededCount * 2 );
            colorBuffer = BufferUtils.createFloatBuffer( neededCount * 4 );
        }
    }

    public void updatePosition() {
        checkSize();
        positionBuffer.clear();
        textureBuffer.clear();
        colorBuffer.clear();

        // alternate top to bottom, and use iterators so we don't make this O(n^2)
        Iterator<Sample> topIter = strip.topPoints.listIterator();
        Iterator<Sample> bottomIter = strip.bottomPoints.listIterator();

        // alternate points in the order needed by GL_TRIANGLE_STRIP
        while ( topIter.hasNext() ) {
            addSamplePoint( topIter.next() );
            addSamplePoint( bottomIter.next() );
        }
    }

    private void addSamplePoint( Sample point ) {
        // calculate point properties
        final Color color = colorMode.get().getMaterial().getColor( point.getDensity(), point.getTemperature(),
                                                                    new Vector2F( point.getPosition().x, point.getPosition().y ),
                                                                    strip.alpha.get() );
        final Vector3F position = modelViewTransform.transformPosition( PlateTectonicsModel.convertToRadial( point.getPosition() ) );

        // fill the three necessary buffers
        colorBuffer.put( color.getComponents( new float[4] ) );
        textureBuffer.put( new float[]{point.getTextureCoordinates().x, point.getTextureCoordinates().y} );
        positionBuffer.put( new float[]{position.x, position.y, position.z} );
    }

    @Override public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        // texture coordinates
        if ( options.shouldSendTexture() ) {
            glEnableClientState( GL_TEXTURE_COORD_ARRAY );
            textureBuffer.rewind();
            glTexCoordPointer( 2, 0, textureBuffer );
        }

        // per-vertex color
        glEnableClientState( GL_COLOR_ARRAY );
        colorBuffer.rewind();
        glColorPointer( 4, 0, colorBuffer );

        // vertex positions
        glEnableClientState( GL_VERTEX_ARRAY );
        positionBuffer.rewind();
        glVertexPointer( 3, 0, positionBuffer );

        if ( !checkDepth ) {
            glDepthFunc( GL_ALWAYS );
        }
        EarthTexture.begin();
        glDrawArrays( GL_TRIANGLE_STRIP, 0, strip.getNumberOfVertices() ); // actually draws the things
        EarthTexture.end();
        if ( !checkDepth ) {
            glDepthFunc( GL_LESS );
        }

        glDisableClientState( GL_VERTEX_ARRAY );
        if ( options.shouldSendTexture() ) {
            glDisableClientState( GL_TEXTURE_COORD_ARRAY );
        }
        glDisableClientState( GL_COLOR_ARRAY );
        glDisableClientState( GL_VERTEX_ARRAY );
    }

    public CrossSectionStrip getStrip() {
        return strip;
    }
}
