/**
 * Class: Partial
 * Package: edu.colorado.phet.lasers.model.mirror
 * Author: Another Guy
 * Date: Apr 2, 2003
 */
package edu.colorado.phet.lasers.model.mirror;

import edu.colorado.phet.lasers.model.photon.Photon;

public class Partial implements ReflectionStrategy {

    private float reflectivity;

    public Partial( float reflectivity ) {
        this.reflectivity = reflectivity;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity( float reflectivity ) {
        if( reflectivity < 0 || reflectivity > 1 ) {
            throw new RuntimeException( "Reflectivity not between 0 and 1.0");
        }
        this.reflectivity = reflectivity;
    }

    public boolean reflects( Photon photon ) {
        boolean result = false;
        if( reflectivity == 0.0 ) {
            result = false;
        }
        else if( reflectivity == 1.0 ) {
            result = true;
        }
        else {
            float r = (float)Math.random();
            if( r < reflectivity ) {
                result = true;
            }
        }
        return result;
    }
}
