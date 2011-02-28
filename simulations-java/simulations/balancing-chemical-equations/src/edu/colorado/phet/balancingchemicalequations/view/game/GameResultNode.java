// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.game;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for all indicators used in the Game to tell the user
 * whether their guess is balanced or unbalanced.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GameResultNode extends PComposite {

    private static final PhetFont FONT = new PhetFont( 24 );
    private static final Color BACKGROUND = new Color( 180, 205, 255 );
    private static final double MARGIN = 20;
    private static final double FACE_DIAMETER = 100;

    private final boolean smile;

    public GameResultNode( boolean smile ) {
        this.smile = smile;

        // make draggable
        addInputEventListener( new PDragEventHandler() );
        addInputEventListener( new CursorHandler() );

        updateNode();
    }

    protected void updateNode() {

        removeAllChildren();

        PNode parentNode = new PComposite();
        addChild( parentNode );

        FaceNode faceNode = new FaceNode( FACE_DIAMETER );
        if ( !smile ) {
            faceNode.frown();
        }
        parentNode.addChild( faceNode );

        PNode iconsAndTextNode = createIconsAndText( FONT );
        parentNode.addChild( iconsAndTextNode );

        // layout
        double maxWidth = Math.max( faceNode.getFullBoundsReference().getWidth(), iconsAndTextNode.getFullBoundsReference().getWidth() );
        double x = ( maxWidth / 2 ) - ( faceNode.getFullBoundsReference().getWidth() / 2 );
        double y = 0;
        faceNode.setOffset( x, y );
        x = ( maxWidth / 2 ) - ( iconsAndTextNode.getFullBoundsReference().getWidth() / 2 );
        y = faceNode.getFullBoundsReference().getMaxY() + 20;
        iconsAndTextNode.setOffset( x - PNodeLayoutUtils.getOriginXOffset( iconsAndTextNode ), y - PNodeLayoutUtils.getOriginYOffset( iconsAndTextNode ) );
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );

        // now add a transparent background
        double w = getFullBoundsReference().getWidth() + ( 2 * MARGIN );
        double h = getFullBoundsReference().getHeight() + ( 2 * MARGIN );
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        backgroundNode.setPaint( BACKGROUND );
        addChild( backgroundNode );
        backgroundNode.moveToBack();

        // layout the top-level nodes
        backgroundNode.setOffset( 0, 0 );
        parentNode.translate( MARGIN, MARGIN );
    }

    protected abstract PNode createIconsAndText( PhetFont font );
}
