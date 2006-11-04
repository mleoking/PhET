/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Arranges graphics into screen and world graphics,
 * which may have separate transforms, and may
 * be interleaved.
 */

public class PhetRootPNode extends PNode {
    private PNode worldNode = new PNode();//used for storing the transform only, nodes are not added to this
    private PNode screenNode = new PNode();//used for storing the transform only, nodes are not added to this

    /**
     * This override of indexOfChild gets the index of the child, first directly, then in the world, then in the screen.
     *
     * @param child
     * @return the index.
     */
    public int indexOfChild( PNode child ) {
        int index = super.indexOfChild( child );
        if( index >= 0 ) {
            return index;
        }
        index = super.indexOfChild( new WorldChild( child ) );
        if( index >= 0 ) {
            return index;
        }
        index = super.indexOfChild( new ScreenChild( child ) );
        if( index >= 0 ) {
            return index;
        }
        return -1;
    }

    /**
     * Adds the child as a screen child.
     *
     * @param screenChild
     */
    public void addScreenChild( PNode screenChild ) {
        ScreenChild child = new ScreenChild( screenChild );
        child.setTransform( screenNode.getTransform() );
        addChild( child );
    }

    /**
     * Adds the child as a screen child at the specified index.
     *
     * @param index
     * @param node
     */
    public void addScreenChild( int index, PNode node ) {
        ScreenChild child = new ScreenChild( node );
        child.setTransform( screenNode.getTransform() );
        addChild( index, child );
    }

    public void addWorldChild( int index, PNode graphic ) {
        WorldChild worldChild = new WorldChild( graphic );
        worldChild.setTransform( worldNode.getTransform() );
        addChild( index, worldChild );
    }

    /**
     * Adds the child to the world.
     *
     * @param worldChild
     */
    public void addWorldChild( PNode worldChild ) {
        WorldChild child = new WorldChild( worldChild );
        child.setTransform( worldNode.getTransform() );
        addChild( child );
    }

    private ArrayList getChildren( Class type ) {
        ArrayList list = new ArrayList();
        for( int i = 0; i < getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if( type.isAssignableFrom( child.getClass() ) ) {
                list.add( child );
            }
        }
        return list;
    }

    /**
     * Translates all world node containers.
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

    private ArrayList getWorldChildren() {
        return getChildren( WorldChild.class );
    }

    /**
     * Sets the world node container transforms.
     *
     * @param worldTransform
     */
    public void setWorldTransform( AffineTransform worldTransform ) {
        worldNode.setTransform( worldTransform );
        updateWorldNodes();
    }

    protected void updateWorldNodes() {
        ArrayList worldChildren = getWorldChildren();
        for( int i = 0; i < worldChildren.size(); i++ ) {
            PNode node = (PNode)worldChildren.get( i );
            node.setTransform( worldNode.getTransformReference( true ) );
        }
    }

    /**
     * Converts the specified point global to screen coordinates.
     *
     * @param point
     */
    public void globalToScreen( Point2D point ) {
        screenNode.globalToLocal( point );
    }

    public void globalToScreen( Rectangle2D rect ) {
        screenNode.globalToLocal( rect );
    }

    public void globalToScreen( PDimension d ) {
        screenNode.globalToLocal( d );
    }

    /**
     * Converts the specified point from global to world coordinates.
     *
     * @param point
     */
    public void globalToWorld( Point2D point ) {
        worldNode.globalToLocal( point );
    }

    public void globalToWorld( PDimension d ) {
        worldNode.globalToLocal( d );
    }

    public void worldToScreen( Point2D pt ) {
        worldNode.localToGlobal( pt );
        screenNode.globalToLocal( pt );
    }

    public void screenToWorld( Dimension2D dim ) {
        screenNode.localToGlobal( dim );
        worldNode.globalToLocal( dim );
    }

    public void setWorldScale( double scale ) {
        worldNode.setScale( scale );
        updateWorldNodes();
    }

    private static class WrapperNode extends PNode {
        private PNode node;

        public WrapperNode( PNode node ) {
            this.node = node;
            addChild( node );
        }

        public boolean equals( Object obj ) {
            return obj instanceof WrapperNode && ( (WrapperNode)obj ).node == node;
        }
    }

    private static class ScreenChild extends WrapperNode {
        public ScreenChild( PNode node ) {
            super( node );
        }
    }

    private static class WorldChild extends WrapperNode {
        public WorldChild( PNode node ) {
            super( node );
        }
    }
}