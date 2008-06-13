/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.phscale.model.PHScaleModel;
import edu.colorado.phet.phscale.model.Liquid.LiquidAdapter;
import edu.colorado.phet.phscale.model.Liquid.LiquidListener;
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
    
    private static final int MARGIN = 15;

    private static final PDimension SLIDER_TRACK_SIZE = new PDimension( 10, 400 );
    private static final PDimension KNOB_SIZE = new PDimension( 40, 30 );
    
    private final PHScaleModel _model;
    private final LiquidListener _liquidListener;
    
    private final PHTextFieldNode _textFieldNode;
    private final PHSliderNode _sliderNode;
    
    public PHControlNode( IntegerRange range, PHScaleModel model ) {
        super();
        
        _model = model;
        
        _liquidListener = new LiquidAdapter() {
            public void pHChanged( double pH ) {
                _textFieldNode.setPH( pH );
                _sliderNode.setPH( pH );
            }
        };
        _model.getLiquid().addLiquidListener( _liquidListener );
        
        _textFieldNode = new PHTextFieldNode( range );
        _sliderNode = new PHSliderNode( range, SLIDER_TRACK_SIZE, KNOB_SIZE );
        
        PNode parentNode = new PNode();
        parentNode.addChild( _textFieldNode );
        parentNode.addChild( _sliderNode );
        _textFieldNode.setOffset( 0, 0 );
        PBounds vb = _textFieldNode.getFullBoundsReference();
        _sliderNode.setOffset( ( vb.getWidth() / 2 ) - ( SLIDER_TRACK_SIZE.getWidth() / 2 ), vb.getHeight() + KNOB_SIZE.getWidth()/2 + 10 );
        
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
                _model.getLiquid().setPH( _textFieldNode.getPH() );
            }
        });
        _sliderNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                _model.getLiquid().setPH( _sliderNode.getPH() );
            }
        });
    }
    
    public void cleanup() {
        _model.getLiquid().removeLiquidListener( _liquidListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getPH() {
        return _sliderNode.getPH();
    }
    
    public void setPH( double pH ) {
        _sliderNode.setPH( pH );
    }
}
