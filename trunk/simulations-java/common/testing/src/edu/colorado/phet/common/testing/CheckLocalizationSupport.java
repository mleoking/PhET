package edu.colorado.phet.common.testing;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.PhetProjectFlavor;

/**
 * Created by: Sam
 * May 13, 2008 at 8:36:32 PM
 */
public class CheckLocalizationSupport {
    public CheckLocalizationSupport() {
    }

    public static void main( String[] args ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, InterruptedException {
        new CheckLocalizationSupport().start();
    }

    private void start() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, InterruptedException {
        PhetProject[] x = PhetProject.getAllProjects( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java" ) );
        for ( int i = 0; i < x.length; i++ ) {
            checkLocalization( x[i] );
        }
    }

    private void checkLocalization( PhetProject phetProject ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, InterruptedException {
        PhetProjectFlavor[] f = phetProject.getFlavors();
        for ( int i = 0; i < f.length; i++ ) {
            PhetProjectFlavor phetProjectFlavor = f[i];
            String cp = System.getProperty( "java.class.path" );
            String command = "java -Dsun.java2d.noddraw=true -classpath \"" + cp + "\" " + TestSim.class.getName() + " " + phetProject.getName() + " " + phetProjectFlavor.getFlavorName();
            System.out.println( "command = " + command );
            Process p = Runtime.getRuntime().exec( command );
            int val = p.waitFor();
            System.out.println( "val = " + val );
        }
    }
}
