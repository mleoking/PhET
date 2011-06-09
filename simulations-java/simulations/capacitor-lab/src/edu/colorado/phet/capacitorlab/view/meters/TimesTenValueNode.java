// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays a value of the form "4.01x10^3".
 * Origin is at upper-left corner of bounding rectangle.
 * </p>
 * Note that this node was originally an extension of HTMLNode, since HTML is a natural for doing superscripts.
 * But HTML rendering of superscripts was so poor on Windows (see #2938) that I decided to implement my own superscripts using PText.
 * The one drawback with this approach is that we can't align baselines of the number and units.
 * Since all of our units are uppercase letters, that's no a problem in Capacitor Lab.
 * But it prevents this from being a good general solution that could be migrated to phetcommon.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TimesTenValueNode extends PComposite {

    private final NumberFormat mantissaFormat;
    private int exponent;
    private final String units;
    private double value;
    private final PText baseNode, exponentNode, unitsNode;

    public TimesTenValueNode( NumberFormat mantissaFormat, int exponent, String units, double value, final PhetFont font, final Color color ) {

        this.mantissaFormat = mantissaFormat;
        this.exponent = exponent;
        this.units = units;
        this.value = value;

        baseNode = new PText() {{
            setFont( font );
            setTextPaint( color );
        }};
        addChild( baseNode );

        exponentNode = new PText() {{
            setFont( new PhetFont( font.getStyle(), (int) ( 0.9 * font.getSize() ) ) );
            setTextPaint( color );
        }};
        addChild( exponentNode );

        unitsNode = new PText( units ) {{
            setFont( font );
            setTextPaint( color );
        }};
        addChild( unitsNode );

        update();
    }

    public void setValue( double value ) {
        if ( value != this.value ) {
            this.value = value;
            update();
        }
    }

    public void setExponent( int maxExponent ) {
        if ( maxExponent != this.exponent ) {
            this.exponent = maxExponent;
            update();
        }
    }

    /*
     * Zero is displayed as "0 units".
     * Non-zero is displayed as "4.01x10^13 units" (for example).
     */
    private void update() {
        final int unitsXSpacing = 3;
        if ( value != 0 ) {
            // update text
            double mantissa = value / Math.pow( 10, exponent );
            baseNode.setText( MessageFormat.format( "{0}x10", mantissaFormat.format( mantissa ) ) );
            exponentNode.setText( String.valueOf( exponent ) );
            // adjust layout
            final double exponentOffset = 0.4 * exponentNode.getFullBoundsReference().getHeight();
            baseNode.setOffset( 0, exponentOffset );
            exponentNode.setOffset( baseNode.getFullBoundsReference().getMaxX(),
                                    baseNode.getFullBoundsReference().getMinY() - exponentOffset );
            unitsNode.setOffset( exponentNode.getFullBoundsReference().getMaxX() + unitsXSpacing,
                                 baseNode.getFullBoundsReference().getMaxY() - unitsNode.getFullBoundsReference().getHeight() );
        }
        else {
            baseNode.setText( "0" );
            exponentNode.setText( "" );
            // adjust layout
            baseNode.setOffset( 0, 0 );
            exponentNode.setOffset( 0, 0 );
            unitsNode.setOffset( baseNode.getFullBoundsReference().getMaxX() + unitsXSpacing,
                                 baseNode.getFullBoundsReference().getMaxY() - unitsNode.getFullBoundsReference().getHeight() );
        }
    }
}
