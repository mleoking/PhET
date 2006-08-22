package edu.colorado.phet.cck3;

import edu.colorado.phet.common_cck.view.ApparatusPanel;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:53:53 AM
 * Copyright (c) Jul 11, 2006 by Sam Reid
 */
public class CCKApparatusPanel extends ApparatusPanel {
    private ArrayList rectangles = new ArrayList();

    public CCKApparatusPanel() {
    }

    public void repaint( long tm, int x, int y, int width, int height ) {
        repaint( x, y, width, height );
    }

    public void repaint( Rectangle r ) {
        repaint( r.x, r.y, r.width, r.height );
    }

    public void repaint() {
        repaint( 0, 0, getWidth(), getHeight() );
    }

    public void superRepaint() {
        super.repaint();
    }

    public void repaint( int x, int y, int width, int height ) {
        if( rectangles != null ) {
            Rectangle r = new Rectangle( x, y, width, height );
            rectangles.add( r );
        }
    }

    public void repaint( long tm ) {
        repaint();
    }

    public void synchronizeImmediately() {
        Rectangle rect = union();
        if( rect != null ) {
            paintImmediately( rect );
        }
        rectangles.clear();
    }

    private Rectangle union() {
        if( rectangles.size() == 0 ) {
            return new Rectangle();
        }
        else {
            Rectangle union = (Rectangle)rectangles.get( 0 );
            for( int i = 1; i < rectangles.size(); i++ ) {
                Rectangle rectangle = (Rectangle)rectangles.get( i );
                union = union.union( rectangle );
            }
            return union;
        }
    }
}
