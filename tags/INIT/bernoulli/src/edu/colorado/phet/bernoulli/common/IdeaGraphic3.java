package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * User: Sam Reid
 * Date: Jun 27, 2003
 * Time: 3:37:52 AM
 * Copyright (c) Jun 27, 2003 by Sam Reid
 */
public class IdeaGraphic3 implements InteractiveGraphic {
    boolean visible;
    int x;
    int y;
    String[] lines;
    private FontRenderContext frc;
    private Font font;
    private Color color;
    private BufferedImage image;
    private Color backgroundColor;
    private ImageObserver target;
    private Area rect;

    public IdeaGraphic3( boolean visible, int x, int y, String[] lines, FontRenderContext frc, Font font, Color color, BufferedImage image, Color backgroundColor, ImageObserver target ) {
        this.visible = visible;
        this.x = x;
        this.y = y;
        this.lines = lines;
        this.frc = frc;
        this.font = font;
        this.color = color;
        this.image = image;
        this.backgroundColor = backgroundColor;
        this.target = target;
        recomputeState();
    }

    public IdeaGraphic3( FontRenderContext frc, Font font ) {
        this.frc = frc;
        this.font = font;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public void setX( int x ) {
        this.x = x;
        recomputeState();
    }

    public void setY( int y ) {
        this.y = y;
        recomputeState();
    }

    public void setLines( String[] lines ) {
        this.lines = lines;
        recomputeState();
    }

    public void setFontRenderContext( FontRenderContext frc ) {
        this.frc = frc;
        recomputeState();
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        if( !visible || rect == null ) {
            return false;
        }
        return rect.contains( event.getPoint() );
    }

    public void mousePressed( MouseEvent event ) {
        this.visible = false;
    }

    public void mouseDragged( MouseEvent event ) {
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
    }

    public void mouseExited( MouseEvent event ) {
    }

    private void recomputeState() {
        if( frc == null ) {
            return;
        }
        Area a = new Area();
        int aty = y;
        Rectangle2D out = font.getMaxCharBounds( frc );
        aty -= out.getHeight();
        for( int i = 0; i < lines.length; i++ ) {
            String line = lines[i];
            Rectangle2D ga = font.getStringBounds( line, frc );

            ga = new Rectangle2D.Double( x - 10, aty - 10, ga.getWidth() + 20, ga.getHeight() + 20 );
            a.add( new Area( ga ) );
            aty += ga.getHeight() - 10;
        }
        this.rect = a;
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            g.setColor( Color.black );
            g.setStroke( new BasicStroke( 6 ) );
            g.draw( rect );
            g.setColor( backgroundColor );
            g.fill( rect );
            g.setColor( color );
            g.setFont( font );
            Rectangle2D out = font.getMaxCharBounds( frc );
            int aty = y;
            for( int i = 0; i < lines.length; i++ ) {
                g.drawString( lines[i], x, aty );
                aty += out.getHeight();
            }
            if( image != null ) {
                g.drawImage( image, x, y - image.getHeight() * 2, target );
            }

        }
    }

    public Point getImageCenter() {
        return new Point( x + image.getWidth(), y - image.getHeight() * 2 + image.getHeight() / 2 );
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
        recomputeState();
    }

    public FontRenderContext getFontRenderContext() {
        return frc;
    }
}


