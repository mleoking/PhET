// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PadBoundsNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for spinner arrows (the default up/down buttons)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SpinnerArrowNode extends PPath {

    public static enum SpinnerArrowOrientation {UP, DOWN}

    private static final PDimension BUTTON_SIZE = new PDimension( 26, 4 );

    public SpinnerArrowNode( final Color color, SpinnerArrowOrientation orientation ) {
        this( color, orientation, true );
    }

    public SpinnerArrowNode( final Color color, final SpinnerArrowOrientation orientation, boolean outlined ) {
        setPathTo( new DoubleGeneralPath() {{
            if ( orientation == SpinnerArrowOrientation.UP ) {
                moveTo( 0, BUTTON_SIZE.getHeight() );
                lineTo( BUTTON_SIZE.getWidth() / 2, 0 );
                lineTo( BUTTON_SIZE.getWidth(), BUTTON_SIZE.getHeight() );
            }
            else {
                moveTo( 0, 0 );
                lineTo( BUTTON_SIZE.getWidth() / 2, BUTTON_SIZE.getHeight() );
                lineTo( BUTTON_SIZE.getWidth(), 0 );
            }
            closePath();
        }}.getGeneralPath() );
        setPaint( color );
        setStroke( new BasicStroke( 0.25f ) );
        setStrokePaint( outlined ? Color.BLACK : color ); // stroke with the fill color, so outlined and non-outlined arrows are the same size
    }

    // WORKAROUND for #558
    @Override public Image toImage() {
        return new PadBoundsNode( this ).toImage();
    }
}
