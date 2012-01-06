// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.lwjglphet.GLMaterial;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
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

    private boolean visible = true;
    private RenderPass renderPass = RenderPass.REGULAR;

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
        if ( isVisible() ) {
            // handle the model-view transform
            if ( !transform.isIdentity() ) {
                glPushMatrix();
                transform.apply();
            }

            preRender( options );
            if ( material != null ) {
                material.before( options );
            }

            // only render self if we have an exact match for the render pass. this allows us to render transparent objects later, etc
            if ( renderPass == options.renderPass ) {
                renderSelf( options );
            }
            renderChildren( options );

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
    }

    protected void renderChildren( GLOptions options ) {
        for ( GLNode child : children ) {
            child.render( options );
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
        // don't re-add children
        if( node.parent == this && children.contains( node )) {
                return;
        }
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
        transform.append( ImmutableMatrix4F.rotation( axis, angle ) );
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

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public RenderPass getRenderPass() {
        return renderPass;
    }

    public void setRenderPass( RenderPass renderPass ) {
        this.renderPass = renderPass;
    }
}
