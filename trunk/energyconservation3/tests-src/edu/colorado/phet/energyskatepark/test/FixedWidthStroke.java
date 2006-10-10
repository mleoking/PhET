/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.test;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 8:50:55 PM
 * Copyright (c) May 26, 2006 by Sam Reid
 */
class FixedWidthStroke implements Stroke {
    private Stroke prototype;
    private float width;

    public FixedWidthStroke( Stroke prototype, float width ) {
        this.prototype = prototype;
        this.width = width;
    }

    public Shape createStrokedShape( Shape p ) {
        return null;
    }
}
