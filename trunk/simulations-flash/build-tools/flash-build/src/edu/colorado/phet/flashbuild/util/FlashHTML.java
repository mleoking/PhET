package edu.colorado.phet.flashbuild.util;

import java.io.*;
import java.util.Scanner;
import java.net.URLEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Feb 1, 2009
 * Time: 4:22:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class FlashHTML {
    private static final String HTML_TEMPLATE = "flash-template.html";
    private static final String NONE = "none";

    // returns true on success
    public static boolean writeHTML( String simName, String language, String country, String deployment,
                                  String distributionTag, String installTimestamp, String simXMLFile,
                                  String htmlFile, String propertiesFile, String commonXMLFile) {
        try {
            String versionMajor = null;
            String versionMinor = null;
            String versionDev = null;
            String versionRevision = null;
            String bgcolor = null;

            // parse the .properties file, store results in variables above
            File propFile = new File(propertiesFile);
            Scanner propScanner = new Scanner(propFile);
            propScanner.useDelimiter("[\n=]");
            while (propScanner.hasNext()) {
                String field = propScanner.next();
                String value = propScanner.next().trim();
                if(field.equals("version.major")) {
                    versionMajor = value;
                } else if(field.equals("version.minor")) {
                    versionMinor = value;
                } else if(field.equals("version.dev")) {
                    versionDev = value;
                } else if(field.equals("version.revision")) {
                    versionRevision = value;
                } else if(field.equals("bgcolor")) {
                    bgcolor = value;
                }
            }
            propScanner.close();

            String encodedSimXML = encodeXML(rawFile(simXMLFile));
            String encodedCommonXML = encodeXML(rawFile(commonXMLFile));

            String html = generateHTML( simName, language, country, deployment, distributionTag, installTimestamp,
                    versionMajor, versionMinor, versionDev, versionRevision, bgcolor, encodedSimXML, encodedCommonXML, "8" );

            // write to file
            FileOutputStream fileOut = new FileOutputStream(htmlFile);
            PrintStream printOut = new PrintStream(fileOut);
            printOut.println(html);
            printOut.close();
        } catch(IOException e) {
            System.out.println("FlashHTML.writeHTML failed with:\n" + e.toString());
            return false;
        }
        return true;
    }
    
    public static String generateHTML( String simName, String language, String country,String deployment,
                                       String distributionTag, String installTimestamp, String versionMajor,
                                       String versionMinor, String versionDev, String versionRevision,
                                       String bgcolor, String encodedSimXML, String encodedCommonXML,
                                       String minimumFlashMajorVersion ) throws IOException {
        String s = "";
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( HTML_TEMPLATE );
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
        String line = bufferedReader.readLine();
        while ( line != null ) {
            s += line;
            line = bufferedReader.readLine();
            if ( line != null ) {
                s += System.getProperty( "line.separator" );
            }
        }

        // get the integer value from the hex of bgcolor
        String bgcolorint = String.valueOf(Integer.parseInt(bgcolor.substring(1), 16));

        String flashVars = "languageCode=@@language@@&countryCode=@@country@@&internationalization=@@encodedSimXML@@" +
                "&commonStrings=@@encodedCommonXML@@&versionMajor=@@versionMajor@@&versionMinor=@@versionMinor@@&" +
                "dev=@@versionDev@@&revision=@@versionRevision@@&simName=@@simName@@&simDeployment=@@deployment@@&" +
                "simDistributionTag=@@distributionTag@@&installTimestamp=@@installTimestamp@@&bgColor=@@bgcolorint@@";

        s = s.replaceAll( "@@flashVars@@", flashVars );

        s = s.replaceAll( "@@simName@@", simName );
        s = s.replaceAll( "@@language@@", language );
        s = s.replaceAll( "@@country@@", country );
        s = s.replaceAll( "@@deployment@@", deployment );
        s = s.replaceAll( "@@distributionTag@@", distributionTag );
        s = s.replaceAll( "@@installTimestamp@@", installTimestamp );
        s = s.replaceAll( "@@versionMajor@@", versionMajor );
        s = s.replaceAll( "@@versionMinor@@", versionMinor );
        s = s.replaceAll( "@@versionDev@@", versionDev );
        s = s.replaceAll( "@@versionRevision@@", versionRevision );
        s = s.replaceAll( "@@bgcolor@@", bgcolor );
        s = s.replaceAll( "@@encodedSimXML@@", encodedSimXML );
        s = s.replaceAll( "@@encodedCommonXML@@", encodedCommonXML );
        s = s.replaceAll( "@@minimumFlashMajorVersion@@", minimumFlashMajorVersion );
        s = s.replaceAll( "@@bgcolorint@@", bgcolorint );
        s = s.replaceAll( "@@locale@@", localeString( language, country) );

        return s;
    }

    public static String localeString( String language, String country ) {
        String ret = language;
        if(country != NONE) {
            ret += "_" + country;
        }
        return ret;
    }

    public static String rawFile( String filename ) throws FileNotFoundException {
        File inFile = new File(filename);
		Scanner scan = new Scanner(inFile);
		scan.useDelimiter("\\Z");
		return scan.next();
    }

    public static String encodeXML( String rawXML ) throws UnsupportedEncodingException {
        return URLEncoder.encode(rawXML, "UTF-8");
    }
}
