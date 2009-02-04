package edu.colorado.phet.build;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import edu.colorado.phet.build.flash.FlashBuildCommand;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jan 29, 2009
 * Time: 11:22:23 AM
 * <p/>
 * //phetProjects.addAll(Arrays.asList( PhetFlashProject.getFlashProjects(baseDir ) ));
 */
public class PhetFlashProject extends PhetProject {
    public PhetFlashProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public PhetFlashProject( File parentDir, String name ) throws IOException {
        super( parentDir, name );
    }

    public static PhetProject[] getFlashProjects( File baseDir ) {
//        File flashSimDir=new File(baseDir.getParentFile(),"team/jolson/simulations");
        File flashSimDir = new File( baseDir.getParentFile(), "simulations-flash/simulations" );
        File[] files = flashSimDir.listFiles( new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory() && !pathname.getName().startsWith( "." );
            }
        } );
        ArrayList projects = new ArrayList();
        for ( int i = 0; ( files != null ) && ( i < files.length ); i++ ) {
            File file = files[i];
            try {
                projects.add( new PhetFlashProject( file ) );
            }
            catch( IOException e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return (PhetFlashProject[]) projects.toArray( new PhetFlashProject[0] );
    }

    public boolean build() throws Exception {
//        super.build();
        System.out.println( "Building flash sim." );
//        JOptionPane.showMessageDialog( null, "Build the Flash Sim SWF file now (Shift-F12 builds without launching), then press OK when you are finished" );
        File configFile = new File( getProjectDir().getParentFile().getParentFile(), "config.properties" );
        Properties properties = new Properties();
        properties.load( new FileInputStream( configFile ) );
        String exe = properties.getProperty( "flash.exe", "C:\\Program Files\\Macromedia\\Flash 8\\Flash.exe" );
        FlashBuildCommand.build( exe,
                                 getName(),
                                 getProjectDir().getParentFile()//simulations
                                         .getParentFile()//simulations-flash
                                         .getParentFile()//trunk
        );
        return true;
    }

    public String getListDisplayName() {
        return "Flash: " + getName();
    }

    public void runSim( Locale locale, String simulationName ) {
        System.out.println( "Running the flash sim: " + simulationName );
    }

    public Simulation getSimulation( String simulationName, String locale ) {
        return new Simulation( simulationName, "title", "description", "mainclass", new String[0], new File( "screenshot.gif" ) );
    }

    public void buildLaunchFiles( String URL, boolean dev ) {
//        super.buildLaunchFiles( URL, dev );
        System.out.println( "What to do for building Flash launch files?  Are these HTML?" );
    }
}
