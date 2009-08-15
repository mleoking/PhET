/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.control.LegendControlNode;
import edu.colorado.phet.acidbasesolutions.control.MaximizeControlNode;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.acidbasesolutions.view.beaker.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * SolutionsCanvas is the canvas for SolutionsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsCanvas extends ABSAbstractCanvas {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MAXIMIZE_CONTROL_WIDTH = 465;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View 
    private final BeakerNode beakerNode;
    private final ConcentrationGraphNode concentrationGraphNode;
    private final SolutionsReactionEquationsNode reactionEquationsNode;
    private final SolutionsEquilibriumExpressionsNode equilibriumExpressionsNode;
    
    // Controls
    private final SolutionControlsNode solutionControlsNode;
    private final SolutionsBeakerControlsNode beakerControlsNode;
    private final LegendControlNode legendControlNode;
    private final MaximizeControlNode graphMaximizeControlNode;
    private final MaximizeControlNode reactionEquationsMaximizeControlNode;
    private final MaximizeControlNode equilibriumExpressionsMaximizeControlNode;
    private final ArrayList<MaximizeControlNode> exclusiveControls;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionsCanvas( SolutionsModel model, Resettable resettable ) {
        super( resettable );
        
        AqueousSolution solution = model.getSolution();
        
        beakerNode = new BeakerNode( SolutionsDefaults.BEAKER_SIZE, solution );
        
        concentrationGraphNode = new ConcentrationGraphNode( SolutionsDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solution );
        
        reactionEquationsNode = new SolutionsReactionEquationsNode( solution );
        
        equilibriumExpressionsNode = new SolutionsEquilibriumExpressionsNode( solution );
        
        solutionControlsNode = new SolutionControlsNode( this, solution, true /* showKButton */ );
        solutionControlsNode.scale( ABSConstants.PSWING_SCALE );
        
        beakerControlsNode = new SolutionsBeakerControlsNode( getBackground(), beakerNode, solution );
        beakerControlsNode.scale( ABSConstants.PSWING_SCALE );
        
        legendControlNode = new LegendControlNode( getBackground() );
        legendControlNode.scale( ABSConstants.PSWING_SCALE );
        
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                MaximizeControlNode source = ( (MaximizeControlNode) e.getSource() );
                if ( source.isMaximized() ) {
                    for ( MaximizeControlNode node : exclusiveControls ) {
                        if ( node != source ) {
                            node.setMaximized( false );
                        }
                    }
                }
                updateLayout();
            }
        };
        
        exclusiveControls = new ArrayList<MaximizeControlNode>();
        
        graphMaximizeControlNode = new MaximizeControlNode( ABSStrings.BUTTON_GRAPH, new PDimension( MAXIMIZE_CONTROL_WIDTH, 560 ), concentrationGraphNode );
        graphMaximizeControlNode.addChangeListener( changeListener );
        exclusiveControls.add( graphMaximizeControlNode );
        
        reactionEquationsMaximizeControlNode = new MaximizeControlNode( ABSStrings.BUTTON_REACTION_EQUATIONS, new PDimension( MAXIMIZE_CONTROL_WIDTH, 450 ), reactionEquationsNode );
        reactionEquationsMaximizeControlNode.addChangeListener( changeListener );
        exclusiveControls.add( reactionEquationsMaximizeControlNode );
        
        equilibriumExpressionsMaximizeControlNode = new MaximizeControlNode( ABSStrings.BUTTON_EQUILIBRIUM_EXPRESSIONS, new PDimension( MAXIMIZE_CONTROL_WIDTH, 380 ), equilibriumExpressionsNode );
        equilibriumExpressionsMaximizeControlNode.addChangeListener( changeListener );
        exclusiveControls.add( equilibriumExpressionsMaximizeControlNode );
        
        // rendering order
        addNode( solutionControlsNode );
        addNode( beakerControlsNode );
        addNode( legendControlNode );
        addNode( beakerNode );
        addNode( graphMaximizeControlNode );
        addNode( reactionEquationsMaximizeControlNode );
        addNode( equilibriumExpressionsMaximizeControlNode );
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
    
    public LegendControlNode getLegendControlNode() {
        return legendControlNode;
    }
    
    public void setGraphVisible( boolean b ) {
        graphMaximizeControlNode.setMaximized( b );
    }
    
    public boolean isGraphVisible() {
        return graphMaximizeControlNode.isMaximized();
    }
    
    public void setReactionEquationsVisible( boolean b ) {
        reactionEquationsMaximizeControlNode.setMaximized( b );
    }
    
    public boolean isReactionEquationsVisible() {
        return reactionEquationsMaximizeControlNode.isMaximized();
    }
    
    public void setReactionEquationsScalingEnabled( boolean enabled, boolean animated ) {
        reactionEquationsNode.setScalingEnabled( enabled, animated );
    }
    
    public boolean isReactionEquationsScalingEnabled() {
        return reactionEquationsNode.isScalingEnabled();
    }
    
    public void setEquilibriumExpressionsVisible( boolean b ) {
        equilibriumExpressionsMaximizeControlNode.setMaximized( b );
    }
    
    public boolean isEquilibriumExpressionsVisible() {
        return equilibriumExpressionsMaximizeControlNode.isMaximized();
    }
    
    public void setEquilibriumExpressionsScalingEnabled( boolean enabled, boolean animated ) {
        equilibriumExpressionsNode.setScalingEnabled( enabled, animated );
    }
    
    public boolean isEquilibriumExpressionsScalingEnabled() {
        return equilibriumExpressionsNode.isScalingEnabled();
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
        
        // start at (0,0), we'll adjust this globally with centerRootNode
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
        
        // graph to the right of beaker
        xOffset = beakerNode.getFullBoundsReference().getMaxX() + 215;
        yOffset = 0;
        graphMaximizeControlNode.setOffset( xOffset, yOffset );
        
        // reaction equations, below graph
        xOffset = graphMaximizeControlNode.getXOffset();
        yOffset = graphMaximizeControlNode.getFullBoundsReference().getMaxY() + 5;
        reactionEquationsMaximizeControlNode.setOffset( xOffset, yOffset );
        
        // equilibrium expressions, below reaction equations
        xOffset = reactionEquationsMaximizeControlNode.getXOffset();
        yOffset = reactionEquationsMaximizeControlNode.getFullBoundsReference().getMaxY() + 5;
        equilibriumExpressionsMaximizeControlNode.setOffset( xOffset, yOffset );
        
        // Reset All button at bottom center
        PNode resetAllButton = getResetAllButton();
        xOffset = beakerNode.getFullBoundsReference().getMaxX() + 10;
        yOffset = beakerNode.getFullBoundsReference().getMaxY() - resetAllButton.getFullBoundsReference().getHeight();
        resetAllButton.setOffset( xOffset , yOffset );
        
        // misc controls above reset button
        xOffset = resetAllButton.getXOffset();
        yOffset = resetAllButton.getFullBoundsReference().getY() - legendControlNode.getFullBoundsReference().getHeight() - 5;
        legendControlNode.setOffset( xOffset, yOffset );
        
        centerRootNode();
    }
}
