/* Copyright 2007, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.PCanvas;


/**
 * DefaultWiggleMe encapsulates the default "look" of wiggle me's for PhET simulations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MyWiggleMe extends MotionHelpBalloon {

    // text properties
    private static final Font DEFAULT_FONT = new PhetFont( Font.BOLD, 20 );
    private static final Color TEXT_COLOR = Color.YELLOW;

    // bubble properties
    private static final Color BACKGROUND = new Color(0,0,0,0);
    private static final Color FOREGROUND = Color.WHITE;
    private static final Stroke STROKE = null;

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
    public MyWiggleMe( PCanvas canvas, String text ) {
        this( canvas, text, DEFAULT_FONT );
    }

    /**
     * Constructor.
     *
     * @param canvas
     * @param text
     * @param font
     */
    public MyWiggleMe( PCanvas canvas, String text, Font font ) {
        super( canvas, text );

        // text...
        setFont( font );
        setTextColor( TEXT_COLOR );

        // balloon...
        setBalloonVisible( true );
        setBalloonStroke( STROKE );

        // arrow...
        setArrowVisible( true );
        setArrowStroke( STROKE );
        setArrowHeadSize( ARROW_HEAD_SIZE.width, ARROW_HEAD_SIZE.height );
        setArrowTailWidth( ARROW_TAIL_WIDTH );
        setArrowLength( ARROW_LENGTH );
        setArrowTailPosition( ARROW_ATTACHMENT );

        //foreground & background...
        setForeground( FOREGROUND );
        setBackground( BACKGROUND );
    }

    public void setForeground( Paint foreground ) {
        setArrowStrokePaint( foreground );
        setBalloonStrokePaint( foreground );
    }

    public void setBackground( Paint background ) {
        setArrowFillPaint( background );
        setBalloonFillPaint( background );
    }
}
