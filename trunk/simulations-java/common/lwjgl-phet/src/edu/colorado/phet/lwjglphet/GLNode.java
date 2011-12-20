// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

/**
 * General scene-graph node for our LWJGL usage
 * <p/>
 * TODO: add selection names for use with glLoadName
 */
public class GLNode {
    private GLNode parent = null;
    private final List<GLNode> children = new ArrayList<GLNode>();

    // transform that is applied to OpenGL's MODELVIEW matrix
    public final LWJGLTransform transform = new LWJGLTransform();

    // material applied before rendering
    private GLMaterial material;

    public GLNode() {
    }

    /**
     * Renders this node and all children
     * NOTE: assumes that we are in the matrix mode GL_MODELVIEW
     *
     * @param options Options specified that can affect certain operations
     */
    public final void render( GLOptions options ) {
        // handle the model-view transform
        if ( !transform.isIdentity() ) {
            glPushMatrix();
            transform.apply();
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
        if ( !transform.isIdentity() ) {
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

    public void translate( float x, float y, float z ) {
        transform.append( ImmutableMatrix4F.translation( x, y, z ) );
    }

    public void scale( float x, float y, float z ) {
        transform.append( ImmutableMatrix4F.scaling( x, y, z ) );
    }

    public void scale( float s ) {
        transform.append( ImmutableMatrix4F.scaling( s ) );
    }

    public void rotate( ImmutableVector3F axis, float angle ) {
        transform.append( ImmutableMatrix4F.rotate( axis, angle ) );
    }

    public GLNode getParent() {
        return parent;
    }

    public GLMaterial getMaterial() {
        return material;
    }

    public void setMaterial( GLMaterial material ) {
        this.material = material;
    }
}
