/**
 * Class: TransformListener
 * Package: edu.colorado.phet.common.view.graphics
 * Author: Another Guy
 * Date: Aug 29, 2003
 */
package edu.colorado.phet.microwaves.common.graphics;

/**
 * Listens for changes in the model or view viewport.
 */
public interface TransformListener {
    public void transformChanged( ModelViewTransform2D mvt );
}
