package edu.colorado.phet.common.buildreports;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.Simulation;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;

/**
 * Created by: Sam
 * May 13, 2008 at 8:36:32 PM
 */
public class BatchSimTest {
    public BatchSimTest() {
    }

    public static void main( String[] args ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, InterruptedException {
//        new BatchSimTest().testAllSims( CaptureScreenshot.class );
        new BatchSimTest().testAllSims( TestPhetApplicationUsage.class );
    }

    int count = 0;

    private void testAllSims( Class mainClass ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, InterruptedException {
        PhetProject[] x = PhetProject.getAllProjects( new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2\\simulations-java" ) );
        for ( int i = 0; i < x.length; i++ ) {
            checkSim( x[i], mainClass );

        }
    }

    private void checkSim( PhetProject phetProject, Class mainClass ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, InterruptedException {
        Simulation[] f = phetProject.getSimulations();
        for ( int i = 0; i < f.length; i++ ) {

            Simulation phetProjectFlavor = f[i];
            String cp = System.getProperty( "java.class.path" );
            String command = "C:\\j2sdk1.4.2_15\\bin\\java -Dsun.java2d.noddraw=true -classpath \"" + cp + "\" " + mainClass.getName() + " " + phetProject.getName() + " " + phetProjectFlavor.getName();
            System.out.println( "command = " + command );

//            if ( count >= 55 ) {
            if ( count >= 0 ) {
                System.out.println( "count=" + count );
                Process p = Runtime.getRuntime().exec( command );
                new StreamReaderThread( p.getErrorStream(), "err_" + phetProjectFlavor.getName() ).start();
                new StreamReaderThread( p.getInputStream(), "out_" + phetProjectFlavor.getName() ).start();
                int val = p.waitFor();
                System.out.println( "Finished exec." );
            }
            count++;
//            System.out.println( "val = " + val );
        }
    }

}
