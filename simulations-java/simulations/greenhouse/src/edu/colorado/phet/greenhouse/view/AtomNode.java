/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
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
    private final ModelViewTransform2D mvt;
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public AtomNode( Atom atom, ModelViewTransform2D mvt ){
        this.atom = atom;
        this.mvt = mvt;
        setPaint( atom.getRepresentationColor() );
        double transformedRadius = mvt.modelToViewDifferentialXDouble( atom.getRadius() );
        setPathTo( new Ellipse2D.Double( -transformedRadius, -transformedRadius,
                transformedRadius * 2, transformedRadius * 2 ) );
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
        setOffset( mvt.modelToViewDouble( atom.getPosition() ) );
        System.out.println("atom node bounds = " + getFullBoundsReference());
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
