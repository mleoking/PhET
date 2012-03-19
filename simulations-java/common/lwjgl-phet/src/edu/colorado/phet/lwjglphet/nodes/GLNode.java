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

import static org.lwjgl.opengl.GL11.*;

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

    // mask of attributes that will be automatically pushed and popped during the preRender and postRender operations
    protected int attributeRestorationMask = 0;

    // mask of client attributes that will be automatically pushed and popped during the preRender and postRender operations
    protected int clientAttributeRestorationMask = 0;

    // flags that should be enabled or disabled at the start. resetting these values is not automatically handled on postRender
    private final List<Integer> glEnableFlags = new ArrayList<Integer>();
    private final List<Integer> glDisableFlags = new ArrayList<Integer>();
    private final List<Integer> glEnableClientFlags = new ArrayList<Integer>();
    private final List<Integer> glDisableClientFlags = new ArrayList<Integer>();

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
        for ( GLNode child : new ArrayList<GLNode>( children ) ) {
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

    public void requireEnabled( int flag ) {
        glEnableFlags.add( flag );
        addResetForFlag( flag );
    }

    public void requireDisabled( int flag ) {
        glDisableFlags.add( flag );
        addResetForFlag( flag );
    }

    // the state denoted by the flag will be pushed on preRender and popped on postRender
    public void addResetForFlag( int flag ) {
        switch( flag ) {
            case GL_BLEND:
            case GL_ALPHA_TEST:
            case GL_DITHER:
            case GL_DRAW_BUFFER:
                // TODO: is ignoring GL_COLOR_BUFFER_BIT OK?
                attributeRestorationMask |= GL_ENABLE_BIT;
                break;
            case GL_EDGE_FLAG:
                attributeRestorationMask |= GL_EDGE_FLAG;
                break;
            case GL_DEPTH_TEST:
            case GL_LIGHT0:
            case GL_LIGHT1:
            case GL_LIGHT2:
            case GL_LIGHT3:
            case GL_LIGHT4:
            case GL_LIGHT5:
            case GL_LIGHT6:
            case GL_LIGHT7:
            case GL_COLOR_LOGIC_OP:
            case GL_INDEX_LOGIC_OP:
            case GL_NORMALIZE:
            case GL_POINT_SMOOTH:
            case GL_SCISSOR_TEST:
            case GL_STENCIL_TEST:
            case GL_TEXTURE_1D:
            case GL_TEXTURE_2D:
            case GL_TEXTURE_GEN_S:
            case GL_TEXTURE_GEN_T:
            case GL_TEXTURE_GEN_R:
            case GL_TEXTURE_GEN_Q:
                attributeRestorationMask |= GL_ENABLE_BIT;
                break;
            case GL_MAP1_COLOR_4:
            case GL_MAP1_GRID_DOMAIN:
            case GL_MAP1_GRID_SEGMENTS:
            case GL_MAP1_INDEX:
            case GL_MAP1_NORMAL:
            case GL_MAP1_TEXTURE_COORD_1:
            case GL_MAP1_TEXTURE_COORD_2:
            case GL_MAP1_TEXTURE_COORD_3:
            case GL_MAP1_TEXTURE_COORD_4:
            case GL_MAP1_VERTEX_3:
            case GL_MAP1_VERTEX_4:
            case GL_MAP2_COLOR_4:
            case GL_MAP2_GRID_DOMAIN:
            case GL_MAP2_GRID_SEGMENTS:
            case GL_MAP2_INDEX:
            case GL_MAP2_NORMAL:
            case GL_MAP2_TEXTURE_COORD_1:
            case GL_MAP2_TEXTURE_COORD_2:
            case GL_MAP2_TEXTURE_COORD_3:
            case GL_MAP2_TEXTURE_COORD_4:
            case GL_MAP2_VERTEX_3:
            case GL_MAP2_VERTEX_4:
            case GL_AUTO_NORMAL:
                attributeRestorationMask |= GL_ENABLE_BIT | GL_EVAL_BIT;
                break;
            case GL_FOG:
                attributeRestorationMask |= GL_ENABLE_BIT;
                // NOTE: don't add a break here, we want both executed
            case GL_FOG_MODE:
                attributeRestorationMask |= GL_FOG_BIT;
                break;
            case GL_PERSPECTIVE_CORRECTION_HINT:
            case GL_POINT_SMOOTH_HINT:
            case GL_LINE_SMOOTH_HINT:
            case GL_POLYGON_SMOOTH_HINT:
            case GL_FOG_HINT:
                attributeRestorationMask |= GL_HINT_BIT;
                break;
            case GL_COLOR_MATERIAL:
            case GL_LIGHTING:
                attributeRestorationMask |= GL_ENABLE_BIT;
                // NOTE: don't add a break here, we want both executed
                break;
            case GL_COLOR_MATERIAL_FACE:
            case GL_LIGHT_MODEL_LOCAL_VIEWER:
            case GL_LIGHT_MODEL_TWO_SIDE:
            case GL_SHADE_MODEL:
                attributeRestorationMask |= GL_LIGHTING_BIT;
                break;
            case GL_LINE_SMOOTH:
            case GL_LINE_STIPPLE:
                attributeRestorationMask |= GL_ENABLE_BIT | GL_LINE_BIT;
                break;
            case GL_LIST_BASE:
                attributeRestorationMask |= GL_LIST_BIT;
                break;
            case GL_CULL_FACE:
            case GL_POLYGON_SMOOTH:
            case GL_POLYGON_STIPPLE:
            case GL_POLYGON_OFFSET_FILL:
            case GL_POLYGON_OFFSET_LINE:
            case GL_POLYGON_OFFSET_POINT:
                attributeRestorationMask |= GL_ENABLE_BIT;
            case GL_FRONT_FACE:
            case GL_POLYGON_MODE:
            case GL_POLYGON_OFFSET_FACTOR:
            case GL_POLYGON_OFFSET_UNITS:
                attributeRestorationMask |= GL_POLYGON_BIT;
                break;
            default:
                System.out.println( "unknown flag to reset: " + flag );
        }
    }

    public void addResetAttrib( int attrib ) {
        attributeRestorationMask |= attrib;
    }

    // resets color, color index, normal vector, texture coordinates, raster position, etc.
    public void addCurrentReset() {
        attributeRestorationMask |= GL_CURRENT_BIT;
    }

    protected void preRender( GLOptions options ) {
        if ( attributeRestorationMask != 0 ) {
            glPushAttrib( attributeRestorationMask );
        }
        if ( clientAttributeRestorationMask != 0 ) {
            glPushClientAttrib( clientAttributeRestorationMask );
        }
        for ( Integer flag : glEnableFlags ) {
            glEnable( flag );
        }
        for ( Integer flag : glDisableFlags ) {
            glDisable( flag );
        }
        for ( Integer flag : glEnableClientFlags ) {
            glEnableClientState( flag );
        }
        for ( Integer flag : glDisableClientFlags ) {
            glDisableClientState( flag );
        }
    }

    protected void postRender( GLOptions options ) {
        if ( attributeRestorationMask != 0 ) {
            glPopAttrib();
        }
        if ( clientAttributeRestorationMask != 0 ) {
            glPopClientAttrib();
        }
    }

    public List<GLNode> getChildren() {
        return children;
    }

    public void addChild( GLNode node ) {
        // don't re-add children
        if ( node.parent == this && children.contains( node ) ) {
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
