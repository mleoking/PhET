/**
 * Class: ControlCurve Package: edu.colorado.phet.emf.view.graphics Author: Tim
 * Lambert See: http://www.cse.unsw.edu.au/~lambert/ See:
 * http://www.cse.unsw.edu.au/~lambert/splines Tim Lambert Office: EE344 School
 * of Computer Science and Engineering Email: lambert@cse.unsw.edu.au The
 * University of New South Wales Phone: +61 2 9385 6496 Sydney 2052, AUSTRALIA
 * Fax: +61 2 9385 5995
 * 
 * Adapted By: Ron LeMaster Date: Jun 2, 2003
 */

package edu.colorado.phet.radiowaves.view.graphics.splines;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * This class represents a curve defined by a sequence of control points
 */
public class ControlCurve {

    protected Polygon pts;
    protected int selection = -1;

    public ControlCurve() {
        pts = new Polygon();
    }

    static Font f = new PhetFont( 12 );

    /**
     * Clears the points so we can reuse the curve.
     * Added by RJL, 6/4/03
     */
    public void reset() {
        pts.reset();
    }

    /**
     * paint this curve into g.
     */
    public void paint( Graphics g ) {
        FontMetrics fm = g.getFontMetrics( f );
        g.setFont( f );
        int h = fm.getAscent() / 2;

        for ( int i = 0; i < pts.npoints; i++ ) {
            String s = Integer.toString( i );
            int w = fm.stringWidth( s ) / 2;
            g.drawString( Integer.toString( i ), pts.xpoints[i] - w, pts.ypoints[i] + h );
        }
    }

    static final int EPSILON = 36; /* square of distance for picking */

    /**
     * return index of control point near to (x,y) or -1 if nothing near
     */
    public int selectPoint( int x, int y ) {
        int mind = Integer.MAX_VALUE;
        selection = -1;
        for ( int i = 0; i < pts.npoints; i++ ) {
            int d = sqr( pts.xpoints[i] - x ) + sqr( pts.ypoints[i] - y );
            if ( d < mind && d < EPSILON ) {
                mind = d;
                selection = i;
            }
        }
        return selection;
    }

    // square of an int
    static int sqr( int x ) {
        return x * x;
    }

    /**
     * add a control point, return index of new control point
     */
    public int addPoint( int x, int y ) {
        pts.addPoint( x, y );
        return selection = pts.npoints - 1;
    }

    /**
     * set selected control point
     */
    public void setPoint( int x, int y ) {
        if ( selection >= 0 ) {
            pts.xpoints[selection] = x;
            pts.ypoints[selection] = y;
        }
    }

    /**
     * remove selected control point
     */
    public void removePoint() {
        if ( selection >= 0 ) {
            pts.npoints--;
            for ( int i = selection; i < pts.npoints; i++ ) {
                pts.xpoints[i] = pts.xpoints[i + 1];
                pts.ypoints[i] = pts.ypoints[i + 1];
            }
        }
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        for ( int i = 0; i < pts.npoints; i++ ) {
            result.append( " " + pts.xpoints[i] + " " + pts.ypoints[i] );
        }
        return result.toString();
    }
}
