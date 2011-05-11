//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeApplication;
import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;

public class MoleculeList {
    private static MoleculeList instance = null;
    private static Thread computeThread = null;
    public static Random random = new Random( System.currentTimeMillis() );

    public static synchronized void startInitialization() {
        computeThread = new Thread() {
            @Override public void run() {


                System.out.println( "Completed MoleculeList initialization" );
            }
        };
        System.out.println( "Starting MoleculeList initialization" );
        computeThread.start();
    }

    public static synchronized MoleculeList getInstance() {
        if ( instance == null ) {
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

        return instance;
    }

    /**
     * Check whether this structure is allowed. Currently this means it is a "sub-molecule" of one of our complete
     * molecules
     *
     * @param moleculeStructure Molecule to check
     * @return True if it is allowed
     */
    public static boolean isAllowedStructure( MoleculeStructure moleculeStructure ) {
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
        if ( allowedStructureMap.containsKey( hashString ) ) {
            List<StrippedMolecule> moleculeStructures = allowedStructureMap.get( hashString );
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
        for ( CompleteMolecule completeMolecule : completeMolecules ) {
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

    private static List<CompleteMolecule> completeMolecules = new ArrayList<CompleteMolecule>(); // all complete molecules
    private static Map<String, CompleteMolecule> moleculeMap = new HashMap<String, CompleteMolecule>(); // map from unique name => complete molecule

    // maps to allow us to efficiently look things up by molecular formula. since we allow isomers, multiple structures can have the same formula.
    private static Map<String, List<CompleteMolecule>> completeMoleculeMap = new HashMap<String, List<CompleteMolecule>>();
    private static Map<String, List<StrippedMolecule>> allowedStructureMap = new HashMap<String, List<StrippedMolecule>>();

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
        final StrippedMolecule strippedMolecule = new StrippedMolecule( structure );
        String hashString = strippedMolecule.stripped.getHistogram().getHashString();
        if ( allowedStructureMap.containsKey( hashString ) ) {
            allowedStructureMap.get( hashString ).add( strippedMolecule );
        }
        else {
            allowedStructureMap.put( hashString, new LinkedList<StrippedMolecule>() {{
                add( strippedMolecule );
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

                    // sanity checks
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
                    //addAllowedStructure( molecule.getMoleculeStructure() );
                }
            }
            finally {
                moleculeReader.close();
            }
            long b = System.currentTimeMillis();
            System.out.println( "molecules read in: " + ( b - a ) + "ms" );

            /*---------------------------------------------------------------------------*
            * read our allowed structures
            *----------------------------------------------------------------------------*/

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

    public static CompleteMolecule pickRandomCollectionBoxMolecule() {
        if ( BuildAMoleculeApplication.allowGenerationWithAllMolecules.get() ) {
            return completeMolecules.get( random.nextInt( completeMolecules.size() ) );
        }
        else {
            return CompleteMolecule.COLLECTION_BOX_MOLECULES[random.nextInt( CompleteMolecule.COLLECTION_BOX_MOLECULES.length )];
        }
    }
}
