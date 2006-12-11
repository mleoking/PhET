/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.help;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.PCanvas;

/**
 * BSWiggleMe encapsulates the "look" of wiggle mes for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSWiggleMe extends MotionHelpBalloon {

    private static final Font FONT =  new Font( BSConstants.FONT_NAME, Font.PLAIN, 20 );
    private static final Color BACKGROUND = Color.RED;
    private static final Color FOREGROUND = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f ); 
    
    /**
     * Constructor.
     * 
     * @param canvas
     * @param text
     */
    public BSWiggleMe( PCanvas canvas, String text ) {
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
        setArrowHeadSize( 10, 10 );
        setArrowTailWidth( 3 );
        setArrowLength( 20 );
        setArrowTailPosition( MotionHelpBalloon.BOTTOM_CENTER );
    }

}
