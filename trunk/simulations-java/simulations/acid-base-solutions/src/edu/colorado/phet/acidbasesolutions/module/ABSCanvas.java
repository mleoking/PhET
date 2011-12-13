// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.module;

import java.awt.Color;
import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.view.ABSConductivityTesterNode;
import edu.colorado.phet.acidbasesolutions.view.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.MagnifyingGlassNode;
import edu.colorado.phet.acidbasesolutions.view.PHColorKeyNode;
import edu.colorado.phet.acidbasesolutions.view.PHMeterNode;
import edu.colorado.phet.acidbasesolutions.view.PHPaperNode;
import edu.colorado.phet.acidbasesolutions.view.ReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Canvas (play area) for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSCanvas extends PhetPCanvas {

    private final PNode rootNode;

    public ABSCanvas( ABSModel model, boolean dev ) {
        super( ABSConstants.CANVAS_RENDERING_SIZE );
        setBackground( ABSColors.CANVAS_BACKGROUND );

        // Root of our scene graph, added to "world" so that we get automatic scaling
        rootNode = new PNode();
        addWorldChild( rootNode );

        // nodes
        PNode beakerNode = new BeakerNode( model.getBeaker() );
        PNode pHMeterNode = new PHMeterNode( model.getPHMeter() );
        PNode magnifyingGlassNode = new MagnifyingGlassNode( model.getMagnifyingGlass() );
        PNode concentrationGraphNode = new ConcentrationGraphNode( model.getConcentrationGraph() );
        PNode reactionEquationNode = new ReactionEquationNode( model.getReactionEquation() );
        PNode pHPaperNode = new PHPaperNode( model.getPHPaper() );
        PNode pHColorKeyNode = new PHColorKeyNode( model.getPHPaper() );
        PNode conductivityTesterNode = new ABSConductivityTesterNode( model.getConductivityTester(), ModelViewTransform.createIdentity(), Color.BLACK, Color.RED, Color.BLACK, dev );

        // rendering order
        addNode( pHMeterNode );
        addNode( pHColorKeyNode );
        addNode( pHPaperNode );
        addNode( conductivityTesterNode );
        addNode( beakerNode );
        addNode( magnifyingGlassNode );
        addNode( concentrationGraphNode );
        addNode( reactionEquationNode );

        pHColorKeyNode.setOffset( ABSConstants.PH_COLOR_KEY_LOCATION );
        // NOTE: all layout is handled via locations of model elements.
    }

    protected void addNode( PNode node ) {
        rootNode.addChild( node );
    }

    protected void centerRootNode() {
        centerNode( rootNode );
    }

    protected void centerNode( PNode node ) {
        if ( node != null ) {
            Dimension2D worldSize = getWorldSize();
            PBounds b = node.getFullBoundsReference();
            double xOffset = ( ( worldSize.getWidth() - b.getWidth() ) / 2 ) - PNodeLayoutUtils.getOriginXOffset( node );
            double yOffset = ( ( worldSize.getHeight() - b.getHeight() ) / 2 ) - PNodeLayoutUtils.getOriginYOffset( node );
            node.setOffset( xOffset, yOffset );
        }
    }

    // Centers the root node on the canvas when the canvas size changes.
    @Override protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
            centerRootNode();
        }
    }
}
