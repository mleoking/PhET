/**
 * Class: ContactDetector
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 26, 2003
 */
package edu.colorado.phet.collision;

import java.util.ArrayList;

abstract public class ContactDetector {

    static private ArrayList s_contactExperts = new ArrayList();

    protected ContactDetector() {
        s_contactExperts.add( this );
    }

    abstract public boolean areInContact( CollidableBody bodyA, CollidableBody bodyB );

    abstract protected boolean applies( CollidableBody bodyA, CollidableBody bodyB );

    //
    // Static fields and methods
    //

    /**
     * Determines if two bodies are in contact. Note that the first ContactDetector that
     * applies to the types of the arguments will make the determination. This means if
     * there are detectors that apply to superclasses of the arguments, they may make
     * the determination, if they are considered before more type-specific detectors.
     *
     * @param bodyA
     * @param bodyB
     * @return
     */
    static public boolean areContacting( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean result = false;
        boolean resultEstablished = false;
        for( int i = 0; i < s_contactExperts.size() && !resultEstablished; i++ ) {
            ContactDetector contactExpert = (ContactDetector)s_contactExperts.get( i );
            if( contactExpert.applies( bodyA, bodyB ) ) {
                result = contactExpert.areInContact( bodyA, bodyB );
                resultEstablished = true;
            }
        }
        return result;
    }
}
