// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.reports;

import java.io.File;
import java.util.StringTokenizer;

import edu.colorado.phet.buildtools.PhetProject;

/**
 * @author Sam Reid
 */
public class CheckLatestDeployedVersion {
    public static void main( String[] args ) {
        PhetProject[] sims = PhetProject.getAllSimulationProjects( new File( "C:\\workingcopy\\phet\\svn\\trunk" ) );
        for ( PhetProject sim : sims ) {
            String text = sim.getChangesText();
            StringTokenizer st = new StringTokenizer( text, "\n" );
            while ( st.hasMoreTokens() ) {
                ParsedLine p = parseLine( st.nextToken() );
                if ( p != null && p.isProdVersion() ) {
                    int fixVersion = 49269;

                    boolean fail = p.getSVNNumber() <= fixVersion;
                    if ( fail ) {
                        System.out.println( sim.getName() + ": " + p.getSVNNumber() + " " + ( fail ? ": FAIL" : "" ) );
                    }

                    break;
                }
            }
        }
    }

    private static ParsedLine parseLine( String line ) {
        if ( line.trim().startsWith( "# " ) ) {

            try {
                //# 00.69.00 (44133) Sep 16, 2010
                StringTokenizer st = new StringTokenizer( line, " " );
                String pound = st.nextToken();
                String version = st.nextToken();
                String svnNumber = st.nextToken();
                return new ParsedLine( version, svnNumber.substring( 1, svnNumber.length() - 1 ) );

            }
            catch ( Exception e ) {
                return null;
            }
        }
        return null;
    }

    private static class ParsedLine {
        private final String version;
        private final String substring;

        public ParsedLine( String version, String substring ) {
            this.version = version;
            this.substring = substring;
        }

        @Override public String toString() {
            return "ParsedLine{" +
                   "version='" + version + '\'' +
                   ", substring='" + substring + '\'' +
                   '}';
        }

        public boolean isProdVersion() {
            return version.substring( version.lastIndexOf( '.' ) + 1 ).equals( "00" );
        }

        public int getSVNNumber() {
            return Integer.parseInt( substring );
        }
    }
}
