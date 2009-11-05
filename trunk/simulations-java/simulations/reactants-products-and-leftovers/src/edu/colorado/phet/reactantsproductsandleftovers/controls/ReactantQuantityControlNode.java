
package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.Font;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Controls for changing and displaying a Reactant quantity value.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReactantQuantityControlNode extends PhetPNode {
    
    private static final PDimension HISTOGRAM_BAR_SIZE = RPALConstants.HISTOGRAM_BAR_SIZE;
    private static final Font NAME_FONT = new PhetFont( 18 );

    private final Reactant reactant;
    private final ReactantChangeListener reactantChangeListener;
    private final IntegerSpinnerNode spinnerNode;
    private final IntegerHistogramBarNode histogramBar;

    public ReactantQuantityControlNode( final Reactant reactant, IntegerRange range, double imageScale, boolean showName ) {
        super();
        
        this.reactant = reactant;
        
        spinnerNode = new IntegerSpinnerNode( range );
        spinnerNode.scale( 1.5 ); //XXX
        histogramBar = new IntegerHistogramBarNode( range, HISTOGRAM_BAR_SIZE );
        PImage imageNode = new PImage( reactant.getImage() );
        imageNode.scale( imageScale );
        HTMLNode nameNode = null;
        if ( showName ) {
            nameNode = new HTMLNode( HTMLUtils.toHTMLString( reactant.getName() ) );
            nameNode.setFont( NAME_FONT );
        }
        
        // when the spinner changes, update the histogram bar and the model
        spinnerNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                histogramBar.setValue( getValue() );
                reactant.setQuantity( getValue() );
            }
        } );
        
        // when the model changes, update this control
        reactantChangeListener = new ReactantChangeAdapter() {
            @Override
            public void quantityChanged() {
                setValue( reactant.getQuantity() );
            }
        };
        reactant.addReactantChangeListener( reactantChangeListener );

        // rendering order
        addChild( spinnerNode );
        addChild( histogramBar );
        addChild( imageNode );
        if ( nameNode != null ) {
            addChild( nameNode );
        }
        
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
        // name centered below image
        if ( nameNode != null ) {
            x = imageNode.getFullBoundsReference().getCenterX() - ( nameNode.getFullBoundsReference().getWidth() / 2 );
            y = imageNode.getFullBoundsReference().getMaxY() + 3;
            nameNode.setOffset( x, y );
        }
        
        // initial state
        setValue( reactant.getQuantity() );
    }
    
    public void cleanup() {
        reactant.removeReactantChangeListener( reactantChangeListener );
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
