/**
 * Class: CollisionUtil
 * Package: edu.colorado.phet.collision
 * Original Author: Ron LeMaster
 * Creation Date: Oct 24, 2004
 * Creation Time: 10:48:02 AM
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.collision;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CollisionUtil {

    /**
     * Determines if two objects are instances of a pair of classes.
     *
     * @param obj1
     * @param obj2
     * @param class1
     * @param class2
     * @return
     */
    public static boolean areConformantToClasses( Object obj1, Object obj2, Class class1, Class class2 ) {
        return class1.isInstance( obj1 ) && class2.isInstance( obj2 )
               || class1.isInstance( obj2 ) && class2.isInstance( obj1 );
    }

    public static Object getInstanceOfClass( Class testClass, Object[] testObjects ) {
        Object result = null;
        for( int i = 0; i < testObjects.length && result == null; i++ ) {
            if( testClass.isInstance( testObjects[i] ) ) {
                result = testObjects[i];
            }
        }
        return result;
    }

    /**
     * 
     * @param bodies
     * @param classifiedBodies
     */
    public static void classifyBodies( Object[] bodies, Map classifiedBodies ) {
        if( areConformantToClasses( bodies[0], bodies[1],
                                    (Class)classifiedBodies.keySet().toArray()[0],
                                    (Class)classifiedBodies.keySet().toArray()[1] ) ) {
            Set classes = classifiedBodies.keySet();
            for( Iterator iterator = classes.iterator(); iterator.hasNext(); ) {
                Class aClass = (Class)iterator.next();
                classifiedBodies.put( aClass, null );
                Object obj = getInstanceOfClass( aClass, bodies );
                classifiedBodies.put( aClass, obj );
            }
        }
    }

    /**
     * More efficient version
     * @param bodies
     * @param classes
     * @param classifiedBodies
     */
    public static void classifyBodies( Object[] bodies, Class[] classes, Map classifiedBodies ) {
        if( areConformantToClasses( bodies[0], bodies[1],
                                    classes[0],
                                    classes[1] ) ) {
            for( int i = 0; i < classes.length; i++ ) {
                classifiedBodies.put( classes[i], null );
                Object obj = getInstanceOfClass( classes[i], bodies );
                classifiedBodies.put( classes[i], obj );
            }
        }
    }

    //    public static void getClassifiedBodies( Class[] classes, Collidable[] bodiesToClassify, Object[] classifiedBodies ) {
    //        bodies[0] = null;
    //        bodies[1] = null;
    //        if( areConformantToClasses( body1, body2, classes[0], classes[1])) {
    //            classifiedBodies[0] = getInstanceOfClass( classes[0], bodiesToClassify );
    //        }
    //    }
}
