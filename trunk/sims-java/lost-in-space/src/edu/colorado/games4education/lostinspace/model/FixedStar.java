/**
 * Class: FixedStar
 * Class: edu.colorado.games4education.lostinspace.model
 * User: Ron LeMaster
 * Date: Mar 20, 2004
 * Time: 6:20:00 AM
 */
package edu.colorado.games4education.lostinspace.model;

import edu.colorado.games4education.lostinspace.Config;

import java.awt.*;
import java.awt.geom.Point2D;

public class FixedStar extends Star {

    public FixedStar( Color color, double luminance, double theta, double syntheticZBound ) {
        super( color, luminance, new Point2D.Double( Config.fixedStarDistance * Math.cos( theta ),
                                                     Config.fixedStarDistance * Math.sin( theta ) ),
               syntheticZBound );
    }
}
