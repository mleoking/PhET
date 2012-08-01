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
public class SingleButton extends PNode {
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