package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by: Sam
 * Feb 28, 2008 at 9:37:32 PM
 */
public class AddTranslationBatch {
    private File basedir;
    private File simDir;
    private String user;
    private String password;

    public AddTranslationBatch( File basedir, File simDir, String user, String password ) {
        this.basedir = basedir;
        this.simDir = simDir;
        this.user = user;
        this.password = password;
    }

    public static void main( String[] args ) throws Exception {
        new AddTranslationBatch( new File( args[0] ), new File( args[1] ), args[2], args[3] ).start();
    }

    //assume we have a directory full of translation files
    private void start() throws Exception {
        System.out.println( "basedir.getAbsolutePath() = " + basedir.getAbsolutePath() );
        System.out.println( "simDir.getAbsolutePath() = " + simDir.getAbsolutePath() );
        File[] translationFiles = simDir.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.endsWith( ".properties" ) && name.indexOf( "_" ) >= 0;
            }
        } );
        for ( int i = 0; i < translationFiles.length; i++ ) {
            File translationFile = translationFiles[i];
            String name = translationFile.getName();
            String simWithSuffix = name.substring( 0, name.indexOf( "_" ) );
            String sim = simWithSuffix.substring( 0, simWithSuffix.lastIndexOf( "-" ) );
            String lang = name.substring( name.indexOf( "_" ) + 1, name.indexOf( "." ) );
            AddTranslation addTranslation = new AddTranslation( basedir, false );
            System.out.println( "addtranslation, sim=" + sim + ", lang=" + lang + ", user=" + user );
//            addTranslation.addTranslation( sim, lang, user, password );
        }
    }
}
