/*, 2003.*/
package edu.colorado.phet.semiconductor.phetcommon.view.util;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;


/**
 * This adapter class for GeneralPath allows provides an interface in double coordinates.
 */
public class DoubleGeneralPath {
    GeneralPath path;

    public DoubleGeneralPath( Vector2D.Double pt ) {
        this( pt.getX(), pt.getY() );
    }

    public DoubleGeneralPath( double x, double y ) {
        path = new GeneralPath();
        path.moveTo( (float) x, (float) y );
    }

    public void lineTo( double x, double y ) {
        path.lineTo( (float) x, (float) y );
    }

    public void lineToRelative( double dx, double dy ) {
        Point2D cur = path.getCurrentPoint();
        lineTo( cur.getX() + dx, cur.getY() + dy );
    }

    public GeneralPath getGeneralPath() {
        return path;
    }

    public void lineTo( AbstractVector2D loc ) {
        lineTo( loc.getX(), loc.getY() );
    }
}
