/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.cck3.common.primarygraphics.PrimaryGraphic;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.help.HelpItem;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 12:04:51 AM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class PositionedHelpItem extends PrimaryGraphic {
    String text;
    Target target;
    Font font;
    private MultiLineComponentTextGraphic textGraphic;
    private Arrow arrow;
    private Color arrowColor = Color.blue;
    private boolean noTarget = false;

    public PositionedHelpItem( String text, Target target, Font font, Component component ) {
        super( component );
        this.text = text;
        this.target = target;
        this.font = font;
        String[] lines = HelpItem.tokenizeString( text );
        Point location = target.getTextLocation();
        if( location == null ) {
            location = new Point();
            setVisible( false );
        }
        int x = location.x;
        int y = location.y;
        textGraphic = new MultiLineComponentTextGraphic( component, lines, font, x, y, Color.blue, 1, 1, Color.yellow );
        target.addObserver( new SimpleObserver() {
            public void update() {
                changed();
            }
        } );
    }

    public void changed() {
        Point location = target.getTextLocation();
        if( location == null ) {
            noTarget = true;
        }
        else {
            noTarget = false;
        }
//        System.out.println( "PHI.location = " + location );
        if( location != null ) {
            int x = location.x;
            int y = location.y;
            textGraphic.setPosition( x, y );
            arrow = target.getArrow( textGraphic );
        }
        if( isVisible() && !noTarget ) {
            super.setBoundsDirty();
            super.repaint();
        }

    }

    public void paint( Graphics2D g ) {
        if( isVisible() && !noTarget ) {
            textGraphic.paint( g );
            if( arrow != null ) {
                g.setColor( arrowColor );
                g.fill( arrow.getShape() );
            }
        }
    }

    public boolean contains( int x, int y ) {
        if( isVisible() ) {
            return textGraphic.contains( x, y );
        }
        return false;
    }

    protected Rectangle determineBounds() {
        if( isVisible() ) {
            return getBounds();
        }
        else {
            return null;
        }
    }

    public Rectangle getBounds() {
        if( isVisible() ) {
            Rectangle a = textGraphic.getBounds();
            if( arrow != null ) {
                return RectangleUtils.toRectangle( a.createUnion( arrow.getShape().getBounds2D() ) );
            }
            else {
                return a;
            }
        }
        else {
            return null;
        }
    }

    public Rectangle2D getTextBounds() {
        return textGraphic.getBounds();
    }

    public abstract static class Target extends SimpleObservable {
        public abstract Point getTextLocation();

        public abstract Arrow getArrow( MultiLineComponentTextGraphic textGraphic );
    }
}
