/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.common.view.util.GraphicsState;

import java.awt.*;

/**
 * GraduatedWallGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GraduatedWallGraphic extends WallGraphic {
    private int minorTickInterval = 5;
    private int majorTickInterval = 25;
    private Color backgroundColor = new Color( 230, 230, 40 );
    private Stroke minorTickStroke = new BasicStroke( 1f );
    private Stroke majorTickStroke = new BasicStroke( 1f );

    public GraduatedWallGraphic( Wall wall, Component component, Paint fill, Paint borderPaint, int translationDirection ) {
        super( wall, component, fill, borderPaint, translationDirection );
    }

    public GraduatedWallGraphic( Wall wall, Component component, Paint fill, int translationDirection ) {
        super( wall, component, fill, translationDirection );
    }

    /**
     * Draws tick marks on the wall for measurement purposes
     * @param g2
     */
    public void paint( Graphics2D g2 ) {
        Rectangle bounds = getBounds();

        GraphicsState gs = new GraphicsState( g2 );
        super.paint( g2 );
        g2.setColor( backgroundColor );
        g2.fill( bounds );
        g2.setColor( Color.black );
        g2.draw( bounds );

        // Draw minor ticks
        for( int i = 0; i < bounds.getHeight(); i += minorTickInterval ) {
            g2.setStroke( minorTickStroke );
            g2.drawLine( (int)bounds.getMinX(), (int)bounds.getMaxY() - i,
                         (int)bounds.getMinX() + 3, (int)bounds.getMaxY() - i );
            g2.drawLine( (int)bounds.getMaxX(), (int)bounds.getMaxY() - i,
                         (int)bounds.getMaxX() - 3, (int)bounds.getMaxY() - i );
        }

        // Draw major ticks
        for( int i = 0; i < bounds.getHeight(); i += majorTickInterval ) {
            g2.setStroke( majorTickStroke );
            g2.drawLine( (int)bounds.getMinX(), (int)bounds.getMaxY() - i,
                         (int)bounds.getMinX() + 5, (int)bounds.getMaxY() - i );
            g2.drawLine( (int)bounds.getMaxX(), (int)bounds.getMaxY() - i,
                         (int)bounds.getMaxX() - 5, (int)bounds.getMaxY() - i );
        }

        gs.restoreGraphics();
    }
}
