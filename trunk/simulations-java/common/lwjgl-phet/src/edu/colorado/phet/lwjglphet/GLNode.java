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
    * transform state
    *----------------------------------------------------------------------------*/

    // transform that is applied to OpenGL's MODELVIEW matrix
    private final Property<ImmutableMatrix4F> transform = new Property<ImmutableMatrix4F>( ImmutableMatrix4F.IDENTITY );

    private ImmutableMatrix4F inverseTransform = ImmutableMatrix4F.IDENTITY;

    // various buffers for quick handling of the model-view transform
    private FloatBuffer transformBuffer = BufferUtils.createFloatBuffer( 16 );
    private FloatBuffer inverseTransformBuffer = BufferUtils.createFloatBuffer( 16 );

    private GLMaterial material;

    public GLNode() {
        // when our MVT changes, update all of our other transformation states
        transform.addObserver( new SimpleObserver() {
            public void update() {
                // store the inverse matrix
                inverseTransform = transform.get().inverted();

                // store both into buffers so they can be sent to OpenGL with less overhead
                transform.get().store( transformBuffer );
                inverseTransform.store( inverseTransformBuffer );
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
        ImmutableMatrix4F transform = this.transform.get();
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
                    transformBuffer.rewind();
                    glMultMatrix( transformBuffer );
            }
        }

        preRender( options );
        if ( material != null ) {
            material.before( options );
        }

        renderSelf( options );
        for ( GLNode child : children ) {
            child.render( options );
        }

        // NOTE: for now material will clean up after children, in case we want to use the material for all of the children
        if ( material != null ) {
            material.after( options );
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
        transform.set( matrix );
    }

    public void appendTransform( ImmutableMatrix4F matrix ) {
        transform.set( transform.get().times( matrix ) );
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

    public GLNode getParent() {
        return parent;
    }

    public ImmutableMatrix4F getTransform() {
        return transform.get();
    }

    public Property<ImmutableMatrix4F> getTransformProperty() {
        return transform;
    }

    public GLMaterial getMaterial() {
        return material;
    }

    public void setMaterial( GLMaterial material ) {
        this.material = material;
    }
}
