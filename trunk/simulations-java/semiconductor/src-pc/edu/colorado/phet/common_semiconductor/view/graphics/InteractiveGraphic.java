/**
 * Class: InteractiveGraphic
 * Package: edu.colorado.phet.common.view.graphics.mousecontrols
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.common_semiconductor.view.graphics;

import edu.colorado.phet.common_semiconductor.view.graphics.bounds.Boundary;

import javax.swing.event.MouseInputListener;

/**
 * The coupling of Graphic and Controller.
 */
public interface InteractiveGraphic extends Graphic, MouseInputListener, Boundary {
}
