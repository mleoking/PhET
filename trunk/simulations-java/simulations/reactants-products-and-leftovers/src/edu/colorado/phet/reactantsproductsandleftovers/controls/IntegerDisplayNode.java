package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance;
import edu.colorado.phet.reactantsproductsandleftovers.view.SubstanceNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class that displays an integer value and histogram bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class IntegerDisplayNode extends PComposite {
    
    private static final PDimension HISTOGRAM_BAR_SIZE = RPALConstants.HISTOGRAM_BAR_SIZE;
    
    private final IntegerHistogramBarNode barNode;
    private final PText valueNode;
    private final SubstanceNode substanceNode;
    
    public IntegerDisplayNode( final Substance substance, IntegerRange range, double imageScale ) {
        super();
        
        // bar
        barNode = new IntegerHistogramBarNode( range, HISTOGRAM_BAR_SIZE );

        // value
        valueNode = new PText(); 
        valueNode.setFont( new PhetFont( 22 ) );

        // image
        substanceNode = new SubstanceNode( substance );
        substanceNode.scale( imageScale );
        
        // rendering order
        addChild( barNode );
        addChild( valueNode );
        addChild( substanceNode );
        
        substanceNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout(); // to preserve center justification
                }
            }
        });
        
        valueNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout(); // to preserve right justification
                }
            }
        });
        
        updateLayout();
        
        setValue( substance.getQuantity() );
    }
    
    public void cleanup() {
        substanceNode.cleanup();
    }
    
    public void setValue( int value ) {
        barNode.setValue( value );
        valueNode.setText( String.valueOf( value ) );
    }
    
    private void updateLayout() {
        // origin at top center of bar
        double x = -( barNode.getFullBoundsReference().getWidth() / 2 );
        double y = 0;
        barNode.setOffset( x, y );
        // value at lower left of bar
        x = barNode.getFullBoundsReference().getMinX() - valueNode.getFullBoundsReference().getWidth() - 6;
        y = barNode.getFullBoundsReference().getMaxY() - valueNode.getFullBoundsReference().getHeight();
        valueNode.setOffset( x, y );
        // image centered below bar
        x = barNode.getFullBoundsReference().getCenterX() - ( substanceNode.getFullBoundsReference().getWidth() / 2 );
        y = barNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( substanceNode ) + 15;
        substanceNode.setOffset( x, y );
    }

}
