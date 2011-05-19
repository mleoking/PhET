package edu.colorado.phet.buildamolecule.model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.ElementHistogram;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.Pair;

/**
 * NOT PRODUCTION CODE. Turns separate 2d and 3d SDF files into a filtered ready-to-use chemical file
 * TODO: find any that we need to manually add in (those without names?) NO2?
 */
public class MoleculeSDFCombinedParser {

    private static final String SEPARATOR = "|";

    private static String[] desiredProperties = new String[] {
            "PUBCHEM_IUPAC_TRADITIONAL_NAME",
            "PUBCHEM_MOLECULAR_FORMULA", "PUBCHEM_PHARMACOPHORE_FEATURES"

            , "PUBCHEM_IUPAC_OPENEYE_NAME", "PUBCHEM_IUPAC_CAS_NAME", "PUBCHEM_IUPAC_NAME", "PUBCHEM_IUPAC_SYSTEMATIC_NAME"
    };

    public static final Set<Integer> EXCLUSION_OVERRIDE_CIDS = new HashSet<Integer>() {{
        add( 3609161 ); // nitrogen dioxide (ion)
    }};

    public static final Set<Integer> COLLECTION_BOX_CIDS = new HashSet<Integer>() {{
        add( 6326 ); // acetylene
        add( 222 ); // ammonia
        add( 6331 ); // borane
        add( 280 ); // CO2
        add( 281 ); // CO
        add( 6327 ); // chloromethane
        add( 6325 ); // ethylene
        add( 11638 ); // fluoromethane
        add( 712 ); // formaldehyde
        add( 768 ); // hydrogen cyanide
        add( 784 ); // hydrogen peroxide
        add( 402 ); // hydrogen sulfide
        add( 297 ); // methane
        add( 24526 ); // molecular chlorine
        add( 24524 ); // molecular fluorine
        add( 783 ); // molecular hydrogen
        add( 947 ); // molecular nitrogen
        add( 977 ); // molecular oxygen
        add( 145068 ); // nitric oxide
        add( 948 ); // nitrous oxide
        add( 24823 ); // ozone
        add( 24404 ); // phosphine
        add( 23953 ); // silane
        add( 1119 ); // sulfur dioxide
        add( 6356 ); // trifluoroborane
        add( 962 ); // water
    }};

    private static final int MAX_NUM_HEAVY_ATOMS = 12; // hard-count of 12
    private static final int MAX_NUM_CARBON = 4;

