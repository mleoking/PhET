// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.util.ArrayList;

import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ProjectedPositions.AtomPosition;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SucrosePositions;

/**
 * @author Sam Reid
 */
public class Sucrose extends Molecule {
    //Lists of the component atoms for use in the SucroseNode
    private ArrayList<Atom> hydrogens = new ArrayList<Atom>();
    private ArrayList<Atom> oxygens = new ArrayList<Atom>();
    private ArrayList<Atom> carbons = new ArrayList<Atom>();
    private ObservableProperty<Double> oxygenCharge;
    private ObservableProperty<Double> hydrogenCharge;

    public Sucrose( World world, final ModelViewTransform transform, double x, double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener, ObservableProperty<Double> oxygenCharge, ObservableProperty<Double> hydrogenCharge ) {
        super( world, transform, x, y, vx, vy, theta, addUpdateListener );
        this.oxygenCharge = oxygenCharge;
        this.hydrogenCharge = hydrogenCharge;
        SucrosePositions sucrosePositions = new SucrosePositions();
        ArrayList<Atom> allAtoms = new ArrayList<Atom>();

        //Flag the first particle as being the coordinate frame origin for the molecule
        boolean origin = true;

        for ( AtomPosition atomPosition : sucrosePositions.getAtoms() ) {
            final Atom a = new Atom( x + atomPosition.position.getX(), y + atomPosition.position.getY(), transform, WaterMolecule.carbonRadius, body, atomPosition.position.getX(), atomPosition.position.getY(), getCharge( atomPosition ), origin );
            if ( atomPosition.type.equals( "C" ) ) { carbons.add( a ); }
            if ( atomPosition.type.equals( "H" ) ) { hydrogens.add( a ); }
            if ( atomPosition.type.equals( "O" ) ) { oxygens.add( a ); }
            allAtoms.add( a );
            origin = false;
        }
        initAtoms( allAtoms.toArray( new Atom[allAtoms.size()] ) );
    }

    private ObservableProperty<Double> getCharge( AtomPosition atomPosition ) {
        if ( atomPosition.type.equals( "C" ) ) { return new Property<Double>( 0.0 ); }
        if ( atomPosition.type.equals( "H" ) ) { return hydrogenCharge; }
        if ( atomPosition.type.equals( "O" ) ) { return oxygenCharge; }
        throw new RuntimeException( "Unknown type: " + atomPosition.type );
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
