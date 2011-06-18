// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * PhetRootPNode provides a convenient mechanism for interleaving
 * "screen" and "world" nodes.
 * <p/>
 * "Screen" nodes are in screen coordinates and have an identity transform.
 * "World" nodes are intended to be in model coordinates, and can have some
 * arbitrary transform specified.
 * There is purposely no support for specifying a transform on screen
 * nodes; they are expected to have a unity transform, and a 1:1 mapping
 * to screen coordinates.
 * <p/>
 * The order that you call addScreenChild and addWorldChild determines the
 * rendering order of all nodes (screen and world).
 * When you call addScreenChild or addWorldChild, the node you provide
 * will be wrapped (see WrapperNode). World transforms will be specified
 * on the wrapper node, leaving your node's transform undisturbed.
 * Any nodes added directly via addChild will be interleaved with
 * the screen and world nodes, but you will be responsible for managing
 * their transforms.
 * <p/>
 * This node is intended for use by PhetPCanvas only, and should not be
 * used directly in simulations.
 */
public class PhetRootPNode extends PNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final PNode worldNode; // used for storing the transform only, nodes are not added to this
    private final PNode screenNode; // used for storing the transform only, nodes are not added to this

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public PhetRootPNode() {
        super();
        worldNode = new PNode();
        screenNode = new PNode();
    }

    //----------------------------------------------------------------------------
    // Node management
    //----------------------------------------------------------------------------

    /**
     * Adds a node as a world node.
     *
     * @param worldChild
     */
    public void addWorldChild( PNode worldChild ) {
        WorldNode child = new WorldNode( worldChild );
        child.setTransform( worldNode.getTransform() );
        addChild( child );
    }

    /**
     * Adds a node as a world node at the specified index.
     *
     * @param index
     * @param node
     */
    public void addWorldChild( int index, PNode node ) {
        WorldNode worldChild = new WorldNode( node );
        worldChild.setTransform( worldNode.getTransform() );
        addChild( index, worldChild );
    }

    /**
     * Removes a node that was added via addWorldChild.
     *
     * @param worldChild the node to be removed.
     */
    public void removeWorldChild( PNode worldChild ) {
        int index = super.indexOfChild( new WorldNode( worldChild ) );
        if ( index >= 0 ) {
            removeChild( index );
        }
    }

    /**
     * Adds a node as a screen node.
     *
     * @param screenChild
     */
    public void addScreenChild( PNode screenChild ) {
        ScreenNode child = new ScreenNode( screenChild );
        child.setTransform( screenNode.getTransform() );
        addChild( child );
    }

    /**
     * Adds a node as a screen node at the specified index.
     *
     * @param index
     * @param node
     */
    public void addScreenChild( int index, PNode node ) {
        ScreenNode child = new ScreenNode( node );
        child.setTransform( screenNode.getTransform() );
        addChild( index, child );
    }

    /**
     * Removes a node that was added via addScreenChild.
     *
     * @param screenChild the node to be removed.
     */
    public void removeScreenChild( PNode screenChild ) {
        int index = super.indexOfChild( new ScreenNode( screenChild ) );
        if ( index >= 0 ) {
            removeChild( index );
        }
    }

    /**
     * Gets the index of a specified node.
     * If the node was added via addScreenChild or addWorldChild, then
     * we are looking for the index of its wrapper node.
     *
     * @param child
     * @return the index, < 0 if not found
     */
    public int indexOfChild( PNode child ) {
        // find index of nodes that were added via addChild
        int index = super.indexOfChild( child );
        // if not found...
        if ( index < 0 ) {
            // look for children of WrapperNodes
            List children = getChildrenReference();
            for ( int i = 0; i < children.size(); i++ ) {
                PNode n = (PNode) children.get( i );
                if ( n instanceof WrapperNode ) {
                    if ( ( (WrapperNode) n ).getWrappedNode() == child ) {
                        index = i;
                        break;
                    }
                }
            }
        }
        return index;
    }

    /*
     * Gets all child nodes of a specified type.
     * 
     * @return ArrayList of nodes, possibly empty
     */
    private ArrayList getChildren( Class type ) {
        ArrayList list = new ArrayList();
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if ( type.isAssignableFrom( child.getClass() ) ) {
                list.add( child );
            }
        }
        return list;
    }

    /*
    * Gets all nodes of type WorldNode.
    *
    * @return ArrayList of WorldNode, possibly empty
    */
    private ArrayList getWorldChildren() {
        return getChildren( WorldNode.class );
    }

    //----------------------------------------------------------------------------
    // World transform management
    //----------------------------------------------------------------------------

    /**
     * Adds a listener for changes to the world transform.
     *
     * @param listener
     */
    public void addWorldTransformListener( PropertyChangeListener listener ) {
        worldNode.addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, listener );
    }

    /**
     * Removes a listener for changes to the world transform.
     *
     * @param listener
     */
    public void removeWorldTransformListener( PropertyChangeListener listener ) {
        worldNode.removePropertyChangeListener( PNode.PROPERTY_TRANSFORM, listener );
    }

    /**
     * Translates all world nodes.
     *
     * @param dx
     * @param dy
     */
    public void translateWorld( double dx, double dy ) {
        worldNode.translate( dx, dy );
        updateWorldNodes();
    }

    /**
     * Scales all world node containers about the specified point.
     *
     * @param scale
     * @param point
     */
    public void scaleWorldAboutPoint( double scale, Point2D point ) {
        worldNode.scaleAboutPoint( scale, point );
        updateWorldNodes();
    }

    /**
     * Sets the world node transform.
     *
     * @param worldTransform
     */
    public void setWorldTransform( AffineTransform worldTransform ) {
        worldNode.setTransform( worldTransform );
        updateWorldNodes();
    }

    /*
     * Visits all WorldNodes and updates their transform.
     */
    protected void updateWorldNodes() {
        ArrayList worldChildren = getWorldChildren();
        for ( int i = 0; i < worldChildren.size(); i++ ) {
            PNode node = (PNode) worldChildren.get( i );
            node.setTransform( worldNode.getTransformReference( true ) );
        }
    }

    /**
     * Gets the world transform scale.
     *
     * @return scale
     */
    public double getWorldScale() {
        return worldNode.getScale();
    }

    /**
     * Sets the world transform scale.
     *
     * @param scale
     */
    public void setWorldScale( double scale ) {
        worldNode.setScale( scale );
        updateWorldNodes();
    }

    //----------------------------------------------------------------------------
    // Transforms between coordinate spaces
    //----------------------------------------------------------------------------

    public void globalToScreen( Point2D point ) {
        screenNode.globalToLocal( point );
    }

    public void globalToScreen( Rectangle2D rect ) {
        screenNode.globalToLocal( rect );
    }

    public void globalToScreen( PDimension d ) {
        screenNode.globalToLocal( d );
    }

    public void globalToWorld( Point2D point ) {
        worldNode.globalToLocal( point );
    }

    public void globalToWorld( PDimension d ) {
        worldNode.globalToLocal( d );
    }

    public void worldToScreen( Rectangle2D rectangle2D ) {
        worldNode.localToGlobal( rectangle2D );
        screenNode.globalToLocal( rectangle2D );
    }

    public void worldToScreen( PDimension d ) {
        worldNode.localToGlobal( d );
        screenNode.globalToLocal( d );
    }

    public void worldToScreen( Point2D pt ) {
        worldNode.localToGlobal( pt );
        screenNode.globalToLocal( pt );
    }

    public void screenToWorld( Point2D pt ) {
        screenNode.localToGlobal( pt );
        worldNode.globalToLocal( pt );
    }

    public void screenToWorld( Dimension2D dim ) {
        screenNode.localToGlobal( dim );
        worldNode.globalToLocal( dim );
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /*
    * WrapperNode is used to wrap screen and world nodes.
    * When we call addScreenChild or addWorldChid, we pass in a PNode that may have
    * a meaningful transform attached to it.  We don't want to disturb that transform,
    * so we wrap our PNode in a WrapperNode.  Screen and world transforms will then
    * be applied to the WrapperNode, leaving our PNode's transform undisturbed.
    */
    private static abstract class WrapperNode extends PNode {
        private PNode node;

        public WrapperNode( PNode node ) {
            this.node = node;
            addChild( node );
        }

        public PNode getWrappedNode() {
            return node;
        }
    }

    /*
     * ScreenNode is the wrapper class for nodes that are added with addScreenChild.
     * This class serves as a marker class, so that we can identify the type of nodes
     * when we need to change the screen transform.
     */
    private static class ScreenNode extends WrapperNode {
        public ScreenNode( PNode node ) {
            super( node );
        }
    }

    /*
     * WorldNode is the wrapper class for nodes that are added with addWorldChild.
     * This class serves as a marker class, so that we can identify the type of nodes
     * when we need to change the world transform.
     */
    private static class WorldNode extends WrapperNode {
        public WorldNode( PNode node ) {
            super( node );
        }
    }
}