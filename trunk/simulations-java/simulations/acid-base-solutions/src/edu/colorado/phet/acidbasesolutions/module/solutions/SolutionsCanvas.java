/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.control.BeakerControls;
import edu.colorado.phet.acidbasesolutions.control.MiscControls;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.acidbasesolutions.view.beaker.BeakerNode;
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
    private final ConcentrationGraphNode concentrationGraphNode;
    
    // Control
    private final SolutionControlsNode solutionControlsNode;
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
        
        beakerNode = new BeakerNode( SolutionsDefaults.BEAKER_SIZE, solution );
        
        concentrationGraphNode = new ConcentrationGraphNode( SolutionsDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solution );
        
        solutionControlsNode = new SolutionControlsNode( solution );
        solutionControlsNode.scale( ABSConstants.PSWING_SCALE );
        
        beakerControls = new BeakerControls( beakerNode );
        beakerControls.setBackground( getBackground() );
        beakerControlsWrapper = new PSwing( beakerControls );
        beakerControlsWrapper.scale( ABSConstants.PSWING_SCALE );
        
        miscControls = new MiscControls( concentrationGraphNode );
        miscControls.setBackground( getBackground() );
        miscControlsWrapper = new PSwing( miscControls );
        miscControlsWrapper.scale( ABSConstants.PSWING_SCALE );
        
        // rendering order
        addNode( solutionControlsNode );
        addNode( beakerControlsWrapper );
        addNode( miscControlsWrapper );
        addNode( beakerNode );
        addNode( concentrationGraphNode );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public SolutionControlsNode getSolutionControlsNode() {
        return solutionControlsNode;
    }
    
    public BeakerControls getBeakerControls() {
        return beakerControls;
    }
    
    public MiscControls getMiscControls() {
        return miscControls;
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
        xOffset = solutionControlsNode.getXOffset() - solutionControlsNode.getFullBoundsReference().getX() + 15;
        yOffset = solutionControlsNode.getYOffset() - solutionControlsNode.getFullBoundsReference().getY() + 15;
        solutionControlsNode.setOffset( xOffset, yOffset );
        
        // beaker below solution controls
        xOffset = solutionControlsNode.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( beakerNode );
        yOffset = solutionControlsNode.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( beakerNode ) + 20;
        beakerNode.setOffset( xOffset, yOffset );
        
        // beaker controls attached to right edge of beaker
        xOffset = beakerNode.getFullBoundsReference().getMaxX() - 25;
        yOffset = beakerNode.getFullBoundsReference().getCenterY() - ( beakerControlsWrapper.getFullBoundsReference().getHeight() / 2 );
        beakerControlsWrapper.setOffset( xOffset, yOffset );
        
        // concentration graph at upper right
        xOffset = worldSize.getWidth() - concentrationGraphNode.getFullBoundsReference().getWidth() - PNodeUtils.getOriginXOffset( concentrationGraphNode ) - 20;
        yOffset = 15;
        concentrationGraphNode.setOffset( xOffset, yOffset );
        
        // misc controls at bottom right
        xOffset = worldSize.getWidth() - miscControlsWrapper.getFullBoundsReference().getWidth() - 15;
        yOffset = worldSize.getHeight() - miscControlsWrapper.getFullBoundsReference().getHeight() - 15;
        miscControlsWrapper.setOffset( xOffset, yOffset );
        
        // Reset All button to the right of misc controls
        PNode resetAllButton = getResetAllButton();
        xOffset = miscControlsWrapper.getFullBoundsReference().getMinX() - resetAllButton.getFullBoundsReference().getWidth() - 10;
        yOffset = miscControlsWrapper.getFullBoundsReference().getMaxY() - resetAllButton.getFullBounds().getHeight();
        resetAllButton.setOffset( xOffset , yOffset );
    }
}
