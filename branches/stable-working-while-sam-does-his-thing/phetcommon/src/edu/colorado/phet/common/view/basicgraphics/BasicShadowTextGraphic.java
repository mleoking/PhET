/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.basicgraphics;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * BasicShadowTextGraphic
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class BasicShadowTextGraphic extends BasicGraphic implements ITextGraphic {
    private BasicTextGraphic foreground;
    private BasicTextGraphic background;
    private int dx;
    private int dy;

    public BasicShadowTextGraphic( String text, Font font, FontMetrics fontMetrics, int x, int y, Color foregroundColor, int dx, int dy, Color backgroundColor ) {
        this.dx = dx;
        this.dy = dy;
        foreground = new BasicTextGraphic( text, font, foregroundColor, x, y, fontMetrics );
        background = new BasicTextGraphic( text, font, backgroundColor, x + dx, y + dy, fontMetrics );
    }

    public void setPosition( int x, int y ) {
        foreground.setPosition( x, y );
        background.setPosition( x + dx, y + dy );
        boundsChanged();
    }

    public void paint( Graphics2D g ) {
        background.paint( g );
        foreground.paint( g );
    }

    public void setText( String text ) {
        if( !text.equals( foreground.getText() ) ) {
            foreground.setText( text );
            background.setText( text );
            boundsChanged();
        }
    }

    public void setColor( Color color ) {
        this.foreground.setPaint( color );
        appearanceChanged();
    }

    public void setShadowColor( Color color ) {
        this.background.setPaint( color );
        appearanceChanged();
    }

    public Rectangle getBounds() {
        Rectangle2D b = foreground.getBounds().createUnion( background.getBounds() );
        return b.getBounds();
    }
}
