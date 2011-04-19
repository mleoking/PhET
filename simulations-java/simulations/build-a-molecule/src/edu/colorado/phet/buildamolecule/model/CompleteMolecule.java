// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.molecules.H2ONode;
import edu.colorado.phet.chemistry.molecules.NH3Node;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemistry.model.Atom.*;
import static edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.*;

/**
 * Represents a complete (stable) molecule with a name and structure
 */
public class CompleteMolecule {
    private String commonName;
    private MoleculeStructure moleculeStructure;
    private Function0<PNode> createNode;
    private String cmlData = null; // data in the CML format to describe the molecule

    // TODO: fully decide on 3d representation or data structure
    //private Map<Atom, ImmutableVector2D> positionMap = new HashMap<Atom, ImmutableVector2D>();

    public PNode createPseudo3DNode() {
        if ( createNode == null ) {
            throw new RuntimeException( "PNode not implemented for " + commonName );
        }
        return createNode.apply();
    }

    // TODO: i18n

    public CompleteMolecule( String commonName, MoleculeStructure moleculeStructure, Function0<PNode> createNode ) {
        this( commonName, moleculeStructure, createNode, null );
    }

    public CompleteMolecule( String commonName, MoleculeStructure moleculeStructure, Function0<PNode> createNode, String cmlData ) {
        this.commonName = commonName;
        this.moleculeStructure = moleculeStructure;
        this.createNode = createNode;
        this.cmlData = cmlData;
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

    public String getCmlData() {
        return cmlData;
    }

    public boolean hasCmlData() {
        return cmlData != null;
    }

    /*---------------------------------------------------------------------------*
    * complete molecules
    *----------------------------------------------------------------------------*/

    // NOTE: some results used http://webbook.nist.gov/chemistry/form-ser.html for the common names

    private static CompleteMolecule diatomic( String commonName, final Atom a, final Atom b, Function0<PNode> createNode ) {
        return new CompleteMolecule( commonName, new MoleculeStructure() {{
            Atom atomA = addAtom( a );
            Atom atomB = addAtom( b );
            addBond( atomA, atomB );
        }}, createNode );
    }

    private static CompleteMolecule triatomic( String commonName, final Atom a, final Atom b, final Atom c, Function0<PNode> createNode ) {
        return new CompleteMolecule( commonName, new MoleculeStructure() {{
            Atom atomA = addAtom( a );
            Atom atomB = addAtom( b );
            Atom atomC = addAtom( c );
            addBond( atomA, atomB );
            addBond( atomB, atomC );
        }}, createNode );
    }

    private static CompleteMolecule withTwoHydrogens( String commonName, final Atom a, Function0<PNode> createNode ) {
        return triatomic( commonName, new H(), a, new H(), createNode );
    }

    private static CompleteMolecule withThreeHydrogens( String commonName, final Atom a, Function0<PNode> createNode ) {
        return new CompleteMolecule( commonName, new MoleculeStructure() {{
            Atom atom = addAtom( a );
            Atom H1 = addAtom( new H() );
            Atom H2 = addAtom( new H() );
            Atom H3 = addAtom( new H() );
            addBond( atom, H1 );
            addBond( atom, H2 );
            addBond( atom, H3 );
        }}, createNode );
    }

    private static CompleteMolecule withFourHydrogens( String commonName, final Atom a, Function0<PNode> createNode ) {
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
        }}, createNode );
    }

    public static final CompleteMolecule H2O = new CompleteMolecule( "Water", new MoleculeStructure() {{
        Atom atomA = addAtom( new H() );
        Atom atomB = addAtom( new O() );
        Atom atomC = addAtom( new H() );
        addBond( atomA, atomB );
        addBond( atomB, atomC );
    }}, new Function0<PNode>() {
        public PNode apply() {
            return new H2ONode();
        }
    }, "<?xml version=\"1.0\"?>\n" +
       "<molecule id=\"id962\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
       " <name>962</name>\n" +
       " <atomArray>\n" +
       "  <atom id=\"a1\" elementType=\"O\" x3=\"0.000000\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
       "  <atom id=\"a2\" elementType=\"H\" x3=\"0.277400\" y3=\"0.892900\" z3=\"0.254400\"/>\n" +
       "  <atom id=\"a3\" elementType=\"H\" x3=\"0.606800\" y3=\"-0.238300\" z3=\"-0.716900\"/>\n" +
       " </atomArray>\n" +
       " <bondArray>\n" +
       "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
       "  <bond atomRefs2=\"a1 a3\" order=\"1\"/>\n" +
       " </bondArray>\n" +
       "</molecule>\n" );

    public static final CompleteMolecule O2 = diatomic( "Oxygen", new O(), new O(), new Function0<PNode>() {
        public PNode apply() {
            return new O2Node();
        }
    } );

    public static final CompleteMolecule H2 = diatomic( "Hydrogen", new H(), new H(), new Function0<PNode>() {
        public PNode apply() {
            return new H2Node();
        }
    } );

    public static final CompleteMolecule CO = diatomic( "Carbon Monoxide", new C(), new O(), new Function0<PNode>() {
        public PNode apply() {
            return new CONode();
        }
    } );

    public static final CompleteMolecule CO2 = triatomic( "Carbon Dioxide", new O(), new C(), new O(), new Function0<PNode>() {
        public PNode apply() {
            return new CO2Node();
        }
    } );

    public static final CompleteMolecule N2 = diatomic( "Nitrogen", new N(), new N(), new Function0<PNode>() {
        public PNode apply() {
            return new N2Node();
        }
    } );

    public static final CompleteMolecule O3 = triatomic( "Ozone", new O(), new O(), new O(), null );

    public static final CompleteMolecule F2 = diatomic( "Fluorine", new F(), new F(), null );

    public static final CompleteMolecule Cl2 = diatomic( "Chlorine", new Cl(), new Cl(), new Function0<PNode>() {
        public PNode apply() {
            return new Cl2Node();
        }
    } );

    public static final CompleteMolecule NO = diatomic( "Nitric Oxide", new N(), new O(), null );

    public static final CompleteMolecule NO2 = triatomic( "Nitrogen Dioxide", new O(), new N(), new O(), null );

    public static final CompleteMolecule N20 = triatomic( "Nitrous Oxide", new N(), new N(), new O(), null );

    public static final CompleteMolecule H2O2 = new CompleteMolecule( "Hydrogen Peroxide", new MoleculeStructure() {{
        Atom O1 = addAtom( new Atom.O() );
        Atom O2 = addAtom( new Atom.O() );
        Atom H1 = addAtom( new Atom.H() );
        Atom H2 = addAtom( new Atom.H() );
        addBond( H1, O1 );
        addBond( O1, O2 );
        addBond( O2, H2 );
    }}, null, "<?xml version=\"1.0\"?>\n" +
              "<molecule id=\"id784\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
              " <name>784</name>\n" +
              " <atomArray>\n" +
              "  <atom id=\"a1\" elementType=\"O\" x3=\"0.724700\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
              "  <atom id=\"a2\" elementType=\"O\" x3=\"-0.724700\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
              "  <atom id=\"a3\" elementType=\"H\" x3=\"0.823300\" y3=\"-0.700000\" z3=\"-0.667600\"/>\n" +
              "  <atom id=\"a4\" elementType=\"H\" x3=\"-0.823300\" y3=\"-0.617500\" z3=\"0.744600\"/>\n" +
              " </atomArray>\n" +
              " <bondArray>\n" +
              "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
              "  <bond atomRefs2=\"a1 a3\" order=\"1\"/>\n" +
              "  <bond atomRefs2=\"a2 a4\" order=\"1\"/>\n" +
              " </bondArray>\n" +
              "</molecule>" );

    public static final CompleteMolecule BH3 = withThreeHydrogens( "Borane", new B(), null );
    public static final CompleteMolecule H2S = withTwoHydrogens( "Hydrogen Sulfide", new S(), null );
    public static final CompleteMolecule NH3 = withThreeHydrogens( "Ammonia", new N(), new Function0<PNode>() {
        public PNode apply() {
            return new NH3Node();
        }
    } );
    public static final CompleteMolecule CH4 = withFourHydrogens( "Methane", new C(), null );
    public static final CompleteMolecule FH = diatomic( "Hydrogen Fluoride", new F(), new H(), null );
    public static final CompleteMolecule PH3 = withThreeHydrogens( "Phosphine", new P(), null );
    public static final CompleteMolecule SiH4 = withFourHydrogens( "Silane", new Si(), null );
    public static final CompleteMolecule ClH = diatomic( "Hydrogen Chloride", new H(), new Cl(), null );
    public static final CompleteMolecule BF3 = new CompleteMolecule( "Boron Trifluoride", new MoleculeStructure() {{
        Atom B1 = addAtom( new B() );
        Atom F1 = addAtom( new F() );
        Atom F2 = addAtom( new F() );
        Atom F3 = addAtom( new F() );
        addBond( B1, F1 );
        addBond( B1, F2 );
        addBond( B1, F3 );
    }}, null );
    public static final CompleteMolecule CHN = triatomic( "Hydrogen Cyanide", new H(), new C(), new N(), null );
    public static final CompleteMolecule CH2O = new CompleteMolecule( "Formaldehyde", new MoleculeStructure() {{
        Atom C1 = addAtom( new C() );
        Atom H1 = addAtom( new H() );
        Atom H2 = addAtom( new H() );
        Atom O1 = addAtom( new O() );
        addBond( C1, H1 );
        addBond( C1, H2 );
        addBond( C1, O1 );
    }}, null );
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
    }}, null );
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
    }}, null, "<?xml version=\"1.0\"?>\n" +
              "<molecule id=\"id11638\" xmlns=\"http://www.xml-cml.org/schema\">\n" +
              " <name>11638</name>\n" +
              " <atomArray>\n" +
              "  <atom id=\"a1\" elementType=\"F\" x3=\"0.678300\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
              "  <atom id=\"a2\" elementType=\"C\" x3=\"-0.678300\" y3=\"0.000000\" z3=\"0.000000\"/>\n" +
              "  <atom id=\"a3\" elementType=\"H\" x3=\"-1.029300\" y3=\"0.464000\" z3=\"0.923900\"/>\n" +
              "  <atom id=\"a4\" elementType=\"H\" x3=\"-1.029300\" y3=\"0.568100\" z3=\"-0.863900\"/>\n" +
              "  <atom id=\"a5\" elementType=\"H\" x3=\"-1.029300\" y3=\"-1.032200\" z3=\"-0.060100\"/>\n" +
              " </atomArray>\n" +
              " <bondArray>\n" +
              "  <bond atomRefs2=\"a1 a2\" order=\"1\"/>\n" +
              "  <bond atomRefs2=\"a2 a3\" order=\"1\"/>\n" +
              "  <bond atomRefs2=\"a2 a4\" order=\"1\"/>\n" +
              "  <bond atomRefs2=\"a2 a5\" order=\"1\"/>\n" +
              " </bondArray>\n" +
              "</molecule>\n" );
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
    }}, null );
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
    }}, null );
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
    }}, null );
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
    }}, null );
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
    }}, null );

    public static final CompleteMolecule C2H2 = new CompleteMolecule( "Acetylene", new MoleculeStructure() {{
        Atom C1 = addAtom( new Atom.C() );
        Atom C2 = addAtom( new Atom.C() );
        Atom H1 = addAtom( new Atom.H() );
        Atom H2 = addAtom( new Atom.H() );
        addBond( H1, C1 );
        addBond( C1, C2 );
        addBond( C2, H2 );
    }}, null );

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
    }}, null );


    public static final CompleteMolecule[] COMPLETE_MOLECULES = new CompleteMolecule[] {
            H2O, O2, H2, CO2, N2, O3, CO, F2, Cl2, NO, NO2, N20, H2O2, BH3, H2S, NH3, CH4, FH, PH3, SiH4, ClH, BF3, CHN,
            CH2O, CH3OH, CH3F, CH2F2, CHF3, CF4, CH3Cl, CH2Cl2, C2H2, C2H4
    };

    /**
     * Find a complete molecule with an equivalent structure to the passed in molecule
     *
     * @param moleculeStructure Molecule structure to match
     * @return Either a matching CompleteMolecule, or null if none is found
     */
    public static CompleteMolecule findMatchingCompleteMolecule( MoleculeStructure moleculeStructure ) {
        for ( CompleteMolecule completeMolecule : CompleteMolecule.COMPLETE_MOLECULES ) {
            if ( moleculeStructure.isEquivalent( completeMolecule.getMoleculeStructure() ) ) {
                return completeMolecule;
            }
        }
        return null;
    }

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
