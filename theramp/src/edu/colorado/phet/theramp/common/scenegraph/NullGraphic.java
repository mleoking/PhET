/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 3:23:48 AM
 * Copyright (c) Jun 2, 2005 by Sam Reid
 */

public class NullGraphic extends AbstractGraphic {
    public void paint( Graphics2D graphics2D ) {
    }

    public boolean contains( double x, double y ) {
        return false;
    }

    public double getWidth() {
        return 0;
    }

    public double getHeight() {
        return 0;
    }
}
