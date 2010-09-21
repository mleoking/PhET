/* Copyright 2008, University of Colorado */

package edu.colorado.phet.capacitorlab.drag;

import java.awt.Font;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;

/**
 * Value displayed on a drag handle.
 * The value is formatted, and its presentation is localizable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DragHandleValueNode extends HTMLNode {

    private static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat( "0.0" );
    private static final Font DEFAULT_FONT = new PhetFont( 18 );
    
    private final String pattern;
    private final String units;
    private final NumberFormat format;
    
    public DragHandleValueNode( String pattern, double value, String units ) {
        this( pattern, value, units, DEFAULT_FORMAT );
    }
    
    public DragHandleValueNode( String pattern, double value, String units, NumberFormat format ) {
        this.pattern = pattern;
        this.units = units;
        this.format = format;
        setFont( DEFAULT_FONT );
        setValue( value );
    }
    
    public void setValue( double value ) {
        String valueString = format.format( value );
        String text = MessageFormat.format( pattern, valueString, units );
        setHTML( text );
    }
}
