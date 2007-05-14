package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.IOException;

//todo: add options such as shrink, etc.
public class PhetBuildAllJNLPTask extends PhetAllSimTask {
    private String deployURL;
    public final void execute() throws BuildException {
        buildAllJNLP( getSimNames() );
    }

    public void buildAllJNLP( String[] simNames ) {
        for( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
            System.out.println( "simName = " + simName );
            System.out.println( "project.getBaseDir() = " + getProject().getBaseDir() );
//            File project = PhetBuildUtils.resolveProject( new File( getProject().getBaseDir(),"simulations" ), simName );
            File projectParentDir = PhetBuildUtils.resolveProject( getProject().getBaseDir(), simName );
            System.out.println( "resolved project = " + projectParentDir );
            try {
                PhetProject phetProject = new PhetProject( projectParentDir,simName );
                String[] flavors = phetProject.getFlavorNames();
                if (flavors.length==0){
                    buildForFlavor( simName,null);
                }
                for( int k = 0; k < flavors.length; k++ ) {
                    String flavor = flavors[k];
                    buildForFlavor( simName, flavor );
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private void buildForFlavor( String simName, String flavor ) {
        System.out.println( "Building JNLP for Flavor="+flavor );
        PhetBuildJnlpTask buildJnlpTask = new PhetBuildJnlpTask();
        buildJnlpTask.setProject( simName );
        buildJnlpTask.setFlavor( flavor );
        buildJnlpTask.setDeployUrl( deployURL );
        runTask( buildJnlpTask );
    }

    public String getDeployURL() {
        return deployURL;
    }

    public void setDeployURL( String deployURL ) {
        this.deployURL = deployURL;
    }
}