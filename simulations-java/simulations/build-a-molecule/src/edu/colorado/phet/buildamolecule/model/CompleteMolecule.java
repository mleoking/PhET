// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import edu.colorado.phet.chemistry.model.Atom;

/**
 * Represents a complete (stable) molecule with a name and structure
 */
public class CompleteMolecule {
    private String commonName;
    private MoleculeStructure moleculeStructure;

    // TODO: add in 3D structure here. ideally a Map<Atom,Point3D>

    // TODO: i18n

    public CompleteMolecule( String commonName, MoleculeStructure moleculeStructure ) {
        this.commonName = commonName;
        this.moleculeStructure = moleculeStructure;
    }

    public String getCommonName() {
        return commonName;
    }

    public MoleculeStructure getMoleculeStructure() {
        return moleculeStructure;
    }

    public static final CompleteMolecule H2O = new CompleteMolecule( "Water", new MoleculeStructure() {{
        Atom H1 = addAtom( new Atom.H() );
        Atom H2 = addAtom( new Atom.H() );
        Atom O1 = addAtom( new Atom.O() );
        addBond( O1, H1 );
        addBond( O1, H2 );
    }} );

    public static final CompleteMolecule O2 = new CompleteMolecule( "Oxygen", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom O2 = addAtom( new Atom.O() );
        addBond( O1, O2 );
    }} );

    public static final CompleteMolecule H2 = new CompleteMolecule( "Hydrogen", new MoleculeStructure() {{
        Atom H1 = addAtom( new Atom.H() );
        Atom H2 = addAtom( new Atom.H() );
        addBond( H1, H2 );
    }} );

    public static final CompleteMolecule CO2 = new CompleteMolecule( "Carbon Dioxide", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom O2 = addAtom( new Atom.O() );
        Atom C1 = addAtom( new Atom.C() );
        addBond( C1, O1 );
        addBond( C1, O2 );
    }} );

    public static final CompleteMolecule N2 = new CompleteMolecule( "Nitrogen", new MoleculeStructure() {{
        Atom N1 = addAtom( new Atom.N() );
        Atom N2 = addAtom( new Atom.N() );
        addBond( N1, N2 );
    }} );

    public static final CompleteMolecule[] COMPLETE_MOLECULES = new CompleteMolecule[] {
            H2O, O2, H2, CO2, N2
    };
}
