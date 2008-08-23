/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * RectangleUtils
 *
 * @author ?
 * @version $Revision$
 */
public class RectangleUtils {
    public static Rectangle2D expandRectangle2D( Rectangle2D r, double dx, double dy ) {
        return new Rectangle2D.Double( r.getX() - dx, r.getY() - dy, r.getWidth() + dx * 2, r.getHeight() + dy * 2 );
    }

    public static Rectangle expand( Rectangle r, int dx, int dy ) {
        return new Rectangle( r.x - dx, r.y - dy, r.width + dx * 2, r.height + dy * 2 );
    }

    public static Rectangle toRectangle( Rectangle2D b ) {
        if ( b == null ) {
            return null;
        }
        if ( b instanceof Rectangle ) {
            return (Rectangle) b;
        }
        else {
            return new Rectangle( (int) b.getX(), (int) b.getY(), (int) b.getWidth(), (int) b.getHeight() );
        }
    }

    public static Point getLeftCenter( Rectangle bounds ) {
        Point pt = bounds.getLocation();
        pt.translate( 0, bounds.height / 2 );
        return pt;
    }

    public static Point getTopCenter( Rectangle bounds ) {
        Point pt = bounds.getLocation();
        pt.translate( bounds.width / 2, 0 );
        return pt;
    }

    public static Point getBottomCenter( Rectangle bounds ) {
        Point pt = bounds.getLocation();
        pt.translate( bounds.width / 2, bounds.height );
        return pt;
    }

    public static Point getRightCenter( Rectangle bounds ) {
        Point pt = bounds.getLocation();
        pt.translate( bounds.width, bounds.height / 2 );
        return pt;
    }

    public static Point getCenter( Rectangle rect ) {
        return new Point( rect.x + rect.width / 2, rect.y + rect.height / 2 );
    }

    public static Point2D.Double getCenter2D( Rectangle2D rect ) {
        return new Point2D.Double( rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2 );
    }

    public static Rectangle2D expand( Rectangle2D r, double dw, double dh ) {
        return new Rectangle2D.Double( r.getX() - dw, r.getY() - dh, r.getWidth() + dw * 2, r.getHeight() + dh * 2 );
    }

    public static Rectangle union( Rectangle[] rectangles ) {
        if ( rectangles.length == 0 ) {
            return null;
        }
        Rectangle union = null;//new Rectangle( rectangles[0] );
        for ( int i = 0; i < rectangles.length; i++ ) {
            Rectangle rectangle = rectangles[i];
            if ( rectangle != null ) {
                if ( union == null ) {
                    union = new Rectangle( rectangle );
                }
                else {
                    union = union.union( rectangle );
                }
            }

        }
        return union;
    }

    public static boolean areEqual( Rectangle a, Rectangle b ) {
        if ( a == null && b == null ) {
            return true;
        }
        else if ( a != null && b == null ) {
            return false;
        }
        else if ( a == null && b != null ) {
            return false;
        }
        else {
            return a.equals( b );
        }
    }

    public static Rectangle union( ArrayList rectangles ) {
        if ( rectangles.size() == 0 ) {
            return null;
        }
        Rectangle union = (Rectangle) rectangles.remove( 0 );
        while ( rectangles.size() > 0 ) {
            union = union.union( (Rectangle) rectangles.remove( 0 ) );
        }
        return union;
    }

    public static Rectangle2D union( Rectangle2D[] rectangles ) {
        if ( rectangles.length == 0 ) {
            return null;
        }
        Rectangle2D.Double union = null;//new Rectangle( rectangles[0] );
        for ( int i = 0; i < rectangles.length; i++ ) {
            Rectangle2D rectangle = rectangles[i];
            if ( rectangle != null ) {
                if ( union == null ) {
                    union = new Rectangle2D.Double( rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight() );
                }
                else {
                    Rectangle2D.union( union, rectangle, union );
//                    union = union.union( rectangle.getX(),rectangle.getY(),rectangle.getWidth(),rectangle.getHeight() );
                }
            }

        }
        return union;
    }

}
