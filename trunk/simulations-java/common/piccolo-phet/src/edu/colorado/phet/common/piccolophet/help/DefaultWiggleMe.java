/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.piccolophet.help;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.umd.cs.piccolo.PCanvas;


/**
 * DefaultWiggleMe encapsulates the default "look" of wiggle me's for PhET simulations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DefaultWiggleMe extends MotionHelpBalloon {

    // text properties
    private static final Font DEFAULT_FONT = new PhetDefaultFont( Font.BOLD, 20 );
    private static final Color TEXT_COLOR = Color.BLACK;

    // bubble properties
    private static final Color BACKGROUND = Color.RED;
    private static final Color FOREGROUND = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f );

    // arrow properties
    private static final Dimension ARROW_HEAD_SIZE = new Dimension( 20, 20 );
    private static final double ARROW_TAIL_WIDTH = 5;
    private static final double ARROW_LENGTH = 30;
    private static final Attachment ARROW_ATTACHMENT = MotionHelpBalloon.BOTTOM_CENTER;

    /**
     * Constructor that uses a default font.
     *
     * @param canvas
     * @param text
     */
    public DefaultWiggleMe( PCanvas canvas, String text ) {
        this( canvas, text, DEFAULT_FONT );
    }

    /**
     * Constructor.
     *
     * @param canvas
     * @param text
     * @param font
     */
    public DefaultWiggleMe( PCanvas canvas, String text, Font font ) {
        super( canvas, text );

        // text...
        setFont( font );
        setTextColor( TEXT_COLOR );

        // balloon...
        setBalloonVisible( true );
        setBalloonFillPaint( BACKGROUND );
        setBalloonStrokePaint( FOREGROUND );
        setBalloonStroke( STROKE );

        // arrow...
        setArrowVisible( true );
        setArrowFillPaint( BACKGROUND );
        setArrowStrokePaint( FOREGROUND );
        setArrowStroke( STROKE );
        setArrowHeadSize( ARROW_HEAD_SIZE.width, ARROW_HEAD_SIZE.height );
        setArrowTailWidth( ARROW_TAIL_WIDTH );
        setArrowLength( ARROW_LENGTH );
        setArrowTailPosition( ARROW_ATTACHMENT );
    }

}
