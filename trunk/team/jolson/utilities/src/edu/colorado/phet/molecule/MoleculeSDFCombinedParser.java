package edu.colorado.phet.molecule;

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import edu.colorado.phet.buildtools.util.FileUtils;

/**
 * NOT PRODUCTION CODE. Turns separate 2d and 3d SDF files into a filtered ready-to-use chemical file
 */
public class MoleculeSDFCombinedParser {

    private static final String SEPARATOR = "|";

    private static String[] desiredProperties = new String[] {
            "PUBCHEM_IUPAC_TRADITIONAL_NAME",
            "PUBCHEM_MOLECULAR_FORMULA", "PUBCHEM_PHARMACOPHORE_FEATURES"

            , "PUBCHEM_IUPAC_OPENEYE_NAME", "PUBCHEM_IUPAC_CAS_NAME", "PUBCHEM_IUPAC_NAME", "PUBCHEM_IUPAC_SYSTEMATIC_NAME"
    };

    public static void main( String[] args ) {
        File moleculeDir = new File( args[0] );

        assert ( moleculeDir.exists() );

        File dir2d = new File( moleculeDir, "2d" );
        File dir3d = new File( moleculeDir, "3d" );

        Set<String> symbols = new HashSet<String>();
        Set<String> propertiesNotOnEveryMolecule = new HashSet<String>();
        Set<String> propertiesUsed = new HashSet<String>();

        int uniqueAcceptedAtoms = 0;
        Set<String> names = new HashSet<String>();

        StringBuilder moleculeString = new StringBuilder();

        try {

            for ( File file : dir2d.listFiles( new FilenameFilter() {
                public boolean accept( File dir, String name ) {
                    return name.endsWith( ".sdf" );
                }
            } ) ) {
                int cid = Integer.parseInt( file.getName().substring( 0, file.getName().indexOf( "." ) ) );

//                System.out.println( "Processing #" + cid );

                BufferedReader reader2d = new BufferedReader( new FileReader( file ) );
                BufferedReader reader3d = new BufferedReader( new FileReader( new File( dir3d, file.getName() ) ) );

                // burn the 1st three lines
                reader2d.readLine();
                reader2d.readLine();
                reader2d.readLine();
                reader3d.readLine();
                reader3d.readLine();
                reader3d.readLine();

                reader3d.readLine(); // burn it for the duplicated 3d version
                StringTokenizer infoTokenizer = new StringTokenizer( reader2d.readLine(), " " );
                int atomCount = Integer.parseInt( infoTokenizer.nextToken() );
                int bondCount = Integer.parseInt( infoTokenizer.nextToken() );

                AtomInfo[] atoms = new AtomInfo[atomCount];
                BondInfo[] bonds = new BondInfo[bondCount];
                Properties moleculeProperties = new Properties();
                boolean hasRings;
                boolean hasName;

                for ( int i = 0; i < atomCount; i++ ) {
                    StringTokenizer t2d = new StringTokenizer( reader2d.readLine(), " " );
                    StringTokenizer t3d = new StringTokenizer( reader3d.readLine(), " " );
                    double x2d = Double.parseDouble( t2d.nextToken() );
                    double y2d = Double.parseDouble( t2d.nextToken() );
                    t2d.nextToken(); // burn it
                    double x3d = Double.parseDouble( t3d.nextToken() );
                    double y3d = Double.parseDouble( t3d.nextToken() );
                    double z3d = Double.parseDouble( t3d.nextToken() );
                    String symbol = t2d.nextToken();
                    symbols.add( symbol );
                    atoms[i] = new AtomInfo( x2d, y2d, x3d, y3d, z3d, symbol );
                }

//                for ( AtomInfo atom : atoms ) {
//                    System.out.println( atom );
//                }

                for ( int i = 0; i < bondCount; i++ ) {
                    StringTokenizer t2d = new StringTokenizer( reader2d.readLine(), " " );
                    reader3d.readLine();

                    int a = Integer.parseInt( t2d.nextToken() );
                    int b = Integer.parseInt( t2d.nextToken() );
                    int order = Integer.parseInt( t2d.nextToken() );

                    bonds[i] = new BondInfo( a, b, order );
                }

//                for ( BondInfo bond : bonds ) {
//                    System.out.println( bond );
//                }

                // don't read charges or other stuff for now

                // just fill up our properties
                funnelProperties( reader3d, moleculeProperties ); // 3d first, so 2d will overwrite if necessary
                funnelProperties( reader2d, moleculeProperties );

                hasRings = moleculeProperties.getProperty( "PUBCHEM_PHARMACOPHORE_FEATURES" ).contains( "rings" );
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

                reader2d.close();
                reader3d.close();

                if ( hasName && !hasRings && cid != 139247 && cid != 9561073 ) { // blacklist
                    // we will accept it
                    String name = moleculeProperties.getProperty( "PUBCHEM_IUPAC_TRADITIONAL_NAME" );
                    String formula = moleculeProperties.getProperty( "PUBCHEM_MOLECULAR_FORMULA" );
                    if ( names.contains( name ) ) {
                        System.out.println( "duplicate name: " + name );
                    }

                    uniqueAcceptedAtoms++;
                    names.add( name );

                    moleculeString.append( name + SEPARATOR + formula + SEPARATOR + atomCount + SEPARATOR + bondCount );
                    for ( AtomInfo atom : atoms ) {
                        moleculeString.append( SEPARATOR );
                        moleculeString.append( atom.toString() );
                    }
                    for ( BondInfo bond : bonds ) {
                        moleculeString.append( SEPARATOR );
                        moleculeString.append( bond.toString() );
                    }
                    moleculeString.append( "\n" );
                }
            }

        }
        catch ( IOException e ) {
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

        try {
            FileUtils.writeString( new File( moleculeDir, "molecules.txt" ), moleculeString.toString() );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void funnelProperties( BufferedReader reader, Properties properties ) throws IOException {
        String key = null;
        String value = "";
        while ( reader.ready() ) {
            String line = reader.readLine();
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
