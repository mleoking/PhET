/**
 * Class: Mirror
 * Package: edu.colorado.phet.lasers.physics.mirror
 * Author: Another Guy
 * Date: Apr 3, 2003
 */
package edu.colorado.phet.lasers.physics.mirror;

import edu.colorado.phet.lasers.physics.photon.Photon;
import edu.colorado.phet.collision.Wall;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Mirror extends Wall {
    // Implementation of specific reflection behaviors
    protected ArrayList reflectionStrategies = new ArrayList();

    public Mirror( Point2D end1, Point2D end2 ) {
        super( new Rectangle2D.Double( end1.getX(), end1.getY(),
                                       end2.getX() - end1.getY(),
                                       end2.getY() - end1.getY() ));
    }

    public boolean reflects( Photon photon ) {
        boolean result = true;
        for( int i = 0; i < reflectionStrategies.size() && result == true; i++ ) {
            ReflectionStrategy reflectionStrategy = (ReflectionStrategy)reflectionStrategies.get( i );
            result &= reflectionStrategy.reflects( photon );
        }
        return result;
    }

    public boolean isLeftReflecting() {
        boolean result = false;
        for( int i = 0;
             result == false && i < reflectionStrategies.size();
             i++ ) {
            ReflectionStrategy reflectionStrategy = (ReflectionStrategy)reflectionStrategies.get( i );
            if( reflectionStrategy instanceof LeftReflecting ) {
                result = true;
            }
        }
        return result;
    }
}
