/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.beaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.model.LiquidDescriptor.LiquidDescriptorAdapter;
import edu.colorado.phet.phscale.view.beaker.FaucetControlNode.FaucetControlListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * WaterControlNode contains on/off faucet for adding water to the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WaterControlNode extends PNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final PDimension LIQUID_COLUMN_SIZE = PHScaleConstants.LIQUID_COLUMN_SIZE;
    private static final double MIN_LIQUID_COLUMN_WIDTH = PHScaleConstants.MIN_LIQUID_COLUMN_WIDTH;
    public static final Font FONT = PHScaleConstants.CONTROL_FONT;
    private static final LiquidDescriptor WATER = LiquidDescriptor.getWater();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final Rectangle2D _waterColumnShape;
    private final PPath _waterColumnNode;
    private final FaucetControlNode _faucetControlNode;
    private boolean _notifyEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WaterControlNode( Liquid liquid, double maxFillRate ) {
        super();
        
        _notifyEnabled = true;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        WATER.addLiquidDescriptorListener( new LiquidDescriptorAdapter() {
            public void colorChanged( Color color ) {
                _waterColumnNode.setPaint( WATER.getColor() );
            }
        } );
        
        JLabel label = new JLabel( WATER.toString() );
        label.setFont( FONT );
        PSwing labelWrapper = new PSwing( label );
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_LEFT, maxFillRate );
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void valueChanged() {
                if ( _notifyEnabled ) {
                    _liquid.setWaterFillRate( _faucetControlNode.getValue() );
                }
            }
        });
        
        _waterColumnShape = new Rectangle2D.Double();
        _waterColumnNode = new PPath( _waterColumnShape );
        _waterColumnNode.setPaint( LiquidDescriptor.getWater().getColor() );
        _waterColumnNode.setStroke( null );
        _waterColumnNode.setVisible( _faucetControlNode.isOn() );
        _waterColumnNode.setPickable( false );
        _waterColumnNode.setChildrenPickable( false );
        
        addChild( labelWrapper );
        addChild( _waterColumnNode );
        addChild( _faucetControlNode );
        
        labelWrapper.setOffset( 0, 0 );
        PBounds lb = labelWrapper.getFullBoundsReference();
        PBounds fb = _faucetControlNode.getFullBoundsReference();
        _faucetControlNode.setOffset( lb.getMaxX() - fb.getWidth(), lb.getMaxY() + 5 );
        _waterColumnNode.setOffset( _faucetControlNode.getFullBoundsReference().getMinX() + 18, _faucetControlNode.getFullBoundsReference().getMaxY() );   
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        
        _notifyEnabled = false;
        _faucetControlNode.setValue( _liquid.getWaterFillRate() );
        _faucetControlNode.setEnabled( !_liquid.isFull() );
        _notifyEnabled = true;
        
        _waterColumnNode.setVisible( _liquid.isFillingWater() );
        
        // shape of the water column
        final double percentOn = _faucetControlNode.getPercentOn();
        final double columnWidth = MIN_LIQUID_COLUMN_WIDTH + ( percentOn * ( LIQUID_COLUMN_SIZE.getWidth() - MIN_LIQUID_COLUMN_WIDTH ) );
        _waterColumnShape.setRect( -columnWidth/2, 0, columnWidth, LIQUID_COLUMN_SIZE.getHeight() );
        _waterColumnNode.setPathTo( _waterColumnShape );
    }
    
}
