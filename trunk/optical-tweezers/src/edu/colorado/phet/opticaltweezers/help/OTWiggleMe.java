/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.help;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.PCanvas;

/**
 * OTWiggleMe encapsulates the "look" of wiggle mes for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OTWiggleMe extends MotionHelpBalloon {

    private static final Font FONT =  new Font( OTConstants.DEFAULT_FONT_NAME, OTConstants.DEFAULT_FONT_STYLE, 20 );
    private static final Color BACKGROUND = Color.RED;
    private static final Color FOREGROUND = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1f ); 
    
    /**
     * Constructor.
     * 
     * @param canvas
     * @param text
     */
    public OTWiggleMe( PCanvas canvas, String text ) {
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
        setArrowTailPosition( MotionHelpBalloon.BOTTOM_CENTER );
    }

}
