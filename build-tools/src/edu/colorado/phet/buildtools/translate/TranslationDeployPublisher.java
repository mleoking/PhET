package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Mar 28, 2009
 * Time: 11:10:11 AM
 */
public class TranslationDeployPublisher {
    private File pathToSimsDir;

    public TranslationDeployPublisher( File pathToSimsDir ) {
        this.pathToSimsDir = pathToSimsDir;
    }

    public static void main( String[] args ) throws IOException, InterruptedException {
        new TranslationDeployPublisher( new File( args[0] ) ).publishTranslations( new File( args[1] ) );
    }

    private void publishTranslations( File translationDir ) {
        //copy new translated JARs and project_all.jar to the sims directory
        //generate new JNLPs in sims directory
        //regenerate server HTML caches
    }
}
