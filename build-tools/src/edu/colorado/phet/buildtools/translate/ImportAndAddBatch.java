package edu.colorado.phet.buildtools.translate;

import java.io.File;

import javax.swing.*;

import edu.colorado.phet.buildtools.AuthenticationInfo;
import edu.colorado.phet.buildtools.LocalProperties;
import edu.colorado.phet.buildtools.translate.AddTranslation.AddTranslationReturnValue;

/**
 * This is the main entry point for importing translations into subversion and deploying them to the production server.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class ImportAndAddBatch {
    private File simulationsJava;
    private LocalProperties localProperties;

    public ImportAndAddBatch( File trunk ) {
        this.simulationsJava = new File( trunk, "simulations-java" );
        this.localProperties = new LocalProperties( new File( trunk, "build-tools/build-local.properties" ) );
    }

    public static void main( String[] args ) throws Exception {
        if ( args.length != 1 ) {
            System.out.println( "usage: ImportAndAddBatch path-to-simulations-java-in-your-workspace" );
            System.exit( 1 );
        }
        startImportAndAddBatch( new File( args[0] ) );
    }

    private String getLocalProperty( String s ) {
        return localProperties.getProperty( s );
    }

    private AuthenticationInfo getDevelopmentAuthentication( String serverType ) {
        return new AuthenticationInfo( getLocalProperty( "deploy." + serverType + ".username" ), getLocalProperty( "deploy." + serverType + ".password" ) );
    }

    public static void startImportAndAddBatch( File trunk ) throws Exception {
        JOptionPane.showMessageDialog( null,
                                       "<html>Put the localization files that you wish to deploy in a directory.<br>" +
                                       "When you have finished this step, press OK to continue.<br>" +
                                       "You will be prompted for the directory name.</html>" );
        String dirname = AddTranslation.prompt( "Enter the name of the directory where your localization files are:" );
        new ImportAndAddBatch( trunk ).importAndAddBatch( dirname );
    }

    private void importAndAddBatch( String dir ) throws Exception {

        // import the translations into the IDE workspace
        new ImportTranslations( simulationsJava ).importTranslations( new File( dir ) );
        JOptionPane.showMessageDialog( null,
                                       "<html>Localization files have been imported into your IDE workspace.<br>" +
                                       "Please refresh your workspace, examine the files,<br>" +
                                       "and manually commit them to the SVN repository.<br><br>" +
                                       "Press OK when you are ready to integrate the files into<br>" +
                                       "the PHET production server." );

        // deploy the translations to the PhET productions server
        AuthenticationInfo auth = getDevelopmentAuthentication( "prod" );
        AddTranslationBatch addTranslationBatch = new AddTranslationBatch( simulationsJava, new File( dir ), auth.getUsername( "production server" ), auth.getPassword( "production server" ) );
        AddTranslationReturnValue[] returnValues = addTranslationBatch.runBatch();
        System.out.println( "Finished deploying" );

        String results = "<html>Results:<br><br>";
        for ( int i = 0; i < returnValues.length; i++ ) {
            AddTranslationReturnValue returnValue = returnValues[i];
            results += returnValue.getSimulation() + " (" + returnValue.getLocale() + ") " + ( returnValue.isSuccess() ? "OK" : "*** FAILED ***" );
            results += "<br>";
        }
        results += "</html>";
        JOptionPane.showMessageDialog( null, results );
    }
}
