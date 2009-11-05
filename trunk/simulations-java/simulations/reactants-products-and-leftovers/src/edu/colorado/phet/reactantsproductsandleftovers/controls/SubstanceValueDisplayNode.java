package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.model.Substance;
import edu.colorado.phet.reactantsproductsandleftovers.view.SubstanceImageNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class that for nodes that display some integer value related to a Substance.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SubstanceValueDisplayNode extends PComposite {
    
    private static final PDimension HISTOGRAM_BAR_SIZE = RPALConstants.HISTOGRAM_BAR_SIZE;
    private static final Font VALUE_FONT = new PhetFont( 22 );
    private static final Font NAME_FONT = new PhetFont( 18 );
    
    private final IntegerHistogramBarNode barNode;
    private final PText valueNode;
    private final SubstanceImageNode imageNode;
    private final HTMLNode nameNode;
    
    public SubstanceValueDisplayNode( final Substance substance, IntegerRange range, double imageScale, boolean showName ) {
        super();
        
        // bar
        barNode = new IntegerHistogramBarNode( range, HISTOGRAM_BAR_SIZE );

        // value
        valueNode = new PText(); 
        valueNode.setFont( VALUE_FONT );

        // image
        imageNode = new SubstanceImageNode( substance );
        imageNode.scale( imageScale );
        
        // name
        if ( showName ) {
            nameNode = new HTMLNode( HTMLUtils.toHTMLString( substance.getName() ) );
            nameNode.setFont( NAME_FONT );
        }
        else {
            nameNode = null;
        }
        
        // rendering order
        addChild( barNode );
        addChild( valueNode );
        addChild( imageNode );
        if ( nameNode != null ) {
            addChild( nameNode );
        }
        
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
    }
    
    public void cleanup() {
        imageNode.cleanup();
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
        // name centered below image
        if ( nameNode != null ) {
            x = imageNode.getFullBoundsReference().getCenterX() - ( nameNode.getFullBoundsReference().getWidth() / 2 );
            y = imageNode.getFullBoundsReference().getMaxY() + 3;
            nameNode.setOffset( x, y );
        }
    }

}
