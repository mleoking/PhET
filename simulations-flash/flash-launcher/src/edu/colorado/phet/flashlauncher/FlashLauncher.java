/* Copyright 2008, University of Colorado */

package edu.colorado.phet.flashlauncher;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import javax.swing.*;

import edu.colorado.phet.flashlauncher.util.BareBonesBrowserLaunch;
import edu.colorado.phet.flashlauncher.util.FileUtils;

/**
 * FlashLauncher is the mechanism for launching Flash simulations.
 * It is bundled into a JAR file as the mainclass.
 * An args file in the JAR identifies the simulation properties
 * which are used to fill in an HTML template.
 * The JAR file is unzipped to a temporary directory.
 * A web browser is launched, and pointed at the HTML.
 *
 * @author Sam Reid
 *         <p/>
 *         Modified by Jonathan Olson to support the new HTML generation for Flash simulations
 */
public class FlashLauncher {

    public static final String ARGS_FILENAME = "flash-launcher-args.txt";
    public static final String SIMULATION_PROPRTIES_FILENAME = "simulation.properties";
    public static final String SOFTWARE_AGREEMENT_FILENAME = "software-agreement.htm";

    private String projectName;
    private String simName;
    private String language;
    private String country;
    private String deployment;
    private String distributionTag;
    private String installationTimestamp;
    private String installerCreationTimestamp;
    private static JTextArea jTextArea;
    public static final String PROPERTY_PROJECT = "project";
    public static final String PROPERTY_SIMULATION = "simulation";
    public static final String PROPERTY_LANGUAGE = "language";
    public static final String PROPERTY_COUNTRY = "country";
    public static final String PROPERTY_TYPE = "type";

    public FlashLauncher() throws IOException {

        // TODO: pull this constant string from somewhere else? should be constant for standalone-jars (all FlashLauncher)
        this.deployment = "standalone-jar";
        this.installationTimestamp = "null";
        this.installerCreationTimestamp = "null";
        this.distributionTag = "null";

        // read sim and language from args file (JAR resource)        
        Properties properties =new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream( SIMULATION_PROPRTIES_FILENAME ));
        this.projectName=properties.getProperty(PROPERTY_PROJECT);
        this.simName=properties.getProperty(PROPERTY_SIMULATION);
        this.language=properties.getProperty(PROPERTY_LANGUAGE);
        this.country=properties.getProperty(PROPERTY_COUNTRY);

