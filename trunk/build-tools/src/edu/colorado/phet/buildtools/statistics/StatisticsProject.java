package edu.colorado.phet.buildtools.statistics;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Mar 15, 2009
 * Time: 2:33:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatisticsProject extends PhetProject {

    public StatisticsProject( File trunk ) throws IOException {
        super( new File( trunk, BuildToolsPaths.STATISTICS ) );
    }

    public Simulation getSimulation( String simulationName, Locale locale ) {
        return null;
    }

    public Locale[] getLocales() {
        return new Locale[0];
    }

    public File getTranslationFile( Locale locale ) {
        return null;
    }

    protected File getTrunkAbsolute() {
        return null;
    }

    public String getAlternateMainClass() {
        return null;
    }

    public String getProdServerDeployPath() {
        return null;
    }

    public String getLaunchFileSuffix() {
        return null;
    }

    public boolean build() throws Exception {
        return false;
    }

    public String getListDisplayName() {
        return null;
    }

    public void runSim( Locale locale, String simulationName ) {

    }

    public PhetProject[] getAllDependencies() {
        return new PhetProject[]{this};
    }
}
