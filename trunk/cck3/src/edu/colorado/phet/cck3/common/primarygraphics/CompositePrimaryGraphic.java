/** Sam Reid*/
package edu.colorado.phet.cck3.common.primarygraphics;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 29, 2004
 * Time: 10:54:12 PM
 * Copyright (c) Jun 29, 2004 by Sam Reid
 */
public class CompositePrimaryGraphic extends PrimaryGraphic {
    private ArrayList list = new ArrayList();

    public CompositePrimaryGraphic( Component component ) {
        super( component );
    }

    public void addGraphic( PrimaryGraphic graphic ) {
        list.add( graphic );
    }

    public void repaint() {
        super.repaint();
        for( int i = 0; i < list.size(); i++ ) {
            PrimaryGraphic graphic = (PrimaryGraphic)list.get( i );
            graphic.repaint();
        }
    }

    public boolean contains( int x, int y ) {
        if( isVisible() ) {
            for( int i = 0; i < list.size(); i++ ) {
                PrimaryGraphic graphic = (PrimaryGraphic)list.get( i );
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
                PrimaryGraphic graphic = (PrimaryGraphic)list.get( i );
                rect = rect.union( graphic.getBounds() );
            }
            return rect;
        }
    }

    private PrimaryGraphic graphicAt( int i ) {
        return (PrimaryGraphic)list.get( i );
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < list.size(); i++ ) {
            PrimaryGraphic graphic = (PrimaryGraphic)list.get( i );
            graphic.paint( g );
        }
    }

}
