// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Convenience class for creating a PText with the specified text and font
 *
 * @author Sam Reid
 */
public class PhetPText extends PText {
    public PhetPText( String text ) {
        super( text );
    }

    //Create a PhetPText with the specified text and font.
    public PhetPText( String text, Font font ) {

        //Set the font first so that it doesn't waste time computing the layout twice (once for the default font and once for the specified font)
        setFont( font );
        setText( text );
    }

    public PhetPText( Font font ) {
        setFont( font );
    }

    public PhetPText( String text, Font font, Paint paint ) {

        //Set the font first so that it doesn't waste time computing the layout twice (once for the default font and once for the specified font)
        setFont( font );
        setText( text );
        setTextPaint( paint );
    }

    //The following copied from PhetPNode, see comments there.  Copied because PhetPText extends PText instead of wrapping it.
    public double getFullWidth() {
        return getFullBoundsReference().getWidth();
    }

    public double getFullHeight() {
        return getFullBoundsReference().getHeight();
    }

    public double getMaxX() {
        return getFullBoundsReference().getMaxX();
    }

    public double getMaxY() {
        return getFullBoundsReference().getMaxY();
    }

    //Note the mismatch between getMinX and getX
    public double getMinX() {
        return getFullBoundsReference().getMinX();
    }

    //Note the mismatch between getMinY and getY
    public double getMinY() {
        return getFullBoundsReference().getMinY();
    }

    public double getCenterY() {
        return getFullBoundsReference().getCenterY();
    }

    public double getCenterX() {
        return getFullBoundsReference().getCenterX();
    }

    public void centerFullBoundsOnPoint( Point2D point ) {
        super.centerFullBoundsOnPoint( point.getX(), point.getY() );
    }

    public void centerFullBoundsOnPoint( ImmutableVector2D point ) {
        super.centerFullBoundsOnPoint( point.getX(), point.getY() );
    }
}