    public static void main( String[] args ) {
        File dir2d = new File( args[0] );
        File dir3d = new File( args[1] );
        File outDir = new File( args[2] );

        assert ( dir2d.exists() );
        assert ( dir3d.exists() );
        assert ( outDir.exists() );

        Set<String> propertiesNotOnEveryMolecule = new HashSet<String>();
        Set<String> propertiesUsed = new HashSet<String>();

        int uniqueAcceptedAtoms = 0;
        Set<String> names = new HashSet<String>();

        List<CompleteMolecule> otherMolecules = new LinkedList<CompleteMolecule>();
        List<CompleteMolecule> collectionMolecules = new LinkedList<CompleteMolecule>();

        FilteredMoleculeIterator moleculeIterator = new FilteredMoleculeIterator( new MoleculeReader( dir2d, MAX_NUM_HEAVY_ATOMS ), new MoleculeReader( dir3d, MAX_NUM_HEAVY_ATOMS ) );

        try {

            while ( moleculeIterator.hasNext() ) {
                Pair<MoleculeFile, MoleculeFile> pair = moleculeIterator.next();
                int cid = pair._1.cid;

                boolean include2d = COLLECTION_BOX_CIDS.contains( cid );

                BufferedReader reader2d = new BufferedReader( new StringReader( pair._1.content ) );
                BufferedReader reader3d = null;
                boolean has3d = pair._2 != null;
                if ( has3d ) {
                    assert ( pair._1.cid == pair._2.cid );
                    reader3d = new BufferedReader( new StringReader( pair._2.content ) );
                }
                else {
//                    System.out.println( "WARNING: no 3d file for " + cid );
                }

//                System.out.println( "processing #" + cid + ", 3d: " + has3d );

                // burn the 1st three lines
                reader2d.readLine();
                reader2d.readLine();
                reader2d.readLine();
                if ( has3d ) {
                    reader3d.readLine();
                    reader3d.readLine();
                    reader3d.readLine();

                    reader3d.readLine(); // burn it for the duplicated 3d version
                }

                StringTokenizer infoTokenizer = new StringTokenizer( reader2d.readLine(), " " );
                int atomCount = Integer.parseInt( infoTokenizer.nextToken() );
                int bondCount = Integer.parseInt( infoTokenizer.nextToken() );

                CompleteMolecule molecule = new CompleteMolecule( "fake common name", "fake molecular formula", atomCount, bondCount, include2d, has3d );

                Properties moleculeProperties = new Properties();
                boolean hasRings;
                boolean hasName;

                int numCarbon = 0;
                boolean atomCountsOk = true;

                for ( int i = 0; i < atomCount; i++ ) {
                    StringTokenizer t2d = new StringTokenizer( reader2d.readLine(), " " );
                    float x2d = Float.parseFloat( t2d.nextToken() );
                    float y2d = Float.parseFloat( t2d.nextToken() );
                    t2d.nextToken(); // burn it (z coordinate for 2d is always 0)
                    String symbol = t2d.nextToken();

                    if ( symbol.equals( "C" ) ) {
                        numCarbon++;
                    }
                    if ( !ElementHistogram.ALLOWED_CHEMICAL_SYMBOLS.contains( symbol ) ) {
                        // has something like lead that we are not allowing
                        atomCountsOk = false;
                        break; // don't try the element lookup
                    }

                    Element element = Element.getElementBySymbol( symbol );

                    // TODO: isomorphism finding
                    if ( has3d ) {
                        StringTokenizer t3d = new StringTokenizer( reader3d.readLine(), " " );
                        float x3d = Float.parseFloat( t3d.nextToken() );
                        float y3d = Float.parseFloat( t3d.nextToken() );
                        float z3d = Float.parseFloat( t3d.nextToken() );

                        if ( include2d ) {
                            // like a collection box molecule
                            molecule.addAtom( new CompleteMolecule.PubChemAtomFull( element, x2d, y2d, x3d, y3d, z3d ) );
                        }
                        else {
                            // not in collection box, so only provide 3d
                            molecule.addAtom( new CompleteMolecule.PubChemAtom3d( element, x3d, y3d, z3d ) );
                        }
                    }
                    else {
                        // only 2d. we will (for now) fake 3d if necessary
                        molecule.addAtom( new CompleteMolecule.PubChemAtom2d( element, x2d, y2d ) );
                    }
                }

                atomCountsOk = atomCountsOk && numCarbon <= MAX_NUM_CARBON;

                if ( atomCountsOk ) {

                    // we want to read our bonding data out of 3d if possible
                    BufferedReader primaryReader = has3d ? reader3d : reader2d;
                    BufferedReader otherReader = has3d ? reader2d : reader3d;

                    // read in bonds
                    for ( int i = 0; i < bondCount; i++ ) {
                        StringTokenizer t2d = new StringTokenizer( primaryReader.readLine(), " " );
                        if ( otherReader != null ) {
                            otherReader.readLine();
                        }

                        int a = Integer.parseInt( t2d.nextToken() );
                        int b = Integer.parseInt( t2d.nextToken() );
                        int order = Integer.parseInt( t2d.nextToken() );

                        molecule.addBond( new CompleteMolecule.PubChemBond( molecule.getAtoms().get( a - 1 ), molecule.getAtoms().get( b - 1 ), order ) );
                    }

                    // don't read charges or other stuff for now

                    // just fill up our properties
                    if ( has3d ) {
                        funnelProperties( reader3d, moleculeProperties ); // 3d first, so 2d will overwrite if necessary
                    }
                    funnelProperties( reader2d, moleculeProperties );

                    hasRings = has3d && moleculeProperties.getProperty( "PUBCHEM_PHARMACOPHORE_FEATURES" ).contains( "rings" );
                    hasName = moleculeProperties.getProperty( "PUBCHEM_IUPAC_TRADITIONAL_NAME" ) != null;

                    propertiesUsed.addAll( moleculeProperties.stringPropertyNames() );

                    for ( String key : desiredProperties ) {
                        if ( moleculeProperties.containsKey( key ) ) {
                            //System.out.println( key + ": " + moleculeProperties.getProperty( key ) );
                        }
                        else {
                            //System.out.println( cid + " missing " + key );
                            propertiesNotOnEveryMolecule.add( key );
                        }
                    }

                    if ( hasName && !hasRings && cid != 139247 && cid != 9561073 ) { // blacklist
                        // we will accept it
                        String name = moleculeProperties.getProperty( "PUBCHEM_IUPAC_TRADITIONAL_NAME" );
                        String formula = moleculeProperties.getProperty( "PUBCHEM_MOLECULAR_FORMULA" );
                        molecule.setCommonName( name );
                        molecule.setMolecularFormula( formula );
                        molecule.cid = cid;
                        if ( names.contains( name ) ) {
                            System.out.println( "duplicate name: " + name );
                        }

                        uniqueAcceptedAtoms++;
                        names.add( name );

                        // actually store it for now
                        if ( COLLECTION_BOX_CIDS.contains( cid ) ) {
                            collectionMolecules.add( molecule );
                            System.out.println( molecule.toSerial2() );
                        }
                        else {
                            otherMolecules.add( molecule );
                        }
                    }
                }

                reader2d.close();
                if ( has3d ) {
                    reader3d.close();
                }
            }

        }
        catch ( IOException e ) {
            e.printStackTrace();
        }

        for ( String key : propertiesNotOnEveryMolecule ) {
            System.out.println( "At least one missed property: " + key );
        }
        for ( String key : desiredProperties ) {
            if ( !propertiesNotOnEveryMolecule.contains( key ) ) {
                System.out.println( "Every one had property: " + key );
            }
        }
        System.out.println( "uniqueAcceptedAtoms: " + uniqueAcceptedAtoms );
        System.out.println( "unique names: " + names.size() );

        /*---------------------------------------------------------------------------*
        * filter molecules by validity, and group together isomers so we can filter out duplicates later
        *----------------------------------------------------------------------------*/

        Map<String, List<CompleteMolecule>> formulaMap = new HashMap<String, List<CompleteMolecule>>(); // map of formula => list of molecule lines

        for ( final CompleteMolecule completeMolecule : otherMolecules.toArray( new CompleteMolecule[otherMolecules.size()] ) ) {
            if ( completeMolecule.hasLoopsOrIsDisconnected() ) {
                // bad molecule, remove it from consideration!
                otherMolecules.remove( completeMolecule );
                System.out.println( "ignoring molecule with loops or disconnected parts: " + completeMolecule.getCommonName() );
            }
            else {
                // good molecule. store it in the map so we can scan for duplicates
                String hillFormula = completeMolecule.getHillSystemFormulaFragment();
                if ( formulaMap.containsKey( hillFormula ) ) {
                    formulaMap.get( hillFormula ).add( completeMolecule );
                }
                else {
                    formulaMap.put( hillFormula, new LinkedList<CompleteMolecule>() {{
                        add( completeMolecule );
                    }} );
                }
            }
        }

        /*---------------------------------------------------------------------------*
        * toss molecules that are "duplicates", but keep shortest name one
        *----------------------------------------------------------------------------*/

        // for each set of molecules with the same formula
        for ( String hillFormula : formulaMap.keySet() ) {
            List<CompleteMolecule> moleculesWithSameFormula = formulaMap.get( hillFormula );

            // pick two of them (not the most efficient)
            for ( CompleteMolecule aMol : moleculesWithSameFormula ) {
                for ( CompleteMolecule bMol : moleculesWithSameFormula ) {
                    if ( !aMol.equals( bMol ) ) {

                        // if they are equivalent, axe the one with the longer name
                        if ( aMol.isEquivalent( bMol ) ) {
                            if ( bMol.getCommonName().length() < aMol.getCommonName().length() ) {
                                otherMolecules.remove( aMol );
                                System.out.println( "tossing duplicate " + aMol.getCommonName() );
                            }
                            else {
                                otherMolecules.remove( bMol );
                                System.out.println( "tossing duplicate " + bMol.getCommonName() );
                            }
                        }
                    }
                }
            }
        }

        /*---------------------------------------------------------------------------*
        * output files
        *----------------------------------------------------------------------------*/

        writeMoleculesToFile( collectionMolecules, new File( outDir, "collection-molecules.txt" ) );
        writeMoleculesToFile( otherMolecules, new File( outDir, "other-molecules.txt" ) );

    }

