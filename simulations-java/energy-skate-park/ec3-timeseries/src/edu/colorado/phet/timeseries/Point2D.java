/* Copyright 2007, University of Colorado */
package edu.colorado.phet.timeseries;

import java.io.Serializable;

public class Point2D {
    public static class Double extends java.awt.geom.Point2D.Double implements Serializable {
        public Double() {
            super();
        }

        public Double( double v, double v1 ) {
            super( v, v1 );
        }
    }
}
