
package edu.colorado.phet.acidbasesolutions.module;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.view.*;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

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
    
}
