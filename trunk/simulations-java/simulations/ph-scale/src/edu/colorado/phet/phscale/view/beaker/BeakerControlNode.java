/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.model.LiquidDescriptor.CustomLiquidDescriptor;
import edu.colorado.phet.phscale.view.beaker.ViewControlPanel.ViewControlPanelListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * BeakerControlNode contains the beaker, liquid and all related controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerControlNode extends PNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final CustomLiquidDescriptor CUSTOM_LIQUID = LiquidDescriptor.getCustom();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final LiquidControlNode _liquidControlNode;
    private final WaterControlNode _waterControlNode;
    private final BeakerNode _beakerNode;
    private final LiquidNode _liquidNode;
    private final ProbeNode _probeNode;
    private final MoleculeCountNode _moleculeCountNode;
    private final ViewControlPanel _viewControlPanel;
    private final DrainControlNode _drainControlNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param liquid
     * @parma canvas PComboBox workaround required for LiquidComboBox 
     */
    public BeakerControlNode( Liquid liquid, PSwingCanvas pSwingCanvas ) {
        super();
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                // hide the water faucet if the Custom liquid is selected
                _waterControlNode.setVisible( !_liquid.getLiquidDescriptor().equals( CUSTOM_LIQUID ) );
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _probeNode = new ProbeNode( PHScaleConstants.PH_PROBE_LENGTH, liquid );
        
        _liquidControlNode = new LiquidControlNode( liquid, pSwingCanvas );
        
        _waterControlNode = new WaterControlNode( liquid );
        
        _drainControlNode = new DrainControlNode( liquid );

        _liquidNode = new LiquidNode( liquid, PHScaleConstants.BEAKER_SIZE );
        
        _beakerNode = new BeakerNode( PHScaleConstants.BEAKER_SIZE, liquid.getMaxVolume() );
        
        _moleculeCountNode = new MoleculeCountNode( liquid );
        
        _viewControlPanel = new ViewControlPanel();
        PSwing viewControlPanelWrapper = new PSwing( _viewControlPanel );
        _viewControlPanel.addViewControlPanelListener( new ViewControlPanelListener() {

            public void countChanged( boolean selected ) {
                _moleculeCountNode.setVisible( selected );
            }

            public void ratioChanged( boolean selected ) {
                _liquidNode.setParticlesVisible( selected );
            }
            
        } );
        
        // Rendering order
        addChild( _liquidControlNode );
        addChild( _waterControlNode );
        addChild( _probeNode );
        addChild( _drainControlNode );
        addChild( _liquidNode );
        addChild( _beakerNode );
        addChild( _moleculeCountNode );
        addChild( viewControlPanelWrapper );
        
        // Layout
        _liquidControlNode.setOffset( 0, 0 );
        // left-aligned with liquid faucet, liquid column hits bottom of beaker
        _beakerNode.setOffset(
                BeakerNode.getLipOffset().getX(),
                _liquidControlNode.getFullBoundsReference().getMaxY() - _beakerNode.getFullBounds().getHeight() + ( 2 * BeakerNode.getLipOffset().getY() ) );
        // liquid has same offset as beaker, so that it's inside the beaker
        _liquidNode.setOffset( _beakerNode.getOffset() );
        // at right edge of beaker, bottom aligned with liquid faucet
        _waterControlNode.setOffset(
                _beakerNode.getFullBoundsReference().getWidth() - _waterControlNode.getFullBoundsReference().getWidth() + BeakerNode.getLipOffset().getX(), 
                _liquidControlNode.getFullBoundsReference().getMaxY() - _waterControlNode.getFullBoundsReference().getHeight() );
        // probe horizontally centered in beaker, tip of probe at bottom of beaker
        _probeNode.setOffset( 
                _beakerNode.getFullBoundsReference().getCenterX() - _probeNode.getFullBoundsReference().getWidth() / 2, 
                _beakerNode.getFullBoundsReference().getMaxY() - _probeNode.getFullBoundsReference().getHeight() );
        // at left edge of beaker, vertically centered in beaker
        _moleculeCountNode.setOffset( 
                _beakerNode.getFullBoundsReference().getX() + 50,  
                _beakerNode.getFullBoundsReference().getCenterY() - _moleculeCountNode.getFullBoundsReference().getHeight() / 2  );
        // below beaker, to the left
        _drainControlNode.setOffset( 10,  _beakerNode.getFullBoundsReference().getMaxY() + 12 );
        // below beaker, to the right of the drain control
        viewControlPanelWrapper.setOffset( 
                _drainControlNode.getFullBoundsReference().getMaxX() + 30,
                _beakerNode.getFullBoundsReference().getMaxY() + 20 );
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
   
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setMoleculeCountSelected( boolean selected ) {
        _viewControlPanel.setCountSelected( selected );
        _moleculeCountNode.setVisible( selected );
    }
    
    public boolean isMoleculeCountSelected() {
        return _viewControlPanel.isCountSelected();
    }
    
    public void setRatioSelected( boolean selected ) {
        _viewControlPanel.setRatioSelected( selected );
        _liquidNode.setParticlesVisible( selected );
    }
    
    public boolean isRatioSelected() {
        return _viewControlPanel.isRatioSelected();
    }
    
    // for attaching developer control panel
    public ParticlesNode dev_getParticlesNode() {
        return _liquidNode.getParticlesNode();
    }
}
