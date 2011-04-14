// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.LinkedList;
import java.util.List;

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

    /**
     * Check whether this structure is allowed. Currently this means it is a "sub-molecule" of one of our complete
     * molecules
     *
     * @param moleculeStructure Molecule to check
     * @return True if it is allowed
     */
    public static boolean isAllowedStructure( MoleculeStructure moleculeStructure ) {
        return isStructureInAllowedStructures( moleculeStructure );
    }

    /*---------------------------------------------------------------------------*
    * complete molecules
    *----------------------------------------------------------------------------*/

    // NOTE: some results used http://webbook.nist.gov/chemistry/form-ser.html for the common names

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

    public static final CompleteMolecule CO = new CompleteMolecule( "Carbon Monoxide", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom C1 = addAtom( new Atom.C() );
        addBond( C1, O1 );
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

    public static final CompleteMolecule O3 = new CompleteMolecule( "Ozone", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom O2 = addAtom( new Atom.O() );
        Atom O3 = addAtom( new Atom.O() );
        addBond( O1, O2 );
        addBond( O2, O3 );
    }} );

    public static final CompleteMolecule F2 = new CompleteMolecule( "Fluorine", new MoleculeStructure() {{
        Atom F1 = addAtom( new Atom.F() );
        Atom F2 = addAtom( new Atom.F() );
        addBond( F1, F2 );
    }} );

    public static final CompleteMolecule Cl2 = new CompleteMolecule( "Chlorine", new MoleculeStructure() {{
        Atom Cl1 = addAtom( new Atom.Cl() );
        Atom Cl2 = addAtom( new Atom.Cl() );
        addBond( Cl1, Cl2 );
    }} );

    public static final CompleteMolecule NO = new CompleteMolecule( "Nitric Oxide", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom N1 = addAtom( new Atom.N() );
        addBond( N1, O1 );
    }} );

    public static final CompleteMolecule NO2 = new CompleteMolecule( "Nitrogen Dioxide", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom O2 = addAtom( new Atom.O() );
        Atom N1 = addAtom( new Atom.N() );
        addBond( N1, O1 );
        addBond( N1, O2 );
    }} );

    public static final CompleteMolecule N20 = new CompleteMolecule( "Nitrous Oxide", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom N1 = addAtom( new Atom.N() );
        Atom N2 = addAtom( new Atom.N() );
        addBond( N1, O1 );
        addBond( N1, N2 );
    }} );

    public static final CompleteMolecule H2O2 = new CompleteMolecule( "Hydrogen Peroxide", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom O2 = addAtom( new Atom.O() );
        Atom H1 = addAtom( new Atom.H() );
        Atom H2 = addAtom( new Atom.H() );
        addBond( H1, O1 );
        addBond( O1, O2 );
        addBond( O2, H2 );
    }} );


    public static final CompleteMolecule[] COMPLETE_MOLECULES = new CompleteMolecule[] {
            H2O, O2, H2, CO2, N2, O3, CO, F2, Cl2, NO, NO2, N20, H2O2
    };

    /*---------------------------------------------------------------------------*
    * computation of allowed molecule structures
    *----------------------------------------------------------------------------*/

    private static final List<MoleculeStructure> allowedStructures = new LinkedList<MoleculeStructure>();

    private static boolean isStructureInAllowedStructures( MoleculeStructure moleculeStructure ) {
        for ( MoleculeStructure allowedStructure : allowedStructures ) {
            if ( moleculeStructure.isEquivalent( allowedStructure ) ) {
                return true;
            }
        }
        return false;
    }

    private static void addMoleculeAndChildren( MoleculeStructure molecule ) {
        if ( !isStructureInAllowedStructures( molecule ) ) {
            // NOTE: only handles tree-based structures here
            allowedStructures.add( molecule );
            for ( Atom atom : molecule.getAtoms() ) {
                if ( molecule.getNeighbors( atom ).size() < 2 && molecule.getAtoms().size() >= 2 ) {
                    // we could remove this atom and it wouldn't break apart
                    addMoleculeAndChildren( molecule.getCopyWithAtomRemoved( atom ) );
                }
            }
        }
    }

    static {
        // add all possible molecule paths to our allowed structures
        long a = System.currentTimeMillis();
        for ( CompleteMolecule completeMolecule : COMPLETE_MOLECULES ) {
            addMoleculeAndChildren( completeMolecule.getMoleculeStructure() );
        }
        long b = System.currentTimeMillis();
        System.out.println( "Built allowed molecule structures in " + ( b - a ) + "ms" );
    }
}
