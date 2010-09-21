/**
 * Class: CompositeTransformListener
 * Package: edu.colorado.phet.common.view.graphics
 * Author: Another Guy
 * Date: Aug 29, 2003
 */
package edu.colorado.phet.microwaves.common.graphics;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 10:26:01 AM
 */
public class CompositeTransformListener implements TransformListener {
    ArrayList listeners = new ArrayList();

    public void transformChanged( ModelViewTransform2D mvt ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            TransformListener o = (TransformListener)listeners.get( i );
            o.transformChanged( mvt );
        }
    }

    public void removeTransformListener( TransformListener tl ) {
        listeners.remove( tl );
    }

    public void addTransformListener( TransformListener tl ) {
        listeners.add( tl );
    }
}