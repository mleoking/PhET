/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.control.BeakerControlsNode;
import edu.colorado.phet.acidbasesolutions.control.MiscControlsNode;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.acidbasesolutions.view.beaker.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.umd.cs.piccolo.PNode;

/**
 * SolutionsCanvas is the canvas for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsCanvas extends ABSAbstractCanvas {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View 
    private final BeakerNode beakerNode;
    private final ConcentrationGraphNode concentrationGraphNode;
    
    // Controls
    private final SolutionControlsNode solutionControlsNode;
    private final BeakerControlsNode beakerControlsNode;
    private final MiscControlsNode miscControlsNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionsCanvas( SolutionsModel model, Resettable resettable ) {
        super( resettable );
        
        AqueousSolution solution = model.getSolution();
        
        beakerNode = new BeakerNode( SolutionsDefaults.BEAKER_SIZE, solution );
        
        concentrationGraphNode = new ConcentrationGraphNode( SolutionsDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solution );
        
        solutionControlsNode = new SolutionControlsNode( solution );
        solutionControlsNode.scale( ABSConstants.PSWING_SCALE );
        
        beakerControlsNode = new BeakerControlsNode( beakerNode, getBackground(), solution );
        beakerControlsNode.scale( ABSConstants.PSWING_SCALE );
        
        miscControlsNode = new MiscControlsNode( concentrationGraphNode, getBackground(), solution );
        miscControlsNode.scale( ABSConstants.PSWING_SCALE );
        
        // rendering order
        addNode( solutionControlsNode );
        addNode( beakerControlsNode );
        addNode( miscControlsNode );
        addNode( beakerNode );
        addNode( concentrationGraphNode );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public SolutionControlsNode getSolutionControlsNode() {
        return solutionControlsNode;
    }
    
    public BeakerControlsNode getBeakerControlsNode() {
        return beakerControlsNode;
    }
    
    public MiscControlsNode getMiscControlsNode() {
        return miscControlsNode;
    }
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( ABSConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( getClass().getName() + ".updateLayout worldSize=" + worldSize );
        }
        
        double xOffset, yOffset = 0;
        
        // solution controls in upper left
        xOffset = solutionControlsNode.getXOffset() - solutionControlsNode.getFullBoundsReference().getX();
        yOffset = solutionControlsNode.getYOffset() - solutionControlsNode.getFullBoundsReference().getY();
        solutionControlsNode.setOffset( xOffset, yOffset );
        
        // beaker below solution controls
        xOffset = solutionControlsNode.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( beakerNode );
        yOffset = solutionControlsNode.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( beakerNode ) + 20;
        beakerNode.setOffset( xOffset, yOffset );
        
        // beaker controls attached to right edge of beaker
        xOffset = beakerNode.getFullBoundsReference().getMaxX() - 25;
        yOffset = beakerNode.getFullBoundsReference().getCenterY() - ( beakerControlsNode.getFullBoundsReference().getHeight() / 2 );
        beakerControlsNode.setOffset( xOffset, yOffset );
        
        // concentration graph to the right of beaker
        xOffset = beakerControlsNode.getFullBoundsReference().getMaxX() - PNodeUtils.getOriginXOffset( concentrationGraphNode ) + 20;
        yOffset = 0;
        concentrationGraphNode.setOffset( xOffset, yOffset );
        
        // misc controls at bottom right
        xOffset = concentrationGraphNode.getFullBoundsReference().getMaxX() - miscControlsNode.getFullBoundsReference().getWidth();
        yOffset = beakerNode.getFullBoundsReference().getMaxY() - miscControlsNode.getFullBoundsReference().getHeight();
        miscControlsNode.setOffset( xOffset, yOffset );
        
        // Reset All button to the right of misc controls
        PNode resetAllButton = getResetAllButton();
        xOffset = miscControlsNode.getFullBoundsReference().getMinX() - resetAllButton.getFullBoundsReference().getWidth() - 10;
        yOffset = miscControlsNode.getFullBoundsReference().getMaxY() - resetAllButton.getFullBounds().getHeight();
        resetAllButton.setOffset( xOffset , yOffset );
        
        centerRootNode();
    }
}
