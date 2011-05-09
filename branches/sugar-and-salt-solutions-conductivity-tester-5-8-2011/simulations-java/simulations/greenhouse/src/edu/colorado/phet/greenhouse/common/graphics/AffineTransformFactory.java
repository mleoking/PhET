// Copyright 2002-2011, University of Colorado

/**
 * Class: AffineTransformFactory
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse.common.graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;

public interface AffineTransformFactory {
    AffineTransform getTx( Rectangle outputRectangle );
}
