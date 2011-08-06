// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.util.ArrayList;

import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SucrosePositions;

/**
 * @author Sam Reid
 */
public class Sucrose extends Molecule {
    //Lists of the component atoms for use in the SucroseNode
    private ArrayList<Atom> hydrogens = new ArrayList<Atom>();
    private ArrayList<Atom> oxygens = new ArrayList<Atom>();
    private ArrayList<Atom> carbons = new ArrayList<Atom>();

    public Sucrose( World world, final ModelViewTransform transform, double x, double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener, ObservableProperty<Double> oxygenCharge, ObservableProperty<Double> hydrogenCharge ) {
        super( world, transform, x, y, vx, vy, theta, addUpdateListener );
        SucrosePositions sucrosePositions = new SucrosePositions();
        ArrayList<Atom> allAtoms = new ArrayList<Atom>();

        //Flag the first particle as being the coordinate frame origin for the molecule
        boolean origin = true;
        for ( ImmutableVector2D offset : sucrosePositions.getCarbonPositions() ) {
            final Atom carbon = new Atom( x + offset.getX(), y + offset.getY(), transform, WaterMolecule.carbonRadius, body, offset.getX(), offset.getY(), new Property<Double>( 0.0 ), origin );
            carbons.add( carbon );
            allAtoms.add( carbon );
            origin = false;
        }
        for ( ImmutableVector2D position : sucrosePositions.getOxygenPositions() ) {
            final Atom oxygen = new Atom( x + position.getX(), y + position.getY(), transform, WaterMolecule.oxygenRadius, body, position.getX(), position.getY(), oxygenCharge, origin );
            oxygens.add( oxygen );
            allAtoms.add( oxygen );
        }
        for ( ImmutableVector2D position : sucrosePositions.getHydrogenPositions() ) {
            final Atom hydrogen = new Atom( x + position.getX(), y + position.getY(), transform, WaterMolecule.hydrogenRadius, body, position.getX(), position.getY(), hydrogenCharge, origin );
            hydrogens.add( hydrogen );
            allAtoms.add( hydrogen );
        }
        initAtoms( allAtoms.toArray( new Atom[allAtoms.size()] ) );
    }

    public ArrayList<Atom> getHydrogens() {
        return hydrogens;
    }

    public ArrayList<Atom> getOxygens() {
        return oxygens;
    }

    public ArrayList<Atom> getCarbons() {
        return carbons;
    }
}
