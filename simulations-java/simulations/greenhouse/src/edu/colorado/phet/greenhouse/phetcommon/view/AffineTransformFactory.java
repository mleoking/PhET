/**
 * Class: AffineTransformFactory
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse.phetcommon.view;

import java.awt.geom.AffineTransform;
import java.awt.*;

public interface AffineTransformFactory {
    AffineTransform getTx( Rectangle outputRectangle );
}
