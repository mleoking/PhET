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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * This class is meant to provide useful deafaults for subclasses of PNode that are not
 * supported directly by the API.  For example, this class checks to see if offsets are different
 * before invalidating the bounds.
 * <p/>
 * Also, when invisible, the node (and its children) become unpickable.
 *
 * @author Sam Reid
 */

public class PhetPNode extends PNode {
    public PhetPNode() {
    }

    public PhetPNode( PNode node ) {
        addChild( node );
    }

    public void setTransform( AffineTransform newTransform ) {
        if( !getTransform().equals( newTransform ) ) {
            super.setTransform( newTransform );
        }
    }

    public void setOffset( Point2D point ) {
        if( !getOffset().equals( point ) ) {
            super.setOffset( point );
        }
    }

    public void setOffset( double x, double y ) {
        if( getOffset().getX() != x || getOffset().getY() != y ) {
            super.setOffset( x, y );
        }
    }

    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        setPickable( isVisible );
        setChildrenPickable( isVisible );
    }
}
