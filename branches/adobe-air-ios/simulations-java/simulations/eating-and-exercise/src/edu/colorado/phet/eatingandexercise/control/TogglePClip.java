// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 * This class can behave like a PPath or PClip depending on whether clipEnabled is set to false or true.
 * <p/>
 * Copied from PClip.  One way to design this to avoid code duplication would be to name the overriden methods in PClip to e.g. clipComputeFullBounds(),
 * and have computeFullBounds() call it.
 */
public class TogglePClip extends PPath {
    private boolean clipEnabled = false;

    public boolean isClipEnabled() {
        return clipEnabled;
    }

    public void setClipEnabled( boolean clipEnabled ) {
        this.clipEnabled = clipEnabled;
        repaint();
    }

    public PBounds computeFullBounds( PBounds dstBounds ) {
        if ( !clipEnabled ) {
            return super.computeFullBounds( dstBounds );
        }
        if ( dstBounds == null ) {
            dstBounds = new PBounds();
        }
        dstBounds.reset();
        dstBounds.add( getBoundsReference() );
        localToParent( dstBounds );
        return dstBounds;
    }

    public void repaintFrom( PBounds localBounds, PNode childOrThis ) {
        if ( !clipEnabled ) {
            super.repaintFrom( localBounds, childOrThis );
            return;
        }
        if ( childOrThis != this ) {
            Rectangle2D.intersect( getBoundsReference(), localBounds, localBounds );
            super.repaintFrom( localBounds, childOrThis );
        }
        else {
            super.repaintFrom( localBounds, childOrThis );
        }
    }

    protected void paint( PPaintContext paintContext ) {
        if ( !clipEnabled ) {
            super.paint( paintContext );
            return;
        }
        Paint p = getPaint();
        if ( p != null ) {
            Graphics2D g2 = paintContext.getGraphics();
            g2.setPaint( p );
            g2.fill( getPathReference() );
        }
        paintContext.pushClip( getPathReference() );
    }

    protected void paintAfterChildren( PPaintContext paintContext ) {
        if ( !clipEnabled ) {
            super.paintAfterChildren( paintContext );
            return;
        }
        paintContext.popClip( getPathReference() );
        if ( getStroke() != null && getStrokePaint() != null ) {
            Graphics2D g2 = paintContext.getGraphics();
            g2.setPaint( getStrokePaint() );
            g2.setStroke( getStroke() );
            g2.draw( getPathReference() );
        }
    }

    public boolean fullPick( PPickPath pickPath ) {
        if ( !clipEnabled ) {
            return super.fullPick( pickPath );
        }
        if ( getPickable() && fullIntersects( pickPath.getPickBounds() ) ) {
            pickPath.pushNode( this );
            pickPath.pushTransform( getTransformReference( false ) );

            if ( pick( pickPath ) ) {
                return true;
            }

            if ( getChildrenPickable() && getPathReference().intersects( pickPath.getPickBounds() ) ) {
                int count = getChildrenCount();
                for ( int i = count - 1; i >= 0; i-- ) {
                    PNode each = getChild( i );
                    if ( each.fullPick( pickPath ) ) {
                        return true;
                    }
                }
            }

            if ( pickAfterChildren( pickPath ) ) {
                return true;
            }

            pickPath.popTransform( getTransformReference( false ) );
            pickPath.popNode( this );
        }

        return false;
    }
}
