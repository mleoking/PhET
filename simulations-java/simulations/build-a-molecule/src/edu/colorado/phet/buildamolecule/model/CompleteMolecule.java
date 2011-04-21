// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.molecules.*;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.*;

/**
 * Represents a complete (stable) molecule with a name and structure. Includes 2d and 3d representations, and can generate visuals of both types.
 */
public class CompleteMolecule {
    private String commonName; // as said by pubchem (or overridden)
    private String molecularFormula; // as said by pubchem
    private MoleculeStructure moleculeStructure;

    // more advanced molecule support. primarily for 3d display
    private AtomWrapper[] atomWrappers;
    private BondWrapper[] bondWrappers;

    // nodes listed so we can construct them with reflection TODO: auto-construction of nodes like the default case, but tuned?
    private static final Class[] nodeClasses = new Class[] {
            Cl2Node.class, CO2Node.class, CO2Node.class, CS2Node.class, F2Node.class, H2Node.class, N2Node.class, NONode.class, N2ONode.class,
            O2Node.class, C2H2Node.class, C2H4Node.class, C2H5ClNode.class, C2H5OHNode.class, C2H6Node.class, CH2ONode.class, CH3OHNode.class,
            CH4Node.class, H2ONode.class, H2SNode.class, HClNode.class, HFNode.class, NH3Node.class, NO2Node.class, OF2Node.class, P4Node.class,
            PCl3Node.class, PCl5Node.class, PF3Node.class, PH3Node.class, SO2Node.class, SO3Node.class
    };

    // TODO: i18n. how?

    /**
     * Construct a molecule out of a pipe-separated line.
     *
     * @param line
     */
    public CompleteMolecule( String line ) {
        StringTokenizer t = new StringTokenizer( line, "|" );

        // read common name first
        commonName = t.nextToken();

        // molecular formula
        molecularFormula = t.nextToken();

        // # of atoms
        int atomCount = Integer.parseInt( t.nextToken() );

        // # of bonds
        int bondCount = Integer.parseInt( t.nextToken() );
        moleculeStructure = new MoleculeStructure();

        // for each atom, read its symbol, then 2d coordinates, then 3d coordinates (total of 6 fields)
        atomWrappers = new AtomWrapper[atomCount];
        for ( int i = 0; i < atomCount; i++ ) {
            String symbol = t.nextToken();
            double x2d = Double.parseDouble( t.nextToken() );
            double y2d = Double.parseDouble( t.nextToken() );
            double x3d = Double.parseDouble( t.nextToken() );
            double y3d = Double.parseDouble( t.nextToken() );
            double z3d = Double.parseDouble( t.nextToken() );
            Atom atom = AtomModel.createAtomBySymbol( symbol );
            moleculeStructure.addAtom( atom );
            atomWrappers[i] = new AtomWrapper( x2d, y2d, x3d, y3d, z3d, atom );
        }

        // for each bond, read atom indices (2 of them, which are 1-indexed), and then the order of the bond (single, double, triple, etc.)
        bondWrappers = new BondWrapper[bondCount];
        for ( int i = 0; i < bondCount; i++ ) {
            int a = Integer.parseInt( t.nextToken() );
            int b = Integer.parseInt( t.nextToken() );
            int order = Integer.parseInt( t.nextToken() );
            MoleculeStructure.Bond bond = new MoleculeStructure.Bond( atomWrappers[a - 1].atom, atomWrappers[b - 1].atom ); // -1 since our format is 1-based
            moleculeStructure.addBond( bond );
            bondWrappers[i] = new BondWrapper( a, b, bond, order );
        }
    }

    public String getCommonName() {
        String ret = commonName;
        if ( ret.startsWith( "molecular " ) ) {
            ret = ret.substring( "molecular ".length() );
        }
        return capitalize( ret );
    }

    private String capitalize( String str ) {
        char[] characters = str.toCharArray();
        boolean lastWasSpace = true;
        for ( int i = 0; i < characters.length; i++ ) {
            char character = characters[i];
            if ( Character.isWhitespace( character ) ) {
                lastWasSpace = true;
            }
            else {
                if ( lastWasSpace && Character.isLetter( character ) && Character.isLowerCase( character ) ) {
                    characters[i] = Character.toUpperCase( character );
                }
                lastWasSpace = false;
            }
        }
        return String.valueOf( characters );
    }

