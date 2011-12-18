// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F.MatrixType;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;

import static org.lwjgl.opengl.GL11.*;

/**
 * General scene-graph node for our LWJGL usage
 * <p/>
 * TODO: add selection names for use with glLoadName
 */
public class GLNode {
    private GLNode parent = null;
    private final List<GLNode> children = new ArrayList<GLNode>();

    /*---------------------------------------------------------------------------*
    * model-view transform state
    *----------------------------------------------------------------------------*/

    // model-view transform, changed to the identity
    private final Property<ImmutableMatrix4F> modelViewTransform = new Property<ImmutableMatrix4F>( ImmutableMatrix4F.IDENTITY );

    private ImmutableMatrix4F modelViewInverseTransform = ImmutableMatrix4F.IDENTITY;

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
        // handle the model-view transform
        ImmutableMatrix4F transform = modelViewTransform.get();
        boolean hasNontrivialTransform = transform.type != MatrixType.IDENTITY;
        if ( hasNontrivialTransform ) {
            glPushMatrix();
            switch( transform.type ) {
                case SCALING:
                    glScalef( transform.m00, transform.m11, transform.m22 );
                    break;
                case TRANSLATION:
                    glTranslatef( transform.m30, transform.m31, transform.m32 );
                    break;
                default:
                    // fall back to sending the entire matrix
                    modelViewTransformBuffer.rewind();
                    glMultMatrix( modelViewTransformBuffer );
            }
        }

        preRender( options );

        renderSelf( options );
        for ( GLNode child : children ) {
            child.render( options );
        }

        postRender( options );

        // reverse our model-view transform for our parent
        if ( hasNontrivialTransform ) {
            glPopMatrix();
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
        node.parent = this;
        children.add( node );
    }

    public void removeChild( GLNode node ) {
        node.parent = null;
        children.remove( node );
    }

    public void setTransform( ImmutableMatrix4F matrix ) {
        modelViewTransform.set( matrix );
    }

    public void appendTransform( ImmutableMatrix4F matrix ) {
        modelViewTransform.set( modelViewTransform.get().times( matrix ) );
    }

    public void translate( float x, float y, float z ) {
        appendTransform( ImmutableMatrix4F.translation( x, y, z ) );
    }

    public void scale( float x, float y, float z ) {
        appendTransform( ImmutableMatrix4F.scaling( x, y, z ) );
    }

    public void scale( float s ) {
        appendTransform( ImmutableMatrix4F.scaling( s ) );
    }

    public void rotate( ImmutableVector3F axis, float angle ) {
        appendTransform( ImmutableMatrix4F.rotate( axis, angle ) );
    }
}
