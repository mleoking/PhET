// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all "popups" used in the Game to tell the user whether their guess is balanced or unbalanced.
 * These indicators look like a dialog (aka "popup").
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GamePopupNode extends PhetPNode {

    private static final PhetFont FONT = new PhetFont( 24 );
    private static final Color BACKGROUND = new Color( 180, 205, 255 );
    private static final double MARGIN = 20;
    private static final double TITLE_BAR_HEIGHT = 25;
    private static final double FACE_DIAMETER = 100;

    /**
     * @param smile
     * @param createContentFunction function that creates the content of the dialog that will appear below the face node
     * @param closeButtonVisible
     * @param titleBarVisible
     */
    public GamePopupNode( boolean smile, boolean closeButtonVisible, boolean titleBarVisible, Function1<PhetFont, PNode> createContentFunction ) {

        // make draggable
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
            }
        });

        PNode parentNode = new PComposite();
        addChild( parentNode );

        // face
        FaceNode faceNode = new FaceNode( FACE_DIAMETER );
        if ( !smile ) {
            faceNode.frown();
        }
        parentNode.addChild( faceNode );

        // content (stuff below the face), function provided by subclasses
        PNode contentNode = createContentFunction.apply( FONT );
        parentNode.addChild( contentNode );

        // layout: content centered below face
        double maxWidth = Math.max( faceNode.getFullBoundsReference().getWidth(), contentNode.getFullBoundsReference().getWidth() );
        double x = ( maxWidth / 2 ) - ( faceNode.getFullBoundsReference().getWidth() / 2 );
        double y = 0;
        faceNode.setOffset( x, y );
        x = ( maxWidth / 2 ) - ( contentNode.getFullBoundsReference().getWidth() / 2 );
        y = faceNode.getFullBoundsReference().getMaxY() + 20;
        contentNode.setOffset( x - PNodeLayoutUtils.getOriginXOffset( contentNode ), y - PNodeLayoutUtils.getOriginYOffset( contentNode ) );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );

        // title bar
        final double titleBarWidth = getFullBoundsReference().getWidth() + ( 2 * MARGIN );
        final double titleBarHeight = ( titleBarVisible ? TITLE_BAR_HEIGHT : 0 );
        if ( titleBarVisible ) {
            PPath titleBarNode = new PPath( new Rectangle2D.Double( 0, 0, titleBarWidth, titleBarHeight ) );
            titleBarNode.setPaint( new Color( 155, 180, 230 ) );
            addChild( titleBarNode );
            titleBarNode.moveToBack();
        }

        // background
        double h = getFullBoundsReference().getHeight() + ( 2 * MARGIN ) + titleBarHeight;
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, titleBarWidth, h ) );
        backgroundNode.setPaint( BACKGROUND );
        addChild( backgroundNode );
        backgroundNode.moveToBack();
        parentNode.translate( MARGIN, titleBarHeight + MARGIN );

        // close button at upper-right, scaled to fit inside title bar
        if ( closeButtonVisible ) {
            final double margin = 3;
            PImage closeButtonNode = new PImage( PhetCommonResources.getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON ) );
            if ( titleBarVisible ) {
                closeButtonNode.scale( ( titleBarHeight - ( 2 * margin ) ) / closeButtonNode.getFullBoundsReference().getHeight() );
            }
            addChild( closeButtonNode );
            closeButtonNode.addInputEventListener( new PBasicInputEventHandler() {
                @Override
                public void mouseReleased( PInputEvent event ) {
                    setVisible( false );
                }
            } );
            x = backgroundNode.getFullBoundsReference().getMaxX() - closeButtonNode.getFullBoundsReference().getWidth() - margin;
            y = backgroundNode.getFullBoundsReference().getMinY() + margin;
            closeButtonNode.setOffset( x, y );
        }
    }
}
