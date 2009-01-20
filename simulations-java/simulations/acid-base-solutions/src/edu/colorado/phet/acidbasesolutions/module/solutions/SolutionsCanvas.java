/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractCanvas;
import edu.colorado.phet.acidbasesolutions.view.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.ExampleNode;
import edu.colorado.phet.acidbasesolutions.view.PHProbeNode;
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

    // Model
    private SolutionsModel _model;
    
    // View 
    private ExampleNode _exampleNode;//XXX
    private final BeakerNode _beakerNode;
    private final PHProbeNode _probeNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SolutionsCanvas( SolutionsModel model, Resettable resettable ) {
        super( resettable );
        
        _model = model;
        
        _exampleNode = new ExampleNode( _model.getExampleModelElement() );
        addNode( _exampleNode );
        
        _beakerNode = new BeakerNode( SolutionsDefaults.BEAKER_SIZE, 1 );
        addNode( _beakerNode );
        
        _probeNode = new PHProbeNode( SolutionsDefaults.PH_PROBE_HEIGHT, _model.getSolution() );
        addNode( _probeNode );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ExampleNode getExampleNode() {
        return _exampleNode;
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
        
        xOffset = 100;
        yOffset = 100;
        _beakerNode.setOffset( xOffset, yOffset );
        
        // probe horizontally centered in beaker, tip of probe at bottom of beaker
        _probeNode.setOffset( 
                _beakerNode.getFullBoundsReference().getCenterX() - _probeNode.getFullBoundsReference().getWidth() / 2, 
                _beakerNode.getFullBoundsReference().getMaxY() - _probeNode.getFullBoundsReference().getHeight() );
        
        PNode resetAllButton = getResetAllButton();
        xOffset = ( worldSize.getWidth() / 2 ) - ( resetAllButton.getFullBoundsReference().getWidth() / 2 );
        yOffset = worldSize.getHeight() - resetAllButton.getFullBounds().getHeight() - 20;
        resetAllButton.setOffset( xOffset , yOffset );
    }
}
