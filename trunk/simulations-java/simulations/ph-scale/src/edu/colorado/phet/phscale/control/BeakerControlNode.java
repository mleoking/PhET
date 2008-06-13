/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Color;

import edu.colorado.phet.phscale.view.BeakerNode;
import edu.colorado.phet.phscale.view.MoleculeCountNode;
import edu.colorado.phet.phscale.view.ProbeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class BeakerControlNode extends PNode {

    private static final double PROBE_LENGTH = 500;
    private static final PDimension BEAKER_SIZE = new PDimension( 350, 400 );
    
    private final ProbeNode _probeNode;
    private ViewControlPanel _viewControlPanel;
    
    public BeakerControlNode( double pH, double volume, Color color, PSwingCanvas pSwingCanvas ) {
        super();
        
        _probeNode = new ProbeNode( PROBE_LENGTH );
        _probeNode.setPH( pH );
        
        _viewControlPanel = new ViewControlPanel();
        PSwing viewControlPanelWrapper = new PSwing( _viewControlPanel );
        
        LiquidControlNode liquidControlNode = new LiquidControlNode( pSwingCanvas );

        WaterControlNode waterControlNode = new WaterControlNode();
        
        DrainControlNode drainControlNode = new DrainControlNode();

        BeakerNode beakerNode = new BeakerNode( BEAKER_SIZE );
        
        MoleculeCountNode moleculeCountNode = new MoleculeCountNode();
        
        // Rendering order
        addChild( liquidControlNode );
        addChild( waterControlNode );
        addChild( _probeNode );
        addChild( drainControlNode );
        addChild( beakerNode );
        addChild( moleculeCountNode );
        addChild( viewControlPanelWrapper );
        
        // Positions
        beakerNode.setOffset( 75, 150 );//XXX
        liquidControlNode.setOffset( 25, 0 );//XXX
        waterControlNode.setOffset( 400, 0 );//XXX
        drainControlNode.setOffset( 25, 575 );//XXX
        _probeNode.setOffset( 175, 50 );//XXX
        viewControlPanelWrapper.setOffset( 225, 575 );//XXX
        moleculeCountNode.setOffset( 85, 250 );//XXX
    }
}
