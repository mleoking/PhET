/**
 * Class: InteractiveGraphic
 * Package: edu.colorado.phet.common.view.graphics.mousecontrols
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.semiconductor.oldphetgraphics.graphics;

import javax.swing.event.MouseInputListener;

import edu.colorado.phet.semiconductor.oldphetgraphics.graphics.bounds.Boundary;

/**
 * The coupling of Graphic and Controller.
 */
public interface InteractiveGraphic extends Graphic, MouseInputListener, Boundary {
}
