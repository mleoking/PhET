// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view.pictures;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * @author Sam Reid
 */
public class IncreaseDecreaseButton extends PNode {

    private final SingleButton subtractButton;
    private final SingleButton addButton;

    public static class SingleButton extends PNode {
        public SingleButton( final BufferedImage buttonImage, final BufferedImage pressedButtonImage, final VoidFunction0 effect ) {
            final PImage node = new PImage( buttonImage );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                boolean pressed = false;

                @Override public void mousePressed( final PInputEvent event ) {
                    node.setImage( pressedButtonImage );
                    pressed = true;
                }

                @Override public void mouseReleased( final PInputEvent event ) {
                    if ( node.getImage() == pressedButtonImage && pressed ) {
                        effect.apply();
                    }
                    node.setImage( buttonImage );
                    pressed = false;
                }

                @Override public void mouseExited( final PInputEvent event ) {
                    node.setImage( buttonImage );
                }

                @Override public void mouseEntered( final PInputEvent event ) {
                    if ( pressed ) {
                        node.setImage( pressedButtonImage );
                    }
                }
            } );
            addChild( node );
        }

        public void setAllPickable( final boolean b ) {
            setPickable( b );
            setChildrenPickable( b );
        }
    }

    public IncreaseDecreaseButton( final VoidFunction0 add, VoidFunction0 subtract ) {
        subtractButton = new SingleButton( multiScaleToWidth( MINUS_BUTTON, 50 ), multiScaleToWidth( MINUS_BUTTON_PRESSED, 50 ), subtract );
        addButton = new SingleButton( multiScaleToWidth( PLUS_BUTTON, 50 ), multiScaleToWidth( PLUS_BUTTON_PRESSED, 50 ), add );
        addChild( new VBox( addButton, subtractButton ) );
    }

    public PInterpolatingActivity hideIncreaseButton() {
        addButton.setAllPickable( false );
        return addButton.animateToTransparency( 0, 200 );
    }

    public PInterpolatingActivity hideDecreaseButton() {
        subtractButton.setAllPickable( false );
        return subtractButton.animateToTransparency( 0, 200 );
    }

    public PInterpolatingActivity showIncreaseButton() {
        addButton.setAllPickable( true );
        return addButton.animateToTransparency( 1, 200 );
    }

    public PInterpolatingActivity showDecreaseButton() {
        subtractButton.setAllPickable( true );
        return subtractButton.animateToTransparency( 1, 200 );
    }

}