package edu.colorado.phet.coreadditions.math.transforms;

import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 25, 2002
 * Time: 10:24:12 PM
 * To change this template use Options | File Templates.
 */
public interface Transform {
    public Point2D.Double transform(Point2D.Double in);
}
