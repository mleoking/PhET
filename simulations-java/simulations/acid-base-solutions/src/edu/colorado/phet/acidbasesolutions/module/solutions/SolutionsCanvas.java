/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlPanel;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
import edu.colorado.phet.acidbasesolutions.view.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.PHProbeNode;
import edu.colorado.phet.acidbasesolutions.view.ParticlesNode;
import edu.colorado.phet.acidbasesolutions.view.SolutionNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.umd.cs.piccolo.PNode;
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
    private SolutionsModel _model;
    
    // View 
    private final BeakerNode _beakerNode;
    private final PHProbeNode _probeNode;
    private final SolutionNode _solutionNode;
    
    // Control
    private final PSwing _solutionControlPanelWrapper;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionsCanvas( SolutionsModel model, Resettable resettable ) {
        super( resettable );
        
        _model = model;
        
        _beakerNode = new BeakerNode( SolutionsDefaults.BEAKER_SIZE, 1 );
        
        _probeNode = new PHProbeNode( _model.getSolution(), SolutionsDefaults.PH_PROBE_HEIGHT );

        _solutionNode = new SolutionNode( _model.getSolution(), SolutionsDefaults.BEAKER_SIZE );
        _solutionNode.setParticlesVisible( true );
        
        SolutionControlPanel solutionControlPanel = new SolutionControlPanel();
        _solutionControlPanelWrapper = new PSwing( solutionControlPanel );
        
        // rendering order
        addNode( _solutionNode );
        addNode( _probeNode );
        addNode( _beakerNode );
        addNode( _solutionControlPanelWrapper );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ParticlesNode dev_getParticlesNode() {
        return _solutionNode.getParticlesNode();
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
        
        // solution controls
        xOffset = 10;
        yOffset = 10;
        _solutionControlPanelWrapper.setOffset( xOffset, yOffset );
        
        xOffset = _solutionControlPanelWrapper.getFullBoundsReference().getMinX() + 40;
        yOffset = _solutionControlPanelWrapper.getFullBoundsReference().getMaxY() + 80;
        _beakerNode.setOffset( xOffset, yOffset );
        
        // probe horizontally centered in beaker, tip of probe at bottom of beaker
        xOffset = _beakerNode.getFullBoundsReference().getCenterX() - _probeNode.getFullBoundsReference().getWidth() / 2;
        yOffset = _beakerNode.getFullBoundsReference().getMaxY() - _probeNode.getFullBoundsReference().getHeight();
        _probeNode.setOffset( xOffset, yOffset );
        
        // liquid has same offset as beaker, so that it's inside the beaker
        _solutionNode.setOffset( _beakerNode.getOffset() );
        
        PNode resetAllButton = getResetAllButton();
        xOffset = ( worldSize.getWidth() / 2 ) - ( resetAllButton.getFullBoundsReference().getWidth() / 2 );
        yOffset = worldSize.getHeight() - resetAllButton.getFullBounds().getHeight() - 20;
        resetAllButton.setOffset( xOffset , yOffset );
    }
}
