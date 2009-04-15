/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.control.BeakerControls;
import edu.colorado.phet.acidbasesolutions.control.MiscControls;
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
    private final BeakerControls _beakerControls;
    private final PSwing _beakerControlsWrapper;
    private final MiscControls _miscControls;
    private final PSwing _miscControlsWrapper;
    
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
        
        _beakerControls = new BeakerControls();
        _beakerControls.setBackground( getBackground() );
        _beakerControlsWrapper = new PSwing( _beakerControls );
        _beakerControlsWrapper.scale( ABSConstants.PSWING_SCALE );
        
        _miscControls = new MiscControls();
        _miscControls.setBackground( getBackground() );
        _miscControlsWrapper = new PSwing( _miscControls );
        _miscControlsWrapper.scale( ABSConstants.PSWING_SCALE );
        
        // rendering order
        addNode( _beakerControlsWrapper );
        addNode( _miscControlsWrapper );
        addNode( _solutionNode );
        addNode( _probeNode );
        addNode( _beakerNode );
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
        
        // beaker
        xOffset = 50;
        yOffset = 325;
        _beakerNode.setOffset( xOffset, yOffset );
        
        // solution inside the beaker
        _solutionNode.setOffset( _beakerNode.getOffset() );
        
        // probe horizontally centered in beaker, tip of probe at bottom of beaker
        xOffset = _beakerNode.getFullBoundsReference().getCenterX() - _probeNode.getFullBoundsReference().getWidth() / 2;
        yOffset = _beakerNode.getFullBoundsReference().getMaxY() - _probeNode.getFullBoundsReference().getHeight();
        _probeNode.setOffset( xOffset, yOffset );
        
        // beaker controls attached to right edge of beaker
        xOffset = _beakerNode.getFullBoundsReference().getMaxX() - 25;
        yOffset = _beakerNode.getFullBoundsReference().getCenterY() - ( _beakerControlsWrapper.getFullBoundsReference().getHeight() / 2 );
        _beakerControlsWrapper.setOffset( xOffset, yOffset );
        
        // misc controls at bottom right
        xOffset = worldSize.getWidth() - _miscControlsWrapper.getFullBoundsReference().getWidth() - 5;
        yOffset = worldSize.getHeight() - _miscControlsWrapper.getFullBoundsReference().getHeight() - 5;
        _miscControlsWrapper.setOffset( xOffset, yOffset );
        
        // Reset All button at bottom center
        PNode resetAllButton = getResetAllButton();
        xOffset = ( worldSize.getWidth() / 2 ) - ( resetAllButton.getFullBoundsReference().getWidth() / 2 );
        yOffset = worldSize.getHeight() - resetAllButton.getFullBounds().getHeight() - 20;
        resetAllButton.setOffset( xOffset , yOffset );
    }
}
