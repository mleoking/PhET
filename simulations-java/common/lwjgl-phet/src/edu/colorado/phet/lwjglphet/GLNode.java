// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;

/**
 * General scene-graph node for our LWJGL usage
 * <p/>
 * TODO: add selection names for use with glLoadName
 */
public class GLNode {
    private final List<GLNode> children = new ArrayList<GLNode>();

    /*---------------------------------------------------------------------------*
    * model-view transform state
    *----------------------------------------------------------------------------*/
    private boolean modelViewIsIdentity = true;

    // model-view transform, changed to the identity
    private final Property<ImmutableMatrix4F> modelViewTransform = new Property<ImmutableMatrix4F>( ImmutableMatrix4F.identity() );

    private ImmutableMatrix4F modelViewInverseTransform = ImmutableMatrix4F.identity();

    // various buffers for quick handling of the model-view transform
    private FloatBuffer modelViewTransformBuffer = BufferUtils.createFloatBuffer( 16 );
    private FloatBuffer modelViewInverseTransformBuffer = BufferUtils.createFloatBuffer( 16 );

    public GLNode() {
        // when our MVT changes, update all of our other transformation states
        modelViewTransform.addObserver( new SimpleObserver() {
            public void update() {
                // store the inverse matrix
                modelViewInverseTransform = modelViewTransform.get().inverted();

                // store both into buffers so they can be sent to OpenGL with less overhead
                modelViewTransform.get().store( modelViewTransformBuffer );
                modelViewInverseTransform.store( modelViewInverseTransformBuffer );

                // TODO: check MVT for identity, and set modelViewIsIdentity as result
            }
        } );
    }

    /**
     * Renders this node and all children
     * NOTE: assumes that we are in the matrix mode GL_MODELVIEW
     *
     * @param options Options specified that can affect certain operations
     */
    public final void render( GLOptions options ) {
        if ( !modelViewIsIdentity ) {
            GL11.glPushMatrix();
            GL11.glMultMatrix( modelViewTransformBuffer );
        }

        renderSelf( options );
        for ( GLNode child : children ) {
            child.render( options );
        }

        if ( !modelViewIsIdentity ) {
            GL11.glPopMatrix();
        }
    }

    /**
     * This should be overridden to handle painting of this specific node (if anything)
     *
     * @param options Options specified that can affect certain operations
     */
    public void renderSelf( GLOptions options ) {

    }

    protected void preRender( GLOptions options ) {

    }

    protected void postRender( GLOptions options ) {

    }

    public List<GLNode> getChildren() {
        return children;
    }

    public void addChild( GLNode node ) {
        children.add( node );
    }

    public void removeChild( GLNode node ) {
        children.remove( node );
    }
}
