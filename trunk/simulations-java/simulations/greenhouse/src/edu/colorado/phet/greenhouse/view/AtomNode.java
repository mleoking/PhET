/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.greenhouse.model.Atom;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * Class that represents an atom in the view.
 * 
 * @author John Blanco
 */
public class AtomNode extends PPath {
    
    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final Atom atom;
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public AtomNode( Atom atom ){
        this.atom = atom;
        setPaint( atom.getRepresentationColor() );
        setPathTo( new Ellipse2D.Double( -atom.getRadius(), -atom.getRadius(), atom.getRadius() * 2, atom.getRadius() * 2 ) );
        atom.addObserver( new SimpleObserver() {
            public void update() {
                updatePosition();
            }
        });
        updatePosition();
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * 
     */
    private void updatePosition() {
        setOffset( atom.getPosition() );
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
