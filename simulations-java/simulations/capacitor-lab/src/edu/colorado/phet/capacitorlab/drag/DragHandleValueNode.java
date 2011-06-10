// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Value displayed on a drag handle.
 * The value is formatted, and its presentation is localizable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class DragHandleValueNode extends PComposite {

    private static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat( "0.0" );
    private static final Font LABEL_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Font VALUE_FONT = new PhetFont( Font.PLAIN, 16 );

    private final String pattern;
    private final String units;
    private final NumberFormat format;
    private final HTMLNode valueNode;

    public DragHandleValueNode( String pattern, String label, double value, String units ) {
        this( pattern, label, value, units, DEFAULT_FORMAT );
    }

    /**
     * Constructor.
     *
     * @param pattern MessageFormat pattern, used to format label, value and units
     * @param label
     * @param value
     * @param units
     * @param format  numeric formatter for value
     */
    public DragHandleValueNode( String pattern, String label, double value, String units, NumberFormat format ) {

        this.pattern = pattern;
        this.units = units;
        this.format = format;

        PText labelNode = new PText( label );
        labelNode.setFont( LABEL_FONT );
        addChild( labelNode );

        valueNode = new HTMLNode() {
            // #2940, adjust HTMLNode bounds are wrong, adjust width to prevent it from leaving artifacts
            @Override public boolean setBounds( final double x, final double y, final double width, final double height ) {
                return super.setBounds( x, y, width + 2, height );
            }
        };
        valueNode.setFont( VALUE_FONT );
        addChild( valueNode );

        // layout: value below label, left-justified
        labelNode.setOffset( 0, 0 );
        valueNode.setOffset( 0, labelNode.getFullBoundsReference().getMaxY() + 1 );

        setValue( value );
    }

    public void setValue( double value ) {
        String valueString = format.format( value );
        String text = MessageFormat.format( pattern, valueString, units );
        valueNode.setHTML( text );
    }
}
