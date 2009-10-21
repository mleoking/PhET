
package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;


public class ReactantQuantityControlNode extends PhetPNode {
    
    //XXX choose spinners or sliders and delete the unused stuff
    private static final boolean USE_SPINNER = false;
    
    private static final PDimension SLIDER_TRACK_SIZE = new PDimension( 15, 75 );
    private static final PDimension SLIDER_KNOB_SIZE = new PDimension( 30, 15 );

    private final ArrayList<ChangeListener> listeners;
    private final IntegerSpinnerNode spinnerNode;
    private final IntegerHistogramBarNode histogramBar;
    private final IntegerSliderNode sliderNode;
    private final IntegerTextFieldNode textFieldNode;
    private final ChangeListener sliderListener, textFieldListener;

    public ReactantQuantityControlNode( IntegerRange range, PNode imageNode, double imageScale ) {
        super();
        
        listeners = new ArrayList<ChangeListener>();
        spinnerNode = new IntegerSpinnerNode( range );
        spinnerNode.scale( 1.5 ); //XXX
        histogramBar = new IntegerHistogramBarNode( range, SLIDER_TRACK_SIZE );
        sliderNode = new IntegerSliderNode( range, SLIDER_TRACK_SIZE, SLIDER_KNOB_SIZE );
        textFieldNode = new IntegerTextFieldNode( range, new PhetFont( 22 ) );
        
        spinnerNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                histogramBar.setValue( spinnerNode.getValue() );
                fireStateChanged();
            }
        });
        
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
        
        if ( USE_SPINNER ) {
            addChild( spinnerNode );
            addChild( histogramBar );
        }
        else {
            addChild( sliderNode );
            addChild( textFieldNode );
        }
        addChild( imageNode );
        imageNode.scale( imageScale );
        
        // layout
        if ( USE_SPINNER ) {
            // origin at upper left of histogram bar
            histogramBar.setOffset( 0, 0 );
            double x = histogramBar.getFullBoundsReference().getMinX() - spinnerNode.getFullBoundsReference().getWidth() - 2;
            double y = SLIDER_TRACK_SIZE.getHeight() - spinnerNode.getFullBoundsReference().getHeight();
            spinnerNode.setOffset( x, y );
            x = histogramBar.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
            y = histogramBar.getFullBoundsReference().getMaxY() + 15;
            imageNode.setOffset( x, y );
        }
        else {
            // origin at upper left of slider track
            sliderNode.setOffset( 0, 0 );
            double x = sliderNode.getFullBoundsReference().getMinX() - textFieldNode.getFullBoundsReference().getWidth() - 2;
            double y = SLIDER_TRACK_SIZE.getHeight() - textFieldNode.getFullBoundsReference().getHeight();
            textFieldNode.setOffset( x, y );
            x = sliderNode.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
            y = sliderNode.getFullBoundsReference().getMaxY() + 15;
            imageNode.setOffset( x, y );
        }
    }
    
    public void setValue( int value ) {
        if ( USE_SPINNER ) {
            spinnerNode.setValue( value );
        }
        else {
            sliderNode.setValue( value );
        }
    }
    
    public int getValue() {
        if ( USE_SPINNER ) {
            return spinnerNode.getValue();
        }
        else {
            return sliderNode.getValue();
        }
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
