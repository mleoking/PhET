// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.test.transforms;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 8:50:55 PM
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
