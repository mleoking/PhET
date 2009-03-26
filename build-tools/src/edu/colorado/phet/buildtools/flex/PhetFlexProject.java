package edu.colorado.phet.buildtools.flex;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;

public class PhetFlexProject extends PhetProject {
    public PhetFlexProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public Simulation getSimulation( String simulationName, Locale locale ) {
        return new Simulation( getName(), getName(), getName(), "?", new String[0], null );
    }

    public Locale[] getLocales() {
        return new Locale[0];
    }

    public File getTranslationFile( Locale locale ) {
        return null;
    }

    public File getTrunkAbsolute() {
        return new File( getProjectDir(), "../../.." );
    }

    public String getAlternateMainClass() {
        return null;//use JARLauncher
    }

    public String getProdServerDeployPath() {
        return null;//deploy to sims
    }

    public String getLaunchFileSuffix() {
        return "swf";
    }

    public boolean build() throws Exception {
        File swfFile = getSWFFile();
        boolean b = swfFile.delete();
        System.out.println( "Delete SWFFile=" + b );
//        Process p = Runtime.getRuntime().exec( "mxmlc -output deploy/" + getName() + ".swf -compiler.source-path=src " + getMXML(), null, getProjectDir() );
        Process p = Runtime.getRuntime().exec( new String[]{"mxmlc", "-output", "deploy/" + getName() + ".swf", "-compiler.source-path=src:../../common/src", "-compiler.accessible", "-compiler.optimize", "-target-player", "9", getMXML()}, null, getProjectDir() );
        new StreamReaderThread( p.getErrorStream(), "err>" ).start();
        new StreamReaderThread( p.getInputStream(), "" ).start();
        p.waitFor();
        return swfFile.exists();
    }

    private File getSWFFile() {
        return new File( getProjectDir(), "deploy/" + getName() + ".swf" );
    }

    private String getMXML() {
        //todo: generalize to have multi-flavors
        return getBuildPropertiesFileObject().getMXML( getBuildPropertiesFileObject().getSimulationNames()[0] );
    }

    public String getListDisplayName() {
        return "Flex: " + getProjectDir().getName();
    }

    public void runSim( Locale locale, String simulationName ) {
        try {
            Process p = Runtime.getRuntime().exec( new String[]{BuildLocalProperties.getInstance().getBrowser(), getSWFFile().getAbsolutePath()} );
            new StreamReaderThread( p.getErrorStream(), "err>" ).start();
            new StreamReaderThread( p.getInputStream(), "" ).start();
            p.waitFor();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        //point a browser at the SWF for now
    }

    public static PhetProject[] getFlexProjects( File trunk ) {
//        File flashSimDir=new File(baseDir.getParentFile(),"team/jolson/simulations");
        File flashSimDir = new File( trunk, "simulations-flex/simulations" );
        File[] files = flashSimDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory() && !pathname.getName().startsWith( "." );
            }
        } );
        Collection projects = new ArrayList();
        for ( int i = 0; ( files != null ) && ( i < files.length ); i++ ) {
            File file = files[i];
            try {
                projects.add( new PhetFlexProject( file ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        projects = PhetProject.sort( new ArrayList( projects ) );
        return (PhetFlexProject[]) projects.toArray( new PhetFlexProject[projects.size()] );
    }
}
