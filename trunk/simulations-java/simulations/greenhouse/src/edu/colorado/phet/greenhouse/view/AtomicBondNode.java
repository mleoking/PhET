/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.greenhouse.model.AtomicBond;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * Class that represents an atomic bond in the view.
 * 
 * @author John Blanco
 */
public class AtomicBondNode extends PPath {
    
    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final AtomicBond atomicBond;
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public AtomicBondNode( AtomicBond atomicBond ){
        this.atomicBond = atomicBond;
        setStrokePaint( atomicBond.getAtom1().getRepresentationColor() );
        atomicBond.addObserver( new SimpleObserver() {
            public void update() {
                updateShape();
            }
        });
        updateShape();
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * 
     */
    private void updateShape() {
        setPathTo( new Line2D.Double( atomicBond.getAtom1().getPosition(), atomicBond.getAtom2().getPosition() ));
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
