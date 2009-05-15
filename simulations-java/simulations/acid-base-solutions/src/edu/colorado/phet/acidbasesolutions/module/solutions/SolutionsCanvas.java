/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.control.BeakerControls;
import edu.colorado.phet.acidbasesolutions.control.MiscControls;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
import edu.colorado.phet.acidbasesolutions.view.beaker.*;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * SolutionsCanvas is the canvas for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsCanvas extends ABSAbstractCanvas {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private SolutionsModel model;
    
    // View 
    private final BeakerNode beakerNode;
    private final PHProbeNode probeNode;
    private final SolutionNode solutionNode;
    private final ConcentrationGraphNode concentrationGraphNode;
    private final MoleculeCountsNode moleculeCountsNode;
    private final BeakerLabelNode beakerLabelNode;
    
    // Control
    private final SolutionControlsNode solutionsControlsNode;
    private final BeakerControls beakerControls;
    private final PSwing beakerControlsWrapper;
    private final MiscControls miscControls;
    private final PSwing miscControlsWrapper;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionsCanvas( SolutionsModel model, Resettable resettable ) {
        super( resettable );
        
        this.model = model;
        AqueousSolution solution = model.getSolution();
        
        beakerNode = new BeakerNode( SolutionsDefaults.BEAKER_SIZE, 1 );
        
        probeNode = new PHProbeNode( SolutionsDefaults.PH_PROBE_HEIGHT, solution );

        solutionNode = new SolutionNode( SolutionsDefaults.BEAKER_SIZE );
        
        concentrationGraphNode = new ConcentrationGraphNode();
        
        moleculeCountsNode = new MoleculeCountsNode();
        
        beakerLabelNode = new BeakerLabelNode( ABSConstants.MIN_BEAKER_LABEL_SIZE );
        
        solutionsControlsNode = new SolutionControlsNode( solution );
        solutionsControlsNode.scale( ABSConstants.PSWING_SCALE );
        
        beakerControls = new BeakerControls( moleculeCountsNode, beakerLabelNode );
        beakerControls.setBackground( getBackground() );
        beakerControlsWrapper = new PSwing( beakerControls );
        beakerControlsWrapper.scale( ABSConstants.PSWING_SCALE );
        
        miscControls = new MiscControls( concentrationGraphNode );
        miscControls.setBackground( getBackground() );
        miscControlsWrapper = new PSwing( miscControls );
        miscControlsWrapper.scale( ABSConstants.PSWING_SCALE );
        
        // rendering order
        addNode( solutionsControlsNode );
        addNode( beakerControlsWrapper );
        addNode( miscControlsWrapper );
        addNode( solutionNode );
        addNode( probeNode );
        addNode( beakerNode );
        addNode( concentrationGraphNode );
        addNode( moleculeCountsNode );
        addNode( beakerLabelNode );
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
        PBounds b1;
        
        // solution controls in upper left
        xOffset = solutionsControlsNode.getXOffset() - solutionsControlsNode.getFullBoundsReference().getX() + 20;
        yOffset = solutionsControlsNode.getYOffset() - solutionsControlsNode.getFullBoundsReference().getY() + 20;
        solutionsControlsNode.setOffset( xOffset, yOffset );
        
        // beaker
        xOffset = 50;
        yOffset = 375;
        beakerNode.setOffset( xOffset, yOffset );
        
        // solution inside the beaker
        solutionNode.setOffset( beakerNode.getOffset() );
        
        // molecule counts inside the beaker
        xOffset = beakerNode.getXOffset() + 10;
        yOffset = beakerNode.getYOffset();
        moleculeCountsNode.setOffset( xOffset, yOffset );
        
        // beaker label at bottom of beaker
        b1 = beakerNode.getFullBoundsReference();
        xOffset = b1.getMinX() + ( b1.getWidth() - beakerLabelNode.getFullBoundsReference().getWidth() ) / 2;
        yOffset = b1.getMaxY() - beakerLabelNode.getFullBoundsReference().getHeight() - 20;
        beakerLabelNode.setOffset( xOffset, yOffset );
        
        // probe horizontally centered in beaker, tip of probe at bottom of beaker
        xOffset = beakerNode.getFullBoundsReference().getCenterX() - probeNode.getFullBoundsReference().getWidth() / 2;
        yOffset = beakerNode.getFullBoundsReference().getMaxY() - probeNode.getFullBoundsReference().getHeight();
        probeNode.setOffset( xOffset, yOffset );
        
        // beaker controls attached to right edge of beaker
        xOffset = beakerNode.getFullBoundsReference().getMaxX() - 25;
        yOffset = beakerNode.getFullBoundsReference().getCenterY() - ( beakerControlsWrapper.getFullBoundsReference().getHeight() / 2 );
        beakerControlsWrapper.setOffset( xOffset, yOffset );
        
        // concentration graph at upper right
        xOffset = worldSize.getWidth() - concentrationGraphNode.getFullBoundsReference().getWidth() - 15;
        yOffset = 15;
        concentrationGraphNode.setOffset( xOffset, yOffset );
        
        // misc controls at bottom right
        xOffset = worldSize.getWidth() - miscControlsWrapper.getFullBoundsReference().getWidth() - 5;
        yOffset = worldSize.getHeight() - miscControlsWrapper.getFullBoundsReference().getHeight() - 5;
        miscControlsWrapper.setOffset( xOffset, yOffset );
        
        // Reset All button at bottom center
        PNode resetAllButton = getResetAllButton();
        xOffset = ( worldSize.getWidth() / 2 ) - ( resetAllButton.getFullBoundsReference().getWidth() / 2 );
        yOffset = worldSize.getHeight() - resetAllButton.getFullBounds().getHeight() - 20;
        resetAllButton.setOffset( xOffset , yOffset );
    }
}
