// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.Function1;
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
 * Base class for all indicators used in the Game to tell the user
 * whether their guess is balanced or unbalanced.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GameResultNode extends PhetPNode {

    private static final PhetFont FONT = new PhetFont( 24 );
    private static final Color BACKGROUND = new Color( 180, 205, 255 );
    private static final double MARGIN = 20;
    private static final double FACE_DIAMETER = 100;

    /**
     * @param smile
     * @param createContentFunction function that creates the content of the dialog that will appear below the face node
     */
    public GameResultNode( boolean smile, Function1<PhetFont, PNode> createContentFunction ) {

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

        // content
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

        // add a transparent background
        double w = getFullBoundsReference().getWidth() + ( 2 * MARGIN );
        double h = getFullBoundsReference().getHeight() + ( 2 * MARGIN );
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        backgroundNode.setPaint( BACKGROUND );
        addChild( backgroundNode );
        backgroundNode.moveToBack();
        backgroundNode.setOffset( 0, 0 );
        parentNode.translate( MARGIN, MARGIN );

        // close button at upper-right
        PImage closeButtonNode = new PImage( PhetCommonResources.getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON ) );
        closeButtonNode.scale( 1.5 );
        addChild( closeButtonNode );
        closeButtonNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseReleased( PInputEvent event ) {
                setVisible( false );
            }
        });
        x = backgroundNode.getFullBoundsReference().getMaxX() - closeButtonNode.getFullBoundsReference().getWidth() - 5;
        y = backgroundNode.getFullBoundsReference().getMinY() + 5;
        closeButtonNode.setOffset( x, y );
    }
}
