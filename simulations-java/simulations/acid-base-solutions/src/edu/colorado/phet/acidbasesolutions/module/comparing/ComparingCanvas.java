/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.comparing;

import java.awt.geom.Dimension2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.control.EquationScalingControl;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.acidbasesolutions.view.beaker.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * ComparingCanvas is the canvas for ComparingModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingCanvas extends ABSAbstractCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View
    private final BeakerNode beakerNodeLeft, beakerNodeRight;
    private final ConcentrationGraphNode graphNodeLeft, graphNodeRight;
    private final ComparingEquationsNode equationsNodeLeft, equationsNodeRight;
    
    // Controls
    private final SolutionControlsNode solutionControlsNodeLeft, solutionControlsNodeRight;
    private final ComparingViewControlsNode viewControlsNode;
    private final ComparingBeakerControlsNode beakerControlsNode;
    private final EquationScalingControl equationScalingControl;
    private final PNode equationScalingControlWrapper;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ComparingCanvas( ComparingModel model, Resettable resettable ) {
        super( resettable );
        
        AqueousSolution solutionLeft = model.getSolutionLeft();
        AqueousSolution solutionRight = model.getSolutionRight();
        
        beakerNodeLeft = new BeakerNode( ComparingDefaults.BEAKER_SIZE, solutionLeft );
        beakerNodeRight = new BeakerNode( ComparingDefaults.BEAKER_SIZE, solutionRight );
        
        graphNodeLeft = new ConcentrationGraphNode( ComparingDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solutionLeft );
        graphNodeRight = new ConcentrationGraphNode( ComparingDefaults.CONCENTRATION_GRAPH_OUTLINE_SIZE, solutionRight );
        
        equationsNodeLeft = new ComparingEquationsNode( solutionLeft );
        equationsNodeRight = new ComparingEquationsNode( solutionRight );
        
        solutionControlsNodeLeft = new SolutionControlsNode( this, solutionLeft );
        solutionControlsNodeLeft.scale( ABSConstants.PSWING_SCALE );
        
        solutionControlsNodeRight = new SolutionControlsNode( this, solutionRight );
        solutionControlsNodeRight.scale( ABSConstants.PSWING_SCALE );
        
        viewControlsNode = new ComparingViewControlsNode( getBackground() );
        viewControlsNode.scale( ABSConstants.PSWING_SCALE );
        viewControlsNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateVisibility();
            }
        });
        
        beakerControlsNode = new ComparingBeakerControlsNode( getBackground(), beakerNodeLeft, beakerNodeRight );
        beakerControlsNode.scale( ABSConstants.PSWING_SCALE );
        
        equationScalingControl = new EquationScalingControl();
        equationScalingControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateEquationsScaling();
            }
        });
        equationScalingControlWrapper = new PhetPNode(); // so that pickability changes with visibility
        equationScalingControlWrapper.addChild( new PSwing( equationScalingControl ) );
        equationScalingControlWrapper.scale( ABSConstants.PSWING_SCALE );
        
        // rendering order
        addNode( solutionControlsNodeLeft );
        addNode( solutionControlsNodeRight );
        addNode( viewControlsNode );
        addNode( beakerNodeLeft );
        addNode( beakerNodeRight );
        addNode( beakerControlsNode );
        addNode( graphNodeLeft );
        addNode( graphNodeRight );
        addNode( equationsNodeLeft );
        addNode( equationsNodeRight );
        addNode( equationScalingControlWrapper );
        
        updateVisibility();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public SolutionControlsNode getSolutionControlsNodeLeft() {
        return solutionControlsNodeLeft;
    }
    
    public SolutionControlsNode getSolutionControlsNodeRight() {
        return solutionControlsNodeRight;
    }
    
    public ComparingViewControlsNode getViewControlsNode() {
        return viewControlsNode;
    }
    
    public ComparingBeakerControlsNode getBeakerControlsNode() {
        return beakerControlsNode;
    }
    
    public EquationScalingControl getEquationScalingControl() {
        return equationScalingControl;
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updateVisibility() {
        // beakers
        beakerControlsNode.setVisible( viewControlsNode.isBeakersSelected() );
        beakerNodeLeft.setVisible( viewControlsNode.isBeakersSelected() );
        beakerNodeRight.setVisible( viewControlsNode.isBeakersSelected() );
        // graphs
        graphNodeLeft.setVisible( viewControlsNode.isGraphsSelected() );
        graphNodeRight.setVisible( viewControlsNode.isGraphsSelected() );
        // equations
        equationScalingControlWrapper.setVisible( viewControlsNode.isEquationsSelected() );
        equationsNodeLeft.setVisible( viewControlsNode.isEquationsSelected() );
        equationsNodeRight.setVisible( viewControlsNode.isEquationsSelected() );
    }
    
    private void updateEquationsScaling() {
        equationsNodeLeft.setScalingEnabled( equationScalingControl.isScalingEnabled() );
        equationsNodeRight.setScalingEnabled( equationScalingControl.isScalingEnabled() );
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
        
        double xOffset, yOffset;
        
        // solution controls in upper left
        xOffset = -PNodeUtils.getOriginXOffset( solutionControlsNodeLeft );
        yOffset = -PNodeUtils.getOriginYOffset( solutionControlsNodeLeft );
        solutionControlsNodeLeft.setOffset( xOffset, yOffset );
        
        // solution controls in upper right
        final double maxControlWidth = getMaxControlWidth();
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxX() + maxControlWidth + 20 - PNodeUtils.getOriginXOffset( solutionControlsNodeRight );
        yOffset = -PNodeUtils.getOriginYOffset( solutionControlsNodeRight );
        solutionControlsNodeRight.setOffset( xOffset, yOffset );
        
        // view controls, centered between solution controls
        double centerX = solutionControlsNodeLeft.getFullBoundsReference().getMaxX() + (( solutionControlsNodeRight.getFullBoundsReference().getMinX() - solutionControlsNodeLeft.getFullBoundsReference().getMaxX() ) / 2 );
        xOffset = centerX - ( viewControlsNode.getFullBoundsReference().getWidth() / 2 );
        yOffset = 0;
        viewControlsNode.setOffset( xOffset, yOffset );
        
        // left beaker, centered below left solution controls
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( beakerNodeLeft ) + 
            (( solutionControlsNodeLeft.getFullBoundsReference().getWidth() - beakerNodeLeft.getFullBoundsReference().getWidth() ) / 2 );
        yOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( beakerNodeLeft ) + 20;
        beakerNodeLeft.setOffset( xOffset, yOffset );
        
        // right beaker, centered below right solution controls
        xOffset = solutionControlsNodeRight.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( beakerNodeRight ) + 
            (( solutionControlsNodeRight.getFullBoundsReference().getWidth() - beakerNodeRight.getFullBoundsReference().getWidth() ) / 2 );
        yOffset = solutionControlsNodeRight.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( beakerNodeRight ) + 20;
        beakerNodeRight.setOffset( xOffset, yOffset );
        
        // beaker view controls, between beakers, centered on view controls
        xOffset = viewControlsNode.getFullBoundsReference().getCenterX() - ( beakerControlsNode.getFullBoundsReference().getWidth() / 2 );
        yOffset = beakerNodeLeft.getFullBoundsReference().getMaxY() - beakerControlsNode.getFullBoundsReference().getHeight();
        beakerControlsNode.setOffset( xOffset, yOffset );
        
        // left graph, left justified below left solution controls
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( graphNodeLeft );
        yOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( graphNodeLeft ) + 20;
        graphNodeLeft.setOffset( xOffset, yOffset );
        
        // right graph, left justified below right solution controls
        xOffset = solutionControlsNodeRight.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( graphNodeRight );
        yOffset = solutionControlsNodeRight.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( graphNodeRight ) + 20;
        graphNodeRight.setOffset( xOffset, yOffset );
        
        // equation scaling controls below solution controls, centered on view controls
        xOffset = viewControlsNode.getFullBoundsReference().getCenterX() - ( equationScalingControlWrapper.getFullBoundsReference().getWidth() / 2 );
        yOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxY() + 10;
        equationScalingControlWrapper.setOffset( xOffset, yOffset );
        
        // left equations, left justified below left solution controls
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( equationsNodeLeft );
        yOffset = equationScalingControlWrapper.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( equationsNodeLeft ) + 20;
        equationsNodeLeft.setOffset( xOffset, yOffset );
        
        // right equations, left justified below right solution controls
        xOffset = solutionControlsNodeRight.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( equationsNodeRight );
        yOffset = equationScalingControlWrapper.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( equationsNodeRight ) + 20;
        equationsNodeRight.setOffset( xOffset, yOffset );
        
        // Reset All button centered below view controls
        PNode resetAllButton = getResetAllButton();
        xOffset = viewControlsNode.getXOffset() + ( ( viewControlsNode.getFullBoundsReference().getWidth() - resetAllButton.getFullBoundsReference().getWidth() ) / 2 );
        yOffset = viewControlsNode.getFullBoundsReference().getMaxY() + 10;
        resetAllButton.setOffset( xOffset , yOffset );
        
        centerRootNode();
    }
    
    /*
     * Gets the maximum control width.
     * Used to determine how much space should be between the left and right solution control panels.
     */
    private double getMaxControlWidth() {
        double maxControlWidth = 0;
        maxControlWidth = Math.max( maxControlWidth, viewControlsNode.getFullBoundsReference().getWidth() );
        maxControlWidth = Math.max( maxControlWidth, beakerControlsNode.getFullBoundsReference().getWidth() );
        maxControlWidth = Math.max( maxControlWidth, getResetAllButton().getFullBoundsReference().getWidth() );
        return maxControlWidth;
    }
}
