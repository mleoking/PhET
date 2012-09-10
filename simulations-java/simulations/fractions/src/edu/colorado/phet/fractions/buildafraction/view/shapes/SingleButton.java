// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Button designed for use with the spinner button images for consistent look within the sim.
 *
 * @author Sam Reid
 */
class SingleButton extends PNode {
    private final BufferedImage buttonImage;
    private final BufferedImage pressedButtonImage;
    private final BufferedImage disabledImage;
    private final PImage node;
    private boolean pressed = false;

    public SingleButton( final BufferedImage buttonImage, final BufferedImage pressedButtonImage, BufferedImage disabledImage, final VoidFunction0 effect ) {
        this.buttonImage = buttonImage;
        this.pressedButtonImage = pressedButtonImage;
        this.disabledImage = disabledImage;
        node = new PImage( buttonImage );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {

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

    public void setEnabled( final boolean enabled ) {
        if ( enabled ) {
            node.setImage( pressed ? pressedButtonImage : buttonImage );
        }
        else {
            node.setImage( disabledImage );
        }
    }
}