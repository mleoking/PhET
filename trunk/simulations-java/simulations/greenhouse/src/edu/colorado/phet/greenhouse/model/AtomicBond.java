/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;


/**
 * Class that represents an atomic bond between two atoms.
 * 
 * @author John Blanco
 */
public class AtomicBond extends SimpleObservable {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final Atom atom1, atom2;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public AtomicBond( Atom atom1, Atom atom2 ) {
        this.atom1 = atom1;
        this.atom2 = atom2;
        
        // Listen to the atoms that are involved in this bond and send an
        // update notification whenever they move.
        this.atom1.addObserver( new SimpleObserver() {
            public void update() {
                notifyObservers();
            }
        });
        this.atom2.addObserver( new SimpleObserver() {
            public void update() {
                notifyObservers();
            }
        });
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public Atom getAtom1() {
        return atom1;
    }

    
    public Atom getAtom2() {
        return atom2;
    }
    
    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
