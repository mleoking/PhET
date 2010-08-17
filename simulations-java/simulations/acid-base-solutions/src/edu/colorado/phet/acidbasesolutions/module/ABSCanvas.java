/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.view.*;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ABSCanvas extends PhetPCanvas {
    
    private final PNode rootNode;
    private final BeakerNode beakerNode;
    private final PHMeterNode pHMeterNode;
    private final MagnifyingGlassNode magnifyingGlassNode;
    private final ConcentrationGraphNode concentrationGraphNode;
    private final ReactionEquationNode reactionEquationNode;
    private final PHPaperNode pHPaperNode;
    private final PNode pHColorKeyNode;
    private final ConductivityTesterNode conductivityTesterNode;
    
    public ABSCanvas( ABSModel model, boolean dev ) {
        super( ABSConstants.CANVAS_RENDERING_SIZE );
        setBackground( ABSColors.CANVAS_BACKGROUND );
        
        // Root of our scene graph, added to "world" so that we get automatic scaling
        rootNode = new PNode();
        addWorldChild( rootNode );
        
        // nodes
        beakerNode = new BeakerNode( model.getBeaker() );
        pHMeterNode = new PHMeterNode( model.getPHMeter() );
        magnifyingGlassNode = new MagnifyingGlassNode( model.getMagnifyingGlass() );
        concentrationGraphNode = new ConcentrationGraphNode( model.getConcentrationGraph() );
        reactionEquationNode = new ReactionEquationNode( model.getReactionEquation() );
        pHPaperNode = new PHPaperNode( model.getPHPaper() );
        pHColorKeyNode = new PHColorKeyNode( model.getPHPaper() );
        conductivityTesterNode = new ConductivityTesterNode( model.getConductivityTester(), dev );
        
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
            double yOffset = ( ( worldSize.getHeight() - b.getHeight() ) / 2 )- PNodeLayoutUtils.getOriginYOffset( node );
            node.setOffset( xOffset, yOffset );
        }
    }
    
    /*
     * Centers the root node on the canvas when the canvas size changes.
     */
    @Override
    protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
            centerRootNode();
        }
    }
}
