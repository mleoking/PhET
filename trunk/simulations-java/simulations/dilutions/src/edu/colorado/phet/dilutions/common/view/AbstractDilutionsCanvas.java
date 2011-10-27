// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AbstractDilutionsCanvas extends PhetPCanvas {

    private static final Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );
    private static final Color CANVAS_COLOR = Color.WHITE;
    private static final boolean SHOW_STAGE_BOUNDS = PhetApplication.getInstance().getSimInfo().hasCommandLineArg( "showStageBounds" );

    private final PNode rootNode;

    protected AbstractDilutionsCanvas() {

        setBackground( CANVAS_COLOR );

        setWorldTransformStrategy( new CenteredStage( this, STAGE_SIZE ) );
        if ( SHOW_STAGE_BOUNDS ) {
            addBoundsNode( STAGE_SIZE );
        }

        rootNode = new PNode();
        addWorldChild( rootNode );
    }

    // Adds a child node to the root node.
    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected Dimension2D getStageSize() {
        return STAGE_SIZE;
    }

    protected void centerRootNode() {
        rootNode.setOffset( ( ( STAGE_SIZE.getWidth() - rootNode.getFullBoundsReference().getWidth() ) / 2 ) - PNodeLayoutUtils.getOriginXOffset( rootNode ),
                            ( ( STAGE_SIZE.getHeight() - rootNode.getFullBoundsReference().getHeight() ) / 2 ) - PNodeLayoutUtils.getOriginYOffset( rootNode ) );
    }
}