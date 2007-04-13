/**
 * Class: RevertableGraphicsSetup
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Mar 10, 2004
 */
package edu.colorado.phet.common.view;

import java.awt.*;

public interface RevertableGraphicsSetup extends GraphicsSetup {
    void revert( Graphics2D g );
}
