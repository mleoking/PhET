package edu.colorado.phet.build.translate;

import java.io.File;

import javax.swing.*;

import edu.colorado.phet.build.translate.AddTranslation.AddTranslationReturnValue;

/**
 * This is the main entry point for importing translations into subversion and deploying them to tigercat.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class ImportAndAddBatch {
    private File baseDir;

    public ImportAndAddBatch( File baseDir ) {
        this.baseDir = baseDir;
    }

    public static void main( String[] args ) throws Exception {
        if ( args.length != 1 ) {
            System.out.println( "usage: ImportAndAddBatch path-to-simulations-java-in-your-workspace" );
            System.exit( 1 );
        }
        startImportAndAddBatch( args[0] );
    }

    public static void startImportAndAddBatch( String simulationsJava ) throws Exception {
        JOptionPane.showMessageDialog( null,
                                       "<html>Put the localization files that you wish to deploy in a directory.<br>" +
                                       "When you have finished this step, press OK to continue.<br>" +
                                       "You will be prompted for the directory name.</html>" );
        String dirname = AddTranslation.prompt( "Enter the name of the directory where your localization files are:" );
        new ImportAndAddBatch( new File( simulationsJava ) ).importAndAddBatch( dirname );
    }

    private void importAndAddBatch( String dir ) throws Exception {

        // import the translations into the IDE workspace
        new ImportTranslations( baseDir ).importTranslations( new File( dir ) );
        JOptionPane.showMessageDialog( null,
                                       "<html>Localization files have been imported into your IDE workspace.<br>" +
                                       "Please refresh your workspace, examine the files,<br>" +
                                       "and manually commit them to the SVN repository.<br><br>" +
                                       "Press OK when you are ready to integrate the files into<br>" +
                                       "the PHET production server." );

        // deploy the translations to the PhET productions server
        String username = AddTranslation.prompt( "Production Server username:" );
        String password = AddTranslation.prompt( "Production Server password:" );
        AddTranslationBatch addTranslationBatch = new AddTranslationBatch( baseDir, new File( dir ), username, password );
        AddTranslationReturnValue[] returnValues = addTranslationBatch.runBatch();
        System.out.println( "Finished deploying" );

        String results = "<html>Results:<br><br>";
        for ( int i = 0; i < returnValues.length; i++ ) {
            AddTranslationReturnValue returnValue = returnValues[i];
            results += returnValue.getSimulation() + " (" + returnValue.getLanguage() + ") " + ( returnValue.isSuccess() ? "OK" : "*** FAILED ***" );
            results += "<br>";
        }
        results += "</html>";
        JOptionPane.showMessageDialog( null, results );
    }
}
