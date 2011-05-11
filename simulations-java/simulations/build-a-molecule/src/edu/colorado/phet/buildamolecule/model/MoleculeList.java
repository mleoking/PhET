//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeApplication;
import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;

/**
 * Has functions relating to lists of molecules (e.g. is a molecule or submolecule allowed?) Uses static initialization to load in a small fraction
 * of molecules from collection-molecules.txt, and then in a separate thread loads the rest of the molecules + the allowed structures. The 1st
 * call that requires the full molecule list or allowed structures will block until it is all read in or computed
 */
public class MoleculeList {
    /*---------------------------------------------------------------------------*
    * complete molecule data structures
    *----------------------------------------------------------------------------*/
    private List<CompleteMolecule> completeMolecules = new ArrayList<CompleteMolecule>(); // all complete molecules
    private Map<String, CompleteMolecule> moleculeNameMap = new HashMap<String, CompleteMolecule>(); // map from unique name => complete molecule

    /*---------------------------------------------------------------------------*
    * allowed structure data structures
    *----------------------------------------------------------------------------*/
    private Map<String, List<StrippedMolecule>> allowedStructureFormulaMap = new HashMap<String, List<StrippedMolecule>>(); // formula => allowed stripped molecules

    /*---------------------------------------------------------------------------*
    * statics
    *----------------------------------------------------------------------------*/
    private static volatile MoleculeList masterInstance = null;
    private static volatile boolean initialized = false;
    private static Thread computeThread = null;
    public static Random random = new Random( System.currentTimeMillis() );
    private static final MoleculeList initialList = new MoleculeList() {{
        loadInitialData();
    }};

    public static synchronized void startInitialization() {
        final long startTime = System.currentTimeMillis();
        computeThread = new Thread() {
            @Override public void run() {
                masterInstance = new MoleculeList() {{
                    loadMasterData();
                }};

                initialized = true;

                final long stopTime = System.currentTimeMillis();
                System.out.println( "completed MoleculeList initialization in " + ( stopTime - startTime ) + "ms" );
            }
        };
        System.out.println( "starting MoleculeList initialization" );
        computeThread.start();
    }

    public static synchronized MoleculeList getMasterInstance() {
        if ( !initialized ) {
            if ( computeThread == null ) {
                startInitialization();
            }
            try {
                computeThread.join();
            }
            catch ( InterruptedException e ) {
                throw new RuntimeException( "interrupted", e );
            }
        }

        return masterInstance;
    }

    public static synchronized CompleteMolecule getMoleculeByName( String name ) {
        CompleteMolecule ret = initialList.moleculeNameMap.get( name );
        if ( ret == null ) {
            System.out.println( "WARNING: looking up " + name + " in master instance." );
            ret = getMasterInstance().moleculeNameMap.get( name );
        }
        return ret;
    }

    protected void loadInitialData() {
        List<CompleteMolecule> mainMolecules = readCompleteMoleculesFromFilename( "collection-molecules.txt" );
        for ( CompleteMolecule molecule : mainMolecules ) {
            addCompleteMolecule( molecule );
        }
    }

    protected void loadMasterData() {
        List<CompleteMolecule> mainMolecules = readCompleteMoleculesFromFilename( "molecules.txt" );
        for ( CompleteMolecule molecule : mainMolecules ) {

            // if our molecule was included in the initial lookup, use that initial version instead so we can have instance equality preserved
            CompleteMolecule initialListLookup = initialList.moleculeNameMap.get( molecule.getCommonName() );
            if ( initialListLookup != null && molecule.getMoleculeStructure().isEquivalent( initialListLookup.getMoleculeStructure() ) ) {
                molecule = initialListLookup;
            }

            addCompleteMolecule( molecule );
        }

        List<MoleculeStructure> mainStructures = readMoleculeStructuresFromFilename( "structures.txt" );
        for ( MoleculeStructure structure : mainStructures ) {
            addAllowedStructure( structure );
        }
    }

