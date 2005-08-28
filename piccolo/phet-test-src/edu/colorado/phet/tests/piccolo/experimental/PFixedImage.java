/*
 * Copyright (c) 2002-@year@, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of the University of Maryland nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Piccolo was written at the Human-Computer Interaction Laboratory www.cs.umd.edu/hcil by Jesse Grosjean
 * under the supervision of Ben Bederson. The Piccolo website is www.cs.umd.edu/hcil/piccolo.
 */
package edu.colorado.phet.tests.piccolo.experimental;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Attempt to maintain image size.
 */
public class PFixedImage extends PImage {

    public PFixedImage( Image newImage ) {
        super( newImage );
        addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                if( !fixingTx ) {
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
        if( getImage() != null ) {
            double iw = image.getWidth( null );
            double ih = image.getHeight( null );
            PBounds b = getBoundsReference();
            Graphics2D g2 = paintContext.getGraphics();

            if( b.x != 0 || b.y != 0 || b.width != iw || b.height != ih ) {
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