        // if the developer flag is specified in args file, open a window to show debug output
        if ( "true".equals(properties.getProperty("dev")) ) {
            println( "FlashLauncher.FlashLauncher dev" );
            JFrame frame = new JFrame( "Text" );
            jTextArea = new JTextArea( 10, 50 );
            frame.setContentPane( new JScrollPane( jTextArea ) );
            frame.setVisible( true );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            frame.setSize( 800, 600 );
        }
    }

    /**
     * Prints to the debug window, if it exists.
     */
    public static void println( String string ) {
        System.out.println( string );
        if ( jTextArea != null ) {
            jTextArea.append( string + "\n" );
        }
    }

    /*
     * Launches the simulation in a web browser.
     */

    private void start( String args[] ) throws IOException {

        println( "FlashLauncher.start" );
        String unzipDirName = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" ) + "phet-" + simName;
        println( "unzipping to directory = " + unzipDirName );
        File unzipDir = new File( unzipDirName );

        // unzip the JAR into temp directory
        File jarfile = getJARFile();
        println( "jarfile = " + jarfile );
        println( "Starting unzip jarfile=" + jarfile + ", unzipDir=" + unzipDir );
        FileUtils.unzip( jarfile, unzipDir );
        println( "Finished unzip" );

        // read the properties file
        Properties properties = readProperties( projectName );

        // pull the version information from the properties file
        String versionMajor, versionMinor, versionDev, versionRevision, versionTimestamp;
        versionMajor = properties.getProperty( "version.major" );
        versionMinor = properties.getProperty( "version.minor" );
        versionDev = properties.getProperty( "version.dev" );
        versionRevision = properties.getProperty( "version.revision" );
        versionTimestamp = properties.getProperty( "version.timestamp" );

        if ( properties.getProperty( "distribution.tag" ) != null ) {
            distributionTag = properties.getProperty( "distribution.tag" );
        }

        if ( simName.equals( "flash-common-strings" ) ) {
            String displayString = "";
            displayString += "PhET Flash common strings : version ";
            displayString += versionMajor + "." + versionMinor + "." + versionDev + " (" + versionRevision + ")";
            displayString += "\n\n";
            displayString += "This JAR file contains common strings used by all PhET Flash simulations.\n" +
                             "You can use this JAR file to translate common strings with Translation Utility.\n" +
                             "But testing those translations is not currently supported.";
            JOptionPane.showMessageDialog( null, displayString );
            return;
        }

        // read the background color property
        String bgcolor = readBackgroundColor( properties );


        String simDev = "false";

        if ( !versionDev.equals( "0" ) && !versionDev.equals( "00" ) ) {
            simDev = "true";
        }

        for ( int i = 0; i < args.length; i++ ) {
            if ( args[i].equals( "-dev" ) ) {
                simDev = "true";
            }
        }


        // get the locale string
        String locale = FlashHTML.localeString( language, country );

        File simXMLFile = getSimXMLFile( unzipDir, locale );

        File commonXMLFile = new File( unzipDir, "common-strings_" + locale + ".xml" );
        if ( !commonXMLFile.exists() ) {
            commonXMLFile = new File( unzipDir, "common-strings_en.xml" );
            println( "WARNING: could not find common strings for " + locale + ", using default en." );
        }

        String creditsString = FlashHTML.rawFile( new File( unzipDir, "credits.txt" ) );

        // encoded XML for sim and common strings
        String simEncodedXML = FlashHTML.encodeXMLFile( simXMLFile );
        String commonEncodedXML = FlashHTML.encodeXMLFile( commonXMLFile );

        Properties agreementProperties = readProperties( "software-agreement" );

        String agreementVersion = agreementProperties.getProperty( "version" );
//        String agreementContent = agreementProperties.getProperty( "content" );
        //String agreementContent = "<p align=\"center\"><font size=\"12\"><b>PhET SOFTWARE AGREEMENT</b></font><br><font size=\"10\">University of Colorado</font></p><br><font size=\"10\">In this document:<br>&nbsp;&nbsp;I.&nbsp;&nbsp;Software License Options<br>&nbsp;&nbsp;&nbsp;&nbsp;A.&nbsp;&nbsp;Option A: Creative Commons - Attribution (executables only)<br>&nbsp;&nbsp;&nbsp;&nbsp;B.&nbsp;&nbsp;Option B: Creative Commons - GNU GPL (executables and source code).<br>&nbsp;&nbsp;&nbsp;&nbsp;C.&nbsp;&nbsp;Attribution<br>&nbsp;&nbsp;&nbsp;&nbsp;D.&nbsp;&nbsp;Donations<br>&nbsp;&nbsp;&nbsp;&nbsp;E.&nbsp;&nbsp;Third Party Software Credits<br>&nbsp;&nbsp;&nbsp;&nbsp;F.&nbsp;&nbsp;Alternative Licensing Options<br>&nbsp;&nbsp;II.&nbsp;&nbsp;Privacy Policy<br>&nbsp;&nbsp;&nbsp;&nbsp;A.&nbsp;&nbsp;Commitment to Individual Privacy<br>&nbsp;&nbsp;&nbsp;&nbsp;B.&nbsp;&nbsp;Information Collected<br>&nbsp;&nbsp;&nbsp;&nbsp;C.&nbsp;&nbsp;Information Security<br>&nbsp;&nbsp;&nbsp;&nbsp;D.&nbsp;&nbsp;Information Sharing<br>&nbsp;&nbsp;&nbsp;&nbsp;E.&nbsp;&nbsp;Colorado Open Records Act<br>&nbsp;&nbsp;&nbsp;&nbsp;F.&nbsp;&nbsp;Setting User Preferences<br>&nbsp;&nbsp;III.&nbsp;&nbsp;Disclaimer<br>&nbsp;&nbsp;IV.&nbsp;&nbsp;Contact Us<br><br><font size=\"12\"><b><u>I. SOFTWARE LICENSE OPTIONS</u></b></font><br><br>The PhET Interactive Simulations Project at the University of Colorado (PhET) distributes these simulations under the Creative Commons-Attribution 3.0 license and the Creative Commons GNU General Public License. The user is responsible for choosing which of the following two licensing options will govern their use of these simulations.<br><br>Both license options require attributing the work to:<br>PhET Interactive Simulations<br>University of Colorado<br><a href=\"http://phet.colorado.edu\">http://phet.colorado.edu</a>.<br><br>If your use includes redistribution of the simulations, please let us know! This type of information is very useful when writing proposals for future funding.<br><br>Please consider contributing a tax-deductible donation to PhET to help keep the project going.<br><br>Contact PhET at <a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>A. <u>OPTION A: CREATIVE COMMONS - ATTRIBUTION</u></b></font><br><br>PhET releases the <b>executable</b> versions of all simulations (the *.jar files, the *.jnlp files, the *.swf files) under the Creative Commons - Attribution (CC-BY) license with the exception of the following simulations which are only released under CC - GNU GPL:<br>Bound States Simulation<br>Quantum Tunneling Simulation<br>and any additional simulations listed at <a href=\"http://phet.colorado.edu/about/licensing.php\">http://phet.colorado.edu/about/licensing.php</a>.<br><br>PhET Interactive Simulations by <a href=\"http://phet.colorado.edu/index.php\">PhET, University of Colorado</a> is licensed under a <a href=\"http://creativecommons.org/licenses/by/3.0/us/\">Creative Commons Attribution 3.0 United States License</a>.<br><br><i>What does this mean?</i><br>The interactive simulations developed by PhET Interactive Simulations (with exceptions listed above) may be freely used and/or redistributed by third parties (e.g. students, educators, school districts, museums, publishers, vendors, etc.). Non-commercial or commercial use is allowed. All uses require attribution of the work as described in the ATTRIBUTION section.<br>Parties wishing to modify the source code must use Option B.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>B. <u>OPTION B: CREATIVE COMMONS - GNU GENERAL PUBLIC LICENSE</u></b></font><br><br>PhET releases both the <b>source code</b> and <b>executable</b> versions of all simulations under the Creative Commons - GNU General Public License (CC-GNU GPL).<br><br>PhET Interactive Simulations by <a href=\"http://phet.colorado.edu/index.php\">PhET, University of Colorado</a> is licensed under a <a href=\"http://creativecommons.org/licenses/GPL/2.0/\">Creative Commons GNU General Public License</a>.<br><br><i>What does this mean?</i><br>The interactive simulations developed by PhET Interactive Simulations may be freely used and/or redistributed by third parties (e.g. students, educators, school districts, museums, publishers, vendors, etc.). Non-commercial or commercial use is allowed. All uses require attribution of the work as described in the ATTRIBUTION section.<br>In addition, the source code for all PhET simulations is available for use and/or modification from <a href=\"https://phet.unfuddle.com/projects/9404/repositories/23262/browse/head/trunk\">Unfuddle</a> (login with username: guest and password: guest). The latest version can always be found there. Anyone can have access to the source code and make changes in it. The source code for any changes someone makes to the software must, in turn, be made publicly available by the party that makes the changes.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>C. <u>ATTRIBUTION</u></b></font><br><br>Both license options require attributing the work to:<br>PhET Interactive Simulations<br>University of Colorado<br><a href=\"http://phet.colorado.edu\">http://phet.colorado.edu</a><br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>D. <u>DONATIONS</u></b></font><br><br>PhET needs ongoing donations and grant funding in order to support the project, continue the development of new simulations, and maintain existing simulations.<br><br>If you find these simulations useful, and especially if your use is commercial, please consider contributing a tax-deductible donation to PhET to help keep the project going. For more information, email <a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a> or visit <a href=\"http://phet.colorado.edu/contribute/index.php\">http://phet.colorado.edu/contribute/index.php</a>.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>E. <u>THIRD PARTY SOFTWARE CREDITS</u></b></font><br><br>PhET's simulations use third-party software. A complete list of third-party software used, the developers, and the associated licenses is available from within each simulation. In Java, from the Help -> About menu, choose the Credits button. In Flash, open the dialog using the About button, and then choose the credits button.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>F. <u>ALTERNATIVE LICENSING OPTIONS</u></b></font><br><br>Permissions beyond the scope of this license may be available at <a href=\"http://phet.colorado.edu\">http://phet.colorado.edu</a>, or by contacting us at <a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>.<br><br><font size=\"12\"><b><u>II. PRIVACY POLICY</u></b></font><br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>A. <u>COMMITMENT TO INDIVIDUAL PRIVACY</u></b></font><br><br>The University of Colorado and the PhET Project are committed to the protection of individual privacy, and ensuring the confidentiality of information provided by its employees, students, visitors, and resource users.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>B. <u>INFORMATION COLLECTED</u></b></font><br><br>In order to document the amount of use of PhET simulations and to provide the best services to our users, PhET collects a minimal amount of non-personally identifying information upon each simulation start-up (e.g. sim version, operating system, java version, ...).<br><br>When the simulation is launched online from http://phet.colorado.edu, information is collected via standard website statistics services.<br><br>When the simulation is launched from another source (e.g. a downloaded simulation file), a small message is sent to PhET as the simulation starts up. You can disable sending this information (see USER PREFERENCES below).<br><br>A complete list of the information sent to PhET is available in the simulations. For Java simulations, go to Files->Preferences, select the Privacy tab, and click on the \"information\" link. For Flash simulations, go to the Preferences button and click on the \"information\" link in the Privacy section.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>C. <u>INFORMATION SECURITY</u></b></font><br><br>The PhET Project has in place appropriate security measures to protect against the unauthorized use or access of its data. Such measures include internal review of data collection, storage, and processing practices and security measures, as well as physical security measures to guard against unauthorized access to systems where data is stored.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>D. <u>INFORMATION SHARING</u></b></font><br><br>Access to information collected through the use of PhET simulations is limited to those employees, contractors, and agents who need to know that information in order to operate, develop, or improve our services. The PhET Project may report a summary of this information (in aggregate) to other organizations and individuals for grant reporting purposes, or in published articles to document PhET's wide-spread use. No personally-identifying information is collected nor distributed. The PhET Project requires third parties to whom it discloses information to protect the information in accordance with this policy and applicable law.  In addition, the PhET Project may disclose information to third parties when such disclosure is required or permitted by law.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>E. <u>COLORADO OPEN RECORDS ACT</u></b></font><br><br>The University of Colorado and the PhET project are subject to the Colorado Open Records Act, C.R.S � 24-72-101 et seq. The Colorado Open Records Act requires that all records maintained by the University and the PhET Project be available for public inspection except as otherwise provided by law.  Non-personal identifying information collected by the PhET Project may be subject to public inspection and copying unless protected by state or federal law.<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"12\"><b>F. <u>SETTING USER PREFERENCES</u></b></font><br><br>For simulations launched online from http://phet.colorado.edu, no preferences options are available because the simulation sends no start-up message to PhET.<br><br>When launching PhET simulations from any other location (e.g. a downloaded offline version), users may choose to disable the feature which sends the information message to the PhET Project.<br><br>Instructions for Java simulations: From the File->Preferences menu, choose the Privacy tab and uncheck the box in front of \"Allow sending of information to PhET.\"  Users will still be able to receive notices of updates, but in this case, no information is sent to the PhET Project. To disallow updates, uncheck 'Automatically check for updates' in the Updates tab.<br><br>Instructions for Flash simulations: Open the dialog using the Preferences button. Uncheck the box in front of \"Allow sending of information to PhET\" in the Privacy section.  Users will still be able to receive notices of updates, but in this case, no information is sent to the PhET Project. To disallow updates, uncheck 'Automatically check for updates' in the Updates section.<br><br><font size=\"12\"><b><u>III. DISCLAIMER</u></b></font><br><br>This software and the information contained therein is provided as a public service, with the understanding that neither the University of Colorado nor the PhET project makes any warranties, either express or implied, concerning the accuracy, completeness, reliability, or suitability of this software and information.<br><br>By using this software, you assume all risks associated with such use, including but not limited to the risk of any damage to your computer, software, or data.� In no event shall the University or the PhET project be liable for any direct, indirect, punitive, special, incidental, or consequential damages (including, without limitation, lost revenues or profits or lost or damaged data) arising from your use of this software.<br><br><font size=\"12\"><b><u>IV. CONTACT US</u></b></font><br><br>Please send comments, questions, or concerns regarding this software agreement to <a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>. Please do not send attachments with the message.<br></font>";
        String agreementContent = FlashHTML.rawFile( new File( unzipDir, SOFTWARE_AGREEMENT_FILENAME ) );

        String encodedAgreement = FlashHTML.encodeXML( agreementContent );

        String titleString = FlashHTML.extractTitleFromXML( simName, simXMLFile );
        if ( titleString == null ) {
            titleString = FlashHTML.extractTitleFromXML( simName, new File( unzipDir, simName + "-strings_en.xml" ) );
            if ( titleString == null ) {
                titleString = simName;
            }
        }

        // dynamically generate an HTML file
        String html = FlashHTML.generateHTML( simName, language, country, deployment, distributionTag, installationTimestamp,
                                              installerCreationTimestamp, versionMajor, versionMinor, versionDev, versionRevision, versionTimestamp, simDev, bgcolor,
                                              simEncodedXML, commonEncodedXML, "8", "flash-template.html", agreementVersion, encodedAgreement, creditsString,
                                              titleString );
        File htmlFile = new File( unzipDir, simName + "_" + locale + ".html" );
        FileOutputStream outputStream = new FileOutputStream( htmlFile );
        outputStream.write( html.getBytes() );
        outputStream.close();

        // open the browser, point it at the HTML file
        println( "Starting openurl" );
        BareBonesBrowserLaunch.openURL( "file://" + htmlFile.getAbsolutePath() );
    }

    private File getSimXMLFile( File baseDir, String localeString ) {
        // files where the simulation and common internationalization XML should be.
        // if they don't exist, replace with defaults

        File simXMLFile = new File( baseDir, projectName + "-strings_" + localeString + ".xml" );

        if ( !simXMLFile.exists() ) {
            simXMLFile = new File( baseDir, projectName + "-strings_en.xml" );
            println( "WARNING: could not find sim strings for " + localeString + ", using default en." );
        }
        return simXMLFile;
    }

    private static Properties readProperties( String filename ) {
        String propertiesFileName = filename + ".properties";
        InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( propertiesFileName );
        Properties properties = new Properties();
        try {
            properties.load( inStream );
        }
        catch( IOException e ) {
            e.printStackTrace(); //TODO handle this better
        }
        return properties;
    }

    private static String readBackgroundColor( Properties properties ) {
        String bgcolor = properties.getProperty( "bgcolor" );
        if ( bgcolor == null ) {
            bgcolor = "#ffffff"; // white
        }
        return bgcolor;
    }


    /*
     * Gets the JAR file that this class was launched from, copied from FileUtils in phetcommon.
     */

    private File getJARFile() {
        URL url = FlashLauncher.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            //TODO: consider using new File(URL.toURI) when we move to 1.5
            return new File( URLDecoder.decode( url.getFile(), "UTF-8" ) );//whitespace are %20 if you don't decode with utf-8, and file ops will fail.  See #1308
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
            try {
                return new File( URLDecoder.decode( url.getPath(), "UTF-8" ) );
            }
            catch( UnsupportedEncodingException e1 ) {
                e1.printStackTrace();
                return new File( url.getPath() );
            }
        }
    }

    public static void main( String[] args ) throws IOException {
        System.out.println( "FlashLauncher.main started" );
//        JOptionPane.showMessageDialog( null, System.getProperty( "java.class.path" ) );
        new FlashLauncher().start( args );
        System.out.println( "FlashLauncher.main finished" );
    }
}
