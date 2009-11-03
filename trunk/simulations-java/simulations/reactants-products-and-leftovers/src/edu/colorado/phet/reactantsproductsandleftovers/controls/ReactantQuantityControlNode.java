
package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;


public class ReactantQuantityControlNode extends PhetPNode {
    
    private static final PDimension HISTOGRAM_BAR_SIZE = new PDimension( 15, 75 );

    private final ArrayList<ChangeListener> listeners;
    private final IntegerSpinnerNode spinnerNode;
    private final IntegerHistogramBarNode histogramBar;

    public ReactantQuantityControlNode( IntegerRange range, PNode imageNode, double imageScale ) {
        super();
        
        listeners = new ArrayList<ChangeListener>();
        spinnerNode = new IntegerSpinnerNode( range );
        spinnerNode.scale( 1.5 ); //XXX
        histogramBar = new IntegerHistogramBarNode( range, HISTOGRAM_BAR_SIZE );
        
        spinnerNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                histogramBar.setValue( spinnerNode.getValue() );
                fireStateChanged();
            }
        } );

        addChild( spinnerNode );
        addChild( histogramBar );
        addChild( imageNode );
        imageNode.scale( imageScale );
        
        // layout
        // origin at upper left of histogram bar
        histogramBar.setOffset( 0, 0 );
        double x = histogramBar.getFullBoundsReference().getMinX() - spinnerNode.getFullBoundsReference().getWidth() - 2;
        double y = HISTOGRAM_BAR_SIZE.getHeight() - spinnerNode.getFullBoundsReference().getHeight();
        spinnerNode.setOffset( x, y );
        x = histogramBar.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        y = histogramBar.getFullBoundsReference().getMaxY() + 15;
        imageNode.setOffset( x, y );
    }
    
    public void setValue( int value ) {
        spinnerNode.setValue( value );
    }
    
    public int getValue() {
        return spinnerNode.getValue();
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
