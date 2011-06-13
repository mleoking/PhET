// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

import edu.colorado.phet.solublesalts.model.ISugarMolecule;
import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.crystal.OneToOneLattice;
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.NegativeSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.PositiveSugarIon;

/**
 * Model sugar as a kind of salt with positive and negative sucrose molecules (that look identical)
 *
 * @author Sam Reid
 */
public class SugarCrystal extends Salt implements ISugarMolecule {
    static private Lattice lattice = new OneToOneLattice( SugarIon.RADIUS * 2 );
    static private ArrayList<Component> components = new ArrayList<Component>();

    static {
        components.add( new Component( PositiveSugarIon.class, 1 ) );
        components.add( new Component( NegativeSugarIon.class, 1 ) );
    }

    public SugarCrystal() {
        super( components, lattice, PositiveSugarIon.class, NegativeSugarIon.class, 36 );
    }
}
