/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.view.phslider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.phscale.model.Liquid;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
import edu.colorado.phet.phscale.model.LiquidDescriptor.CustomLiquidDescriptor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * PHControlNode is the control for pH, composed of a text field and a slider.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHControlNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int MARGIN = 15;
    private static final double TRACK_WIDTH = 10;
    private static final PDimension KNOB_SIZE = new PDimension( 40, 30 );
    
    private static final CustomLiquidDescriptor CUSTOM_LIQUID = LiquidDescriptor.getCustom();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Liquid _liquid;
    private final LiquidListener _liquidListener;
    
    private final PHTextFieldNode _textFieldNode;
    private final PHSliderNode _sliderNode;
    private boolean _notifyEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PHControlNode( Liquid liquid, double ticksYSpacing ) {
        super();
        
        _notifyEnabled = false;
        
        _liquid = liquid;
        _liquidListener = new LiquidListener() {
            public void stateChanged() {
                update();
            }
        };
        _liquid.addLiquidListener( _liquidListener );
        
        _textFieldNode = new PHTextFieldNode( liquid.getPHRange() );
        _sliderNode = new PHSliderNode( liquid.getPHRange(), TRACK_WIDTH, ticksYSpacing, KNOB_SIZE );
        
        PNode parentNode = new PNode();
        parentNode.addChild( _textFieldNode );
        parentNode.addChild( _sliderNode );
        _textFieldNode.setOffset( 0, 0 );
        PBounds vb = _textFieldNode.getFullBoundsReference();
        _sliderNode.setOffset( ( vb.getWidth() / 2 ) - ( TRACK_WIDTH / 2 ), vb.getHeight() + KNOB_SIZE.getWidth()/2 + 10 );
        
        double w = parentNode.getFullBoundsReference().getWidth() + ( 2 * MARGIN );
        double h = parentNode.getFullBoundsReference().getHeight() + ( 2 * MARGIN ) + KNOB_SIZE.getWidth()/2;
        PPath outlineNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        outlineNode.setStroke( new BasicStroke( 2f ) );
        outlineNode.setStrokePaint( Color.BLACK );
        
        addChild( parentNode );
        addChild( outlineNode );
        
        outlineNode.setOffset( 0, 0 );
        PBounds ob = outlineNode.getFullBoundsReference();
        PBounds pb = parentNode.getFullBoundsReference();
        parentNode.setOffset( ( ob.getWidth() - pb.getWidth() ) / 2, MARGIN );
        
        _textFieldNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateModelPH(_textFieldNode.getPH() );
            }
        });
        _sliderNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateModelPH(_sliderNode.getPH() );
            }
        });
        
        update();
    }
    
    public void cleanup() {
        _liquid.removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Gets the offset used to vertically align the graph ticks with the pH slider ticks.
     * Offset is in global coordinates.
     * Only the y offset is meaningful.
     * 
     * @return
     */
    public Point2D getTickAlignmentGlobalOffset() {
        return _sliderNode.getTickAlignmentGlobalOffset();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void updateModelPH( double pH ) {
        if ( _notifyEnabled ) {
            _liquid.setPH( pH );
        }
    }
    
    private void update() {
        _notifyEnabled = false;
        Double pH = _liquid.getPH();
        if ( pH != null ) {
            _sliderNode.setEnabled( true );
            _textFieldNode.setEnabled( true );
            _sliderNode.setPH( pH.doubleValue() );
            _textFieldNode.setPH( pH.doubleValue() );
        }
        else {
            _sliderNode.setEnabled( false );
            _textFieldNode.setEnabled( false );
        }
        _notifyEnabled = true;
    }
}
