/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.comparing;

import java.awt.geom.Dimension2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.control.ComparingBeakerControlsNode;
import edu.colorado.phet.acidbasesolutions.control.ComparingViewControlsNode;
import edu.colorado.phet.acidbasesolutions.control.EquationScalingControl;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.control.EquationScalingControl.VerticalEquationScalingControl;
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
    //XXX equations
    
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
        
        //XXX equations
        
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
        
        equationScalingControl = new VerticalEquationScalingControl( getBackground() );
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
        //XXX equations
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
        //XXX equations
    }
    
    private void updateEquationsScaling() {
        //XXX equations
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
        
        // view controls between solution controls
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxX() + 10;
        yOffset = 0;
        viewControlsNode.setOffset( xOffset, yOffset );
        
        // solution controls in upper right
        xOffset = viewControlsNode.getFullBoundsReference().getMaxX() + 30;
        yOffset = -PNodeUtils.getOriginYOffset( solutionControlsNodeRight );
        solutionControlsNodeRight.setOffset( xOffset, yOffset );
        
        // left beaker below left solution controls
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( beakerNodeLeft );
        yOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( beakerNodeLeft ) + 20;
        beakerNodeLeft.setOffset( xOffset, yOffset );
        
        // right beaker below right solution controls
        xOffset = solutionControlsNodeRight.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( beakerNodeRight );
        yOffset = solutionControlsNodeRight.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( beakerNodeRight ) + 20;
        beakerNodeRight.setOffset( xOffset, yOffset );
        
        // beaker view controls between beakers
        double centerX = beakerNodeLeft.getFullBoundsReference().getMaxX() + ( ( beakerNodeRight.getFullBoundsReference().getMinX() - beakerNodeLeft.getFullBoundsReference().getMaxX() ) / 2 );
        xOffset = centerX - ( beakerControlsNode.getFullBoundsReference().getWidth() / 2 );
        yOffset = beakerNodeLeft.getFullBoundsReference().getMaxY() - beakerControlsNode.getFullBoundsReference().getHeight();
        beakerControlsNode.setOffset( xOffset, yOffset );
        
        // left graph below left solution controls
        xOffset = solutionControlsNodeLeft.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( graphNodeLeft );
        yOffset = solutionControlsNodeLeft.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( graphNodeLeft ) + 20;
        graphNodeLeft.setOffset( xOffset, yOffset );
        
        // right graph below right solution controls
        xOffset = solutionControlsNodeRight.getFullBoundsReference().getMinX() - PNodeUtils.getOriginXOffset( graphNodeRight );
        yOffset = solutionControlsNodeRight.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( graphNodeRight ) + 20;
        graphNodeRight.setOffset( xOffset, yOffset );
        
        //XXX equations
        
        // equation scaling controls between left and right equations
        xOffset = viewControlsNode.getFullBoundsReference().getX();//XXX
        yOffset = beakerNodeLeft.getFullBoundsReference().getMaxY() - equationScalingControlWrapper.getFullBoundsReference().getHeight();//XXX
        equationScalingControlWrapper.setOffset( xOffset, yOffset );
        
        // Reset All button below view controls
        PNode resetAllButton = getResetAllButton();
        xOffset = viewControlsNode.getXOffset();
        yOffset = viewControlsNode.getFullBoundsReference().getMaxY() + 10;
        resetAllButton.setOffset( xOffset , yOffset );
        
        centerRootNode();
    }
}
