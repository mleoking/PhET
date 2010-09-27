/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


/**
 * Main model class.  Units are picometers (1E-12).
 */
public class BuildAnAtomModel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Rectangle2D MODEL_BOUNDS = new Rectangle2D.Double(-200, -150, 400, 300);

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    private final Atom atom;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomModel( BuildAnAtomClock clock ) {
        super();

        this.clock = clock;

        atom = new Atom(new Point2D.Double(0, 0));
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    public Atom getAtom() {
        return atom;
    }

    public Rectangle2D getBounds(){
        return MODEL_BOUNDS;
    }

    public BuildAnAtomClock getClock() {
        return clock;
    }

    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------

    /**
     * This class represents that atom in the model.  It supplies static
     * information such as the position of the atom, as well as dynamic
     * information such as the number of protons present.
     */
    public static class Atom {

        private static final double NUCLEUS_RADIUS = 20; // In picometers.

        private final Point2D position = new Point2D.Double();

        private final ArrayList<Double> electronShellRadii = new ArrayList<Double>(){{
            add(new Double(50));
            add( new Double(100));
        }};

        public Atom( Point2D position ) {
            this.position.setLocation( position );
        }

        public ArrayList<Double> getElectronShellRadii() {
            return electronShellRadii;
        }

        public static double getNucleusRadius() {
            return NUCLEUS_RADIUS;
        }

        public Point2D getPosition() {
            return position;
        }
    }

}
