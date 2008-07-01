/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import edu.colorado.phet.phscale.control.ViewControlPanel.ViewControlPanelListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.PHScaleModel;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.view.*;
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
    
    private static final double PROBE_LENGTH = 475;
    private static final PDimension BEAKER_SIZE = new PDimension( 350, 400 );
    
    private final PHScaleModel _model;
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
    
    public BeakerControlNode( PSwingCanvas pSwingCanvas, PHScaleModel model ) {
        super();
        
        _model = model;
        Liquid liquid = model.getLiquid();
        
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                //XXX update everything
            }
        };
        liquid.addLiquidListener( _liquidListener );
        
        _probeNode = new ProbeNode( PROBE_LENGTH, liquid );
        
        _liquidControlNode = new LiquidControlNode( pSwingCanvas, liquid );
        
        _waterControlNode = new WaterControlNode( liquid );
        
        _drainControlNode = new DrainControlNode( liquid );

        _liquidNode = new LiquidNode( liquid, BEAKER_SIZE );
        
        _beakerNode = new BeakerNode( BEAKER_SIZE, liquid.getMaxVolume() );
        
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
        //XXX this needs to be generalized
        _liquidControlNode.setOffset( 0, 0  );
        PBounds b = _liquidControlNode.getFullBoundsReference();
        _waterControlNode.setOffset( b.getX() + 330, b.getY() + 5 );
        _beakerNode.setOffset( b.getX() + 40,  b.getY() + 160 );
        _liquidNode.setOffset( _beakerNode.getOffset() );
        _drainControlNode.setOffset( b.getX() + 10,  b.getY() + 585 );
        _probeNode.setOffset( b.getX() + 152, b.getY() + 85 );
        _moleculeCountNode.setOffset( b.getX() + 50,  b.getY() + 260 );
        viewControlPanelWrapper.setOffset( b.getX() + 220,  b.getY() + 585 );
    }
    
    public void cleanup() {
        _model.getLiquid().removeLiquidListener( _liquidListener );
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
    public ParticlesNode getParticlesNode() {
        return _liquidNode.getParticlesNode();
    }
    
}
