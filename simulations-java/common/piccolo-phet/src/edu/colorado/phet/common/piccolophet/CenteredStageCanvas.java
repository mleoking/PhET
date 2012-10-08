// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * PhET Piccolo canvas that provides a "centered stage" transform strategy (see CenteredStage) and a root node.
 * The root node is in the "world" coordinate frame (see PhetPCanvas) and methods are provided for centering
 * and scaling the root node on the stage.
 * <p>
 * Note: A wart in this implementation is that you can change the transform strategy so that the canvas
 * is no longer a centered stage. This could be prevented by overriding setWorldTransformStrategy to
 * throw an exception if called. But instead I'll just say "don't do that."
 * </p>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CenteredStageCanvas extends PhetPCanvas implements Resettable {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( CenteredStageCanvas.class.getCanonicalName() );

    private final PNode rootNode;
    private final Dimension2D stageSize;

    // Uses a default stage size that seems to be good for most sims.
    public CenteredStageCanvas() {
        this( new PDimension( 1008, 679 ) );
    }

    public CenteredStageCanvas( Dimension2D stageSize ) {

        this.stageSize = stageSize;

        setWorldTransformStrategy( new CenteredStage( this, stageSize ) );

        // Show the stage bounds if this program arg is specified.
        if ( PhetApplication.getInstance().getSimInfo().hasCommandLineArg( "-showStageBounds" ) ) {
            addBoundsNode( stageSize );
        }

        rootNode = new PNode();
        addWorldChild( rootNode );
    }

    // Adds a child node to the root node.
    public void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public Dimension2D getStageSize() {
        return stageSize;
    }

    public Rectangle2D getStageBounds() {
        return new Rectangle2D.Double( -rootNode.getXOffset(), -rootNode.getYOffset(), stageSize.getWidth(), stageSize.getHeight() );
    }

    // Centers the root node on the stage.
    public void centerRootNodeOnStage() {
        rootNode.setOffset( ( ( stageSize.getWidth() - rootNode.getFullBoundsReference().getWidth() ) / 2 ) - PNodeLayoutUtils.getOriginXOffset( rootNode ),
                            ( ( stageSize.getHeight() - rootNode.getFullBoundsReference().getHeight() ) / 2 ) - PNodeLayoutUtils.getOriginYOffset( rootNode ) );
    }

    // Scales the root node to fit in the bounds of the stage.
    public void scaleRootNodeToFitStage() {
        double xScale = stageSize.getWidth() / rootNode.getFullBoundsReference().getWidth();
        double yScale = stageSize.getHeight() / rootNode.getFullBoundsReference().getHeight();
        if ( xScale < 1 || yScale < 1 ) {
            double scale = Math.min( xScale, yScale );
            LOGGER.info( "rootNode won't fit in the play area, scaling rootNode by " + scale + " for " + getClass().getName() );
            rootNode.scale( scale );
        }
    }

    public void reset() {}
}