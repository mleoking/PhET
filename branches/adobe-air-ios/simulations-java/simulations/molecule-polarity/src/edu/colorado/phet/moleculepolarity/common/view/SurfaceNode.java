// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculepolarity.common.control.MoleculeAngleHandler;
import edu.colorado.phet.moleculepolarity.common.control.RotateCursorHandler;
import edu.colorado.phet.moleculepolarity.common.model.Atom;
import edu.colorado.phet.moleculepolarity.common.model.Molecule2D;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for 2D surface nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class SurfaceNode extends PComposite {

    public SurfaceNode( Molecule2D molecule ) {

        // Update the node when the atom's location or EN changes.
        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                if ( getVisible() ) {
                    updateNode();
                }
            }
        };
        for ( Atom atom : molecule.getAtoms() ) {
            atom.location.addObserver( observer, false );
            atom.electronegativity.addObserver( observer, false );
        }

        addInputEventListener( new RotateCursorHandler() );
        addInputEventListener( new MoleculeAngleHandler( molecule, this ) );
    }

    // implemented by subclasses, specific to the number of colors in the surface's gradient
    protected abstract void updateNode();

    // Update when the node becomes visible.
    @Override public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            updateNode();
        }
    }

    // Creates the shape of a "cloud" around an atom.
    protected static Shape createCloudShape( Atom atom, double diameterScale ) {
        final double diameter = diameterScale * atom.getDiameter();
        double x = atom.location.get().getX() - ( diameter / 2 );
        double y = atom.location.get().getY() - ( diameter / 2 );
        return new Ellipse2D.Double( x, y, diameter, diameter );
    }

    // Creates a transform that accounts for the molecule's location and orientation.
    protected static AffineTransform createTransform( Molecule2D molecule ) {
        AffineTransform transform = new AffineTransform();
        transform.translate( molecule.location.getX(), molecule.location.getY() );
        transform.rotate( molecule.angle.get() );
        return transform;
    }
}
