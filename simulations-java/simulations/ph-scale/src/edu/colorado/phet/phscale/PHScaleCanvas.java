/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale;

import java.awt.Dimension;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.phscale.control.BeakerControlNode;
import edu.colorado.phet.phscale.control.PHControlNode;
import edu.colorado.phet.phscale.control.PHScaleResetAllButton;
import edu.colorado.phet.phscale.model.PHScaleModel;
import edu.colorado.phet.phscale.view.BarGraphNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * PHScaleCanvas is the canvas for PHScaleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension RENDERING_SIZE = new Dimension( 1024, 768 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private final PHScaleModel _model;
    
    // View 
    private final PNode _rootNode;
    private final BeakerControlNode _beakerControlNode;
    private final PHControlNode _pHControlNode;
    private final BarGraphNode _barGraphNode;
    private final PHScaleResetAllButton _resetAllButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHScaleCanvas( PHScaleModel model, Resettable resettable ) {
        super();
        setWorldTransformStrategy( new RenderingSizeStrategy( this, RENDERING_SIZE ) );
        setBackground( PHScaleConstants.CANVAS_BACKGROUND );
        
        _model = model;
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        // Nodes
        _beakerControlNode = new BeakerControlNode( this, _model ); //XXX
        _pHControlNode = new PHControlNode( PHScaleConstants.PH_RANGE, _model.getLiquid() );
        _barGraphNode = new BarGraphNode( new PDimension( 225, 400 ), _model.getLiquid() );//XXX
        _resetAllButton = new PHScaleResetAllButton( resettable, this );
        PSwing resetAllButtonWrapper = new PSwing( _resetAllButton );
        
        // Rendering order
        _rootNode.addChild( _beakerControlNode );
        _rootNode.addChild( _pHControlNode );
        _rootNode.addChild( _barGraphNode );
        _rootNode.addChild( resetAllButtonWrapper );
        
        // Layout
        final double xSpacing = 60;
        final double ySpacing = 15;
        // beaker at left
        _beakerControlNode.setOffset( 35, 15 );
        // pH control to right of beaker
        double x = _beakerControlNode.getFullBoundsReference().getMaxX() + xSpacing;
        double y = _beakerControlNode.getFullBoundsReference().getY();
        _pHControlNode.setOffset( x, y );
        // Reset All button centered below pH control
        x = _pHControlNode.getFullBoundsReference().getX() + ( ( _pHControlNode.getFullBoundsReference().getWidth() - resetAllButtonWrapper.getFullBoundsReference().getWidth() ) / 2 );
        y = _pHControlNode.getFullBoundsReference().getMaxY() + ySpacing;
        resetAllButtonWrapper.setOffset( x, y );
        // bar graph to right of pH control
        x = _pHControlNode.getFullBoundsReference().getMaxX() + xSpacing;
        y = _pHControlNode.getFullBoundsReference().getY();
        _barGraphNode.setOffset( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public BeakerControlNode getBeakerControlNode() {
        return _beakerControlNode;
    }
    
    public PHControlNode getPHControlNode() {
        return _pHControlNode;
    }
    
    public BarGraphNode getBarGraphNode() {
        return _barGraphNode;
    }
    
    public PHScaleResetAllButton getResetAllButton() {
        return _resetAllButton;
    }
    
}
