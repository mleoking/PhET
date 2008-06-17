/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LiquidControlNode extends PNode {

    private final ArrayList _listeners;
    private final LiquidComboBox _comboBox;
    private final FaucetControlNode _faucetControlNode;
    
    public LiquidControlNode( PSwingCanvas canvas ) {
        super();
        
        _listeners = new ArrayList();
        
        _comboBox = new LiquidComboBox();
        PSwing comboBoxWrapper = new PSwing( _comboBox );
        _comboBox.setEnvironment( comboBoxWrapper, canvas ); // hack required by PComboBox
        _comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                notifyLiquidChanged();
                _faucetControlNode.setOn( _comboBox.getChoice() != null ); // automatically turn on the faucet
                _faucetControlNode.setEnabled( _comboBox.getChoice() != null ); // automatically turn on the faucet
            }
        } );
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_RIGHT );
        _faucetControlNode.setOn( false );
        _faucetControlNode.setEnabled( false ); // disabled until user makes a liquid choice
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                notifyOnOffChanged();
            }
        });
        
        addChild( comboBoxWrapper );
        addChild( _faucetControlNode );
        
        PBounds cb = comboBoxWrapper.getFullBoundsReference();
        _faucetControlNode.setOffset( cb.getX(), cb.getMaxY() + 5 );
    }
    
    public void setOn( boolean on ) {
        _faucetControlNode.setOn( on );
    }
    
    public boolean isOn() {
        return _faucetControlNode.isOn();
    }
    
    public void setLiquid( Liquid liquid ) {
        _comboBox.setChoice( liquid );
    }
    
    public Liquid getLiquid() {
        return _comboBox.getChoice();
    }
    
    public interface LiquidControlListener {
        public void liquidChanged();
        public void onOffChanged();
    }
    
    public static class LiquidControlAdapter implements LiquidControlListener {
        public void liquidChanged() {}
        public void onOffChanged() {}
    }
    
    public void addLiquidControlListener( LiquidControlListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeLiquidControlListener( LiquidControlListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyLiquidChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidControlListener) i.next() ).liquidChanged();
        }
    }
    
    private void notifyOnOffChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidControlListener) i.next() ).onOffChanged();
        }
    }
}
