// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test.experimental;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Attempt to maintain image size.
 */
public class PFixedImage extends PImage {

    public PFixedImage( Image newImage ) {
        super( newImage );
        addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if ( !fixingTx ) {
                    fixingTx = true;
                    fixTx();
                    fixingTx = false;
                }
            }
        } );
    }

    private void fixTx() {
        AffineTransform affineTransform = getTransformReference( true );
        affineTransform.setToTranslation( affineTransform.getTranslateX(), affineTransform.getTranslateY() );
        setTransform( affineTransform );
        System.out.println( "affineTransform = " + affineTransform );
    }

    boolean fixingTx = false;

    protected void paint( PPaintContext paintContext ) {
        Image image = getImage();
        if ( getImage() != null ) {
            double iw = image.getWidth( null );
            double ih = image.getHeight( null );
            PBounds b = getBoundsReference();
            Graphics2D g2 = paintContext.getGraphics();

            if ( b.x != 0 || b.y != 0 || b.width != iw || b.height != ih ) {
//                System.out.println( "g2 = " + g2 );
                g2.translate( b.x, b.y );
                g2.drawImage( image, 0, 0, null );
                g2.translate( -b.x, -b.y );
            }
            else {
//                System.out.println( "g2x = " + g2 );
//                AffineTransform tx = g2.getTransform();
//                g2.setTransform( new AffineTransform() );
//                g2.scale(1.2,1.2);
//                g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
//                g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
//                g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
//                g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED );
//                tx = getTransform();
                g2.drawImage( image, 0, 0, null );
//				g2.drawRenderedImage((RenderedImage)image, new AffineTransform( ) );
//                g2.setTransform( tx );
            }
        }
    }

}
