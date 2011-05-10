package edu.colorado.phet.molecule;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import edu.colorado.phet.buildamolecule.model.AtomHistogram;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.Pair;

/**
 * NOT PRODUCTION CODE. Turns separate 2d and 3d SDF files into a filtered ready-to-use chemical file
 * TODO: find any that we need to manually add in (those without names?) NO2?
 */
public class MoleculeSDFCombinedParser {

    private static final String SEPARATOR = "|";

    private static String[] desiredProperties = new String[]{
            "PUBCHEM_IUPAC_TRADITIONAL_NAME",
            "PUBCHEM_MOLECULAR_FORMULA", "PUBCHEM_PHARMACOPHORE_FEATURES"

            , "PUBCHEM_IUPAC_OPENEYE_NAME", "PUBCHEM_IUPAC_CAS_NAME", "PUBCHEM_IUPAC_NAME", "PUBCHEM_IUPAC_SYSTEMATIC_NAME"
    };

    private static int maxHeavy = 12; // hard-count of 12
    private static int maxCarbon = 4;

    public static void main( String[] args ) {
        File dir2d = new File( args[0] );
        File dir3d = new File( args[1] );
        File outfile = new File( args[2] );

        assert ( dir2d.exists() );
        assert ( dir3d.exists() );
        assert ( outfile.getParentFile().exists() );

        Set<String> symbols = new HashSet<String>();
        Set<String> propertiesNotOnEveryMolecule = new HashSet<String>();
        Set<String> propertiesUsed = new HashSet<String>();

        int uniqueAcceptedAtoms = 0;
        Set<String> names = new HashSet<String>();

        List<String> molecules = new LinkedList<String>();

        FilteredMoleculeIterator moleculeIterator = new FilteredMoleculeIterator( new MoleculeReader( dir2d, maxHeavy ), new MoleculeReader( dir3d, maxHeavy ) );

        try {

            while ( moleculeIterator.hasNext() ) {
                Pair<MoleculeFile, MoleculeFile> pair = moleculeIterator.next();
                int cid = pair._1.cid;

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

                AtomInfo[] atoms = new AtomInfo[atomCount];
                BondInfo[] bonds = new BondInfo[bondCount];
                Properties moleculeProperties = new Properties();
                boolean hasRings;
                boolean hasName;

                int numCarbon = 0;
                boolean atomCountsOk = true;

                for ( int i = 0; i < atomCount; i++ ) {
                    StringTokenizer t2d = new StringTokenizer( reader2d.readLine(), " " );
                    double x2d = Double.parseDouble( t2d.nextToken() );
                    double y2d = Double.parseDouble( t2d.nextToken() );
                    t2d.nextToken(); // burn it
                    String symbol = t2d.nextToken();
                    symbols.add( symbol );

                    if ( symbol.equals( "C" ) ) {
                        numCarbon++;
                    }
                    if ( !AtomHistogram.ALLOWED_CHEMICAL_SYMBOLS.contains( symbol ) ) {
                        // has something like lead that we are not allowing
                        atomCountsOk = false;
                    }

                    double x3d;
                    double y3d;
                    double z3d;

                    if ( has3d ) {
                        StringTokenizer t3d = new StringTokenizer( reader3d.readLine(), " " );
                        x3d = Double.parseDouble( t3d.nextToken() );
                        y3d = Double.parseDouble( t3d.nextToken() );
                        z3d = Double.parseDouble( t3d.nextToken() );
                    }
                    else {
                        // should be planar, so just transfer over the coordinates
                        x3d = x2d;
                        y3d = y2d;
                        z3d = 0;
                    }
                    atoms[i] = new AtomInfo( x2d, y2d, x3d, y3d, z3d, symbol );
                }

                atomCountsOk = atomCountsOk && numCarbon <= maxCarbon;

                if ( atomCountsOk ) {

                    for ( int i = 0; i < bondCount; i++ ) {
                        StringTokenizer t2d = new StringTokenizer( reader2d.readLine(), " " );
                        if ( has3d ) {
                            reader3d.readLine();
                        }

                        int a = Integer.parseInt( t2d.nextToken() );
                        int b = Integer.parseInt( t2d.nextToken() );
                        int order = Integer.parseInt( t2d.nextToken() );

                        bonds[i] = new BondInfo( a, b, order );
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
                        if ( names.contains( name ) ) {
                            System.out.println( "duplicate name: " + name );
                        }

                        uniqueAcceptedAtoms++;
                        names.add( name );

                        StringBuilder moleculeString = new StringBuilder();
                        moleculeString.append( name + SEPARATOR + formula + SEPARATOR + atomCount + SEPARATOR + bondCount );
                        for ( AtomInfo atom : atoms ) {
                            moleculeString.append( SEPARATOR );
                            moleculeString.append( atom.toString() );
                        }
                        for ( BondInfo bond : bonds ) {
                            moleculeString.append( SEPARATOR );
                            moleculeString.append( bond.toString() );
                        }
                        moleculeString.append( SEPARATOR ).append( cid ); // add the CID on to the end (just for now)
                        moleculeString.append( "\n" );
                        String moleculeResult = moleculeString.toString();
                        molecules.add( moleculeResult );

//                        System.out.print( moleculeResult );
                    }
                }

                reader2d.close();
                if ( has3d ) {
                    reader3d.close();
                }
            }

        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        for ( String symbol : symbols ) {
            System.out.println( "Used symbol: " + symbol );
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

        Map<String, List<String>> formulaMap = new HashMap<String, List<String>>(); // map of formula => list of molecule lines

        for ( final String aString : molecules.toArray( new String[molecules.size()] ) ) {
            final CompleteMolecule completeMolecule = new CompleteMolecule( aString.trim() );
            if ( completeMolecule.getMoleculeStructure().hasLoopsOrIsDisconnected() ) {
                // bad molecule, remove it from consideration!
                molecules.remove( aString );
                System.out.println( "ignoring molecule with loops or disconnected parts: " + completeMolecule.getCommonName() );
            }
            else {
                // good molecule. store it in the map so we can scan for duplicates
                String hillFormula = completeMolecule.getMoleculeStructure().getHillSystemFormulaFragment();
                if ( formulaMap.containsKey( hillFormula ) ) {
                    formulaMap.get( hillFormula ).add( aString );
                }
                else {
                    formulaMap.put( hillFormula, new LinkedList<String>() {{
                        add( aString );
                    }} );
                }
            }
        }

        /*---------------------------------------------------------------------------*
        * toss molecules that are "duplicates", but keep shortest name one
        *----------------------------------------------------------------------------*/

        // for each set of molecules with the same formula
        for ( String hillFormula : formulaMap.keySet() ) {
            List<String> moleculesWithSameFormula = formulaMap.get( hillFormula );

            // pick two of them (not the most efficient)
            for ( String aString : moleculesWithSameFormula ) {
                CompleteMolecule aMol = new CompleteMolecule( aString.trim() );
                for ( String bString : moleculesWithSameFormula ) {
                    if ( !aString.equals( bString ) ) {
                        CompleteMolecule bMol = new CompleteMolecule( bString.trim() );

                        // if they are equivalent, axe the one with the longer name
                        if ( aMol.getMoleculeStructure().isEquivalent( bMol.getMoleculeStructure() ) ) {
                            if ( bMol.getCommonName().length() < aMol.getCommonName().length() ) {
                                molecules.remove( aString );
                                System.out.println( "tossing duplicate " + aMol.getCommonName() );
                            }
                            else {
                                molecules.remove( bString );
                                System.out.println( "tossing duplicate " + bMol.getCommonName() );
                            }
                        }
                    }
                }
            }
        }

        try {
            StringBuilder mainBuilder = new StringBuilder();
            Collections.sort( molecules );
            for ( String molecule : molecules ) {
                mainBuilder.append( molecule );
            }
            FileUtils.writeString( outfile, mainBuilder.toString() );
        }
        catch( IOException e ) {
            e.printStackTrace();
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

    private static class AtomInfo {
        public final double x2d;
        public final double y2d;
        public final double x3d;
        public final double y3d;
        public final double z3d;
        public final String symbol;

        private AtomInfo( double x2d, double y2d, double x3d, double y3d, double z3d, String symbol ) {
            this.x2d = x2d;
            this.y2d = y2d;
            this.x3d = x3d;
            this.y3d = y3d;
            this.z3d = z3d;
            this.symbol = symbol;
        }

        @Override
        public String toString() {
            return symbol + SEPARATOR + x2d + SEPARATOR + y2d + SEPARATOR + x3d + SEPARATOR + y3d + SEPARATOR + z3d;
        }
    }

    private static class BondInfo {
        public final int a;
        public final int b;
        public final int order;

        private BondInfo( int a, int b, int order ) {
            this.a = a;
            this.b = b;
            this.order = order;
        }

        @Override
        public String toString() {
            return a + SEPARATOR + b + SEPARATOR + order;
        }
    }
}
