// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import java.util.ArrayList;

import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.water.model.SucrosePositions;

/**
 * @author Sam Reid
 */
public class Sucrose extends Molecule {
    public Sucrose( World world, final ModelViewTransform transform, double x, double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener ) {
        super( world, transform, x, y, vx, vy, theta, addUpdateListener );
        SucrosePositions sucrosePositions = new SucrosePositions();
        ArrayList<Atom> allAtoms = new ArrayList<Atom>();

        //Flag the first particle as being the coordinate frame origin for the molecule
        boolean origin = true;
        for ( ImmutableVector2D offset : sucrosePositions.getCarbonPositions() ) {
            allAtoms.add( new Atom( x + offset.getX(), y + offset.getY(), transform, WaterMolecule.oxygenRadius, body, offset.getX(), offset.getY(), 0, origin ) );
            origin = false;
        }
        for ( ImmutableVector2D position : sucrosePositions.getOxygenPositions() ) {
            allAtoms.add( new Atom( x + position.getX(), y + position.getY(), transform, WaterMolecule.oxygenRadius, body, position.getX(), position.getY(), 0, origin ) );
        }
        for ( ImmutableVector2D position : sucrosePositions.getHydrogenPositions() ) {
            allAtoms.add( new Atom( x + position.getX(), y + position.getY(), transform, WaterMolecule.oxygenRadius, body, position.getX(), position.getY(), 0, origin ) );
        }
        initAtoms( allAtoms.toArray( new Atom[0] ) );
    }
}
