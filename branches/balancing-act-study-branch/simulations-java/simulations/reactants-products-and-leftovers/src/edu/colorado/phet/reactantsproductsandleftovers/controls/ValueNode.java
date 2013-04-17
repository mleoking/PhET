// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.Font;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for value controls that appears under the Before and After boxes.
 * Displays a value in histogram and numeric form.
 * The numeric value is optionally editable, via a spinner.
 * Labeled using an image and text label.
 * The origin is at the top-center of the histogram bar.
 * <p/>
 * This control has no dependencies on the model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ValueNode extends PhetPNode {

    private static final PDimension HISTOGRAM_BAR_SIZE = RPALConstants.HISTOGRAM_BAR_SIZE;
    private static final Font VALUE_FONT = new PhetFont( 24 );
    private static final Font NAME_FONT = new PhetFont( 18 );

    private final EventListenerList listeners;
    private final HistogramBarNode barNode;
    private final PImage imageNode;
    private final HTMLNode nameNode;
    private final IntegerSpinnerNode spinnerNode;
    private final PText valueNode;

    private int value;

    public ValueNode( IUserComponent userComponent, IntegerRange range, int value, Image image, double imageScale, String name, boolean showName, boolean editable ) {
        super();

        if ( !range.contains( value ) ) {
            throw new IllegalArgumentException( "value is out of range: " + value );
        }

        listeners = new EventListenerList();

        // bar
        barNode = new HistogramBarNode( value, range.getMin(), range.getMax(), HISTOGRAM_BAR_SIZE );

        // image
        imageNode = new PImage( image );
        imageNode.scale( imageScale );

        // name
        nameNode = new HTMLNode( HTMLUtils.toHTMLString( name ) );
        nameNode.setFont( NAME_FONT );

        // editable value
        spinnerNode = new IntegerSpinnerNode( userComponent, range );
        spinnerNode.scale( 1.5 ); // setting font size would be preferable, but doesn't change size of up/down arrows on Mac
        spinnerNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( spinnerNode.getValue() );
            }
        } );

        // read-only value
        valueNode = new PText();
        valueNode.setFont( VALUE_FONT );

        // rendering order
        addChild( barNode );
        addChild( imageNode );
        if ( showName ) {
            addChild( nameNode );
        }
        addChild( spinnerNode );
        addChild( valueNode );

        // children that are not pickable
        barNode.setPickable( false );
        imageNode.setPickable( false );
        nameNode.setPickable( false );
        valueNode.setPickable( false );

        imageNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout(); // to preserve center justification
                }
            }
        } );

        valueNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout(); // to preserve right justification
                }
            }
        } );

        updateLayout();
        setEditable( editable );
        this.value = value - 1; // force update
        setValue( value );
    }

    public void setValue( int value ) {
        if ( value != this.value ) {
            this.value = value;
            barNode.setValue( value );
            spinnerNode.setValue( value );
            valueNode.setText( String.valueOf( value ) );
            fireStateChanged();
        }
    }

    public int getValue() {
        return spinnerNode.getValue();
    }

    public void setEditable( boolean editable ) {
        spinnerNode.setPickable( editable );
        spinnerNode.setEnabled( editable );
        if ( editable ) {
            addChild( spinnerNode );
            removeChild( valueNode );
        }
        else {
            removeChild( spinnerNode );
            addChild( valueNode );
        }
    }

    public void setImage( Image image ) {
        imageNode.setImage( image );
    }

    public void setValueVisible( boolean visible ) {
        valueNode.setVisible( visible );
    }

    public void setHistogramBarVisible( boolean visible ) {
        barNode.setVisible( visible );
    }

    public void setImageVisible( boolean visible ) {
        imageNode.setVisible( visible );
    }

    private void updateLayout() {
        // origin at top center of bar
        double x = -( barNode.getFullBoundsReference().getWidth() / 2 );
        double y = 0;
        barNode.setOffset( x, y );
        // spinner at lower left of histogram bar
        x = barNode.getFullBoundsReference().getMinX() - spinnerNode.getFullBoundsReference().getWidth() - 2;
        y = HISTOGRAM_BAR_SIZE.getHeight() - spinnerNode.getFullBoundsReference().getHeight();
        spinnerNode.setOffset( x, y );
        // read-only value at lower left of bar
        x = barNode.getFullBoundsReference().getMinX() - valueNode.getFullBoundsReference().getWidth() - 6;
        y = barNode.getFullBoundsReference().getMaxY() - valueNode.getFullBoundsReference().getHeight();
        valueNode.setOffset( x, y );
        // image centered below bar
        x = barNode.getFullBoundsReference().getCenterX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        y = barNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( imageNode ) + 15;
        imageNode.setOffset( x, y );
        // name centered below image
        x = imageNode.getFullBoundsReference().getCenterX() - ( nameNode.getFullBoundsReference().getWidth() / 2 );
        y = imageNode.getFullBoundsReference().getMaxY() + 3;
        nameNode.setOffset( x, y );
    }

    public void addChangeListener( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }

    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}
