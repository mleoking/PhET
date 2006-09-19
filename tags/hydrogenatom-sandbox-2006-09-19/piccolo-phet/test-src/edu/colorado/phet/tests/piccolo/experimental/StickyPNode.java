/* Copyright 2004, Sam Reid */
package edu.colorado.phet.tests.piccolo.experimental;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Aug 20, 2005
 * Time: 6:16:07 PM
 * Copyright (c) Aug 20, 2005 by Sam Reid
 */

public class StickyPNode extends PNode {
    private PNode pNode;
    private boolean recursing = false;

    public StickyPNode( PNode pNode ) {
        this.pNode = pNode;
        addChild( pNode );
    }

//    public void fullPaint( PPaintContext paintContext ) {
//        if( getVisible() && fullIntersects( paintContext.getLocalClip() ) ) {
//
//            if( !recursing ) {
//                recursing = true;
//                setScale( getScale() / paintContext.getScale() );
//                recursing = false;
//            }
//            if( true ) {
//                paintContext.pushTransform( getTransformReference( true ) );
//                paintContext.pushTransparency( getTransparency() );
//                paintContext.pushRenderingHints( getRenderingHints() );
//
//                if( !getOccluded() ) {
//                    paint( paintContext );
//                }
//
//                int count = getChildrenCount();
//                for( int i = 0; i < count; i++ ) {
//                    PNode each = (PNode)getChildrenReference().get( i );
//                    each.fullPaint( paintContext );
//                }
//
//                paintAfterChildren( paintContext );
//
//                paintContext.popRenderingHints( super.getRenderingHints() );
//                paintContext.popTransparency( getTransparency() );
//                paintContext.popTransform( getTransform() );
//            }
//        }
//    }

//    protected void paint( PPaintContext paintContext ) {
//        if( !recursing ) {
//            recursing = true;
//            setScale( getScale() / paintContext.getScale() );
//            recursing = false;
//        }
//        if( paintContext.getScale() == 1.0 ) {
//            AffineTransform at = paintContext.getGraphics().getTransform();
//            at.setTransform( 1.0, 1.0, at.getShearX(), at.getShearY(), at.getTranslateX(), at.getTranslateY() );
//
////            at.setTransform( at.getScaleX(), at.getShearY(), at.getShearX(), at.getScaleY(), newX, newY );
////            paintContext.getGraphics().getTransform().setTransform( 0, 0, 0, 0, 0, 0 );
//            super.paint( paintContext );
//        }
//    }

    protected void paint( PPaintContext paintContext ) {
        if( !recursing ) {
            recursing = true;
            setScale( getScale() / paintContext.getScale() );
            recursing = false;
        }
        if( paintContext.getScale() == 1.0 ) {
            AffineTransform at = paintContext.getGraphics().getTransform();
            AffineTransform orig = paintContext.getGraphics().getTransform();
            at.setTransform( 1.0, 1.0, at.getShearX(), at.getShearY(), at.getTranslateX(), at.getTranslateY() );

//            at.setTransform( at.getScaleX(), at.getShearY(), at.getShearX(), at.getScaleY(), newX, newY );
            paintContext.getGraphics().setTransform( at );
            super.paint( paintContext );
            paintContext.getGraphics().setTransform( orig );
        }
    }
//}
}