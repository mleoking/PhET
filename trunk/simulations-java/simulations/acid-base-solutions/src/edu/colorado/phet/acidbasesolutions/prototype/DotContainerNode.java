/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.prototype.DotNode.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for anything node that contains dots.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
abstract class DotContainerNode extends PComposite {
    
    private final PNode dotsParent;
    
    public DotContainerNode() {
        super();
        dotsParent = new PComposite();
    }
    
    protected PNode getDotsParent() {
        return dotsParent;
    }
    
    public void addDot( DotNode particleNode ) {
        dotsParent.addChild( particleNode );
    }
    
    public void removeAllDots() {
        dotsParent.removeAllChildren();
    }
    
    public void setDotDiameter( double diameter ) {
        int count = dotsParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            ((DotNode)dotsParent.getChild(i)).setDiameter( diameter );
        }
    }
    
    public void setHAColor( Color color ) {
        int count = dotsParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = dotsParent.getChild( i );
            if ( node instanceof HADotNode ) {
                node.setPaint( color );
            }
        }
    }
    
    public void setAColor( Color color ) {
        int count = dotsParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = dotsParent.getChild( i );
            if ( node instanceof ADotNode ) {
                node.setPaint( color );
            }
        }
    }
    
    public void setH3OColor( Color color ) {
        int count = dotsParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = dotsParent.getChild( i );
            if ( node instanceof H3ODotNode ) {
                node.setPaint( color );
            }
        }
    }
    
    public void setOHColor( Color color ) {
        int count = dotsParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = dotsParent.getChild( i );
            if ( node instanceof POHDotNode ) {
                node.setPaint( color );
            }
        }
    }
    
    public void setH2OColor( Color color ) {
        int count = dotsParent.getChildrenCount();
        for ( int i = 0; i < count; i++ ) {
            PNode node = dotsParent.getChild( i );
            if ( node instanceof H2ODotNode ) {
                node.setPaint( color );
            }
        }
    }
}
