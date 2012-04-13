// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGCanvas extends PhetPCanvas {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( LGCanvas.class.getCanonicalName() );

    private static final Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );
    private static final boolean SHOW_STAGE_BOUNDS = PhetApplication.getInstance().getSimInfo().hasCommandLineArg( "-showStageBounds" );

    private final PNode rootNode;

    protected LGCanvas() {

        setBackground( LGColors.CANVAS_COLOR );

        setWorldTransformStrategy( new CenteredStage( this, STAGE_SIZE ) );
        if ( SHOW_STAGE_BOUNDS ) {
            addBoundsNode( STAGE_SIZE );
        }

        rootNode = new PNode();
        addWorldChild( rootNode );
    }

    protected PNode getRootNode() {
        return rootNode;
    }

    // Adds a child node to the root node.
    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected Dimension2D getStageSize() {
        return STAGE_SIZE;
    }

    protected Rectangle2D getStageBounds() {
        return new Rectangle2D.Double( 0, 0, STAGE_SIZE.getWidth(), STAGE_SIZE.getHeight() );
    }

    // Centers the root node on the stage.
    protected void centerRootNodeOnStage() {
        rootNode.setOffset( ( ( STAGE_SIZE.getWidth() - rootNode.getFullBoundsReference().getWidth() ) / 2 ) - PNodeLayoutUtils.getOriginXOffset( rootNode ),
                            ( ( STAGE_SIZE.getHeight() - rootNode.getFullBoundsReference().getHeight() ) / 2 ) - PNodeLayoutUtils.getOriginYOffset( rootNode ) );
    }

    // Scales the root node to fit in the bounds of the stage.
    protected void scaleRootNodeToFitStage() {
        double xScale = STAGE_SIZE.getWidth() / rootNode.getFullBoundsReference().getWidth();
        double yScale = STAGE_SIZE.getHeight() / rootNode.getFullBoundsReference().getHeight();
        if ( xScale < 1 || yScale < 1 ) {
            double scale = Math.min( xScale, yScale );
            LOGGER.info( "rootNode won't fit in the play area, scaling rootNode by " + scale + " for " + getClass().getName() );
            rootNode.scale( scale );
        }
    }
}