// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * FormattedNumberNode displays a value using a specified NumberFormat.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FormattedNumberNode extends PComposite {

    private static final Font DEFAULT_FONT = new PhetFont();
    private static final Color DEFAULT_COLOR = Color.BLACK;

    private NumberFormat _format;
    private double _value;
    private final HTMLNode _htmlNode; // use HTMLNode to provide more formatting flexibility, eg, superscripts and subscripts

    public FormattedNumberNode( NumberFormat format ) {
        this( format, 0, DEFAULT_FONT, DEFAULT_COLOR );
    }

    public FormattedNumberNode( NumberFormat format, double value ) {
        this( format, value, DEFAULT_FONT, DEFAULT_COLOR );
    }

    public FormattedNumberNode( NumberFormat format, double value, Font font, Color textColor ) {
        _format = format;
        _value = value;
        _htmlNode = new HTMLNode( _format.format( value ) );
        _htmlNode.setFont( font );
        _htmlNode.setHTMLColor( textColor );
        addChild( _htmlNode );
    }

    public void setValue( double value ) {
        if ( value != _value ) {
            _value = value;
            _htmlNode.setHTML( _format.format( value ) );
        }
    }

    public double getValue() {
        return _value;
    }

    public void setFont( Font font ) {
        _htmlNode.setFont( font );
    }

    public Font getFont() {
        return _htmlNode.getFont();
    }

    public void setTextColor( Color color ) {
        _htmlNode.setHTMLColor( color );
    }

    public Color getTextColor() {
        return _htmlNode.getHTMLColor();
    }

    public void setFormat( NumberFormat format ) {
        _format = format;
        _htmlNode.setHTML( _format.format( _value ) );
    }
}
