// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPCanvas extends PhetPCanvas {

    private static final Logger LOGGER = LoggingUtils.getLogger( MPCanvas.class.getCanonicalName() );
    private static final boolean SHOW_STAGE_BOUNDS = true && PhetApplication.getInstance().isDeveloperControlsEnabled();

    private final PNode rootNode;

    protected MPCanvas() {

        setBackground( MPConstants.CANVAS_COLOR );
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, MPConstants.STAGE_SIZE ) );
        if ( SHOW_STAGE_BOUNDS ) {
            addBoundsNode( MPConstants.STAGE_SIZE );
        }

        rootNode = new PNode();
        addWorldChild( rootNode );
    }

    // Adds a child node to the root node.
    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    // Removes a child node from the root node.
    protected void removeChild( PNode node ) {
        if ( node != null && rootNode.indexOfChild( node ) != -1 ) {
            rootNode.removeChild( node );
        }
    }
}