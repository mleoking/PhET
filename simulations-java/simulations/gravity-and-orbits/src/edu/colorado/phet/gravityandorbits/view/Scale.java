// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

/**
 * Enumeration pattern that indicates the different types of scale for the simulation, currently cartoon and real.
 * Cartoon scale modifies the sizes and locations of the bodies, Real shows everything to scale.
 *
 * @author Sam Reid
 */
public class Scale {
    public static final Scale CARTOON = new Cartoon();
    public static final Scale REAL = new Real();
    private final boolean showLabelArrows;

    private Scale( boolean showLabelArrows ) {
        this.showLabelArrows = showLabelArrows;
    }

    public boolean getShowLabelArrows() {
        return showLabelArrows;
    }

    private static class Cartoon extends Scale {
        public Cartoon() {
            super( false );
        }
    }

    private static class Real extends Scale {
        public Real() {
            super( true );
        }
    }
}