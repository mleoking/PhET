package edu.colorado.phet.common.phetcommon.updates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

/**
 * AbstractVersionChecker is the interface for getting the most up-to-date version information.
 */
public abstract class AbstractVersionChecker {

    /**
     * Gets the most up-to-date version number of a project.
     *
     * @param project
     * @return
     * @throws IOException
     */
    public abstract PhetVersion getVersion( String project, String sim ) throws IOException;

    protected String readURL( String urlLocation ) throws IOException {
        BufferedReader in = new BufferedReader( new InputStreamReader( new URL( urlLocation ).openStream() ) );

        String inputLine;
        String totalText = "";

        while ( ( inputLine = in.readLine() ) != null ) {
            if ( totalText.length() > 0 ) {
                totalText += "\n";
            }
            totalText += inputLine;
        }
        in.close();
        return totalText;
    }

    public static AbstractVersionChecker getDefaultVersionCheckerImplementation() {
        return new XMLVersionChecker();
    }
}
