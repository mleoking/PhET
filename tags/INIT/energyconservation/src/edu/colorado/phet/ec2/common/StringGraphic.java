/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common;

import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 5, 2003
 * Time: 2:26:43 AM
 * Copyright (c) Aug 5, 2003 by Sam Reid
 */
public class StringGraphic implements Graphic {
    String line;
    Color textColor;
    Color background;
    private Font font;
    private int x;
    private int y;
    private FontRenderContext frc;
    private Rectangle2D backgroundRect;

    public StringGraphic( String line, Color textColor, Color background, Font font, int x, int y ) {
        this.line = line;
        this.textColor = textColor;
        this.background = background;
        this.font = font;
        this.x = x;
        this.y = y;
    }

    public void setText( String text ) {
        this.line = text;
        if( frc != null ) {
            backgroundRect = font.getStringBounds( line, frc );
            backgroundRect = new Rectangle2D.Double( backgroundRect.getX() + x, backgroundRect.getY() + y, backgroundRect.getWidth(), backgroundRect.getHeight() );
        }
    }

    public void paint( Graphics2D graphics2D ) {
        if( frc == null ) {
            frc = graphics2D.getFontRenderContext();
            backgroundRect = font.getStringBounds( line, frc );
            backgroundRect = new Rectangle2D.Double( backgroundRect.getX() + x, backgroundRect.getY() + y, backgroundRect.getWidth(), backgroundRect.getHeight() );
        }

        graphics2D.setFont( font );
        graphics2D.setColor( background );
        graphics2D.fill( backgroundRect );

        graphics2D.setColor( textColor );
        graphics2D.drawString( line, x, y );
    }

    public int getHeight() {
        return (int)backgroundRect.getHeight();
    }

    public void setLocation( int x, int y ) {
        this.x = x;
        this.y = y;
        if( frc != null ) {
            backgroundRect = font.getStringBounds( line, frc );
            backgroundRect = new Rectangle2D.Double( backgroundRect.getX() + x, backgroundRect.getY() + y, backgroundRect.getWidth(), backgroundRect.getHeight() );
        }
    }

    public boolean isVisible() {
        return !line.equals( "" );
    }

}

