/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.help;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.umd.cs.piccolo.PCanvas;

/**
 * HAWiggleMe encapsulates the "look" of wiggle me's for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RSWiggleMe extends MotionHelpBalloon {

    private static final Font FONT =  new Font( RSConstants.DEFAULT_FONT_NAME, RSConstants.DEFAULT_FONT_STYLE, 20 );
    private static final Color BACKGROUND = Color.RED;
    private static final Color FOREGROUND = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f ); 
    
    /**
     * Constructor.
     * 
     * @param canvas
     * @param text
     */
    public RSWiggleMe( PCanvas canvas, String text ) {
        super( canvas, text );
        
        // text...
        setFont( FONT );
        setTextColor( FOREGROUND );
        
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
        setArrowHeadSize( 20, 20 );
        setArrowTailWidth( 5 );
        setArrowLength( 30 );
    }

}
