/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.model.LiquidDescriptor.CustomLiquidDescriptor;
import edu.colorado.phet.phscale.view.*;
import edu.colorado.phet.phscale.view.beaker.ViewControlPanel.ViewControlPanelListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
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
    
    private static final double PROBE_LENGTH = 525;
    private static final PDimension BEAKER_SIZE = new PDimension( 450, 450 );
    
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
    private final MoleculeCountAlternateNode _moleculeCountAlternateNode;
    private final ViewControlPanel _viewControlPanel;
    private final DrainControlNode _drainControlNode;

    private boolean _useAlternateMoleculeCountView;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BeakerControlNode( PSwingCanvas pSwingCanvas, Liquid liquid ) {
        super();
        
        _useAlternateMoleculeCountView = false;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                // hide the water faucet if the Custom liquid is selected
                _waterControlNode.setVisible( !_liquid.getLiquidDescriptor().equals( CUSTOM_LIQUID ) );
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _probeNode = new ProbeNode( PROBE_LENGTH, liquid );
        
        _liquidControlNode = new LiquidControlNode( pSwingCanvas, liquid );
        
        _waterControlNode = new WaterControlNode( liquid );
        
        _drainControlNode = new DrainControlNode( liquid );

        _liquidNode = new LiquidNode( liquid, BEAKER_SIZE );
        
        _beakerNode = new BeakerNode( BEAKER_SIZE, liquid.getMaxVolume() );
        
        _moleculeCountNode = new MoleculeCountNode( liquid );
        _moleculeCountNode.setVisible( !_useAlternateMoleculeCountView );
        _moleculeCountAlternateNode = new MoleculeCountAlternateNode( liquid );
        _moleculeCountAlternateNode.setVisible( _useAlternateMoleculeCountView  );
        
        _viewControlPanel = new ViewControlPanel();
        PSwing viewControlPanelWrapper = new PSwing( _viewControlPanel );
        _viewControlPanel.addViewControlPanelListener( new ViewControlPanelListener() {

            public void countChanged( boolean selected ) {
                _moleculeCountNode.setVisible( selected && !_useAlternateMoleculeCountView );
                _moleculeCountAlternateNode.setVisible( selected && _useAlternateMoleculeCountView );
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
        addChild( _moleculeCountAlternateNode );
        addChild( viewControlPanelWrapper );
        
        // Layout
        //XXX this needs to be generalized
        _liquidControlNode.setOffset( 0, 0  );
        PBounds b = _liquidControlNode.getFullBoundsReference();
        _waterControlNode.setOffset( b.getX() + 390, b.getY() + 5 );
        _beakerNode.setOffset( 20,  b.getY() + 160 );
        _liquidNode.setOffset( _beakerNode.getOffset() );
        _drainControlNode.setOffset( b.getX() + 10,  b.getY() + 623 );
        _probeNode.setOffset( b.getX() + 185, b.getY() + 85 );
        _moleculeCountAlternateNode.setOffset( 170, 230 );
        _moleculeCountNode.setOffset( b.getX() + 60,  b.getY() + 320 );
        viewControlPanelWrapper.setOffset( b.getX() + 220,  b.getY() + 635 );
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
   
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setMoleculeCountSelected( boolean selected ) {
        _viewControlPanel.setCountSelected( selected );
        _moleculeCountNode.setVisible( selected && _useAlternateMoleculeCountView );
        _moleculeCountAlternateNode.setVisible( selected && !_useAlternateMoleculeCountView );
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
    
    public void dev_setUseAlternateMoleculeCountView( boolean b ) {
        _useAlternateMoleculeCountView = b;
        if ( _viewControlPanel.isCountSelected() ) {
            _moleculeCountNode.setVisible( !_useAlternateMoleculeCountView );
            _moleculeCountAlternateNode.setVisible( _useAlternateMoleculeCountView );
        }
    }
}
