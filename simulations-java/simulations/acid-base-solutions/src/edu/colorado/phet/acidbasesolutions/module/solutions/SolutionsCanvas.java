/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
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
    private final SolutionsBeakerControlsNode beakerControlsNode;
    private final MiscControlsNode miscControlsNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionsCanvas( SolutionsModel model, Resettable resettable ) {
        super( resettable );
        
        AqueousSolution solution = model.getSolution();
        
        beakerNode = new BeakerNode( SolutionsDefaults.BEAKER_SIZE, solution );
        
        concentrationGraphNode = new ConcentrationGraphNode( SolutionsDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solution );
        
        solutionControlsNode = new SolutionControlsNode( this, solution );
        solutionControlsNode.scale( ABSConstants.PSWING_SCALE );
        
        beakerControlsNode = new SolutionsBeakerControlsNode( getBackground(), beakerNode, solution );
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
    
    public SolutionsBeakerControlsNode getBeakerControlsNode() {
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
        xOffset = -PNodeUtils.getOriginXOffset( solutionControlsNode );
        yOffset = -PNodeUtils.getOriginYOffset( solutionControlsNode );
        solutionControlsNode.setOffset( xOffset, yOffset );
        
        // beaker below solution controls
        xOffset = solutionControlsNode.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( beakerNode );
        yOffset = solutionControlsNode.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( beakerNode ) + 20;
        beakerNode.setOffset( xOffset, yOffset );
        
        // beaker controls attached to right edge of beaker
        xOffset = beakerNode.getXOffset() + SolutionsDefaults.BEAKER_SIZE.getWidth() - 1;
        yOffset = beakerNode.getYOffset() + ( 0.1 * SolutionsDefaults.BEAKER_SIZE.getHeight() );
        beakerControlsNode.setOffset( xOffset, yOffset );
        
        // concentration graph to the right of beaker
        double fudgeX = 50; // add space to handle widest label for disassociated components ratio check box
        xOffset = beakerControlsNode.getFullBoundsReference().getMaxX() - PNodeUtils.getOriginXOffset( concentrationGraphNode ) + fudgeX;
        yOffset = 0;
        concentrationGraphNode.setOffset( xOffset, yOffset );
        
        // misc controls at bottom right, but don't overlap beaker
        xOffset = concentrationGraphNode.getFullBoundsReference().getMaxX() - miscControlsNode.getFullBoundsReference().getWidth();
        if ( xOffset < beakerNode.getFullBoundsReference().getMaxX() ) {
            xOffset = beakerNode.getFullBoundsReference().getMaxX();
        }
        yOffset = beakerNode.getFullBoundsReference().getMaxY() - miscControlsNode.getFullBoundsReference().getHeight();
        miscControlsNode.setOffset( xOffset, yOffset );
        
        // Reset All button to the right of solute controls
        PNode resetAllButton = getResetAllButton();
        xOffset = solutionControlsNode.getFullBoundsReference().getMaxX() + 10;
        yOffset = solutionControlsNode.getFullBoundsReference().getY();
        resetAllButton.setOffset( xOffset , yOffset );
        
        centerRootNode();
    }
}
