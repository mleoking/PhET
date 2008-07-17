/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.PHScaleModel;
import edu.colorado.phet.phscale.view.beaker.BeakerControlNode;
import edu.colorado.phet.phscale.view.beaker.ParticlesNode;
import edu.colorado.phet.phscale.view.graph.GraphControlNode;
import edu.colorado.phet.phscale.view.phslider.PHControlNode;
import edu.umd.cs.piccolo.PNode;
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

    // View 
    private final PNode _rootNode;
    private final BeakerControlNode _beakerControlNode;
    private final PHControlNode _pHControlNode;
    private final GraphControlNode _graphControlNode;
    private final PHScaleResetAllButton _resetAllButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHScaleCanvas( PHScaleModel model, Resettable resettable ) {
        super();
        setWorldTransformStrategy( new RenderingSizeStrategy( this, RENDERING_SIZE ) );
        setBackground( PHScaleConstants.CANVAS_BACKGROUND );
        
        Liquid liquid = model.getLiquid();
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
        
        // Nodes
        _beakerControlNode = new BeakerControlNode( liquid, this );
        _pHControlNode = new PHControlNode( liquid, PHScaleConstants.LOG_TICKS_Y_SPACING );
        _graphControlNode = new GraphControlNode( liquid, PHScaleConstants.LOG_TICKS_Y_SPACING );
        _resetAllButton = new PHScaleResetAllButton( resettable, this );
        PSwing resetAllButtonWrapper = new PSwing( _resetAllButton );
        
        // Rendering order
        _rootNode.addChild( _beakerControlNode );
        _rootNode.addChild( _pHControlNode );
        _rootNode.addChild( _graphControlNode );
        _rootNode.addChild( resetAllButtonWrapper );
        
        // Layout
        // beaker at left
        _beakerControlNode.setOffset( 25, 15 );
        // pH control to right of beaker
        double x = _beakerControlNode.getFullBoundsReference().getMaxX() + 15;
        double y = _beakerControlNode.getFullBoundsReference().getY() + 56;
        _pHControlNode.setOffset( x, y );
        // Reset All button centered below pH control
        x = _pHControlNode.getFullBoundsReference().getX() + ( ( _pHControlNode.getFullBoundsReference().getWidth() - resetAllButtonWrapper.getFullBoundsReference().getWidth() ) / 2 );
        y = _pHControlNode.getFullBoundsReference().getMaxY() + 70;
        resetAllButtonWrapper.setOffset( x, y );
        // bar graph to right of pH control
        x = _pHControlNode.getFullBoundsReference().getMaxX() + 120;
        y = 10;
        _graphControlNode.setOffset( x, y );
        
        // Tweak the layout to align the pH slider ticks and bar graph log ticks
        Point2D sliderOffset = _rootNode.globalToLocal( _pHControlNode.getTickAlignmentGlobalOffset() );
        Point2D graphOffset = _rootNode.globalToLocal( _graphControlNode.getTickAlignmentGlobalOffset() );
        _pHControlNode.setOffset( _pHControlNode.getXOffset(), _pHControlNode.getYOffset() + ( graphOffset.getY() - sliderOffset.getY() ) );
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
    
    public GraphControlNode getGraphControlNode() {
        return _graphControlNode;
    }
    
    public PHScaleResetAllButton getResetAllButton() {
        return _resetAllButton;
    }
    
    // for attaching developer control panel
    public ParticlesNode dev_getParticlesNode() {
        return _beakerControlNode.dev_getParticlesNode();
    }
}
