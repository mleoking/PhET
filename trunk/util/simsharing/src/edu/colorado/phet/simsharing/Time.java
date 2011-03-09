// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public interface Time extends Serializable {
    public static Time LIVE = new Time() {
        @Override
        public String toString() {
            return "Live";
        }

        @Override
        public boolean equals( Object obj ) {
            return obj.toString().equals( toString() );
        }
    };

    public static class Index implements Time {
        int index;

        public Index( int index ) {
            this.index = index;
        }

        @Override
        public String toString() {
            return "Time.Index{" +
                   "index=" + index +
                   '}';
        }
    }
}
