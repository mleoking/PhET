/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.FormattedNumberNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays a value as a formatted number on a background.
 * If that number drops below some threshold, then "negligible" is displayed.
 */
public class NegligibleValueNode extends PhetPNode {
    
    private static final Font VALUE_FONT = new PhetFont( Font.PLAIN, 18 );
    private static final Color VALUE_COLOR = Color.BLACK;
    private static final Font NEGLIGIBLE_FONT = new PhetFont( Font.BOLD, 20 );
    private static final Color NEGLIGIBLE_COLOR = Color.BLACK;

    private final FormattedNumberNode numberNode;
    private final PText negligibleNode;
    private boolean negligibleEnabled;
    private double negligibleThreshold;
    
    public NegligibleValueNode( double value, NumberFormat format, double negligibleThreshold ) {
        this( value, format );
        // not interactive
        setPickable( false );
        setChildrenPickable( false );
        this.negligibleThreshold = negligibleThreshold;
        negligibleEnabled = true;
    }
    
    /**
     * Constructor that does not provide a threshold for "negligible",
     * so that feature is off by default.
     * @param value
     * @param format
     */
    public NegligibleValueNode( double value, NumberFormat format ) {
        // displays the value
        numberNode = new FormattedNumberNode( format, value, VALUE_FONT, VALUE_COLOR );
        addChild( numberNode );
        // displays "NEGLIGIBLE"
        negligibleNode = new PText( ABSStrings.NEGLIGIBLE );
        negligibleNode.setFont( NEGLIGIBLE_FONT );
        negligibleNode.setTextPaint( NEGLIGIBLE_COLOR );
        addChild( negligibleNode );
        // negligible mode is off by default
        negligibleEnabled = false;
        negligibleThreshold = 0;
    }
    
    public void setFormat( NumberFormat format ) {
        numberNode.setFormat( format );
    }
    
    public void setNegligibleColor( Color negligibleColor ) {
        negligibleNode.setTextPaint( negligibleColor );
    }

    public double getValue() {
        return numberNode.getValue();
    }
    
    public void setValue( double value ) {
        numberNode.setValue( value );
        update();
    }

    public void setNegligibleEnabled( boolean enabled, double threshold ) {
        if ( enabled != negligibleEnabled || threshold != negligibleThreshold ) {
            negligibleEnabled = enabled;
            negligibleThreshold = threshold;
            update();
        }
    }

    /*
     * Use addChild/removeChild instead of setVisible so that fullBounds will be correct.
     * Piccolo includes all children (visible and invisible) in its fullBounds calculations.
     */
    private void update() {
        final double value = numberNode.getValue();
        if ( value <= negligibleThreshold && negligibleEnabled ) {
            addChild( negligibleNode );
            removeChild( numberNode );
        }
        else {
            addChild( numberNode );
            removeChild( negligibleNode );
        }
    }
}