    private void setCommonName( String commonName ) {
        this.commonName = commonName;
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
        String formula = moleculeStructure.getHillSystemFormulaFragment();

        // use the allowed structure map as an acceleration feature
        if ( allowedStructureMap.containsKey( formula ) ) {
            List<MoleculeStructure> moleculeStructures = allowedStructureMap.get( formula );
            if ( moleculeStructures != null ) {
                for ( MoleculeStructure structure : moleculeStructures ) {
                    if ( structure.isEquivalent( moleculeStructure ) ) {
                        // sanity checks TODO: change to assertions once we figure out the bug
                        if ( !structure.isValid() ) {
                            System.out.println( "inherent structure invalid" );
                        }
                        if ( !moleculeStructure.isValid() ) {
                            System.out.println( "presented structure invalid!" );
                            System.out.println( "comparing " + structure.toSerial() + " to " + moleculeStructure.toSerial() );
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return An XML CML string for our 3D representation
     */
    public String getCmlData() {
        String ret = "<?xml version=\"1.0\"?>\n" +
                     "<molecule id=\"" + commonName + "\" xmlns=\"http://www.xml-cml.org/schema\">";
        ret += "<name>" + commonName + "</name>";
        ret += "<atomArray>";
        for ( int i = 0; i < atomWrappers.length; i++ ) {
            AtomWrapper atomWrapper = atomWrappers[i];
            // TODO: include the formal charge possibly later, if Jmol can show it?
            ret += "<atom id=\"a" + ( i + 1 ) + "\" elementType=\"" + atomWrapper.atom.getSymbol() + "\" x3=\"" + atomWrapper.x3d + "\" y3=\"" + atomWrapper.y3d + "\" z3=\"" + atomWrapper.z3d + "\"/>";
        }
        ret += "</atomArray>";
        ret += "<bondArray>";
        for ( BondWrapper bondWrapper : bondWrappers ) {
            ret += "<bond atomRefs2=\"a" + bondWrapper.a + " a" + bondWrapper.b + "\" order=\"" + bondWrapper.order + "\"/>";
        }
        ret += "</bondArray>";
        ret += "</molecule>";
        return ret;
    }

    /**
     * @return A node that represents a 2d but quasi-3D version
     */
    public PNode createPseudo3DNode() {
        // if we can find it in the common chemistry nodes, use that
        for ( Class nodeClass : nodeClasses ) {
            if ( nodeClass.getSimpleName().equals( molecularFormula + "Node" ) || ( nodeClass == NH3Node.class && molecularFormula.equals( "H3N" ) ) ) {
                try {
                    return (PNode) nodeClass.getConstructors()[0].newInstance();
                }
                catch ( InstantiationException e ) {
                    e.printStackTrace();
                }
                catch ( IllegalAccessException e ) {
                    e.printStackTrace();
                }
                catch ( InvocationTargetException e ) {
                    e.printStackTrace();
                }
            }
        }

        // otherwise, use our 2d positions to construct a version. we get the correct back-to-front rendering
        return new PNode() {{
            List<AtomWrapper> wrappers = new LinkedList<AtomWrapper>( Arrays.asList( atomWrappers ) );

            // sort by Z-depth in 3D
            Collections.sort( wrappers, new Comparator<AtomWrapper>() {
                public int compare( AtomWrapper a, AtomWrapper b ) {
                    return ( new Double( a.z3d ) ).compareTo( b.z3d );
                }
            } );

            for ( final AtomWrapper atomWrapper : wrappers ) {
                addChild( new AtomNode( atomWrapper.atom ) {{
                    setOffset( atomWrapper.x2d * 15, atomWrapper.y2d * 15 ); // custom scale for now.
                }} );
            }
        }};
    }

    /*---------------------------------------------------------------------------*
    * complete molecules
    *----------------------------------------------------------------------------*/

    /**
     * Find a complete molecule with an equivalent structure to the passed in molecule
     *
     * @param moleculeStructure Molecule structure to match
     * @return Either a matching CompleteMolecule, or null if none is found
     */
    public static CompleteMolecule findMatchingCompleteMolecule( MoleculeStructure moleculeStructure ) {
        for ( CompleteMolecule completeMolecule : CompleteMolecule.completeMolecules ) {
            if ( moleculeStructure.isEquivalent( completeMolecule.getMoleculeStructure() ) ) {
                return completeMolecule;
            }
        }
        return null;
    }

    public static CompleteMolecule getMoleculeByName( String name ) {
        CompleteMolecule ret = moleculeMap.get( name );
        if ( ret == null ) {
            System.out.println( "WARNING: could not find molecule with name: " + name );
        }
        return ret;
    }

    public static List<CompleteMolecule> getAllCompleteMolecules() {
        return new LinkedList<CompleteMolecule>( completeMolecules );
    }

    /*---------------------------------------------------------------------------*
    * computation of allowed molecule structures
    *----------------------------------------------------------------------------*/

    // TODO: pair down some of the data structures for simplicity

    private static List<CompleteMolecule> completeMolecules = new LinkedList<CompleteMolecule>(); // all complete molecules
    private static Map<String, CompleteMolecule> moleculeMap = new HashMap<String, CompleteMolecule>(); // map from unique name => complete molecule

    // maps to allow us to efficiently look things up by molecular formula. since we allow isomers, multiple structures can have the same formula.
    private static Map<String, List<CompleteMolecule>> completeMoleculeMap = new HashMap<String, List<CompleteMolecule>>();
    private static Map<String, List<MoleculeStructure>> allowedStructureMap = new HashMap<String, List<MoleculeStructure>>();

    private static void addCompleteMolecule( final CompleteMolecule completeMolecule ) {
        String formula = completeMolecule.getMoleculeStructure().getHillSystemFormulaFragment();
        if ( completeMoleculeMap.containsKey( formula ) ) {
            completeMoleculeMap.get( formula ).add( completeMolecule );
        }
        else {
            completeMoleculeMap.put( formula, new LinkedList<CompleteMolecule>() {{
                add( completeMolecule );
            }} );
        }
    }

    private static void addAllowedStructure( final MoleculeStructure structure ) {
        String formula = structure.getHillSystemFormulaFragment();
        if ( allowedStructureMap.containsKey( formula ) ) {
            allowedStructureMap.get( formula ).add( structure );
        }
        else {
            allowedStructureMap.put( formula, new LinkedList<MoleculeStructure>() {{
                add( structure );
            }} );
        }
    }

    static {
        try {
            long a = System.currentTimeMillis();

            /*---------------------------------------------------------------------------*
            * read our complete molecules
            *----------------------------------------------------------------------------*/

            BufferedReader moleculeReader = new BufferedReader( new InputStreamReader( BuildAMoleculeResources.getResourceLoader().getResourceAsStream( "molecules.txt" ) ) );
            try {
                while ( moleculeReader.ready() ) {
                    String line = moleculeReader.readLine();
                    CompleteMolecule molecule = new CompleteMolecule( line );

                    // TODO: potentially in the future remove the unnecessary checks here if we filter data correctly
                    if ( molecule.getMoleculeStructure().hasLoopsOrIsDisconnected() ) {
                        System.out.println( "ignoring molecule: " + molecule.getCommonName() );
                        continue;
                    }
                    if ( molecule.getMoleculeStructure().hasWeirdHydrogenProperties() ) {
                        System.out.println( "weird hydrogen pattern in: " + molecule.getCommonName() );
                        continue;
                    }

                    completeMolecules.add( molecule );
                    moleculeMap.put( molecule.getCommonName(), molecule );

                    addCompleteMolecule( molecule );
                    addAllowedStructure( molecule.getMoleculeStructure() );
                }
            }
            finally {
                moleculeReader.close();
            }
            long b = System.currentTimeMillis();
            System.out.println( "molecules read in: " + ( b - a ) + "ms" );

            /*---------------------------------------------------------------------------*
            * read our incomplete (but allowed) structures
            *----------------------------------------------------------------------------*/

            BufferedReader structureReader = new BufferedReader( new InputStreamReader( BuildAMoleculeResources.getResourceLoader().getResourceAsStream( "structures.txt" ) ) );
            try {
                while ( structureReader.ready() ) {
                    String line = structureReader.readLine();
                    MoleculeStructure structure = MoleculeStructure.fromSerial( line );

                    if ( structure.hasWeirdHydrogenProperties() ) {
                        System.out.println( "weird hydrogen pattern in structure: " + line );
                        continue;
                    }

                    addAllowedStructure( structure );
                }
            }
            finally {
                structureReader.close();
            }
            long c = System.currentTimeMillis();
            System.out.println( "other structures read in: " + ( c - b ) + "ms" );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    /*---------------------------------------------------------------------------*
    * molecule references and customized names
    *----------------------------------------------------------------------------*/

    public static final CompleteMolecule CO2 = getMoleculeByName( "Carbon Dioxide" );
    public static final CompleteMolecule H2O = getMoleculeByName( "Water" );
    public static final CompleteMolecule N2 = getMoleculeByName( "Nitrogen" );
    public static final CompleteMolecule CO = getMoleculeByName( "Carbon Monoxide" );
    public static final CompleteMolecule NO = getMoleculeByName( "Nitric Oxide" );
    public static final CompleteMolecule O2 = getMoleculeByName( "Oxygen" );
    public static final CompleteMolecule H2 = getMoleculeByName( "Hydrogen" );
    public static final CompleteMolecule Cl2 = getMoleculeByName( "Chlorine" );
    public static final CompleteMolecule NH3 = getMoleculeByName( "Ammonia" );

    /**
     * Molecules that can be used for collection boxes
     */
    private static final CompleteMolecule[] COLLECTION_BOX_MOLECULES = new CompleteMolecule[] {
            CO2, H2O, N2, CO, O2, H2, NH3, Cl2, NO,
            getMoleculeByName( "Acetylene" ),
            getMoleculeByName( "Formaldehyde" ),
            getMoleculeByName( "Hydrogen Cyanide" ),
            getMoleculeByName( "Sulfur Dioxide" )
    };

    static {
        // TODO: i18n
        for ( CompleteMolecule m : COLLECTION_BOX_MOLECULES ) {
            assert ( m != null );
        }
    }

    private static Random random = new Random( System.currentTimeMillis() );

    public static CompleteMolecule pickRandomCollectionBoxMolecule() {
        return COLLECTION_BOX_MOLECULES[random.nextInt( COLLECTION_BOX_MOLECULES.length )];
    }

    private static class AtomWrapper {
        // 2d coordinates
        public final double x2d;
        public final double y2d;

        // 3d coordinates
        public final double x3d;
        public final double y3d;
        public final double z3d;

        // our atom
        public final Atom atom;

        private AtomWrapper( double x2d, double y2d, double x3d, double y3d, double z3d, Atom atom ) {
            this.x2d = x2d;
            this.y2d = y2d;
            this.x3d = x3d;
            this.y3d = y3d;
            this.z3d = z3d;
            this.atom = atom;
        }
    }

    private static class BondWrapper {
        public int a;
        public int b;
        public final MoleculeStructure.Bond bond;
        public final int order;

        private BondWrapper( int a, int b, MoleculeStructure.Bond bond, int order ) {
            this.a = a;
            this.b = b;
            this.bond = bond;
            this.order = order;
        }
    }

    /*---------------------------------------------------------------------------*
    * precomputation of allowed molecule structures TODO: consider separating this into another class
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

    /**
     * This generates a list of allowed "structures" that are not complete molecules. Since this takes about 10 minutes, we need to precompute the
     * majority of it.
     *
     * @param args
     */
    public static void main( String[] args ) {
        StringBuilder builder = new StringBuilder();
        // add all possible molecule paths to our allowed structures
        long a = System.currentTimeMillis();
        for ( CompleteMolecule completeMolecule : completeMolecules ) {
            System.out.println( "processing molecule and children: " + completeMolecule.getCommonName() );
            addMoleculeAndChildren( completeMolecule.getMoleculeStructure() );
        }
        long b = System.currentTimeMillis();
        System.out.println( "Built allowed molecule structures in " + ( b - a ) + "ms" );
        for ( MoleculeStructure structure : allowedStructures ) {
            if ( findMatchingCompleteMolecule( structure ) == null ) {
                // it is an intermediate structure
                builder.append( structure.toSerial() + "\n" );
            }
        }
        try {
            File outputFile = new File( "structures.txt" );
            FileOutputStream outputStream = new FileOutputStream( outputFile );
            try {
                outputStream.write( builder.toString().getBytes( "utf-8" ) );
            }
            finally {
                outputStream.close();
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
