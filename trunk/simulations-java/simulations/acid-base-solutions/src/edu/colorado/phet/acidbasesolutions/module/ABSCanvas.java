/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.view.*;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Base class for all canvases.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ABSCanvas extends PhetPCanvas {
    
    private static final double MIN_MARGIN = 10;

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
        addNode( pHPaperNode );
        addNode( conductivityTesterNode );
        addNode( beakerNode );
        addNode( magnifyingGlassNode );
        addNode( concentrationGraphNode );
        addNode( reactionEquationNode );
        addNode( pHColorKeyNode );
        
        pHColorKeyNode.setOffset( ABSConstants.PH_COLOR_KEY_LOCATION );
        // NOTE: all layout is handled via locations of model elements.
        
        centerRootNode();
    }    
    
    protected void addNode( PNode node ) {
        rootNode.addChild( node );
    }
    
    protected void centerRootNode() {
        Dimension2D worldSize = getWorldSize();
        PBounds b = rootNode.getFullBoundsReference();
        double xOffset = Math.max( MIN_MARGIN, ( worldSize.getWidth() - b.getWidth() ) / 2 );
        double yOffset = Math.max( MIN_MARGIN, ( worldSize.getHeight() - b.getHeight() ) / 2 );
        rootNode.setOffset( xOffset, yOffset );
    }
}
