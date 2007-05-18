package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

public class PhetDisplayStatsTask extends PhetAllSimTask {
    public final void execute() throws BuildException {
        showStats( getSimNames() );
    }

    public void showStats( String[] simNames ) {
        File baseDir = getProject().getBaseDir();
        showStats( simNames, baseDir );
    }

    private void showStats( String[] simNames, File baseDir ) {
        int flavorCount = 0;
        HashMap languages = new HashMap();
        for( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
            try {
                File projectParentDir = PhetBuildUtils.resolveProject( baseDir, simName );
                PhetProject phetProject = new PhetProject( projectParentDir, simName );
                System.out.println( phetProject.getName() + ": " + Arrays.asList( phetProject.getFlavorNames() ) + " locales: " + Arrays.asList( phetProject.getLocales() ) );
                flavorCount += phetProject.getFlavorNames().length;
                for( int j = 0; j < phetProject.getLocales().length; j++ ) {
                    String locale = phetProject.getLocales()[j];
                    languages.put( locale, "" );
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        ArrayList locales=new ArrayList( languages.keySet( ));
        System.out.println( "Number of Sims: " + simNames.length + ", number of declared flavors: " + flavorCount + ", number of locales used at least once: " + languages.size() +", all languages="+locales);
    }

    public static void main( String[] args ) {
        File simsroot = new File( "C:\\phet\\subversion\\trunk\\simulations-java" );
        String[] sims = PhetAllSimTask.getSimNames( new File( simsroot, "simulations" ) );
        PhetDisplayStatsTask phetDisplayStatsTask = new PhetDisplayStatsTask();
        phetDisplayStatsTask.showStats( sims, simsroot );
    }
}
