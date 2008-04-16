package edu.colorado.phet.build.translate;

import java.io.File;

import javax.swing.JOptionPane;

/**
 * Created by: Sam
 * Feb 28, 2008 at 11:55:38 PM
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
        JOptionPane.showMessageDialog( null,
                "<html>Put the localization files that you wish to deploy in a directory.<br>" +
                "When you have finished this step, press OK to continue.<br>" +
                "You will be prompted for the directory name.</html>" );
        String dirname = AddTranslation.prompt( "Enter the name of the directory where your localization files are:" );
        new ImportAndAddBatch( new File( args[0] ) ).importAndAddBatch( dirname );
    }

    private void importAndAddBatch( String dir ) throws Exception {
        System.out.println( "Importing..." );
        new ImportTranslations( baseDir ).importTranslations( new File( dir ) );
        System.out.println( "Finished Importing." );
        JOptionPane.showMessageDialog( null, 
                "<html>Localization files have been imported into your IDE workspace.<br>" +
        		"Please refresh your workspace, examine the files,<br>" +
        		"and manually commit them to the SVN repository.<br><br>" +
        		"Press OK when you are ready to integrate the files into<br>" +
        		"the PHET production server." );

        System.out.println( "Adding to tigercat" );
        String username = AddTranslation.prompt( "Production Server username:" );
        String password = AddTranslation.prompt( "Production Server password:" );
        new AddTranslationBatch( baseDir, new File( dir ), username, password ).runBatch( true );
        System.out.println( "Finished Adding" );
    }
}
