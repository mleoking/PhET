/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LiquidControlNode extends PNode {

    private final Liquid _liquid;
    private final LiquidComboBox _comboBox;
    private final FaucetControlNode _faucetControlNode;
    
    public LiquidControlNode( PSwingCanvas canvas, Liquid liquid ) {
        super();
        
        _liquid = liquid;
        
        _comboBox = new LiquidComboBox();
        PSwing comboBoxWrapper = new PSwing( _comboBox );
        _comboBox.setEnvironment( comboBoxWrapper, canvas ); // hack required by PComboBox
        _comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                LiquidDescriptor liquidDescriptor = _comboBox.getChoice();
                if ( liquidDescriptor != null ) {
                    _liquid.setLiquid( liquidDescriptor, 1 );
                }
                _faucetControlNode.setOn( _comboBox.getChoice() != null ); // automatically turn on the faucet
                _faucetControlNode.setEnabled( _comboBox.getChoice() != null ); // automatically turn on the faucet
            }
        } );
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_RIGHT );
        _faucetControlNode.setOn( false );
        _faucetControlNode.setEnabled( false ); // disabled until user makes a liquid choice
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                //XXX
            }
        });
        
        addChild( comboBoxWrapper );
        addChild( _faucetControlNode );
        
        PBounds cb = comboBoxWrapper.getFullBoundsReference();
        _faucetControlNode.setOffset( cb.getX(), cb.getMaxY() + 5 );
        
        setLiquidDescriptor( _liquid.getBaseLiquid() );
    }
    
    public void setOn( boolean on ) {
        _faucetControlNode.setOn( on );
    }
    
    public boolean isOn() {
        return _faucetControlNode.isOn();
    }
    
    public void setLiquidDescriptor( LiquidDescriptor liquidDescriptor ) {
        _comboBox.setChoice( liquidDescriptor );
    }
    
    public LiquidDescriptor getLiquidDescriptor() {
        return _comboBox.getChoice();
    }
}
