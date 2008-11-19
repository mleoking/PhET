package edu.colorado.phet.build.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Echo;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.PhetProjectFlavor;

/**
 * Author: Sam Reid
 * Aug 24, 2007, 6:47:30 PM
 */
public class PhetBuildAllSimJarTask extends PhetAllSimTask {
    private boolean shrink = true;

    public boolean getShrink() {
        return shrink;
    }

    public void setShrink( boolean shrink ) {
        this.shrink = shrink;
    }

    public void execute() throws BuildException {
        String[] sims = PhetProject.getSimNames( getBaseDir() );
        String SIMS = "";
        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];
            SIMS += sim;
            if ( i < sims.length - 1 ) {
                SIMS += " : ";
            }
        }

        String template = "project.depends.data=\n" +
                          "project.depends.source=\n" +
                          "project.depends.lib=" + SIMS + "\n" +
                          "project.name=\n" +
                          "project.description=\n" +
                          "project.screenshot=\n";

        PhetProject[] phetProjects = PhetProject.getAllProjects( getBaseDir() );

        for ( int i = 0; i < phetProjects.length; i++ ) {
            PhetProject phetProject = phetProjects[i];

            String sim = phetProject.getName();

            template += "\n# Simulation entry for " + sim + "\n";

            PhetProjectFlavor[] f = phetProject.getFlavors();

            for ( int j = 0; j < f.length; j++ ) {
                PhetProjectFlavor phetProjectFlavor = f[j];
                String flavorname = sim + "_" + phetProjectFlavor.getFlavorName();
                template += "project.flavor." + flavorname + ".mainclass=" + phetProjectFlavor.getMainclass() + "\n" +
                            "project.flavor." + flavorname + ".args=" + toArgsList( phetProjectFlavor.getArgs() ) + "\n";
            }
        }
        System.out.println( "template=\n" + template );
        Echo echo = new Echo();
        echo.setFile( new File( getProject().getBaseDir(), "simulations/all-sims/all-sims-build.properties" ) );
        echo.setMessage( template );
        runTask( echo );

        PhetBuildTask buildTask = new PhetBuildTask();
        buildTask.setProject( "all-sims" );
        buildTask.setShrink( shrink );
        runTask( buildTask );
    }

    private String toArgsList( String[] args ) {
        String str = "";
        for ( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            str += arg + " ";
        }
        return str;
    }
}
