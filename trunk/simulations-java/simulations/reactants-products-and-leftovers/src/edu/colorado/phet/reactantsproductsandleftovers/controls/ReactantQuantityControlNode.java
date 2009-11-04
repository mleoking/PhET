
package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;


public class ReactantQuantityControlNode extends PhetPNode {
    
    private static final PDimension HISTOGRAM_BAR_SIZE = new PDimension( 15, 75 );

    private final IntegerSpinnerNode spinnerNode;
    private final IntegerHistogramBarNode histogramBar;

    public ReactantQuantityControlNode( final Reactant reactant, IntegerRange range, double imageScale ) {
        super();
        
        spinnerNode = new IntegerSpinnerNode( range );
        spinnerNode.scale( 1.5 ); //XXX
        histogramBar = new IntegerHistogramBarNode( range, HISTOGRAM_BAR_SIZE );
        PImage imageNode = new PImage( reactant.getNode().toImage() );
        imageNode.scale( imageScale );
        
        // when the spinner changes, update the histogram bar and the model
        spinnerNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                histogramBar.setValue( getValue() );
                reactant.setQuantity( getValue() );
            }
        } );
        
        // when the model changes, update this control
        reactant.addReactantChangeListener( new ReactantChangeAdapter() {
            @Override
            public void quantityChanged() {
                setValue( reactant.getQuantity() );
            }
        });

        // rendering order
        addChild( spinnerNode );
        addChild( histogramBar );
        addChild( imageNode );
        
        // layout
        // origin at top center of histogram bar
        double x = -histogramBar.getFullBoundsReference().getWidth() / 2;
        double y = 0;
        histogramBar.setOffset( x, y );
        // spinner at lower left of histogram bar
        x = histogramBar.getFullBoundsReference().getMinX() - spinnerNode.getFullBoundsReference().getWidth() - 2;
        y = HISTOGRAM_BAR_SIZE.getHeight() - spinnerNode.getFullBoundsReference().getHeight();
        spinnerNode.setOffset( x, y );
        // image centered below histogram bar
        x = histogramBar.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        y = histogramBar.getFullBoundsReference().getMaxY() + 15;
        imageNode.setOffset( x, y );
        
        // initial state
        setValue( reactant.getQuantity() );
    }
    
    public void setValue( int value ) {
        if ( value != getValue() ) {
            spinnerNode.setValue( value );
        }
    }
    
    public int getValue() {
        return spinnerNode.getValue();
    }
}
