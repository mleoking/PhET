/* Copyright 2007, University of Colorado */
package edu.colorado.phet.timeseries;

import java.io.IOException;
import java.io.Serializable;

public class SPoint2D {
    public static class Double extends java.awt.geom.Point2D.Double implements Serializable {
        public Double() {
            super();
        }

        public Double( double v, double v1 ) {
            super( v, v1 );
        }

        private void writeObject(java.io.ObjectOutputStream out) throws IOException {
            out.writeDouble(x);
            out.writeDouble(y);
        }

        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            x = in.readDouble();
            y = in.readDouble();
        }

        public String toString() {
            return getClass().getName()+" ["+getX()+", "+getY()+"]";
        }
    }
}
