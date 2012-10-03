package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;

public class HTML5Project extends PhetProject {
    public HTML5Project( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public HTML5Project( File parentDir, String name ) throws IOException {
        super( parentDir, name );
    }

    @Override public Simulation getSimulation( final String simulationName, final Locale locale ) {
        return new Simulation( simulationName, simulationName, "<javascript>", new String[0], false );
    }

    @Override public Locale[] getLocales() {
        return new Locale[0];
    }

    @Override public File getLocalizationFile( final Locale locale ) {
        return null;
    }

    @Override public File getTranslationFile( final Locale locale ) {
        return null;
    }

    @Override protected File getTrunkAbsolute() {
        return getProjectDir().getParentFile().getParentFile().getParentFile();
    }

    @Override public String getAlternateMainClass() {
        return null;
    }

    @Override public String getProdServerDeployPath() {
        return null;
    }

    @Override public String getLaunchFileSuffix() {
        return null;
    }

    @Override public boolean build() throws Exception {
        return true;
    }

    @Override public String getListDisplayName() {
        return null;
    }

    @Override public void runSim( final Locale locale, final String simulationName ) {
    }

    @Override public boolean isTestable() {
        return false;
    }

    public static HTML5Project[] getProjects( final File trunk ) {
        File[] html5SimDir = new File( trunk, "simulations-html5/simulations" ).listFiles( new FileFilter() {
            public boolean accept( final File pathname ) {
                return pathname.isDirectory() && !pathname.getName().toLowerCase().equals( ".svn" );
            }
        } );
        HTML5Project[] x = new HTML5Project[html5SimDir.length];
        for ( int i = 0; i < html5SimDir.length; i++ ) {
            File file = html5SimDir[i];
            try {
                x[i] = new HTML5Project( file );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return x;
    }
}