/**
 * Class: PartialMirror
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 31, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.mirror;

import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * This class represents partially reflecting mirror.
 */
public class PartialMirror extends Mirror {

    // Fraction of photons the mirror reflects. Must be number between
    // 0 an 1
    // Default partial reflection strategy
    private Partial partialStrategy;


    public PartialMirror ( Point2D end1, Point2D end2 ) {
        super( end1, end2 );
        partialStrategy = new Partial( 1.0f );
        this.addReflectionStrategy( partialStrategy );
    }

    public double getReflectivity() {
        return partialStrategy.getReflectivity();
    }

    public void setReflectivity( double reflectivity ) {
        partialStrategy.setReflectivity( reflectivity );
    }

    public void addReflectionStrategy( ReflectionStrategy reflectionStrategy ) {
        // If the strategy being added is a reflecting strategy, remove the old one
        if( reflectionStrategy instanceof Partial ) {
            partialStrategy = (Partial)reflectionStrategy;
            for( int i = 0; i < reflectionStrategies.size(); i++ ) {
                ReflectionStrategy strategy = (ReflectionStrategy)reflectionStrategies.get( i );
                if( strategy instanceof Partial ) {
                    reflectionStrategies.remove( strategy );
                    break;
                }
            }
        }
        this.reflectionStrategies.add( reflectionStrategy );
    }

    /**
     * This method is arranged so that if a photon is not reflected by the partially
     * reflecting mirror, it will not be reconsidered in the next time step. If this
     * is not done, we can't control the percent of photons that get through, because
     * some of them get considered more than once.
     * @param photon
     * @return
     */
//    public boolean reflects( Photon photon ) {
//        boolean result = super.reflects( photon );
//        if( result && !partialStrategy.reflects( photon )) {
//            throw new RuntimeException( "TBI" );
////            result = false;
////            photon.setCollidable( false );
//        }
//        return result;
//    }
}
