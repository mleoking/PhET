/** Sam Reid*/
package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;
import java.util.ArrayList;

/**
 * A list of PhetGraphics, painted in order.
 */
public class CompositePhetGraphic extends PhetGraphic {
    private ArrayList list = new ArrayList();
    private Point location = new Point();

    public CompositePhetGraphic( Component component ) {
        super( component );
    }

    public void addGraphic( PhetGraphic graphic ) {
        list.add( graphic );
        graphic.setParent( this );
    }

    public void setLocation( Point p ) {
        this.setLocation( p.x, p.y );
    }

    public void setLocation( int x, int y ) {
        int dx = x - location.x;
        int dy = y - location.y;
        location.setLocation( x, y );
        for( int i = 0; i < list.size(); i++ ) {
            PhetGraphic graphic = (PhetGraphic)list.get( i );
            graphic.setLocation( graphic.getLocation().x + dx, graphic.getLocation().y + dy );
        }
    }

    public Point getLocation() {
        return location;
    }

    public void repaint() {
        super.repaint();
        for( int i = 0; i < list.size(); i++ ) {
            PhetGraphic graphic = (PhetGraphic)list.get( i );
            graphic.repaint();
        }
    }

    public boolean contains( int x, int y ) {
        if( isVisible() ) {
            for( int i = 0; i < list.size(); i++ ) {
                PhetGraphic graphic = (PhetGraphic)list.get( i );
                if( graphic.contains( x, y ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Rectangle determineBounds() {
        if( list.size() == 0 ) {
            return null;
        }
        else {
            Rectangle rect = graphicAt( 0 ).getBounds();
            for( int i = 1; i < list.size(); i++ ) {
                PhetGraphic graphic = (PhetGraphic)list.get( i );
                Rectangle bounds = graphic.getBounds();
                if( bounds != null ) {
                    rect = rect.union( bounds );
                }
            }
            return rect;
        }
    }

    public PhetGraphic graphicAt( int i ) {
        return (PhetGraphic)list.get( i );
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < list.size(); i++ ) {
            PhetGraphic graphic = (PhetGraphic)list.get( i );
            graphic.paint( g );
        }
    }

    public int numGraphics() {
        return list.size();
    }

    public void removeGraphic( PhetGraphic graphic ) {
        list.remove( graphic );
        graphic.setParent( null );
    }

    protected void forceRepaint() {
        for( int i = 0; i < list.size(); i++ ) {
            PhetGraphic graphic = (PhetGraphic)list.get( i );
            graphic.forceRepaint();
        }
    }

}
