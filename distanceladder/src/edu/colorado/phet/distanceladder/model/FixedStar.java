/**
 * Class: FixedStar
 * Class: edu.colorado.phet.distanceladder.model
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 6:20:00 AM
 */
package edu.colorado.phet.distanceladder.model;

import edu.colorado.phet.distanceladder.Config;
import edu.colorado.phet.distanceladder.Config;

import java.awt.*;
import java.awt.geom.Point2D;

public class FixedStar extends Star {

    public FixedStar( Color color, double luminance, double theta, double syntheticZBound ) {
        super( color, luminance, new Point2D.Double( Config.universeWidth * 0.5 * Math.cos( theta ),
                                                     Config.universeWidth * 0.5 * Math.sin( theta ) ),
               syntheticZBound );
    }
}
