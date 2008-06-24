/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LiquidControlNode extends PNode {
    
    private static final PDimension LIQUID_COLUMN_SIZE = new PDimension( 20, 440 );
    private static final double FILL_RATE = 0.01; // liters per clock tick
    private static final double FAST_FILL_VOLUME = 1.0; // liters
    private static final double FAST_FILL_RATE = 0.2; // liters

    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final LiquidComboBox _comboBox;
    private final PPath _liquidColumnNode;
    private final FaucetControlNode _faucetControlNode;
    
    public LiquidControlNode( PSwingCanvas canvas, Liquid liquid ) {
        super();
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _comboBox = new LiquidComboBox();
        PSwing comboBoxWrapper = new PSwing( _comboBox );
        _comboBox.setEnvironment( comboBoxWrapper, canvas ); // hack required by PComboBox
        _comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                LiquidDescriptor liquidDescriptor = _comboBox.getChoice();
                if ( liquidDescriptor != null ) {
                    _liquid.drainImmediately();
                    _liquid.setLiquidDescriptor( liquidDescriptor );
                    _liquid.startFilling( FAST_FILL_RATE, liquidDescriptor, FAST_FILL_VOLUME );
                }
                else {
                    _liquidColumnNode.setPaint( null );
                }
                _faucetControlNode.setOn( liquidDescriptor != null ); // automatically turn on the faucet
                _faucetControlNode.setEnabled( liquidDescriptor != null ); // automatically turn on the faucet
            }
        } );
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_RIGHT );
        _faucetControlNode.setOn( false );
        _faucetControlNode.setEnabled( false ); // disabled until user makes a liquid choice
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                if ( on ) {
                    LiquidDescriptor liquidDescriptor = _comboBox.getChoice();
                    _liquid.startFilling( FILL_RATE, liquidDescriptor );
                }
                else {
                    _liquid.stopFilling();
                }
            }
        });
        
        _liquidColumnNode = new PPath( new Rectangle2D.Double( 0, 0, LIQUID_COLUMN_SIZE.getWidth(), LIQUID_COLUMN_SIZE.getHeight() ) );
        _liquidColumnNode.setStroke( null );
        _liquidColumnNode.setVisible( _faucetControlNode.isOn() );
        
        addChild( comboBoxWrapper );
        addChild( _liquidColumnNode );
        addChild( _faucetControlNode );
        
        comboBoxWrapper.setOffset( 0, 0 );
        PBounds cb = comboBoxWrapper.getFullBoundsReference();
        _faucetControlNode.setOffset( cb.getX(), cb.getMaxY() + 5 );
        _liquidColumnNode.setOffset( _faucetControlNode.getFullBoundsReference().getMaxX() - _liquidColumnNode.getFullBoundsReference().getWidth() - 3, _faucetControlNode.getFullBoundsReference().getMaxY() );
        
        setLiquidDescriptor( _liquid.geLiquidDescriptor() );
        _faucetControlNode.setOn( false );
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
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
    
    private void update() {
        _faucetControlNode.setOn( _liquid.isFilling() );
        _liquidColumnNode.setVisible( _liquid.isFilling() && _faucetControlNode.isOn() );
        _liquidColumnNode.setPaint( _liquid.getColor() );
    }
}
