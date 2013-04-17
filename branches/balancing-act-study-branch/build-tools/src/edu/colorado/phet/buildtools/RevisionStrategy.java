// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools;

import java.io.File;
import java.util.StringTokenizer;

import edu.colorado.phet.buildtools.util.ProcessOutputReader;

/**
 * @author Sam Reid
 */
public interface RevisionStrategy {

    int getRevision();

    public static class DynamicRevisionStrategy implements RevisionStrategy {
        private final File trunk;

        public DynamicRevisionStrategy( File trunk ) {
            this.trunk = trunk;
        }

        public int getRevision() {
            AuthenticationInfo auth = BuildLocalProperties.getInstance().getRespositoryAuthenticationInfo();
            File readmeFile = new File( trunk, "README.txt" );
            if ( !readmeFile.exists() ) {
                throw new RuntimeException( "Readme file doesn't exist, need to get version info some other way" );
            }
            String[] args = new String[] { "svn", "status", "-u", "--non-interactive", "--username", auth.getUsername(), "--password", auth.getPassword(), readmeFile.getAbsolutePath() };
            ProcessOutputReader.ProcessExecResult output = ProcessOutputReader.exec( args );
            StringTokenizer st = new StringTokenizer( output.getOut(), "\n" );
            while ( st.hasMoreTokens() ) {
                String token = st.nextToken();
                String key = "Status against revision:";
                if ( token.toLowerCase().startsWith( key.toLowerCase() ) ) {
                    String suffix = token.substring( key.length() ).trim();
                    return Integer.parseInt( suffix );
                }
            }
            String cmd = "";
            for ( String arg : args ) {
                cmd += " " + arg;
            }
//        System.out.println("Failed on command: "+cmd.trim());
            throw new RuntimeException( "No svn version information found: " + output.getOut() );
        }
    }

    public static class ConstantRevisionStrategy implements RevisionStrategy {

        private int revision;

        public ConstantRevisionStrategy( int revision ) {
            this.revision = revision;
        }

        public int getRevision() {
            return revision;
        }

    }
}
