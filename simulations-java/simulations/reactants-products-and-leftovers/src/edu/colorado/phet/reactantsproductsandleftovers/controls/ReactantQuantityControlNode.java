
package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;


public class ReactantQuantityControlNode extends PhetPNode {
    
    private static final PDimension SLIDER_TRACK_SIZE = new PDimension( 15, 75 );
    private static final PDimension SLIDER_KNOB_SIZE = new PDimension( 30, 15 );

    private final ArrayList<ChangeListener> listeners;
    private final IntegerSliderNode sliderNode;
    private final IntegerTextFieldNode textFieldNode;
    private final ChangeListener sliderListener, textFieldListener;

    public ReactantQuantityControlNode( IntegerRange range, PNode imageNode, double imageScale ) {
        super();
        
        listeners = new ArrayList<ChangeListener>();
        sliderNode = new IntegerSliderNode(range, SLIDER_TRACK_SIZE, SLIDER_KNOB_SIZE );
        textFieldNode = new IntegerTextFieldNode( range );
        
        sliderListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                textFieldNode.removeChangeListener( textFieldListener );
                textFieldNode.setValue( sliderNode.getValue() );
                textFieldNode.addChangeListener( textFieldListener );
                fireStateChanged();
            }
        };
        sliderNode.addChangeListener( sliderListener );
        
        textFieldListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                sliderNode.removeChangeListener( sliderListener );
                sliderNode.setValue( textFieldNode.getValue() );
                sliderNode.addChangeListener( sliderListener );
                fireStateChanged();
            }
        };
        textFieldNode.addChangeListener( textFieldListener );
        
        addChild( sliderNode );
        addChild( textFieldNode );
        addChild( imageNode );
        imageNode.scale( imageScale );
        
        // layout, origin at upper right of slider track
        sliderNode.setOffset( 0, 0 );
        double x = sliderNode.getFullBoundsReference().getMinX() - textFieldNode.getFullBoundsReference().getWidth() - 2;
        double y = SLIDER_TRACK_SIZE.getHeight() - textFieldNode.getFullBoundsReference().getHeight();
        textFieldNode.setOffset( x, y );
        x = sliderNode.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        y = sliderNode.getFullBoundsReference().getMaxY() + 15;
        imageNode.setOffset( x, y );
    }
    
    public void setValue( int value ) {
        sliderNode.setValue( value );
    }
    
    public int getValue() {
        return sliderNode.getValue();
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged( event );
        }
    }
}
