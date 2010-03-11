package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.buildtools.MyAntTaskRunner;
import edu.colorado.phet.buildtools.java.JavaProject;
import edu.colorado.phet.buildtools.java.WebsiteBuildCommand;

/**
 * Project meant to compile the Wicket website
 */
public class WebsiteProject extends JavaProject {
    public WebsiteProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        return new File( getProjectDir(), "../.." );
    }

    public String getAlternateMainClass() {
        return null;
    }

    public String getProdServerDeployPath() {
        return null;
        //return BuildToolsPaths.BUILD_TOOLS_PROD_SERVER_DEPLOY_PATH;
    }

    public boolean isTestable() {
        return true;
    }

    @Override
    public boolean isShrink() {
        // until many things are done
        return false;
    }

    @Override
    public boolean getSignJar() {
        return false;
    }

    @Override
    public boolean build() throws Exception {
        new WebsiteBuildCommand( this, new MyAntTaskRunner(), isShrink(), this.getDefaultDeployJar() ).execute();
        File[] f = getDeployDir().listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.getName().toLowerCase().endsWith( ".jar" );
            }
        } );
        return f.length == 1;//success if there is exactly one jar
    }

    @Override
    public void runSim( Locale locale, String simulationName ) {
        
    }

    @Override
    public File getJarFile() {
        File file = new File( getAntOutputDir(), "jars/ROOT.war" );
        file.getParentFile().mkdirs();
        return file;
    }
}