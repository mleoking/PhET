/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.control.FaucetControlNode.FaucetControlListener;
import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.model.LiquidDescriptor.LiquidDescriptorAdapter;
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
    
    private static final PDimension WATER_COLUMN_SIZE = new PDimension( 20, 488 );
    public static final Font FONT = PHScaleConstants.CONTROL_FONT;
    private static final LiquidDescriptor WATER = LiquidDescriptor.getWater();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    private final PPath _waterColumnNode;
    private final FaucetControlNode _faucetControlNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public WaterControlNode( Liquid liquid ) {
        super();
        
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
        
        _faucetControlNode = new FaucetControlNode( FaucetControlNode.ORIENTATION_LEFT );
        _faucetControlNode.addFaucetControlListener( new FaucetControlListener() {
            public void onOffChanged( boolean on ) {
                handleFaucetOnOff( on );
            }
        });
        
        _waterColumnNode = new PPath( new Rectangle2D.Double( 0, 0, WATER_COLUMN_SIZE.getWidth(), WATER_COLUMN_SIZE.getHeight() ) );
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
        _waterColumnNode.setOffset( _faucetControlNode.getFullBoundsReference().getMinX() + 8, _faucetControlNode.getFullBoundsReference().getMaxY() );   
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        _faucetControlNode.setOn( _liquid.isFillingWater() );
        _waterColumnNode.setVisible( _liquid.isFillingWater() );
    }
    
    private void handleFaucetOnOff( boolean on ) {
        if ( on ) {
            _liquid.startFillingWater( Liquid.SLOW_FILL_RATE );
        }
        else {
            _liquid.stopFillingWater();
        }
    }
}