    public static void writeMoleculesToFile( List<CompleteMolecule> molecules, File file ) {
        try {
            List<String> moleculeStrings = new ArrayList<String>( molecules.size() );
            for ( CompleteMolecule molecule : molecules ) {
                moleculeStrings.add( molecule.toSerial2() );
            }
            StringBuilder builder = new StringBuilder();
            Collections.sort( moleculeStrings );
            for ( String molecule : moleculeStrings ) {
                builder.append( molecule ).append( "\n" );
            }
            System.out.println( "writing molecules to: " + file.getAbsolutePath() );
            FileUtils.writeString( file, builder.toString() );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public static void funnelProperties( BufferedReader reader, Properties properties ) throws IOException {
        String key = null;
        String value = "";
        while ( reader.ready() ) {
            String line = reader.readLine();
            if ( line == null ) {
                // TODO: why do we need this? is reader.ready() returning true for StringReader?
                return;
            }
            if ( line.startsWith( "> <" ) ) {
                if ( key != null ) {
                    properties.put( key, value );
                }
                key = line.substring( 3, line.length() - 1 );
                value = "";
            }
            else {
                if ( line.trim().length() > 0 ) {
                    if ( value.length() > 0 ) {
                        value += "\n";
                    }
                    value += line;
                }
            }
        }
    }
}
