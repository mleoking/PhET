/*, 2003.*/
package edu.colorado.phet.common_semiconductor.view.util;

import edu.colorado.phet.common_semiconductor.math.PhetVector;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * This adapter class for GeneralPath allows provides an interface in double coordinates.
 */
public class DoubleGeneralPath {
    GeneralPath path;

    public DoubleGeneralPath( PhetVector pt ) {
        this( pt.getX(), pt.getY() );
    }

    public DoubleGeneralPath( double x, double y ) {
        path = new GeneralPath();
        path.moveTo( (float)x, (float)y );
    }

    public void lineTo( double x, double y ) {
        path.lineTo( (float)x, (float)y );
    }

    public void lineToRelative( double dx, double dy ) {
        Point2D cur = path.getCurrentPoint();
        lineTo( cur.getX() + dx, cur.getY() + dy );
    }

    public GeneralPath getGeneralPath() {
        return path;
    }

    public void lineTo( PhetVector loc ) {
        lineTo( loc.getX(), loc.getY() );
    }
}
