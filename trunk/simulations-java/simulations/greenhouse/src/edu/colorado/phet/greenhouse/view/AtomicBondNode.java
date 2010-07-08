/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
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
    
    private static double BOND_WIDTH = 10; // In model coords, which are essentially pixels.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final AtomicBond atomicBond;
    private final ModelViewTransform2D mvt;
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public AtomicBondNode( AtomicBond atomicBond, ModelViewTransform2D mvt ){
        this.atomicBond = atomicBond;
        this.mvt = mvt;
        setStrokePaint( atomicBond.getAtom1().getRepresentationColor() );
        setStroke( new BasicStroke( (float)BOND_WIDTH ) );
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

    private void updateShape() {
        Point2D transformedPt1 = mvt.modelToViewDouble( atomicBond.getAtom1().getPosition() );
        Point2D transformedPt2 = mvt.modelToViewDouble( atomicBond.getAtom2().getPosition() );
        setPathTo( new Line2D.Double( transformedPt1, transformedPt2 ) );
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
