package edu.colorado.phet.gravityandorbits.view;

/**
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
