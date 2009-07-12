package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.java.JavaProject;

/**
 * build the Translation Utility (translation-utility.jar)
 */
public class TranslationUtilityProject extends JavaProject {
    public TranslationUtilityProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        return getProjectDir().getParentFile().getParentFile(); // ../../trunk/
    }

    public String getAlternateMainClass() {
        return "edu.colorado.phet.translationutility.TranslationUtility";
    }

    public File getDefaultDeployJar() {
        return new File( getDeployDir(), "translation-utility.jar" );
    }

    public String getProdServerDeployPath() {
        return BuildToolsPaths.TRANSLATION_UTILITY_PROD_SERVER_DEPLOY_PATH;
    }

    public boolean isTestable() {
        return true;
    }
}
