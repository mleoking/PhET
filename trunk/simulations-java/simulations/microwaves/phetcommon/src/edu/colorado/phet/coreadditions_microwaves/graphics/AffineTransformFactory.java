/**
 * Class: AffineTransformFactory
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.coreadditions_microwaves.graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;

public interface AffineTransformFactory {
    AffineTransform getTx( Rectangle outputRectangle );
}
