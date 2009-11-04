package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance.SubstanceChangeAdapter;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class QuantityDisplayNode extends PComposite {
    
    private static final PDimension HISTOGRAM_BAR_SIZE = RPALConstants.HISTOGRAM_BAR_SIZE;
    
    private final IntegerHistogramBarNode barNode;
    private final PText valueNode;
    private final PImage imageNode;
    
    public QuantityDisplayNode( final Substance substance, IntegerRange range, double imageScale ) {
        super();
        
        // bar
        barNode = new IntegerHistogramBarNode( range, HISTOGRAM_BAR_SIZE );

        // value
        valueNode = new PText(); 
        valueNode.setFont( new PhetFont( 22 ) );

        // image
        imageNode = new PImage( substance.getImage() );
        imageNode.scale( imageScale );
        
        // rendering order
        addChild( barNode );
        addChild( valueNode );
        addChild( imageNode );
        
        substance.addSubstanceChangeListener( new SubstanceChangeAdapter() {
            @Override
            public void quantityChanged() {
                setValue( substance.getQuantity() );
            }
            @Override
            public void imageChanged() {
                imageNode.setImage( substance.getImage() );
            }
        });
        
        imageNode.addPropertyChangeListener( new PropertyChangeListener() {
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
        x = barNode.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        y = barNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( imageNode ) + 15;
        imageNode.setOffset( x, y );
    }

}
