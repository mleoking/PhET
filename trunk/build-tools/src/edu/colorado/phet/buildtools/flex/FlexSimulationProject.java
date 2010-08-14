package edu.colorado.phet.buildtools.flex;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.Simulation;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;

public class FlexSimulationProject extends FlashSimulationProject {
    public FlexSimulationProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    protected boolean buildSWF( String simulationName ) throws Exception {
        File swfFile = getSWFFile( simulationName );
        boolean b = swfFile.delete();
        System.out.println( "Delete SWFFile=" + b );

        // TODO: factor out libraries and other dependencies into build properties
        Process p = Runtime.getRuntime().exec( new String[]{getMxmlcExecutable(),
                "-use-network",
                "-output", "deploy/" + simulationName + ".swf",
                "-compiler.source-path", "src",
                "../../contrib/away3d/fp9", "../../common/src", "../../../simulations-flash/contrib/box2d/src",
                "-compiler.accessible", "-compiler.optimize", "-target-player", "9", getMXML( simulationName )}, null, getProjectDir() );
        new StreamReaderThread( p.getErrorStream(), "err>" ).start();
        new StreamReaderThread( p.getInputStream(), "" ).start();
        p.waitFor();
        return swfFile.exists();
    }

    private String getMXML( String simulationName ) {
        return getBuildPropertiesFileObject().getMXML( simulationName );
    }

    public String getListDisplayName() {
        return "Flex: " + getProjectDir().getName();
    }

    public String getMxmlcExecutable() {
        if ( System.getProperty( "os.name" ).indexOf( "Windows" ) >= 0 ) {
            return BuildLocalProperties.getInstance().getFlexSDK() + "\\bin\\mxmlc.exe";
        }
        else {
            return BuildLocalProperties.getInstance().getFlexSDK() + "/bin/mxmlc";
        }
    }

    public static PhetProject[] getFlexSimulations( File trunk ) {
        File flashSimDir = new File( trunk, BuildToolsPaths.FLEX_SIMULATIONS_DIR );
        File[] files = flashSimDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory() && !pathname.getName().startsWith( "." );
            }
        } );
        Collection projects = new ArrayList();
        for ( int i = 0; ( files != null ) && ( i < files.length ); i++ ) {
            File file = files[i];
            try {
                projects.add( new FlexSimulationProject( file ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        projects = PhetProject.sort( new ArrayList( projects ) );
        return (FlexSimulationProject[]) projects.toArray( new FlexSimulationProject[projects.size()] );
    }
}
