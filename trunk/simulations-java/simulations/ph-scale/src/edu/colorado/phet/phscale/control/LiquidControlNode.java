/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.util.DialogUtils;
import edu.colorado.phet.phscale.PHScaleApplication;
import edu.colorado.phet.phscale.PHScaleStrings;
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
    
    private LiquidDescriptor _selectedLiquidDescriptor;
    private boolean _autoFilling;
    
    public LiquidControlNode( PSwingCanvas canvas, Liquid liquid ) {
        super();
        
        _autoFilling = false;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _comboBox = new LiquidComboBox();
        _comboBox.setChoice( _liquid.getLiquidDescriptor() );
        _selectedLiquidDescriptor = _comboBox.getChoice();
        _comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                handleLiquidSelection();
            }
        } );
        PSwing comboBoxWrapper = new PSwing( _comboBox );
        _comboBox.setEnvironment( comboBoxWrapper, canvas ); // hack required by PComboBox
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_RIGHT );
        _faucetControlNode.setOn( false );
        _faucetControlNode.setEnabled( _comboBox.getChoice() != null ); // disabled until a liquid is chosen
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                handleFaucetOnOff( on );
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
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    private void update() {
        if ( _autoFilling ) {
            _autoFilling = _liquid.isFilling();
            _faucetControlNode.setOn( _liquid.isFilling() );
        }
        _liquidColumnNode.setVisible( _liquid.isFilling() && _faucetControlNode.isOn() );
        _liquidColumnNode.setPaint( _liquid.getColor() );
    }
    
    private void handleLiquidSelection() {
        LiquidDescriptor liquidDescriptor = _comboBox.getChoice();
        if ( liquidDescriptor != null ) {
            if ( liquidDescriptor != _selectedLiquidDescriptor ) {
                boolean confirm = true;
                if ( !_liquid.isEmpty() ) {
                    confirm = confirmChangeLiquid();
                }
                if ( confirm ) {
                    _autoFilling = true;
                    _selectedLiquidDescriptor = liquidDescriptor;
                    _liquid.setLiquidDescriptor( liquidDescriptor );
                    _liquid.startFilling( FAST_FILL_RATE, liquidDescriptor, FAST_FILL_VOLUME );
                }
                else {
                    _comboBox.setChoice( _selectedLiquidDescriptor );
                }
            }
        }
        else {
            _liquidColumnNode.setPaint( null );
        }
        _faucetControlNode.setOn( liquidDescriptor != null ); // automatically turn on the faucet
        _faucetControlNode.setEnabled( liquidDescriptor != null ); // automatically turn on the faucet
    }
    
    private void handleFaucetOnOff( boolean on ) {
        if ( on ) {
            LiquidDescriptor liquidDescriptor = _comboBox.getChoice();
            _liquid.startFilling( FILL_RATE, liquidDescriptor );
        }
        else {
            _liquid.stopFilling();
        }
    }
    
    private static boolean confirmChangeLiquid() {
        Frame parent = PHScaleApplication.instance().getPhetFrame();
        String message = PHScaleStrings.CONFIRM_CHANGE_LIQUID;
        int rval = DialogUtils.showConfirmDialog( parent, message, JOptionPane.YES_NO_OPTION );
        return ( rval == JOptionPane.YES_OPTION );
    }
}
