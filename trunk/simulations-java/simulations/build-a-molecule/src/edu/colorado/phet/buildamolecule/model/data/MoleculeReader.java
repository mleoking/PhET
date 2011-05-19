package edu.colorado.phet.buildamolecule.model.data;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Is able to separate molecules in order from a series of gzip-compressed composite SDF files, with some decently
 * fast filtering
 */
public class MoleculeReader {

    /*---------------------------------------------------------------------------*
    * initial state
    *----------------------------------------------------------------------------*/

    private final int maxHeavy;
    private final List<File> archiveFiles;

    /*---------------------------------------------------------------------------*
    * parsing state
    *----------------------------------------------------------------------------*/

    private BufferedReader reader = null;
    private int nextFileIndex = 0;
    private int countInFile = 0;

    private boolean atStartOfMolecule = true;
    private int cid = 0;
    private boolean exclusionOverride = false; // allow certain CIDs to be included no matter what filtering is above

    // flags
    private boolean ok = true;
    private boolean isotopeMarker = false;
    private boolean heavyMarker = false;
    private boolean componentMarker = false;
    private boolean chargeMarker = false;

    public MoleculeReader( File archiveDir, int maxHeavy ) {
        this.maxHeavy = maxHeavy;

        archiveFiles = new LinkedList<File>( Arrays.asList( archiveDir.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.endsWith( ".sdf.gz" );
            }
        } ) ) );

        Collections.sort( archiveFiles, new Comparator<File>() {
            public int compare( File a, File b ) {
                return a.getName().compareTo( b.getName() );
            }
        } );
    }

    private boolean isOk() {
        return ok || exclusionOverride;
    }

    private void resetMarkerState() {
        atStartOfMolecule = true;
        isotopeMarker = false;
        heavyMarker = false;
        componentMarker = false;
        chargeMarker = false;
    }

    public MoleculeFile nextMoleculeFile() {
        try {
            // implicitly loop through all possible files if necessary to find the next molecule file
            while ( true ) {

                // if the file (reader) isn't open, we will open it
                if ( reader == null ) {
                    openNextFile();
                    if ( reader == null ) {
                        // we have reached the end, so we return null
                        return null;
                    }
                }

                // holds the molecule "file" as it is read in
                ArrayList<String> lines = new ArrayList<String>( 200 );

                while ( reader.ready() ) {
                    String line = reader.readLine();
                    if ( atStartOfMolecule ) {
                        atStartOfMolecule = false;
                        cid = Integer.parseInt( line );
                        exclusionOverride = MoleculeSDFCombinedParser.EXCLUSION_OVERRIDE_CIDS.contains( cid );
                    }

                    // every molecule in the archive ends with this
                    if ( line.equals( "$$$$" ) ) {
                        // separator

                        // reset state so that we can start reading again without problems
                        resetMarkerState();

                        if ( isOk() ) {
                            debug( cid, "OK" );
                            countInFile++;
                            exclusionOverride = false;
                            StringBuilder builder = new StringBuilder();
                            for ( String allLine : lines ) {
                                builder.append( allLine ).append( "\n" );
                            }
                            return new MoleculeFile( cid, builder.toString() );
                            //FileUtils.writeString( new File( "/home/jon/phet/molecules/full3d/" + cid + ".sdf" ), builder.toString() );
                        }
                        else {
                            // if we weren't OK, reset the lines and OK flag
                            lines.clear();
                            ok = true;
                        }
                    }
                    else {
                        if ( isOk() ) {
                            lines.add( line );
                            if ( isotopeMarker ) {
                                isotopeMarker = false;
                                int isotopic = Integer.parseInt( line );
                                if ( isotopic > 0 ) {
                                    ok = false;
                                    debug( cid, "ISO" );
                                }
                            }
                            if ( heavyMarker ) {
                                heavyMarker = false;
                                int heavy = Integer.parseInt( line );
                                if ( heavy > maxHeavy ) {
                                    ok = false;
                                    debug( cid, "HEAVY" );
                                }
                            }
                            if ( componentMarker ) {
                                componentMarker = false;
                                int components = Integer.parseInt( line );
                                if ( components > 1 ) {
                                    ok = false;
                                    debug( cid, "COMPONENTS" );
                                }
                            }
                            if ( chargeMarker ) {
                                chargeMarker = false;
                                if ( !line.trim().equals( "0" ) ) {
                                    ok = false;
                                    debug( cid, "TOTAL CHARGE" );
                                }
                            }
                            if ( line.startsWith( "> <" ) ) {
                                if ( line.equals( "> <PUBCHEM_ISOTOPIC_ATOM_COUNT>" ) ) {
                                    isotopeMarker = true;
                                }
                                if ( line.equals( "> <PUBCHEM_HEAVY_ATOM_COUNT>" ) ) {
                                    heavyMarker = true;
                                }
                                if ( line.equals( "> <PUBCHEM_COMPONENT_COUNT>" ) ) {
                                    componentMarker = true;
                                }
                                if ( line.equals( "> <PUBCHEM_TOTAL_CHARGE>" ) ) {
                                    chargeMarker = true;
                                }
                            }
                        }
                    }
                }

                System.out.println( countInFile + " in file" );

                reader.close();

                reader = null;
            }
        }
        catch( IOException e ) {
            throw new RuntimeException( "molecule reader failure", e );
        }
    }

    private void openNextFile() throws IOException {
        if ( nextFileIndex < archiveFiles.size() ) {
            File file = archiveFiles.get( nextFileIndex );
            nextFileIndex++;

            // reset count in file
            countInFile = 0;

            System.out.println( "processing " + file.getName() + ", " + nextFileIndex + " of " + archiveFiles.size() + ": " );

            reader = new BufferedReader( new InputStreamReader( new GZIPInputStream( new FileInputStream( file ) ) ) );

            atStartOfMolecule = true;
            cid = 0;

            // reset flags
            ok = true;
            isotopeMarker = false;
            heavyMarker = false;
            componentMarker = false;
            chargeMarker = false;
            exclusionOverride = false;
        }
        else {
            // we reached the end
        }
    }

    public static void debug( int cid, String mess ) {
//        System.out.println( mess + ": " + cid );
    }

}