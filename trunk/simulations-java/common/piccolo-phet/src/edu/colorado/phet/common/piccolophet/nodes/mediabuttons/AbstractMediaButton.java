/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;


public class AbstractMediaButton extends PNode {

    private PImage buttonImageNode;
    private int buttonHeight;

    public AbstractMediaButton( int buttonHeight ) {
        this.buttonHeight = buttonHeight;
        BufferedImage image = getImage();
        image = BufferedImageUtils.multiScaleToHeight( image, buttonHeight );
        buttonImageNode = new PImage( image );
        addChild( buttonImageNode );

        addInputEventListener( new PBasicInputEventHandler() {

            public void mouseEntered( PInputEvent event ) {
                buttonImageNode.setImage( new MyRescaleOp( 1.2, 0 ).filter( getImage(), null ) );
            }

            public void mouseExited( PInputEvent event ) {
                buttonImageNode.setImage( getImage() );
            }

            public void mousePressed( PInputEvent event ) {
                buttonImageNode.setImage( new MyRescaleOp( 0.9, 0 ).filter( getImage(), null ) );
            }

            public void mouseReleased( PInputEvent event ) {
                buttonImageNode.setImage( getImage() );
            }
        } );
    }

    static class MyRescaleOp extends LookupOp {

        public MyRescaleOp( final double scale, final double offset ) {
            super( new LookupTable( 0, 4 ) {
                public int[] lookupPixel( int[] src, int[] dest ) {
                    for ( int i = 0; i < src.length; i++ ) {
                        dest[i] = (int) MathUtil.clamp( 0, src[i] * scale + offset, 255 );
                    }
                    return dest;
                }
            }, null );
        }
    }

    protected BufferedImage getImage() {
        BufferedImage image = new PhetResources( "piccolo-phet" ).getImage( "button-template.png" );
        return BufferedImageUtils.multiScaleToHeight( image, buttonHeight );
    }

    public PDimension getButtonDimension() {
        return new PDimension( buttonImageNode.getFullBounds().width, buttonImageNode.getFullBounds().height );
    }

}
