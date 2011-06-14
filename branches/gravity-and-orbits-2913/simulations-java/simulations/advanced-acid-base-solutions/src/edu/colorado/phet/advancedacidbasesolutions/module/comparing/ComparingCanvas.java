// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.module.comparing;

import java.awt.geom.Dimension2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.advancedacidbasesolutions.AABSConstants;
import edu.colorado.phet.advancedacidbasesolutions.control.EquationScalingControl;
import edu.colorado.phet.advancedacidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.module.AABSAbstractCanvas;
import edu.colorado.phet.advancedacidbasesolutions.view.beaker.BeakerNode;
import edu.colorado.phet.advancedacidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * ComparingCanvas is the canvas for ComparingModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingCanvas extends AABSAbstractCanvas {

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
        solutionControlsNodeLeft.scale( AABSConstants.PSWING_SCALE );
        
        solutionControlsNodeRight = new SolutionControlsNode( this, solutionRight );
        solutionControlsNodeRight.scale( AABSConstants.PSWING_SCALE );
        
        viewControlsNode = new ComparingViewControlsNode( getBackground() );
        viewControlsNode.scale( AABSConstants.PSWING_SCALE );
        viewControlsNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateVisibility();
            }
        });
        
        beakerControlsNode = new ComparingBeakerControlsNode( getBackground(), beakerNodeLeft, beakerNodeRight );
        beakerControlsNode.scale( AABSConstants.PSWING_SCALE );
        
        equationScalingControl = new EquationScalingControl();
        equationScalingControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateEquationsScaling( true /* animated */ );
            }
        });
        equationScalingControlWrapper = new PhetPNode(); // so that pickability changes with visibility
        equationScalingControlWrapper.addChild( new PSwing( equationScalingControl ) );
        equationScalingControlWrapper.scale( AABSConstants.PSWING_SCALE );
        
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
    
    private void updateEquationsScaling( boolean animated ) {
        equationsNodeLeft.setScalingEnabled( equationScalingControl.isScalingEnabled(), animated );
        equationsNodeRight.setScalingEnabled( equationScalingControl.isScalingEnabled(), animated );
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
        else if ( AABSConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( getClass().getName() + ".updateLayout worldSize=" + worldSize );
        }
        
        double xOffset, yOffset;
        
        // solution controls in upper left
        xOffset = -PNodeLayoutUtils.getOriginXOffset( solutionControlsNodeLeft );
        yOffset = -PNodeLayoutUtils.getOriginYOffset( solutionControlsNodeLeft );
        solutionControlsNodeLeft.setOffset( xOffset, yOffset );
        
        // solution controls in upper right
        final double maxControlWidth = getMaxControlWidth();
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxX() + maxControlWidth + 20 - PNodeLayoutUtils.getOriginXOffset( solutionControlsNodeRight );
        yOffset = -PNodeLayoutUtils.getOriginYOffset( solutionControlsNodeRight );
        solutionControlsNodeRight.setOffset( xOffset, yOffset );
        
        // view controls, centered between solution controls
        double centerX = solutionControlsNodeLeft.getFullBoundsReference().getMaxX() + (( solutionControlsNodeRight.getFullBoundsReference().getMinX() - solutionControlsNodeLeft.getFullBoundsReference().getMaxX() ) / 2 );
        xOffset = centerX - ( viewControlsNode.getFullBoundsReference().getWidth() / 2 );
        yOffset = 0;
        viewControlsNode.setOffset( xOffset, yOffset );
        
        // left beaker, centered below left solution controls
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMinX() - PNodeLayoutUtils.getOriginXOffset( beakerNodeLeft ) + 
            (( solutionControlsNodeLeft.getFullBoundsReference().getWidth() - beakerNodeLeft.getFullBoundsReference().getWidth() ) / 2 );
        yOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beakerNodeLeft ) + 20;
        beakerNodeLeft.setOffset( xOffset, yOffset );
        
        // right beaker, centered below right solution controls
        xOffset = solutionControlsNodeRight.getFullBoundsReference().getMinX() - PNodeLayoutUtils.getOriginXOffset( beakerNodeRight ) + 
            (( solutionControlsNodeRight.getFullBoundsReference().getWidth() - beakerNodeRight.getFullBoundsReference().getWidth() ) / 2 );
        yOffset = solutionControlsNodeRight.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beakerNodeRight ) + 20;
        beakerNodeRight.setOffset( xOffset, yOffset );
        
        // Reset All button at bottom center
        PNode resetAllButton = getResetAllButton();
        xOffset = viewControlsNode.getFullBoundsReference().getCenterX() - ( resetAllButton.getFullBoundsReference().getWidth() / 2 );
        yOffset = beakerNodeLeft.getFullBoundsReference().getMaxY() - resetAllButton.getFullBoundsReference().getHeight();
        resetAllButton.setOffset( xOffset , yOffset );
        
        // beaker view controls, between beakers, centered on view controls
        xOffset = viewControlsNode.getFullBoundsReference().getCenterX() - ( beakerControlsNode.getFullBoundsReference().getWidth() / 2 );
        yOffset = resetAllButton.getFullBoundsReference().getY() - beakerControlsNode.getFullBoundsReference().getHeight() - 10;
        beakerControlsNode.setOffset( xOffset, yOffset );
        
        // left graph, left justified below left solution controls
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMinX() - PNodeLayoutUtils.getOriginXOffset( graphNodeLeft );
        yOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( graphNodeLeft ) + 20;
        graphNodeLeft.setOffset( xOffset, yOffset );
        
        // right graph, left justified below right solution controls
        xOffset = solutionControlsNodeRight.getFullBoundsReference().getMinX() - PNodeLayoutUtils.getOriginXOffset( graphNodeRight );
        yOffset = solutionControlsNodeRight.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( graphNodeRight ) + 20;
        graphNodeRight.setOffset( xOffset, yOffset );
        
        // equation scaling controls below solution controls, centered on view controls
        xOffset = viewControlsNode.getFullBoundsReference().getCenterX() - ( equationScalingControlWrapper.getFullBoundsReference().getWidth() / 2 );
        yOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxY() + 10;
        equationScalingControlWrapper.setOffset( xOffset, yOffset );
        
        // left equations, left justified below left solution controls
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMinX() - PNodeLayoutUtils.getOriginXOffset( equationsNodeLeft );
        yOffset = equationScalingControlWrapper.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( equationsNodeLeft ) + 20;
        equationsNodeLeft.setOffset( xOffset, yOffset );
        
        // right equations, left justified below right solution controls
        xOffset = solutionControlsNodeRight.getFullBoundsReference().getMinX() - PNodeLayoutUtils.getOriginXOffset( equationsNodeRight );
        yOffset = equationScalingControlWrapper.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( equationsNodeRight ) + 20;
        equationsNodeRight.setOffset( xOffset, yOffset );
        
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
