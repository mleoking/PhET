/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.fastpaint.FastPaint;
import edu.colorado.phet.common.view.graphics.BoundedGraphic;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.help.HelpItem;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 12:04:51 AM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class PositionedHelpItem implements BoundedGraphic {
    String text;
    Target movingObject;
    Font font;
    private MultiLineComponentTextGraphic textGraphic;
    private boolean visible = true;
    private Arrow arrow;
    private Color arrowColor = Color.blue;
    private Component parent;

    public PositionedHelpItem( String text, Target movingObject, Font font, Component component ) {
        this.parent = component;
        this.text = text;
        this.movingObject = movingObject;
        this.font = font;
        String[] lines = HelpItem.tokenizeString( text );
        Point location = movingObject.getTextLocation();
        if( location == null ) {
            location = new Point();
            visible = false;
        }
        int x = location.x;
        int y = location.y;
        textGraphic = new MultiLineComponentTextGraphic( component, lines, font, x, y, Color.blue, 1, 1, Color.yellow );
        movingObject.addObserver( new SimpleObserver() {
            public void update() {
                changed();
            }
        } );
    }

    public void changed() {
        Point location = movingObject.getTextLocation();
        if( location == null ) {
            visible = false;
            return;
        }
        else {
            Rectangle rect = getBounds();
            visible = true;
            int x = location.x;
            int y = location.y;
            textGraphic.setPosition( x, y );
            arrow = movingObject.getArrow( textGraphic );
            Rectangle newBounds = getBounds();
            FastPaint.fastRepaint( parent, rect, newBounds );
        }
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            textGraphic.paint( g );
            if( arrow != null ) {
                g.setColor( arrowColor );
                g.fill( arrow.getShape() );
            }
        }
    }

    public boolean contains( int x, int y ) {
        if( visible ) {
            return textGraphic.contains( x, y );
        }
        return false;
    }

    public Rectangle getBounds() {
        Rectangle a = textGraphic.getBounds();
        if( arrow != null ) {
            return RectangleUtils.toRectangle( a.createUnion( arrow.getShape().getBounds2D() ) );
        }
        else {
            return a;
        }

    }

    public abstract static class Target extends SimpleObservable {
        public abstract Point getTextLocation();

        public abstract Arrow getArrow( MultiLineComponentTextGraphic textGraphic );
    }
}
