/**
 * Class: ReflectivityAssessor
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 21, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.geom.Point2D;

public interface ReflectivityAssessor {
    double getReflectivity( Photon photon );
}
