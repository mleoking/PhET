// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Model for a water molecule, including mappings between box2d physics engine and SI coordinates
 *
 * @author Sam Reid
 */
public class WaterMolecule extends Molecule {
    public static final double oxygenRadius = 1E-10;
    public static final double hydrogenRadius = 0.5E-10;
    public static final double carbonRadius = oxygenRadius;//TODO: get better radii

    public WaterMolecule( World world, final ModelViewTransform transform, double x, double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener ) {
        super( world, transform, x, y, vx, vy, theta, addUpdateListener );

        initAtoms( new Atom( x, y, transform, oxygenRadius, body, 0, 0, -2, true ),
                   new Atom( x + 0.5E-10, y + 0.5E-10, transform, hydrogenRadius, body, 0.5E-10, 0.5E-10, 1, false ),
                   new Atom( x - 1E-10, y + 1E-10, transform, hydrogenRadius, body, -0.5E-10, 0.5E-10, 1, false ) );
    }

    public Atom getOxygen() {
        return atoms.get( 0 );
    }

    public Atom getHydrogen1() {
        return atoms.get( 1 );
    }

    public Atom getHydrogen2() {
        return atoms.get( 2 );
    }
}