    /**
     * Check whether this structure is allowed. Currently this means it is a "sub-molecule" of one of our complete
     * molecules
     *
     * @param moleculeStructure Molecule to check
     * @return True if it is allowed
     */
    public boolean isAllowedStructure( MoleculeStructure moleculeStructure ) {
        StrippedMolecule strippedMolecule = new StrippedMolecule( moleculeStructure );
        String hashString = strippedMolecule.stripped.getHistogram().getHashString();

        // if the molecule contained only 1 or 2 hydrogen, it is ok
        if ( strippedMolecule.stripped.getAtoms().isEmpty() && moleculeStructure.getAtoms().size() <= 2 ) {
            return true;
        }

        // don't allow invalid forms!
        if ( !moleculeStructure.isValid() ) {
            return false;
        }

        // use the allowed structure map as an acceleration feature
        if ( allowedStructureFormulaMap.containsKey( hashString ) ) {
            List<StrippedMolecule> moleculeStructures = allowedStructureFormulaMap.get( hashString );
            if ( moleculeStructures != null ) {
                for ( StrippedMolecule structure : moleculeStructures ) {
                    if ( structure.isHydrogenSubmolecule( strippedMolecule ) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Find a complete molecule with an equivalent structure to the passed in molecule
     *
     * @param moleculeStructure Molecule structure to match
     * @return Either a matching CompleteMolecule, or null if none is found
     */
    public CompleteMolecule findMatchingCompleteMolecule( MoleculeStructure moleculeStructure ) {
        for ( CompleteMolecule completeMolecule : completeMolecules ) {
            if ( moleculeStructure.isEquivalent( completeMolecule.getMoleculeStructure() ) ) {
                return completeMolecule;
            }
        }
        return null;
    }

    public List<CompleteMolecule> getAllCompleteMolecules() {
        return new LinkedList<CompleteMolecule>( completeMolecules );
    }

    public CompleteMolecule pickRandomCollectionBoxMolecule() {
        // TODO: once this is gone, refactor so we don't need the molecule list master instance for this
        if ( BuildAMoleculeApplication.allowGenerationWithAllMolecules.get() ) {
            return completeMolecules.get( random.nextInt( completeMolecules.size() ) );
        }
        else {
            return COLLECTION_BOX_MOLECULES[random.nextInt( COLLECTION_BOX_MOLECULES.length )];
        }
    }

    /*---------------------------------------------------------------------------*
    * computation of allowed molecule structures
    *----------------------------------------------------------------------------*/

    private void addCompleteMolecule( final CompleteMolecule molecule ) {
        completeMolecules.add( molecule );
        moleculeNameMap.put( molecule.getCommonName(), molecule );
    }

    private void addAllowedStructure( final MoleculeStructure structure ) {
        final StrippedMolecule strippedMolecule = new StrippedMolecule( structure );
        String hashString = strippedMolecule.stripped.getHistogram().getHashString();
        if ( allowedStructureFormulaMap.containsKey( hashString ) ) {
            allowedStructureFormulaMap.get( hashString ).add( strippedMolecule );
        }
        else {
            allowedStructureFormulaMap.put( hashString, new LinkedList<StrippedMolecule>() {{
                add( strippedMolecule );
            }} );
        }
    }

    /*---------------------------------------------------------------------------*
    * molecule references and customized names
    *----------------------------------------------------------------------------*/

    public static final CompleteMolecule CO2 = MoleculeList.getMoleculeByName( "Carbon Dioxide" );
    public static final CompleteMolecule H2O = MoleculeList.getMoleculeByName( "Water" );
    public static final CompleteMolecule N2 = MoleculeList.getMoleculeByName( "Nitrogen" );
    public static final CompleteMolecule CO = MoleculeList.getMoleculeByName( "Carbon Monoxide" );
    public static final CompleteMolecule NO = MoleculeList.getMoleculeByName( "Nitric Oxide" );
    public static final CompleteMolecule O2 = MoleculeList.getMoleculeByName( "Oxygen" );
    public static final CompleteMolecule H2 = MoleculeList.getMoleculeByName( "Hydrogen" );
    public static final CompleteMolecule Cl2 = MoleculeList.getMoleculeByName( "Chlorine" );
    public static final CompleteMolecule NH3 = MoleculeList.getMoleculeByName( "Ammonia" );

    /**
     * Molecules that can be used for collection boxes
     */
    public static final CompleteMolecule[] COLLECTION_BOX_MOLECULES = new CompleteMolecule[] {
            CO2, H2O, N2, CO, O2, H2, NH3, Cl2, NO,
            MoleculeList.getMoleculeByName( "Acetylene" ),
            MoleculeList.getMoleculeByName( "Borane" ),
            MoleculeList.getMoleculeByName( "Trifluoroborane" ),
            MoleculeList.getMoleculeByName( "Chloromethane" ),
            MoleculeList.getMoleculeByName( "Ethylene" ),
            MoleculeList.getMoleculeByName( "Fluorine" ),
            MoleculeList.getMoleculeByName( "Fluoromethane" ),
            MoleculeList.getMoleculeByName( "Formaldehyde" ),
            MoleculeList.getMoleculeByName( "Hydrogen Cyanide" ),
            MoleculeList.getMoleculeByName( "Hydrogen Peroxide" ),
            MoleculeList.getMoleculeByName( "Hydrogen Sulfide" ),
            MoleculeList.getMoleculeByName( "Methane" ),
            MoleculeList.getMoleculeByName( "Nitrous Oxide" ),
            MoleculeList.getMoleculeByName( "Ozone" ),
            MoleculeList.getMoleculeByName( "Phosphine" ),
            MoleculeList.getMoleculeByName( "Silane" ),
            MoleculeList.getMoleculeByName( "Sulfur Dioxide" )
    };

    static {
        // TODO: i18n
        for ( CompleteMolecule m : COLLECTION_BOX_MOLECULES ) {
            assert ( m != null );
        }
    }

    /*---------------------------------------------------------------------------*
    * static helper methods
    *----------------------------------------------------------------------------*/

    /**
     * @param filename File name relative to the sim's data directory
     * @return A list of complete molecules
     */
    private static List<CompleteMolecule> readCompleteMoleculesFromFilename( String filename ) {
        List<CompleteMolecule> result = new ArrayList<CompleteMolecule>();
        try {
            long startTime = System.currentTimeMillis();

            BufferedReader moleculeReader = new BufferedReader( new InputStreamReader( BuildAMoleculeResources.getResourceLoader().getResourceAsStream( filename ) ) );
            try {
                while ( moleculeReader.ready() ) {
                    String line = moleculeReader.readLine();
                    CompleteMolecule molecule = new CompleteMolecule( line );

                    // sanity checks
                    if ( molecule.getMoleculeStructure().hasLoopsOrIsDisconnected() ) {
                        System.out.println( "ignoring molecule: " + molecule.getCommonName() );
                        continue;
                    }
                    if ( molecule.getMoleculeStructure().hasWeirdHydrogenProperties() ) {
                        System.out.println( "weird hydrogen pattern in: " + molecule.getCommonName() );
                        continue;
                    }

                    result.add( molecule );
                }
            }
            finally {
                moleculeReader.close();
            }
            long endTime = System.currentTimeMillis();
            System.out.println( filename + " read in: " + ( endTime - startTime ) + "ms" );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        return result;
    }

    /**
     * @param filename File name relative to the sim's data directory
     * @return A list of molecule structures
     */
    private static List<MoleculeStructure> readMoleculeStructuresFromFilename( String filename ) {
        List<MoleculeStructure> result = new ArrayList<MoleculeStructure>();
        try {
            long startTime = System.currentTimeMillis();

            BufferedReader structureReader = new BufferedReader( new InputStreamReader( BuildAMoleculeResources.getResourceLoader().getResourceAsStream( "structures.txt" ) ) );
            try {
                while ( structureReader.ready() ) {
                    String line = structureReader.readLine();
                    MoleculeStructure structure = MoleculeStructure.fromSerial( line );

                    // sanity checks
                    if ( structure.hasWeirdHydrogenProperties() ) {
                        System.out.println( "weird hydrogen pattern in structure: " + line );
                        continue;
                    }

                    result.add( structure );
                }
            }
            finally {
                structureReader.close();
            }
            long endTime = System.currentTimeMillis();
            System.out.println( filename + " read in: " + ( endTime - startTime ) + "ms" );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
        return result;
    }
}
