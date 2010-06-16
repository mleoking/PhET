/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module;

import java.awt.geom.Dimension2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.view.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.MagnifyingGlassNode;
import edu.colorado.phet.acidbasesolutions.view.PHMeterNode;
import edu.colorado.phet.acidbasesolutions.view.ReactionEquationNode;
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
    
    public ABSCanvas( ABSModel model ) {
        super( ABSConstants.CANVAS_RENDERING_SIZE );
        setBackground( ABSColors.CANVAS_BACKGROUND );
        
        // Root of our scene graph, added to "world" so that we get automatic scaling
        rootNode = new PNode();
        addWorldChild( rootNode );
        
        // nodes
        beakerNode = new BeakerNode( model );
        pHMeterNode = new PHMeterNode( model );
        magnifyingGlassNode = new MagnifyingGlassNode( model );
        concentrationGraphNode = new ConcentrationGraphNode( model );
        reactionEquationNode = new ReactionEquationNode( model );
        
        // rendering order
        addNode( pHMeterNode );
        addNode( beakerNode );
        addNode( magnifyingGlassNode );
        addNode( concentrationGraphNode );
        addNode( reactionEquationNode );
        
        // update the reaction equation's position when its bounds change
        reactionEquationNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateReactionEquationLayout();
                }
            }
        });
        updateReactionEquationLayout();
        
        // NOTE: all other layout is handled via locations of model elements.
    }    
    
    /*
     * Centers the reaction equation below the beaker.
     */
    private void updateReactionEquationLayout() {
        double x = beakerNode.getFullBoundsReference().getCenterX() - ( reactionEquationNode.getFullBoundsReference().getWidth() / 2 );
        double y = beakerNode.getFullBoundsReference().getMaxY() + 10;
        reactionEquationNode.setOffset( x, y );
    }
    
    protected void addNode( PNode node ) {
        rootNode.addChild( node );
    }
    
    protected void centerRootNode() {
        Dimension2D worldSize = getWorldSize();
        PBounds b = rootNode.getFullBoundsReference();
        double xOffset = Math.max( MIN_MARGIN, ( worldSize.getWidth() -  b.getWidth() ) / 2 );
        double yOffset = Math.max( MIN_MARGIN, ( worldSize.getHeight() -  b.getHeight() ) / 2 );
        rootNode.setOffset( xOffset, yOffset );
    }
}
