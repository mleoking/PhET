package edu.colorado.phet.buildtools.java.projects;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.java.JavaProject;

public class TimesheetProject extends JavaProject {
    public TimesheetProject( File file ) throws IOException {
        super( file );
    }

    public File getTrunkAbsolute() {
        return getProjectDir().getParentFile().getParentFile(); // ../../trunk/
    }

    public String getAlternateMainClass() {
        return "edu.colorado.phet.reids.admin.TimesheetApp";
    }

    public File getDefaultDeployJar() {
        return new File( getDeployDir(), "timesheet.jar" );
    }

    public String getProdServerDeployPath() {
        return BuildToolsPaths.TIMESHEET_PROD_SERVER_DEPLOY_PATH;
    }
}