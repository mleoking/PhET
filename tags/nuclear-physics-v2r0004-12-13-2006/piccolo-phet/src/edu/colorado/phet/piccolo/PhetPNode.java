/* Copyright 2003-2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.PNode;

/**
 * PhetPNode provides useful extensions to the 
 * standard PNode behavior and public interface.
 * <p>
 * Features include:
 * <p>
 * <ul>
 * <li>node is not pickable (does not receive input events) when it is invisible
 * <li>setTransform does nothing if the transform is not different
 * <li>setOffset does nothing if the offset is not different
 * <li>added isVisible method
 * </ul>
 *
 * @author Sam Reid / Chris Malley
 * @version $Revision$
 */
public class PhetPNode extends PNode {
    
    private boolean pickable; // keep track of node's pickable property
    private boolean childrenPickable; // keep track of children's pickable property
    
    /**
     * Constructs a new PNode.
     */
    public PhetPNode() {
        super();
        // Remember the default state of pickability.
        pickable = getPickable();
        childrenPickable = getChildrenPickable();
    }

    /**
     * Constructs a new PNode with a specified child.
     * This is useful for wrapping some PNode in a PhetPNode
     * in order to take advantage of some feature of PhetPNode.
     * 
     * @param node
     */
    public PhetPNode( PNode node ) {
        this();
        addChild( node );
    }

    /**
     * Sets the transform.
     * If the transform is not different than the current offset,
     * then calling this method does nothing.
     * 
     * @param transform
     */
    public void setTransform( AffineTransform transform ) {
        if( !getTransform().equals( transform ) ) {
            super.setTransform( transform );
        }
    }

    /**
     * Sets the offset.
     * If the offset is not different than the current offset,
     * then calling this method does nothing.
     * 
     * @param point
     */
    public void setOffset( Point2D point ) {
        if( !getOffset().equals( point ) ) {
            super.setOffset( point );
        }
    }

    /**
     * Sets the offset.
     * If the offset is not different than the current offset,
     * then calling this method does nothing.
     * 
     * @param x
     * @param y
     */
    public void setOffset( double x, double y ) {
        if( getOffset().getX() != x || getOffset().getY() != y ) {
            super.setOffset( x, y );
        }
    }
    
    /**
     * Determines if the node is visible.
     * This method should be in the PNode interface but isn't.
     * 
     * @return true or false
     */
    public boolean isVisible() {
        return super.getVisible();
    }
    
    /**
     * Sets the visibility of the node.
     * <p>
     * When a node is made invisible, the pickable flag for 
     * the node and its children is set to false. This prevents
     * input events from being passed to invisible nodes.
     * <p>
     * When a node is made visible, the pickable property of the 
     * node and its children is restored to the value that was 
     * specified in the most recent calls to methods setPickable
     * and setChildrenPickable. 
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if( visible ) {
            // Restore pickable property when node becomes visible.
            super.setPickable( pickable );
            super.setChildrenPickable( childrenPickable );
        }
        else {
            // Turn off pickable proprety when node becomes invisible.
            super.setPickable( false );
            super.setChildrenPickable( false );
        }
    }
    
    /**
     * Sets the pickable flag for this node.
     * Only pickable nodes will receive input events.
     * <p>
     * If this node is invisible, then setting its pickable flag
     * is deferred until the node becomes visible.
     * The following code will therefore report "false" and "true".
     * <p>
     * <pre>
     *     setVisible( false );
     *     setPickable( true );
     *     System.out.println( getPickable() );
     *     setVisible( true );
     *     System.out.println( getPickable() );
     * </pre>
     * 
     * @param pickable true or false
     */
    public void setPickable( boolean pickable ) {
        if( getVisible() ) {
            super.setPickable( pickable );
        }
        this.pickable = pickable;
    }
    
    /**
     * Sets the pickable flag for this node's children.
     * If this flag is false then this node will not try to pick its children.
     * <p>
     * If this node is invisible, then setting children's pickable flag
     * is deferred until this node becomes visible.
     * The following code will therefore report "false" and "true".
     * <p>
     * <pre>
     *     setVisible( false );
     *     setChildrenPickable( true );
     *     System.out.println( getChildrenPickable() );
     *     setVisible( true );
     *     System.out.println( getChildrenPickable() );
     * </pre>
     * @param pickable true or false
     */
    public void setChildrenPickable( boolean childrenPickable ) {
        if( getVisible() ) {
            super.setChildrenPickable( childrenPickable );
        }
        this.childrenPickable = childrenPickable;
    }
}
