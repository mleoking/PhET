/**
 * Class: Mirror
 * Package: edu.colorado.phet.lasers.physics.mirror
 * Author: Another Guy
 * Date: Apr 3, 2003
 */
package edu.colorado.phet.lasers.physics.mirror;

import edu.colorado.phet.lasers.physics.photon.Photon;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Mirror extends Wall {
    // Implementation of specific reflection behaviors
    protected ArrayList reflectionStrategies = new ArrayList();

    public Mirror( Point2D.Float end1, Point2D.Float end2 ) {
        super( end1, end2 );
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
