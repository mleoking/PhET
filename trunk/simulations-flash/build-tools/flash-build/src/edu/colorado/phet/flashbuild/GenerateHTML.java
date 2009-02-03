package edu.colorado.phet.flashbuild;

/*
 A script to be build into a JAR that builds the HTML
 for a single simulation and locale.

 usage:

 java -jar flash-build.jar <simName> <language> [<country> [<deployment> [<distributionTag> [<installationTimestamp> [<installerCreationTimestamp>]]]]]

*/

import edu.colorado.phet.flashbuild.util.FlashHTML;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Feb 1, 2009
 * Time: 4:19:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class GenerateHTML {
    public static void main( String args[] ) {
        if( args.length <= 1 ) {
            System.err.println( "Need at least a sim name and language" );
			System.exit( 1 );
        }

        String simName = args[0];
        String language = args[1];
        String country = "none";
        String deployment = "standalone-jar";
        String distributionTag = "none";
        String installationTimestamp = "none";
        String installerCreationTimestamp = "none";

        // TODO: rewrite to accept named / flag options

        if( args.length > 2 ) {
            country = args[ 2 ];
        }
        if( args.length > 3 ) {
            deployment = args[ 3 ];
        }
        if( args.length > 4 ) {
            distributionTag = args[ 4 ];
        }
        if( args.length > 5 ) {
            installationTimestamp = args[ 5 ];
        }
        if( args.length > 6 ) {
            installerCreationTimestamp = args[ 6 ];
        }

        String locale = FlashHTML.localeString( language, country );

        String simData = "simulations/" + simName + "/data/" + simName + "/";
        String simLocalization = simData + "localization/";

        String commonLocalization = "common/data/localization/";

        String simXMLFile = simLocalization + simName + "-strings_" + locale + ".xml";
        if ( ( new File( simXMLFile ) ).exists() == false ) {
            simXMLFile = simLocalization + simName + "-strings_" + "en" + ".xml";
            System.out.println( "WARNING: could not find sim internationalization data for " + locale + ", defaulting to en" );
        }

        String commonXMLFile = commonLocalization + "common-strings_" + locale + ".xml";
        if( ( new File( commonXMLFile ) ).exists() == false) {
            commonXMLFile = commonLocalization + "common-strings_" + "en" + ".xml";
            System.out.println( "WARNING: could not find common internationalization data for " + locale + ", defaulting to en" );
        }

        String htmlFile = "simulations/" + simName + "/deploy/" + simName + "_" + locale + ".html";
		String propertiesFile = simData + simName + ".properties";

        FlashHTML.writeHTML( simName, language, country, deployment, distributionTag, installationTimestamp,
                installerCreationTimestamp, simXMLFile, htmlFile, propertiesFile, commonXMLFile );
    }
}
