/** Sam Reid*/
package edu.colorado.phet.cck3.common.phetgraphics;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 29, 2004
 * Time: 10:54:12 PM
 * Copyright (c) Jun 29, 2004 by Sam Reid
 */
public class CompositePhetGraphic extends PhetGraphic {
    private ArrayList list = new ArrayList();

    public CompositePhetGraphic( Component component ) {
        super( component );
    }

    public void addGraphic( PhetGraphic graphic ) {
        list.add( graphic );
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
                if( bounds == null ) {
//                    int x=3;
//                    graphic.getBounds();
                }
                else {
                    rect = rect.union( bounds );
                }
            }
            return rect;
        }
    }

    private PhetGraphic graphicAt( int i ) {
        return (PhetGraphic)list.get( i );
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < list.size(); i++ ) {
            PhetGraphic graphic = (PhetGraphic)list.get( i );
            graphic.paint( g );
        }
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        for( int i = 0; i < list.size(); i++ ) {
            PhetGraphic graphic = (PhetGraphic)list.get( i );
            graphic.setVisible( visible );
        }
    }
}
