// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Piccolo node used to show the "Go!" and "Stop" buttons in the center of the play area, used to start and stop the tug-of-war game.
 *
 * @author Sam Reid
 */
class ImageButtonNodeWithText extends PNode {
    private final BufferedImage hover;
    private final PImage imageNode;

    public ImageButtonNodeWithText( final IUserComponent component, final BufferedImage up, final BufferedImage hover, final BufferedImage pressed, final String text, final VoidFunction0 effect ) {
        this.hover = hover;
        imageNode = new PImage( up );
        addChild( imageNode );
        final PhetPText textNode = new PhetPText( text );
        textNode.scale( up.getWidth() / textNode.getFullWidth() * 0.65 );
        addChild( textNode );

        textNode.centerFullBoundsOnPoint( imageNode.getFullBounds().getCenter2D() );

        //account for shadow
        textNode.translate( -4 / textNode.getScale(), -4 / textNode.getScale() );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseEntered( final PInputEvent event ) {
                super.mouseEntered( event );
                hover();
            }

            @Override public void mouseExited( final PInputEvent event ) {
                imageNode.setImage( up );
            }

            @Override public void mousePressed( final PInputEvent event ) {
                super.mousePressed( event );
                imageNode.setImage( pressed );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                super.mouseReleased( event );

                SimSharingManager.sendButtonPressed( component );
                effect.apply();
                imageNode.setImage( up );
            }
        } );
    }

    //Need to show the highlight any time the mouse is over the object, even if the button was shown when the mouse was already there.
    public void hover() { imageNode.setImage( hover ); }
}