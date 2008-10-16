/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;


public class AbstractMediaButton extends PNode {
    
    private PImage buttonImageNode;
    private int buttonHeight;
    private boolean enabled = true;
    private boolean mousePressed = false;
    private boolean mouseEntered = false;
    
    private final Image disabledImage;
    private final Image mouseEnteredImage;
    private final Image armedImage;
    private CursorHandler cursorHandler;

    public AbstractMediaButton( int buttonHeight ) {
        
        this.buttonHeight = buttonHeight;
        BufferedImage image = getImage();
        image = BufferedImageUtils.multiScaleToHeight( image, buttonHeight );
        buttonImageNode = new PImage( image );
        addChild( buttonImageNode );

        disabledImage = new MyRescaleOp( 0.5, -100 ).filter( getImage(), null );
        mouseEnteredImage = new MyRescaleOp( 1.2, 0 ).filter( getImage(), null );
        armedImage = new MyRescaleOp( 0.9, 0 ).filter( getImage(), null );
        
        //TODO why are we not using CursorHandler here?
        addInputEventListener( new PBasicInputEventHandler() {

            public void mouseEntered( PInputEvent event ) {
                mouseEntered = true;
                updateImage();
            }

            public void mouseExited( PInputEvent event ) {
                mouseEntered = false;
                updateImage();
            }

            public void mousePressed( PInputEvent event ) {
                mousePressed = true;
                updateImage();
            }

            public void mouseReleased( PInputEvent event ) {
                mousePressed = false;
                updateImage();
            }
        } );
        cursorHandler = new CursorHandler();
        addInputEventListener( cursorHandler );
        addListener(new Listener(){
            public void enabledChanged() {
                if (isEnabled() ){
                    addInputEventListener( cursorHandler );
                }else{
                    removeInputEventListener( cursorHandler );
                }
            }
        });
    }

    private void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public boolean isMouseEntered() {
        return mouseEntered;
    }

    public void setEnabled( boolean b ) {
        if (this.enabled!=b){
        this.enabled = b;
        updateImage();
            notifyEnabledChanged();
        }
    }
    private ArrayList listeners=new ArrayList( );
    private void notifyEnabledChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener o = (Listener) listeners.get( i );
            o.enabledChanged();
        }
    }
    public static interface Listener{
        void enabledChanged();
    }

    protected void updateImage() {
        if ( !enabled ) {
            buttonImageNode.setImage( disabledImage );
        }
        else if ( !mouseEntered ) {
            buttonImageNode.setImage( getImage() );
        }
        else {
            if ( mousePressed ) {
                buttonImageNode.setImage( armedImage );
            }
            else {
                buttonImageNode.setImage( mouseEnteredImage );
            }
        }
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
