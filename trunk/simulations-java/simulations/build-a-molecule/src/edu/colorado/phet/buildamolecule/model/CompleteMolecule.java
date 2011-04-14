// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.chemistry.model.Atom;

import static edu.colorado.phet.chemistry.model.Atom.*;

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

    private static CompleteMolecule diatomic( String commonName, final Atom a, final Atom b ) {
        return new CompleteMolecule( commonName, new MoleculeStructure() {{
            Atom atomA = addAtom( a );
            Atom atomB = addAtom( b );
            addBond( atomA, atomB );
        }} );
    }

    private static CompleteMolecule triatomic( String commonName, final Atom a, final Atom b, final Atom c ) {
        return new CompleteMolecule( commonName, new MoleculeStructure() {{
            Atom atomA = addAtom( a );
            Atom atomB = addAtom( b );
            Atom atomC = addAtom( c );
            addBond( atomA, atomB );
            addBond( atomB, atomC );
        }} );
    }

    private static CompleteMolecule withTwoHydrogens( String commonName, final Atom a ) {
        return triatomic( commonName, new H(), a, new H() );
    }

    private static CompleteMolecule withThreeHydrogens( String commonName, final Atom a ) {
        return new CompleteMolecule( commonName, new MoleculeStructure() {{
            Atom atom = addAtom( a );
            Atom H1 = addAtom( new H() );
            Atom H2 = addAtom( new H() );
            Atom H3 = addAtom( new H() );
            addBond( atom, H1 );
            addBond( atom, H2 );
            addBond( atom, H3 );
        }} );
    }

    private static CompleteMolecule withFourHydrogens( String commonName, final Atom a ) {
        return new CompleteMolecule( commonName, new MoleculeStructure() {{
            Atom atom = addAtom( a );
            Atom H1 = addAtom( new H() );
            Atom H2 = addAtom( new H() );
            Atom H3 = addAtom( new H() );
            Atom H4 = addAtom( new H() );
            addBond( atom, H1 );
            addBond( atom, H2 );
            addBond( atom, H3 );
            addBond( atom, H4 );
        }} );
    }

    public static final CompleteMolecule H2O = withTwoHydrogens( "Water", new O() );

    public static final CompleteMolecule O2 = diatomic( "Oxygen", new O(), new O() );

    public static final CompleteMolecule H2 = diatomic( "Hydrogen", new H(), new H() );

    public static final CompleteMolecule CO = diatomic( "Carbon Monoxide", new C(), new O() );

    public static final CompleteMolecule CO2 = triatomic( "Carbon Dioxide", new O(), new C(), new O() );

    public static final CompleteMolecule N2 = diatomic( "Nitrogen", new N(), new N() );

    public static final CompleteMolecule O3 = triatomic( "Ozone", new O(), new O(), new O() );

    public static final CompleteMolecule F2 = diatomic( "Fluorine", new F(), new F() );

    public static final CompleteMolecule Cl2 = diatomic( "Chlorine", new Cl(), new Cl() );

    public static final CompleteMolecule NO = diatomic( "Nitric Oxide", new N(), new O() );

    public static final CompleteMolecule NO2 = triatomic( "Nitrogen Dioxide", new O(), new N(), new O() );

    public static final CompleteMolecule N20 = triatomic( "Nitrous Oxide", new N(), new N(), new O() );

    public static final CompleteMolecule H2O2 = new CompleteMolecule( "Hydrogen Peroxide", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom O2 = addAtom( new Atom.O() );
        Atom H1 = addAtom( new Atom.H() );
        Atom H2 = addAtom( new Atom.H() );
        addBond( H1, O1 );
        addBond( O1, O2 );
        addBond( O2, H2 );
    }} );

    public static final CompleteMolecule BH3 = withThreeHydrogens( "Borane", new B() );
    public static final CompleteMolecule H2S = withTwoHydrogens( "Hydrogen Sulfide", new S() );
    public static final CompleteMolecule NH3 = withThreeHydrogens( "Ammonia", new N() );
    public static final CompleteMolecule CH4 = withFourHydrogens( "Methane", new C() );
    public static final CompleteMolecule FH = diatomic( "Hydrogen Fluoride", new F(), new H() );
    public static final CompleteMolecule PH3 = withThreeHydrogens( "Phosphine", new P() );
    public static final CompleteMolecule SiH4 = withFourHydrogens( "Silane", new Si() );
    public static final CompleteMolecule ClH = diatomic( "Hydrogen Chloride", new H(), new Cl() );
    public static final CompleteMolecule BF3 = new CompleteMolecule( "Boron Trifluoride", new MoleculeStructure() {{
        Atom B1 = addAtom( new B() );
        Atom F1 = addAtom( new F() );
        Atom F2 = addAtom( new F() );
        Atom F3 = addAtom( new F() );
        addBond( B1, F1 );
        addBond( B1, F2 );
        addBond( B1, F3 );
    }} );
    public static final CompleteMolecule CHN = triatomic( "Hydrogen Cyanide", new H(), new C(), new N() );
    public static final CompleteMolecule CH2O = new CompleteMolecule( "Formaldehyde", new MoleculeStructure() {{
        Atom C1 = addAtom( new C() );
        Atom H1 = addAtom( new H() );
        Atom H2 = addAtom( new H() );
        Atom O1 = addAtom( new O() );
        addBond( C1, H1 );
        addBond( C1, H2 );
        addBond( C1, O1 );
    }} );
    public static final CompleteMolecule CH3OH = new CompleteMolecule( "Methyl Alcohol", new MoleculeStructure() {{
        Atom C1 = addAtom( new C() );
        Atom H1 = addAtom( new H() );
        Atom H2 = addAtom( new H() );
        Atom H3 = addAtom( new H() );
        Atom H4 = addAtom( new H() );
        Atom O1 = addAtom( new O() );
        addBond( C1, H1 );
        addBond( C1, H2 );
        addBond( C1, H3 );
        addBond( C1, O1 );
        addBond( O1, H4 );
    }} );
    public static final CompleteMolecule CH3F = new CompleteMolecule( "Fluoromethane", new MoleculeStructure() {{
        Atom C1 = addAtom( new C() );
        Atom H1 = addAtom( new H() );
        Atom H2 = addAtom( new H() );
        Atom H3 = addAtom( new H() );
        Atom F1 = addAtom( new F() );
        addBond( C1, H1 );
        addBond( C1, H2 );
        addBond( C1, H3 );
        addBond( C1, F1 );
    }} );
    public static final CompleteMolecule CH2F2 = new CompleteMolecule( "Difluoromethane", new MoleculeStructure() {{
        Atom C1 = addAtom( new C() );
        Atom H1 = addAtom( new H() );
        Atom H2 = addAtom( new H() );
        Atom F1 = addAtom( new F() );
        Atom F2 = addAtom( new F() );
        addBond( C1, H1 );
        addBond( C1, H2 );
        addBond( C1, F1 );
        addBond( C1, F2 );
    }} );
    public static final CompleteMolecule CHF3 = new CompleteMolecule( "Trifluoromethane", new MoleculeStructure() {{
        Atom C1 = addAtom( new C() );
        Atom H1 = addAtom( new H() );
        Atom F1 = addAtom( new F() );
        Atom F2 = addAtom( new F() );
        Atom F3 = addAtom( new F() );
        addBond( C1, H1 );
        addBond( C1, F1 );
        addBond( C1, F2 );
        addBond( C1, F3 );
    }} );
    public static final CompleteMolecule CF4 = new CompleteMolecule( "Carbon Tetrafluoride", new MoleculeStructure() {{
        Atom C1 = addAtom( new C() );
        Atom F1 = addAtom( new F() );
        Atom F2 = addAtom( new F() );
        Atom F3 = addAtom( new F() );
        Atom F4 = addAtom( new F() );
        addBond( C1, F1 );
        addBond( C1, F2 );
        addBond( C1, F3 );
        addBond( C1, F4 );
    }} );
    public static final CompleteMolecule CH3Cl = new CompleteMolecule( "Chloromethane", new MoleculeStructure() {{
        Atom C1 = addAtom( new C() );
        Atom H1 = addAtom( new H() );
        Atom H2 = addAtom( new H() );
        Atom H3 = addAtom( new H() );
        Atom Cl1 = addAtom( new Cl() );
        addBond( C1, H1 );
        addBond( C1, H2 );
        addBond( C1, H3 );
        addBond( C1, Cl1 );
    }} );
    public static final CompleteMolecule CH2Cl2 = new CompleteMolecule( "Methylene Chloride", new MoleculeStructure() {{
        Atom C1 = addAtom( new C() );
        Atom H1 = addAtom( new H() );
        Atom H2 = addAtom( new H() );
        Atom Cl1 = addAtom( new Cl() );
        Atom Cl2 = addAtom( new Cl() );
        addBond( C1, H1 );
        addBond( C1, H2 );
        addBond( C1, Cl1 );
        addBond( C1, Cl2 );
    }} );

    public static final CompleteMolecule C2H2 = new CompleteMolecule( "Acetylene", new MoleculeStructure() {{
        Atom C1 = addAtom( new Atom.C() );
        Atom C2 = addAtom( new Atom.C() );
        Atom H1 = addAtom( new Atom.H() );
        Atom H2 = addAtom( new Atom.H() );
        addBond( H1, C1 );
        addBond( C1, C2 );
        addBond( C2, H2 );
    }} );

    public static final CompleteMolecule C2H4 = new CompleteMolecule( "Ethylene", new MoleculeStructure() {{
        Atom C1 = addAtom( new Atom.C() );
        Atom C2 = addAtom( new Atom.C() );
        Atom H1 = addAtom( new Atom.H() );
        Atom H2 = addAtom( new Atom.H() );
        Atom H3 = addAtom( new Atom.H() );
        Atom H4 = addAtom( new Atom.H() );
        addBond( H1, C1 );
        addBond( H2, C1 );
        addBond( C1, C2 );
        addBond( C2, H3 );
        addBond( C2, H4 );
    }} );


    public static final CompleteMolecule[] COMPLETE_MOLECULES = new CompleteMolecule[] {
            H2O, O2, H2, CO2, N2, O3, CO, F2, Cl2, NO, NO2, N20, H2O2, BH3, H2S, CH4, FH, PH3, SiH4, ClH, BF3, CHN,
            CH2O, CH3OH, CH3F, CH2F2, CHF3, CF4, CH3Cl, CH2Cl2, C2H2
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
