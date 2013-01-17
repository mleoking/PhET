// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * An integer value displayed on a background with rounded corners.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NumberBackgroundNode extends PComposite {

    // Use this constructor if you want the background sized to the value.
    public NumberBackgroundNode( double value, NumberFormat format,
                                 PhetFont font, Color textColor, Color backgroundColor,
                                 double xMargin, double yMargin, double cornerRadius ) {
        this( value, format, font, textColor, backgroundColor, xMargin, yMargin, cornerRadius, 0 );
    }

    /**
     * Use this constructor if you want to specify a minimum width for the value.
     *
     * @param value
     * @param format
     * @param font
     * @param textColor
     * @param backgroundColor
     * @param xMargin
     * @param yMargin
     * @param cornerRadius
     * @param minWidth        the minimum width of the value (xMargin is added to this to determine background width)
     */
    public NumberBackgroundNode( double value, NumberFormat format,
                                 PhetFont font, Color textColor, Color backgroundColor,
                                 double xMargin, double yMargin, double cornerRadius, double minWidth ) {

        PNode textNode = new PhetPText( format.format( value ), font, textColor );

        // background for the label
        final double textWidth = textNode.getFullBoundsReference().getWidth();
        final double backgroundWidth = ( minWidth == 0 ) ? textWidth : Math.max( minWidth, textWidth );
        final RoundRectangle2D.Double backgroundShape = new RoundRectangle2D.Double( 0, 0,
                                                                                     backgroundWidth + ( 2 * xMargin ),
                                                                                     textNode.getFullBoundsReference().getHeight() + ( 2 * yMargin ),
                                                                                     cornerRadius, cornerRadius );
        PPath backgroundNode = new PPath( backgroundShape );
        backgroundNode.setPaint( backgroundColor );
        backgroundNode.setStroke( null );

        // rendering order
        addChild( backgroundNode );
        addChild( textNode );

        // layout
        textNode.setOffset( backgroundNode.getFullBoundsReference().getCenterX() - ( textNode.getFullBoundsReference().getWidth() / 2 ), yMargin );
    }
}
