package edu.colorado.phet.buildtools.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.projects.JavaProject;

public class TranslationUtilityProject extends JavaProject {
    public TranslationUtilityProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        // directory structure is trunk/util/translation-utility
        return getProjectDir().getParentFile().getParentFile();
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
        System.out.println( "No launch files (JNLP) for " + getClass().getName() );
    }

    public String getAlternateMainClass() {
        return "edu.colorado.phet.translationutility.TranslationUtility";
    }

    public File getDefaultDeployJar() {
        return new File( getDeployDir(), "translation-utility.jar" );
    }

    public String getProdServerDeployPath() {
        return "/web/htdocs/phet/phet-dist/translation-utility";
    }
}
